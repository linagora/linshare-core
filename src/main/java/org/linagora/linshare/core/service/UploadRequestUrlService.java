package org.linagora.linshare.core.service;

import java.io.InputStream;

import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.exception.BusinessException;

public interface UploadRequestUrlService {

	UploadRequestUrl find(String uuid, String password) throws BusinessException;

	UploadRequestUrl close(String uuid, String password) throws BusinessException;

	UploadRequestEntry createUploadRequestEntry(String uploadRequestUrlUuid,
			InputStream fi, String fileName, String password) throws BusinessException;
}
