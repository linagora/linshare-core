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
package org.linagora.linshare.storage.encryption.format;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.linagora.linshare.storage.encryption.exception.EncryptedBlobFormatException;
import org.linagora.linshare.storage.encryption.exception.EncryptedBlobUnsupportedVersionException;

class EncryptedBlobHeaderTest {

	private static final byte[] NONCE_PREFIX = new byte[EncryptedBlobHeader.NONCE_PREFIX_LENGTH_BYTES];

	private static final String KEY_ID = "test-key";

	private static final byte[] WRAPPED_KEY = new byte[48];

	private static EncryptedBlobHeader header(int formatVersion, int algorithmId, int nonceSchemeId,
			long plaintextSize, int chunkPlaintextSize, long chunkCount, byte[] noncePrefix, String keyId,
			int reservedKeyIdCapacity, byte[] wrappedKeyBytes, int reservedWrappedKeyCapacity) {
		return new EncryptedBlobHeader(formatVersion, algorithmId, nonceSchemeId, plaintextSize, chunkPlaintextSize,
				chunkCount, noncePrefix, keyId, reservedKeyIdCapacity, wrappedKeyBytes, reservedWrappedKeyCapacity);
	}

	@Test
	void validHeaderExposesSuppliedFields() {
		long plaintextSize = 2048;
		int chunkPlaintextSize = 1024;
		long chunkCount = EncryptedBlobHeader.computeChunkCount(plaintextSize, chunkPlaintextSize);

		EncryptedBlobHeader h = header(EncryptedBlobHeader.FORMAT_VERSION, EncryptedBlobHeader.ALGORITHM_AES_256_GCM,
				RandomPrefixCounterNonceStrategy.SCHEME_ID, plaintextSize, chunkPlaintextSize, chunkCount,
				NONCE_PREFIX, KEY_ID, 64, WRAPPED_KEY, 512);

		assertEquals(plaintextSize, h.getPlaintextSize());
		assertEquals(chunkPlaintextSize, h.getChunkPlaintextSize());
		assertEquals(chunkCount, h.getChunkCount());
		assertEquals(KEY_ID, h.getKeyId());
		assertArrayEquals(NONCE_PREFIX, h.getNoncePrefix());
		assertArrayEquals(WRAPPED_KEY, h.getWrappedKeyBytes());
	}

	@Test
	void emptyPlaintextProducesExactlyOneChunk() {
		assertEquals(1, EncryptedBlobHeader.computeChunkCount(0, 1024));
	}

	@Test
	void rejectsUnsupportedFormatVersion() {
		assertThrows(EncryptedBlobUnsupportedVersionException.class,
				() -> header(99, EncryptedBlobHeader.ALGORITHM_AES_256_GCM,
						RandomPrefixCounterNonceStrategy.SCHEME_ID, 1024, 1024, 1, NONCE_PREFIX, KEY_ID, 64,
						WRAPPED_KEY, 512));
	}

	@Test
	void rejectsUnsupportedAlgorithmId() {
		assertThrows(EncryptedBlobUnsupportedVersionException.class,
				() -> header(EncryptedBlobHeader.FORMAT_VERSION, 99, RandomPrefixCounterNonceStrategy.SCHEME_ID, 1024,
						1024, 1, NONCE_PREFIX, KEY_ID, 64, WRAPPED_KEY, 512));
	}

	@Test
	void rejectsUnsupportedNonceSchemeId() {
		assertThrows(EncryptedBlobUnsupportedVersionException.class,
				() -> header(EncryptedBlobHeader.FORMAT_VERSION, EncryptedBlobHeader.ALGORITHM_AES_256_GCM, 99, 1024,
						1024, 1, NONCE_PREFIX, KEY_ID, 64, WRAPPED_KEY, 512));
	}

	@Test
	void rejectsNegativePlaintextSize() {
		assertThrows(EncryptedBlobFormatException.class,
				() -> header(EncryptedBlobHeader.FORMAT_VERSION, EncryptedBlobHeader.ALGORITHM_AES_256_GCM,
						RandomPrefixCounterNonceStrategy.SCHEME_ID, -1, 1024, 1, NONCE_PREFIX, KEY_ID, 64,
						WRAPPED_KEY, 512));
	}

	@Test
	void rejectsZeroChunkPlaintextSize() {
		assertThrows(EncryptedBlobFormatException.class,
				() -> header(EncryptedBlobHeader.FORMAT_VERSION, EncryptedBlobHeader.ALGORITHM_AES_256_GCM,
						RandomPrefixCounterNonceStrategy.SCHEME_ID, 1024, 0, 1, NONCE_PREFIX, KEY_ID, 64, WRAPPED_KEY,
						512));
	}

	@Test
	void rejectsChunkPlaintextSizeAboveMax() {
		assertThrows(EncryptedBlobFormatException.class,
				() -> header(EncryptedBlobHeader.FORMAT_VERSION, EncryptedBlobHeader.ALGORITHM_AES_256_GCM,
						RandomPrefixCounterNonceStrategy.SCHEME_ID, 1024,
						EncryptedBlobHeader.MAX_CHUNK_PLAINTEXT_SIZE + 1, 1, NONCE_PREFIX, KEY_ID, 64, WRAPPED_KEY,
						512));
	}

	@Test
	void rejectsChunkCountInconsistentWithPlaintextAndChunkSize() {
		assertThrows(EncryptedBlobFormatException.class,
				() -> header(EncryptedBlobHeader.FORMAT_VERSION, EncryptedBlobHeader.ALGORITHM_AES_256_GCM,
						RandomPrefixCounterNonceStrategy.SCHEME_ID, 2048, 1024, 3, NONCE_PREFIX, KEY_ID, 64,
						WRAPPED_KEY, 512));
	}

	@Test
	void rejectsChunkCountExceedingNonceConstructionRange() {
		int chunkPlaintextSize = 1;
		long plaintextSize = RandomPrefixCounterNonceStrategy.MAX_CHUNK_INDEX + 2;
		long chunkCount = EncryptedBlobHeader.computeChunkCount(plaintextSize, chunkPlaintextSize);

		assertThrows(EncryptedBlobFormatException.class,
				() -> header(EncryptedBlobHeader.FORMAT_VERSION, EncryptedBlobHeader.ALGORITHM_AES_256_GCM,
						RandomPrefixCounterNonceStrategy.SCHEME_ID, plaintextSize, chunkPlaintextSize, chunkCount,
						NONCE_PREFIX, KEY_ID, 64, WRAPPED_KEY, 512));
	}

	@Test
	void rejectsWrongNoncePrefixLength() {
		assertThrows(EncryptedBlobFormatException.class,
				() -> header(EncryptedBlobHeader.FORMAT_VERSION, EncryptedBlobHeader.ALGORITHM_AES_256_GCM,
						RandomPrefixCounterNonceStrategy.SCHEME_ID, 1024, 1024, 1, new byte[4], KEY_ID, 64,
						WRAPPED_KEY, 512));
	}

	@Test
	void rejectsNullKeyId() {
		assertThrows(EncryptedBlobFormatException.class,
				() -> header(EncryptedBlobHeader.FORMAT_VERSION, EncryptedBlobHeader.ALGORITHM_AES_256_GCM,
						RandomPrefixCounterNonceStrategy.SCHEME_ID, 1024, 1024, 1, NONCE_PREFIX, null, 64,
						WRAPPED_KEY, 512));
	}

	@Test
	void rejectsKeyIdLengthAboveReservedCapacity() {
		assertThrows(EncryptedBlobFormatException.class,
				() -> header(EncryptedBlobHeader.FORMAT_VERSION, EncryptedBlobHeader.ALGORITHM_AES_256_GCM,
						RandomPrefixCounterNonceStrategy.SCHEME_ID, 1024, 1024, 1, NONCE_PREFIX, "a-long-key-id", 5,
						WRAPPED_KEY, 512));
	}

	@Test
	void rejectsReservedKeyIdCapacityAboveHardCeiling() {
		assertThrows(EncryptedBlobFormatException.class,
				() -> header(EncryptedBlobHeader.FORMAT_VERSION, EncryptedBlobHeader.ALGORITHM_AES_256_GCM,
						RandomPrefixCounterNonceStrategy.SCHEME_ID, 1024, 1024, 1, NONCE_PREFIX, KEY_ID,
						EncryptedBlobHeader.MAX_KEY_ID_CAPACITY + 1, WRAPPED_KEY, 512));
	}

	@Test
	void rejectsNullWrappedKeyBytes() {
		assertThrows(EncryptedBlobFormatException.class,
				() -> header(EncryptedBlobHeader.FORMAT_VERSION, EncryptedBlobHeader.ALGORITHM_AES_256_GCM,
						RandomPrefixCounterNonceStrategy.SCHEME_ID, 1024, 1024, 1, NONCE_PREFIX, KEY_ID, 64, null,
						512));
	}

	@Test
	void rejectsWrappedKeyLengthAboveReservedCapacity() {
		assertThrows(EncryptedBlobFormatException.class,
				() -> header(EncryptedBlobHeader.FORMAT_VERSION, EncryptedBlobHeader.ALGORITHM_AES_256_GCM,
						RandomPrefixCounterNonceStrategy.SCHEME_ID, 1024, 1024, 1, NONCE_PREFIX, KEY_ID, 64,
						new byte[20], 10));
	}

	@Test
	void rejectsReservedWrappedKeyCapacityAboveHardCeiling() {
		assertThrows(EncryptedBlobFormatException.class,
				() -> header(EncryptedBlobHeader.FORMAT_VERSION, EncryptedBlobHeader.ALGORITHM_AES_256_GCM,
						RandomPrefixCounterNonceStrategy.SCHEME_ID, 1024, 1024, 1, NONCE_PREFIX, KEY_ID, 64,
						WRAPPED_KEY, EncryptedBlobHeader.MAX_WRAPPED_KEY_CAPACITY + 1));
	}
}
