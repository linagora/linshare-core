package org.linagora.linshare.core.dao.jackRabbit;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.jcr.NodeIterator;

import org.linagora.linshare.core.dao.FileSystemDao;
import org.linagora.linshare.core.domain.objects.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JackRabbitFileSystemMock implements FileSystemDao {

	final private static Logger logger = LoggerFactory
			.getLogger(JackRabbitFileSystemMock.class);

	@Override
	public void removeFileByUUID(String uuid) {
		logger.debug("mock method");
	}

	@Override
	public String insertFile(String path, InputStream file, long size,
			String fileName, String mimeType) {
		logger.debug("mock method");
		return null;
	}

	@Override
	public InputStream getContentFile(String path) {
		logger.debug("mock method");
		return null;
	}

	@Override
	public List<String> getAllPath() {
		logger.debug("mock method");
		return null;
	}

	@Override
	public List<String> getAllSubPath(String path) {
		logger.debug("mock method");
		return null;
	}

	@Override
	public List<FileInfo> getAllFilePathInSubPath(String path) {
		logger.debug("mock method");
		return null;
	}

	@Override
	public FileInfo getFileInfoByUUID(String uuid) {
		logger.debug("mock method");
		return null;
	}

	@Override
	public InputStream getFileContentByUUID(String uuid) {
		logger.debug("mock method");
		return null;
	}

	@Override
	public Map<String, NodeIterator> executeXPathQuery(List<String> statements) {
		logger.debug("mock method");
		return null;
	}

	@Override
	public Map<String, NodeIterator> executeSqlQuery(List<String> statements) {
		logger.debug("mock method");
		return null;
	}

	@Override
	public void renameFile(String uuid, String newName) {
		logger.debug("mock method");
	}

}
