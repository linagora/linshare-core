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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Locale;

import org.linagora.linshare.core.dao.AtomicBlobReplace;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.storage.encryption.crypto.ChunkedDecryptor;
import org.linagora.linshare.storage.encryption.crypto.ChunkedEncryptor;
import org.linagora.linshare.storage.encryption.crypto.DecryptingInputStream;
import org.linagora.linshare.storage.encryption.crypto.EncryptingBlobContext;
import org.linagora.linshare.storage.encryption.crypto.EncryptingInputStream;
import org.linagora.linshare.storage.encryption.crypto.EncryptionParameters;
import org.linagora.linshare.storage.encryption.crypto.UnwrappedBlobContext;
import org.linagora.linshare.storage.encryption.exception.EncryptedBlobAuthenticationException;
import org.linagora.linshare.storage.encryption.exception.EncryptedBlobFormatException;
import org.linagora.linshare.storage.encryption.format.ChunkLayout;
import org.linagora.linshare.storage.encryption.format.EncryptedBlobFormat;
import org.linagora.linshare.storage.encryption.format.EncryptedBlobHeader;
import org.linagora.linshare.storage.encryption.key.KeyEncryptionService;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

/**
 * Migrates one legacy plaintext blob to LSE1 following docs/ARCH.md 15 /
 * docs/CLAUDE.md 26's safe sequence: encrypt to a temporary key, verify
 * integrity against the caller-supplied expected SHA-256, then commit —
 * never overwrite the original in place, so a crash at any point leaves
 * either the untouched original or a fully-verified replacement, never a
 * partial one.
 *
 * <p>Restartable and idempotent: {@link #migrate} re-derives everything it
 * needs from what is already on disk (a leftover temp key from an
 * interrupted prior run is resumed — re-verified, not re-encrypted; a
 * corrupted leftover is discarded and the next run starts fresh), and an
 * already-migrated blob is a cheap no-op.
 */
public class EncryptedBlobMigrator {

	private static final String TEMP_KEY_SUFFIX = ".migrating";

	private final FileDataStore delegate;

	private final KeyEncryptionService keyEncryptionService;

	private final EncryptionParameters encryptionParameters;

	public EncryptedBlobMigrator(FileDataStore delegate, KeyEncryptionService keyEncryptionService,
			EncryptionParameters encryptionParameters) {
		if (delegate == null || keyEncryptionService == null || encryptionParameters == null) {
			throw new IllegalArgumentException(
					"delegate, keyEncryptionService and encryptionParameters must not be null");
		}
		this.delegate = delegate;
		this.keyEncryptionService = keyEncryptionService;
		this.encryptionParameters = encryptionParameters;
	}

	/** Peeks the blob's magic bytes only; never decrypts/returns content. */
	public boolean isLegacyBlob(FileMetaData metadata) throws IOException {
		byte[] peeked = new byte[EncryptedBlobHeader.magic().length];
		try (InputStream in = delegate.getRange(metadata, 0, peeked.length).openStream()) {
			int read = readAtMost(in, peeked);
			return !(read == peeked.length && Arrays.equals(peeked, EncryptedBlobHeader.magic()));
		}
	}

	public MigrationOutcome migrate(FileMetaData metadata, String expectedSha256Hex) throws IOException {
		if (!delegate.exists(metadata)) {
			return MigrationOutcome.MISSING;
		}
		FileMetaData tempMetadata = tempMetadataFor(metadata);

		if (!isLegacyBlob(metadata)) {
			if (delegate.exists(tempMetadata)) {
				// Leftover from a run that committed successfully but crashed
				// or errored before its own cleanup ran.
				delegate.remove(tempMetadata);
			}
			return MigrationOutcome.ALREADY_ENCRYPTED;
		}

		byte[] aadBlobId = metadata.getUuid().getBytes(StandardCharsets.UTF_8);

		if (!delegate.exists(tempMetadata)) {
			encryptToTemp(metadata, tempMetadata, aadBlobId);
		}

		if (!verifyTemp(tempMetadata, aadBlobId, expectedSha256Hex)) {
			delegate.remove(tempMetadata);
			return MigrationOutcome.VERIFICATION_FAILED;
		}

		commit(tempMetadata, metadata);
		return MigrationOutcome.MIGRATED;
	}

	private FileMetaData tempMetadataFor(FileMetaData metadata) {
		FileMetaData temp = new FileMetaData(metadata.getKind(), metadata.getMimeType(), metadata.getSize(),
				metadata.getFileName());
		temp.setUuid(metadata.getUuid() + TEMP_KEY_SUFFIX);
		temp.setBucketUuid(metadata.getBucketUuid());
		return temp;
	}

	private void encryptToTemp(FileMetaData metadata, FileMetaData tempMetadata, byte[] aadBlobId)
			throws IOException {
		long plaintextSize = metadata.getSize();
		ByteSource legacyPlaintext = delegate.get(metadata);
		ChunkedEncryptor encryptor = new ChunkedEncryptor(keyEncryptionService, encryptionParameters);
		EncryptingBlobContext ctx = encryptor.prepare(plaintextSize);
		try (ctx) {
			long physicalSize = ChunkLayout.of(ctx.getHeader()).totalPhysicalLength();
			FileMetaData physicalTempMetadata = new FileMetaData(tempMetadata.getKind(), tempMetadata.getMimeType(),
					physicalSize, tempMetadata.getFileName());
			physicalTempMetadata.setUuid(tempMetadata.getUuid());
			physicalTempMetadata.setBucketUuid(tempMetadata.getBucketUuid());

			ByteSource encryptingSource = new ByteSource() {
				@Override
				public InputStream openStream() throws IOException {
					return new EncryptingInputStream(encryptor, ctx, legacyPlaintext.openStream(), aadBlobId);
				}
			};
			delegate.add(encryptingSource, physicalTempMetadata);
		}
	}

	private boolean verifyTemp(FileMetaData tempMetadata, byte[] aadBlobId, String expectedSha256Hex)
			throws IOException {
		ChunkedDecryptor decryptor = new ChunkedDecryptor(keyEncryptionService);
		try (InputStream raw = delegate.get(tempMetadata).openStream()) {
			UnwrappedBlobContext ctx = decryptor.open(raw, aadBlobId);
			MessageDigest digest = sha256();
			try (DecryptingInputStream decrypting = new DecryptingInputStream(decryptor, ctx, raw);
					DigestInputStream digestIn = new DigestInputStream(decrypting, digest)) {
				ByteStreams.copy(digestIn, ByteStreams.nullOutputStream());
			}
			return toHex(digest.digest()).equalsIgnoreCase(expectedSha256Hex);
		} catch (EncryptedBlobAuthenticationException | EncryptedBlobFormatException e) {
			// The temp write itself is corrupt/truncated (e.g. an interrupted
			// prior run) — the original legacy blob is untouched either way.
			return false;
		}
	}

	private void commit(FileMetaData tempMetadata, FileMetaData realMetadata) throws IOException {
		if (delegate instanceof AtomicBlobReplace) {
			((AtomicBlobReplace) delegate).atomicReplace(realMetadata.getBucketUuid(), tempMetadata.getUuid(),
					realMetadata.getUuid());
			return;
		}
		// Best-effort fallback for backends without an atomic primitive: safe
		// to retry (a later run re-verifies the temp key before committing
		// again), but not atomic against a crash in the instant of this add().
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

	private static int readAtMost(InputStream in, byte[] buffer) throws IOException {
		int totalRead = 0;
		while (totalRead < buffer.length) {
			int read = in.read(buffer, totalRead, buffer.length - totalRead);
			if (read == -1) {
				break;
			}
			totalRead += read;
		}
		return totalRead;
	}

	private static MessageDigest sha256() {
		try {
			return MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("SHA-256 is not available", e);
		}
	}

	private static String toHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			sb.append(String.format(Locale.ROOT, "%02x", b));
		}
		return sb.toString();
	}
}
