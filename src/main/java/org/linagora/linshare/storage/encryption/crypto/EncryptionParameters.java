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

import org.linagora.linshare.storage.encryption.format.EncryptedBlobHeader;

/**
 * Caller-chosen policy for encrypting a new blob. The chunk size recorded
 * here becomes immutable for that blob once written (ARCH.md 6).
 */
public final class EncryptionParameters {

	private final int chunkPlaintextSize;

	private final int reservedKeyIdCapacity;

	private final int reservedWrappedKeyCapacity;

	public EncryptionParameters() {
		this(EncryptedBlobHeader.DEFAULT_CHUNK_PLAINTEXT_SIZE, EncryptedBlobHeader.DEFAULT_KEY_ID_CAPACITY,
				EncryptedBlobHeader.DEFAULT_WRAPPED_KEY_CAPACITY);
	}

	public EncryptionParameters(int chunkPlaintextSize, int reservedKeyIdCapacity, int reservedWrappedKeyCapacity) {
		if (chunkPlaintextSize <= 0 || chunkPlaintextSize > EncryptedBlobHeader.MAX_CHUNK_PLAINTEXT_SIZE) {
			throw new IllegalArgumentException("chunkPlaintextSize out of range: " + chunkPlaintextSize);
		}
		if (reservedKeyIdCapacity <= 0 || reservedKeyIdCapacity > EncryptedBlobHeader.MAX_KEY_ID_CAPACITY) {
			throw new IllegalArgumentException("reservedKeyIdCapacity out of range: " + reservedKeyIdCapacity);
		}
		if (reservedWrappedKeyCapacity <= 0
				|| reservedWrappedKeyCapacity > EncryptedBlobHeader.MAX_WRAPPED_KEY_CAPACITY) {
			throw new IllegalArgumentException(
					"reservedWrappedKeyCapacity out of range: " + reservedWrappedKeyCapacity);
		}
		this.chunkPlaintextSize = chunkPlaintextSize;
		this.reservedKeyIdCapacity = reservedKeyIdCapacity;
		this.reservedWrappedKeyCapacity = reservedWrappedKeyCapacity;
	}

	public int getChunkPlaintextSize() {
		return chunkPlaintextSize;
	}

	public int getReservedKeyIdCapacity() {
		return reservedKeyIdCapacity;
	}

	public int getReservedWrappedKeyCapacity() {
		return reservedWrappedKeyCapacity;
	}
}
