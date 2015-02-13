package org.linagora.linshare.core.service;

import java.io.InputStream;

import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestEntryUrl;
import org.linagora.linshare.core.exception.BusinessException;

public interface UploadRequestEntryUrlService {

	UploadRequestEntryUrl find(String uuid, String password)
			throws BusinessException;

	UploadRequestEntryUrl create(UploadRequestEntry requestEntry,
			boolean secured) throws BusinessException;

	void deleteUploadRequestEntryUrl(UploadRequestEntryUrl url)
			throws BusinessException;

	boolean exists(String uuid, String urlPath);

	boolean isProtectedByPassword(String uuid)
			throws BusinessException;

	boolean isValid(String uuid, String password)
			throws BusinessException;

	InputStream retrieveFileStream(String uploadRequestEntryUrlUuid,
			String uploadRequestEntryUuid, String password)
			throws BusinessException;

	UploadRequestEntry getUploadRequestEntry(String uploadRequestUrlUuid,
			String password) throws BusinessException;
}
