/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2020-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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

import org.apache.commons.lang3.Validate;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;

import com.google.inject.Module;

public class FileSystemJcloudFileDataStoreImpl extends AbstractJcloudFileDataStoreImpl {

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

	@Override
	public String toString() {
		return "FileSystemJcloudFileDataStoreImpl [baseDirectory=" + baseDirectory + "]";
	}
}
