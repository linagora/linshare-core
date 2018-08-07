/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.domain.Location;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.swift.v1.blobstore.RegionScopedBlobStoreContext;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

public class JcloudObjectStorageFileDataStoreImpl implements FileDataStore {
	protected static final Logger logger = LoggerFactory.getLogger(JcloudObjectStorageFileDataStoreImpl.class);
	// Do not support object storage region
	protected static String SWIFT_KEYSTONE = "swift-keystone";
	// Do support object storage region
	protected static String OPENSTACK_SWIFT = "openstack-swift";
	protected static String S3 = "s3";
	protected static String FILESYSTEM = "filesystem";
	protected String provider;
	protected List<String> supportedProviders;

	protected BlobStoreContext context;
	protected String baseDirectory;
	protected String identity;
	protected String credential;
	protected String endpoint;
	protected String regionId;
	protected String bucketIdentifier;

	public JcloudObjectStorageFileDataStoreImpl(String provider, String supportedProviders) {
		super();
		this.provider = provider;
		this.context = null;
		this.baseDirectory = null;
		this.identity = null;
		this.credential = null;
		this.endpoint = null;
		this.regionId = null;
		this.bucketIdentifier = null;
		this.supportedProviders = Arrays.asList(supportedProviders.split(","));
	}

	public void afterPropertiesSet() throws Exception {
		if (!this.supportedProviders.contains(provider)) {
			throw new IllegalArgumentException("Supported providers: " + this.supportedProviders.toString());
		}
		Properties properties = new Properties();
		ContextBuilder contextBuilder = null;
		BlobStore blobStore = null;
		Iterable<Module> modules = ImmutableSet.<Module> of(new SLF4JLoggingModule());
		if (provider.equals(FILESYSTEM)) {
			properties.setProperty(org.jclouds.filesystem.reference.FilesystemConstants.PROPERTY_BASEDIR,
					baseDirectory);
			contextBuilder = ContextBuilder.newBuilder(FILESYSTEM);
			contextBuilder.overrides(properties).modules(modules);
			context = contextBuilder.buildView(BlobStoreContext.class);
			blobStore = context.getBlobStore();
		} else {
			properties.setProperty(org.jclouds.Constants.PROPERTY_TRUST_ALL_CERTS, "true");
			properties.setProperty(org.jclouds.Constants.PROPERTY_LOGGER_WIRE_LOG_SENSITIVE_INFO, "true");
			contextBuilder = ContextBuilder.newBuilder(provider);
			contextBuilder.endpoint(endpoint)
							.credentials(identity, credential)
							.modules(modules)
							.overrides(properties);
			if (provider.equals(OPENSTACK_SWIFT)) {
				context = contextBuilder.buildView(RegionScopedBlobStoreContext.class);
				blobStore = ((RegionScopedBlobStoreContext) context).getBlobStore(regionId);
			} else {
				// We force region to null because regionId is not supported yet by the default BlobStoreContext.
				regionId = null;
				context = contextBuilder.buildView(BlobStoreContext.class);
				blobStore = context.getBlobStore();
			}
		}
		createContainerIfNotExist(blobStore);
	}

	protected void createContainerIfNotExist(BlobStore blobStore) {
		if (!blobStore.containerExists(bucketIdentifier)) {
			if (provider.equals(OPENSTACK_SWIFT)) {
				Location location = getLocation(blobStore, regionId);
				logger.info("creation of a new bucket {} with locale {}.", bucketIdentifier, location);
				blobStore.createContainerInLocation(location, bucketIdentifier);
			} else {
				logger.info("creation of a new bucket {} without locale.", bucketIdentifier);
				blobStore.createContainerInLocation(null, bucketIdentifier);
			}
		}
	}

	protected Location getLocation(BlobStore blobStore, String locationId) {
		Location location = null;
		if (locationId != null) {
			if (!locationId.isEmpty()) {
				Set<? extends Location> listAssignableLocations = blobStore.listAssignableLocations();
				logger.info("available locations : {}", listAssignableLocations);
				for (Location loc : listAssignableLocations) {
					if (loc.getId().equalsIgnoreCase(locationId)) {
						location = loc;
						break;
					}
				}
				if (location == null) {
					throw new IllegalArgumentException("unknown location: " + locationId);
				}
			}
		}
		return location;
	}

	public void destroy() {
		if (context != null)
			context.close();
	}

	public BlobStore getBlobStore(String containerName) {
		BlobStore blobStore = null;
		if (provider.equals(OPENSTACK_SWIFT)) {
			blobStore = ((RegionScopedBlobStoreContext) context).getBlobStore(regionId);
		} else {
			blobStore = context.getBlobStore();
		}
		createContainerIfNotExist(blobStore);
		return blobStore;
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
			// document stored in jackrabbit does not have bucket_uuid. :)
			return false;
		}
		BlobStore blobStore = getBlobStore(containerName);
		return blobStore.blobExists(containerName, metadata.getUuid());
	}

	public String getBaseDirectory() {
		return baseDirectory;
	}

	public void setBaseDirectory(String baseDirectory) {
		this.baseDirectory = baseDirectory;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getBucketIdentifier() {
		return bucketIdentifier;
	}

	public void setBucketIdentifier(String bucketIdentifier) {
		this.bucketIdentifier = bucketIdentifier;
	}

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	@Override
	public String toString() {
		return "JcloudObjectStorageFileDataStoreImpl [provider=" + provider + ", supportedProviders="
				+ supportedProviders + ", baseDirectory=" + baseDirectory + ", endpoint=" + endpoint + ", regionId="
				+ regionId + ", bucketIdentifier=" + bucketIdentifier + "]";
	}

}
