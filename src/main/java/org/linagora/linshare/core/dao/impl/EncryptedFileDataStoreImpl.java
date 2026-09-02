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
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.storage.encryption.crypto.ChunkedDecryptor;
import org.linagora.linshare.storage.encryption.crypto.ChunkedEncryptor;
import org.linagora.linshare.storage.encryption.crypto.DecryptingInputStream;
import org.linagora.linshare.storage.encryption.crypto.EncryptingBlobContext;
import org.linagora.linshare.storage.encryption.crypto.EncryptingInputStream;
import org.linagora.linshare.storage.encryption.crypto.EncryptionParameters;
import org.linagora.linshare.storage.encryption.crypto.UnwrappedBlobContext;
import org.linagora.linshare.storage.encryption.exception.EncryptedBlobFormatException;
import org.linagora.linshare.storage.encryption.format.ChunkLayout;
import org.linagora.linshare.storage.encryption.format.ChunkRange;
import org.linagora.linshare.storage.encryption.format.EncryptedBlobFormat;
import org.linagora.linshare.storage.encryption.format.EncryptedBlobHeader;
import org.linagora.linshare.storage.encryption.format.RangeMapper;
import org.linagora.linshare.storage.encryption.key.KeyEncryptionService;

import com.google.common.io.ByteSource;

/**
 * Decorates a {@link FileDataStore} with transparent chunked AES-256-GCM
 * encryption at rest (ARCH.md, docs/CLAUDE.md). Higher-level LinShare code
 * keeps dealing exclusively in plaintext streams and plaintext
 * {@link FileMetaData#getSize()} values; only the bytes actually handed to
 * the delegate store are ciphertext.
 *
 * <p>{@code remove}/{@code exists} are pure pass-through: deletion never
 * requires the key service (ARCH.md 18, 20).
 */
public class EncryptedFileDataStoreImpl implements FileDataStore {

	private final FileDataStore delegate;

	private final KeyEncryptionService keyEncryptionService;

	private final EncryptionParameters encryptionParameters;

	private final boolean writeEnabled;

	private final boolean readEnabled;

	private final boolean allowLegacyRead;

	private final EncryptedBlobMigrator migrator;

	public EncryptedFileDataStoreImpl(FileDataStore delegate, KeyEncryptionService keyEncryptionService,
			EncryptionParameters encryptionParameters, boolean writeEnabled, boolean readEnabled,
			boolean allowLegacyRead) {
		if (delegate == null || keyEncryptionService == null || encryptionParameters == null) {
			throw new IllegalArgumentException("delegate, keyEncryptionService and encryptionParameters must not be null");
		}
		this.delegate = delegate;
		this.keyEncryptionService = keyEncryptionService;
		this.encryptionParameters = encryptionParameters;
		this.writeEnabled = writeEnabled;
		this.readEnabled = readEnabled;
		this.allowLegacyRead = allowLegacyRead;
		this.migrator = new EncryptedBlobMigrator(delegate, keyEncryptionService, encryptionParameters);
	}

	/** @see EncryptedBlobMigrator#isLegacyBlob */
	public boolean isLegacyBlob(FileMetaData metadata) throws IOException {
		return migrator.isLegacyBlob(metadata);
	}

	/** @see EncryptedBlobMigrator#migrate */
	public MigrationOutcome migrateLegacyBlob(FileMetaData metadata, String expectedSha256Hex) throws IOException {
		return migrator.migrate(metadata, expectedSha256Hex);
	}

	@Override
	public void remove(FileMetaData metadata) {
		delegate.remove(metadata);
	}

	@Override
	public boolean exists(FileMetaData metadata) {
		return delegate.exists(metadata);
	}

	@Override
	public FileMetaData add(ByteSource byteSource, FileMetaData metadata) throws IOException {
		if (!writeEnabled) {
			return delegate.add(byteSource, metadata);
		}
		if (metadata.getSize() == null) {
			throw new IllegalArgumentException("metadata.size is required to encrypt a blob");
		}
		if (metadata.getUuid() == null) {
			// Generated here (rather than left to the delegate) so the blob id is
			// stable and known before any byte is encrypted, since it is bound
			// into every chunk's authenticated data.
			metadata.setUuid(UUID.randomUUID().toString());
		}
		byte[] blobId = metadata.getUuid().getBytes(StandardCharsets.UTF_8);
		long plaintextSize = metadata.getSize();

		ChunkedEncryptor encryptor = new ChunkedEncryptor(keyEncryptionService, encryptionParameters);
		EncryptingBlobContext ctx = encryptor.prepare(plaintextSize);
		try (ctx) {
			// The physical (ciphertext) length is a deterministic function of the
			// header alone, known before a single byte is encrypted. The delegate
			// store must receive this exact length: at least the filesystem
			// provider verifies actual bytes written against the declared size and
			// fails the upload on any mismatch.
			long physicalSize = ChunkLayout.of(ctx.getHeader()).totalPhysicalLength();
			FileMetaData physicalMetadata = new FileMetaData(metadata.getKind(), metadata.getMimeType(), physicalSize,
					metadata.getFileName());
			physicalMetadata.setUuid(metadata.getUuid());
			if (metadata.getBucketUuid() != null) {
				physicalMetadata.setBucketUuid(metadata.getBucketUuid());
			}

			ByteSource encryptingByteSource = new ByteSource() {
				@Override
				public InputStream openStream() throws IOException {
					return new EncryptingInputStream(encryptor, ctx, byteSource.openStream(), blobId);
				}
			};

			FileMetaData stored = delegate.add(encryptingByteSource, physicalMetadata);

			// The caller's original metadata is what carries the plaintext size
			// onward (e.g. into Document.size); only its uuid/bucketUuid may need
			// to be synchronized with whatever the delegate assigned.
			metadata.setUuid(stored.getUuid());
			if (stored.getBucketUuid() != null) {
				metadata.setBucketUuid(stored.getBucketUuid());
			}
			return metadata;
		}
	}

	@Override
	public ByteSource get(FileMetaData metadata) {
		if (!readEnabled) {
			return delegate.get(metadata);
		}
		byte[] blobId = metadata.getUuid().getBytes(StandardCharsets.UTF_8);
		ByteSource innerSource = delegate.get(metadata);

		return new ByteSource() {
			@Override
			public InputStream openStream() throws IOException {
				InputStream raw = innerSource.openStream();
				byte[] peeked = new byte[EncryptedBlobHeader.magic().length];
				int peekedLength = readAtMost(raw, peeked);

				if (peekedLength == peeked.length && Arrays.equals(peeked, EncryptedBlobHeader.magic())) {
					InputStream reconstructed = new SequenceInputStream(new ByteArrayInputStream(peeked), raw);
					ChunkedDecryptor decryptor = new ChunkedDecryptor(keyEncryptionService);
					UnwrappedBlobContext ctx = decryptor.open(reconstructed, blobId);
					return new DecryptingInputStream(decryptor, ctx, reconstructed);
				}
				if (allowLegacyRead) {
					return new SequenceInputStream(new ByteArrayInputStream(peeked, 0, peekedLength), raw);
				}
				raw.close();
				throw new EncryptedBlobFormatException(
						"blob " + metadata.getUuid() + " is not an LSE1 blob and legacy reads are disabled");
			}
		};
	}

	@Override
	public ByteSource getRange(FileMetaData metadata, long offset, long length) {
		if (!readEnabled) {
			return delegate.getRange(metadata, offset, length);
		}
		byte[] blobId = metadata.getUuid().getBytes(StandardCharsets.UTF_8);

		return new ByteSource() {
			@Override
			public InputStream openStream() throws IOException {
				byte[] peeked = new byte[EncryptedBlobHeader.magic().length];
				try (InputStream peekIn = delegate.getRange(metadata, 0, peeked.length).openStream()) {
					readAtMost(peekIn, peeked);
				}

				if (Arrays.equals(peeked, EncryptedBlobHeader.magic())) {
					EncryptedBlobHeader header;
					// Bounded: the header's true length depends on its own
					// per-blob reserved capacities, only known once parsed, so
					// this fetches a generous but bounded upper bound rather
					// than the whole (possibly huge) object; any unused
					// trailing bytes are simply never read by readHeader.
					try (InputStream headerIn = delegate
							.getRange(metadata, 0, EncryptedBlobHeader.MAX_HEADER_LENGTH).openStream()) {
						header = EncryptedBlobFormat.readHeader(headerIn);
					}

					ChunkLayout layout = ChunkLayout.of(header);
					ChunkRange chunkRange = RangeMapper.map(layout, offset, length);
					long physicalStart = layout.chunkRecordOffset(chunkRange.getFirstChunkIndex());
					long physicalEndExclusive = layout.chunkRecordOffset(chunkRange.getLastChunkIndex())
							+ layout.chunkCiphertextLength(chunkRange.getLastChunkIndex());

					InputStream chunkDataIn = delegate
							.getRange(metadata, physicalStart, physicalEndExclusive - physicalStart).openStream();
					ChunkedDecryptor decryptor = new ChunkedDecryptor(keyEncryptionService);
					UnwrappedBlobContext ctx = decryptor.openFromHeader(header, blobId);
					return new DecryptingInputStream(decryptor, ctx, chunkDataIn, chunkRange.getFirstChunkIndex(),
							chunkRange.getLastChunkIndex(), chunkRange.getSkipBytesInFirstChunk(),
							chunkRange.getRequestedPlaintextLength());
				}
				if (allowLegacyRead) {
					return delegate.getRange(metadata, offset, length).openStream();
				}
				throw new EncryptedBlobFormatException(
						"blob " + metadata.getUuid() + " is not an LSE1 blob and legacy reads are disabled");
			}
		};
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
}
