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

import java.nio.ByteBuffer;

import org.linagora.linshare.storage.encryption.exception.EncryptedBlobFormatException;

/**
 * Nonce = 8-byte random per-blob prefix || 4-byte big-endian chunk counter.
 * Uniqueness within one blob is a pure function of chunkIndex, since no two
 * chunk indices in a single blob ever collide; the random prefix guards
 * against cross-blob DEK reuse rather than being itself the uniqueness
 * argument for a single blob's stream.
 */
public final class RandomPrefixCounterNonceStrategy implements NonceStrategy {

	public static final int SCHEME_ID = 1;

	public static final int NONCE_COUNTER_LENGTH_BYTES = 4;

	public static final int NONCE_LENGTH_BYTES = EncryptedBlobHeader.NONCE_PREFIX_LENGTH_BYTES
			+ NONCE_COUNTER_LENGTH_BYTES;

	public static final long MAX_CHUNK_INDEX = 0xFFFFFFFFL;

	@Override
	public int schemeId() {
		return SCHEME_ID;
	}

	@Override
	public byte[] deriveNonce(byte[] noncePrefix, long chunkIndex) {
		if (noncePrefix == null || noncePrefix.length != EncryptedBlobHeader.NONCE_PREFIX_LENGTH_BYTES) {
			throw new EncryptedBlobFormatException(
					"noncePrefix must be exactly " + EncryptedBlobHeader.NONCE_PREFIX_LENGTH_BYTES + " bytes");
		}
		if (chunkIndex < 0 || chunkIndex > MAX_CHUNK_INDEX) {
			throw new EncryptedBlobFormatException("chunkIndex " + chunkIndex
					+ " exceeds the nonce construction's representable range");
		}
		ByteBuffer buffer = ByteBuffer.allocate(NONCE_LENGTH_BYTES);
		buffer.put(noncePrefix);
		buffer.putInt((int) chunkIndex);
		return buffer.array();
	}

	@Override
	public long maxChunkIndex() {
		return MAX_CHUNK_INDEX;
	}
}
