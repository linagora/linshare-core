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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.linagora.linshare.storage.encryption.exception.EncryptedBlobFormatException;
import org.linagora.linshare.storage.encryption.exception.EncryptedBlobKeyException;
import org.linagora.linshare.storage.encryption.format.ChunkAadFactory;
import org.linagora.linshare.storage.encryption.format.ChunkLayout;
import org.linagora.linshare.storage.encryption.format.EncryptedBlobFormat;
import org.linagora.linshare.storage.encryption.format.EncryptedBlobHeader;
import org.linagora.linshare.storage.encryption.format.NonceStrategy;
import org.linagora.linshare.storage.encryption.format.RandomPrefixCounterNonceStrategy;
import org.linagora.linshare.storage.encryption.key.KeyEncryptionService;
import org.linagora.linshare.storage.encryption.key.WrappedKey;

/**
 * Streams a plaintext of a declared size into a complete LSE1 blob, one
 * chunk-sized buffer at a time. Never buffers the whole plaintext or
 * ciphertext (CLAUDE.md 5, 13).
 */
public final class ChunkedEncryptor {

	private final KeyEncryptionService keyEncryptionService;

	private final EncryptionParameters params;

	private final SecureRandom secureRandom = new SecureRandom();

	public ChunkedEncryptor(KeyEncryptionService keyEncryptionService, EncryptionParameters params) {
		if (keyEncryptionService == null) {
			throw new IllegalArgumentException("keyEncryptionService must not be null");
		}
		if (params == null) {
			throw new IllegalArgumentException("params must not be null");
		}
		this.keyEncryptionService = keyEncryptionService;
		this.params = params;
	}

	/**
	 * @param blobId stable identifier bound into every chunk's AAD; the
	 *               decryptor must be given the identical bytes or every
	 *               chunk will fail authentication. Not stored in the LSE1
	 *               bytes.
	 */
	public EncryptedBlobHeader encrypt(InputStream plaintextIn, long plaintextSize, byte[] blobId,
			OutputStream encryptedOut) throws IOException {
		if (plaintextSize < 0) {
			throw new IllegalArgumentException("plaintextSize must not be negative: " + plaintextSize);
		}
		if (blobId == null) {
			throw new IllegalArgumentException("blobId is required and must not be null");
		}

		NonceStrategy nonceStrategy = new RandomPrefixCounterNonceStrategy();
		long chunkCount = EncryptedBlobHeader.computeChunkCount(plaintextSize, params.getChunkPlaintextSize());
		if (chunkCount - 1 > nonceStrategy.maxChunkIndex()) {
			throw new EncryptedBlobFormatException(
					"plaintextSize " + plaintextSize + " requires more chunks than the nonce construction allows");
		}

		byte[] dek = new byte[EncryptedBlobHeader.DEK_LENGTH_BYTES];
		secureRandom.nextBytes(dek);
		try {
			WrappedKey wrappedKey = keyEncryptionService.wrap(dek);
			if (wrappedKey.getWrappedKeyBytes().length > params.getReservedWrappedKeyCapacity()) {
				throw new EncryptedBlobKeyException("wrapped key length exceeds the reserved wrapped-key capacity");
			}

			byte[] noncePrefix = new byte[EncryptedBlobHeader.NONCE_PREFIX_LENGTH_BYTES];
			secureRandom.nextBytes(noncePrefix);

			EncryptedBlobHeader header = new EncryptedBlobHeader(EncryptedBlobHeader.FORMAT_VERSION,
					EncryptedBlobHeader.ALGORITHM_AES_256_GCM, nonceStrategy.schemeId(), plaintextSize,
					params.getChunkPlaintextSize(), chunkCount, noncePrefix, wrappedKey.getKeyId(),
					params.getReservedKeyIdCapacity(), wrappedKey.getWrappedKeyBytes(),
					params.getReservedWrappedKeyCapacity());

			EncryptedBlobFormat.writeHeader(header, encryptedOut);

			ChunkLayout layout = ChunkLayout.of(header);
			SecretKeySpec keySpec = new SecretKeySpec(dek, "AES");
			byte[] buffer = new byte[params.getChunkPlaintextSize()];

			for (long chunkIndex = 0; chunkIndex < chunkCount; chunkIndex++) {
				int chunkLength = layout.chunkPlaintextLength(chunkIndex);
				readFullyStrict(plaintextIn, buffer, chunkLength);

				byte[] nonce = nonceStrategy.deriveNonce(noncePrefix, chunkIndex);
				byte[] aad = ChunkAadFactory.build(header, blobId, chunkIndex);

				Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
				cipher.init(Cipher.ENCRYPT_MODE, keySpec,
						new GCMParameterSpec(EncryptedBlobHeader.GCM_TAG_LENGTH_BITS, nonce));
				cipher.updateAAD(aad);
				byte[] ciphertextAndTag = cipher.doFinal(buffer, 0, chunkLength);
				encryptedOut.write(ciphertextAndTag);
			}

			return header;
		} catch (GeneralSecurityException e) {
			throw new EncryptedBlobKeyException("Failed to encrypt blob", e);
		} finally {
			Arrays.fill(dek, (byte) 0);
		}
	}

	/**
	 * Generates a DEK, wraps it, and builds the header — with no I/O. Lets a
	 * caller learn the exact physical (ciphertext) length up front via
	 * {@link ChunkLayout#totalPhysicalLength()} before streaming a single
	 * byte, and lets it drive its own per-chunk encryption via
	 * {@link #encryptChunk}, e.g. from a lazily-pulled {@code InputStream}
	 * that a push-style {@link OutputStream} target such as {@link #encrypt}
	 * cannot serve.
	 */
	public EncryptingBlobContext prepare(long plaintextSize) {
		if (plaintextSize < 0) {
			throw new IllegalArgumentException("plaintextSize must not be negative: " + plaintextSize);
		}
		NonceStrategy nonceStrategy = new RandomPrefixCounterNonceStrategy();
		long chunkCount = EncryptedBlobHeader.computeChunkCount(plaintextSize, params.getChunkPlaintextSize());
		if (chunkCount - 1 > nonceStrategy.maxChunkIndex()) {
			throw new EncryptedBlobFormatException(
					"plaintextSize " + plaintextSize + " requires more chunks than the nonce construction allows");
		}

		byte[] dek = new byte[EncryptedBlobHeader.DEK_LENGTH_BYTES];
		secureRandom.nextBytes(dek);
		boolean prepared = false;
		try {
			WrappedKey wrappedKey = keyEncryptionService.wrap(dek);
			if (wrappedKey.getWrappedKeyBytes().length > params.getReservedWrappedKeyCapacity()) {
				throw new EncryptedBlobKeyException("wrapped key length exceeds the reserved wrapped-key capacity");
			}

			byte[] noncePrefix = new byte[EncryptedBlobHeader.NONCE_PREFIX_LENGTH_BYTES];
			secureRandom.nextBytes(noncePrefix);

			EncryptedBlobHeader header = new EncryptedBlobHeader(EncryptedBlobHeader.FORMAT_VERSION,
					EncryptedBlobHeader.ALGORITHM_AES_256_GCM, nonceStrategy.schemeId(), plaintextSize,
					params.getChunkPlaintextSize(), chunkCount, noncePrefix, wrappedKey.getKeyId(),
					params.getReservedKeyIdCapacity(), wrappedKey.getWrappedKeyBytes(),
					params.getReservedWrappedKeyCapacity());
			EncryptingBlobContext ctx = new EncryptingBlobContext(header, dek);
			prepared = true;
			return ctx;
		} finally {
			if (!prepared) {
				Arrays.fill(dek, (byte) 0);
			}
		}
	}

	/** Encrypts exactly one chunk; bounded to one chunk-sized buffer. */
	public byte[] encryptChunk(EncryptingBlobContext ctx, long chunkIndex, byte[] chunkPlaintext, byte[] blobId) {
		EncryptedBlobHeader header = ctx.getHeader();
		ChunkLayout layout = ChunkLayout.of(header);
		int expectedLength = layout.chunkPlaintextLength(chunkIndex);
		if (chunkPlaintext.length != expectedLength) {
			throw new IllegalArgumentException("Chunk " + chunkIndex + " has unexpected plaintext length "
					+ chunkPlaintext.length + ", expected " + expectedLength);
		}

		NonceStrategy nonceStrategy = NonceStrategy.forSchemeId(header.getNonceSchemeId());
		byte[] nonce = nonceStrategy.deriveNonce(header.getNoncePrefix(), chunkIndex);
		byte[] aad = ChunkAadFactory.build(header, blobId, chunkIndex);

		try {
			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(ctx.getDek(), "AES"),
					new GCMParameterSpec(EncryptedBlobHeader.GCM_TAG_LENGTH_BITS, nonce));
			cipher.updateAAD(aad);
			return cipher.doFinal(chunkPlaintext);
		} catch (GeneralSecurityException e) {
			throw new EncryptedBlobKeyException("Chunk " + chunkIndex + " could not be encrypted", e);
		}
	}

	private static void readFullyStrict(InputStream in, byte[] buffer, int length) throws IOException {
		int totalRead = 0;
		while (totalRead < length) {
			int read = in.read(buffer, totalRead, length - totalRead);
			if (read == -1) {
				throw new EOFException(
						"Plaintext stream ended before the declared plaintextSize was fully read");
			}
			totalRead += read;
		}
	}
}
