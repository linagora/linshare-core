package org.linagora.linshare.core.service;

import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.exception.BusinessException;

public interface UploadRequestUrlService {

	UploadRequestUrl find(String uuid) throws BusinessException;

	UploadRequestUrl close(String uuid);

}
