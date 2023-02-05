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

import java.io.IOException;

import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.objects.FileMetaData;

import com.google.common.io.ByteSource;

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
	public FileMetaData add(ByteSource byteSource, FileMetaData metadata) throws IOException {
		if (!newDataStore.exists(metadata)) {
			return newDataStore.add(byteSource, metadata);
		}
		return metadata;
	}

	@Override
	public ByteSource get(FileMetaData metadata) {
		if (newDataStore.exists(metadata)) {
			return newDataStore.get(metadata);
		} else {
			return oldDataStore.get(metadata);
		}
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
