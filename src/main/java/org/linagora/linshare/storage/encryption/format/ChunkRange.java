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
 * The inclusive chunk span a plaintext byte range maps onto, plus the number
 * of leading bytes of the first decrypted chunk to discard before the
 * requested range actually begins.
 */
public final class ChunkRange {

	private final long firstChunkIndex;

	private final long lastChunkIndex;

	private final int skipBytesInFirstChunk;

	private final long requestedPlaintextLength;

	public ChunkRange(long firstChunkIndex, long lastChunkIndex, int skipBytesInFirstChunk,
			long requestedPlaintextLength) {
		this.firstChunkIndex = firstChunkIndex;
		this.lastChunkIndex = lastChunkIndex;
		this.skipBytesInFirstChunk = skipBytesInFirstChunk;
		this.requestedPlaintextLength = requestedPlaintextLength;
	}

	public long getFirstChunkIndex() {
		return firstChunkIndex;
	}

	public long getLastChunkIndex() {
		return lastChunkIndex;
	}

	public int getSkipBytesInFirstChunk() {
		return skipBytesInFirstChunk;
	}

	public long getRequestedPlaintextLength() {
		return requestedPlaintextLength;
	}
}
