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

class RangeMapperTest {

	private static final int CHUNK_SIZE = 16;

	private static final long PLAINTEXT_SIZE = CHUNK_SIZE * 5L; // 80 bytes, chunks 0..4

	private ChunkLayout layout() {
		EncryptedBlobHeader header = new EncryptedBlobHeader(EncryptedBlobHeader.FORMAT_VERSION,
				EncryptedBlobHeader.ALGORITHM_AES_256_GCM, RandomPrefixCounterNonceStrategy.SCHEME_ID,
				PLAINTEXT_SIZE, CHUNK_SIZE, EncryptedBlobHeader.computeChunkCount(PLAINTEXT_SIZE, CHUNK_SIZE),
				new byte[EncryptedBlobHeader.NONCE_PREFIX_LENGTH_BYTES], "key", 64, new byte[48], 512);
		return ChunkLayout.of(header);
	}

	@Test
	void firstByteMapsToChunkZero() {
		ChunkRange range = RangeMapper.map(layout(), 0, 1);
		assertEquals(0, range.getFirstChunkIndex());
		assertEquals(0, range.getLastChunkIndex());
		assertEquals(0, range.getSkipBytesInFirstChunk());
		assertEquals(1, range.getRequestedPlaintextLength());
	}

	@Test
	void lastByteMapsToLastChunk() {
		ChunkRange range = RangeMapper.map(layout(), PLAINTEXT_SIZE - 1, 1);
		assertEquals(4, range.getFirstChunkIndex());
		assertEquals(4, range.getLastChunkIndex());
		assertEquals(CHUNK_SIZE - 1, range.getSkipBytesInFirstChunk());
	}

	@Test
	void exactOneChunkAlignedRange() {
		ChunkRange range = RangeMapper.map(layout(), CHUNK_SIZE, CHUNK_SIZE);
		assertEquals(1, range.getFirstChunkIndex());
		assertEquals(1, range.getLastChunkIndex());
		assertEquals(0, range.getSkipBytesInFirstChunk());
	}

	@Test
	void rangeStartingOneByteBeforeBoundary() {
		ChunkRange range = RangeMapper.map(layout(), CHUNK_SIZE - 1, 1);
		assertEquals(0, range.getFirstChunkIndex());
		assertEquals(0, range.getLastChunkIndex());
		assertEquals(CHUNK_SIZE - 1, range.getSkipBytesInFirstChunk());
	}

	@Test
	void rangeCrossingABoundary() {
		ChunkRange range = RangeMapper.map(layout(), CHUNK_SIZE - 1, 2);
		assertEquals(0, range.getFirstChunkIndex());
		assertEquals(1, range.getLastChunkIndex());
	}

	@Test
	void rangeSpanningManyChunks() {
		ChunkRange range = RangeMapper.map(layout(), 5, CHUNK_SIZE * 3L);
		assertEquals(0, range.getFirstChunkIndex());
		assertEquals(3, range.getLastChunkIndex());
		assertEquals(5, range.getSkipBytesInFirstChunk());
	}

	@Test
	void completeFileRangeMapsToAllChunks() {
		ChunkRange range = RangeMapper.map(layout(), 0, PLAINTEXT_SIZE);
		assertEquals(0, range.getFirstChunkIndex());
		assertEquals(4, range.getLastChunkIndex());
		assertEquals(0, range.getSkipBytesInFirstChunk());
		assertEquals(PLAINTEXT_SIZE, range.getRequestedPlaintextLength());
	}

	@Test
	void negativeOffsetIsRejected() {
		assertThrows(IllegalArgumentException.class, () -> RangeMapper.map(layout(), -1, 1));
	}

	@Test
	void zeroOrNegativeLengthIsRejected() {
		assertThrows(IllegalArgumentException.class, () -> RangeMapper.map(layout(), 0, 0));
		assertThrows(IllegalArgumentException.class, () -> RangeMapper.map(layout(), 0, -1));
	}

	@Test
	void rangeExceedingPlaintextSizeIsRejected() {
		assertThrows(IllegalArgumentException.class, () -> RangeMapper.map(layout(), PLAINTEXT_SIZE - 1, 2));
		assertThrows(IllegalArgumentException.class, () -> RangeMapper.map(layout(), PLAINTEXT_SIZE + 1, 1));
	}
}
