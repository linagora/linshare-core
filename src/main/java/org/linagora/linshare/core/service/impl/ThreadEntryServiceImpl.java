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
 * and free version of LinShare™, powered by Linagora © 2009–2016. Contribute to
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
import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.core.domain.constants.OperationHistoryTypeEnum;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.OperationHistory;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.domain.entities.ThreadMember;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.rac.ThreadEntryResourceAccessControl;
import org.linagora.linshare.core.repository.ThreadMemberRepository;
import org.linagora.linshare.core.service.AntiSamyService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.MimeTypeService;
import org.linagora.linshare.core.service.QuotaService;
import org.linagora.linshare.core.service.ThreadEntryService;
import org.linagora.linshare.core.service.VirusScannerService;
import org.linagora.linshare.mongo.entities.logs.WorkGroupEntryAuditLogEntry;

public class ThreadEntryServiceImpl extends GenericEntryServiceImpl<Account, ThreadEntry>
		implements ThreadEntryService {

	private final DocumentEntryBusinessService documentEntryBusinessService;
	private final LogEntryService logEntryService;
	private final FunctionalityReadOnlyService functionalityReadOnlyService;
	private final MimeTypeService mimeTypeService;
	private final VirusScannerService virusScannerService;
	private final ThreadMemberRepository threadMemberRepository;
	private final MimeTypeMagicNumberDao mimeTypeIdentifier;
	private final AntiSamyService antiSamyService;
	private final OperationHistoryBusinessService operationHistoryBusinessService;
	private final QuotaService quotaService;

	public ThreadEntryServiceImpl(DocumentEntryBusinessService documentEntryBusinessService,
			LogEntryService logEntryService, FunctionalityReadOnlyService functionalityReadOnlyService,
			MimeTypeService mimeTypeService, VirusScannerService virusScannerService,
			ThreadMemberRepository threadMemberRepository, MimeTypeMagicNumberDao mimeTypeIdentifier,
			AntiSamyService antiSamyService, ThreadEntryResourceAccessControl rac,
			OperationHistoryBusinessService operationHistoryBusinessService, QuotaService quotaService) {
		super(rac);
		this.documentEntryBusinessService = documentEntryBusinessService;
		this.logEntryService = logEntryService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.mimeTypeService = mimeTypeService;
		this.virusScannerService = virusScannerService;
		this.threadMemberRepository = threadMemberRepository;
		this.mimeTypeIdentifier = mimeTypeIdentifier;
		this.antiSamyService = antiSamyService;
		this.operationHistoryBusinessService = operationHistoryBusinessService;
		this.quotaService = quotaService;
	}

	@Override
	public ThreadEntry createThreadEntry(Account actor, Account owner, Thread thread, File tempFile, String filename)
			throws BusinessException {
		checkCreatePermission(actor, owner, ThreadEntry.class, BusinessErrorCode.THREAD_ENTRY_FORBIDDEN, null, thread);
		filename = sanitizeFileName(filename); // throws
		Long size = tempFile.length();
		ThreadEntry threadEntry = null;

		try {
			String mimeType = mimeTypeIdentifier.getMimeType(tempFile);
			AbstractDomain domain = owner.getDomain();
			checkSpace(thread, size);

			// check if the file MimeType is allowed
			Functionality mimeFunctionality = functionalityReadOnlyService.getMimeTypeFunctionality(domain);
			if (mimeFunctionality.getActivationPolicy().getStatus()) {
				mimeTypeService.checkFileMimeType(owner, filename, mimeType);
			}

			Functionality antivirusFunctionality = functionalityReadOnlyService.getAntivirusFunctionality(domain);
			if (antivirusFunctionality.getActivationPolicy().getStatus()) {
				checkVirus(filename, owner, tempFile);
			}

			// want a timestamp on doc ?
			String timeStampingUrl = null;
			StringValueFunctionality timeStampingFunctionality = functionalityReadOnlyService
					.getTimeStampingFunctionality(domain);
			if (timeStampingFunctionality.getActivationPolicy().getStatus()) {
				timeStampingUrl = timeStampingFunctionality.getValue();
			}

			Functionality enciphermentFunctionality = functionalityReadOnlyService.getEnciphermentFunctionality(domain);
			Boolean checkIfIsCiphered = enciphermentFunctionality.getActivationPolicy().getStatus();

			threadEntry = documentEntryBusinessService.createThreadEntry(thread, tempFile, size, filename,
					checkIfIsCiphered, timeStampingUrl, mimeType);
			WorkGroupEntryAuditLogEntry log = new WorkGroupEntryAuditLogEntry(actor, owner, LogAction.CREATE,
					AuditLogEntryType.WORKGROUP_ENTRY, threadEntry);
			logEntryService.insert(log);
			addToQuota(thread, size);
		} finally {
			try {
				logger.debug("deleting temp file : " + tempFile.getName());
				tempFile.delete(); // remove the temporary file
			} catch (Exception e) {
				logger.error("can not delete temp file : " + e.getMessage());
			}
		}
		return threadEntry;
	}

	@Override
	public ThreadEntry copyFromDocumentEntry(Account actor, Account member,
			Thread thread, DocumentEntry documentEntry)
			throws BusinessException {
		checkCreatePermission(actor, member, ThreadEntry.class,
				BusinessErrorCode.THREAD_ENTRY_FORBIDDEN, null, thread);
		checkSpace(thread, documentEntry.getSize());
		AbstractDomain domain = member.getDomain();
		// check if the file MimeType is allowed
		Functionality mimeFunctionality = functionalityReadOnlyService.getMimeTypeFunctionality(domain);
		if (mimeFunctionality.getActivationPolicy().getStatus()) {
			mimeTypeService.checkFileMimeType(member, documentEntry.getName(), documentEntry.getType());
		}

		ThreadEntry threadEntry = documentEntryBusinessService.copyFromDocumentEntry(thread, documentEntry);
		WorkGroupEntryAuditLogEntry log = new WorkGroupEntryAuditLogEntry(actor, member, LogAction.CREATE,
				AuditLogEntryType.WORKGROUP_ENTRY, threadEntry);
		log.setCause(LogActionCause.COPY);
		logEntryService.insert(log);
		addToQuota(thread, documentEntry.getSize());
		return threadEntry;
	}

	public DocumentEntry copyFromThreadEntry(Account actor, Account member, Thread thread, ThreadEntry threadEntry) {
		checkCreatePermission(actor, member, ThreadEntry.class,
				BusinessErrorCode.THREAD_ENTRY_FORBIDDEN, null, thread);
		checkSpace(member, threadEntry.getSize());
		AbstractDomain domain = member.getDomain();
		// check if the file MimeType is allowed
		Functionality mimeFunctionality = functionalityReadOnlyService.getMimeTypeFunctionality(domain);
		if (mimeFunctionality.getActivationPolicy().getStatus()) {
			mimeTypeService.checkFileMimeType(member, threadEntry.getName(), threadEntry.getType());
		}
		DocumentEntry documentEntry = documentEntryBusinessService
				.copyFromThreadEntry(member, threadEntry,
						threadEntry.getExpirationDate());
		addToQuota(member, documentEntry.getSize());
		return documentEntry;
	}

	@Override
	public ThreadEntry find(Account actor, Account owner, String threadEntryUuid) throws BusinessException {
		ThreadEntry threadEntry = documentEntryBusinessService
				.findThreadEntryById(threadEntryUuid);
		if (threadEntry == null) {
			throw new BusinessException(BusinessErrorCode.THREAD_ENTRY_NOT_FOUND,
					"Thread entry with uuid : " + threadEntryUuid + " not found.");
		}
		checkReadPermission(actor, owner, ThreadEntry.class, BusinessErrorCode.THREAD_ENTRY_FORBIDDEN, threadEntry);
		return threadEntry;
	}

	@Override
	public void deleteThreadEntry(Account actor, Account owner, ThreadEntry threadEntry) throws BusinessException {
		Thread thread = (Thread) threadEntry.getEntryOwner();
		try {
			checkDeletePermission(actor, owner, ThreadEntry.class, BusinessErrorCode.THREAD_ENTRY_FORBIDDEN,
					threadEntry, thread);
			WorkGroupEntryAuditLogEntry log = new WorkGroupEntryAuditLogEntry(actor, owner, LogAction.DELETE,
					AuditLogEntryType.WORKGROUP_ENTRY, threadEntry);
			logEntryService.insert(log);
			documentEntryBusinessService.deleteThreadEntry(threadEntry);
			delFromQuota(thread, threadEntry.getSize());
		} catch (IllegalArgumentException e) {
			logger.error("Could not delete thread entry " + threadEntry.getUuid() + " in thread " + thread.getLsUuid()
					+ " by account " + owner.getLsUuid() + ", reason : ", e);
			throw new TechnicalException(TechnicalErrorCode.COULD_NOT_DELETE_DOCUMENT, "Could not delete document");
		}
	}

	@Override
	public void deleteInconsistentThreadEntry(SystemAccount actor, ThreadEntry threadEntry) throws BusinessException {
		Thread thread = (Thread) threadEntry.getEntryOwner();
		try {
			WorkGroupEntryAuditLogEntry log = new WorkGroupEntryAuditLogEntry(actor, actor, LogAction.DELETE,
					AuditLogEntryType.WORKGROUP_ENTRY, threadEntry);
			log.setCause(LogActionCause.INCONSISTENCY);
			logEntryService.insert(LogEntryService.WARN, log);
			documentEntryBusinessService.deleteThreadEntry(threadEntry);
			delFromQuota(thread, threadEntry.getSize());
		} catch (IllegalArgumentException e) {
			logger.error("Could not delete thread entry " + threadEntry.getUuid() + " in thread " + thread.getLsUuid()
					+ " by account " + actor.getLsUuid() + ", reason : ", e);
			throw new TechnicalException(TechnicalErrorCode.COULD_NOT_DELETE_DOCUMENT, "Could not delete document");
		}
	}

	@Override
	public List<ThreadEntry> findAllThreadEntries(Account actor, Account owner, Thread thread) throws BusinessException {
		checkListPermission(actor, owner, ThreadEntry.class,
				BusinessErrorCode.THREAD_ENTRY_FORBIDDEN, null, thread);
		checkListPermission(actor, owner, ThreadEntry.class, BusinessErrorCode.THREAD_ENTRY_FORBIDDEN, null, thread);
		return documentEntryBusinessService.findAllThreadEntries(thread);
	}

	@Override
	public InputStream getDocumentStream(Account actor, Account owner, String uuid) throws BusinessException {
		ThreadEntry threadEntry = find(actor, owner, uuid);
		checkDownloadPermission(actor, owner, ThreadEntry.class,
				BusinessErrorCode.THREAD_ENTRY_FORBIDDEN, threadEntry);
		WorkGroupEntryAuditLogEntry log = new WorkGroupEntryAuditLogEntry(actor, owner, LogAction.DOWNLOAD,
				AuditLogEntryType.WORKGROUP_ENTRY, threadEntry);
		logEntryService.insert(log);
		return documentEntryBusinessService.getDocumentStream(threadEntry);
	}

	@Override
	public InputStream getDocumentThumbnailStream(Account actor, Account owner, String uuid) throws BusinessException {
		ThreadEntry threadEntry = find(actor, owner, uuid);
		checkThumbNailDownloadPermission(actor, owner, ThreadEntry.class,
				BusinessErrorCode.THREAD_ENTRY_FORBIDDEN, threadEntry);
		return documentEntryBusinessService.getThreadEntryThumbnailStream(threadEntry);
	}

	@Override
	public boolean documentHasThumbnail(Account actor, String uuid) {
		ThreadEntry threadEntry = documentEntryBusinessService.findThreadEntryById(uuid);
		if (threadEntry == null) {
			logger.error("Can't find document entry, are you sure it is not a share ? : " + uuid);
			return false;
		}

		if (!this.isThreadMember((Thread) threadEntry.getEntryOwner(), (User) actor)) {
			return false;
		}
		String thmbUUID = threadEntry.getDocument().getThmbUuid();
		return (thmbUUID != null && thmbUUID.length() > 0);
	}

	@Override
	public ThreadEntry updateFileProperties(Account actor, Account owner,
			String threadEntryUuid, String fileComment, String metaData,
			String newName) throws BusinessException {
		ThreadEntry threadEntry = documentEntryBusinessService.findThreadEntryById(threadEntryUuid);
		WorkGroupEntryAuditLogEntry log = new WorkGroupEntryAuditLogEntry(actor, owner, LogAction.UPDATE,
				AuditLogEntryType.WORKGROUP_ENTRY, threadEntry);
		// Avoid overwritting metadata in database to null when update
		// threadEntry from interface.
		if (metaData == null) {
			metaData = threadEntry.getMetaData();
		}
		if (newName == null) {
			newName = threadEntry.getName();
		}
		if (fileComment == null) {
			fileComment = threadEntry.getComment();
		}
		if (!this.canUpload((Thread) threadEntry.getEntryOwner(), (User) owner)) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN,
					"You are not authorized to update this document.");
		}
		threadEntry = documentEntryBusinessService.updateFileProperties(threadEntry, fileComment, metaData,
				sanitizeFileName(newName));
		log.setResourceUpdated(threadEntry, owner);
		logEntryService.insert(log);
		return threadEntry;
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

	private Boolean checkVirus(String fileName, Account actor, File file) throws BusinessException {
		if (logger.isDebugEnabled()) {
			logger.debug("antivirus activation:" + !virusScannerService.isDisabled());
		}

		boolean checkStatus = false;
		try {
			checkStatus = virusScannerService.check(file);
		} catch (TechnicalException e) {
//			LogEntry logEntry = new AntivirusLogEntry(actor, LogAction.ANTIVIRUS_SCAN_FAILED, e.getMessage());
//			logEntryService.create(LogEntryService.ERROR, logEntry);
			logger.error("File scan failed: antivirus enabled but not available ?");
			throw new BusinessException(BusinessErrorCode.FILE_SCAN_FAILED, "File scan failed", e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("antivirus scan result : " + checkStatus);
		}
		// check if the file contains virus
		if (!checkStatus) {
//			LogEntry logEntry = new AntivirusLogEntry(actor, LogAction.FILE_WITH_VIRUS, fileName);
//			logEntryService.create(LogEntryService.WARN, logEntry);
			logger.warn(actor.getLsUuid() + " tried to upload a file containing virus:" + fileName);
			String[] extras = { fileName };
			throw new BusinessException(BusinessErrorCode.FILE_CONTAINS_VIRUS, "File contains virus", extras);
		}
		return checkStatus;
	}

	protected void checkSpace(Thread thread, long size) throws BusinessException {
		quotaService.checkIfUserCanAddFile(thread, size, ContainerQuotaType.WORK_GROUP);
	}

	protected void checkSpace(Account owner, long size) throws BusinessException {
		quotaService.checkIfUserCanAddFile(owner, size, ContainerQuotaType.USER);
	}

	protected void addToQuota(Thread thread, Long size) {
		OperationHistory oh = new OperationHistory(thread, thread.getDomain(), size, OperationHistoryTypeEnum.CREATE,
				ContainerQuotaType.WORK_GROUP);
		operationHistoryBusinessService.create(oh);
	}

	protected void addToQuota(Account owner, Long size) {
		OperationHistory oh = new OperationHistory(owner, owner.getDomain(), size, OperationHistoryTypeEnum.CREATE,
				ContainerQuotaType.USER);
		operationHistoryBusinessService.create(oh);
	}

	protected void delFromQuota(Thread thread, Long size) {
		OperationHistory oh = new OperationHistory(thread, thread.getDomain(), size, OperationHistoryTypeEnum.DELETE,
				ContainerQuotaType.WORK_GROUP);
		operationHistoryBusinessService.create(oh);
	}

	/**
	 * PERMISSIONS
	 */

	private boolean isThreadMember(Thread thread, User user) {
		ThreadMember threadMember = threadMemberRepository.findUserThreadMember(thread, user);
		return threadMember != null;
	}

	private boolean canUpload(Thread thread, User user) {
		ThreadMember threadMember = threadMemberRepository.findUserThreadMember(thread, user);
		return threadMember.getCanUpload();
	}

	@Override
	public List<ThreadEntry> findMoreRecentByName(Account actor, Thread thread) throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		Validate.notNull(thread, "Thread must be set.");
		if (!isThreadMember(thread, (User) actor)) {
			throw new BusinessException(BusinessErrorCode.THREAD_ENTRY_FORBIDDEN,
					"The actor is not member of the thread.");
		}

		return documentEntryBusinessService.findMoreRecentByName(thread);
	}
}
