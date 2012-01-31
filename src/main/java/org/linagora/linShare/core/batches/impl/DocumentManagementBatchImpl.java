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
package org.linagora.linShare.core.batches.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.linagora.linShare.core.batches.DocumentManagementBatch;
import org.linagora.linShare.core.dao.FileSystemDao;
import org.linagora.linShare.core.domain.constants.Language;
import org.linagora.linShare.core.domain.constants.Reason;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.entities.MailContainerWithRecipient;
import org.linagora.linShare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.DocumentRepository;
import org.linagora.linShare.core.service.DocumentService;
import org.linagora.linShare.core.service.FunctionalityService;
import org.linagora.linShare.core.service.MailContentBuildingService;
import org.linagora.linShare.core.service.NotifierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * Batch for document management.
 */
public class DocumentManagementBatchImpl implements DocumentManagementBatch {

    Logger logger = LoggerFactory.getLogger(DocumentManagementBatchImpl.class);

    private final DocumentRepository documentRepository;
    private final DocumentService documentService;
    private final FileSystemDao fileSystemDao;
    private final boolean securedStorageDisallowed;
    private final boolean cronActivated;
	private final NotifierService notifierService;
	private final MailContentBuildingService mailBuilder;
	private final FunctionalityService functionalityService;

    public DocumentManagementBatchImpl(DocumentRepository documentRepository, DocumentService documentService,
        FileSystemDao fileSystemDao, boolean securedStorageDisallowed, boolean cronActivated,
        NotifierService notifierService, MailContentBuildingService mailBuilder, FunctionalityService functionalityService) {
        this.documentRepository = documentRepository;
        this.documentService = documentService;
        this.fileSystemDao = fileSystemDao;
        this.securedStorageDisallowed = securedStorageDisallowed;
        this.cronActivated = cronActivated;
        this.notifierService = notifierService;
        this.mailBuilder = mailBuilder;
        this.functionalityService = functionalityService;
    }

    public void removeMissingDocuments() {
        List<Document> documents = documentRepository.findAll();

        logger.info("Remove missing documents batch launched.");

        for (Document document : documents) {
            InputStream stream = fileSystemDao.getFileContentByUUID(document.getIdentifier());

            if (stream == null) {
                try {
                    logger.info("Removing file with UID = {} because of inconsistency", document.getIdentifier());
                    documentService.deleteFile(document.getOwner().getLogin(), document.getIdentifier(),
                        Reason.INCONSISTENCY);
                } catch (BusinessException ex) {
                    logger.error("Error when processing cleaning of document whith UID = {} during consistency check " +
                        "process", document.getIdentifier());
                }
            }
        }
        logger.info("Remove missing documents batch ended.");
    }
    
    public void cleanOldDocuments() {
    	
    	if (!securedStorageDisallowed) {
    		logger.info("Documents cleaner batch launched but secured storage not disallowed : stopping.");
    		return;
    	}
    	
    	if (!cronActivated) {
    		logger.info("Documents cleaner batch launched but was told to be unactivated : stopping.");
    		return;
    	}
    	
    	logger.info("Documents cleaner batch launched.");
    	
    	List<Document> documents = documentRepository.findAll();
    	
    	Calendar now = GregorianCalendar.getInstance();
    	for (Document document : documents) {
			if (!document.getShared() && !document.getSharedWithGroup()) {
				if (document.getDeletionDate() == null) {
					TimeUnitValueFunctionality fileExpirationTimeFunctionality = functionalityService.getDefaultFileExpiryTimeFunctionality(document.getOwner().getDomain());
			    	
					if(!fileExpirationTimeFunctionality.getActivationPolicy().getStatus()) {
						break;
					}
					
			    	Calendar deletionDate = (Calendar)document.getCreationDate().clone();
					deletionDate.add(fileExpirationTimeFunctionality.toCalendarValue(), fileExpirationTimeFunctionality.getValue());
					
					document.setDeletionDate(deletionDate);
					
					try {
						documentRepository.update(document);
						logger.info("Documents cleaner batch has set a file to be cleaned at "+(new Date(deletionDate.getTimeInMillis())).toString());
						
						final long MILISECOND_PER_DAY = 24 * 60 * 60 * 1000;
						int days = Math.round(Math.abs((deletionDate.getTimeInMillis()- now.getTimeInMillis())/MILISECOND_PER_DAY))+1;
						sendUpcomingDeletionNotification(document, days);
					} catch (Exception e) {
						logger.error("Documents cleaner batch error while updating deletion date : "+e.getMessage());
						e.printStackTrace();
					}
				} else {
					if (document.getDeletionDate().before(now)) {
						try {
							documentService.deleteFile("system", document.getIdentifier(), Reason.EXPIRY);
							logger.info("Documents cleaner batch has removed a file.");
						} catch (BusinessException e) {
							logger.error("Documents cleaner batch error when deleting expired file : "+e.getMessage());
							e.printStackTrace();
						}
					}
				}
			}
		}
    	
    	logger.info("Documents cleaner batch ended.");
    	
    }

	private void sendUpcomingDeletionNotification(Document document, Integer days) {
		MailContainer mailContainer = new MailContainer("", Language.FRENCH);
		try {

			List<MailContainerWithRecipient> mailContainerWithRecipient_ = new ArrayList<MailContainerWithRecipient>();
			mailContainerWithRecipient_.add(new MailContainerWithRecipient(mailBuilder.buildMailUpcomingOutdatedDocument(document.getOwner(), mailContainer, document, days), document.getOwner().getMail()));				
						
			notifierService.sendAllNotifications(null, mailContainerWithRecipient_);
		} catch (BusinessException e) {
			logger.error("Can't create the email for "+document.getOwner().getMail());
			e.printStackTrace();
		}
	}
	 
}
