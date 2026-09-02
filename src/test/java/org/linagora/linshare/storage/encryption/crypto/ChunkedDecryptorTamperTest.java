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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.linagora.linshare.storage.encryption.exception.EncryptedBlobAuthenticationException;
import org.linagora.linshare.storage.encryption.exception.EncryptedBlobFormatException;
import org.linagora.linshare.storage.encryption.exception.EncryptedBlobKeyException;
import org.linagora.linshare.storage.encryption.format.EncryptedBlobHeader;
import org.linagora.linshare.storage.encryption.key.KeyEncryptionService;
import org.linagora.linshare.storage.encryption.key.LocalKeyEncryptionService;
import org.linagora.linshare.storage.encryption.key.WrappedKey;

/**
 * Required LSE1 tamper tests (CLAUDE.md 25): a decrypt/authentication
 * failure must never be reinterpreted as a smaller-but-valid or plaintext
 * result.
 */
class ChunkedDecryptorTamperTest {

	// Wire offsets of the fixed 48-byte header, mirroring EncryptedBlobFormat's layout.
	private static final int OFFSET_PLAINTEXT_SIZE = 8;

	private static final int OFFSET_CHUNK_PLAINTEXT_SIZE = 16;

	private static final int OFFSET_CHUNK_COUNT = 20;

	private static final int OFFSET_WRAPPED_KEY_LENGTH = 40;

	private static final int CHUNK_SIZE = 32;

	private static final int KEY_ID_CAPACITY = EncryptedBlobHeader.DEFAULT_KEY_ID_CAPACITY;

	private static final int WRAPPED_KEY_CAPACITY = EncryptedBlobHeader.DEFAULT_WRAPPED_KEY_CAPACITY;

	private static final int HEADER_TOTAL_LENGTH = EncryptedBlobHeader.FIXED_HEADER_LENGTH + KEY_ID_CAPACITY
			+ WRAPPED_KEY_CAPACITY;

	private static final int FULL_CHUNK_RECORD_SIZE = CHUNK_SIZE + EncryptedBlobHeader.GCM_TAG_LENGTH_BYTES;

	private static final byte[] BLOB_ID = "blob-under-test".getBytes(StandardCharsets.UTF_8);

	private byte[] randomMasterKey() {
		byte[] key = new byte[32];
		new SecureRandom().nextBytes(key);
		return key;
	}

	private byte[] encryptFourFullChunks(KeyEncryptionService keyService) throws Exception {
		byte[] plaintext = new byte[CHUNK_SIZE * 4];
		new Random(7).nextBytes(plaintext);
		EncryptionParameters params = new EncryptionParameters(CHUNK_SIZE, KEY_ID_CAPACITY, WRAPPED_KEY_CAPACITY);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new ChunkedEncryptor(keyService, params).encrypt(new ByteArrayInputStream(plaintext), plaintext.length,
				BLOB_ID, out);
		return out.toByteArray();
	}

	private void assertDecryptFails(byte[] encrypted, KeyEncryptionService keyService, Class<? extends Exception> expected) {
		assertThrows(expected, () -> new ChunkedDecryptor(keyService).decrypt(new ByteArrayInputStream(encrypted),
				BLOB_ID, new ByteArrayOutputStream()));
	}

	private long chunkRecordOffset(int chunkIndex) {
		return HEADER_TOTAL_LENGTH + (long) chunkIndex * FULL_CHUNK_RECORD_SIZE;
	}

	@Test
	void flipCiphertextByte_failsAuthentication() throws Exception {
		KeyEncryptionService keyService = new LocalKeyEncryptionService(randomMasterKey(), "key-1");
		byte[] encrypted = encryptFourFullChunks(keyService);
		int firstCiphertextByte = (int) chunkRecordOffset(0);
		encrypted[firstCiphertextByte] ^= 0x01;

		assertDecryptFails(encrypted, keyService, EncryptedBlobAuthenticationException.class);
	}

	@Test
	void flipTagByte_failsAuthentication() throws Exception {
		KeyEncryptionService keyService = new LocalKeyEncryptionService(randomMasterKey(), "key-1");
		byte[] encrypted = encryptFourFullChunks(keyService);
		int lastTagByte = (int) chunkRecordOffset(1) - 1;
		encrypted[lastTagByte] ^= 0x01;

		assertDecryptFails(encrypted, keyService, EncryptedBlobAuthenticationException.class);
	}

	@Test
	void changeChunkIndexUsedForAad_failsAuthentication() throws Exception {
		KeyEncryptionService keyService = new LocalKeyEncryptionService(randomMasterKey(), "key-1");
		byte[] encrypted = encryptFourFullChunks(keyService);

		byte[] chunk0Record = Arrays.copyOfRange(encrypted, (int) chunkRecordOffset(0), (int) chunkRecordOffset(1));

		try (UnwrappedBlobContext ctx = new ChunkedDecryptor(keyService)
				.open(new ByteArrayInputStream(encrypted), BLOB_ID)) {
			ChunkedDecryptor decryptor = new ChunkedDecryptor(keyService);
			// chunk0's own bytes decrypt fine at index 0...
			decryptor.decryptChunk(ctx, 0, chunk0Record);
			// ...but fail when the same bytes are authenticated as if they were chunk 1's.
			assertThrows(EncryptedBlobAuthenticationException.class, () -> decryptor.decryptChunk(ctx, 1, chunk0Record));
		}
	}

	@Test
	void swapTwoChunks_failsAuthentication() throws Exception {
		KeyEncryptionService keyService = new LocalKeyEncryptionService(randomMasterKey(), "key-1");
		byte[] encrypted = encryptFourFullChunks(keyService);

		int start0 = (int) chunkRecordOffset(0);
		int start1 = (int) chunkRecordOffset(1);
		int start2 = (int) chunkRecordOffset(2);
		byte[] chunk0 = Arrays.copyOfRange(encrypted, start0, start1);
		byte[] chunk1 = Arrays.copyOfRange(encrypted, start1, start2);
		System.arraycopy(chunk1, 0, encrypted, start0, chunk1.length);
		System.arraycopy(chunk0, 0, encrypted, start1, chunk0.length);

		assertDecryptFails(encrypted, keyService, EncryptedBlobAuthenticationException.class);
	}

	@Test
	void replayChunkAtDifferentIndex_failsAuthentication() throws Exception {
		KeyEncryptionService keyService = new LocalKeyEncryptionService(randomMasterKey(), "key-1");
		byte[] encrypted = encryptFourFullChunks(keyService);

		int start0 = (int) chunkRecordOffset(0);
		int start1 = (int) chunkRecordOffset(1);
		byte[] chunk0 = Arrays.copyOfRange(encrypted, start0, start1);
		System.arraycopy(chunk0, 0, encrypted, start1, chunk0.length);

		assertDecryptFails(encrypted, keyService, EncryptedBlobAuthenticationException.class);
	}

	@Test
	void truncateFinalChunk_rejectedAsFormatError() throws Exception {
		KeyEncryptionService keyService = new LocalKeyEncryptionService(randomMasterKey(), "key-1");
		byte[] encrypted = encryptFourFullChunks(keyService);
		byte[] truncated = Arrays.copyOf(encrypted, encrypted.length - 5);

		assertDecryptFails(truncated, keyService, EncryptedBlobFormatException.class);
	}

	@Test
	void modifyPlaintextSizeInconsistentWithChunkCount_rejectedAtParse() throws Exception {
		KeyEncryptionService keyService = new LocalKeyEncryptionService(randomMasterKey(), "key-1");
		byte[] encrypted = encryptFourFullChunks(keyService);
		// Push plaintextSize into a bracket that would require 5 chunks while
		// leaving the on-disk chunkCount (4) untouched.
		putLong(encrypted, OFFSET_PLAINTEXT_SIZE, CHUNK_SIZE * 4L + 1);

		assertDecryptFails(encrypted, keyService, EncryptedBlobFormatException.class);
	}

	@Test
	void modifyPlaintextSizeAndChunkCountConsistently_failsAuthentication() throws Exception {
		KeyEncryptionService keyService = new LocalKeyEncryptionService(randomMasterKey(), "key-1");
		byte[] encrypted = encryptFourFullChunks(keyService);
		// A smaller plaintextSize that still recomputes to the same chunkCount (4)
		// changes the AAD without touching the on-disk chunk layout.
		putLong(encrypted, OFFSET_PLAINTEXT_SIZE, CHUNK_SIZE * 3L + 1);

		assertDecryptFails(encrypted, keyService, EncryptedBlobAuthenticationException.class);
	}

	@Test
	void modifyChunkSizeInconsistentWithChunkCount_rejectedAtParse() throws Exception {
		KeyEncryptionService keyService = new LocalKeyEncryptionService(randomMasterKey(), "key-1");
		byte[] encrypted = encryptFourFullChunks(keyService);
		// Doubling chunkPlaintextSize halves the expected chunk count, which no
		// longer matches the on-disk chunkCount (4).
		putInt(encrypted, OFFSET_CHUNK_PLAINTEXT_SIZE, CHUNK_SIZE * 2);

		assertDecryptFails(encrypted, keyService, EncryptedBlobFormatException.class);
	}

	@Test
	void modifyChunkSizeAndChunkCountConsistently_failsAuthentication() throws Exception {
		KeyEncryptionService keyService = new LocalKeyEncryptionService(randomMasterKey(), "key-1");
		byte[] encrypted = encryptFourFullChunks(keyService);
		// plaintextSize=128 still yields chunkCount=4 with chunkPlaintextSize=32
		// (original) or with a smaller size such as 20: ceil(128/20)=7... choose a
		// value that keeps chunkCount identical: chunkPlaintextSize=33 -> ceil(128/33)=4.
		putInt(encrypted, OFFSET_CHUNK_PLAINTEXT_SIZE, CHUNK_SIZE + 1);
		putLong(encrypted, OFFSET_CHUNK_COUNT, 4L);

		assertDecryptFails(encrypted, keyService, EncryptedBlobAuthenticationException.class);
	}

	@Test
	void wrongWrappedDek_failsAuthentication() throws Exception {
		KeyEncryptionService keyService = new LocalKeyEncryptionService(randomMasterKey(), "key-1");
		byte[] encrypted = encryptFourFullChunks(keyService);

		byte[] otherDek = new byte[EncryptedBlobHeader.DEK_LENGTH_BYTES];
		new SecureRandom().nextBytes(otherDek);
		WrappedKey otherWrapped = keyService.wrap(otherDek);
		byte[] wrappedBytes = otherWrapped.getWrappedKeyBytes();

		putInt(encrypted, OFFSET_WRAPPED_KEY_LENGTH, wrappedBytes.length);
		int wrappedKeyRegionStart = EncryptedBlobHeader.FIXED_HEADER_LENGTH + KEY_ID_CAPACITY;
		System.arraycopy(wrappedBytes, 0, encrypted, wrappedKeyRegionStart, wrappedBytes.length);

		assertDecryptFails(encrypted, keyService, EncryptedBlobAuthenticationException.class);
	}

	@Test
	void wrongKek_failsAtUnwrap() throws Exception {
		KeyEncryptionService encryptingKeyService = new LocalKeyEncryptionService(randomMasterKey(), "key-1");
		byte[] encrypted = encryptFourFullChunks(encryptingKeyService);

		KeyEncryptionService differentKek = new LocalKeyEncryptionService(randomMasterKey(), "key-1");
		assertDecryptFails(encrypted, differentKek, EncryptedBlobKeyException.class);
	}

	private static void putLong(byte[] bytes, int offset, long value) {
		ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).putLong(offset, value);
	}

	private static void putInt(byte[] bytes, int offset, int value) {
		ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).putInt(offset, value);
	}
}
