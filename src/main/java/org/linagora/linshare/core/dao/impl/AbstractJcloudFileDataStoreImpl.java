/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.dao.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.linagora.linshare.core.dao.JcloudObjectStorageFileDataStore;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v1.util.ClosableResource;

public abstract class AbstractJcloudFileDataStoreImpl implements JcloudObjectStorageFileDataStore, ClosableResource {

	protected static final Logger logger = LoggerFactory.getLogger(AbstractJcloudFileDataStoreImpl.class);

	protected String bucketIdentifier;
	protected String regionId = null;
	protected BlobStoreContext context;

	@Override
	public BlobStore getBlobStore(String containerName) {
		BlobStore blobStore = context.getBlobStore();
		createContainerIfNotExist(blobStore);
		return blobStore;
	}

	@Override
	public void createContainerIfNotExist(BlobStore blobStore) {
		if (!blobStore.containerExists(bucketIdentifier)) {
			logger.info("creation of a new bucket {} without locale.", bucketIdentifier);
			blobStore.createContainerInLocation(null, bucketIdentifier);
		}
	}

	@Override
	public void close() throws Exception {
		logger.debug("Closing current context.");
		if (context != null)
			context.close();
	}

	/**
	 * Ugly statistics and debug function :)
	 */
	public static void stats(Date start, String title) {
		if (logger.isDebugEnabled()) {
			logger.debug("diff : " + title + " : " + String.valueOf(new Date().getTime() - start.getTime()));
		}
	}

	@Override
	public void remove(FileMetaData metadata) {
		String containerName = metadata.getBucketUuid();
		if (containerName == null) {
			logger.error("Can not remove file because bucket is null : {}", metadata);
			return;
		}
		BlobStore blobStore = getBlobStore(containerName);
		Date start = new Date();
		try {
			blobStore.removeBlob(containerName, metadata.getUuid());
		} catch (org.jclouds.blobstore.ContainerNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
		stats(start, "blobRetrieved");
	}

	@Override
	public FileMetaData add(File file, FileMetaData metadata) {
		try (FileInputStream fis = new FileInputStream(file)) {
			return metadata = add(fis, metadata);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new TechnicalException(TechnicalErrorCode.GENERIC, "Can not add a new file : " + e.getMessage());
		}
	}

	@Override
	public FileMetaData add(InputStream file, FileMetaData metadata) {
		BlobStore blobStore = getBlobStore(bucketIdentifier);
		Date start = null;

		// Create a Blob
		start = new Date();
		Payload payload = Payloads.newInputStreamPayload(file);
		stats(start, "Payload");

		start = new Date();
		Blob blob;
		if (metadata.getUuid() == null) {
			metadata.setUuid(UUID.randomUUID().toString());
		}
		metadata.setBucketUuid(bucketIdentifier);
		blob = blobStore.blobBuilder(metadata.getUuid()).payload(payload)
				.contentLength(metadata.getSize())
				.build();
		stats(start, "blob");

		// Upload the Blob
		start = new Date();

		String eTag = blobStore.putBlob(bucketIdentifier, blob);
		logger.debug("etag : {}", eTag);
		stats(start, "putBlob");
		return metadata;
	}

	@Override
	public InputStream get(FileMetaData metadata) {
		String containerName = metadata.getBucketUuid();
		if (containerName == null) {
			logger.error("document's BucketUuid can not be null.");
			throw new TechnicalException(TechnicalErrorCode.MISSING_FILEDATASTORE_BUCKET, "document's BucketUuid can not be null.");
		}
		BlobStore blobStore = getBlobStore(containerName);
		Date start = new Date();
		Blob blobRetrieved = blobStore.getBlob(containerName, metadata.getUuid());
		stats(start, "blobRetrieved");
		try {
			return blobRetrieved.getPayload().openStream();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new TechnicalException(TechnicalErrorCode.GENERIC, "Can not get a file : " + e.getMessage());
		}
	}

	@Override
	public boolean exists(FileMetaData metadata) {
		String containerName = metadata.getBucketUuid();
		if (containerName == null) {
			return false;
		}
		BlobStore blobStore = getBlobStore(containerName);
		return blobStore.blobExists(containerName, metadata.getUuid());
	}

	public String getBucketIdentifier() {
		return bucketIdentifier;
	}

	public String getRegionId() {
		return regionId;
	}
}