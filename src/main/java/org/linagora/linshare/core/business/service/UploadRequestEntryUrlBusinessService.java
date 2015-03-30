package org.linagora.linshare.core.business.service;

import java.util.Date;

import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestEntryUrl;
import org.linagora.linshare.core.exception.BusinessException;

public interface UploadRequestEntryUrlBusinessService {

	UploadRequestEntryUrl findByUuid(String uuid) throws BusinessException;

	UploadRequestEntryUrl find(UploadRequestEntry entry) throws BusinessException;

	UploadRequestEntryUrl create(UploadRequestEntry url,
			Boolean passwordProtected, Date expirtyDate) throws BusinessException;

	UploadRequestEntryUrl update(UploadRequestEntryUrl url)
			throws BusinessException;

	void delete(UploadRequestEntryUrl url) throws BusinessException;

	boolean isValidPassword(UploadRequestEntryUrl uploadRequestEntryUrl,
			String password) throws BusinessException;

	boolean isExpired(UploadRequestEntryUrl uploadRequestEntryUrl);

	SystemAccount getUploadRequestEntryURLAccount();
}
