/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.dao.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.linagora.linshare.core.dao.AtomicBlobReplace;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.storage.encryption.crypto.ChunkedDecryptor;
import org.linagora.linshare.storage.encryption.crypto.DecryptingInputStream;
import org.linagora.linshare.storage.encryption.crypto.UnwrappedBlobContext;
import org.linagora.linshare.storage.encryption.exception.EncryptedBlobAuthenticationException;
import org.linagora.linshare.storage.encryption.exception.EncryptedBlobFormatException;
import org.linagora.linshare.storage.encryption.exception.EncryptedBlobKeyException;
import org.linagora.linshare.storage.encryption.format.ChunkLayout;
import org.linagora.linshare.storage.encryption.format.EncryptedBlobFormat;
import org.linagora.linshare.storage.encryption.format.EncryptedBlobHeader;
import org.linagora.linshare.storage.encryption.key.KeyEncryptionService;
import org.linagora.linshare.storage.encryption.key.WrappedKey;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

/**
 * KEK rotation (ARCH.md 5.4, 27, 28): unwrap the DEK with the old KEK, wrap
 * the SAME DEK with the new KEK, rewrite only the header — ciphertext
 * chunks are never touched, decrypted, or re-encrypted, exactly because
 * Phase 1's fixed reserved wrapped-key header region was designed for this.
 *
 * <p>Commits via a temp-key-then-{@link AtomicBlobReplace} sequence rather
 * than a true in-place byte patch of the header region: a single {@code
 * write()} at a fixed file offset is not guaranteed atomic against a crash
 * by POSIX, and this project does not want rotation's correctness to rest
 * on an unstated filesystem assumption. The cost is one full byte-for-byte
 * I/O pass over the (unencrypted-again) chunk data — no cryptographic work
 * on it at all — which is negligible next to how infrequently KEK rotation
 * runs.
 */
public class KekRotator {

	private static final String TEMP_KEY_SUFFIX = ".rewrapping";

	private final FileDataStore delegate;

	private final KeyEncryptionService oldKeyEncryptionService;

	private final KeyEncryptionService newKeyEncryptionService;

	public KekRotator(FileDataStore delegate, KeyEncryptionService oldKeyEncryptionService,
			KeyEncryptionService newKeyEncryptionService) {
		if (delegate == null || oldKeyEncryptionService == null || newKeyEncryptionService == null) {
			throw new IllegalArgumentException(
					"delegate, oldKeyEncryptionService and newKeyEncryptionService must not be null");
		}
		this.delegate = delegate;
		this.oldKeyEncryptionService = oldKeyEncryptionService;
		this.newKeyEncryptionService = newKeyEncryptionService;
	}

	/**
	 * @param expectedNewKeyId the key id {@code newKeyEncryptionService.wrap}
	 *                         is expected to produce — supplied by the
	 *                         caller (who configured that service), used
	 *                         both as a cheap idempotency check and as a
	 *                         sanity check against a misconfigured service.
	 */
	public RotationOutcome rotate(FileMetaData metadata, String expectedNewKeyId) throws IOException {
		if (!delegate.exists(metadata)) {
			return RotationOutcome.MISSING;
		}
		EncryptedBlobHeader header = readHeader(metadata);
		FileMetaData tempMetadata = tempMetadataFor(metadata);

		if (header.getKeyId().equals(expectedNewKeyId)) {
			if (delegate.exists(tempMetadata)) {
				delegate.remove(tempMetadata);
			}
			return RotationOutcome.ALREADY_ROTATED;
		}

		byte[] dek = oldKeyEncryptionService.unwrap(header.getKeyId(), header.getWrappedKeyBytes());
		try {
			WrappedKey rewrapped = newKeyEncryptionService.wrap(dek);
			if (!rewrapped.getKeyId().equals(expectedNewKeyId)) {
				throw new EncryptedBlobKeyException("newKeyEncryptionService produced key id \""
						+ rewrapped.getKeyId() + "\" but \"" + expectedNewKeyId + "\" was expected");
			}
			byte[] newKeyIdBytes = rewrapped.getKeyId().getBytes(StandardCharsets.UTF_8);
			if (newKeyIdBytes.length > header.getReservedKeyIdCapacity()
					|| rewrapped.getWrappedKeyBytes().length > header.getReservedWrappedKeyCapacity()) {
				return RotationOutcome.WRAPPED_KEY_TOO_LARGE;
			}

			EncryptedBlobHeader newHeader = new EncryptedBlobHeader(header.getFormatVersion(),
					header.getAlgorithmId(), header.getNonceSchemeId(), header.getPlaintextSize(),
					header.getChunkPlaintextSize(), header.getChunkCount(), header.getNoncePrefix(),
					rewrapped.getKeyId(), header.getReservedKeyIdCapacity(), rewrapped.getWrappedKeyBytes(),
					header.getReservedWrappedKeyCapacity());

			writeRewrapped(metadata, tempMetadata, header, newHeader);

			if (!verify(tempMetadata, metadata)) {
				delegate.remove(tempMetadata);
				return RotationOutcome.VERIFICATION_FAILED;
			}

			commit(tempMetadata, metadata);
			return RotationOutcome.ROTATED;
		} finally {
			Arrays.fill(dek, (byte) 0);
		}
	}

	private EncryptedBlobHeader readHeader(FileMetaData metadata) throws IOException {
		try (InputStream in = delegate.getRange(metadata, 0, EncryptedBlobHeader.MAX_HEADER_LENGTH).openStream()) {
			return EncryptedBlobFormat.readHeader(in);
		}
	}

	private FileMetaData tempMetadataFor(FileMetaData metadata) {
		FileMetaData temp = new FileMetaData(metadata.getKind(), metadata.getMimeType(), metadata.getSize(),
				metadata.getFileName());
		temp.setUuid(metadata.getUuid() + TEMP_KEY_SUFFIX);
		temp.setBucketUuid(metadata.getBucketUuid());
		return temp;
	}

	private void writeRewrapped(FileMetaData metadata, FileMetaData tempMetadata, EncryptedBlobHeader oldHeader,
			EncryptedBlobHeader newHeader) throws IOException {
		long oldHeaderLength = ChunkLayout.of(oldHeader).headerTotalLength();
		long totalPhysicalLength = ChunkLayout.of(newHeader).totalPhysicalLength();
		FileMetaData physicalTempMetadata = new FileMetaData(tempMetadata.getKind(), tempMetadata.getMimeType(),
				totalPhysicalLength, tempMetadata.getFileName());
		physicalTempMetadata.setUuid(tempMetadata.getUuid());
		physicalTempMetadata.setBucketUuid(tempMetadata.getBucketUuid());

		ByteArrayOutputStream headerBytes = new ByteArrayOutputStream();
		EncryptedBlobFormat.writeHeader(newHeader, headerBytes);
		byte[] newHeaderBytes = headerBytes.toByteArray();

		ByteSource rewrappedSource = new ByteSource() {
			@Override
			public InputStream openStream() throws IOException {
				InputStream oldFullStream = delegate.get(metadata).openStream();
				ByteStreams.skipFully(oldFullStream, oldHeaderLength);
				return new SequenceInputStream(new ByteArrayInputStream(newHeaderBytes), oldFullStream);
			}
		};
		delegate.add(rewrappedSource, physicalTempMetadata);
	}

	/**
	 * Decrypts every chunk of the rewrapped temp blob, discarding the
	 * plaintext (never persisting or buffering the whole thing). This is a
	 * full pass, not a sample: the "rewrap" write below is a byte-for-byte
	 * copy of the entire original ciphertext under a new header, and a copy
	 * can be corrupted anywhere in it — checking only the first chunk would
	 * miss a truncation or torn write further into the file.
	 */
	private boolean verify(FileMetaData tempMetadata, FileMetaData realMetadata) throws IOException {
		byte[] blobId = realMetadata.getUuid().getBytes(StandardCharsets.UTF_8);
		ChunkedDecryptor decryptor = new ChunkedDecryptor(newKeyEncryptionService);
		try (InputStream raw = delegate.get(tempMetadata).openStream()) {
			UnwrappedBlobContext ctx = decryptor.open(raw, blobId);
			try (DecryptingInputStream decrypting = new DecryptingInputStream(decryptor, ctx, raw)) {
				ByteStreams.copy(decrypting, ByteStreams.nullOutputStream());
			}
			return true;
		} catch (EncryptedBlobAuthenticationException | EncryptedBlobFormatException | EncryptedBlobKeyException e) {
			return false;
		}
	}

	private void commit(FileMetaData tempMetadata, FileMetaData realMetadata) throws IOException {
		if (delegate instanceof AtomicBlobReplace) {
			((AtomicBlobReplace) delegate).atomicReplace(realMetadata.getBucketUuid(), tempMetadata.getUuid(),
					realMetadata.getUuid());
			return;
		}
		long physicalSize = physicalSizeOf(tempMetadata);
		FileMetaData realPhysicalMetadata = new FileMetaData(realMetadata.getKind(), realMetadata.getMimeType(),
				physicalSize, realMetadata.getFileName());
		realPhysicalMetadata.setUuid(realMetadata.getUuid());
		realPhysicalMetadata.setBucketUuid(realMetadata.getBucketUuid());
		delegate.add(delegate.get(tempMetadata), realPhysicalMetadata);
		delegate.remove(tempMetadata);
	}

	private long physicalSizeOf(FileMetaData metadata) throws IOException {
		try (InputStream headerIn = delegate.getRange(metadata, 0, EncryptedBlobHeader.MAX_HEADER_LENGTH)
				.openStream()) {
			return ChunkLayout.of(EncryptedBlobFormat.readHeader(headerIn)).totalPhysicalLength();
		}
	}
}
