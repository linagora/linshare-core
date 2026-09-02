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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.linagora.linshare.storage.encryption.exception.EncryptedBlobFormatException;

class EncryptedBlobFormatReaderWriterTest {

	private EncryptedBlobHeader validHeader() {
		long plaintextSize = 4096;
		int chunkPlaintextSize = 1024;
		return new EncryptedBlobHeader(EncryptedBlobHeader.FORMAT_VERSION, EncryptedBlobHeader.ALGORITHM_AES_256_GCM,
				RandomPrefixCounterNonceStrategy.SCHEME_ID, plaintextSize, chunkPlaintextSize,
				EncryptedBlobHeader.computeChunkCount(plaintextSize, chunkPlaintextSize),
				new byte[EncryptedBlobHeader.NONCE_PREFIX_LENGTH_BYTES], "test-key", 64, new byte[48], 512);
	}

	@Test
	void writeThenReadYieldsFieldEqualHeader() throws Exception {
		EncryptedBlobHeader original = validHeader();
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		EncryptedBlobFormat.writeHeader(original, out);
		EncryptedBlobHeader parsed = EncryptedBlobFormat.readHeader(new ByteArrayInputStream(out.toByteArray()));

		assertEquals(original.getFormatVersion(), parsed.getFormatVersion());
		assertEquals(original.getAlgorithmId(), parsed.getAlgorithmId());
		assertEquals(original.getNonceSchemeId(), parsed.getNonceSchemeId());
		assertEquals(original.getPlaintextSize(), parsed.getPlaintextSize());
		assertEquals(original.getChunkPlaintextSize(), parsed.getChunkPlaintextSize());
		assertEquals(original.getChunkCount(), parsed.getChunkCount());
		assertArrayEquals(original.getNoncePrefix(), parsed.getNoncePrefix());
		assertEquals(original.getKeyId(), parsed.getKeyId());
		assertEquals(original.getReservedKeyIdCapacity(), parsed.getReservedKeyIdCapacity());
		assertArrayEquals(original.getWrappedKeyBytes(), parsed.getWrappedKeyBytes());
		assertEquals(original.getReservedWrappedKeyCapacity(), parsed.getReservedWrappedKeyCapacity());
	}

	@Test
	void truncatedHeaderStreamThrowsFormatException() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		EncryptedBlobFormat.writeHeader(validHeader(), out);
		byte[] fullBytes = out.toByteArray();
		byte[] truncated = Arrays.copyOf(fullBytes, fullBytes.length - 10);

		assertThrows(EncryptedBlobFormatException.class,
				() -> EncryptedBlobFormat.readHeader(new ByteArrayInputStream(truncated)));
	}

	@Test
	void truncatedFixedHeaderThrowsFormatException() {
		byte[] tooShort = new byte[EncryptedBlobHeader.FIXED_HEADER_LENGTH - 1];
		assertThrows(EncryptedBlobFormatException.class,
				() -> EncryptedBlobFormat.readHeader(new ByteArrayInputStream(tooShort)));
	}

	@Test
	void badMagicIsRejected() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		EncryptedBlobFormat.writeHeader(validHeader(), out);
		byte[] bytes = out.toByteArray();
		bytes[0] = 'X';

		assertThrows(EncryptedBlobFormatException.class,
				() -> EncryptedBlobFormat.readHeader(new ByteArrayInputStream(bytes)));
	}

	@Test
	void nonZeroReservedFlagsAreRejected() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		EncryptedBlobFormat.writeHeader(validHeader(), out);
		byte[] bytes = out.toByteArray();
		bytes[7] = 1; // flags byte, offset 7

		assertThrows(EncryptedBlobFormatException.class,
				() -> EncryptedBlobFormat.readHeader(new ByteArrayInputStream(bytes)));
	}

	@Test
	void reservedKeyIdCapacityAboveCeilingIsRejectedWithoutLargeAllocation() {
		byte[] fixed = fixedHeaderClaimingCapacities(EncryptedBlobHeader.MAX_KEY_ID_CAPACITY + 1, 512);

		assertThrows(EncryptedBlobFormatException.class,
				() -> EncryptedBlobFormat.readHeader(new ByteArrayInputStream(fixed)));
	}

	@Test
	void reservedWrappedKeyCapacityAboveCeilingIsRejectedWithoutLargeAllocation() {
		byte[] fixed = fixedHeaderClaimingCapacities(64, EncryptedBlobHeader.MAX_WRAPPED_KEY_CAPACITY + 1);

		assertThrows(EncryptedBlobFormatException.class,
				() -> EncryptedBlobFormat.readHeader(new ByteArrayInputStream(fixed)));
	}

	@Test
	void trailingBytesAfterHeaderRemainPositionedForSubsequentReads() throws Exception {
		EncryptedBlobHeader header = validHeader();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		EncryptedBlobFormat.writeHeader(header, out);
		byte[] marker = "FIRST-CHUNK-RECORD".getBytes(StandardCharsets.UTF_8);
		out.write(marker);

		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		EncryptedBlobFormat.readHeader(in);

		byte[] remaining = new byte[marker.length];
		EncryptedBlobFormat.readFully(in, remaining, marker.length);
		assertArrayEquals(marker, remaining);
	}

	/**
	 * Builds only the 48-byte fixed header claiming the given (attacker
	 * controlled) capacities, with no variable-region bytes following it —
	 * proving the ceiling check happens before any allocation/read sized
	 * from those fields.
	 */
	private byte[] fixedHeaderClaimingCapacities(int reservedKeyIdCapacity, int reservedWrappedKeyCapacity) {
		ByteBuffer buffer = ByteBuffer.allocate(EncryptedBlobHeader.FIXED_HEADER_LENGTH);
		buffer.put(EncryptedBlobHeader.magic());
		buffer.put((byte) EncryptedBlobHeader.FORMAT_VERSION);
		buffer.put((byte) EncryptedBlobHeader.ALGORITHM_AES_256_GCM);
		buffer.put((byte) RandomPrefixCounterNonceStrategy.SCHEME_ID);
		buffer.put((byte) 0);
		buffer.putLong(1024L);
		buffer.putInt(1024);
		buffer.putLong(1L);
		buffer.put(new byte[EncryptedBlobHeader.NONCE_PREFIX_LENGTH_BYTES]);
		buffer.putShort((short) 4);
		buffer.putShort((short) reservedKeyIdCapacity);
		buffer.putInt(16);
		buffer.putInt(reservedWrappedKeyCapacity);
		return buffer.array();
	}
}
