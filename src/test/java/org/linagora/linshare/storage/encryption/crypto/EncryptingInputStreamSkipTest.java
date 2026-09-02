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
package org.linagora.linshare.storage.encryption.crypto;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.linagora.linshare.storage.encryption.key.KeyEncryptionService;
import org.linagora.linshare.storage.encryption.key.LocalKeyEncryptionService;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

/**
 * Proves {@link EncryptingInputStream#skip} both produces the exact same
 * tail bytes as reading-and-discarding would, and actually avoids the
 * expensive AES-GCM work for whole chunks it skips past — the optimization
 * jclouds' generic multipart upload slicing depends on for large objects
 * (docs/ARCH.md 18, docs/PHASE7-OBJECT-STORAGE.md).
 */
class EncryptingInputStreamSkipTest {

	private static final byte[] BLOB_ID = "blob-1".getBytes(StandardCharsets.UTF_8);

	private static final int CHUNK_SIZE = 16;

	private KeyEncryptionService newKeyService() {
		byte[] masterKey = new byte[32];
		new SecureRandom().nextBytes(masterKey);
		return new LocalKeyEncryptionService(masterKey, "test-kek");
	}

	private static byte[] randomBytes(int length) {
		byte[] bytes = new byte[length];
		new Random(29).nextBytes(bytes);
		return bytes;
	}

	@Test
	void skipZeroOrNegativeReturnsZeroWithoutTouchingAnything() throws Exception {
		ChunkedEncryptor encryptor = new ChunkedEncryptor(newKeyService(), new EncryptionParameters(CHUNK_SIZE, 64, 512));
		EncryptingBlobContext ctx = encryptor.prepare(0);
		try (EncryptingInputStream in = new EncryptingInputStream(encryptor, ctx, new ByteArrayInputStream(new byte[0]),
				BLOB_ID)) {
			assertEquals(0, in.skip(0));
			assertEquals(0, in.skip(-5));
		} finally {
			ctx.close();
		}
	}

	@Test
	void skipPastMultipleWholeChunksLandingMidChunkProducesCorrectTailAndAvoidsEncryptingSkippedChunks()
			throws Exception {
		KeyEncryptionService keyService = newKeyService();
		EncryptionParameters params = new EncryptionParameters(CHUNK_SIZE, 64, 512);
		byte[] plaintext = randomBytes(CHUNK_SIZE * 5 + 3); // 6 chunks: 5 full + 1 three-byte tail

		ChunkedEncryptor encryptor = new ChunkedEncryptor(keyService, params);
		EncryptingBlobContext ctx = encryptor.prepare(plaintext.length);
		try {
			byte[] fullEncrypted;
			try (EncryptingInputStream baseline = new EncryptingInputStream(encryptor, ctx,
					new ByteArrayInputStream(plaintext), BLOB_ID)) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				ByteStreams.copy(baseline, out);
				fullEncrypted = out.toByteArray();
			}

			// Skip past the header, then chunks 0 and 1 in full (each 16+16=32
			// bytes ciphertext), landing 5 bytes into chunk 2's ciphertext.
			long headerLength = org.linagora.linshare.storage.encryption.format.ChunkLayout.of(ctx.getHeader())
					.headerTotalLength();
			long skipAmount = headerLength + 2L * (CHUNK_SIZE + 16) + 5;
			SkipTrackingInputStream tracked = new SkipTrackingInputStream(new ByteArrayInputStream(plaintext));
			byte[] tail;
			try (EncryptingInputStream skipping = new EncryptingInputStream(encryptor, ctx, tracked, BLOB_ID)) {
				long actuallySkipped = skipping.skip(skipAmount);
				assertEquals(skipAmount, actuallySkipped);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				ByteStreams.copy(skipping, out);
				tail = out.toByteArray();
			}

			byte[] expectedTail = Arrays.copyOfRange(fullEncrypted, (int) skipAmount, fullEncrypted.length);
			assertArrayEquals(expectedTail, tail);

			// The two whole skipped chunks' plaintext (32 bytes) must have been
			// skip()'d, not read()'d — proving their AES-GCM encryption was
			// never performed.
			assertTrue(tracked.totalSkipped >= CHUNK_SIZE * 2,
					"expected at least " + (CHUNK_SIZE * 2) + " bytes skipped, got " + tracked.totalSkipped);
		} finally {
			ctx.close();
		}
	}

	@Test
	void skipExactlyToAChunkBoundaryLandsCleanlyOnTheNextChunk() throws Exception {
		KeyEncryptionService keyService = newKeyService();
		EncryptionParameters params = new EncryptionParameters(CHUNK_SIZE, 64, 512);
		byte[] plaintext = randomBytes(CHUNK_SIZE * 4);

		ChunkedEncryptor encryptor = new ChunkedEncryptor(keyService, params);
		EncryptingBlobContext ctx = encryptor.prepare(plaintext.length);
		try {
			byte[] fullEncrypted;
			try (EncryptingInputStream baseline = new EncryptingInputStream(encryptor, ctx,
					new ByteArrayInputStream(plaintext), BLOB_ID)) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				ByteStreams.copy(baseline, out);
				fullEncrypted = out.toByteArray();
			}

			long headerLength = org.linagora.linshare.storage.encryption.format.ChunkLayout.of(ctx.getHeader())
					.headerTotalLength();
			long skipAmount = headerLength + 2L * (CHUNK_SIZE + 16); // header, then exactly two whole chunk records
			byte[] tail;
			try (EncryptingInputStream skipping = new EncryptingInputStream(encryptor, ctx,
					new ByteArrayInputStream(plaintext), BLOB_ID)) {
				assertEquals(skipAmount, skipping.skip(skipAmount));
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				ByteStreams.copy(skipping, out);
				tail = out.toByteArray();
			}

			assertArrayEquals(Arrays.copyOfRange(fullEncrypted, (int) skipAmount, fullEncrypted.length), tail);
		} finally {
			ctx.close();
		}
	}

	@Test
	void skipComposedWithGuavaByteSourceSliceMatchesMultipartUsagePattern() throws Exception {
		KeyEncryptionService keyService = newKeyService();
		EncryptionParameters params = new EncryptionParameters(CHUNK_SIZE, 64, 512);
		byte[] plaintext = randomBytes(CHUNK_SIZE * 6 + 7);

		ChunkedEncryptor encryptor = new ChunkedEncryptor(keyService, params);
		EncryptingBlobContext ctx = encryptor.prepare(plaintext.length);
		try {
			byte[] fullEncrypted;
			try (EncryptingInputStream baseline = new EncryptingInputStream(encryptor, ctx,
					new ByteArrayInputStream(plaintext), BLOB_ID)) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				ByteStreams.copy(baseline, out);
				fullEncrypted = out.toByteArray();
			}

			// Exactly what jclouds' BasePayloadSlicer does per multipart part:
			// ByteSource.slice(offset, length) on the encrypting ByteSource.
			ByteSource encryptingByteSource = new ByteSource() {
				@Override
				public InputStream openStream() throws IOException {
					return new EncryptingInputStream(encryptor, ctx, new ByteArrayInputStream(plaintext), BLOB_ID);
				}
			};

			long headerLength = org.linagora.linshare.storage.encryption.format.ChunkLayout.of(ctx.getHeader())
					.headerTotalLength();
			long partOffset = headerLength + CHUNK_SIZE + 16 + 4; // header, chunk 0, then partway into chunk 1
			long partLength = 40;
			byte[] partBytes = encryptingByteSource.slice(partOffset, partLength).read();

			byte[] expected = Arrays.copyOfRange(fullEncrypted, (int) partOffset, (int) (partOffset + partLength));
			assertArrayEquals(expected, partBytes);
		} finally {
			ctx.close();
		}
	}

	/** Tracks how many bytes were discarded via {@code skip()} rather than {@code read()}. */
	private static final class SkipTrackingInputStream extends InputStream {

		private final InputStream delegate;

		long totalSkipped;

		SkipTrackingInputStream(InputStream delegate) {
			this.delegate = delegate;
		}

		@Override
		public int read() throws IOException {
			return delegate.read();
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			return delegate.read(b, off, len);
		}

		@Override
		public long skip(long n) throws IOException {
			long skipped = delegate.skip(n);
			totalSkipped += skipped;
			return skipped;
		}
	}
}
