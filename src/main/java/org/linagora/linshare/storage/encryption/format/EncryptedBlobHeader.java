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

import java.nio.charset.StandardCharsets;

import org.linagora.linshare.storage.encryption.exception.EncryptedBlobFormatException;
import org.linagora.linshare.storage.encryption.exception.EncryptedBlobUnsupportedVersionException;

/**
 * Immutable, fully-validated LSE1 header. Constructing an instance is itself
 * the proof that every structural invariant (ARCH.md 7.2, CLAUDE.md 6) holds;
 * callers never need to re-check a header they were handed.
 */
public final class EncryptedBlobHeader {

	public static final byte[] MAGIC = { 'L', 'S', 'E', '1' };

	public static final int FORMAT_VERSION = 1;

	public static final int ALGORITHM_AES_256_GCM = 1;

	public static final int FIXED_HEADER_LENGTH = 48;

	public static final int GCM_TAG_LENGTH_BYTES = 16;

	public static final int GCM_TAG_LENGTH_BITS = GCM_TAG_LENGTH_BYTES * 8;

	public static final int DEK_LENGTH_BYTES = 32;

	public static final int NONCE_PREFIX_LENGTH_BYTES = 8;

	public static final int MAX_CHUNK_PLAINTEXT_SIZE = 64 * 1024 * 1024;

	public static final int DEFAULT_CHUNK_PLAINTEXT_SIZE = 4 * 1024 * 1024;

	public static final int MAX_KEY_ID_CAPACITY = 4096;

	public static final int DEFAULT_KEY_ID_CAPACITY = 64;

	public static final int MAX_WRAPPED_KEY_CAPACITY = 65536;

	public static final int DEFAULT_WRAPPED_KEY_CAPACITY = 512;

	/**
	 * Upper bound on a blob's total header length, for callers that must
	 * fetch the header via a bounded physical byte-range read before they
	 * can parse it and learn its actual (per-blob) reserved capacities.
	 */
	public static final int MAX_HEADER_LENGTH = FIXED_HEADER_LENGTH + MAX_KEY_ID_CAPACITY + MAX_WRAPPED_KEY_CAPACITY;

	private final int formatVersion;

	private final int algorithmId;

	private final int nonceSchemeId;

	private final long plaintextSize;

	private final int chunkPlaintextSize;

	private final long chunkCount;

	private final byte[] noncePrefix;

	private final String keyId;

	private final int reservedKeyIdCapacity;

	private final byte[] wrappedKeyBytes;

	private final int reservedWrappedKeyCapacity;

	public EncryptedBlobHeader(int formatVersion, int algorithmId, int nonceSchemeId, long plaintextSize,
			int chunkPlaintextSize, long chunkCount, byte[] noncePrefix, String keyId, int reservedKeyIdCapacity,
			byte[] wrappedKeyBytes, int reservedWrappedKeyCapacity) {
		if (formatVersion != FORMAT_VERSION) {
			throw new EncryptedBlobUnsupportedVersionException("Unsupported LSE1 format version: " + formatVersion);
		}
		if (algorithmId != ALGORITHM_AES_256_GCM) {
			throw new EncryptedBlobUnsupportedVersionException("Unsupported LSE1 algorithm id: " + algorithmId);
		}
		NonceStrategy nonceStrategy = NonceStrategy.forSchemeId(nonceSchemeId);

		if (plaintextSize < 0) {
			throw new EncryptedBlobFormatException("plaintextSize must not be negative: " + plaintextSize);
		}
		if (chunkPlaintextSize <= 0 || chunkPlaintextSize > MAX_CHUNK_PLAINTEXT_SIZE) {
			throw new EncryptedBlobFormatException("chunkPlaintextSize out of range: " + chunkPlaintextSize);
		}
		long expectedChunkCount = computeChunkCount(plaintextSize, chunkPlaintextSize);
		if (chunkCount != expectedChunkCount) {
			throw new EncryptedBlobFormatException(
					"chunkCount " + chunkCount + " is inconsistent with plaintextSize " + plaintextSize
							+ " and chunkPlaintextSize " + chunkPlaintextSize + " (expected " + expectedChunkCount
							+ ")");
		}
		if (chunkCount - 1 > nonceStrategy.maxChunkIndex()) {
			throw new EncryptedBlobFormatException(
					"chunkCount " + chunkCount + " exceeds the nonce construction's representable range");
		}
		if (noncePrefix == null || noncePrefix.length != NONCE_PREFIX_LENGTH_BYTES) {
			throw new EncryptedBlobFormatException("noncePrefix must be exactly " + NONCE_PREFIX_LENGTH_BYTES
					+ " bytes");
		}
		if (keyId == null || keyId.isEmpty()) {
			throw new EncryptedBlobFormatException("keyId must not be null or empty");
		}
		byte[] keyIdBytes = keyId.getBytes(StandardCharsets.UTF_8);
		if (reservedKeyIdCapacity <= 0 || reservedKeyIdCapacity > MAX_KEY_ID_CAPACITY) {
			throw new EncryptedBlobFormatException("reservedKeyIdCapacity out of range: " + reservedKeyIdCapacity);
		}
		if (keyIdBytes.length > reservedKeyIdCapacity) {
			throw new EncryptedBlobFormatException("keyId length " + keyIdBytes.length
					+ " exceeds reservedKeyIdCapacity " + reservedKeyIdCapacity);
		}
		if (wrappedKeyBytes == null || wrappedKeyBytes.length == 0) {
			throw new EncryptedBlobFormatException("wrappedKeyBytes must not be null or empty");
		}
		if (reservedWrappedKeyCapacity <= 0 || reservedWrappedKeyCapacity > MAX_WRAPPED_KEY_CAPACITY) {
			throw new EncryptedBlobFormatException(
					"reservedWrappedKeyCapacity out of range: " + reservedWrappedKeyCapacity);
		}
		if (wrappedKeyBytes.length > reservedWrappedKeyCapacity) {
			throw new EncryptedBlobFormatException("wrappedKeyBytes length " + wrappedKeyBytes.length
					+ " exceeds reservedWrappedKeyCapacity " + reservedWrappedKeyCapacity);
		}

		this.formatVersion = formatVersion;
		this.algorithmId = algorithmId;
		this.nonceSchemeId = nonceSchemeId;
		this.plaintextSize = plaintextSize;
		this.chunkPlaintextSize = chunkPlaintextSize;
		this.chunkCount = chunkCount;
		this.noncePrefix = noncePrefix.clone();
		this.keyId = keyId;
		this.reservedKeyIdCapacity = reservedKeyIdCapacity;
		this.wrappedKeyBytes = wrappedKeyBytes.clone();
		this.reservedWrappedKeyCapacity = reservedWrappedKeyCapacity;
	}

	/**
	 * Ceil-divides plaintextSize by chunkPlaintextSize, with an empty
	 * plaintext always mapping to exactly one (zero-length) chunk so every
	 * LSE1 blob has at least one authenticated record.
	 */
	public static long computeChunkCount(long plaintextSize, int chunkPlaintextSize) {
		if (plaintextSize == 0) {
			return 1;
		}
		try {
			long numerator = Math.addExact(plaintextSize, (long) chunkPlaintextSize - 1);
			return numerator / chunkPlaintextSize;
		} catch (ArithmeticException e) {
			throw new EncryptedBlobFormatException("plaintextSize too large to compute a chunk count", e);
		}
	}

	public int getFormatVersion() {
		return formatVersion;
	}

	public int getAlgorithmId() {
		return algorithmId;
	}

	public int getNonceSchemeId() {
		return nonceSchemeId;
	}

	public long getPlaintextSize() {
		return plaintextSize;
	}

	public int getChunkPlaintextSize() {
		return chunkPlaintextSize;
	}

	public long getChunkCount() {
		return chunkCount;
	}

	public byte[] getNoncePrefix() {
		return noncePrefix.clone();
	}

	public String getKeyId() {
		return keyId;
	}

	public int getReservedKeyIdCapacity() {
		return reservedKeyIdCapacity;
	}

	public byte[] getWrappedKeyBytes() {
		return wrappedKeyBytes.clone();
	}

	public int getReservedWrappedKeyCapacity() {
		return reservedWrappedKeyCapacity;
	}
}
