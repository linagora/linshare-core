package org.linagora.linshare.core.dao.impl;

import java.io.File;
import java.io.InputStream;

import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.objects.FileMetaData;

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
	public FileMetaData add(File file, FileMetaData metadata) {
		if (metadata.getKind().equals(FileMetaDataKind.DATA)) {
			return bigFilesDataStore.add(file, metadata);
		} else {
			return smallFilesDataStore.add(file, metadata);
		}
	}

	@Override
	public FileMetaData add(InputStream inputStream, FileMetaData metadata) {
		if (metadata.getKind().equals(FileMetaDataKind.DATA)) {
			return bigFilesDataStore.add(inputStream, metadata);
		} else {
			return smallFilesDataStore.add(inputStream, metadata);
		}
	}

	@Override
	public InputStream get(FileMetaData metadata) {
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
