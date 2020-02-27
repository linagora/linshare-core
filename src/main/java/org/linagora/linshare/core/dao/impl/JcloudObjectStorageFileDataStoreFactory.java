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

import java.util.Arrays;
import java.util.List;

import org.jclouds.blobstore.BlobStoreContext;
import org.linagora.linshare.core.dao.JcloudObjectStorageFileDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JcloudObjectStorageFileDataStoreFactory {

	protected static final Logger logger = LoggerFactory.getLogger(JcloudObjectStorageFileDataStoreFactory.class);
	protected static String FILESYSTEM = "filesystem";
	protected static String OPENSTACK_SWIFT = "openstack-swift";
	// Do not support object storage region
	protected static String SWIFT_KEYSTONE = "swift-keystone";
	protected static String S3 = "s3";

	protected String provider;
	protected String supportedProviders;
	protected List<String> supportedProvidersList;
	protected BlobStoreContext context;
	protected String baseDirectory;
	protected String identity;
	protected String credential;
	protected String endpoint;
	protected String regionId;
	protected String bucketIdentifier;

	public JcloudObjectStorageFileDataStore getDefault() {
		this.supportedProvidersList = Arrays.asList(supportedProviders.split(","));
		logger.debug("Defined provider: {}", provider);
		if (!this.supportedProviders.contains(provider)) {
			throw new IllegalArgumentException("Supported providers: " + this.supportedProviders.toString());
		}
		if (provider.equals(FILESYSTEM)) {
			return new FileSystemJcloudFileDataStoreImpl(provider, baseDirectory, bucketIdentifier);
		} else if (provider.equals(OPENSTACK_SWIFT)) {
			return new OpenStackSwiftJcloudFileDataStoreImpl(provider, bucketIdentifier, identity, credential, endpoint, regionId);
		} else {
			return new DefaultJcloudFileDataStoreImpl(provider, bucketIdentifier, identity, credential, endpoint);
		}
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getSupportedProviders() {
		return supportedProviders;
	}

	public void setSupportedProviders(String supportedProviders) {
		this.supportedProviders = supportedProviders;
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

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	public String getBucketIdentifier() {
		return bucketIdentifier;
	}

	public void setBucketIdentifier(String bucketIdentifier) {
		this.bucketIdentifier = bucketIdentifier;
	}
}
