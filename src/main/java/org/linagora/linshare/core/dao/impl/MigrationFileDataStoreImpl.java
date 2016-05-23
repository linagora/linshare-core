/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
import java.io.InputStream;

import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.objects.FileMetaData;

public class MigrationFileDataStoreImpl implements FileDataStore {

	private FileDataStore newDataStore;

	private FileDataStore oldDataStore;

	public MigrationFileDataStoreImpl(FileDataStore newDataStore, FileDataStore oldDataStore) {
		super();
		this.newDataStore = newDataStore;
		this.oldDataStore = oldDataStore;
	}

	@Override
	public void remove(FileMetaData metadata) {
		if (newDataStore.exists(metadata)) {
			newDataStore.remove(metadata);
		}
		if (oldDataStore.exists(metadata)) {
			oldDataStore.remove(metadata);
		}
	}

	@Override
	public FileMetaData add(File file, FileMetaData metadata) {
		return newDataStore.add(file, metadata);
	}

	@Override
	public FileMetaData add(InputStream inputStream, FileMetaData metadata) {
		return newDataStore.add(inputStream, metadata);
	}

	@Override
	public InputStream get(FileMetaData metadata) {
		InputStream inputStream = null;
		if (newDataStore.exists(metadata)) {
			inputStream = newDataStore.get(metadata);
		} else {
			inputStream = oldDataStore.get(metadata);
		}
		return inputStream;
	}

	@Override
	public boolean exists(FileMetaData metadata) {
		if (newDataStore.exists(metadata)) {
			return true;
		} else {
			return oldDataStore.exists(metadata);
		}
	}

}
