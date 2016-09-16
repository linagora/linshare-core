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
import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AntivirusLogEntry;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.LogEntry;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.domain.entities.ThreadLogEntry;
import org.linagora.linshare.core.domain.entities.ThreadMember;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.rac.ThreadEntryResourceAccessControl;
import org.linagora.linshare.core.repository.ThreadMemberRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AntiSamyService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.MimeTypeService;
import org.linagora.linshare.core.service.ThreadEntryService;
import org.linagora.linshare.core.service.VirusScannerService;

public class ThreadEntryServiceImpl extends GenericEntryServiceImpl<Account, ThreadEntry> implements ThreadEntryService {

	private final DocumentEntryBusinessService documentEntryBusinessService;
	private final LogEntryService logEntryService;
	private final AbstractDomainService abstractDomainService;
	private final FunctionalityReadOnlyService functionalityReadOnlyService;
	private final MimeTypeService mimeTypeService;
	private final AccountService accountService;
	private final VirusScannerService virusScannerService;
	private final ThreadMemberRepository threadMemberRepository;
	private final MimeTypeMagicNumberDao mimeTypeIdentifier;
	private final AntiSamyService antiSamyService;

	public ThreadEntryServiceImpl(
			DocumentEntryBusinessService documentEntryBusinessService,
			LogEntryService logEntryService,
			AbstractDomainService abstractDomainService,
			FunctionalityReadOnlyService functionalityReadOnlyService,
			MimeTypeService mimeTypeService, AccountService accountService,
			VirusScannerService virusScannerService,
			ThreadMemberRepository threadMemberRepository,
			MimeTypeMagicNumberDao mimeTypeIdentifier,
			AntiSamyService antiSamyService,
			ThreadEntryResourceAccessControl rac) {
		super(rac);
		this.documentEntryBusinessService = documentEntryBusinessService;
		this.logEntryService = logEntryService;
		this.abstractDomainService = abstractDomainService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.mimeTypeService = mimeTypeService;
		this.accountService = accountService;
		this.virusScannerService = virusScannerService;
		this.threadMemberRepository = threadMemberRepository;
		this.mimeTypeIdentifier = mimeTypeIdentifier;
		this.antiSamyService = antiSamyService;
	}

	@Override
	public ThreadEntry createThreadEntry(Account actor, Account owner, Thread thread, File tempFile, String filename) throws BusinessException {
		checkCreatePermission(actor, owner, ThreadEntry.class,
				BusinessErrorCode.THREAD_ENTRY_FORBIDDEN, null, thread);
		filename = sanitizeFileName(filename); // throws
		Long size = tempFile.length();
		ThreadEntry threadEntry = null;

		try {
			String mimeType = mimeTypeIdentifier.getMimeType(tempFile);
			AbstractDomain domain = owner.getDomain();

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
			StringValueFunctionality timeStampingFunctionality = functionalityReadOnlyService.getTimeStampingFunctionality(domain);
			if (timeStampingFunctionality.getActivationPolicy().getStatus()) {
				timeStampingUrl = timeStampingFunctionality.getValue();
			}

			Functionality enciphermentFunctionality = functionalityReadOnlyService.getEnciphermentFunctionality(domain);
			Boolean checkIfIsCiphered = enciphermentFunctionality.getActivationPolicy().getStatus();

			threadEntry = documentEntryBusinessService.createThreadEntry(thread, tempFile, size, filename, checkIfIsCiphered, timeStampingUrl, mimeType);
			logEntryService.create(new ThreadLogEntry(owner, threadEntry, LogAction.THREAD_UPLOAD_ENTRY, "Uploading a file in a thread."));
		} finally {
			try{
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
		AbstractDomain domain = member.getDomain();
		// check if the file MimeType is allowed
		Functionality mimeFunctionality = functionalityReadOnlyService.getMimeTypeFunctionality(domain);
		if (mimeFunctionality.getActivationPolicy().getStatus()) {
			mimeTypeService.checkFileMimeType(member, documentEntry.getName(), documentEntry.getType());
		}

		ThreadEntry threadEntry = documentEntryBusinessService.copyFromDocumentEntry(thread, documentEntry);
		logEntryService.create(new ThreadLogEntry(member, threadEntry, LogAction.THREAD_UPLOAD_ENTRY, "Uploading a file in a thread."));
		return threadEntry;
	}

	public DocumentEntry copyFromThreadEntry(Account actor, Account member, Thread thread, ThreadEntry threadEntry) {
		checkCreatePermission(actor, member, ThreadEntry.class,
				BusinessErrorCode.THREAD_ENTRY_FORBIDDEN, null, thread);
		AbstractDomain domain = member.getDomain();
		// check if the file MimeType is allowed
		Functionality mimeFunctionality = functionalityReadOnlyService.getMimeTypeFunctionality(domain);
		if (mimeFunctionality.getActivationPolicy().getStatus()) {
			mimeTypeService.checkFileMimeType(member, threadEntry.getName(), threadEntry.getType());
		}
		DocumentEntry documentEntry = documentEntryBusinessService
				.copyFromThreadEntry(member, threadEntry,
						threadEntry.getExpirationDate());
		return documentEntry;
	}

	@Override
	public ThreadEntry find(Account actor, Account owner, String threadEntryUuid) throws BusinessException {
		ThreadEntry threadEntry = documentEntryBusinessService
				.findThreadEntryById(threadEntryUuid);
		if (threadEntry == null) {
			throw new BusinessException(
					BusinessErrorCode.THREAD_ENTRY_NOT_FOUND,
					"Thread entry with uuid : " + threadEntryUuid
							+ " not found.");
		}
		checkReadPermission(actor, owner, ThreadEntry.class,
				BusinessErrorCode.THREAD_ENTRY_FORBIDDEN, threadEntry);
		return threadEntry;
	}

	@Override
	public void deleteThreadEntry(Account actor, Account owner, ThreadEntry threadEntry) throws BusinessException {
		Thread thread = (Thread) threadEntry.getEntryOwner();
		try {
			checkDeletePermission(actor, owner, ThreadEntry.class,
					BusinessErrorCode.THREAD_ENTRY_FORBIDDEN, threadEntry,
					thread);
			ThreadLogEntry log = new ThreadLogEntry(owner, threadEntry,
					LogAction.THREAD_REMOVE_ENTRY, "Deleting a thread entry.");
			documentEntryBusinessService.deleteThreadEntry(threadEntry);
			logEntryService.create(log);
		} catch (IllegalArgumentException e) {
			logger.error(
					"Could not delete thread entry " + threadEntry.getUuid()
							+ " in thread " + thread.getLsUuid()
							+ " by account " + owner.getLsUuid()
							+ ", reason : ", e);
			throw new TechnicalException(
					TechnicalErrorCode.COULD_NOT_DELETE_DOCUMENT,
					"Could not delete document");
		}
	}

	@Override
	public void deleteInconsistentThreadEntry(SystemAccount actor,
			ThreadEntry threadEntry) throws BusinessException {
		Thread owner = (Thread) threadEntry.getEntryOwner();
		try {
			ThreadLogEntry log = new ThreadLogEntry(actor, threadEntry,
					LogAction.THREAD_REMOVE_INCONSISTENCY_ENTRY,
					"Deleting an inconsistent thread entry.");
			logEntryService.create(LogEntryService.WARN, log);
			documentEntryBusinessService.deleteThreadEntry(threadEntry);
		} catch (IllegalArgumentException e) {
			logger.error(
					"Could not delete thread entry " + threadEntry.getUuid()
							+ " in thread " + owner.getLsUuid()
							+ " by account " + actor.getLsUuid()
							+ ", reason : ", e);
			throw new TechnicalException(
					TechnicalErrorCode.COULD_NOT_DELETE_DOCUMENT,
					"Could not delete document");
		}
	}

	@Override
	public List<ThreadEntry> findAllThreadEntries(Account actor, Account owner, Thread thread) throws BusinessException {
		checkListPermission(actor, owner, ThreadEntry.class,
				BusinessErrorCode.THREAD_ENTRY_FORBIDDEN, null, thread);
		return documentEntryBusinessService.findAllThreadEntries(thread);
	}

	@Override
	public InputStream getDocumentStream(Account actor, Account owner, String uuid) throws BusinessException {
		ThreadEntry threadEntry = find(actor, owner, uuid);
		checkDownloadPermission(actor, owner, ThreadEntry.class,
				BusinessErrorCode.THREAD_ENTRY_FORBIDDEN, threadEntry);
		logEntryService.create(new ThreadLogEntry(actor, threadEntry, LogAction.THREAD_DOWNLOAD_ENTRY, "Downloading a file in a thread."));
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
		ThreadEntry threadEntry = documentEntryBusinessService
				.findThreadEntryById(threadEntryUuid);
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
		return documentEntryBusinessService.updateFileProperties(threadEntry,
				fileComment, metaData, sanitizeFileName(newName));
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

	private Boolean checkVirus(String fileName, Account actor, File file) throws BusinessException {
		if (logger.isDebugEnabled()) {
			logger.debug("antivirus activation:" + !virusScannerService.isDisabled());
		}

		boolean checkStatus = false;
		try {
			checkStatus = virusScannerService.check(file);
		} catch (TechnicalException e) {
			LogEntry logEntry = new AntivirusLogEntry(actor, LogAction.ANTIVIRUS_SCAN_FAILED, e.getMessage());
			logger.error("File scan failed: antivirus enabled but not available ?");
			logEntryService.create(LogEntryService.ERROR, logEntry);
			throw new BusinessException(BusinessErrorCode.FILE_SCAN_FAILED, "File scan failed", e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("antivirus scan result : " + checkStatus);
		}
		// check if the file contains virus
		if (!checkStatus) {
			LogEntry logEntry = new AntivirusLogEntry(actor, LogAction.FILE_WITH_VIRUS, fileName);
			logEntryService.create(LogEntryService.WARN, logEntry);
			logger.warn(actor.getLsUuid() + " tried to upload a file containing virus:" + fileName);
			String[] extras = { fileName };
			throw new BusinessException(BusinessErrorCode.FILE_CONTAINS_VIRUS, "File contains virus", extras);
		}
		return checkStatus;
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
	public List<ThreadEntry> findMoreRecentByName(Account actor, Thread thread)
			throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		Validate.notNull(thread, "Thread must be set.");
		if (!isThreadMember(thread, (User) actor)) {
			throw new BusinessException(BusinessErrorCode.THREAD_ENTRY_FORBIDDEN, "The actor is not member of the thread.");
		}

		return documentEntryBusinessService.findMoreRecentByName(thread);
	}
}
