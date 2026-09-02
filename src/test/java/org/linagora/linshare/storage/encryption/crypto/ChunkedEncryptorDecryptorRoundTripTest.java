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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.linagora.linshare.storage.encryption.format.EncryptedBlobHeader;
import org.linagora.linshare.storage.encryption.key.KeyEncryptionService;
import org.linagora.linshare.storage.encryption.key.LocalKeyEncryptionService;

class ChunkedEncryptorDecryptorRoundTripTest {

	private static final byte[] BLOB_ID = "blob-under-test".getBytes(StandardCharsets.UTF_8);

	private KeyEncryptionService newKeyService() {
		byte[] masterKey = new byte[32];
		new SecureRandom().nextBytes(masterKey);
		return new LocalKeyEncryptionService(masterKey, "test-kek");
	}

	private byte[] roundTrip(byte[] plaintext, int chunkPlaintextSize) throws Exception {
		KeyEncryptionService keyService = newKeyService();
		EncryptionParameters params = new EncryptionParameters(chunkPlaintextSize,
				EncryptedBlobHeader.DEFAULT_KEY_ID_CAPACITY, EncryptedBlobHeader.DEFAULT_WRAPPED_KEY_CAPACITY);

		ByteArrayOutputStream encrypted = new ByteArrayOutputStream();
		new ChunkedEncryptor(keyService, params).encrypt(new ByteArrayInputStream(plaintext), plaintext.length,
				BLOB_ID, encrypted);

		ByteArrayOutputStream decrypted = new ByteArrayOutputStream();
		new ChunkedDecryptor(keyService).decrypt(new ByteArrayInputStream(encrypted.toByteArray()), BLOB_ID,
				decrypted);

		return decrypted.toByteArray();
	}

	private static byte[] randomBytes(Random random, int length) {
		byte[] bytes = new byte[length];
		random.nextBytes(bytes);
		return bytes;
	}

	@Test
	void emptyPlaintext() throws Exception {
		byte[] plaintext = new byte[0];
		assertArrayEquals(plaintext, roundTrip(plaintext, 64));
	}

	@Test
	void oneBytePlaintext() throws Exception {
		byte[] plaintext = { 42 };
		assertArrayEquals(plaintext, roundTrip(plaintext, 64));
	}

	@Test
	void oneByteLessThanChunkSize() throws Exception {
		byte[] plaintext = randomBytes(new Random(1), 63);
		assertArrayEquals(plaintext, roundTrip(plaintext, 64));
	}

	@Test
	void exactChunkSize() throws Exception {
		byte[] plaintext = randomBytes(new Random(2), 64);
		assertArrayEquals(plaintext, roundTrip(plaintext, 64));
	}

	@Test
	void oneByteMoreThanChunkSize() throws Exception {
		byte[] plaintext = randomBytes(new Random(3), 65);
		assertArrayEquals(plaintext, roundTrip(plaintext, 64));
	}

	@Test
	void manyChunks() throws Exception {
		byte[] plaintext = randomBytes(new Random(4), 64 * 1000 + 37);
		assertArrayEquals(plaintext, roundTrip(plaintext, 64));
	}

	@Test
	void largeStreamingFixtureMatchesBySha256() throws Exception {
		byte[] plaintext = randomBytes(new Random(5), 8 * 1024 * 1024);
		byte[] result = roundTrip(plaintext, 64 * 1024);

		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		assertEquals(bytesToHex(digest.digest(plaintext)), bytesToHex(digest.digest(result)));
	}

	private static String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}
}
