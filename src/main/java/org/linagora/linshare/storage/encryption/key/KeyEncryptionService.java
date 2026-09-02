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

/**
 * Wraps/unwraps per-blob DEKs under a master KEK. Implementations must fail
 * closed and must never log or otherwise expose raw DEK/KEK bytes.
 */
public interface KeyEncryptionService {

	WrappedKey wrap(byte[] dek);

	byte[] unwrap(String keyId, byte[] wrappedKeyBytes);
}
