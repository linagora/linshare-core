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
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class ChunkAadFactoryTest {

	private EncryptedBlobHeader header(String keyId) {
		long plaintextSize = 4096;
		int chunkPlaintextSize = 1024;
		return new EncryptedBlobHeader(EncryptedBlobHeader.FORMAT_VERSION, EncryptedBlobHeader.ALGORITHM_AES_256_GCM,
				RandomPrefixCounterNonceStrategy.SCHEME_ID, plaintextSize, chunkPlaintextSize,
				EncryptedBlobHeader.computeChunkCount(plaintextSize, chunkPlaintextSize),
				new byte[EncryptedBlobHeader.NONCE_PREFIX_LENGTH_BYTES], keyId, 64, new byte[48], 512);
	}

	@Test
	void deterministicForIdenticalInputs() {
		EncryptedBlobHeader h = header("key-1");
		byte[] blobId = "blob-1".getBytes(StandardCharsets.UTF_8);
		assertArrayEquals(ChunkAadFactory.build(h, blobId, 3), ChunkAadFactory.build(h, blobId, 3));
	}

	@Test
	void differentChunkIndexProducesDifferentBytes() {
		EncryptedBlobHeader h = header("key-1");
		byte[] blobId = "blob-1".getBytes(StandardCharsets.UTF_8);
		assertFalse(java.util.Arrays.equals(ChunkAadFactory.build(h, blobId, 0), ChunkAadFactory.build(h, blobId, 1)));
	}

	@Test
	void differentBlobIdProducesDifferentBytes() {
		EncryptedBlobHeader h = header("key-1");
		byte[] blobIdA = "blob-A".getBytes(StandardCharsets.UTF_8);
		byte[] blobIdB = "blob-B".getBytes(StandardCharsets.UTF_8);
		assertFalse(java.util.Arrays.equals(ChunkAadFactory.build(h, blobIdA, 0), ChunkAadFactory.build(h, blobIdB, 0)));
	}

	@Test
	void lengthPrefixingPreventsAmbiguousConcatenation() {
		// keyId="ab" + blobId="cdef" must not serialize the same as keyId="abcd" + blobId="ef"
		EncryptedBlobHeader headerAb = header("ab");
		EncryptedBlobHeader headerAbcd = header("abcd");
		byte[] aadAbCdef = ChunkAadFactory.build(headerAb, "cdef".getBytes(StandardCharsets.UTF_8), 0);
		byte[] aadAbcdEf = ChunkAadFactory.build(headerAbcd, "ef".getBytes(StandardCharsets.UTF_8), 0);
		assertFalse(java.util.Arrays.equals(aadAbCdef, aadAbcdEf));
	}
}
