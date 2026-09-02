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

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import org.linagora.linshare.storage.encryption.format.ChunkLayout;
import org.linagora.linshare.storage.encryption.format.EncryptedBlobFormat;

/**
 * Pull-based counterpart to {@link ChunkedEncryptor#encrypt}: produces the
 * complete LSE1 byte stream (header, then each chunk's ciphertext+tag) as
 * bytes are read from it, rather than requiring a target
 * {@code OutputStream}. This is what lets an encrypted blob be exposed as a
 * lazily-openable source (e.g. a {@code com.google.common.io.ByteSource})
 * to APIs that pull from a stream instead of being pushed into one.
 *
 * <p>Bounded to one chunk-sized buffer at a time; never buffers the whole
 * plaintext or ciphertext. Does not close or zero {@code ctx} — the caller
 * owns that context's lifecycle, since it may be reused across more than one
 * {@code InputStream} instance reading the same logical plaintext (e.g.
 * multipart upload slicing re-opening the source per part).
 */
public final class EncryptingInputStream extends InputStream {

	private final ChunkedEncryptor encryptor;

	private final EncryptingBlobContext ctx;

	private final InputStream plaintextIn;

	private final byte[] blobId;

	private final ChunkLayout layout;

	private byte[] currentBuffer;

	private int posInBuffer;

	private long nextChunkIndex;

	public EncryptingInputStream(ChunkedEncryptor encryptor, EncryptingBlobContext ctx, InputStream plaintextIn,
			byte[] blobId) throws IOException {
		if (encryptor == null || ctx == null || plaintextIn == null || blobId == null) {
			throw new IllegalArgumentException("encryptor, ctx, plaintextIn and blobId must not be null");
		}
		this.encryptor = encryptor;
		this.ctx = ctx;
		this.plaintextIn = plaintextIn;
		this.blobId = blobId;
		this.layout = ChunkLayout.of(ctx.getHeader());

		ByteArrayOutputStream headerBytes = new ByteArrayOutputStream((int) layout.headerTotalLength());
		EncryptedBlobFormat.writeHeader(ctx.getHeader(), headerBytes);
		this.currentBuffer = headerBytes.toByteArray();
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
		int available = currentBuffer.length - posInBuffer;
		int toCopy = Math.min(available, len);
		System.arraycopy(currentBuffer, posInBuffer, b, off, toCopy);
		posInBuffer += toCopy;
		return toCopy;
	}

	private boolean fillIfNeeded() throws IOException {
		if (currentBuffer != null && posInBuffer < currentBuffer.length) {
			return true;
		}
		if (nextChunkIndex >= layout.chunkCount()) {
			return false;
		}
		int chunkPlaintextLength = layout.chunkPlaintextLength(nextChunkIndex);
		byte[] plaintextChunk = new byte[chunkPlaintextLength];
		readFullyStrict(plaintextIn, plaintextChunk);
		currentBuffer = encryptor.encryptChunk(ctx, nextChunkIndex, plaintextChunk, blobId);
		posInBuffer = 0;
		nextChunkIndex++;
		return true;
	}

	private static void readFullyStrict(InputStream in, byte[] buffer) throws IOException {
		int totalRead = 0;
		while (totalRead < buffer.length) {
			int read = in.read(buffer, totalRead, buffer.length - totalRead);
			if (read == -1) {
				throw new EOFException("Plaintext stream ended before the declared plaintextSize was fully read");
			}
			totalRead += read;
		}
	}
}
