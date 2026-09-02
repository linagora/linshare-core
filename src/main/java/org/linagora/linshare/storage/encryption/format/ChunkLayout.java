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
 * Pure, allocation-free arithmetic derived from a validated header: physical
 * offsets and lengths of every chunk record. Every non-final chunk has a
 * fixed on-disk size, so any chunk's offset is O(1) (ARCH.md 9.2).
 */
public final class ChunkLayout {

	private final EncryptedBlobHeader header;

	private final long fullChunkRecordSize;

	private ChunkLayout(EncryptedBlobHeader header) {
		this.header = header;
		this.fullChunkRecordSize = (long) header.getChunkPlaintextSize() + EncryptedBlobHeader.GCM_TAG_LENGTH_BYTES;
	}

	public static ChunkLayout of(EncryptedBlobHeader header) {
		return new ChunkLayout(header);
	}

	public long headerTotalLength() {
		return Math.addExact(Math.addExact((long) EncryptedBlobHeader.FIXED_HEADER_LENGTH,
				header.getReservedKeyIdCapacity()), header.getReservedWrappedKeyCapacity());
	}

	public long chunkCount() {
		return header.getChunkCount();
	}

	public long totalPlaintextSize() {
		return header.getPlaintextSize();
	}

	public int chunkPlaintextSize() {
		return header.getChunkPlaintextSize();
	}

	public int chunkPlaintextLength(long chunkIndex) {
		validateChunkIndex(chunkIndex);
		long chunkCount = header.getChunkCount();
		if (chunkIndex < chunkCount - 1) {
			return header.getChunkPlaintextSize();
		}
		long consumedByPriorChunks = Math.multiplyExact(chunkIndex, (long) header.getChunkPlaintextSize());
		return (int) Math.subtractExact(header.getPlaintextSize(), consumedByPriorChunks);
	}

	public int chunkCiphertextLength(long chunkIndex) {
		return chunkPlaintextLength(chunkIndex) + EncryptedBlobHeader.GCM_TAG_LENGTH_BYTES;
	}

	public long chunkRecordOffset(long chunkIndex) {
		validateChunkIndex(chunkIndex);
		return Math.addExact(headerTotalLength(), Math.multiplyExact(chunkIndex, fullChunkRecordSize));
	}

	public long totalPhysicalLength() {
		long lastChunkIndex = header.getChunkCount() - 1;
		return Math.addExact(chunkRecordOffset(lastChunkIndex), chunkCiphertextLength(lastChunkIndex));
	}

	private void validateChunkIndex(long chunkIndex) {
		if (chunkIndex < 0 || chunkIndex >= header.getChunkCount()) {
			throw new IllegalArgumentException(
					"chunkIndex " + chunkIndex + " out of range [0, " + header.getChunkCount() + ")");
		}
	}
}
