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

import java.util.Arrays;

import org.linagora.linshare.storage.encryption.format.EncryptedBlobHeader;

/**
 * Holds a header and its once-unwrapped DEK across the decryption of many
 * chunks in one request, so the DEK is unwrapped once per file/request
 * rather than once per chunk (ARCH.md 15, 24). {@link #close()} zeroes the
 * DEK bytes; Java offers no stronger memory-erasure guarantee than that.
 */
public final class UnwrappedBlobContext implements AutoCloseable {

	private final EncryptedBlobHeader header;

	private final byte[] blobId;

	private final byte[] dek;

	private boolean closed;

	UnwrappedBlobContext(EncryptedBlobHeader header, byte[] blobId, byte[] dek) {
		this.header = header;
		this.blobId = blobId.clone();
		this.dek = dek;
	}

	public EncryptedBlobHeader getHeader() {
		ensureOpen();
		return header;
	}

	public byte[] getBlobId() {
		ensureOpen();
		return blobId.clone();
	}

	byte[] getDek() {
		ensureOpen();
		return dek;
	}

	private void ensureOpen() {
		if (closed) {
			throw new IllegalStateException("UnwrappedBlobContext is already closed");
		}
	}

	@Override
	public void close() {
		if (!closed) {
			Arrays.fill(dek, (byte) 0);
			closed = true;
		}
	}
}
