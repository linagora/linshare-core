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
package org.linagora.linshare.storage.encryption.format;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.linagora.linshare.storage.encryption.exception.EncryptedBlobFormatException;

/**
 * Reads and writes the LSE1 binary layout. All multi-byte integers are
 * unsigned, big-endian. No Java serialization is used anywhere in this
 * format.
 */
public final class EncryptedBlobFormat {

	private EncryptedBlobFormat() {
	}

	public static EncryptedBlobHeader readHeader(InputStream in) throws IOException {
		byte[] fixed = new byte[EncryptedBlobHeader.FIXED_HEADER_LENGTH];
		readFully(in, fixed, fixed.length);
		ByteBuffer buffer = ByteBuffer.wrap(fixed);

		byte[] magic = new byte[EncryptedBlobHeader.MAGIC.length];
		buffer.get(magic);
		if (!Arrays.equals(magic, EncryptedBlobHeader.MAGIC)) {
			throw new EncryptedBlobFormatException("Bad magic: not an LSE1 blob");
		}
		int formatVersion = buffer.get() & 0xFF;
		int algorithmId = buffer.get() & 0xFF;
		int nonceSchemeId = buffer.get() & 0xFF;
		int flags = buffer.get() & 0xFF;
		if (flags != 0) {
			throw new EncryptedBlobFormatException("Reserved header flags must be zero, was " + flags);
		}
		long plaintextSize = buffer.getLong();
		int chunkPlaintextSize = buffer.getInt();
		long chunkCount = buffer.getLong();
		byte[] noncePrefix = new byte[EncryptedBlobHeader.NONCE_PREFIX_LENGTH_BYTES];
		buffer.get(noncePrefix);
		int keyIdLength = buffer.getShort() & 0xFFFF;
		int reservedKeyIdCapacity = buffer.getShort() & 0xFFFF;
		int wrappedKeyLength = buffer.getInt();
		int reservedWrappedKeyCapacity = buffer.getInt();

		// Ceiling checks MUST happen before any allocation sized from these
		// attacker-controlled fields.
		if (reservedKeyIdCapacity < 0 || reservedKeyIdCapacity > EncryptedBlobHeader.MAX_KEY_ID_CAPACITY) {
			throw new EncryptedBlobFormatException("reservedKeyIdCapacity exceeds allowed maximum: "
					+ reservedKeyIdCapacity);
		}
		if (reservedWrappedKeyCapacity < 0
				|| reservedWrappedKeyCapacity > EncryptedBlobHeader.MAX_WRAPPED_KEY_CAPACITY) {
			throw new EncryptedBlobFormatException("reservedWrappedKeyCapacity exceeds allowed maximum: "
					+ reservedWrappedKeyCapacity);
		}
		if (keyIdLength > reservedKeyIdCapacity) {
			throw new EncryptedBlobFormatException(
					"keyIdLength " + keyIdLength + " exceeds reservedKeyIdCapacity " + reservedKeyIdCapacity);
		}
		if (wrappedKeyLength > reservedWrappedKeyCapacity) {
			throw new EncryptedBlobFormatException("wrappedKeyLength " + wrappedKeyLength
					+ " exceeds reservedWrappedKeyCapacity " + reservedWrappedKeyCapacity);
		}

		byte[] keyIdRegion = new byte[reservedKeyIdCapacity];
		readFully(in, keyIdRegion, reservedKeyIdCapacity);
		String keyId = new String(keyIdRegion, 0, keyIdLength, StandardCharsets.UTF_8);

		byte[] wrappedKeyRegion = new byte[reservedWrappedKeyCapacity];
		readFully(in, wrappedKeyRegion, reservedWrappedKeyCapacity);
		byte[] wrappedKeyBytes = Arrays.copyOfRange(wrappedKeyRegion, 0, wrappedKeyLength);

		return new EncryptedBlobHeader(formatVersion, algorithmId, nonceSchemeId, plaintextSize, chunkPlaintextSize,
				chunkCount, noncePrefix, keyId, reservedKeyIdCapacity, wrappedKeyBytes, reservedWrappedKeyCapacity);
	}

	public static void writeHeader(EncryptedBlobHeader header, OutputStream out) throws IOException {
		byte[] keyIdBytes = header.getKeyId().getBytes(StandardCharsets.UTF_8);
		byte[] wrappedKeyBytes = header.getWrappedKeyBytes();

		ByteBuffer buffer = ByteBuffer.allocate(EncryptedBlobHeader.FIXED_HEADER_LENGTH);
		buffer.put(EncryptedBlobHeader.MAGIC);
		buffer.put((byte) header.getFormatVersion());
		buffer.put((byte) header.getAlgorithmId());
		buffer.put((byte) header.getNonceSchemeId());
		buffer.put((byte) 0); // reserved flags
		buffer.putLong(header.getPlaintextSize());
		buffer.putInt(header.getChunkPlaintextSize());
		buffer.putLong(header.getChunkCount());
		buffer.put(header.getNoncePrefix());
		buffer.putShort((short) keyIdBytes.length);
		buffer.putShort((short) header.getReservedKeyIdCapacity());
		buffer.putInt(wrappedKeyBytes.length);
		buffer.putInt(header.getReservedWrappedKeyCapacity());
		out.write(buffer.array());

		byte[] keyIdRegion = new byte[header.getReservedKeyIdCapacity()];
		System.arraycopy(keyIdBytes, 0, keyIdRegion, 0, keyIdBytes.length);
		out.write(keyIdRegion);

		byte[] wrappedKeyRegion = new byte[header.getReservedWrappedKeyCapacity()];
		System.arraycopy(wrappedKeyBytes, 0, wrappedKeyRegion, 0, wrappedKeyBytes.length);
		out.write(wrappedKeyRegion);
	}

	/**
	 * Reads exactly {@code length} bytes, treating an early end of stream as
	 * blob corruption rather than a plain I/O EOF, since every caller of this
	 * method already knows the exact record length from an authenticated or
	 * structurally-validated header.
	 */
	public static void readFully(InputStream in, byte[] buffer, int length) throws IOException {
		int totalRead = 0;
		while (totalRead < length) {
			int read = in.read(buffer, totalRead, length - totalRead);
			if (read == -1) {
				throw new EncryptedBlobFormatException("Unexpected end of stream: truncated LSE1 blob");
			}
			totalRead += read;
		}
	}
}
