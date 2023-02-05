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
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.objects.FileMetaData;

import com.google.common.io.ByteSource;

public class DataKindBalancerFileDataStoreImpl implements FileDataStore {

	private FileDataStore bigFilesDataStore;

	private FileDataStore smallFilesDataStore;

	public DataKindBalancerFileDataStoreImpl(FileDataStore bigFilesDataStore, FileDataStore smallFilesDataStore) {
		super();
		this.bigFilesDataStore = bigFilesDataStore;
		this.smallFilesDataStore = smallFilesDataStore;
	}
	
	@Override
	public void remove(FileMetaData metadata) {
		if (metadata.getKind().equals(FileMetaDataKind.DATA)) {
			bigFilesDataStore.remove(metadata);
		} else {
			smallFilesDataStore.remove(metadata);
		}
	}

	@Override
	public FileMetaData add(ByteSource byteSource, FileMetaData metadata) throws IOException {
		if (metadata.getKind().equals(FileMetaDataKind.DATA)) {
			return bigFilesDataStore.add(byteSource, metadata);
		} else {
			return smallFilesDataStore.add(byteSource, metadata);
		}
	}

	@Override
	public ByteSource get(FileMetaData metadata) {
		if (metadata.getKind().equals(FileMetaDataKind.DATA)) {
			return bigFilesDataStore.get(metadata);
		} else {
			return smallFilesDataStore.get(metadata);
		}
	}

	@Override
	public boolean exists(FileMetaData metadata) {
		if (metadata.getKind().equals(FileMetaDataKind.DATA)) {
			return bigFilesDataStore.exists(metadata);
		} else {
			return smallFilesDataStore.exists(metadata);
		}
	}

}
