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

import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.domain.Location;
import org.jclouds.openstack.keystone.config.KeystoneProperties;
import org.jclouds.openstack.swift.v1.blobstore.RegionScopedBlobStoreContext;

public class OpenStackSwiftJcloudFileDataStoreImpl extends AbstractJcloudFileDataStoreImpl {

	protected String apiVersion;
	protected String projectName;

	public OpenStackSwiftJcloudFileDataStoreImpl(ContextBuilder contextBuilder, Properties properties,
			String bucketIdentifier, String regionId, String apiVersion, String projectName, boolean multipartUpload) {
		Validate.notNull(contextBuilder, "Missing contextBuilder");
		Validate.notNull(properties, "Missing properties");
		Validate.notEmpty(bucketIdentifier, "Missing bucket identifier");
		Validate.notEmpty(regionId, "Missing regionId");
		Validate.notEmpty(apiVersion, "Missing apiVersion");
		this.bucketIdentifier = bucketIdentifier;
		this.multipartUpload = multipartUpload;
		this.regionId = regionId;
		this.apiVersion = apiVersion;
		this.projectName = projectName;
		if (Integer.valueOf(apiVersion) != 2) {
			properties.put(KeystoneProperties.KEYSTONE_VERSION, apiVersion);
			Validate.notEmpty(projectName, "Missing project name");
			properties.put(KeystoneProperties.SCOPE, "project:" + projectName);
		}
		contextBuilder.overrides(properties);
		context = contextBuilder.buildView(RegionScopedBlobStoreContext.class);
		BlobStore blobStore = ((RegionScopedBlobStoreContext) context).getBlobStore(regionId);
		createContainerIfNotExist(blobStore);
	}

	@Override
	public void createContainerIfNotExist(BlobStore blobStore) {
		if (!blobStore.containerExists(bucketIdentifier)) {
			Location location = getLocation(blobStore, regionId);
			logger.info("creation of a new bucket {} with locale {}.", bucketIdentifier, location);
			blobStore.createContainerInLocation(location, bucketIdentifier);
		}
	}

	public Location getLocation(BlobStore blobStore, String locationId) {
		Location location = null;
		if (locationId != null && !locationId.isEmpty()) {
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
		return location;
	}

	@Override
	public BlobStore getBlobStore(String containerName) {
		BlobStore blobStore = ((RegionScopedBlobStoreContext) context).getBlobStore(regionId);
		createContainerIfNotExist(blobStore);
		return blobStore;
	}

	@Override
	public String toString() {
		return "OpenStackSwiftJcloudFileDataStoreImpl []";
	}

}
