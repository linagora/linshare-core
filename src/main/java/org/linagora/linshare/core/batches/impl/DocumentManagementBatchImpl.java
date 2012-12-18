/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linshare.core.batches.impl;

import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.linagora.linshare.core.batches.DocumentManagementBatch;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.dao.FileSystemDao;
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
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.core.service.MailContentBuildingService;
import org.linagora.linshare.core.service.NotifierService;
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
    private final DocumentEntryBusinessService documentEntryBusinessService;
    
    private final AccountRepository<Account> accountRepository;
    
    private final FileSystemDao fileSystemDao;
    private final boolean cronActivated;
	private final NotifierService notifierService;
	private final MailContentBuildingService mailBuilder;
	private final FunctionalityService functionalityService;
	private final EntryService entryService;

	
	
	public DocumentManagementBatchImpl(DocumentRepository documentRepository, DocumentEntryRepository documentEntryRepository, DocumentEntryService documentEntryService,
			AccountRepository<Account> accountRepository, FileSystemDao fileSystemDao, boolean cronActivated, NotifierService notifierService,
			MailContentBuildingService mailBuilder, FunctionalityService functionalityService, EntryService entryService, DocumentEntryBusinessService documentEntryBusinessService) {
		super();
		this.documentRepository = documentRepository;
		this.documentEntryRepository = documentEntryRepository;
		this.documentEntryService = documentEntryService;
		this.accountRepository = accountRepository;
		this.fileSystemDao = fileSystemDao;
		this.cronActivated = cronActivated;
		this.notifierService = notifierService;
		this.mailBuilder = mailBuilder;
		this.functionalityService = functionalityService;
		this.entryService = entryService;
		this.documentEntryBusinessService = documentEntryBusinessService;
	}

	//    public DocumentManagementBatchImpl(DocumentRepository documentRepository, DocumentEntryService documentEntryService,
//        FileSystemDao fileSystemDao, boolean securedStorageDisallowed, boolean cronActivated,
//        NotifierService notifierService, MailContentBuildingService mailBuilder, FunctionalityService functionalityService) {
//        this.documentRepository = documentRepository;
//        this.documentEntryService = documentEntryService;
//        this.fileSystemDao = fileSystemDao;
//        this.securedStorageDisallowed = securedStorageDisallowed;
//        this.cronActivated = cronActivated;
//        this.notifierService = notifierService;
//        this.mailBuilder = mailBuilder;
//        this.functionalityService = functionalityService;
//    }

	
	@Override
    public void removeMissingDocuments() {
    	SystemAccount systemAccount = accountRepository.getSystemAccount();
    	
        List<Document> documents = documentRepository.findAll();

        logger.info("Remove missing documents batch launched.");

        for (Document document : documents) {
            InputStream stream = fileSystemDao.getFileContentByUUID(document.getUuid());

            if (stream == null) {
                try {
                    logger.info("Removing file with UID = {} because of inconsistency", document.getUuid());
                    entryService.deleteAllInconsistentShareEntries(systemAccount, document.getDocumentEntry());
                    documentEntryService.deleteInconsistentDocumentEntry(systemAccount, document.getDocumentEntry());
                    
                } catch (BusinessException ex) {
                    logger.error("Error when processing cleaning of document whith UID = {} during consistency check " +
                        "process", document.getUuid());
                    logger.debug(ex.toString());
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
    	SystemAccount systemAccount = accountRepository.getSystemAccount();
    	Calendar now = GregorianCalendar.getInstance();
    	
    	for (DocumentEntry documentEntry : findAllExpiredEntries) {
    		// we check if there is not related shares. Should not happen.
    		if(documentEntryBusinessService.getRelatedEntriesCount(documentEntry) > 0) {
    			
    			TimeUnitValueFunctionality fileExpirationTimeFunctionality = functionalityService.getDefaultFileExpiryTimeFunctionality(documentEntry.getEntryOwner().getDomain());
    			if(fileExpirationTimeFunctionality.getActivationPolicy().getStatus()) {
					if (documentEntry.getExpirationDate().before(now)) {
						try {
							documentEntryService.deleteExpiredDocumentEntry(systemAccount, documentEntry);
						} catch (BusinessException e) {
							logger.error("Can't delete expired document entry : " + documentEntry.getUuid()  + " : " + e.getMessage() );
							logger.debug(e.toString());
						}
    				}
    			}
    		} else {
    			logger.warn("expired document with shares found : " + documentEntry.getUuid());
    		}
		}
	}
	
	private void sendUpcomingDeletionNotification(DocumentEntry document, Integer days) {
		final long MILISECOND_PER_DAY = 24 * 60 * 60 * 1000;
//		int days = Math.round(Math.abs((deletionDate.getTimeInMillis()- now.getTimeInMillis())/MILISECOND_PER_DAY))+1;
//		sendUpcomingDeletionNotification(document, days);

//		MailContainer mailContainer = new MailContainer("", Language.FRENCH);
//		try {
//						
//			notifierService.sendAllNotifications(mailBuilder.buildMailUpcomingOutdatedDocumentWithOneRecipient((User)document.getEntryOwner(), mailContainer, document, days));
//		} catch (BusinessException e) {
//			logger.error("Can't create the email for "+ ((User)document.getEntryOwner()).getMail());
//			e.printStackTrace();
//		}
	}
	 
}
