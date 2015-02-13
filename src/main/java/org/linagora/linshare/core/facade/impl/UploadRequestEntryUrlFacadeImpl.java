package org.linagora.linshare.core.facade.impl;

import java.io.InputStream;

import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.UploadRequestEntryUrlFacade;
import org.linagora.linshare.core.service.UploadRequestEntryUrlService;

public class UploadRequestEntryUrlFacadeImpl implements
		UploadRequestEntryUrlFacade {

	private final UploadRequestEntryUrlService uploadRequestEntryUrlService;

	public UploadRequestEntryUrlFacadeImpl(
			final UploadRequestEntryUrlService uploadRequestEntryUrlService) {
		this.uploadRequestEntryUrlService = uploadRequestEntryUrlService;
	}

	@Override
	public boolean exists(String uuid, String path) {
		return uploadRequestEntryUrlService.exists(uuid, path);
	}

	@Override
	public boolean isValid(String uuid, String password) {
		return uploadRequestEntryUrlService.isValid(uuid, password);
	}

	@Override
	public DocumentVo getDocument(String uploadRequestUrlUuid, String password)
			throws BusinessException {
		UploadRequestEntry uploadRequestEntry = uploadRequestEntryUrlService
				.getUploadRequestEntry(uploadRequestUrlUuid, password);
		DocumentVo document = new DocumentVo(uploadRequestEntry);
		return document;
	}

	@Override
	public boolean isPasswordProtected(String uuid) throws BusinessException {
		return uploadRequestEntryUrlService.isProtectedByPassword(uuid);
	}

	@Override
	public InputStream retrieveFileStream(String uploadRequestEntryUrlUuid,
			String uploadRequestEntryUuid, String password)
			throws BusinessException {
		return uploadRequestEntryUrlService.retrieveFileStream(
				uploadRequestEntryUrlUuid, uploadRequestEntryUuid, password);
	}
}
