package org.linagora.linshare.core.dao.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.dao.FileSystemDao;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JackRabbitFileDataStoreImpl implements FileDataStore {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String jackRabbitDefaultPath = "ee0e115f-240e-428b-8f5c-427184cf67b1";

	private FileSystemDao dao;

	public JackRabbitFileDataStoreImpl(FileSystemDao dao) {
		super();
		this.dao = dao;
	}

	@Override
	public void remove(FileMetaData metadata) {
		Validate.notNull(metadata);
		String uuid = metadata.getUuid();
		try {
			dao.removeFileByUUID(uuid);
		} catch (org.springmodules.jcr.JcrSystemException e1) {
			logger.warn(e1.getMessage(), e1);
		} catch (org.springframework.dao.DataRetrievalFailureException e) {
			logger.warn(e.getMessage(), e);
		}
	}

	@Override
	public FileMetaData add(File file, FileMetaData metadata) {
		Validate.notNull(metadata);
		String mimeType = metadata.getMimeType();
		Long size = metadata.getSize();
		Validate.notEmpty(mimeType);
		Validate.notNull(size);
		try (FileInputStream fis = new FileInputStream(file)) {
			String uuid = dao.insertFile(jackRabbitDefaultPath, fis, size, UUID.randomUUID().toString(), mimeType);
			metadata.setUuid(uuid);
			return metadata;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new TechnicalException(TechnicalErrorCode.GENERIC, "Can not add a new file : " + e.getMessage());
		}
	}

	@Override
	public FileMetaData add(InputStream file, FileMetaData metadata) {
		String mimeType = metadata.getMimeType();
		Long size = metadata.getSize();
		Validate.notEmpty(mimeType);
		Validate.notNull(size);
		String uuid = dao.insertFile(jackRabbitDefaultPath, file, size, UUID.randomUUID().toString(), mimeType);
		metadata.setUuid(uuid);
		return metadata;
	}

	@Override
	public InputStream get(FileMetaData metadata) {
		Validate.notEmpty(metadata.getUuid());
		InputStream inputStream = dao.getFileContentByUUID(metadata.getUuid());
		if (inputStream == null) {
			throw new TechnicalException(TechnicalErrorCode.GENERIC,
					"Can not find document in jackrabbit : " + metadata.getUuid());
		}
		return inputStream;
	}

	@Override
	public boolean exists(FileMetaData metadata) {
		try (InputStream is = dao.getFileContentByUUID(metadata.getUuid())) {
			if (is != null) {
				return true;
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}

}
