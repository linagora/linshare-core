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
