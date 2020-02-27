package org.linagora.linshare.core.dao;

import org.jclouds.blobstore.BlobStore;

public interface JcloudObjectStorageFileDataStore extends FileDataStore {

	void createContainerIfNotExist(BlobStore blobStore);

	BlobStore getBlobStore(String containerName);

}
