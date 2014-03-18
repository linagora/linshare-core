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

import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.business.service.ShareEntryBusinessService;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.ShareLogEntry;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.SuccessesAndFailsItems;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.MailContentBuildingService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.ShareEntryService;
import org.linagora.linshare.core.service.ShareExpiryDateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShareEntryServiceImpl implements ShareEntryService {
	
	private static final Logger logger = LoggerFactory.getLogger(ShareEntryServiceImpl.class);
	
	private final GuestRepository guestRepository;
	
	private final FunctionalityReadOnlyService functionalityService;
	
	private final ShareEntryBusinessService shareEntryBusinessService;
	
	private final ShareExpiryDateService shareExpiryDateService;
	
	private final LogEntryService logEntryService;
	
	private final DocumentEntryService documentEntryService ;
	
	private final DocumentEntryBusinessService documentEntryBusinessService ;
	
	private final NotifierService notifierService;
    
    private final MailContentBuildingService mailContentBuildingService;


	public ShareEntryServiceImpl(GuestRepository guestRepository, FunctionalityReadOnlyService functionalityReadOnlyService, ShareEntryBusinessService shareEntryBusinessService,
		ShareExpiryDateService shareExpiryDateService, LogEntryService logEntryService, DocumentEntryService documentEntryService, NotifierService notifierService,
		MailContentBuildingService mailElementsFactory, DocumentEntryBusinessService documentEntryBusinessService) {
	super();
	this.guestRepository = guestRepository;
	this.functionalityService = functionalityReadOnlyService;
	this.shareEntryBusinessService = shareEntryBusinessService;
	this.shareExpiryDateService = shareExpiryDateService;
	this.logEntryService = logEntryService;
	this.documentEntryService = documentEntryService;
	this.notifierService = notifierService;
	this.mailContentBuildingService = mailElementsFactory;
	this.documentEntryBusinessService = documentEntryBusinessService;
}

	@Override
	public ShareEntry createShare(DocumentEntry documentEntry, User sender, User recipient, Calendar expirationDate) throws BusinessException {
		
		if (expirationDate == null) {
			expirationDate = shareExpiryDateService.computeShareExpiryDate(documentEntry, sender);
		}
		
		ShareEntry createShare = shareEntryBusinessService.createShare(documentEntry, sender, recipient, expirationDate);
		if (recipient.getAccountType().equals(AccountType.GUEST)) {
			updateGuestExpirationDate(recipient, sender);
		}
		
		logEntryService.create(new ShareLogEntry(sender, createShare, LogAction.FILE_SHARE, "Sharing of a file"));
		logEntryService.create(new ShareLogEntry(
				recipient, LogAction.SHARE_RECEIVED, "Receiving a shared file", createShare, sender));
		return createShare;
	}

	
	private void updateGuestExpirationDate(User recipient, User sender) {
		// update guest account expiry date
		if (recipient.getAccountType().equals(AccountType.GUEST)) {
			
			// get new guest expiry date
			Calendar guestExpiryDate = Calendar.getInstance();
			TimeUnitValueFunctionality guestFunctionality = functionalityService.getGuestAccountExpiryTimeFunctionality(sender.getDomain());
	        guestExpiryDate.add(guestFunctionality.toCalendarUnitValue(), guestFunctionality.getValue());
	        
			Guest guest = guestRepository.findByMail(recipient.getLogin());
			guest.setExpirationDate(guestExpiryDate.getTime());
			try {
				guestRepository.update(guest);
			} catch (IllegalArgumentException e) {
				logger.error("Can't update expiration date of guest : " + guest.getAccountReprentation() + ":" + e.getMessage());
			} catch (BusinessException e) {
				logger.error("Can't update expiration date of guest : " + guest.getAccountReprentation() + ":" + e.getMessage());
			}
		}
	}
	
	
	@Override
	public SuccessesAndFailsItems<ShareEntry> createShare(DocumentEntry documentEntry, User sender, List<User> recipients, Calendar expirationDate)  {
		
		SuccessesAndFailsItems<ShareEntry> returnItems = new SuccessesAndFailsItems<ShareEntry>();
		
		for (User recipient : recipients) {
			try {
				ShareEntry s = createShare(documentEntry, sender, recipient, expirationDate);
				returnItems.addSuccessItem(s);
				
			} catch (BusinessException e) {
				ShareEntry failSharing = new ShareEntry(sender, documentEntry.getName(), documentEntry.getComment(), recipient, documentEntry, expirationDate);
				returnItems.addSuccessItem(failSharing);
				logger.error("Could not create the sharing for owner " + sender.getLsUuid() + " to user " + recipient.getLsUuid() + " of doc " + documentEntry.getUuid(), e);
			}
		}
		return returnItems;
	}


	@Override
	public void deleteShare(Account actor, String shareUuid) throws BusinessException {
		ShareEntry share = shareEntryBusinessService.findByUuid(shareUuid);
		if(share == null) {
			logger.error("Share not found : " + shareUuid);
			throw new BusinessException(BusinessErrorCode.SHARED_DOCUMENT_NOT_FOUND, "Share entry not found : " + shareUuid);
		}
		deleteShare(actor, share);
	}
	

	@Override
	public void deleteShare(Account actor, ShareEntry share) throws BusinessException {
		if (share.getEntryOwner().equals(actor) || share.getRecipient().equals(actor) || actor.isSuperAdmin()
				|| actor.isTechnicalAccount()) {
			ShareLogEntry logEntry = new ShareLogEntry(actor, share, LogAction.SHARE_DELETE, "Delete a sharing");
			logEntryService.create(logEntry);

			logger.info("delete share : " + share.getUuid());
			shareEntryBusinessService.deleteShare(share);

			if (share.getEntryOwner().equals(actor) || actor.isSuperAdmin() || actor.isTechnicalAccount()) {
				notifierService.sendAllNotification(mailContentBuildingService.buildMailSharedFileDeletedWithRecipient(
						actor, share));
			}

		} else {
			logger.error("Actor " + actor.getAccountReprentation() + " does not own the share : " + share.getUuid());
			throw new BusinessException(BusinessErrorCode.FORBIDDEN,
					"You are not authorized to delete this share, it does not belong to you.");
		}
	}
	
	
	@Override
	public void deleteShare(SystemAccount actor, ShareEntry share) throws BusinessException {
		if(share.getEntryOwner().equals(actor) || share.getRecipient().equals(actor) || actor.isSuperAdmin() || actor.isTechnicalAccount()) {
			ShareLogEntry logEntry = new ShareLogEntry(actor, share, LogAction.SHARE_DELETE, "Delete a sharing");
			logEntryService.create(logEntry);
			logger.info("delete share : " + share.getUuid());
			shareEntryBusinessService.deleteShare(share);
		} else {
			logger.error("Actor " + actor.getAccountReprentation() + " does not own the share : " + share.getUuid());
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You are not authorized to delete this share, it does not belong to you.");
		}
	}
	

	@Override
	public SuccessesAndFailsItems<ShareEntry> createShare(List<DocumentEntry> documentEntries, User sender, List<User> recipients, Calendar expirationDate) {
		SuccessesAndFailsItems<ShareEntry> returnItems = new SuccessesAndFailsItems<ShareEntry>();
		
		for (DocumentEntry documentEntry : documentEntries) {
			returnItems.addAll(createShare(documentEntry, sender, recipients, expirationDate));
		}
		return returnItems;
	}
	
	
	@Override
	public DocumentEntry copyDocumentFromShare(String shareUuid, User actor) throws BusinessException {

		ShareEntry share = shareEntryBusinessService.findByUuid(shareUuid);
		if(share == null) {
			logger.error("Share not found : " + shareUuid);
			throw new BusinessException(BusinessErrorCode.SHARED_DOCUMENT_NOT_FOUND, "Share entry not found : " + shareUuid);
		}

		if(share.getEntryOwner().equals(actor) || share.getRecipient().equals(actor)) {
			 //log the copy
			ShareLogEntry logEntryShare = ShareLogEntry.hasCopiedAShare(actor, share);
			logEntryService.create(logEntryShare);

			DocumentEntry newDocumentEntry = documentEntryService.duplicateDocumentEntry(actor, share.getDocumentEntry().getUuid());

			ShareLogEntry logEntry = new ShareLogEntry(actor, share, LogAction.SHARE_DELETE, "Remove a received sharing (Copy of a sharing)"); 
			logEntryService.create(logEntry);
			logger.info("delete share : " + share.getUuid());

			if (share.getDownloaded() < 1) {
				notifierService.sendAllNotification(mailContentBuildingService.buildMailRegisteredDownloadWithOneRecipient(share));
			}
			shareEntryBusinessService.deleteShare(share);

			return newDocumentEntry;
		} else {
			logger.error("Actor " + actor.getAccountReprentation() + " does not own the share : " + shareUuid);
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You are not authorized to copy this share, it does not belong to you.");
		}
		
	}


	@Override
	public ShareEntry findByUuid(User actor, String shareUuid) throws BusinessException {
		ShareEntry share = shareEntryBusinessService.findByUuid(shareUuid);
		if(share == null) {
			logger.error("Share not found : " + shareUuid);
			throw new BusinessException(BusinessErrorCode.SHARED_DOCUMENT_NOT_FOUND, "Share entry not found : " + shareUuid);
		}
		if(share.getEntryOwner().equals(actor) || share.getRecipient().equals(actor) || actor.isSuperAdmin() || actor.isTechnicalAccount()) {
			return share;
		} else {
			logger.error("Actor " + actor.getAccountReprentation() + " does not own the share : " + shareUuid);
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You are not authorized to get this share, it does not belong to you.");
		}
	}


	@Override
	public void updateShareComment(User actor, String shareUuid, String comment) throws BusinessException {
		ShareEntry share = shareEntryBusinessService.findByUuid(shareUuid);
		if(share == null) {
			logger.error("Share not found : " + shareUuid);
			throw new BusinessException(BusinessErrorCode.SHARED_DOCUMENT_NOT_FOUND, "Share entry not found : " + shareUuid);
		}
		if(share.getRecipient().equals(actor)) {
			shareEntryBusinessService.updateShareComment(share, comment);
		} else {
			logger.error("Actor " + actor.getAccountReprentation() + " does not own the share : " + shareUuid);
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You are not authorized to update comment on this share, it does not belong to you.");
		}
		
	}


	@Override
	public boolean shareHasThumbnail(User actor, String shareEntryUuid) {
		try {
			ShareEntry shareEntry = findByUuid(actor, shareEntryUuid);
			if (shareEntry.getRecipient().equals(actor)) {
				String thmbUUID = shareEntry.getDocumentEntry().getDocument().getThmbUuid();
				return (thmbUUID!=null && thmbUUID.length()>0);
			} else {
				logger.error("You don't own this share : " + shareEntryUuid);
			}
		} catch (BusinessException e) {
			logger.error("Can't fin share for thumbnail : " + shareEntryUuid + " : " + e.getMessage());
		}
		return false;
	}


	@Override
	public InputStream getShareThumbnailStream(User actor, String shareEntryUuid) throws BusinessException {
		try {
			ShareEntry shareEntry = findByUuid(actor, shareEntryUuid);
			if (!shareEntry.getRecipient().equals(actor)) {
				throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You are not authorized to get thumbnail for this share.");
			}
			return documentEntryBusinessService.getDocumentThumbnailStream(shareEntry.getDocumentEntry());
		} catch (BusinessException e) {
			logger.error("Can't find share for thumbnail : " + shareEntryUuid + " : " + e.getMessage());
			throw e;
		}
	}


	@Override
	public InputStream getShareStream(User actor, String shareEntryUuid) throws BusinessException {
		try {
			ShareEntry shareEntry = findByUuid(actor, shareEntryUuid);
			if (!shareEntry.getRecipient().equals(actor)) {
				throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You are not authorized to get this share.");
			}
			ShareLogEntry logEntryActor = ShareLogEntry.hasDownloadedAShare(actor, shareEntry);
			ShareLogEntry logEntryTarget = ShareLogEntry.aShareWasDownloaded(actor, shareEntry);
			logEntryService.create(logEntryActor);
			logEntryService.create(logEntryTarget);
			shareEntryBusinessService.addDownload(shareEntry);
			return documentEntryBusinessService.getDocumentStream(shareEntry.getDocumentEntry());
		} catch (BusinessException e) {
			logger.error("Can't find share for thumbnail : " + shareEntryUuid + " : " + e.getMessage());
			throw e;
		}
	}
	

	@Override
	public List<ShareEntry> findAllMyShareEntries(Account actor, User owner) {
		// TODO check permissions
		return shareEntryBusinessService.findAllMyShareEntries(owner);
	}


	@Override
	public void sendDocumentEntryUpdateNotification(ShareEntry shareEntry, String friendlySize, String originalFileName) {
		try {
			notifierService.sendAllNotification(mailContentBuildingService.buildMailSharedDocumentUpdated(shareEntry, originalFileName, friendlySize));
		} catch (BusinessException e) {
			logger.error("Error while trying to notify document update ", e);
		}
	}
	

	@Override
	public void sendUpcomingOutdatedShareEntryNotification(SystemAccount actor, ShareEntry shareEntry, Integer days) {
		try {
			notifierService.sendAllNotification(mailContentBuildingService.buildMailUpcomingOutdatedShare(shareEntry, days));
		} catch (BusinessException e) {
			logger.error("Error while trying to notify upcoming outdated share", e);
		}
	}
}
