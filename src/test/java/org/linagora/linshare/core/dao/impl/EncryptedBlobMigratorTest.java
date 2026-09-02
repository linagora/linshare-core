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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.linagora.linshare.core.dao.AtomicBlobReplace;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.storage.encryption.crypto.ChunkedEncryptor;
import org.linagora.linshare.storage.encryption.crypto.EncryptingBlobContext;
import org.linagora.linshare.storage.encryption.crypto.EncryptingInputStream;
import org.linagora.linshare.storage.encryption.crypto.EncryptionParameters;
import org.linagora.linshare.storage.encryption.format.EncryptedBlobHeader;
import org.linagora.linshare.storage.encryption.key.KeyEncryptionService;
import org.linagora.linshare.storage.encryption.key.LocalKeyEncryptionService;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

class EncryptedBlobMigratorTest {

	private static final String BUCKET = "bucket-1";

	private KeyEncryptionService newKeyService() {
		byte[] masterKey = new byte[32];
		new SecureRandom().nextBytes(masterKey);
		return new LocalKeyEncryptionService(masterKey, "test-kek");
	}

	private EncryptionParameters smallChunkParams() {
		return new EncryptionParameters(16, 64, 512);
	}

	private static byte[] randomBytes(int length) {
		byte[] bytes = new byte[length];
		new Random(17).nextBytes(bytes);
		return bytes;
	}

	private static String sha256Hex(byte[] data) throws Exception {
		byte[] digest = MessageDigest.getInstance("SHA-256").digest(data);
		StringBuilder sb = new StringBuilder();
		for (byte b : digest) {
			sb.append(String.format(Locale.ROOT, "%02x", b));
		}
		return sb.toString();
	}

	private FileMetaData legacyMetadata(InMemoryFileDataStore store, byte[] legacyPlaintext, String uuid)
			throws IOException {
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, "application/octet-stream",
				(long) legacyPlaintext.length, "f.bin");
		metadata.setUuid(uuid);
		metadata.setBucketUuid(BUCKET);
		store.putRaw(BUCKET, uuid, legacyPlaintext);
		return metadata;
	}

	@Test
	void missingBlobReturnsMissing() throws Exception {
		InMemoryFileDataStore store = new InMemoryFileDataStore();
		EncryptedBlobMigrator migrator = new EncryptedBlobMigrator(store, newKeyService(), smallChunkParams());
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, "application/octet-stream", 10L, "f.bin");
		metadata.setUuid("does-not-exist");
		metadata.setBucketUuid(BUCKET);

		assertEquals(MigrationOutcome.MISSING, migrator.migrate(metadata, "irrelevant"));
	}

	@Test
	void freshMigrationEncryptsVerifiesAndCommits() throws Exception {
		InMemoryFileDataStore store = new InMemoryFileDataStore();
		byte[] legacyPlaintext = randomBytes(16 * 5 + 3);
		FileMetaData metadata = legacyMetadata(store, legacyPlaintext, "doc-1");
		EncryptedBlobMigrator migrator = new EncryptedBlobMigrator(store, newKeyService(), smallChunkParams());

		MigrationOutcome outcome = migrator.migrate(metadata, sha256Hex(legacyPlaintext));

		assertEquals(MigrationOutcome.MIGRATED, outcome);
		assertFalse(store.exists(tempOf(metadata)), "temp key must be cleaned up after commit");
		assertFalse(migrator.isLegacyBlob(metadata));

		byte[] persisted = store.rawBytes(BUCKET, "doc-1");
		assertArrayEquals(EncryptedBlobHeader.magic(), Arrays.copyOf(persisted, 4));
	}

	@Test
	void alreadyEncryptedBlobIsSkippedIdempotently() throws Exception {
		InMemoryFileDataStore store = new InMemoryFileDataStore();
		byte[] legacyPlaintext = randomBytes(50);
		FileMetaData metadata = legacyMetadata(store, legacyPlaintext, "doc-1");
		EncryptedBlobMigrator migrator = new EncryptedBlobMigrator(store, newKeyService(), smallChunkParams());
		migrator.migrate(metadata, sha256Hex(legacyPlaintext));
		byte[] afterFirstMigration = store.rawBytes(BUCKET, "doc-1");

		MigrationOutcome secondRun = migrator.migrate(metadata, sha256Hex(legacyPlaintext));

		assertEquals(MigrationOutcome.ALREADY_ENCRYPTED, secondRun);
		assertArrayEquals(afterFirstMigration, store.rawBytes(BUCKET, "doc-1"),
				"a no-op skip must not touch the already-migrated blob");
	}

	@Test
	void interruptedRunWithValidTempIsResumedWithoutReEncrypting() throws Exception {
		InMemoryFileDataStore store = new InMemoryFileDataStore();
		byte[] legacyPlaintext = randomBytes(16 * 5 + 3);
		FileMetaData metadata = legacyMetadata(store, legacyPlaintext, "doc-1");
		KeyEncryptionService keyService = newKeyService();
		EncryptionParameters params = smallChunkParams();

		// Simulate a prior run that finished encrypt-to-temp but crashed before commit.
		byte[] preplacedTempCiphertext = encryptForMigration(keyService, params, legacyPlaintext, "doc-1");
		store.putRaw(BUCKET, "doc-1.migrating", preplacedTempCiphertext);

		EncryptedBlobMigrator migrator = new EncryptedBlobMigrator(store, keyService, params);
		MigrationOutcome outcome = migrator.migrate(metadata, sha256Hex(legacyPlaintext));

		assertEquals(MigrationOutcome.MIGRATED, outcome);
		// The committed bytes must be EXACTLY the pre-placed temp ciphertext —
		// proving the migrator resumed/reused it rather than generating a
		// fresh DEK and re-encrypting from scratch.
		assertArrayEquals(preplacedTempCiphertext, store.rawBytes(BUCKET, "doc-1"));
	}

	@Test
	void corruptedTempIsDiscardedAndRetrySucceeds() throws Exception {
		InMemoryFileDataStore store = new InMemoryFileDataStore();
		byte[] legacyPlaintext = randomBytes(16 * 5 + 3);
		FileMetaData metadata = legacyMetadata(store, legacyPlaintext, "doc-1");
		KeyEncryptionService keyService = newKeyService();
		EncryptionParameters params = smallChunkParams();

		byte[] corruptedTemp = encryptForMigration(keyService, params, legacyPlaintext, "doc-1");
		corruptedTemp[corruptedTemp.length - 1] ^= 0x01; // flip the final chunk's tag
		store.putRaw(BUCKET, "doc-1.migrating", corruptedTemp);

		EncryptedBlobMigrator migrator = new EncryptedBlobMigrator(store, keyService, params);

		MigrationOutcome firstAttempt = migrator.migrate(metadata, sha256Hex(legacyPlaintext));
		assertEquals(MigrationOutcome.VERIFICATION_FAILED, firstAttempt);
		assertFalse(store.exists(tempOf(metadata)), "corrupted temp must be removed after a failed verification");
		// The original legacy blob must be completely untouched by a failed attempt.
		assertArrayEquals(legacyPlaintext, store.rawBytes(BUCKET, "doc-1"));

		MigrationOutcome retry = migrator.migrate(metadata, sha256Hex(legacyPlaintext));
		assertEquals(MigrationOutcome.MIGRATED, retry);
	}

	@Test
	void migratedBlobDecryptsToOriginalPlaintextThroughTheDecorator() throws Exception {
		InMemoryFileDataStore store = new InMemoryFileDataStore();
		byte[] legacyPlaintext = randomBytes(16 * 5 + 3);
		FileMetaData metadata = legacyMetadata(store, legacyPlaintext, "doc-1");
		KeyEncryptionService keyService = newKeyService();
		EncryptionParameters params = smallChunkParams();
		new EncryptedBlobMigrator(store, keyService, params).migrate(metadata, sha256Hex(legacyPlaintext));

		EncryptedFileDataStoreImpl decoratedStore = new EncryptedFileDataStoreImpl(store, keyService, params, true,
				true, true);
		byte[] roundTripped;
		try (InputStream in = decoratedStore.get(metadata).openStream()) {
			roundTripped = ByteStreams.toByteArray(in);
		}
		assertArrayEquals(legacyPlaintext, roundTripped);
	}

	@Test
	void commitUsesAtomicReplaceWhenBackendSupportsIt() throws Exception {
		InMemoryAtomicFileDataStore store = new InMemoryAtomicFileDataStore();
		byte[] legacyPlaintext = randomBytes(50);
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, "application/octet-stream",
				(long) legacyPlaintext.length, "f.bin");
		metadata.setUuid("doc-1");
		metadata.setBucketUuid(BUCKET);
		store.putRaw(BUCKET, "doc-1", legacyPlaintext);
		EncryptedBlobMigrator migrator = new EncryptedBlobMigrator(store, newKeyService(), smallChunkParams());

		MigrationOutcome outcome = migrator.migrate(metadata, sha256Hex(legacyPlaintext));

		assertEquals(MigrationOutcome.MIGRATED, outcome);
		assertTrue(store.atomicReplaceWasCalled, "commit must prefer AtomicBlobReplace when available");
	}

	private FileMetaData tempOf(FileMetaData metadata) {
		FileMetaData temp = new FileMetaData(metadata.getKind(), metadata.getMimeType(), metadata.getSize(),
				metadata.getFileName());
		temp.setUuid(metadata.getUuid() + ".migrating");
		temp.setBucketUuid(metadata.getBucketUuid());
		return temp;
	}

	/** Encrypts {@code plaintext} exactly as the migrator would, for pre-seeding a temp key in tests. */
	private static byte[] encryptForMigration(KeyEncryptionService keyService, EncryptionParameters params,
			byte[] plaintext, String realUuid) throws IOException {
		ChunkedEncryptor encryptor = new ChunkedEncryptor(keyService, params);
		EncryptingBlobContext ctx = encryptor.prepare(plaintext.length);
		try (EncryptingInputStream in = new EncryptingInputStream(encryptor, ctx,
				new ByteArrayInputStream(plaintext), realUuid.getBytes(StandardCharsets.UTF_8))) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ByteStreams.copy(in, out);
			return out.toByteArray();
		} finally {
			ctx.close();
		}
	}

	/** Minimal in-memory {@link FileDataStore}, enforcing the same Content-Length contract real backends do. */
	private static class InMemoryFileDataStore implements FileDataStore {

		final Map<String, byte[]> blobs = new HashMap<>();

		static String key(String container, String uuid) {
			return container + "/" + uuid;
		}

		@Override
		public void remove(FileMetaData metadata) {
			blobs.remove(key(metadata.getBucketUuid(), metadata.getUuid()));
		}

		@Override
		public FileMetaData add(ByteSource byteSource, FileMetaData metadata) throws IOException {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try (InputStream in = byteSource.openStream()) {
				long written = ByteStreams.copy(in, out);
				if (written != metadata.getSize()) {
					throw new IOException(
							"Content-Length mismatch, actual: " + written + " expected: " + metadata.getSize());
				}
			}
			blobs.put(key(metadata.getBucketUuid(), metadata.getUuid()), out.toByteArray());
			return metadata;
		}

		@Override
		public ByteSource get(FileMetaData metadata) {
			return new ByteSource() {
				@Override
				public InputStream openStream() {
					byte[] bytes = blobs.get(key(metadata.getBucketUuid(), metadata.getUuid()));
					if (bytes == null) {
						throw new IllegalStateException("no such blob: " + metadata.getUuid());
					}
					return new ByteArrayInputStream(bytes);
				}
			};
		}

		@Override
		public boolean exists(FileMetaData metadata) {
			return blobs.containsKey(key(metadata.getBucketUuid(), metadata.getUuid()));
		}

		void putRaw(String container, String uuid, byte[] bytes) {
			blobs.put(key(container, uuid), bytes);
		}

		byte[] rawBytes(String container, String uuid) {
			return blobs.get(key(container, uuid));
		}
	}

	/** Same fake, additionally implementing the atomic-replace capability. */
	private static final class InMemoryAtomicFileDataStore extends InMemoryFileDataStore implements AtomicBlobReplace {

		boolean atomicReplaceWasCalled;

		@Override
		public void atomicReplace(String container, String sourceKey, String targetKey) throws IOException {
			atomicReplaceWasCalled = true;
			byte[] sourceBytes = blobs.remove(key(container, sourceKey));
			if (sourceBytes == null) {
				throw new IOException("no such source blob: " + sourceKey);
			}
			blobs.put(key(container, targetKey), sourceBytes);
		}
	}
}
