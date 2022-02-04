/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2020-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.dao.impl;

import static org.jclouds.blobstore.options.PutOptions.Builder.multipart;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import org.apache.tika.mime.MediaType;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder.PayloadBlobBuilder;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.linagora.linshare.core.dao.JcloudObjectStorageFileDataStore;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;


public abstract class AbstractJcloudFileDataStoreImpl implements JcloudObjectStorageFileDataStore, Closeable {

	protected static final Logger logger = LoggerFactory.getLogger(AbstractJcloudFileDataStoreImpl.class);

	protected String bucketIdentifier;
	protected String regionId = null;
	protected BlobStoreContext context;
	protected boolean multipartUpload = false;

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
	public void close() throws IOException {
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
	public FileMetaData add(ByteSource byteSource, FileMetaData metadata) throws IOException {
		String seq = "sequence-" + UUID.randomUUID().toString();
		logger.info("{}:uploading file using jcloud ...", seq);
		BlobStore blobStore = getBlobStore(bucketIdentifier);
		Date start = null;

		// Create a Blob
		start = new Date();

		Payload payload = Payloads.newByteSourcePayload(byteSource);
		stats(start, "Payload");

		start = new Date();
		if (metadata.getUuid() == null) {
			metadata.setUuid(UUID.randomUUID().toString());
		}
		metadata.setBucketUuid(bucketIdentifier);
		logger.debug("{}:metadata: {}", seq, metadata);
		PayloadBlobBuilder pbb = blobStore.blobBuilder(metadata.getUuid())
				.payload(payload)
				.contentLength(metadata.getSize());
		if (multipartUpload) {
			pbb.contentDisposition(metadata.getUuid());
			pbb.contentType(MediaType.OCTET_STREAM.toString());
		}
		Blob blob = pbb.build();
		logger.debug("{}:blob: {}", seq, blob);
		stats(start, "blob");

		// Upload the Blob
		start = new Date();
		String eTag = "";
		if (multipartUpload) {
			eTag = blobStore.putBlob(bucketIdentifier, blob, multipart());
		} else {
			eTag = blobStore.putBlob(bucketIdentifier, blob);
		}
		logger.debug("{}:etag : {}", seq, eTag);
		stats(start, "putBlob");
		logger.info("{}:file uploaded using jcloud.", seq);
		return metadata;
	}

	@Override
	public ByteSource get(FileMetaData metadata) {
		String containerName = metadata.getBucketUuid();
		if (containerName == null) {
			logger.error("document's BucketUuid can not be null.");
			throw new TechnicalException(TechnicalErrorCode.MISSING_FILEDATASTORE_BUCKET, "document's BucketUuid can not be null.");
		}
		return new ByteSource() {
			@Override
			public InputStream openStream() throws IOException {
				Blob blob = getBlobStore(containerName).getBlob(containerName, metadata.getUuid());
				return blob.getPayload().openStream();
			}
		};
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