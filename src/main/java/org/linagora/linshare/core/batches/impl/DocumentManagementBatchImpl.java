/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
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
package org.linagora.linshare.core.batches.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.linagora.linshare.core.batches.DocumentManagementBatch;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.dao.FileSystemDao;
import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.EntryService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.MailContentBuildingService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.ThreadEntryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Batch for document management.
 */
public class DocumentManagementBatchImpl implements DocumentManagementBatch {

	private static final Logger logger = LoggerFactory.getLogger(DocumentManagementBatchImpl.class);

	private final DocumentRepository documentRepository;

	private final DocumentEntryRepository documentEntryRepository;

	private final DocumentEntryService documentEntryService;

	private final ThreadEntryService threadEntryService;

	private final DocumentEntryBusinessService documentEntryBusinessService;

	private final AccountRepository<Account> accountRepository;

	private final FileSystemDao fileSystemDao;

	private final boolean cronActivated;

	private final NotifierService notifierService;

	private final MailContentBuildingService mailBuilder;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final EntryService entryService;

	private final MimeTypeMagicNumberDao mimeTypeMagicNumberDao;



	public DocumentManagementBatchImpl(DocumentRepository documentRepository,
			DocumentEntryRepository documentEntryRepository, DocumentEntryService documentEntryService,
			AccountRepository<Account> accountRepository, FileSystemDao fileSystemDao, boolean cronActivated,
			NotifierService notifierService, MailContentBuildingService mailBuilder,
			FunctionalityReadOnlyService functionalityService, EntryService entryService,
			DocumentEntryBusinessService documentEntryBusinessService, MimeTypeMagicNumberDao mimeTypeMagicNumberDao, ThreadEntryService threadEntryService) {
		super();
		this.documentRepository = documentRepository;
		this.documentEntryRepository = documentEntryRepository;
		this.documentEntryService = documentEntryService;
		this.accountRepository = accountRepository;
		this.fileSystemDao = fileSystemDao;
		this.cronActivated = cronActivated;
		this.notifierService = notifierService;
		this.mailBuilder = mailBuilder;
		this.functionalityReadOnlyService = functionalityService;
		this.entryService = entryService;
		this.documentEntryBusinessService = documentEntryBusinessService;
		this.mimeTypeMagicNumberDao = mimeTypeMagicNumberDao;
		this.threadEntryService = threadEntryService;
	}

	private String getCpt(long curr, long total) {
		return String.valueOf(curr) + "/" + String.valueOf(total) + ":";
	}

	@Override
	public void removeMissingDocuments() {
		SystemAccount systemAccount = accountRepository.getBatchSystemAccount();

		List<Document> documents = documentRepository.findAll();
		long totalDocument = documents.size();
		logger.info("Remove missing documents batch launched : {}", totalDocument);
		long cpt = 0;

		for (Document document : documents) {
			cpt++;
			logger.debug(getCpt(cpt, totalDocument) + "processing current document : {}", document.getUuid());

			InputStream stream = null;
			try {
				stream = fileSystemDao.getFileContentByUUID(document.getUuid());
			} catch (org.springmodules.jcr.JcrSystemException e) {
				// TODO : should never happen ! need to send a notification to
				// admins.
				logger.error("Document with UID = {} was not found in datastore.", document.getUuid());
				logger.debug(e.toString());
			}

			if (stream == null) {
				try {
					if (document.getDocumentEntry() != null) {
						logger.info("Removing document (document entry) with UID = {} because of inconsistency",
								document.getUuid());
						entryService.deleteAllInconsistentShareEntries(systemAccount, document.getDocumentEntry());
						documentEntryService
								.deleteInconsistentDocumentEntry(systemAccount, document.getDocumentEntry());
					} else if (document.getThreadEntry() != null) {
						logger.info("Removing document (thread entry) with UID = {} because of inconsistency",
								document.getUuid());
						threadEntryService.deleteInconsistentThreadEntry(systemAccount, document.getThreadEntry());
					} else {
						logger.warn("Removing a document unrelated to an entry with UID = {} because of inconsistency",
								document.getUuid());
					}
				} catch (BusinessException ex) {
					logger.error(
							"Error when processing cleaning of document with UID = {} during consistency check process",
							document.getUuid());
					logger.debug(ex.toString());
				}
			} else {
				try {
					stream.close();
				} catch (IOException e) {
					logger.error("Error when closing document stream '{}' during consistency check ",
							document.getUuid());
					logger.debug(e.getMessage());
				}
			}
		}
		logger.info("Remove missing documents batch ended.");
	}

	@Override
	public void cleanOldDocuments() {
		logger.debug("cleanOldDocuments : begin");

		if (!cronActivated) {
			logger.info("Documents cleaner batch launched but was told to be unactivated (cf linshare.properties): stopping.");
			return;
		}
		cleanExpiredDocumentEntries();
		logger.info("Documents cleaner batch ended.");
	}

	private void cleanExpiredDocumentEntries() {
		logger.info("Document entries cleaner batch launched.");

		List<DocumentEntry> findAllExpiredEntries = documentEntryRepository.findAllExpiredEntries();
		logger.info("Expired documents found : " + findAllExpiredEntries.size());
		SystemAccount systemAccount = accountRepository.getBatchSystemAccount();
		Calendar now = GregorianCalendar.getInstance();

		for (DocumentEntry documentEntry : findAllExpiredEntries) {
			// we check if there is not related shares. Should not happen.
			if (documentEntryBusinessService.getRelatedEntriesCount(documentEntry) <= 0) {

				TimeUnitValueFunctionality fileExpirationTimeFunctionality = functionalityReadOnlyService
						.getDefaultFileExpiryTimeFunctionality(documentEntry.getEntryOwner().getDomain());
				if (fileExpirationTimeFunctionality.getActivationPolicy().getStatus()) {
					if (documentEntry.getExpirationDate().before(now)) {
						try {
							documentEntryService.deleteExpiredDocumentEntry(systemAccount, documentEntry);
						} catch (BusinessException e) {
							logger.error("Can't delete expired document entry : " + documentEntry.getUuid() + " : "
									+ e.getMessage());
							logger.debug(e.toString());
						}
					}
				}
			} else {
				logger.warn("expired document with shares found : " + documentEntry.getUuid());
			}
		}
	}

	@Override
	public void checkDocumentsMimeType() {
		logger.info("checkDocumentEntriesMimeType : begin");
		List<Document> docs = documentRepository.findAllMimeTypeCheckNeededDocuments();

		logger.info("Found " + docs.size() + " document(s) in need of a MIME type check.");
		for (Document doc : docs) {
			try {
				logger.debug("retrieve from JackRabbit : " + doc.getUuid());
				InputStream stream = fileSystemDao.getFileContentByUUID(doc.getUuid());
				if (stream != null) {
					String type = mimeTypeMagicNumberDao.getMimeType(stream);
					try {
						stream.close();
					} catch (IOException e) {
						logger.error("Error when closing document stream '{}' during consistency check ", doc.getUuid());
						logger.debug(e.getMessage());
					}
					doc.setType(type);
					doc.setCheckMimeType(false);
					documentRepository.update(doc);
					logger.info("Changing document : " + doc.getUuid() + " Mime Type to " + type);
				} else {
					logger.warn("the file {} is missing in the content repository.", doc.getUuid());
				}
			} catch (BusinessException e) {
				logger.error("Can't find file with uuid : " + doc.getUuid() + " : " + e.getMessage());
				logger.debug(e.toString());
			}
		}
		logger.info("checkDocumentEntriesMimeType : batch ended");
	}

	@SuppressWarnings("unused")
	private void sendUpcomingDeletionNotification(DocumentEntry document, Integer days) {
		final long MILISECOND_PER_DAY = 24 * 60 * 60 * 1000;
		// int days = Math.round(Math.abs((deletionDate.getTimeInMillis()-
		// now.getTimeInMillis())/MILISECOND_PER_DAY))+1;
		// sendUpcomingDeletionNotification(document, days);

		// MailContainer mailContainer = new MailContainer("", Language.FRENCH);
		// try {
		//
		// TODO : FMA : TO BE FIX
		// notifierService.sendNotification(mailBuilder.buildMailUpcomingOutdatedDocumentWithOneRecipient((User)document.getEntryOwner(),
		// mailContainer, document, days));
		// } catch (BusinessException e) {
		// logger.error("Can't create the email for "+
		// ((User)document.getEntryOwner()).getMail());
		// e.printStackTrace();
		// }
	}

}
