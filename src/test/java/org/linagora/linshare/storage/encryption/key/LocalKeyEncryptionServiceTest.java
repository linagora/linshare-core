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
package org.linagora.linshare.storage.encryption.key;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.SecureRandom;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.linagora.linshare.storage.encryption.exception.EncryptedBlobKeyException;
import org.linagora.linshare.storage.encryption.format.EncryptedBlobHeader;

class LocalKeyEncryptionServiceTest {

	private byte[] randomKey(int length) {
		byte[] key = new byte[length];
		new SecureRandom().nextBytes(key);
		return key;
	}

	private byte[] randomDek() {
		return randomKey(EncryptedBlobHeader.DEK_LENGTH_BYTES);
	}

	@Test
	void wrapThenUnwrapReturnsOriginalDek() {
		LocalKeyEncryptionService service = new LocalKeyEncryptionService(randomKey(32), "key-1");
		byte[] dek = randomDek();

		WrappedKey wrapped = service.wrap(dek);
		byte[] unwrapped = service.unwrap(wrapped.getKeyId(), wrapped.getWrappedKeyBytes());

		assertArrayEquals(dek, unwrapped);
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 16, 31, 33, 64 })
	void constructorRejectsWrongMasterKeyLength(int length) {
		assertThrows(EncryptedBlobKeyException.class, () -> new LocalKeyEncryptionService(randomKey(length), "key-1"));
	}

	@Test
	void unwrapRejectsMismatchedKeyId() {
		LocalKeyEncryptionService service = new LocalKeyEncryptionService(randomKey(32), "key-1");
		WrappedKey wrapped = service.wrap(randomDek());

		assertThrows(EncryptedBlobKeyException.class,
				() -> service.unwrap("some-other-key", wrapped.getWrappedKeyBytes()));
	}

	@Test
	void unwrapRejectsFlippedByteInWrappedKeyBytes() {
		LocalKeyEncryptionService service = new LocalKeyEncryptionService(randomKey(32), "key-1");
		WrappedKey wrapped = service.wrap(randomDek());
		byte[] tampered = wrapped.getWrappedKeyBytes();
		tampered[tampered.length - 1] ^= 0x01;

		assertThrows(EncryptedBlobKeyException.class, () -> service.unwrap("key-1", tampered));
	}

	@Test
	void unwrapWithDifferentKekFails() {
		LocalKeyEncryptionService wrapper = new LocalKeyEncryptionService(randomKey(32), "key-1");
		LocalKeyEncryptionService differentKek = new LocalKeyEncryptionService(randomKey(32), "key-1");
		WrappedKey wrapped = wrapper.wrap(randomDek());

		assertThrows(EncryptedBlobKeyException.class,
				() -> differentKek.unwrap("key-1", wrapped.getWrappedKeyBytes()));
	}

	@Test
	void wrapOutputStaysWithinDefaultReservedCapacity() {
		LocalKeyEncryptionService service = new LocalKeyEncryptionService(randomKey(32), "key-1");
		WrappedKey wrapped = service.wrap(randomDek());
		assertTrue(wrapped.getWrappedKeyBytes().length <= EncryptedBlobHeader.DEFAULT_WRAPPED_KEY_CAPACITY);
	}
}
