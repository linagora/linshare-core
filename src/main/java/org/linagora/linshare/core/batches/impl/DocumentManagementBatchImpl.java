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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.cxf.helpers.IOUtils;
import org.linagora.linshare.core.batches.DocumentManagementBatch;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Batch for document management.
 */
public class DocumentManagementBatchImpl implements DocumentManagementBatch {

	private static final Logger logger = LoggerFactory.getLogger(DocumentManagementBatchImpl.class);

	private final DocumentRepository documentRepository;

	private final DocumentEntryRepository documentEntryRepository;

	private final DocumentEntryBusinessService documentEntryBusinessService;

	private final AccountRepository<Account> accountRepository;

	private final FileDataStore fileDataStore;

	private final boolean cronJackRabbitKeepAlive;

	public DocumentManagementBatchImpl(
			boolean cronJackRabbitKeepAlive,
			DocumentRepository documentRepository,
			DocumentEntryRepository documentEntryRepository,
			AccountRepository<Account> accountRepository,
			FileDataStore fileDataStore,
			DocumentEntryBusinessService documentEntryBusinessService) {
		super();
		this.documentRepository = documentRepository;
		this.documentEntryRepository = documentEntryRepository;
		this.accountRepository = accountRepository;
		this.fileDataStore = fileDataStore;
		this.documentEntryBusinessService = documentEntryBusinessService;
		this.cronJackRabbitKeepAlive = cronJackRabbitKeepAlive;
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

	@Override
	public void jackRabbitKeepAlive() {
		logger.debug("jackRabbitKeepAlive - begin");
		if (cronJackRabbitKeepAlive) {
			SystemAccount actor = accountRepository.getBatchSystemAccount();
			List<DocumentEntry> findAllMyDocumentEntries = documentEntryRepository.findAllMyDocumentEntries(actor);
			for (DocumentEntry documentEntry : findAllMyDocumentEntries) {
				String uuid = documentEntry.getDocument().getUuid();
				logger.debug("removing resource : " + uuid);
				try {
					documentEntryBusinessService.deleteDocumentEntry(documentEntry);
				} catch (Exception e) {
					logger.error("exception : ", e);
				}
			}
			try {
				uploadTestFile(actor);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		} else {
			logger.debug("jackRabbitKeepAlive - skip");
		}
		logger.debug("jackRabbitKeepAlive - end");
	}

	private void uploadTestFile(SystemAccount actor) throws Exception {
		logger.debug("uploading file ...");
		String filePath = "jackRabbit.properties";
		InputStream inputStream = java.lang.Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
		File tempFile = File.createTempFile("linshare-",".tmp");
		try (FileOutputStream fos = new FileOutputStream(tempFile)) {
			tempFile.deleteOnExit();
			IOUtils.copy(inputStream, fos);
		}
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, "test/plain", 561L, filePath);
		metadata = fileDataStore.add(inputStream, metadata);
		Document document = new Document(metadata.getUuid(), "text/plain", 561L);
		try {
			inputStream = java.lang.Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
			document.setSha256sum(documentEntryBusinessService.SHA256CheckSumFileStream(inputStream));
			inputStream.close();
			documentRepository.create(document);
			DocumentEntry docEntry = new DocumentEntry(actor, "inputStream test", "", document);
			docEntry = documentEntryRepository.create(docEntry);
			actor.getEntries().add(docEntry );
		} catch (IOException e) {
			logger.error("exception : ", e);
		}
		logger.debug("file uploaded");
	}

}
