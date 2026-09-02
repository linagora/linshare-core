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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStoreContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.objects.FileMetaData;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

/**
 * Exercises {@link AbstractJcloudFileDataStoreImpl}'s generic, {@code
 * copyBlob}-based {@code atomicReplace} — the fallback S3/Swift use since
 * neither backend has a filesystem-style inode rename (ARCH.md 18) — against
 * jclouds' in-process "transient" provider (bundled inside jclouds-blobstore,
 * no real cloud credentials or new dependency needed).
 *
 * <p>The transient provider is backed by the same generic {@code BlobStore}
 * machinery the filesystem provider uses, so this proves our calling code
 * drives the real jclouds {@code copyBlob}/{@code CopyOptions} API surface
 * correctly. It cannot, by itself, prove S3/Swift's own wire-level copy
 * behavior (a real {@code x-amz-copy-source}/{@code X-Copy-From} PUT) — that
 * was independently confirmed by decompiling the S3/Swift jclouds jars (see
 * docs/PHASE7-OBJECT-STORAGE.md).
 */
class AbstractJcloudFileDataStoreImplAtomicReplaceTest {

	private TransientJcloudStore store;

	@AfterEach
	void closeStore() throws IOException {
		if (store != null) {
			store.close();
		}
	}

	@Test
	void genericAtomicReplaceCopiesThenRemovesSource() throws Exception {
		store = new TransientJcloudStore("test-bucket");
		byte[] content = "hello atomic replace".getBytes(StandardCharsets.UTF_8);
		FileMetaData sourceMetadata = new FileMetaData(FileMetaDataKind.DATA, "text/plain", (long) content.length,
				"source.bin");
		FileMetaData stored = store.add(ByteSource.wrap(content), sourceMetadata);

		store.atomicReplace("test-bucket", stored.getUuid(), "target-key");

		FileMetaData sourceLookup = new FileMetaData(FileMetaDataKind.DATA, "text/plain", (long) content.length,
				"source.bin");
		sourceLookup.setUuid(stored.getUuid());
		sourceLookup.setBucketUuid("test-bucket");
		assertFalse(store.exists(sourceLookup), "source key must no longer exist after atomicReplace");

		FileMetaData targetMetadata = new FileMetaData(FileMetaDataKind.DATA, "text/plain", (long) content.length,
				"target.bin");
		targetMetadata.setUuid("target-key");
		targetMetadata.setBucketUuid("test-bucket");
		byte[] copied;
		try (InputStream in = store.get(targetMetadata).openStream()) {
			copied = ByteStreams.toByteArray(in);
		}
		assertArrayEquals(content, copied);
	}

	@Test
	void genericAtomicReplaceReplacesExistingTargetContent() throws Exception {
		store = new TransientJcloudStore("test-bucket");
		byte[] oldTargetContent = "old target content".getBytes(StandardCharsets.UTF_8);
		byte[] newContent = "new content wins".getBytes(StandardCharsets.UTF_8);

		FileMetaData oldTarget = new FileMetaData(FileMetaDataKind.DATA, "text/plain",
				(long) oldTargetContent.length, "old.bin");
		oldTarget.setUuid("target-key");
		store.add(ByteSource.wrap(oldTargetContent), oldTarget);

		FileMetaData source = new FileMetaData(FileMetaDataKind.DATA, "text/plain", (long) newContent.length,
				"new.bin");
		FileMetaData storedSource = store.add(ByteSource.wrap(newContent), source);

		store.atomicReplace("test-bucket", storedSource.getUuid(), "target-key");

		FileMetaData targetMetadata = new FileMetaData(FileMetaDataKind.DATA, "text/plain",
				(long) newContent.length, "target.bin");
		targetMetadata.setUuid("target-key");
		targetMetadata.setBucketUuid("test-bucket");
		byte[] result;
		try (InputStream in = store.get(targetMetadata).openStream()) {
			result = ByteStreams.toByteArray(in);
		}
		assertArrayEquals(newContent, result);
	}

	@Test
	void genericAtomicReplaceFailsWhenSourceIsMissing() {
		store = new TransientJcloudStore("test-bucket");

		assertThrows(Exception.class, () -> store.atomicReplace("test-bucket", "no-such-source", "target-key"));
	}

	/** Minimal concrete subclass wiring the offline jclouds "transient" provider, for testing the base class's generic path directly. */
	private static final class TransientJcloudStore extends AbstractJcloudFileDataStoreImpl {

		TransientJcloudStore(String bucketIdentifier) {
			this.bucketIdentifier = bucketIdentifier;
			this.context = ContextBuilder.newBuilder("transient").buildView(BlobStoreContext.class);
			createContainerIfNotExist(context.getBlobStore());
		}
	}
}
