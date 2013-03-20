/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.business.service.TagBusinessService;
import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.FileLogEntry;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.domain.entities.ThreadLogEntry;
import org.linagora.linshare.core.domain.entities.ThreadMember;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.repository.ThreadMemberRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.MimeTypeService;
import org.linagora.linshare.core.service.ThreadEntryService;
import org.linagora.linshare.core.service.VirusScannerService;
import org.linagora.linshare.core.utils.DocumentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadEntryServiceImpl implements ThreadEntryService {

	private static final Logger logger = LoggerFactory.getLogger(ThreadEntryServiceImpl.class);

	private final DocumentEntryBusinessService documentEntryBusinessService;
	private final LogEntryService logEntryService;
	private final AbstractDomainService abstractDomainService;
	private final FunctionalityService functionalityService;
	private final MimeTypeService mimeTypeService;
	private final AccountService accountService;
	private final VirusScannerService virusScannerService;
	private final TagBusinessService tagBusinessService;
	private final ThreadMemberRepository threadMemberRepository;
	private final MimeTypeMagicNumberDao mimeTypeIdentifier;

	public ThreadEntryServiceImpl(DocumentEntryBusinessService documentEntryBusinessService, LogEntryService logEntryService, AbstractDomainService abstractDomainService,
			FunctionalityService functionalityService, MimeTypeService mimeTypeService, AccountService accountService, VirusScannerService virusScannerService, TagBusinessService tagBusinessService,
			ThreadMemberRepository threadMemberRepository, MimeTypeMagicNumberDao mimeTypeIdentifier) {
		super();
		this.documentEntryBusinessService = documentEntryBusinessService;
		this.logEntryService = logEntryService;
		this.abstractDomainService = abstractDomainService;
		this.functionalityService = functionalityService;
		this.mimeTypeService = mimeTypeService;
		this.accountService = accountService;
		this.virusScannerService = virusScannerService;
		this.tagBusinessService = tagBusinessService;
		this.threadMemberRepository = threadMemberRepository;
		this.mimeTypeIdentifier = mimeTypeIdentifier;
	}

	@Override
	public ThreadEntry createThreadEntry(Account actor, Thread thread, InputStream stream, Long size, String fileName) throws BusinessException {
		DocumentUtils util = new DocumentUtils();
		File tempFile = util.getTempFile(stream, fileName);
		ThreadEntry threadEntry = null;

		try {
			String mimeType = mimeTypeIdentifier.getMimeType(tempFile);
			AbstractDomain domain = abstractDomainService.retrieveDomain(actor.getDomain().getIdentifier());

			// check if the file MimeType is allowed
			Functionality mimeFunctionality = functionalityService.getMimeTypeFunctionality(domain);
			if (mimeFunctionality.getActivationPolicy().getStatus()) {
				mimeTypeService.checkFileMimeType(fileName, mimeType, actor);
			}

			Functionality antivirusFunctionality = functionalityService.getAntivirusFunctionality(domain);
			if (antivirusFunctionality.getActivationPolicy().getStatus()) {
				// TODO antivirus check for thread entries
				// checkVirus(fileName, actor, tempFile);
			}

			// want a timestamp on doc ?
			String timeStampingUrl = null;
			StringValueFunctionality timeStampingFunctionality = functionalityService.getTimeStampingFunctionality(domain);
			if (timeStampingFunctionality.getActivationPolicy().getStatus()) {
				timeStampingUrl = timeStampingFunctionality.getValue();
			}

			Functionality enciphermentFunctionality = functionalityService.getEnciphermentFunctionality(domain);
			Boolean checkIfIsCiphered = enciphermentFunctionality.getActivationPolicy().getStatus();

			threadEntry = documentEntryBusinessService.createThreadEntry(thread, tempFile, size, fileName, checkIfIsCiphered, timeStampingUrl, mimeType);
			logEntryService.create(new ThreadLogEntry(actor, thread, LogAction.THREAD_UPLOAD_ENTRY, "Uploading a file in a thread."));
			tagBusinessService.runTagFiltersOnThreadEntry(actor, thread, threadEntry);
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
	public ThreadEntry findById(Account actor, String threadEntryUuid) throws BusinessException {
		ThreadEntry threadEntry = documentEntryBusinessService.findThreadEntryById(threadEntryUuid);
		if (!this.isThreadMember((Thread) threadEntry.getEntryOwner(), (User) actor)) {
			throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to delete this document.");
		}
		return threadEntry;
	}

	@Override
	public void deleteThreadEntry(Account actor, ThreadEntry threadEntry) throws BusinessException {
		try {
			if (!this.isAdmin((Thread) threadEntry.getEntryOwner(), (User) actor)) {
				throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to delete this document.");
			}
			ThreadLogEntry log = new ThreadLogEntry(actor, threadEntry, LogAction.THREAD_REMOVE_ENTRY, "Deleting a thread entry.");
			documentEntryBusinessService.deleteThreadEntry(threadEntry);
			logEntryService.create(log);
		} catch (IllegalArgumentException e) {
			logger.error("Could not delete file " + threadEntry.getName() + " of user " + actor.getLsUuid() + ", reason : ", e);
			throw new TechnicalException(TechnicalErrorCode.COULD_NOT_DELETE_DOCUMENT, "Could not delete document");
		}
	}

	@Override
	public List<ThreadEntry> findAllThreadEntries(Account actor, Thread thread) throws BusinessException {
		if (!this.isThreadMember(thread, (User) actor)) {
			return new ArrayList<ThreadEntry>();
		}
		return documentEntryBusinessService.findAllThreadEntries(thread);
	}

	@Override
	public List<ThreadEntry> findAllThreadEntriesTaggedWith(Account actor, Thread thread, String[] names) {
		if (!this.isThreadMember(thread, (User) actor)) {
			return new ArrayList<ThreadEntry>();
		}
		return documentEntryBusinessService.findAllThreadEntriesTaggedWith(thread, names);
	}

	@Override
	public InputStream getDocumentStream(Account actor, String uuid) throws BusinessException {
		ThreadEntry threadEntry = documentEntryBusinessService.findThreadEntryById(uuid);
		if (threadEntry == null) {
			logger.error("Can't find document entry, are you sure it is not a share ? : " + uuid);
			return null;
		}
		if (!this.isThreadMember((Thread) threadEntry.getEntryOwner(), (User) actor)) {
			throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to get this document.");
		}
		return documentEntryBusinessService.getDocumentStream(threadEntry);
	}

	@Override
	public InputStream getDocumentThumbnailStream(Account owner, String uuid) throws BusinessException {
		ThreadEntry threadEntry = documentEntryBusinessService.findThreadEntryById(uuid);
		if (threadEntry == null) {
			logger.error("Can't find document entry, are you sure it is not a share ? : " + uuid);
			return null;
		}
		if (!this.isThreadMember((Thread) threadEntry.getEntryOwner(), (User) owner)) {
			throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to get thumbnail for this document.");
		}
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
	public void updateFileProperties(Account actor, String threadEntryUuid, String fileComment) throws BusinessException {
		ThreadEntry threadEntry = documentEntryBusinessService.findThreadEntryById(threadEntryUuid);
		if (!this.canUpload((Thread) threadEntry.getEntryOwner(), (User) actor)) {
			throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to update this document.");
		}
		documentEntryBusinessService.updateFileProperties(threadEntry, fileComment);
	}

	/**
	 * PERMISSIONS
	 */

	private boolean isThreadMember(Thread thread, User user) {
		ThreadMember threadMember = threadMemberRepository.findUserThreadMember(thread, user);
		return threadMember != null;
	}

	private boolean isAdmin(Thread thread, User user) {
		ThreadMember threadMember = threadMemberRepository.findUserThreadMember(thread, user);
		return threadMember.getAdmin();
	}

	private boolean canUpload(Thread thread, User user) {
		ThreadMember threadMember = threadMemberRepository.findUserThreadMember(thread, user);
		return threadMember.getCanUpload();
	}
}
