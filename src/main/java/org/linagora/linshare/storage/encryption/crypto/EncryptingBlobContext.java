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
 * Holds a freshly generated DEK and its header across the encryption of many
 * chunks — and, for callers that must reopen the same logical plaintext more
 * than once (e.g. multipart upload slicing), across more than one encryption
 * pass, since the header (including the random nonce prefix) must stay
 * identical for the produced bytes to be deterministic. {@link #close()}
 * zeroes the DEK bytes; Java offers no stronger memory-erasure guarantee than
 * that.
 */
public final class EncryptingBlobContext implements AutoCloseable {

	private final EncryptedBlobHeader header;

	private final byte[] dek;

	private boolean closed;

	EncryptingBlobContext(EncryptedBlobHeader header, byte[] dek) {
		this.header = header;
		this.dek = dek;
	}

	public EncryptedBlobHeader getHeader() {
		ensureOpen();
		return header;
	}

	byte[] getDek() {
		ensureOpen();
		return dek;
	}

	private void ensureOpen() {
		if (closed) {
			throw new IllegalStateException("EncryptingBlobContext is already closed");
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
