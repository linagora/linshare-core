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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

import org.apache.commons.lang3.Validate;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.linagora.linshare.core.dao.AtomicBlobReplace;

import com.google.inject.Module;

public class FileSystemJcloudFileDataStoreImpl extends AbstractJcloudFileDataStoreImpl implements AtomicBlobReplace {

	protected static String PROVIDER = "filesystem";

	protected String baseDirectory;

	public FileSystemJcloudFileDataStoreImpl(Iterable<Module> modules, Properties properties,
			String bucketIdentifier, String baseDirectory) {
		Validate.notEmpty(bucketIdentifier, "Missing bucket identifier");
		Validate.notEmpty(baseDirectory, "Missing base directory");
		Validate.notNull(properties, "Missing properties");
		this.bucketIdentifier = bucketIdentifier;
		this.baseDirectory = baseDirectory;
		properties.setProperty(org.jclouds.filesystem.reference.FilesystemConstants.PROPERTY_BASEDIR, baseDirectory);
		ContextBuilder contextBuilder = ContextBuilder.newBuilder(PROVIDER)
			.modules(modules)
			.overrides(properties);
		context = contextBuilder.buildView(BlobStoreContext.class);
		BlobStore blobStore = context.getBlobStore();
		createContainerIfNotExist(blobStore);
	}

	public String getBaseDirectory() {
		return baseDirectory;
	}

	/**
	 * Backs onto {@code java.nio.file.Files.move(..., ATOMIC_MOVE)} — a real
	 * filesystem rename, since both blobs live under the same
	 * {@code baseDirectory}/{@code container} directory and therefore the
	 * same filesystem/mount. Mirrors exactly how jclouds' own
	 * {@code FilesystemStorageStrategyImpl} computes a blob's on-disk path
	 * (baseDirectory/container/key, no further nesting or key transformation).
	 */
	@Override
	public void atomicReplace(String container, String sourceKey, String targetKey) throws IOException {
		Path source = resolveBlobPath(container, sourceKey);
		Path target = resolveBlobPath(container, targetKey);
		Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
	}

	private Path resolveBlobPath(String container, String key) {
		validatePathComponent(container);
		validatePathComponent(key);
		return Paths.get(baseDirectory, container, key);
	}

	private static void validatePathComponent(String value) {
		if (value == null || value.isEmpty() || value.contains("/") || value.contains("\\")
				|| value.contains("..")) {
			throw new IllegalArgumentException("Invalid blob container/key component: " + value);
		}
	}

	@Override
	public String toString() {
		return "FileSystemJcloudFileDataStoreImpl [baseDirectory=" + baseDirectory + "]";
	}
}
