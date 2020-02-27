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

import java.util.Properties;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.domain.Location;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.swift.v1.blobstore.RegionScopedBlobStoreContext;
import org.jsoup.helper.Validate;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

public class OpenStackSwiftJcloudFileDataStoreImpl extends AbstractJcloudFileDataStoreImpl {

	public OpenStackSwiftJcloudFileDataStoreImpl(String provider, String bucketIdentifier, String identity, String credential,
			String endpoint, String regionId) {
		super(bucketIdentifier, identity, credential, endpoint);
		Validate.notEmpty(regionId, "Missing regionId");
		Validate.notEmpty(provider, "Missing provider");
		this.regionId = regionId;
		Iterable<Module> modules = ImmutableSet.<Module> of(new SLF4JLoggingModule());
		Properties properties = new Properties();
		properties.setProperty(org.jclouds.Constants.PROPERTY_TRUST_ALL_CERTS, "true");
		properties.setProperty(org.jclouds.Constants.PROPERTY_LOGGER_WIRE_LOG_SENSITIVE_INFO, "true");
		ContextBuilder contextBuilder = ContextBuilder.newBuilder(provider);
		contextBuilder.endpoint(endpoint)
						.credentials(identity, credential)
						.modules(modules)
						.overrides(properties);
		BlobStore blobStore = null;
		context = contextBuilder.buildView(RegionScopedBlobStoreContext.class);
		blobStore = ((RegionScopedBlobStoreContext) context).getBlobStore(regionId);
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
