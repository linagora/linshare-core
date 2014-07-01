package org.linagora.linshare.core.service.impl;

import org.linagora.linshare.core.business.service.UploadRequestEntryBusinessService;
import org.linagora.linshare.core.business.service.UploadRequestUrlBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.UploadRequestUrlService;

public class UploadRequestUrlServiceImpl implements UploadRequestUrlService {

	private final UploadRequestUrlBusinessService uploadRequestUrlBusinessService;

	private final UploadRequestEntryBusinessService uploadRequestEntryBusinessService;

	public UploadRequestUrlServiceImpl(
			final UploadRequestUrlBusinessService uploadRequestUrlBusinessService,
		final UploadRequestEntryBusinessService uploadRequestEntryBusinessService) {
		super();
		this.uploadRequestUrlBusinessService = uploadRequestUrlBusinessService;
		this.uploadRequestEntryBusinessService = uploadRequestEntryBusinessService;
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

	@Override
	public UploadRequestEntry findRequestEntryByUuid(Account actor, String uuid) {
		return uploadRequestEntryBusinessService.findByUuid(uuid);
	}

	@Override
	public UploadRequestEntry createRequestEntry(Account actor,
			UploadRequestEntry entry) throws BusinessException {
		return uploadRequestEntryBusinessService.create(entry);
	}

	@Override
	public UploadRequestEntry updateRequestEntry(Account actor,
			UploadRequestEntry entry) throws BusinessException {
		return uploadRequestEntryBusinessService.update(entry);
	}

	@Override
	public void deleteRequestEntry(Account actor, UploadRequestEntry entry)
			throws BusinessException {
		uploadRequestEntryBusinessService.delete(entry);
	}

}
