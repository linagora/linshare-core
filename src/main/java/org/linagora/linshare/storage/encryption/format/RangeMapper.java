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

/**
 * Maps a plaintext byte range onto the chunk span that must be read and
 * authenticated to serve it (ARCH.md 9, 12). Pure math, no I/O: a future
 * range reader authenticates every chunk in the returned span in full before
 * slicing out the requested bytes.
 */
public final class RangeMapper {

	private RangeMapper() {
	}

	public static ChunkRange map(ChunkLayout layout, long plaintextOffset, long plaintextLength) {
		if (plaintextOffset < 0) {
			throw new IllegalArgumentException("plaintextOffset must not be negative: " + plaintextOffset);
		}
		if (plaintextLength <= 0) {
			throw new IllegalArgumentException("plaintextLength must be positive: " + plaintextLength);
		}
		long totalSize = layout.totalPlaintextSize();
		if (plaintextOffset > totalSize) {
			throw new IllegalArgumentException(
					"plaintextOffset " + plaintextOffset + " exceeds plaintext size " + totalSize);
		}
		long remaining = totalSize - plaintextOffset;
		if (plaintextLength > remaining) {
			throw new IllegalArgumentException("range [" + plaintextOffset + ", " + plaintextOffset + "+"
					+ plaintextLength + ") exceeds plaintext size " + totalSize);
		}

		int chunkPlaintextSize = layout.chunkPlaintextSize();
		long firstChunkIndex = plaintextOffset / chunkPlaintextSize;
		long endOffsetInclusive = plaintextOffset + plaintextLength - 1;
		long lastChunkIndex = endOffsetInclusive / chunkPlaintextSize;
		int skipBytesInFirstChunk = (int) (plaintextOffset % chunkPlaintextSize);

		return new ChunkRange(firstChunkIndex, lastChunkIndex, skipBytesInFirstChunk, plaintextLength);
	}
}
