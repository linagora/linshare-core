/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2018. Contribute to
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
import java.io.InputStream;
import java.util.Calendar;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.business.service.UploadRequestEntryBusinessService;
import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.core.domain.constants.OperationHistoryTypeEnum;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.OperationHistory;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.UploadRequestDeleteFileByOwnerEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.rac.UploadRequestEntryRessourceAccessControl;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AntiSamyService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.MimeTypeService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.QuotaService;
import org.linagora.linshare.core.service.UploadRequestEntryService;
import org.linagora.linshare.core.service.VirusScannerService;
import org.linagora.linshare.mongo.entities.DocumentGarbageCollecteur;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.entities.logs.DocumentEntryAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.UploadRequestEntryAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.repository.DocumentGarbageCollecteurMongoRepository;

public class UploadRequestEntryServiceImpl extends GenericEntryServiceImpl<Account, UploadRequestEntry>
		implements UploadRequestEntryService {

	private final UploadRequestEntryBusinessService uploadRequestEntryBusinessService;

	private final OperationHistoryBusinessService operationHistoryBusinessService;

	private final AbstractDomainService abstractDomainService;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final MimeTypeService mimeTypeService;

	private final VirusScannerService virusScannerService;

	private final MimeTypeMagicNumberDao mimeTypeIdentifier;

	private final AntiSamyService antiSamyService;

	private final QuotaService quotaService;

	private final MailBuildingService mailBuildingService;

	private final NotifierService notifierService;

	protected final DocumentGarbageCollecteurMongoRepository documentGarbageCollecteur;

	private DocumentEntryBusinessService documentEntryBusinessService;

	private LogEntryService logEntryService;

	public UploadRequestEntryServiceImpl(
			UploadRequestEntryBusinessService uploadRequestEntryBusinessService,
			AbstractDomainService abstractDomainService,
			FunctionalityReadOnlyService functionalityReadOnlyService,
			MimeTypeService mimeTypeService,
			VirusScannerService virusScannerService,
			MimeTypeMagicNumberDao mimeTypeIdentifier,
			AntiSamyService antiSamyService,
			UploadRequestEntryRessourceAccessControl rac,
			OperationHistoryBusinessService operationHistoryBusinessService,
			QuotaService quotaService,
			DocumentGarbageCollecteurMongoRepository documentGarbageCollecteur,
			MailBuildingService mailBuildingService,
			NotifierService notifierService,
			DocumentEntryBusinessService documentEntryBusinessService,
			LogEntryService logEntryService) {
		super(rac);
		this.uploadRequestEntryBusinessService = uploadRequestEntryBusinessService;
		this.abstractDomainService = abstractDomainService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.mimeTypeService = mimeTypeService;
		this.virusScannerService = virusScannerService;
		this.mimeTypeIdentifier = mimeTypeIdentifier;
		this.antiSamyService = antiSamyService;
		this.operationHistoryBusinessService = operationHistoryBusinessService;
		this.quotaService = quotaService;
		this.documentGarbageCollecteur = documentGarbageCollecteur;
		this.mailBuildingService = mailBuildingService;
		this.notifierService = notifierService;
		this.documentEntryBusinessService = documentEntryBusinessService;
		this.logEntryService = logEntryService;
	}

	@Override
	public UploadRequestEntry create(Account authUser, Account actor, File tempFile, String fileName, String comment,
			boolean isFromCmis, String metadata, UploadRequestUrl uploadRequestUrl) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notEmpty(fileName, "fileName is required.");
		UploadRequestEntry upReqEntry = null;
		try {
			fileName = sanitizeFileName(fileName);
			Long size = tempFile.length();
			checkSpace(actor, size);
			// detect file's mime type.
			String mimeType = mimeTypeIdentifier.getMimeType(tempFile);
			// check if the file MimeType is allowed
			if (mimeTypeFilteringStatus(actor)) {
				mimeTypeService.checkFileMimeType(actor, fileName, mimeType);
			}
			virusScannerService.checkVirus(fileName, actor, tempFile, size);
			// want a timestamp on doc ?
			String timeStampingUrl = null;
			StringValueFunctionality timeStampingFunctionality = functionalityReadOnlyService
					.getTimeStampingFunctionality(actor.getDomain());
			if (timeStampingFunctionality.getActivationPolicy().getStatus()) {
				timeStampingUrl = timeStampingFunctionality.getValue();
			}
			Functionality enciphermentFunctionality = functionalityReadOnlyService
					.getEnciphermentFunctionality(actor.getDomain());
			Boolean checkIfIsCiphered = enciphermentFunctionality.getActivationPolicy().getStatus();
			// We need to set an expiration date in case of file cleaner
			// activation.
			upReqEntry = uploadRequestEntryBusinessService.createUploadRequestEntryDocument(actor, tempFile, size,
					fileName, comment, checkIfIsCiphered, timeStampingUrl, mimeType,
					getDocumentExpirationDate(actor.getDomain()), isFromCmis, metadata, uploadRequestUrl);
			addToQuota(actor, size);
		} finally {
			try {
				logger.debug("deleting temp file : " + tempFile.getName());
				if (tempFile.exists()) {
					tempFile.delete(); // remove the temporary file
				}
			} catch (Exception e) {
				logger.error("can not delete temp file : " + e.getMessage(), e);
			}
		}
		AuditLogEntryUser log = new UploadRequestEntryAuditLogEntry(new AccountMto(authUser), new AccountMto(actor),
				LogAction.CREATE, AuditLogEntryType.UPLOAD_REQUEST_ENTRY, upReqEntry.getUuid(), upReqEntry);
		logEntryService.insert(log);
		return upReqEntry;
	}

	@Override
	public boolean mimeTypeFilteringStatus(Account actor) throws BusinessException {
		AbstractDomain domain = abstractDomainService.retrieveDomain(actor.getDomain().getUuid());
		Functionality mimeFunctionality = functionalityReadOnlyService.getMimeTypeFunctionality(domain);
		return mimeFunctionality.getActivationPolicy().getStatus();
	}

	@Override
	public DocumentEntry copy(Account actor, Account owner, UploadRequestEntry uploadRequestEntry)
			throws BusinessException {
		DocumentEntry entity = null;
		preChecks(actor, owner);
		Validate.notEmpty(uploadRequestEntry.getDocument().getUuid(), "documentUuid is required.");
		Validate.notEmpty(uploadRequestEntry.getName(), "fileName is required.");
		Validate.notNull(uploadRequestEntry.getSize(), "size is required.");
		checkCreatePermission(actor, owner, DocumentEntry.class, BusinessErrorCode.DOCUMENT_ENTRY_FORBIDDEN, null);
		checkSpace(owner, uploadRequestEntry.getSize());
		UploadRequest uploadRequest = uploadRequestEntry.getUploadRequestUrl().getUploadRequest();
		if (uploadRequest.getStatus().compareTo(UploadRequestStatus.CLOSED) > 0) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_ENTRY_FILE_CANNOT_BE_COPIED,
					"You need first close the current upload request before copying file");
		}
		entity = documentEntryBusinessService.copy(owner, uploadRequestEntry);
		uploadRequestEntry.setDocumentEntry(entity);
		DocumentEntryAuditLogEntry log = new DocumentEntryAuditLogEntry(actor, owner, entity, LogAction.CREATE);
		log.setCause(LogActionCause.COPY);
		log.setFromResourceUuid(uploadRequestEntry.getUuid());
		logEntryService.insert(log);
		return entity;
	}

	private String sanitizeFileName(String fileName) throws BusinessException {
		fileName = fileName.replace("\\", "_");
		fileName = fileName.replace(":", "_");
		fileName = antiSamyService.clean(fileName);
		if (fileName.isEmpty()) {
			throw new BusinessException(BusinessErrorCode.INVALID_FILENAME, "fileName is empty after the xss filter");
		}
		return fileName;
	}

	protected void checkSpace(Account owner, long size) throws BusinessException {
		quotaService.checkIfUserCanAddFile(owner, size, ContainerQuotaType.USER);
	}

	protected void addToQuota(Account owner, Long size) {
		OperationHistory oh = new OperationHistory(owner, owner.getDomain(), size, OperationHistoryTypeEnum.CREATE,
				ContainerQuotaType.USER);
		operationHistoryBusinessService.create(oh);
	}

	protected Calendar getDocumentExpirationDate(AbstractDomain domain) {
		return functionalityReadOnlyService.getDefaultFileExpiryTime(domain);
	}

	@Override
	public UploadRequestEntry find(Account authUser, Account actor, String uuid) {
		preChecks(authUser, actor);
		return uploadRequestEntryBusinessService.findByUuid(uuid);
	}

	@Override
	public InputStream download(Account actor, Account owner, String uuid) throws BusinessException {
		preChecks(actor, owner);
		Validate.notEmpty(uuid, "upload request entry uuid is required.");
		UploadRequestEntry entry = find(actor, owner, uuid);
		checkDownloadPermission(actor, owner, DocumentEntry.class, BusinessErrorCode.DOCUMENT_ENTRY_FORBIDDEN, entry);
		AuditLogEntryUser log = new UploadRequestEntryAuditLogEntry(new AccountMto(owner), new AccountMto(owner),
				LogAction.DOWNLOAD, AuditLogEntryType.UPLOAD_REQUEST_ENTRY, entry.getUuid(), entry);
		logEntryService.insert(log);
		return uploadRequestEntryBusinessService.download(entry);
	}

	@Override
	public UploadRequestEntry deleteEntryByRecipients(UploadRequestUrl uploadRequestUrl, String entryUuid) throws BusinessException{
		UploadRequestEntry entry = uploadRequestEntryBusinessService.findByUuid(entryUuid);
		if (entry == null) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_ENTRY_NOT_FOUND,
					"File not found");
		}
		uploadRequestEntryBusinessService.delete(entry);
		if (!entry.getCopied()) {
			documentGarbageCollecteur.insert(new DocumentGarbageCollecteur(entry.getDocument().getUuid()));
		}
		Account actor = uploadRequestUrl.getUploadRequest().getUploadRequestGroup().getOwner();
		AuditLogEntryUser log = new UploadRequestEntryAuditLogEntry(new AccountMto(actor),
				new AccountMto(actor), LogAction.DELETE, AuditLogEntryType.UPLOAD_REQUEST_ENTRY,
				entry.getUuid(), entry);
		logEntryService.insert(log);
		return entry;
	}

	@Override
	public UploadRequestEntry delete(User authUser, User actor, String uuid) {
		preChecks(authUser, actor);
		Validate.notEmpty(uuid, "entry uuid is required.");
		logger.debug(
				"Actor: " + actor.getAccountRepresentation() + " is trying to delete upload request entry: " + uuid);
		UploadRequestEntry uploadRequestEntry = find(authUser, actor, uuid);
		checkDeletePermission(authUser, actor, UploadRequestEntry.class,
				BusinessErrorCode.UPLOAD_REQUEST_ENTRY_FORBIDDEN, uploadRequestEntry);
		if (uploadRequestEntry.getUploadRequestUrl().getUploadRequest().getStatus()
				.compareTo(UploadRequestStatus.CLOSED) > 0) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_ENTRY_FILE_CANNOT_DELETED,
					"Cannot delete file when upload request is not closed or archived");
		}
		if (!uploadRequestEntry.getCopied()) {
			documentGarbageCollecteur.insert(new DocumentGarbageCollecteur(uploadRequestEntry.getDocument().getUuid()));
		}
		uploadRequestEntryBusinessService.delete(uploadRequestEntry);
		if (uploadRequestEntry.getUploadRequestUrl().getUploadRequest().getEnableNotification()) {
			for (UploadRequestUrl urUrl : uploadRequestEntry.getUploadRequestUrl().getUploadRequest().getUploadRequestURLs()) {
				EmailContext context = new UploadRequestDeleteFileByOwnerEmailContext(
						(User) urUrl.getUploadRequest().getUploadRequestGroup().getOwner(),
						urUrl.getUploadRequest(),	urUrl, uploadRequestEntry);
				MailContainerWithRecipient mail = mailBuildingService.build(context);
				notifierService.sendNotification(mail); 
			}
		}
		AuditLogEntryUser log = new UploadRequestEntryAuditLogEntry(new AccountMto(authUser),
				new AccountMto(actor), LogAction.DELETE, AuditLogEntryType.UPLOAD_REQUEST_ENTRY,
				uploadRequestEntry.getUuid(), uploadRequestEntry);
		logEntryService.insert(log);
		delFromQuota(actor, uploadRequestEntry.getSize());
		return uploadRequestEntry;
	}

	protected void delFromQuota(Account owner, Long size) {
		OperationHistory oh = new OperationHistory(owner, owner.getDomain(), - size, OperationHistoryTypeEnum.DELETE,
				ContainerQuotaType.USER);
		operationHistoryBusinessService.create(oh);
	}
}
