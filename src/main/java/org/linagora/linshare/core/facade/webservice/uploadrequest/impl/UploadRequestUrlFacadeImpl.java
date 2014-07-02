package org.linagora.linshare.core.facade.webservice.uploadrequest.impl;

import java.io.InputStream;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.MimeType;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.uploadrequest.UploadRequestUrlFacade;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.ContactDto;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.UploadRequestDto;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.MimePolicyService;
import org.linagora.linshare.core.service.UploadRequestUrlService;

public class UploadRequestUrlFacadeImpl implements UploadRequestUrlFacade {

	private final UploadRequestUrlService uploadRequestUrlService;

	private final MimePolicyService mimePolicyService;

	private final DocumentEntryService documentEntryService;

	public UploadRequestUrlFacadeImpl(
			final UploadRequestUrlService uploadRequestUrlService,
			final MimePolicyService mimePolicyService,
			final DocumentEntryService documentEntryService) {
		this.uploadRequestUrlService = uploadRequestUrlService;
		this.mimePolicyService = mimePolicyService;
		this.documentEntryService = documentEntryService;
	}

	@Override
	public UploadRequestDto find(String uploadRequestUrlUuid) throws BusinessException {
		Validate.notEmpty(uploadRequestUrlUuid);
		UploadRequestUrl requestUrl = uploadRequestUrlService.find(uploadRequestUrlUuid);
		UploadRequestDto dto = transform(requestUrl);
		return dto;
	}

	@Override
	public UploadRequestDto close(UploadRequestDto req)
			throws BusinessException {
		Validate.notNull(req);
		Validate.notEmpty(req.getUuid());
		return transform(uploadRequestUrlService.close(req.getUuid()));
	}

	@Override
	public void addUploadRequestEntry(String uploadRequestUrlUuid,
			InputStream fi, String fileName) throws BusinessException {
		Validate.notEmpty(uploadRequestUrlUuid);
		Validate.notNull(fi);
		Validate.notEmpty(fileName);
		uploadRequestUrlService.createUploadRequestEntry(uploadRequestUrlUuid, fi, fileName);
	}

	/**
	 * Helpers
	 */

	private UploadRequestDto transform(UploadRequestUrl requestUrl) throws BusinessException {
		UploadRequest request = requestUrl.getUploadRequest();
		UploadRequestDto dto = new UploadRequestDto(request);
		dto.setRecipient(new ContactDto(requestUrl.getContact()));
		dto.setProtectedByPassword(requestUrl.isProtectedByPassword());

		Account owner = requestUrl.getUploadRequest().getOwner();
		Set<MimeType> mimeTypes = mimePolicyService.findAllMyMimeTypes(owner);
		for (MimeType mimeType : mimeTypes) {
			String extension = mimeType.getExtensions();
			if (!extension.isEmpty()) {
				dto.addMimeTypes(extension.substring(1));
			}
		}
		return dto;
	}
}
