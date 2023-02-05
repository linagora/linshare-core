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

import org.apache.commons.lang3.Validate;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;

public class DefaultJcloudFileDataStoreImpl extends AbstractJcloudFileDataStoreImpl {

	public DefaultJcloudFileDataStoreImpl(ContextBuilder contextBuilder, Properties properties, String bucketIdentifier, boolean multipartUpload) {
		Validate.notEmpty(bucketIdentifier, "Missing bucket identifier");
		Validate.notNull(properties, "Missing properties");
		this.bucketIdentifier = bucketIdentifier;
		this.multipartUpload = multipartUpload;
		contextBuilder.overrides(properties);
		context = contextBuilder.buildView(BlobStoreContext.class);
		BlobStore blobStore = context.getBlobStore();
		createContainerIfNotExist(blobStore);
	}

	@Override
	public String toString() {
		return "DefaultJcloudFileDataStoreImpl []";
	}

}
