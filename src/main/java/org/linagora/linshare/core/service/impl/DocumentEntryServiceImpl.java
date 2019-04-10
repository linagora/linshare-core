/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.AntivirusLogEntry;
import org.linagora.linshare.core.domain.entities.BooleanValueFunctionality;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.FileLogEntry;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.LogEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.objects.SizeUnitValueFunctionality;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.rac.DocumentEntryResourceAccessControl;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AntiSamyService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.MailBuildingService;
import org.linagora.linshare.core.service.MimeTypeService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.VirusScannerService;

import com.google.common.collect.Lists;

public class DocumentEntryServiceImpl extends GenericEntryServiceImpl<Account, DocumentEntry> implements DocumentEntryService {

	private final DocumentEntryBusinessService documentEntryBusinessService;

	private final LogEntryService logEntryService;

	private final AbstractDomainService abstractDomainService;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final MimeTypeService mimeTypeService;

	private final VirusScannerService virusScannerService;

	private final MimeTypeMagicNumberDao mimeTypeIdentifier;

	private final AntiSamyService antiSamyService;

	private final DomainBusinessService domainBusinessService;

	private final MailBuildingService mailBuildingService;

	private final NotifierService notifierService;

	private final Long virusscannerLimitFilesize;

	private final boolean overrideGlobalQuota;

	public DocumentEntryServiceImpl(
			DocumentEntryBusinessService documentEntryBusinessService,
			LogEntryService logEntryService,
			AbstractDomainService abstractDomainService,
			FunctionalityReadOnlyService functionalityReadOnlyService,
			MimeTypeService mimeTypeService,
			VirusScannerService virusScannerService,
			MimeTypeMagicNumberDao mimeTypeIdentifier,
			AntiSamyService antiSamyService,
			DomainBusinessService domainBusinessService,
			DocumentEntryResourceAccessControl rac,
			MailBuildingService mailBuildingService,
			NotifierService notifierService,
			Long virusscannerLimitFilesize,
			boolean overrideGlobalQuota) {
		super(rac);
		this.documentEntryBusinessService = documentEntryBusinessService;
		this.logEntryService = logEntryService;
		this.abstractDomainService = abstractDomainService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.mimeTypeService = mimeTypeService;
		this.virusScannerService = virusScannerService;
		this.mimeTypeIdentifier = mimeTypeIdentifier;
		this.antiSamyService = antiSamyService;
		this.domainBusinessService = domainBusinessService;
		this.mailBuildingService = mailBuildingService;
		this.notifierService = notifierService;
		// if the file size equal the limit of the virus scanner limit, or very close,
		// the file size more the protocol overhead of the clamavLib could reach the StreamMaxLength of clamav
		// and raise a Exception.
		// That's why we retrieve 1024bytes to avoid this corner case.
		if (virusscannerLimitFilesize != null) {
			virusscannerLimitFilesize = virusscannerLimitFilesize - 1024L;
		}
		this.virusscannerLimitFilesize = virusscannerLimitFilesize;
		this.overrideGlobalQuota = overrideGlobalQuota;
	}

	@Override
	public DocumentEntry find(Account actor, Account owner, String uuid)
			throws BusinessException {
		preChecks(actor, owner);
		Validate.notEmpty(uuid, "Missing document entry uuid");
		DocumentEntry entry = documentEntryBusinessService.find(uuid);
		if (entry == null) {
			logger.error("Current actor " + actor.getAccountRepresentation()
					+ " is looking for a misssing document entry (" + uuid
					+ ") owned by : " + owner.getAccountRepresentation());
			String message = "Can not find document entry with uuid : " + uuid;
			throw new BusinessException(
					BusinessErrorCode.DOCUMENT_ENTRY_NOT_FOUND, message);
		}
		checkReadPermission(actor, owner, DocumentEntry.class,
				BusinessErrorCode.DOCUMENT_ENTRY_FORBIDDEN, entry);
		return entry;
	}

	@Override
	public List<DocumentEntry> findAll(Account actor, Account owner)
			throws BusinessException {
		preChecks(actor, owner);
		checkListPermission(actor, owner, DocumentEntry.class,
				BusinessErrorCode.DOCUMENT_ENTRY_FORBIDDEN, null);
		return documentEntryBusinessService.findAllMyDocumentEntries(owner);
	}

	@Override
	public List<DocumentEntry> findAllMySyncEntries(Account actor, Account owner)
			throws BusinessException {
		preChecks(actor, owner);
		checkListPermission(actor, owner, DocumentEntry.class,
				BusinessErrorCode.DOCUMENT_ENTRY_FORBIDDEN, null);
		return documentEntryBusinessService.findAllMySyncEntries(owner);
	}

	@Override
	public DocumentEntry create(Account actor, Account owner, File tempFile, String fileName, String comment, boolean isFromCmis, String metadata) throws BusinessException {
		return create(actor, owner, tempFile, fileName, comment, false, isFromCmis, metadata);
	}

	@Override
	public DocumentEntry create(Account actor, Account owner, File tempFile, String fileName, String comment, boolean forceAntivirusOff, boolean isFromCmis, String metadata) throws BusinessException {
		preChecks(actor, owner);
		Validate.notEmpty(fileName, "fileName is required.");
		checkCreatePermission(actor, owner, DocumentEntry.class,
				BusinessErrorCode.DOCUMENT_ENTRY_FORBIDDEN, null);
		fileName = sanitizeFileName(fileName); // throws
		Long size = tempFile.length();
		DocumentEntry docEntry = null;
		try {
			String mimeType = mimeTypeIdentifier.getMimeType(tempFile);
			checkSpace(size, fileName, owner);

			// check if the file MimeType is allowed
			AbstractDomain domain = abstractDomainService.retrieveDomain(actor
					.getDomain().getIdentifier());
			if (mimeTypeFilteringStatus(actor)) {
				mimeTypeService.checkFileMimeType(owner, fileName, mimeType);
			}

			if (!forceAntivirusOff) {
				Functionality antivirusFunctionality = functionalityReadOnlyService
						.getAntivirusFunctionality(domain);
				if (antivirusFunctionality.getActivationPolicy().getStatus()) {
					checkVirus(fileName, owner, tempFile, size);
				}
			}

			// want a timestamp on doc ?
			String timeStampingUrl = null;
			StringValueFunctionality timeStampingFunctionality = functionalityReadOnlyService
					.getTimeStampingFunctionality(domain);
			if (timeStampingFunctionality.getActivationPolicy().getStatus()) {
				timeStampingUrl = timeStampingFunctionality.getValue();
			}

			Functionality enciphermentFunctionality = functionalityReadOnlyService
					.getEnciphermentFunctionality(domain);
			Boolean checkIfIsCiphered = enciphermentFunctionality
					.getActivationPolicy().getStatus();

			// We need to set an expiration date in case of file cleaner
			// activation.
			docEntry = documentEntryBusinessService.createDocumentEntry(owner,
					tempFile, size, fileName, comment, checkIfIsCiphered,
					timeStampingUrl, mimeType,
					getDocumentExpirationDate(domain), isFromCmis, metadata);

			FileLogEntry logEntry = new FileLogEntry(owner,
					LogAction.FILE_UPLOAD, "Creation of a file",
					docEntry.getName(), docEntry.getSize(), docEntry.getType());
			logEntryService.create(logEntry);

			addDocSizeToGlobalUsedQuota(docEntry.getDocument(), domain);
			// Extra check to avoid over quota when we authorize multiple upload for fineuploader
			long availableSize = getAvailableSize(owner);
			logger.debug("availableSize :" + availableSize);
			if (availableSize < 0) {
				logger.error("The file  " + fileName + " is too large to fit in " + owner.getAccountRepresentation() + " user's space.");
				String[] extras = { fileName };
				throw new BusinessException(BusinessErrorCode.FILE_TOO_LARGE, "The file is too large to fit in user's space.", extras);
			}
		} finally {
			try{
				logger.debug("deleting temp file : " + tempFile.getName());
				if (tempFile.exists()) {
					tempFile.delete(); // remove the temporary file
				}
			} catch (Exception e) {
				logger.error("can not delete temp file : " + e.getMessage());
			}
		}
		return docEntry;
	}

	@Override
	public boolean mimeTypeFilteringStatus(Account actor) throws BusinessException {
		AbstractDomain domain = abstractDomainService.retrieveDomain(actor.getDomain().getIdentifier());
		Functionality mimeFunctionality = functionalityReadOnlyService.getMimeTypeFunctionality(domain);
		return mimeFunctionality.getActivationPolicy().getStatus();
	}

	@Override
	public DocumentEntry update(Account actor, Account owner,
			String docEntryUuid, File tempFile, String fileName)
			throws BusinessException {
		preChecks(actor, owner);
		Validate.notEmpty(docEntryUuid, "document entry uuid is required.");
		DocumentEntry originalEntry = find(actor, owner, docEntryUuid);
		String originalFileName = originalEntry.getName();
		if (fileName == null || fileName.isEmpty()) {
			fileName = originalFileName;
		}
		checkUpdatePermission(actor, owner, DocumentEntry.class,
				BusinessErrorCode.DOCUMENT_ENTRY_FORBIDDEN, originalEntry);
		fileName = sanitizeFileName(fileName); // throws
		Long size = tempFile.length();
		DocumentEntry documentEntry = null;

		try {
			String mimeType = mimeTypeIdentifier.getMimeType(tempFile);

			AbstractDomain domain = abstractDomainService.retrieveDomain(owner
					.getDomain().getIdentifier());

			long oldDocSize = originalEntry.getSize();
			checkSpace(size, fileName, owner);

			// check if the file MimeType is allowed
			Functionality mimeFunctionality = functionalityReadOnlyService
					.getMimeTypeFunctionality(domain);
			if (mimeFunctionality.getActivationPolicy().getStatus()) {
				mimeTypeService.checkFileMimeType(owner, fileName, mimeType);
			}

			Functionality antivirusFunctionality = functionalityReadOnlyService
					.getAntivirusFunctionality(domain);
			if (antivirusFunctionality.getActivationPolicy().getStatus()) {
				checkVirus(fileName, owner, tempFile, size);
			}

			// want a timestamp on doc ?
			String timeStampingUrl = null;
			StringValueFunctionality timeStampingFunctionality = functionalityReadOnlyService
					.getTimeStampingFunctionality(domain);
			if (timeStampingFunctionality.getActivationPolicy().getStatus()) {
				timeStampingUrl = timeStampingFunctionality.getValue();
			}

			Functionality enciphermentFunctionality = functionalityReadOnlyService
					.getEnciphermentFunctionality(domain);
			Boolean checkIfIsCiphered = enciphermentFunctionality
					.getActivationPolicy().getStatus();

			// We need to set an expiration date in case of file cleaner
			// activation.
			documentEntry = documentEntryBusinessService.updateDocumentEntry(
					owner, originalEntry, tempFile, size, fileName,
					checkIfIsCiphered, timeStampingUrl, mimeType,
					getDocumentExpirationDate(domain));

			// put new file name in log
			// if the file is updated/replaced with a new file (new file name)
			// put new file name in log
			String logText = originalFileName;
			if (!logText.equalsIgnoreCase(documentEntry.getName())) {
				logText = documentEntry.getName() + " [" + logText + "]";
			}

			FileLogEntry logEntry = new FileLogEntry(owner, LogAction.FILE_UPDATE, "Update of a file", logText, documentEntry.getSize(), documentEntry.getType());

			logEntryService.create(logEntry);

			removeDocSizeFromGlobalUsedQuota(oldDocSize, domain);
			addDocSizeToGlobalUsedQuota(documentEntry.getDocument(), domain);
			if(documentEntryBusinessService.getRelatedEntriesCount(documentEntry) > 0) {
				// send email, file has been replaced ....

				List<MailContainerWithRecipient> mails = Lists.newArrayList();
				for (AnonymousShareEntry anonymousShareEntry : documentEntry
						.getAnonymousShareEntries()) {
					mails.add(mailBuildingService.buildSharedDocUpdated(
							anonymousShareEntry, originalFileName,
							documentEntry.getSize()));
				}
				for (ShareEntry shareEntry : documentEntry.getShareEntries()) {
					mails.add(mailBuildingService.buildSharedDocUpdated(
							shareEntry, originalFileName, documentEntry.getSize()));
				}
				notifierService.sendNotification(mails);
			}
		} finally {
			try{
				logger.debug("deleting temp file : " + tempFile.getName());
				tempFile.delete(); // remove the temporary file
			} catch (Exception e) {
				logger.error("can not delete temp file : " + e.getMessage());
			}
		}
		return documentEntry;
	}

	protected Calendar getDocumentExpirationDate(AbstractDomain domain) {
		return functionalityReadOnlyService.getDefaultFileExpiryTime(domain);
	}

	@Override
	public void deleteInconsistentDocumentEntry(SystemAccount actor, DocumentEntry documentEntry) throws BusinessException {
		checkDeletePermission(actor, null, DocumentEntry.class,
				BusinessErrorCode.DOCUMENT_ENTRY_FORBIDDEN, documentEntry);
		Account owner = documentEntry.getEntryOwner();
		try {

			if (documentEntryBusinessService.getRelatedEntriesCount(documentEntry) > 0) {
				throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You are not authorized to delete this document. It still exists shares.");
			}

			AbstractDomain domain = abstractDomainService.retrieveDomain(owner.getDomain().getIdentifier());
			removeDocSizeFromGlobalUsedQuota(documentEntry.getSize(), domain);

			FileLogEntry logEntry = new FileLogEntry(owner, LogAction.FILE_INCONSISTENCY, "File removed because of inconsistency. Please contact your administrator.", documentEntry.getName(),
					documentEntry.getSize(), documentEntry.getType());
			logEntryService.create(LogEntryService.WARN, logEntry);
			documentEntryBusinessService.deleteDocumentEntry(documentEntry);
		} catch (IllegalArgumentException e) {
			logger.error("Could not delete file " + documentEntry.getName() + " of user " + owner.getLsUuid() + ", reason : ", e);
			throw new TechnicalException(TechnicalErrorCode.COULD_NOT_DELETE_DOCUMENT, "Could not delete document");
		}
	}

	@Override
	public void deleteExpiredDocumentEntry(SystemAccount actor, DocumentEntry documentEntry) throws BusinessException {
		checkDeletePermission(actor, null, DocumentEntry.class,
				BusinessErrorCode.DOCUMENT_ENTRY_FORBIDDEN, documentEntry);
		Account owner = documentEntry.getEntryOwner();
		try {
			if (documentEntryBusinessService
					.getRelatedEntriesCount(documentEntry) > 0) {
				throw new BusinessException(BusinessErrorCode.CANNOT_DELETE_EXPIRED_DOCUMENT_ENTRY,
						"This document can't be deleted. It still exists shares for it.");
			}

			AbstractDomain domain = abstractDomainService.retrieveDomain(owner.getDomain().getIdentifier());
			removeDocSizeFromGlobalUsedQuota(documentEntry.getSize(), domain);

			FileLogEntry logEntry = new FileLogEntry(actor, LogAction.FILE_EXPIRE, "Expiration of a file", documentEntry.getName(), documentEntry.getSize(), documentEntry.getType());
			logEntryService.create(LogEntryService.INFO, logEntry);
			documentEntryBusinessService.deleteDocumentEntry(documentEntry);

		} catch (IllegalArgumentException e) {
			logger.error("Could not delete file " + documentEntry.getName() + " of user " + owner.getLsUuid() + ", reason : ", e);
			throw new TechnicalException(TechnicalErrorCode.COULD_NOT_DELETE_DOCUMENT, "Could not delete document");
		}
	}

	@Override
	public DocumentEntry delete(Account actor, Account owner, String documentUuid) throws BusinessException {
		preChecks(actor, owner);
		Validate.notEmpty(documentUuid, "documentUuid is required.");
		logger.debug("Actor: " + actor.getAccountRepresentation() + " is trying to delete document entry: " + documentUuid);
		DocumentEntry documentEntry = find(actor, owner, documentUuid);
		checkDeletePermission(actor, owner, DocumentEntry.class,
				BusinessErrorCode.DOCUMENT_ENTRY_FORBIDDEN, documentEntry);
		if (documentEntryBusinessService
				.getRelatedEntriesCount(documentEntry) > 0) {
			throw new BusinessException(BusinessErrorCode.CANNOT_DELETE_DOCUMENT_ENTRY,
					"You are not authorized to delete this document. There's still existing shares.");
		}
		AbstractDomain domain = abstractDomainService.retrieveDomain(owner.getDomain().getIdentifier());
		removeDocSizeFromGlobalUsedQuota(documentEntry.getSize(), domain);
		FileLogEntry logEntry = new FileLogEntry(owner, LogAction.FILE_DELETE, "Deletion of a file", documentEntry.getName(), documentEntry.getSize(), documentEntry.getType());
		logEntryService.create(LogEntryService.INFO, logEntry);
		documentEntryBusinessService.deleteDocumentEntry(documentEntry);
		return documentEntry;
	}

	@Override
	public long getUserMaxFileSize(Account account) throws BusinessException {
		// if user is not in one domain = BOUM
		AbstractDomain domain = abstractDomainService.retrieveDomain(account.getDomain().getIdentifier());
		SizeUnitValueFunctionality userMaxFileSizeFunctionality = functionalityReadOnlyService.getUserMaxFileSizeFunctionality(domain);
		if (userMaxFileSizeFunctionality.getActivationPolicy().getStatus()) {
			long maxSize = userMaxFileSizeFunctionality.getPlainSize();
			if (maxSize < 0) {
				maxSize = 0;
			}
			return maxSize;
		}
		return LinShareConstants.defaultMaxFileSize;
	}

	@Override
	public long getAvailableSize(Account account) throws BusinessException {
		// if user is not in one domain = BOUM
		AbstractDomain domain = account.getDomain();
		SizeUnitValueFunctionality globalQuotaFunctionality = functionalityReadOnlyService.getGlobalQuotaFunctionality(domain);
		SizeUnitValueFunctionality userQuotaFunctionality = functionalityReadOnlyService.getUserQuotaFunctionality(domain);
		if (globalQuotaFunctionality.getActivationPolicy().getStatus()) {
			long usedSpace = abstractDomainService.getUsedSpace(account);
			long availableSize = globalQuotaFunctionality.getPlainSize() - usedSpace;
			if (availableSize < 0) {
				availableSize = 0;
			}
			return availableSize;
		} else if (userQuotaFunctionality.getActivationPolicy().getStatus()) {
			long userQuota = userQuotaFunctionality.getPlainSize();
			long usedSpace = documentEntryBusinessService.getUsedSpace(account);
			return userQuota - usedSpace;
		}
		return LinShareConstants.defaultFreeSpace;
	}

	@Override
	public long getTotalSize(Account account) throws BusinessException {
		AbstractDomain domain = abstractDomainService.retrieveDomain(account.getDomain().getIdentifier());
		SizeUnitValueFunctionality globalQuotaFunctionality = functionalityReadOnlyService.getGlobalQuotaFunctionality(domain);
		SizeUnitValueFunctionality userQuotaFunctionality = functionalityReadOnlyService.getUserQuotaFunctionality(domain);
		if (globalQuotaFunctionality.getActivationPolicy().getStatus()) {
			return globalQuotaFunctionality.getPlainSize();
		}
		long userQuota = userQuotaFunctionality.getPlainSize();
		return userQuota;
	}

	@Override
	public InputStream getDocumentThumbnailStream(Account actor, Account owner, String uuid) throws BusinessException {
		preChecks(actor, owner);
		Validate.notEmpty(uuid, "document entry uuid is required.");
		DocumentEntry entry = find(actor, owner, uuid);
		checkThumbNailDownloadPermission(actor, owner, DocumentEntry.class,
				BusinessErrorCode.DOCUMENT_ENTRY_FORBIDDEN, entry);
		return documentEntryBusinessService.getDocumentThumbnailStream(entry);
	}

	@Override
	public InputStream getDocumentStream(Account actor, Account owner,
			String uuid) throws BusinessException {
		preChecks(actor, owner);
		Validate.notEmpty(uuid, "document entry uuid is required.");
		DocumentEntry entry = find(actor, owner, uuid);
		checkDownloadPermission(actor, owner, DocumentEntry.class,
				BusinessErrorCode.DOCUMENT_ENTRY_FORBIDDEN, entry);
		try {
			return documentEntryBusinessService.getDocumentStream(entry);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(BusinessErrorCode.FILE_UNREACHABLE, "no stream available.");
		}
	}

	@Override
	public DocumentEntry copyFromShareEntry(Account owner, ShareEntry share, DocumentEntry de,
			Calendar expirationDate) throws BusinessException {
		InputStream stream = null;
		DocumentEntry documentEntry = null;
		checkSpace(share.getSize(), share.getName(), owner);
		try {
			stream = documentEntryBusinessService.getDocumentStream(share.getDocumentEntry());
			documentEntry = documentEntryBusinessService.copyFromShareEntry(owner, share, stream, expirationDate);
			addDocSizeToGlobalUsedQuota(documentEntry.getDocument(), owner.getDomain());
		} catch ( org.springmodules.jcr.JcrSystemException e) {
			logger.error("unable to copy a shared file, file is missing on the filesystem");
			throw new BusinessException(BusinessErrorCode.FILE_UNREACHABLE, "copy failed, file missing on the filesystem.");
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return documentEntry;
	}

	@Override
	public boolean isSignatureActive(Account account) {
		return functionalityReadOnlyService.getSignatureFunctionality(account.getDomain()).getActivationPolicy().getStatus();
	}

	@Override
	public boolean isEnciphermentActive(Account account) {
		return functionalityReadOnlyService.getEnciphermentFunctionality(account.getDomain()).getActivationPolicy().getStatus();
	}

	@Override
	public boolean isGlobalQuotaActive(Account account) throws BusinessException {
		return functionalityReadOnlyService.getGlobalQuotaFunctionality(account.getDomain()).getActivationPolicy().getStatus();
	}

	@Override
	public boolean isUserQuotaActive(Account account) throws BusinessException {
		return functionalityReadOnlyService.getUserQuotaFunctionality(account.getDomain()).getActivationPolicy().getStatus();
	}

	@Override
	public Long getGlobalQuota(Account account) throws BusinessException {
		SizeUnitValueFunctionality globalQuotaFunctionality = functionalityReadOnlyService.getGlobalQuotaFunctionality(account.getDomain());
		return globalQuotaFunctionality.getPlainSize();
	}

	@Override
	public void renameDocumentEntry(Account actor, Account owner, String uuid,
			String newName) throws BusinessException {
		preChecks(actor, owner);
		Validate.notEmpty(uuid, "document entry uuid is required.");
		Validate.notEmpty(newName, "new name is required.");
		DocumentEntry entry = find(actor, owner, uuid);
		checkUpdatePermission(actor, owner, DocumentEntry.class,
				BusinessErrorCode.DOCUMENT_ENTRY_FORBIDDEN, entry);
		documentEntryBusinessService.renameDocumentEntry(entry, newName);
	}

	@Override
	public DocumentEntry updateFileProperties(Account actor, Account owner, String uuid, String newName, String fileComment, String meta) throws BusinessException {
		preChecks(actor, owner);
		Validate.notEmpty(uuid, "document entry uuid is required.");
		Validate.notEmpty(newName, "new name is required.");
		DocumentEntry entry = find(actor, owner, uuid);
		// on Tapestry interface update event, we have to check the value
		// of the metadata field to avoid overwriting the database value to null
		if (meta == null) {
			meta = entry.getMetaData();
		}
		if (fileComment == null) {
			fileComment = entry.getComment();
		}
		checkUpdatePermission(actor, owner, DocumentEntry.class,
				BusinessErrorCode.DOCUMENT_ENTRY_FORBIDDEN, entry);
		return documentEntryBusinessService.updateFileProperties(entry, newName, fileComment, meta);
	}

	@Override
	public void updateFileProperties(Account actor, String docEntryUuid,
			String newName, String fileComment, boolean isFromCmisSync)
			throws BusinessException {
		DocumentEntry entry = documentEntryBusinessService
				.find(docEntryUuid);
		if (!actor.hasSuperAdminRole() && !actor.hasSystemAccountRole()) {
			if (!entry.getEntryOwner().equals(actor)) {
				throw new BusinessException(BusinessErrorCode.FORBIDDEN,
						"You are not authorized to update this document.");
			}
		}
		if (!isFromCmisSync)
			entry.setCmisSync(false);
		else {
			documentEntryBusinessService.syncUniqueDocument(actor, newName);
			entry.setCmisSync(true);
		}
		documentEntryBusinessService.updateFileProperties(entry, newName,
				fileComment, null);
	}

	private void checkSpace(long size, String fileName, Account owner) throws BusinessException {
		// check the user quota
		if (size > getUserMaxFileSize(owner)) {
			logger.info("The file  " + fileName + " is larger than " + owner.getLsUuid() + " user's max file size.");
			String[] extras = { fileName };
			throw new BusinessException(BusinessErrorCode.FILE_TOO_LARGE, "The file is larger than user's max file size.", extras);
		}
		long availableSize = getAvailableSize(owner);
		if (availableSize < size) {
			logger.info("The file  " + fileName + " is too large to fit in " + owner.getLsUuid() + " user's space.");
			String[] extras = { fileName };
			throw new BusinessException(BusinessErrorCode.FILE_TOO_LARGE, "The file is too large to fit in user's space.", extras);
		}
	}

	private String sanitizeFileName(String fileName) throws BusinessException {
		fileName = fileName.replace("\\", "_");
		fileName = fileName.replace(":", "_");
		fileName = antiSamyService.clean(fileName);
		if (fileName.isEmpty()) {
			throw new BusinessException(BusinessErrorCode.INVALID_FILENAME,
					"fileName is empty after the xss filter");
		}
		return fileName;
	}

	private Boolean checkVirus(String fileName, Account owner, File file, Long size) throws BusinessException {
		if (logger.isDebugEnabled()) {
			logger.debug("antivirus is deactivated :" + virusScannerService.isDisabled());
		}
		if (virusscannerLimitFilesize != null
				&& size > virusscannerLimitFilesize) {
			if (logger.isDebugEnabled()) {
				logger.debug("antivirus skipped.");
			}
			return true;
		}
		boolean checkStatus = false;
		try {
			checkStatus = virusScannerService.check(file);
		} catch (TechnicalException e) {
			LogEntry logEntry = new AntivirusLogEntry(owner, LogAction.ANTIVIRUS_SCAN_FAILED, e.getMessage());
			logger.error("File scan failed: antivirus enabled but not available ?");
			logEntryService.create(LogEntryService.ERROR, logEntry);
			throw new BusinessException(BusinessErrorCode.FILE_SCAN_FAILED, "File scan failed", e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("antivirus scan result : " + checkStatus);
		}
		// check if the file contains virus
		if (!checkStatus) {
			LogEntry logEntry = new AntivirusLogEntry(owner, LogAction.FILE_WITH_VIRUS, fileName);
			logEntryService.create(LogEntryService.WARN, logEntry);
			logger.warn(owner.getLsUuid() + " tried to upload a file containing virus:" + fileName);
			String[] extras = { fileName };
			throw new BusinessException(BusinessErrorCode.FILE_CONTAINS_VIRUS, "File contains virus", extras);
		}
		return checkStatus;
	}

	private void addDocSizeToGlobalUsedQuota(Document docEntity, AbstractDomain domain) throws BusinessException {
		if (!overrideGlobalQuota) {
			long newUsedQuota = domain.getUsedSpace().longValue() + docEntity.getSize();
			domain.setUsedSpace(newUsedQuota);
			domainBusinessService.update(domain);
		}
	}

	private void removeDocSizeFromGlobalUsedQuota(long docSize, AbstractDomain domain) throws BusinessException {
		if (!overrideGlobalQuota) {
			long newUsedQuota = domain.getUsedSpace().longValue() - docSize;
			domain.setUsedSpace(newUsedQuota);
			domainBusinessService.update(domain);
		}
	}

	@Override
	public DocumentEntry findMoreRecentByName(Account actor, Account owner,
			String fileName) throws BusinessException {
		preChecks(actor, owner);
		Validate.notEmpty(fileName, "document entry name is required.");
		return documentEntryBusinessService.findMoreRecentByName(owner, fileName);
	}

	@Override
	public long getRelatedEntriesCount(Account actor, Account owner,
			DocumentEntry documentEntry) {
		preChecks(actor, owner);
		return documentEntryBusinessService.getRelatedEntriesCount(documentEntry);
	}

	@Override
	public void deleteOrComputeExpiryDate(SystemAccount actor,
			AbstractDomain domain, DocumentEntry documentEntry) {
			if (documentEntryBusinessService.getRelatedEntriesCount(documentEntry) == 0 ) {
				BooleanValueFunctionality deleteShareFunc= functionalityReadOnlyService.getDefaultShareExpiryTimeDeletionFunctionality(domain);
				// Test if we have to remove the document now.
				if (deleteShareFunc.getValue()) {
					logger.debug("Current document entry " + documentEntry.getRepresentation() + " need to be deleted.");
					deleteExpiredDocumentEntry(actor, documentEntry);
				} else {
					TimeUnitValueFunctionality fileExpirationFunc = functionalityReadOnlyService.getDefaultFileExpiryTimeFunctionality(domain);
					Calendar deletionDate = Calendar.getInstance();
					deletionDate.add(fileExpirationFunc.toCalendarValue(), fileExpirationFunc.getValue());
					documentEntry.setExpirationDate(deletionDate);
					documentEntryBusinessService.update(documentEntry);
				}
			}
	}

	@Override
	public List<String> findAllExpiredEntries(Account actor, Account owner) {
		preChecks(actor, owner);
		if (!actor.hasAllRights()) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You do not have the right to use this method.");
		}
		return documentEntryBusinessService.findAllExpiredEntries();
	}

	@Override
	public List<String> findAllEntriesWithoutExpirationDate(Account actor, Account owner) {
		preChecks(actor, owner);
		if (!actor.hasAllRights()) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You do not have the right to use this method.");
		}
		return documentEntryBusinessService.findAllEntriesWithoutExpirationDate();
	}
}
