package org.linagora.linshare.core.service.impl;

import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.UploadRequestUrlBusinessService;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.UploadRequestService;
import org.linagora.linshare.core.service.UploadRequestUrlService;
import org.linagora.linshare.core.utils.HashUtils;

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
	public UploadRequestUrl find(String uuid, String password)
			throws BusinessException {
		Validate.notEmpty(uuid);
		UploadRequestUrl requestUrl = uploadRequestUrlBusinessService
				.findByUuid(uuid);
		if (requestUrl != null) {
			accessBusinessCheck(requestUrl, password);
			return requestUrl;
		}
		throw new BusinessException(BusinessErrorCode.FORBIDDEN,
				"You do not have the right to get this upload request url : "
						+ uuid);
	}

	@Override
	public UploadRequestUrl close(String uuid, String password)
			throws BusinessException {
		UploadRequestUrl url = find(uuid, password);
		// if it is alread close.
		if (url.getUploadRequest().getStatus()
				.equals(UploadRequestStatus.STATUS_CLOSED)) {
			return find(uuid, password);
		}
		Account actor = accountRepository.getUploadRequestSystemAccount();
		uploadRequestService.setStatusToClosed(actor, url.getUploadRequest());
		return find(uuid, password);
	}

	@Override
	public void deleteUploadRequestEntry(String uploadRequestUrlUuid,
			String password, String entryUuid) throws BusinessException {
		UploadRequestUrl requestUrl = find(uploadRequestUrlUuid, password);
		deleteBusinessCheck(requestUrl);
		Set<UploadRequestEntry> entries = requestUrl.getUploadRequest()
				.getUploadRequestEntries();
		UploadRequestEntry found = null;
		for (UploadRequestEntry entry : entries) {
			if (entry.getUuid().equals(entryUuid)) {
				found = entry;
				break;
			}
		}
		if (found != null) {
			DocumentEntry documentEntry = found.getDocumentEntry();
			Account actor = accountRepository.getUploadRequestSystemAccount();
			uploadRequestService.deleteRequestEntry(actor, found);

			if (documentEntry != null) {
				// TODO: HOOK : Extract owner for upload request URL
				// Actor should be used instead of owner
				Account owner = requestUrl.getUploadRequest().getOwner();
				// Store the file into the owner account.
				documentEntryService.deleteDocumentEntry(owner, documentEntry);
			}
		} else {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN,
					"You do not have the right to delete a file into upload request : "
							+ uploadRequestUrlUuid);
		}
	}

	@Override
	public UploadRequestEntry createUploadRequestEntry(
			String uploadRequestUrlUuid, InputStream fi, String fileName,
			String password) throws BusinessException {
		// Retrieve upload request URL
		UploadRequestUrl requestUrl = find(uploadRequestUrlUuid, password);
		// HOOK : Extract owner for upload request URL
		Account owner = requestUrl.getUploadRequest().getOwner();
		// Store the file into the owner account.
		DocumentEntry document = documentEntryService.createDocumentEntry(
				owner, fi, fileName);
		createBusinessCheck(requestUrl, document);
		Account actor = accountRepository.getUploadRequestSystemAccount();
		// Create the link between the document and the upload request URL.
		UploadRequestEntry uploadRequestEntry = new UploadRequestEntry(
				document, requestUrl.getUploadRequest());
		return uploadRequestService.createRequestEntry(actor,
				uploadRequestEntry);
	}

	private boolean isValidPassword(UploadRequestUrl data, String password) {
		if (data.isProtectedByPassword()) {
			if (password == null)
				return false;
			String hashedPassword = HashUtils.hashSha1withBase64(password
					.getBytes());
			return hashedPassword.equals(data.getPassword());
		}
		return true;
	}

	private void accessBusinessCheck(UploadRequestUrl requestUrl,
			String password) throws BusinessException {
		UploadRequest request = requestUrl.getUploadRequest();
		if (!isValidPassword(requestUrl, password)) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN,
					"You do not have the right to get this upload request url : "
							+ requestUrl.getUuid());
		}

		if (!(request.getStatus().equals(UploadRequestStatus.STATUS_ENABLED)
				|| request.getStatus().equals(UploadRequestStatus.STATUS_CLOSED)
				)) {
			throw new BusinessException(
					BusinessErrorCode.UPLOAD_REQUEST_READONLY_MODE,
					"The current upload request url is not available : "
							+ requestUrl.getUuid());
		}

		Calendar now = GregorianCalendar.getInstance();
		Calendar compare = GregorianCalendar.getInstance();
		compare.setTime(request.getActivationDate());
		if (now.before(
				compare)) {
			throw new BusinessException(
					BusinessErrorCode.UPLOAD_REQUEST_NOT_ENABLE_YET,
					"The current upload request url is not enable yet : "
							+ requestUrl.getUuid());
		}
		compare.setTime(request.getExpiryDate());
		if (now.after(
				compare)) {
			throw new BusinessException(
					BusinessErrorCode.UPLOAD_REQUEST_NOT_ENABLE_YET,
					"The current upload request url is no more available now. : "
							+ requestUrl.getUuid());
		}
	}

	private void createBusinessCheck(UploadRequestUrl requestUrl,
			DocumentEntry document) throws BusinessException {
		UploadRequest request = requestUrl.getUploadRequest();
		if (!request.getStatus().equals(UploadRequestStatus.STATUS_ENABLED)) {
			throw new BusinessException(
					BusinessErrorCode.UPLOAD_REQUEST_READONLY_MODE,
					"The current upload request url is in read only mode : "
							+ requestUrl.getUuid());
		}
		if (request.getMaxFileSize() != null) {
			if (document.getSize() > request.getMaxFileSize()) {
				throw new BusinessException(
						BusinessErrorCode.UPLOAD_REQUEST_FILE_TOO_LARGE,
						"You already have reached the uploaded file limit.");
			}
		}
		if (request.getMaxFileCount() != null) {
			// already reach the limit
			if (request.getUploadRequestEntries().size() >= request
					.getMaxFileCount()) {
				throw new BusinessException(
						BusinessErrorCode.UPLOAD_REQUEST_TOO_MANY_FILES,
						"You already have reached the uploaded file limit.");
			}
		}
		if (request.getMaxDepositSize() != null) {
			long totalSize = 0;
			for (UploadRequestEntry entry : request.getUploadRequestEntries()) {
				totalSize += entry.getSize();
			}
			totalSize += document.getSize();
			if (totalSize >= request.getMaxDepositSize()) {
				throw new BusinessException(
						BusinessErrorCode.UPLOAD_REQUEST_TOTAL_DEPOSIT_SIZE_TOO_LARGE,
						"You already have reached the limit of your quota.");
			}
		}
	}

	private void deleteBusinessCheck(UploadRequestUrl requestUrl) throws BusinessException {
		UploadRequest request = requestUrl.getUploadRequest();
		if (!request.getStatus().equals(UploadRequestStatus.STATUS_ENABLED)) {
			throw new BusinessException(
					BusinessErrorCode.UPLOAD_REQUEST_READONLY_MODE,
					"The current upload request url is in read only mode : "
							+ requestUrl.getUuid());
		}
	}
}
