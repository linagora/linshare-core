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

import org.junit.jupiter.api.Test;

class WrappedKeyTest {

	@Test
	void mutatingConstructorArrayDoesNotAffectStoredState() {
		byte[] source = { 1, 2, 3 };
		WrappedKey key = new WrappedKey("key-1", source, "ALG");
		source[0] = 99;
		assertArrayEquals(new byte[] { 1, 2, 3 }, key.getWrappedKeyBytes());
	}

	@Test
	void mutatingReturnedArrayDoesNotAffectStoredState() {
		WrappedKey key = new WrappedKey("key-1", new byte[] { 1, 2, 3 }, "ALG");
		byte[] returned = key.getWrappedKeyBytes();
		returned[0] = 99;
		assertArrayEquals(new byte[] { 1, 2, 3 }, key.getWrappedKeyBytes());
	}

	@Test
	void rejectsEmptyKeyId() {
		assertThrows(IllegalArgumentException.class, () -> new WrappedKey("", new byte[] { 1 }, "ALG"));
	}

	@Test
	void rejectsEmptyWrappedKeyBytes() {
		assertThrows(IllegalArgumentException.class, () -> new WrappedKey("key-1", new byte[0], "ALG"));
	}
}
