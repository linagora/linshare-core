package org.linagora.linshare.core.service.impl;

import org.linagora.linshare.core.business.service.UploadRequestUrlBusinessService;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.UploadRequestUrlService;

public class UploadRequestUrlServiceImpl implements UploadRequestUrlService {

	private final UploadRequestUrlBusinessService uploadRequestUrlBusinessService;

	public UploadRequestUrlServiceImpl(
			final UploadRequestUrlBusinessService uploadRequestUrlBusinessService) {
		super();
		this.uploadRequestUrlBusinessService = uploadRequestUrlBusinessService;
	}

	@Override
	public UploadRequestUrl find(String uuid) throws BusinessException {
		UploadRequestUrl url = uploadRequestUrlBusinessService.findByUuid(uuid);
		if (url == null) {
			throw new BusinessException(BusinessErrorCode.NO_SUCH_ELEMENT, "UploadRequestUrl not found.");
		}
		return url;
	}

	@Override
	public UploadRequestUrl close(String uuid) {
		UploadRequestUrl uploadRequestUrl = uploadRequestUrlBusinessService.findByUuid(uuid);
		// TODO close the upload request.
		return uploadRequestUrl;
	}

}
