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

	private final long firstChunkIndex;

	private final long lastChunkIndexInclusive;

	private byte[] currentChunkPlaintext = new byte[0];

	private int posInChunk;

	private long nextChunkIndex;

	private long remainingToServe;

	public DecryptingInputStream(ChunkedDecryptor decryptor, UnwrappedBlobContext ctx, InputStream encryptedIn) {
		this(decryptor, ctx, encryptedIn, 0, ChunkLayout.of(ctx.getHeader()).chunkCount() - 1, 0,
				ctx.getHeader().getPlaintextSize());
	}

	/**
	 * Serves only the plaintext bytes covered by chunks
	 * {@code [firstChunkIndex, lastChunkIndexInclusive]} read from
	 * {@code encryptedIn} (which must be positioned exactly at
	 * {@code firstChunkIndex}'s physical record, e.g. via a range read),
	 * discarding {@code skipBytesInFirstChunk} leading bytes of the first
	 * decrypted chunk and never serving more than
	 * {@code maxPlaintextBytesToServe} bytes in total — the mechanism behind
	 * both a full download (the no-window constructor above) and an HTTP
	 * Range request onto the same chunked, per-chunk-authenticated format.
	 */
	public DecryptingInputStream(ChunkedDecryptor decryptor, UnwrappedBlobContext ctx, InputStream encryptedIn,
			long firstChunkIndex, long lastChunkIndexInclusive, long skipBytesInFirstChunk,
			long maxPlaintextBytesToServe) {
		if (decryptor == null || ctx == null || encryptedIn == null) {
			throw new IllegalArgumentException("decryptor, ctx and encryptedIn must not be null");
		}
		this.decryptor = decryptor;
		this.ctx = ctx;
		this.encryptedIn = encryptedIn;
		this.layout = ChunkLayout.of(ctx.getHeader());
		this.firstChunkIndex = firstChunkIndex;
		this.lastChunkIndexInclusive = lastChunkIndexInclusive;
		this.nextChunkIndex = firstChunkIndex;
		this.remainingToServe = maxPlaintextBytesToServe;
		// Applied only once, the first time a chunk is decrypted; consumed here
		// as an int-sized "pending skip" so read()'s normal posInChunk bookkeeping
		// handles it uniformly with everything else.
		this.pendingSkipInFirstChunk = skipBytesInFirstChunk;
	}

	private long pendingSkipInFirstChunk;

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
		int toCopy = (int) Math.min(Math.min(available, len), remainingToServe);
		System.arraycopy(currentChunkPlaintext, posInChunk, b, off, toCopy);
		posInChunk += toCopy;
		remainingToServe -= toCopy;
		return toCopy;
	}

	private boolean fillIfNeeded() throws IOException {
		if (remainingToServe <= 0) {
			return false;
		}
		if (posInChunk < currentChunkPlaintext.length) {
			return true;
		}
		if (nextChunkIndex > lastChunkIndexInclusive) {
			return false;
		}
		int ciphertextLength = layout.chunkCiphertextLength(nextChunkIndex);
		byte[] chunkBytes = new byte[ciphertextLength];
		EncryptedBlobFormat.readFully(encryptedIn, chunkBytes, ciphertextLength);
		currentChunkPlaintext = decryptor.decryptChunk(ctx, nextChunkIndex, chunkBytes);
		posInChunk = (nextChunkIndex == firstChunkIndex) ? (int) pendingSkipInFirstChunk : 0;
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
