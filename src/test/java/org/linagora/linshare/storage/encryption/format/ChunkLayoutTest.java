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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ChunkLayoutTest {

	private static final int CHUNK_SIZE = 16;

	private ChunkLayout layoutFor(long plaintextSize) {
		EncryptedBlobHeader header = new EncryptedBlobHeader(EncryptedBlobHeader.FORMAT_VERSION,
				EncryptedBlobHeader.ALGORITHM_AES_256_GCM, RandomPrefixCounterNonceStrategy.SCHEME_ID, plaintextSize,
				CHUNK_SIZE, EncryptedBlobHeader.computeChunkCount(plaintextSize, CHUNK_SIZE),
				new byte[EncryptedBlobHeader.NONCE_PREFIX_LENGTH_BYTES], "key", 64, new byte[48], 512);
		return ChunkLayout.of(header);
	}

	@Test
	void headerTotalLengthIsFixedHeaderPlusReservedRegions() {
		ChunkLayout layout = layoutFor(100);
		assertEquals(EncryptedBlobHeader.FIXED_HEADER_LENGTH + 64 + 512, layout.headerTotalLength());
	}

	@Test
	void emptyPlaintextHasOneZeroLengthChunk() {
		ChunkLayout layout = layoutFor(0);
		assertEquals(1, layout.chunkCount());
		assertEquals(0, layout.chunkPlaintextLength(0));
		assertEquals(EncryptedBlobHeader.GCM_TAG_LENGTH_BYTES, layout.chunkCiphertextLength(0));
	}

	@Test
	void oneByteLessThanChunkSizeIsASingleChunk() {
		ChunkLayout layout = layoutFor(CHUNK_SIZE - 1);
		assertEquals(1, layout.chunkCount());
		assertEquals(CHUNK_SIZE - 1, layout.chunkPlaintextLength(0));
	}

	@Test
	void exactChunkSizeIsASingleFullChunk() {
		ChunkLayout layout = layoutFor(CHUNK_SIZE);
		assertEquals(1, layout.chunkCount());
		assertEquals(CHUNK_SIZE, layout.chunkPlaintextLength(0));
	}

	@Test
	void oneByteMoreThanChunkSizeIsTwoChunks() {
		ChunkLayout layout = layoutFor(CHUNK_SIZE + 1);
		assertEquals(2, layout.chunkCount());
		assertEquals(CHUNK_SIZE, layout.chunkPlaintextLength(0));
		assertEquals(1, layout.chunkPlaintextLength(1));
	}

	@Test
	void manyChunksHaveFixedSizeExceptTheLast() {
		long plaintextSize = CHUNK_SIZE * 10L + 3;
		ChunkLayout layout = layoutFor(plaintextSize);
		assertEquals(11, layout.chunkCount());
		for (long i = 0; i < 10; i++) {
			assertEquals(CHUNK_SIZE, layout.chunkPlaintextLength(i));
		}
		assertEquals(3, layout.chunkPlaintextLength(10));
	}

	@Test
	void chunkRecordOffsetIsConstantSizeTimesIndex() {
		ChunkLayout layout = layoutFor(CHUNK_SIZE * 5L);
		long fullRecordSize = CHUNK_SIZE + EncryptedBlobHeader.GCM_TAG_LENGTH_BYTES;
		for (long i = 0; i < 5; i++) {
			assertEquals(layout.headerTotalLength() + i * fullRecordSize, layout.chunkRecordOffset(i));
		}
	}

	@Test
	void totalPhysicalLengthMatchesManualSum() {
		long plaintextSize = CHUNK_SIZE * 3L + 5;
		ChunkLayout layout = layoutFor(plaintextSize);
		long expected = layout.headerTotalLength();
		for (long i = 0; i < layout.chunkCount(); i++) {
			expected += layout.chunkCiphertextLength(i);
		}
		assertEquals(expected, layout.totalPhysicalLength());
	}

	@Test
	void chunkIndexOutOfRangeThrows() {
		ChunkLayout layout = layoutFor(CHUNK_SIZE * 2L);
		assertThrows(IllegalArgumentException.class, () -> layout.chunkPlaintextLength(2));
		assertThrows(IllegalArgumentException.class, () -> layout.chunkPlaintextLength(-1));
	}
}
