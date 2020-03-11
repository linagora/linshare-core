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
