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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.storage.encryption.crypto.EncryptionParameters;
import org.linagora.linshare.storage.encryption.exception.EncryptedBlobAuthenticationException;
import org.linagora.linshare.storage.encryption.exception.EncryptedBlobFormatException;
import org.linagora.linshare.storage.encryption.key.KeyEncryptionService;
import org.linagora.linshare.storage.encryption.key.LocalKeyEncryptionService;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

class EncryptedFileDataStoreImplTest {

	private KeyEncryptionService newKeyService() {
		byte[] masterKey = new byte[32];
		new SecureRandom().nextBytes(masterKey);
		return new LocalKeyEncryptionService(masterKey, "test-kek");
	}

	private EncryptionParameters smallChunkParams() {
		return new EncryptionParameters(16, 64, 512);
	}

	@Test
	void writeDisabledIsPureDelegation() throws IOException {
		FileDataStore delegate = mock(FileDataStore.class);
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, "text/plain", 5L, "f.txt");
		FileMetaData delegateResult = new FileMetaData(FileMetaDataKind.DATA, "text/plain", 5L, "f.txt");
		when(delegate.add(any(), any())).thenReturn(delegateResult);
		ByteSource byteSource = ByteSource.wrap(new byte[] { 1, 2, 3, 4, 5 });

		EncryptedFileDataStoreImpl store = new EncryptedFileDataStoreImpl(delegate, newKeyService(),
				smallChunkParams(), false, false, true);
		FileMetaData result = store.add(byteSource, metadata);

		verify(delegate).add(byteSource, metadata);
		assertSame(delegateResult, result);
	}

	@Test
	void readDisabledIsPureDelegation() {
		FileDataStore delegate = mock(FileDataStore.class);
		ByteSource delegateSource = ByteSource.wrap(new byte[] { 9, 9, 9 });
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, "text/plain", 3L, "f.txt");
		when(delegate.get(metadata)).thenReturn(delegateSource);

		EncryptedFileDataStoreImpl store = new EncryptedFileDataStoreImpl(delegate, newKeyService(),
				smallChunkParams(), false, false, true);

		assertSame(delegateSource, store.get(metadata));
	}

	@Test
	void removeAndExistsArePureDelegation() {
		FileDataStore delegate = mock(FileDataStore.class);
		when(delegate.exists(any())).thenReturn(true);
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, "text/plain", 3L, "f.txt");

		EncryptedFileDataStoreImpl store = new EncryptedFileDataStoreImpl(delegate, newKeyService(),
				smallChunkParams(), true, true, true);

		store.remove(metadata);
		verify(delegate).remove(metadata);
		assertEquals(true, store.exists(metadata));
		verify(delegate).exists(metadata);
	}

	@Test
	void encryptedUploadSetsExactPhysicalSizeAndPreservesPlaintextSizeOnReturn() throws IOException {
		byte[] plaintext = randomBytes(16 * 3 + 5);
		FileDataStore delegate = mock(FileDataStore.class);
		long[] actualCiphertextLength = new long[1];
		// Every real FileDataStore implementation (jclouds, the filesystem
		// provider's own strict length check) consumes the ByteSource
		// synchronously inside add(), before returning; a mock must do the same
		// to exercise the decorator realistically, since it owns the encryption
		// context's lifecycle only for the duration of this call.
		when(delegate.add(any(), any())).thenAnswer(invocation -> {
			ByteSource passedSource = invocation.getArgument(0);
			try (InputStream in = passedSource.openStream()) {
				actualCiphertextLength[0] = ByteStreams.copy(in, ByteStreams.nullOutputStream());
			}
			return invocation.getArgument(1);
		});
		FileMetaData originalMetadata = new FileMetaData(FileMetaDataKind.DATA, "application/octet-stream",
				(long) plaintext.length, "f.bin");

		EncryptedFileDataStoreImpl store = new EncryptedFileDataStoreImpl(delegate, newKeyService(),
				smallChunkParams(), true, true, true);
		FileMetaData returned = store.add(ByteSource.wrap(plaintext), originalMetadata);

		assertSame(originalMetadata, returned);
		assertEquals(plaintext.length, returned.getSize());
		org.mockito.ArgumentCaptor<FileMetaData> metadataCaptor = org.mockito.ArgumentCaptor.forClass(FileMetaData.class);
		verify(delegate).add(any(), metadataCaptor.capture());

		// The Content-Length declared to the delegate MUST equal the actual bytes
		// produced by the encrypting ByteSource, or a real filesystem provider
		// throws "Content-Length mismatch" and aborts the whole upload.
		assertEquals(metadataCaptor.getValue().getSize(), actualCiphertextLength[0]);
	}

	@Test
	void decryptedDownloadRoundTripsThroughFakeStore() throws Exception {
		InMemoryFileDataStore fakeDelegate = new InMemoryFileDataStore();
		byte[] plaintext = randomBytes(16 * 5 + 3);
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, "application/octet-stream",
				(long) plaintext.length, "f.bin");

		EncryptedFileDataStoreImpl store = new EncryptedFileDataStoreImpl(fakeDelegate, newKeyService(),
				smallChunkParams(), true, true, true);
		store.add(ByteSource.wrap(plaintext), metadata);

		byte[] roundTripped;
		try (InputStream in = store.get(metadata).openStream()) {
			roundTripped = ByteStreams.toByteArray(in);
		}
		assertArrayEquals(plaintext, roundTripped);
	}

	@Test
	void persistedBytesAreNotThePlaintext() throws Exception {
		InMemoryFileDataStore fakeDelegate = new InMemoryFileDataStore();
		byte[] plaintext = "this is a very recognizable plaintext marker".getBytes();
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, "text/plain", (long) plaintext.length,
				"f.txt");

		EncryptedFileDataStoreImpl store = new EncryptedFileDataStoreImpl(fakeDelegate, newKeyService(),
				smallChunkParams(), true, true, true);
		store.add(ByteSource.wrap(plaintext), metadata);

		byte[] persisted = fakeDelegate.rawBytes(metadata.getUuid());
		String persistedAsLatin1 = new String(persisted, java.nio.charset.StandardCharsets.ISO_8859_1);
		org.junit.jupiter.api.Assertions.assertFalse(
				persistedAsLatin1.contains("this is a very recognizable plaintext marker"));
	}

	@Test
	void legacyPlaintextReadAllowedWhenConfigured() throws Exception {
		InMemoryFileDataStore fakeDelegate = new InMemoryFileDataStore();
		byte[] legacyPlaintext = randomBytes(50);
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, "text/plain", (long) legacyPlaintext.length,
				"legacy.txt");
		metadata.setUuid("legacy-uuid");
		fakeDelegate.putRaw(metadata.getUuid(), legacyPlaintext);

		EncryptedFileDataStoreImpl store = new EncryptedFileDataStoreImpl(fakeDelegate, newKeyService(),
				smallChunkParams(), true, true, true);

		byte[] result;
		try (InputStream in = store.get(metadata).openStream()) {
			result = ByteStreams.toByteArray(in);
		}
		assertArrayEquals(legacyPlaintext, result);
	}

	@Test
	void legacyPlaintextReadRejectedWhenDisallowed() throws Exception {
		InMemoryFileDataStore fakeDelegate = new InMemoryFileDataStore();
		byte[] legacyPlaintext = randomBytes(50);
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, "text/plain", (long) legacyPlaintext.length,
				"legacy.txt");
		metadata.setUuid("legacy-uuid");
		fakeDelegate.putRaw(metadata.getUuid(), legacyPlaintext);

		EncryptedFileDataStoreImpl store = new EncryptedFileDataStoreImpl(fakeDelegate, newKeyService(),
				smallChunkParams(), true, true, false);

		assertThrows(EncryptedBlobFormatException.class, () -> store.get(metadata).openStream());
	}

	@Test
	void malformedLse1NeverFallsBackToPlaintext() throws Exception {
		InMemoryFileDataStore fakeDelegate = new InMemoryFileDataStore();
		byte[] plaintext = randomBytes(16 * 3);
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, "application/octet-stream",
				(long) plaintext.length, "f.bin");

		EncryptedFileDataStoreImpl store = new EncryptedFileDataStoreImpl(fakeDelegate, newKeyService(),
				smallChunkParams(), true, true, true);
		store.add(ByteSource.wrap(plaintext), metadata);

		byte[] corrupted = fakeDelegate.rawBytes(metadata.getUuid());
		corrupted[corrupted.length - 1] ^= 0x01; // flip a byte in the final chunk's tag
		fakeDelegate.putRaw(metadata.getUuid(), corrupted);

		assertThrows(EncryptedBlobAuthenticationException.class, () -> {
			try (InputStream in = store.get(metadata).openStream()) {
				ByteStreams.toByteArray(in);
			}
		});
	}

	private static byte[] randomBytes(int length) {
		byte[] bytes = new byte[length];
		new Random(13).nextBytes(bytes);
		return bytes;
	}

	/** Minimal in-memory {@link FileDataStore} for exercising real byte flows without jclouds. */
	private static final class InMemoryFileDataStore implements FileDataStore {

		private final Map<String, byte[]> blobs = new HashMap<>();

		@Override
		public void remove(FileMetaData metadata) {
			blobs.remove(metadata.getUuid());
		}

		@Override
		public FileMetaData add(ByteSource byteSource, FileMetaData metadata) throws IOException {
			if (metadata.getUuid() == null) {
				metadata.setUuid(java.util.UUID.randomUUID().toString());
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try (InputStream in = byteSource.openStream()) {
				long written = ByteStreams.copy(in, out);
				if (written != metadata.getSize()) {
					throw new IOException("Content-Length mismatch, actual: " + written + " expected: "
							+ metadata.getSize());
				}
			}
			blobs.put(metadata.getUuid(), out.toByteArray());
			return metadata;
		}

		@Override
		public ByteSource get(FileMetaData metadata) {
			return new ByteSource() {
				@Override
				public InputStream openStream() {
					return new ByteArrayInputStream(blobs.get(metadata.getUuid()));
				}
			};
		}

		@Override
		public boolean exists(FileMetaData metadata) {
			return blobs.containsKey(metadata.getUuid());
		}

		byte[] rawBytes(String uuid) {
			return blobs.get(uuid);
		}

		void putRaw(String uuid, byte[] bytes) {
			blobs.put(uuid, bytes);
		}
	}
}
