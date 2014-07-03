package org.linagora.linshare.core.facade.webservice.uploadrequest.impl;

import java.io.InputStream;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.MimeType;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.uploadrequest.UploadRequestUrlFacade;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.EntryDto;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.UploadRequestDto;
import org.linagora.linshare.core.service.MimePolicyService;
import org.linagora.linshare.core.service.UploadRequestUrlService;

public class UploadRequestUrlFacadeImpl implements UploadRequestUrlFacade {

	private final UploadRequestUrlService uploadRequestUrlService;

	private final MimePolicyService mimePolicyService;


	public UploadRequestUrlFacadeImpl(
			final UploadRequestUrlService uploadRequestUrlService,
			final MimePolicyService mimePolicyService) {
		this.uploadRequestUrlService = uploadRequestUrlService;
		this.mimePolicyService = mimePolicyService;
	}

	@Override
	public UploadRequestDto find(String uploadRequestUrlUuid, String password) throws BusinessException {
		Validate.notEmpty(uploadRequestUrlUuid);
		UploadRequestUrl requestUrl = uploadRequestUrlService.find(uploadRequestUrlUuid, password);
		UploadRequestDto dto = transform(requestUrl);
		return dto;
	}

	@Override
	public UploadRequestDto close(UploadRequestDto req, String password)
			throws BusinessException {
		Validate.notNull(req);
		Validate.notEmpty(req.getUuid());
		return transform(uploadRequestUrlService.close(req.getUuid(), password));
	}

	@Override
	public void addUploadRequestEntry(String uploadRequestUrlUuid,
			String password, InputStream fi, String fileName) throws BusinessException {
		Validate.notEmpty(uploadRequestUrlUuid);
		Validate.notNull(fi);
		Validate.notEmpty(fileName);
		uploadRequestUrlService.createUploadRequestEntry(uploadRequestUrlUuid, fi, fileName, password);
	}

	@Override
	public void deleteUploadRequestEntry(String uploadRequestUrlUuid,
			String password, EntryDto entry) throws BusinessException {
		Validate.notEmpty(uploadRequestUrlUuid);
		Validate.notNull(entry);
		Validate.notEmpty(entry.getUuid());
		uploadRequestUrlService.deleteUploadRequestEntry(uploadRequestUrlUuid, password, entry.getUuid());
	}

	@Override
	public void deleteUploadRequestEntry(String uploadRequestUrlUuid,
			String password, String entryUuid) throws BusinessException {
		Validate.notEmpty(uploadRequestUrlUuid);
		Validate.notEmpty(entryUuid);
		uploadRequestUrlService.deleteUploadRequestEntry(uploadRequestUrlUuid, password, entryUuid);
	}

	/*
	 * Helpers
	 */

	private UploadRequestDto transform(UploadRequestUrl requestUrl) throws BusinessException {
		if (requestUrl==null)
			return null;
		UploadRequestDto dto = new UploadRequestDto(requestUrl);
		Account owner = requestUrl.getUploadRequest().getOwner();
		Set<MimeType> mimeTypes = mimePolicyService.findAllMyMimeTypes(owner);
		for (MimeType mimeType : mimeTypes) {
			if (mimeType.getEnable()) {
				String extension = mimeType.getExtensions();
				if (!extension.isEmpty()) {
					dto.addExtensions(extension.substring(1));
				}
			}
		}
		return dto;
	}
}
