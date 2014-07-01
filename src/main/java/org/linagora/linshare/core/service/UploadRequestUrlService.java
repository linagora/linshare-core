package org.linagora.linshare.core.service;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.exception.BusinessException;

public interface UploadRequestUrlService {

	UploadRequestUrl find(String uuid) throws BusinessException;

	UploadRequestUrl close(String uuid);

	UploadRequestEntry findRequestEntryByUuid(Account actor, String uuid);

	UploadRequestEntry createRequestEntry(Account actor, UploadRequestEntry entry)
			throws BusinessException;

	UploadRequestEntry updateRequestEntry(Account actor, UploadRequestEntry entry)
			throws BusinessException;

	void deleteRequestEntry(Account actor, UploadRequestEntry entry)
			throws BusinessException;
}
