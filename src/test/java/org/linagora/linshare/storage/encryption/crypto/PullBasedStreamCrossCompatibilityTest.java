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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.linagora.linshare.storage.encryption.key.KeyEncryptionService;
import org.linagora.linshare.storage.encryption.key.LocalKeyEncryptionService;

/**
 * Proves the new pull-based (lazy {@code InputStream}) encrypt/decrypt path
 * added for the storage decorator produces and consumes exactly the same
 * LSE1 wire bytes as the push-based {@link ChunkedEncryptor#encrypt} /
 * {@link ChunkedDecryptor#decrypt} path from the original format/crypto
 * library — i.e. both are just two ways of driving the same format.
 */
class PullBasedStreamCrossCompatibilityTest {

	private static final byte[] BLOB_ID = "blob-under-test".getBytes(StandardCharsets.UTF_8);

	private KeyEncryptionService newKeyService() {
		byte[] masterKey = new byte[32];
		new SecureRandom().nextBytes(masterKey);
		return new LocalKeyEncryptionService(masterKey, "test-kek");
	}

	@Test
	void pullEncryptThenPushDecryptRoundTrips() throws Exception {
		byte[] plaintext = randomBytes(37 * 100 + 3);
		KeyEncryptionService keyService = newKeyService();
		EncryptionParameters params = new EncryptionParameters(37, 64, 512);
		ChunkedEncryptor encryptor = new ChunkedEncryptor(keyService, params);

		EncryptingBlobContext ctx = encryptor.prepare(plaintext.length);
		byte[] encrypted;
		try (EncryptingInputStream in = new EncryptingInputStream(encryptor, ctx, new ByteArrayInputStream(plaintext),
				BLOB_ID)) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			copy(in, out);
			encrypted = out.toByteArray();
		} finally {
			ctx.close();
		}

		ByteArrayOutputStream decrypted = new ByteArrayOutputStream();
		new ChunkedDecryptor(keyService).decrypt(new ByteArrayInputStream(encrypted), BLOB_ID, decrypted);

		assertArrayEquals(plaintext, decrypted.toByteArray());
	}

	@Test
	void pushEncryptThenPullDecryptRoundTrips() throws Exception {
		byte[] plaintext = randomBytes(29 * 50 + 7);
		KeyEncryptionService keyService = newKeyService();
		EncryptionParameters params = new EncryptionParameters(29, 64, 512);

		ByteArrayOutputStream encrypted = new ByteArrayOutputStream();
		new ChunkedEncryptor(keyService, params).encrypt(new ByteArrayInputStream(plaintext), plaintext.length,
				BLOB_ID, encrypted);

		ChunkedDecryptor decryptor = new ChunkedDecryptor(keyService);
		ByteArrayInputStream encryptedIn = new ByteArrayInputStream(encrypted.toByteArray());
		UnwrappedBlobContext ctx = decryptor.open(encryptedIn, BLOB_ID);
		byte[] result;
		try (DecryptingInputStream in = new DecryptingInputStream(decryptor, ctx, encryptedIn)) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			copy(in, out);
			result = out.toByteArray();
		}

		assertArrayEquals(plaintext, result);
	}

	@Test
	void pullEncryptThenPullDecryptRoundTripsForByteAtATimeReads() throws Exception {
		byte[] plaintext = randomBytes(64 * 3 + 1);
		KeyEncryptionService keyService = newKeyService();
		EncryptionParameters params = new EncryptionParameters(64, 64, 512);
		ChunkedEncryptor encryptor = new ChunkedEncryptor(keyService, params);

		EncryptingBlobContext encCtx = encryptor.prepare(plaintext.length);
		byte[] encrypted;
		try (EncryptingInputStream in = new EncryptingInputStream(encryptor, encCtx,
				new ByteArrayInputStream(plaintext), BLOB_ID)) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int b;
			while ((b = in.read()) != -1) { // exercise the single-byte read() path
				out.write(b);
			}
			encrypted = out.toByteArray();
		} finally {
			encCtx.close();
		}

		ChunkedDecryptor decryptor = new ChunkedDecryptor(keyService);
		ByteArrayInputStream encryptedIn = new ByteArrayInputStream(encrypted);
		UnwrappedBlobContext decCtx = decryptor.open(encryptedIn, BLOB_ID);
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		try (DecryptingInputStream in = new DecryptingInputStream(decryptor, decCtx, encryptedIn)) {
			int b;
			while ((b = in.read()) != -1) { // exercise the single-byte read() path
				result.write(b);
			}
		}

		assertArrayEquals(plaintext, result.toByteArray());
	}

	private static byte[] randomBytes(int length) {
		byte[] bytes = new byte[length];
		new Random(11).nextBytes(bytes);
		return bytes;
	}

	private static void copy(java.io.InputStream in, java.io.OutputStream out) throws java.io.IOException {
		byte[] buffer = new byte[8192];
		int n;
		while ((n = in.read(buffer)) != -1) {
			out.write(buffer, 0, n);
		}
	}
}
