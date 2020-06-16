/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.PasswordService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.UploadRequestUrlBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.objects.UploadRequestContainer;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.UploadRequestActivationEmailContext;
import org.linagora.linshare.core.notifications.context.UploadRequestCreatedEmailContext;
import org.linagora.linshare.core.notifications.context.UploadRequestDeleteFileEmailContext;
import org.linagora.linshare.core.notifications.context.UploadRequestUploadedFileEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.rac.UploadRequestUrlResourceAccessControl;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.UploadRequestEntryService;
import org.linagora.linshare.core.service.UploadRequestUrlService;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.entities.logs.UploadRequestUrlAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.AccountMto;

public class UploadRequestUrlServiceImpl extends GenericServiceImpl<Account, UploadRequestUrl> implements UploadRequestUrlService {

	private final UploadRequestUrlBusinessService uploadRequestUrlBusinessService;

	private final AccountRepository<Account> accountRepository;

	private final MailBuildingService mailBuildingService;

	private final NotifierService notifierService;

	private final UploadRequestEntryService uploadRequestEntryService;
	
	private final PasswordService passwordService;

	public UploadRequestUrlServiceImpl(
			final UploadRequestUrlBusinessService uploadRequestUrlBusinessService,
			final AccountRepository<Account> accountRepository,
			final MailBuildingService mailBuildingService,
			final NotifierService notifierService,
			final UploadRequestEntryService uploadRequestEntryService,
			final UploadRequestUrlResourceAccessControl rac,
			final PasswordService passwordService,
			final SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super(rac, sanitizerInputHtmlBusinessService);
		this.uploadRequestUrlBusinessService = uploadRequestUrlBusinessService;
		this.accountRepository = accountRepository;
		this.mailBuildingService = mailBuildingService;
		this.notifierService = notifierService;
		this.uploadRequestEntryService = uploadRequestEntryService;
		this.passwordService = passwordService;
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
	public UploadRequestContainer create(UploadRequest request, Contact contact, UploadRequestContainer container)
			throws BusinessException {
		UploadRequestUrl requestUrl = uploadRequestUrlBusinessService.create(request, request.isSecured(), contact);
		User owner = (User) request.getUploadRequestGroup().getOwner();
		if (UploadRequestStatus.CREATED.equals(request.getStatus())) {
			if (request.getEnableNotification()) {
				UploadRequestCreatedEmailContext context = new UploadRequestCreatedEmailContext(owner, requestUrl,
						request);
				container.addMailContainersAddEmail(mailBuildingService.build(context));
			}
		} else if (UploadRequestStatus.ENABLED.equals(request.getStatus())) {
			UploadRequestActivationEmailContext mailContext = new UploadRequestActivationEmailContext(owner, request,
					requestUrl);
			container.addMailContainersAddEmail(mailBuildingService.build(mailContext));
		}
		AuditLogEntryUser log = new UploadRequestUrlAuditLogEntry(new AccountMto(owner), new AccountMto(owner),
				LogAction.CREATE, AuditLogEntryType.UPLOAD_REQUEST_URL, requestUrl.getUuid(), requestUrl);
		container.addLog(log);
		return container;
	}

	@Override
	public UploadRequestEntry deleteUploadRequestEntry(String uploadRequestUrlUuid, String password, String entryUuid)
			throws BusinessException {
		UploadRequestUrl requestUrl = find(uploadRequestUrlUuid, password);
		if (requestUrl.getUploadRequest().getStatus() != UploadRequestStatus.ENABLED) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_ENTRY_FILE_CANNOT_DELETED,
					"Cannot delete file when upload request is not enabled");
		}
		deleteBusinessCheck(requestUrl);
		User owner = (User) requestUrl.getUploadRequest().getUploadRequestGroup().getOwner();
		UploadRequestEntry entry = uploadRequestEntryService.deleteEntryByRecipients(requestUrl, entryUuid);
		if (requestUrl.getUploadRequest().getEnableNotification()) {
			EmailContext context = new UploadRequestDeleteFileEmailContext(owner, requestUrl.getUploadRequest(), requestUrl,
					entry);
			MailContainerWithRecipient mail = mailBuildingService.build(context);
			notifierService.sendNotification(mail);
		}
		return entry;
	}

	@Override
	public UploadRequestEntry createUploadRequestEntry(String uploadRequestUrlUuid, File  file, String fileName,
			String password) throws BusinessException {
		Account actor = accountRepository.getUploadRequestSystemAccount();
		// Retrieve upload request URL
		UploadRequestUrl requestUrl = find(uploadRequestUrlUuid, password);
		// HOOK : Extract owner for upload request URL
		Account owner = requestUrl.getUploadRequest().getUploadRequestGroup().getOwner();
		// Store the file into the owner account.
		UploadRequestEntry upReqdoc = uploadRequestEntryService.create(
				actor, owner, file, fileName, "", false, null, requestUrl);
		createBusinessCheck(requestUrl, upReqdoc);
		if (requestUrl.getUploadRequest().getEnableNotification()) {
			EmailContext context = new UploadRequestUploadedFileEmailContext(
					(User) requestUrl.getUploadRequest().getUploadRequestGroup().getOwner(),
					requestUrl.getUploadRequest(), requestUrl, upReqdoc);
			MailContainerWithRecipient mail = mailBuildingService.build(context);
			notifierService.sendNotification(mail, true);
		}
		return upReqdoc;
	}

	private boolean isValidPassword(UploadRequestUrl data, String password) {
		if (data.isProtectedByPassword()) {
			if (password == null)
				return false;
			return passwordService.matches(password, data.getPassword());
		}
		return true;
	}

	private void accessBusinessCheck(UploadRequestUrl requestUrl,
			String password) throws BusinessException {
		UploadRequest request = requestUrl.getUploadRequest();
		if (!isValidPassword(requestUrl, password)) {
			throw new BusinessException(
					BusinessErrorCode.UPLOAD_REQUEST_URL_FORBIDDEN,
					"You do not have the right to get this upload request url : "
							+ requestUrl.getUuid());
		}
		if (!(request.getStatus().equals(UploadRequestStatus.ENABLED) || request
				.getStatus().equals(UploadRequestStatus.CLOSED))) {
			throw new BusinessException(
					BusinessErrorCode.UPLOAD_REQUEST_READONLY_MODE,
					"The current upload request url is not available : "
							+ requestUrl.getUuid());
		}
		Calendar now = GregorianCalendar.getInstance();
		Calendar compare = GregorianCalendar.getInstance();
		compare.setTime(request.getActivationDate());
		if (now.before(compare)) {
			throw new BusinessException(
					BusinessErrorCode.UPLOAD_REQUEST_NOT_ENABLE_YET,
					"The current upload request url is not enable yet : "
							+ requestUrl.getUuid());
		}
		if (request.getExpiryDate() != null) {
			compare.setTime(request.getExpiryDate());
			if (now.after(compare)) {
				throw new BusinessException(
						BusinessErrorCode.UPLOAD_REQUEST_NOT_ENABLE_YET,
						"The current upload request url is no more available now. : "
								+ requestUrl.getUuid());
			}
		}
	}

	private void createBusinessCheck(UploadRequestUrl requestUrl,
			UploadRequestEntry document) throws BusinessException {
		UploadRequest request = requestUrl.getUploadRequest();
		if (!request.getStatus().equals(UploadRequestStatus.ENABLED)) {
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
			if (requestUrl.getUploadRequestEntries().size() >= request
					.getMaxFileCount()) {
				throw new BusinessException(
						BusinessErrorCode.UPLOAD_REQUEST_TOO_MANY_FILES,
						"You already have reached the uploaded file limit.");
			}
		}
		if (request.getMaxDepositSize() != null) {
			long totalSize = 0;
			for (UploadRequestEntry entry : requestUrl.getUploadRequestEntries()) {
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

	private void deleteBusinessCheck(UploadRequestUrl requestUrl)
			throws BusinessException {
		UploadRequest request = requestUrl.getUploadRequest();
		if (!request.getStatus().equals(UploadRequestStatus.ENABLED)) {
			throw new BusinessException(
					BusinessErrorCode.UPLOAD_REQUEST_READONLY_MODE,
					"The current upload request url is in read only mode : "
							+ requestUrl.getUuid());
		}
	}

	@Override
	public Long computeEntriesSize(UploadRequest request) {
		return uploadRequestEntryService.computeEntriesSize(request);
	}
}
