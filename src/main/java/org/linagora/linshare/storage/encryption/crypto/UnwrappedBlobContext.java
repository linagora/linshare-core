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

import org.linagora.linshare.storage.encryption.format.EncryptedBlobHeader;

/**
 * Holds a header and its once-unwrapped DEK across the decryption of many
 * chunks in one request, so the DEK is unwrapped once per file/request
 * rather than once per chunk (ARCH.md 15, 24). {@link #close()} zeroes the
 * DEK bytes; Java offers no stronger memory-erasure guarantee than that.
 */
public final class UnwrappedBlobContext extends AbstractBlobContext {

	private final byte[] blobId;

	UnwrappedBlobContext(EncryptedBlobHeader header, byte[] blobId, byte[] dek) {
		super(header, dek);
		this.blobId = blobId.clone();
	}

	public byte[] getBlobId() {
		ensureOpen();
		return blobId.clone();
	}
}
