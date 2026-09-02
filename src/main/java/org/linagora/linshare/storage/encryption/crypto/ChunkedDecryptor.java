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
import java.io.OutputStream;
import java.security.GeneralSecurityException;

import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.linagora.linshare.storage.encryption.exception.EncryptedBlobAuthenticationException;
import org.linagora.linshare.storage.encryption.exception.EncryptedBlobFormatException;
import org.linagora.linshare.storage.encryption.exception.EncryptedBlobKeyException;
import org.linagora.linshare.storage.encryption.format.ChunkAadFactory;
import org.linagora.linshare.storage.encryption.format.ChunkLayout;
import org.linagora.linshare.storage.encryption.format.EncryptedBlobFormat;
import org.linagora.linshare.storage.encryption.format.EncryptedBlobHeader;
import org.linagora.linshare.storage.encryption.format.NonceStrategy;
import org.linagora.linshare.storage.encryption.key.KeyEncryptionService;

/**
 * Parses and authenticates an LSE1 header, unwraps its DEK once, and decrypts
 * chunks. Every chunk is fully authenticated before any of its plaintext is
 * released (CLAUDE.md 5) — a malformed or tampered LSE1 blob never falls back
 * to plaintext (ARCH.md 21).
 */
public final class ChunkedDecryptor {

	private final KeyEncryptionService keyEncryptionService;

	public ChunkedDecryptor(KeyEncryptionService keyEncryptionService) {
		if (keyEncryptionService == null) {
			throw new IllegalArgumentException("keyEncryptionService must not be null");
		}
		this.keyEncryptionService = keyEncryptionService;
	}

	/**
	 * @param blobId must be the identical bytes supplied at encryption time.
	 */
	public void decrypt(InputStream encryptedIn, byte[] blobId, OutputStream plaintextOut) throws IOException {
		try (UnwrappedBlobContext ctx = open(encryptedIn, blobId)) {
			ChunkLayout layout = ChunkLayout.of(ctx.getHeader());
			for (long chunkIndex = 0; chunkIndex < layout.chunkCount(); chunkIndex++) {
				int ciphertextLength = layout.chunkCiphertextLength(chunkIndex);
				byte[] chunkBytes = new byte[ciphertextLength];
				EncryptedBlobFormat.readFully(encryptedIn, chunkBytes, ciphertextLength);
				byte[] plaintext = decryptChunk(ctx, chunkIndex, chunkBytes);
				plaintextOut.write(plaintext);
			}
		}
	}

	/**
	 * Parses+authenticates the header and unwraps the DEK once, for callers
	 * driving their own per-chunk reads (e.g. a future range reader).
	 */
	public UnwrappedBlobContext open(InputStream encryptedInPositionedAtHeader, byte[] blobId) throws IOException {
		if (blobId == null) {
			throw new IllegalArgumentException("blobId is required and must not be null");
		}
		EncryptedBlobHeader header = EncryptedBlobFormat.readHeader(encryptedInPositionedAtHeader);
		byte[] dek = keyEncryptionService.unwrap(header.getKeyId(), header.getWrappedKeyBytes());
		if (dek == null || dek.length != EncryptedBlobHeader.DEK_LENGTH_BYTES) {
			throw new EncryptedBlobKeyException("Unwrapped DEK has an unexpected length");
		}
		return new UnwrappedBlobContext(header, blobId, dek);
	}

	/** Decrypts exactly one chunk record; bounded to one chunk-sized buffer. */
	public byte[] decryptChunk(UnwrappedBlobContext ctx, long chunkIndex, byte[] chunkCiphertextAndTag) {
		EncryptedBlobHeader header = ctx.getHeader();
		ChunkLayout layout = ChunkLayout.of(header);
		int expectedLength = layout.chunkCiphertextLength(chunkIndex);
		if (chunkCiphertextAndTag.length != expectedLength) {
			throw new EncryptedBlobFormatException("Chunk " + chunkIndex + " has unexpected record length "
					+ chunkCiphertextAndTag.length + ", expected " + expectedLength);
		}

		NonceStrategy nonceStrategy = NonceStrategy.forSchemeId(header.getNonceSchemeId());
		byte[] nonce = nonceStrategy.deriveNonce(header.getNoncePrefix(), chunkIndex);
		byte[] aad = ChunkAadFactory.build(header, ctx.getBlobId(), chunkIndex);

		try {
			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(ctx.getDek(), "AES"),
					new GCMParameterSpec(EncryptedBlobHeader.GCM_TAG_LENGTH_BITS, nonce));
			cipher.updateAAD(aad);
			return cipher.doFinal(chunkCiphertextAndTag);
		} catch (AEADBadTagException e) {
			throw new EncryptedBlobAuthenticationException("Chunk " + chunkIndex + " failed authentication", e);
		} catch (GeneralSecurityException e) {
			throw new EncryptedBlobAuthenticationException("Chunk " + chunkIndex + " could not be decrypted", e);
		}
	}
}
