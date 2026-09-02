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
package org.linagora.linshare.core.dao.impl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;
import java.util.stream.Stream;

import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.storage.encryption.crypto.EncryptionParameters;
import org.linagora.linshare.storage.encryption.format.EncryptedBlobHeader;
import org.linagora.linshare.storage.encryption.key.KeyEncryptionService;
import org.linagora.linshare.storage.encryption.key.LocalKeyEncryptionService;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.inject.Module;

/**
 * Exercises {@link EncryptedFileDataStoreImpl} against a real jclouds
 * filesystem provider on real disk — the ARCH.md 33 / CLAUDE.md 33 first
 * milestone: upload, confirm the persistent bytes are not plaintext, full
 * download matches the original, and an existing legacy plaintext blob
 * remains readable.
 */
class EncryptedFileSystemJcloudIntegrationTest {

	private static final Iterable<Module> MODULES = ImmutableSet.of(new SLF4JLoggingModule());

	private FileDataStore newRawFilesystemStore(Path baseDirectory) {
		return new FileSystemJcloudFileDataStoreImpl(MODULES, new Properties(), "test-bucket",
				baseDirectory.toString());
	}

	private EncryptedFileDataStoreImpl newEncryptedStore(FileDataStore delegate, boolean allowLegacyRead) {
		KeyEncryptionService keyEncryptionService = new LocalKeyEncryptionService(randomMasterKey(), "test-kek");
		EncryptionParameters params = new EncryptionParameters(64 * 1024, EncryptedBlobHeader.DEFAULT_KEY_ID_CAPACITY,
				EncryptedBlobHeader.DEFAULT_WRAPPED_KEY_CAPACITY);
		return new EncryptedFileDataStoreImpl(delegate, keyEncryptionService, params, true, true, allowLegacyRead);
	}

	private byte[] randomMasterKey() {
		byte[] key = new byte[32];
		new SecureRandom().nextBytes(key);
		return key;
	}

	@Test
	void uploadPersistsCiphertextAndDownloadMatchesOriginalSha256(@TempDir Path tempDir) throws Exception {
		byte[] plaintext = randomBytes(64 * 1024 * 3 + 777); // several chunks, non-aligned tail
		FileDataStore rawStore = newRawFilesystemStore(tempDir);
		EncryptedFileDataStoreImpl store = newEncryptedStore(rawStore, true);

		FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, "application/octet-stream",
				(long) plaintext.length, "large-file.bin");
		FileMetaData stored = store.add(ByteSource.wrap(plaintext), metadata);

		assertEquals(plaintext.length, stored.getSize());
		assertSame(metadata, stored);

		Path persistedFile = findPersistedFile(tempDir, stored.getUuid());
		byte[] persisted = Files.readAllBytes(persistedFile);
		assertFalse(containsSubsequence(persisted, plaintext), "persisted bytes must not contain the plaintext");
		byte[] magicPrefix = Arrays.copyOf(persisted, EncryptedBlobHeader.MAGIC.length);
		assertArrayEquals(EncryptedBlobHeader.MAGIC, magicPrefix);

		byte[] downloaded;
		try (InputStream in = store.get(stored).openStream()) {
			downloaded = ByteStreams.toByteArray(in);
		}
		assertArrayEquals(sha256(plaintext), sha256(downloaded));

		store.remove(stored);
		assertFalse(store.exists(stored));
	}

	@Test
	void legacyPlaintextBlobWrittenDirectlyToDiskRemainsReadable(@TempDir Path tempDir) throws Exception {
		FileDataStore rawStore = newRawFilesystemStore(tempDir);
		EncryptedFileDataStoreImpl store = newEncryptedStore(rawStore, true);

		byte[] legacyPlaintext = "legacy unencrypted content, predates this fork".getBytes(StandardCharsets.UTF_8);
		FileMetaData legacyMetadata = new FileMetaData(FileMetaDataKind.DATA, "text/plain",
				(long) legacyPlaintext.length, "legacy.txt");
		// Written through the RAW (unwrapped) store, exactly as an install
		// upgraded from before this fork existed would already have on disk.
		rawStore.add(ByteSource.wrap(legacyPlaintext), legacyMetadata);

		byte[] readBack;
		try (InputStream in = store.get(legacyMetadata).openStream()) {
			readBack = ByteStreams.toByteArray(in);
		}
		assertArrayEquals(legacyPlaintext, readBack);
	}

	private static Path findPersistedFile(Path tempDir, String uuid) throws IOException {
		try (Stream<Path> paths = Files.walk(tempDir)) {
			return paths.filter(Files::isRegularFile).filter(p -> p.getFileName().toString().equals(uuid)).findFirst()
					.orElseThrow(() -> new AssertionError("persisted blob file not found for uuid " + uuid));
		}
	}

	private static boolean containsSubsequence(byte[] haystack, byte[] needle) {
		outer: for (int i = 0; i <= haystack.length - needle.length; i++) {
			for (int j = 0; j < needle.length; j++) {
				if (haystack[i + j] != needle[j]) {
					continue outer;
				}
			}
			return true;
		}
		return false;
	}

	private static byte[] sha256(byte[] data) throws Exception {
		return MessageDigest.getInstance("SHA-256").digest(data);
	}

	private static byte[] randomBytes(int length) {
		byte[] bytes = new byte[length];
		new Random(21).nextBytes(bytes);
		return bytes;
	}
}
