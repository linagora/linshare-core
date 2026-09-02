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
package org.linagora.linshare.core.dao.utils;

import java.nio.file.Paths;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.dao.impl.EncryptedFileDataStoreImpl;
import org.linagora.linshare.storage.encryption.crypto.EncryptionParameters;
import org.linagora.linshare.storage.encryption.exception.EncryptedBlobKeyException;
import org.linagora.linshare.storage.encryption.format.EncryptedBlobHeader;
import org.linagora.linshare.storage.encryption.key.KeyEncryptionService;
import org.linagora.linshare.storage.encryption.key.LocalKeyEncryptionService;

/**
 * Builds the {@code fileDataStore} bean, transparently wrapping a delegate
 * store with {@link EncryptedFileDataStoreImpl} when encryption is enabled.
 * With both {@code writeEnabled} and {@code readEnabled} false (the
 * conservative default), the delegate is returned unchanged and no key
 * material is ever read — upgrading the fork must not change existing,
 * unencrypted installations (ARCH.md 20, 31).
 */
public class EncryptedFileDataStoreFactory {

	protected static final String LOCAL_KEY_PROVIDER = "local";

	protected FileDataStore delegate;

	protected boolean writeEnabled;

	protected boolean readEnabled;

	protected boolean allowLegacyRead = true;

	protected int chunkSize = EncryptedBlobHeader.DEFAULT_CHUNK_PLAINTEXT_SIZE;

	protected String keyProvider = LOCAL_KEY_PROVIDER;

	protected String localMasterKeyFile;

	protected String keyId;

	public FileDataStore getDefault() {
		if (!writeEnabled && !readEnabled) {
			return delegate;
		}
		KeyEncryptionService keyEncryptionService = buildKeyEncryptionService();
		EncryptionParameters params = new EncryptionParameters(chunkSize,
				EncryptedBlobHeader.DEFAULT_KEY_ID_CAPACITY, EncryptedBlobHeader.DEFAULT_WRAPPED_KEY_CAPACITY);
		return new EncryptedFileDataStoreImpl(delegate, keyEncryptionService, params, writeEnabled, readEnabled,
				allowLegacyRead);
	}

	private KeyEncryptionService buildKeyEncryptionService() {
		if (!LOCAL_KEY_PROVIDER.equals(keyProvider)) {
			throw new EncryptedBlobKeyException("Unsupported linshare.documents.encryption.key-provider: "
					+ keyProvider);
		}
		Validate.notEmpty(localMasterKeyFile, "Missing linshare.documents.encryption.local.master-key-file");
		Validate.notEmpty(keyId, "Missing linshare.documents.encryption.key-id");
		return new LocalKeyEncryptionService(Paths.get(localMasterKeyFile), keyId);
	}

	public void setDelegate(FileDataStore delegate) {
		this.delegate = delegate;
	}

	public void setWriteEnabled(boolean writeEnabled) {
		this.writeEnabled = writeEnabled;
	}

	public void setReadEnabled(boolean readEnabled) {
		this.readEnabled = readEnabled;
	}

	public void setAllowLegacyRead(boolean allowLegacyRead) {
		this.allowLegacyRead = allowLegacyRead;
	}

	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	public void setKeyProvider(String keyProvider) {
		this.keyProvider = keyProvider;
	}

	public void setLocalMasterKeyFile(String localMasterKeyFile) {
		this.localMasterKeyFile = localMasterKeyFile;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}
}
