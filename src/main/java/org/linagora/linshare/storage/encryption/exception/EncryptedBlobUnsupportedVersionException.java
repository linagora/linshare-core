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
package org.linagora.linshare.storage.encryption.exception;

/**
 * Raised when a header declares a format version, algorithm id or nonce
 * scheme id this implementation does not know how to interpret.
 */
public class EncryptedBlobUnsupportedVersionException extends EncryptedBlobException {

	private static final long serialVersionUID = 1L;

	public EncryptedBlobUnsupportedVersionException(String message) {
		super(message);
	}

	public EncryptedBlobUnsupportedVersionException(String message, Throwable cause) {
		super(message, cause);
	}
}
