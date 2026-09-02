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
import java.nio.charset.StandardCharsets;

import org.linagora.linshare.storage.encryption.exception.EncryptedBlobFormatException;

/**
 * Builds the AES-GCM additional authenticated data for one chunk, binding
 * format version, plaintext size, chunk size, chunk index, key id and the
 * caller-supplied blob identifier (ARCH.md 7.2) so that header tampering,
 * chunk swapping and chunk replay-at-a-different-index all fail
 * authentication. Every variable-length field is length-prefixed so distinct
 * (keyId, blobId) pairs never serialize to the same bytes.
 */
public final class ChunkAadFactory {

	private ChunkAadFactory() {
	}

	public static byte[] build(EncryptedBlobHeader header, byte[] blobId, long chunkIndex) {
		if (header == null) {
			throw new EncryptedBlobFormatException("header must not be null");
		}
		if (blobId == null) {
			throw new EncryptedBlobFormatException("blobId is required and must not be null");
		}
		byte[] keyIdBytes = header.getKeyId().getBytes(StandardCharsets.UTF_8);

		int size = 1 + 1 + 1 // formatVersion, algorithmId, nonceSchemeId
				+ 8 // plaintextSize
				+ 4 // chunkPlaintextSize
				+ 8 // chunkCount
				+ 8 // chunkIndex
				+ 2 + keyIdBytes.length // keyIdLength + keyId
				+ 4 + blobId.length; // blobIdLength + blobId

		ByteBuffer buffer = ByteBuffer.allocate(size);
		buffer.put((byte) header.getFormatVersion());
		buffer.put((byte) header.getAlgorithmId());
		buffer.put((byte) header.getNonceSchemeId());
		buffer.putLong(header.getPlaintextSize());
		buffer.putInt(header.getChunkPlaintextSize());
		buffer.putLong(header.getChunkCount());
		buffer.putLong(chunkIndex);
		buffer.putShort((short) keyIdBytes.length);
		buffer.put(keyIdBytes);
		buffer.putInt(blobId.length);
		buffer.put(blobId);
		return buffer.array();
	}
}
