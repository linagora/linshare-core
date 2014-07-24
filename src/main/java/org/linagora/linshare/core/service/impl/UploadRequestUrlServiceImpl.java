/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */

package org.linagora.linshare.core.service.impl;

import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.UploadRequestUrlBusinessService;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.*;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.*;
import org.linagora.linshare.core.utils.HashUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadRequestUrlServiceImpl implements UploadRequestUrlService {

	final private static Logger logger = LoggerFactory.getLogger(UploadRequestUrlServiceImpl.class);

	private final UploadRequestUrlBusinessService uploadRequestUrlBusinessService;

	private final UploadRequestService uploadRequestService;

	private final AccountRepository<Account> accountRepository;

	private final DocumentEntryService documentEntryService;

	private final MailBuildingService mailBuildingService;

	private final NotifierService notifierService;

	public UploadRequestUrlServiceImpl(
			final UploadRequestUrlBusinessService uploadRequestUrlBusinessService,
			final UploadRequestService uploadRequestService,
			final AccountRepository<Account> accountRepository,
			final DocumentEntryService documentEntryService,
			final MailBuildingService mailBuildingService,
			final NotifierService notifierService) {
		super();
		this.uploadRequestUrlBusinessService = uploadRequestUrlBusinessService;
		this.uploadRequestService = uploadRequestService;
		this.accountRepository = accountRepository;
		this.documentEntryService = documentEntryService;
		this.mailBuildingService = mailBuildingService;
		this.notifierService = notifierService;
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
		// if it is already close.
		if (url.getUploadRequest().getStatus()
				.equals(UploadRequestStatus.STATUS_CLOSED)) {
			logger.warn("Closing an already closed upload request url : " + uuid);
			return url;
		}
		Account actor = accountRepository.getUploadRequestSystemAccount();
		uploadRequestService.setStatusToClosed(actor, url.getUploadRequest());
		url = find(uuid, password);
		MailContainerWithRecipient mail = mailBuildingService.buildCloseUploadRequestByRecipient((User) url.getUploadRequest().getOwner(), url);
		notifierService.sendAllNotification(mail);
		return url;
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
			Account actor = accountRepository.getUploadRequestSystemAccount();
			String documentEntryUuid = null;
			if (found.getDocumentEntry() != null) {
				documentEntryUuid = found.getDocumentEntry().getUuid();
			}
			found.setDocumentEntry(null);
			uploadRequestService.updateRequestEntry(actor, found);
			if (documentEntryUuid != null) {
				// TODO: HOOK : Extract owner for upload request URL
				// Actor should be used instead of owner
				Account owner = requestUrl.getUploadRequest().getOwner();
				// Store the file into the owner account.
				DocumentEntry documentEntry = documentEntryService.findById(owner, documentEntryUuid);
				documentEntryService.deleteDocumentEntry(owner, documentEntry);
			}
			uploadRequestService.deleteRequestEntry(actor, found);
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
		UploadRequestEntry requestEntry = uploadRequestService.createRequestEntry(actor,
				uploadRequestEntry);
		MailContainerWithRecipient mail = mailBuildingService.buildAckUploadRequest((User) requestUrl.getUploadRequest().getOwner(), requestUrl, requestEntry);
		notifierService.sendAllNotification(mail);
		return requestEntry;
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
			throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_URL_FORBIDDEN,
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
