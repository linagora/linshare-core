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
package org.linagora.linshare.storage.encryption.key;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.linagora.linshare.storage.encryption.exception.EncryptedBlobKeyException;
import org.linagora.linshare.storage.encryption.format.EncryptedBlobHeader;

/**
 * Development/test key provider: wraps DEKs with a single AES-256-GCM master
 * key held in memory. Fails closed at construction time on any invalid key
 * material; there is no fallback default key. Not intended for production
 * use (ARCH.md 19.1).
 */
public final class LocalKeyEncryptionService implements KeyEncryptionService {

	public static final String WRAPPING_ALGORITHM_ID = "LOCAL-AES-256-GCM-WRAP-v1";

	private static final String CIPHER_TRANSFORMATION = "AES/GCM/NoPadding";

	private static final int WRAP_NONCE_LENGTH_BYTES = 12;

	private final byte[] masterKey;

	private final String keyId;

	private final SecureRandom secureRandom = new SecureRandom();

	public LocalKeyEncryptionService(byte[] masterKey, String keyId) {
		if (masterKey == null || masterKey.length != EncryptedBlobHeader.DEK_LENGTH_BYTES) {
			throw new EncryptedBlobKeyException(
					"master key must be exactly " + EncryptedBlobHeader.DEK_LENGTH_BYTES + " bytes");
		}
		if (keyId == null || keyId.isEmpty()) {
			throw new EncryptedBlobKeyException("keyId must not be null or empty");
		}
		this.masterKey = masterKey.clone();
		this.keyId = keyId;
	}

	public LocalKeyEncryptionService(Path masterKeyFile, String keyId) {
		this(readKeyFile(masterKeyFile), keyId);
	}

	private static byte[] readKeyFile(Path masterKeyFile) {
		try {
			return Files.readAllBytes(masterKeyFile);
		} catch (IOException e) {
			throw new EncryptedBlobKeyException("Unable to read master key file: " + masterKeyFile, e);
		}
	}

	@Override
	public WrappedKey wrap(byte[] dek) {
		if (dek == null || dek.length != EncryptedBlobHeader.DEK_LENGTH_BYTES) {
			throw new EncryptedBlobKeyException(
					"dek must be exactly " + EncryptedBlobHeader.DEK_LENGTH_BYTES + " bytes");
		}
		byte[] nonce = new byte[WRAP_NONCE_LENGTH_BYTES];
		secureRandom.nextBytes(nonce);
		try {
			Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(masterKey, "AES"),
					new GCMParameterSpec(EncryptedBlobHeader.GCM_TAG_LENGTH_BITS, nonce));
			cipher.updateAAD(keyIdBytes());
			byte[] ciphertextAndTag = cipher.doFinal(dek);

			byte[] wrapped = new byte[nonce.length + ciphertextAndTag.length];
			System.arraycopy(nonce, 0, wrapped, 0, nonce.length);
			System.arraycopy(ciphertextAndTag, 0, wrapped, nonce.length, ciphertextAndTag.length);
			return new WrappedKey(keyId, wrapped, WRAPPING_ALGORITHM_ID);
		} catch (GeneralSecurityException e) {
			throw new EncryptedBlobKeyException("Failed to wrap DEK", e);
		}
	}

	@Override
	public byte[] unwrap(String requestedKeyId, byte[] wrappedKeyBytes) {
		if (!keyId.equals(requestedKeyId)) {
			throw new EncryptedBlobKeyException("Unknown key id: " + requestedKeyId);
		}
		if (wrappedKeyBytes == null || wrappedKeyBytes.length <= WRAP_NONCE_LENGTH_BYTES) {
			throw new EncryptedBlobKeyException("wrappedKeyBytes is too short to be valid");
		}
		byte[] nonce = Arrays.copyOfRange(wrappedKeyBytes, 0, WRAP_NONCE_LENGTH_BYTES);
		byte[] ciphertextAndTag = Arrays.copyOfRange(wrappedKeyBytes, WRAP_NONCE_LENGTH_BYTES,
				wrappedKeyBytes.length);
		try {
			Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(masterKey, "AES"),
					new GCMParameterSpec(EncryptedBlobHeader.GCM_TAG_LENGTH_BITS, nonce));
			cipher.updateAAD(keyIdBytes());
			return cipher.doFinal(ciphertextAndTag);
		} catch (AEADBadTagException e) {
			throw new EncryptedBlobKeyException("Wrapped key authentication failed", e);
		} catch (GeneralSecurityException e) {
			throw new EncryptedBlobKeyException("Failed to unwrap DEK", e);
		}
	}

	private byte[] keyIdBytes() {
		return keyId.getBytes(java.nio.charset.StandardCharsets.UTF_8);
	}
}
