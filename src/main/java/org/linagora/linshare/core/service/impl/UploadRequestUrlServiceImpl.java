package org.linagora.linshare.core.service.impl;

import java.io.InputStream;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.UploadRequestUrlBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.UploadRequestService;
import org.linagora.linshare.core.service.UploadRequestUrlService;

public class UploadRequestUrlServiceImpl implements UploadRequestUrlService {

	private final UploadRequestUrlBusinessService uploadRequestUrlBusinessService;

	private final UploadRequestService uploadRequestService;

	private final AccountRepository<Account> accountRepository;

	private final DocumentEntryService documentEntryService;

	public UploadRequestUrlServiceImpl(
			final UploadRequestUrlBusinessService uploadRequestUrlBusinessService,
			final UploadRequestService uploadRequestService,
			final AccountRepository<Account> accountRepository,
			final DocumentEntryService documentEntryService) {
		super();
		this.uploadRequestUrlBusinessService = uploadRequestUrlBusinessService;
		this.uploadRequestService = uploadRequestService;
		this.accountRepository = accountRepository;
		this.documentEntryService = documentEntryService;
	}

	@Override
	public UploadRequestUrl find(String uuid) throws BusinessException {
		Validate.notEmpty(uuid);
		UploadRequestUrl url = uploadRequestUrlBusinessService.findByUuid(uuid);
		if (url == null) {
			throw new BusinessException(BusinessErrorCode.NO_SUCH_ELEMENT,
					"UploadRequestUrl not found.");
		}
		return url;
	}

	@Override
	public UploadRequestUrl close(String uuid) throws BusinessException {
		UploadRequestUrl url = find(uuid);
		Account actor = accountRepository.getSystemAccount();
		uploadRequestService.setStatusToClosed(actor, url.getUploadRequest());
		return find(uuid);
	}

	@Override
	public UploadRequestEntry createUploadRequestEntry(
			String uploadRequestUrlUuid, InputStream fi, String fileName)
			throws BusinessException {
		// Retrieve upload request URL
		UploadRequestUrl requestUrl = find(uploadRequestUrlUuid);
		// Extract owner for upload request URL
		Account owner = requestUrl.getUploadRequest().getOwner();
		// Store the file into the owner account.
		DocumentEntry document = documentEntryService.createDocumentEntry(
				owner, fi, fileName);
		Account actor = accountRepository.getSystemAccount();
		// Create the link between the document and the upload request URL.
		UploadRequestEntry uploadRequestEntry = new UploadRequestEntry(
				document, requestUrl.getUploadRequest());
		return uploadRequestService.createRequestEntry(actor,
				uploadRequestEntry);
	}
}
