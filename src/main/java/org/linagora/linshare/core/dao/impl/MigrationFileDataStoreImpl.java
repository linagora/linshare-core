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
