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

import java.io.IOException;
import java.io.InputStream;

import org.linagora.linshare.storage.encryption.format.ChunkLayout;
import org.linagora.linshare.storage.encryption.format.EncryptedBlobFormat;

/**
 * Pull-based counterpart to {@link ChunkedDecryptor#decrypt}: serves
 * authenticated plaintext bytes as they are read, built entirely on top of
 * the existing {@link ChunkedDecryptor#open} / {@link ChunkedDecryptor#decryptChunk}
 * primitives — no chunk's plaintext is ever released before that chunk's GCM
 * tag has verified. Bounded to one chunk-sized buffer at a time.
 *
 * <p>Owns {@code ctx} exclusively: {@link #close()} closes it (zeroing the
 * DEK) along with the underlying encrypted stream.
 */
public final class DecryptingInputStream extends InputStream {

	private final ChunkedDecryptor decryptor;

	private final UnwrappedBlobContext ctx;

	private final InputStream encryptedIn;

	private final ChunkLayout layout;

	private byte[] currentChunkPlaintext = new byte[0];

	private int posInChunk;

	private long nextChunkIndex;

	public DecryptingInputStream(ChunkedDecryptor decryptor, UnwrappedBlobContext ctx, InputStream encryptedIn) {
		if (decryptor == null || ctx == null || encryptedIn == null) {
			throw new IllegalArgumentException("decryptor, ctx and encryptedIn must not be null");
		}
		this.decryptor = decryptor;
		this.ctx = ctx;
		this.encryptedIn = encryptedIn;
		this.layout = ChunkLayout.of(ctx.getHeader());
	}

	@Override
	public int read() throws IOException {
		byte[] one = new byte[1];
		int n = read(one, 0, 1);
		return n == -1 ? -1 : (one[0] & 0xFF);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (len == 0) {
			return 0;
		}
		if (!fillIfNeeded()) {
			return -1;
		}
		int available = currentChunkPlaintext.length - posInChunk;
		int toCopy = Math.min(available, len);
		System.arraycopy(currentChunkPlaintext, posInChunk, b, off, toCopy);
		posInChunk += toCopy;
		return toCopy;
	}

	private boolean fillIfNeeded() throws IOException {
		if (posInChunk < currentChunkPlaintext.length) {
			return true;
		}
		if (nextChunkIndex >= layout.chunkCount()) {
			return false;
		}
		int ciphertextLength = layout.chunkCiphertextLength(nextChunkIndex);
		byte[] chunkBytes = new byte[ciphertextLength];
		EncryptedBlobFormat.readFully(encryptedIn, chunkBytes, ciphertextLength);
		currentChunkPlaintext = decryptor.decryptChunk(ctx, nextChunkIndex, chunkBytes);
		posInChunk = 0;
		nextChunkIndex++;
		return true;
	}

	@Override
	public void close() throws IOException {
		try {
			ctx.close();
		} finally {
			encryptedIn.close();
		}
	}
}
