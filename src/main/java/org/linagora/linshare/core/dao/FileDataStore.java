package org.linagora.linshare.core.dao;

import java.io.File;
import java.io.InputStream;

import org.linagora.linshare.core.domain.objects.FileMetaData;

public interface FileDataStore {

	void remove(FileMetaData metadata);

	FileMetaData add(File file, FileMetaData metadata);

	FileMetaData add(InputStream inputStream, FileMetaData metadata);

	InputStream get(FileMetaData metadata);

	boolean exists(FileMetaData metadata);

}
