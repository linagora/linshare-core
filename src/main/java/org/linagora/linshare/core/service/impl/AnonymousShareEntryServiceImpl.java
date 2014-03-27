/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.business.service.AnonymousShareEntryBusinessService;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.entities.ShareLogEntry;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AnonymousShareEntryService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.MailContentBuildingService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.ShareExpiryDateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnonymousShareEntryServiceImpl implements AnonymousShareEntryService {

	private final FunctionalityReadOnlyService functionalityReadOnlyService;
	
	private final AnonymousShareEntryBusinessService anonymousShareEntryBusinessService;
	
	private final ShareExpiryDateService shareExpiryDateService;
	
	private final LogEntryService logEntryService;
	
	private final NotifierService notifierService;
    
    private final MailContentBuildingService mailContentBuildingService;
    
    private final DocumentEntryBusinessService documentEntryBusinessService;
    
    private static final Logger logger = LoggerFactory.getLogger(AnonymousShareEntryServiceImpl.class);
    
	public AnonymousShareEntryServiceImpl(FunctionalityReadOnlyService functionalityService, AnonymousShareEntryBusinessService anonymousShareEntryBusinessService,
			ShareExpiryDateService shareExpiryDateService, LogEntryService logEntryService, NotifierService notifierService, MailContentBuildingService mailElementsFactory,
			DocumentEntryBusinessService documentEntryBusinessService) {
		super();
		this.functionalityReadOnlyService = functionalityService;
		this.anonymousShareEntryBusinessService = anonymousShareEntryBusinessService;
		this.shareExpiryDateService = shareExpiryDateService;
		this.logEntryService = logEntryService;
		this.notifierService = notifierService;
		this.mailContentBuildingService = mailElementsFactory;
		this.documentEntryBusinessService = documentEntryBusinessService;
	}

	
	@Override
	public AnonymousShareEntry findByUuid(Account actor, String shareUuid) throws BusinessException {
		AnonymousShareEntry share = anonymousShareEntryBusinessService.findByUuid(shareUuid);
		if(share == null) {
			logger.error("Share not found : " + shareUuid);
			throw new BusinessException(BusinessErrorCode.SHARED_DOCUMENT_NOT_FOUND, "Share entry not found : " + shareUuid);
		}
		if(actor.isTechnicalAccount() || share.getEntryOwner().equals(actor) ) {
			return share;
		} else {
			logger.error("Actor " + actor.getAccountReprentation() + " does not own the share : " + shareUuid);
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You are not authorized to get this share, it does not belong to you.");
		}
	}
	

	@Override
	public List<AnonymousShareEntry> createAnonymousShare(List<DocumentEntry> documentEntries, User sender, Contact recipient, Calendar expirationDate, Boolean passwordProtected, MailContainer mailContainer) throws BusinessException {
		
		if(functionalityReadOnlyService.isSauMadatory(sender.getDomain().getIdentifier())) {
			passwordProtected = true;
		} else if(!functionalityReadOnlyService.isSauAllowed(sender.getDomain().getIdentifier())) {
			// if it is not mandatory an not allowed, it must be forbidden
			passwordProtected = false;
		}
		
		if (expirationDate == null) {
			expirationDate = shareExpiryDateService.computeMinShareExpiryDateOfList(documentEntries, sender);
		}
		
		AnonymousUrl anonymousUrl = anonymousShareEntryBusinessService.createAnonymousShare(documentEntries, sender, recipient, expirationDate, passwordProtected);
		
		
		// logs
		for (DocumentEntry documentEntry : documentEntries) {
			ShareLogEntry logEntry = new ShareLogEntry(sender, documentEntry, LogAction.FILE_SHARE, "Anonymous sharing of a file", expirationDate);
		    logEntryService.create(logEntry);
		}
		
		notifierService.sendAllNotification(mailContentBuildingService.buildMailNewSharingWithRecipient(mailContainer, anonymousUrl, sender));
		
		
		List<AnonymousShareEntry> anonymousShareEntries = new ArrayList<AnonymousShareEntry>(anonymousUrl.getAnonymousShareEntries());
		return anonymousShareEntries;
	}


	@Override
	public List<AnonymousShareEntry> createAnonymousShare(List<DocumentEntry> documentEntries, User sender, List<Contact> recipients, Calendar expirationDate, Boolean passwordProtected, MailContainer mailContainer) throws BusinessException {
		List<AnonymousShareEntry> anonymousShareEntries = new ArrayList<AnonymousShareEntry>();
		for (Contact contact : recipients) {
			anonymousShareEntries.addAll(createAnonymousShare(documentEntries, sender, contact, expirationDate, passwordProtected, mailContainer));
		}
		return anonymousShareEntries;
	}


	@Override
	public void deleteShare(Account actor, String shareUuid) throws BusinessException {
		AnonymousShareEntry shareEntry = findByUuid(actor, shareUuid);
		this.deleteShare(actor, shareEntry);
	}


	@Override
	public void deleteShare(Account actor, AnonymousShareEntry shareEntry) throws BusinessException {
		// TODO : fix permissions
//		if(shareEntry.getEntryOwner().equals(actor) || actor.equals(guestRepository.getSystemAccount()) || actor.getAccountType().equals(AccountType.ROOT) ) {
//			
//		} else {
//			logger.error("Actor " + actor.getAccountReprentation() + " does not own the share : " + shareEntry.getUuid());
//			throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to delete this anonymous share, it does not belong to you.");
//		}
		
		
		
		anonymousShareEntryBusinessService.deleteAnonymousShare(shareEntry);
		ShareLogEntry logEntry = new ShareLogEntry(actor, shareEntry, LogAction.SHARE_DELETE, "Deleting anonymous share" );
		logEntryService.create(logEntry);
		
		// TODO : anonymous share deletion notification
		// notifierService.sendAllNotification();
	}
	
	
	@Override
	public void deleteShare(SystemAccount systemAccount, AnonymousShareEntry shareEntry) throws BusinessException {
		anonymousShareEntryBusinessService.deleteAnonymousShare(shareEntry);
		ShareLogEntry logEntry = new ShareLogEntry(systemAccount, shareEntry, LogAction.SHARE_DELETE, "Deleting anonymous share" );
		logEntryService.create(logEntry);
	}
	
	
	@Override
	public InputStream getAnonymousShareEntryStream(String shareUuid) throws BusinessException {
		AnonymousShareEntry shareEntry;
		try {
			shareEntry = downloadAnonymousShareEntry(shareUuid);
		} catch (BusinessException e) {
			logger.error("Can't find anonymous share : " + shareUuid + " : " + e.getMessage());
			throw e;
		}
		try {
			//send a notification by mail to the owner
			notifierService.sendAllNotification(mailContentBuildingService.buildMailAnonymousDownload(shareEntry));
		} catch (BusinessException e) {
			// TODO : FIXME : send the notification to the domain administration address. => a new functionality need to be add. 
			if(e.getErrorCode().equals(BusinessErrorCode.RELAY_HOST_NOT_ENABLE)) {
				logger.error("Can't send notification to anonymous share (" + shareUuid + ") owner because : " + e.getMessage());
			}
		}
		return documentEntryBusinessService.getDocumentStream(shareEntry.getDocumentEntry());
	}

	
	private AnonymousShareEntry downloadAnonymousShareEntry(String shareUuid) throws BusinessException {
		AnonymousShareEntry shareEntry = anonymousShareEntryBusinessService.findByUuidForDownload(shareUuid);
		
		ShareLogEntry logEntry = new ShareLogEntry(shareEntry.getEntryOwner(), shareEntry, LogAction.ANONYMOUS_SHARE_DOWNLOAD,
				"Anonymous user " + shareEntry.getEntryOwner().getAccountReprentation() +  " downloaded a file");
		logEntryService.create(logEntry);
		return shareEntry;
	}

	
	@Override
	public void sendDocumentEntryUpdateNotification(AnonymousShareEntry anonymousShareEntry, String friendlySize, String originalFileName) {
		try {
			notifierService.sendAllNotification(mailContentBuildingService.buildMailSharedDocumentUpdated(anonymousShareEntry, originalFileName, friendlySize));
		} catch (BusinessException e) {
			logger.error("Error while trying to notify document update ", e);
		}
	}
	
	
	@Override
	public void sendUpcomingOutdatedShareEntryNotification(SystemAccount actor, AnonymousShareEntry shareEntry, Integer days) {
		try {
			notifierService.sendAllNotification(mailContentBuildingService.buildMailUpcomingOutdatedShare(shareEntry, days));
		} catch (BusinessException e) {
			logger.error("Error while trying to notify upcoming outdated share", e);
		}
		
	}
}
