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

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.dao.impl.EncryptedFileDataStoreImpl;
import org.linagora.linshare.storage.encryption.exception.EncryptedBlobKeyException;

class EncryptedFileDataStoreFactoryTest {

	@Test
	void disabledFactoryReturnsDelegateUnchanged() {
		FileDataStore delegate = mock(FileDataStore.class);
		EncryptedFileDataStoreFactory factory = new EncryptedFileDataStoreFactory();
		factory.setDelegate(delegate);
		factory.setWriteEnabled(false);
		factory.setReadEnabled(false);

		assertSame(delegate, factory.getDefault());
	}

	@Test
	void enabledFactoryWithValidLocalKeyBuildsEncryptedStore(@TempDir Path tempDir) throws Exception {
		Path keyFile = tempDir.resolve("master.key");
		Files.write(keyFile, new byte[32]);

		FileDataStore delegate = mock(FileDataStore.class);
		EncryptedFileDataStoreFactory factory = new EncryptedFileDataStoreFactory();
		factory.setDelegate(delegate);
		factory.setWriteEnabled(true);
		factory.setReadEnabled(true);
		factory.setKeyProvider("local");
		factory.setLocalMasterKeyFile(keyFile.toString());
		factory.setKeyId("test-kek");

		assertTrue(factory.getDefault() instanceof EncryptedFileDataStoreImpl);
	}

	@Test
	void failsClosedAtStartupWhenMasterKeyFileIsMissing(@TempDir Path tempDir) {
		FileDataStore delegate = mock(FileDataStore.class);
		EncryptedFileDataStoreFactory factory = new EncryptedFileDataStoreFactory();
		factory.setDelegate(delegate);
		factory.setWriteEnabled(true);
		factory.setReadEnabled(true);
		factory.setKeyProvider("local");
		factory.setLocalMasterKeyFile(tempDir.resolve("does-not-exist").toString());
		factory.setKeyId("test-kek");

		assertThrows(EncryptedBlobKeyException.class, factory::getDefault);
	}

	@Test
	void failsClosedAtStartupWhenMasterKeyHasWrongLength(@TempDir Path tempDir) throws Exception {
		Path keyFile = tempDir.resolve("master.key");
		Files.write(keyFile, new byte[16]); // wrong length, must be 32 bytes

		FileDataStore delegate = mock(FileDataStore.class);
		EncryptedFileDataStoreFactory factory = new EncryptedFileDataStoreFactory();
		factory.setDelegate(delegate);
		factory.setWriteEnabled(true);
		factory.setReadEnabled(false);
		factory.setKeyProvider("local");
		factory.setLocalMasterKeyFile(keyFile.toString());
		factory.setKeyId("test-kek");

		assertThrows(EncryptedBlobKeyException.class, factory::getDefault);
	}

	@Test
	void failsClosedOnUnsupportedKeyProvider(@TempDir Path tempDir) throws Exception {
		Path keyFile = tempDir.resolve("master.key");
		Files.write(keyFile, new byte[32]);

		FileDataStore delegate = mock(FileDataStore.class);
		EncryptedFileDataStoreFactory factory = new EncryptedFileDataStoreFactory();
		factory.setDelegate(delegate);
		factory.setWriteEnabled(true);
		factory.setReadEnabled(true);
		factory.setKeyProvider("vault");
		factory.setLocalMasterKeyFile(keyFile.toString());
		factory.setKeyId("test-kek");

		assertThrows(EncryptedBlobKeyException.class, factory::getDefault);
	}
}
