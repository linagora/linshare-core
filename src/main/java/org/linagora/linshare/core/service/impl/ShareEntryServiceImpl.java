package org.linagora.linshare.core.service.impl;

import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.business.service.ShareEntryBusinessService;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.ShareLogEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.SuccessesAndFailsItems;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.MailContentBuildingService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.ShareEntryService;
import org.linagora.linshare.core.service.ShareExpiryDateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShareEntryServiceImpl implements ShareEntryService {
	
	private final GuestRepository guestRepository;
	
	private final FunctionalityService functionalityService;
	
	private final ShareEntryBusinessService shareEntryBusinessService;
	
	private final ShareExpiryDateService shareExpiryDateService;
	
	private final LogEntryService logEntryService;
	
	private final DocumentEntryService documentEntryService ;
	
	private final NotifierService notifierService;
    
    private final MailContentBuildingService mailElementsFactory;

	
	
//	private final UserService userService;
	
	private static final Logger logger = LoggerFactory.getLogger(ShareEntryServiceImpl.class);
	

	public ShareEntryServiceImpl(GuestRepository guestRepository, FunctionalityService functionalityService, ShareEntryBusinessService shareEntryBusinessService,
		ShareExpiryDateService shareExpiryDateService, LogEntryService logEntryService, DocumentEntryService documentEntryService, NotifierService notifierService,
		MailContentBuildingService mailElementsFactory) {
	super();
	this.guestRepository = guestRepository;
	this.functionalityService = functionalityService;
	this.shareEntryBusinessService = shareEntryBusinessService;
	this.shareExpiryDateService = shareExpiryDateService;
	this.logEntryService = logEntryService;
	this.documentEntryService = documentEntryService;
	this.notifierService = notifierService;
	this.mailElementsFactory = mailElementsFactory;
}



	@Override
	public ShareEntry createShare(DocumentEntry documentEntry, User sender, User recipient, Calendar expirationDate) throws BusinessException {
		
		if (expirationDate == null) {
			expirationDate = shareExpiryDateService.computeShareExpiryDate(documentEntry, sender);
		}
		
		shareEntryBusinessService.createShare(documentEntry, sender, recipient, expirationDate);
		if (recipient.getAccountType().equals(AccountType.GUEST)) {
			updateGuestExpirationDate(recipient);
		}
		
		ShareLogEntry logEntry = new ShareLogEntry(sender.getMail(), sender.getFirstName(), sender.getLastName(),
				sender.getDomainId(),
	        	LogAction.FILE_SHARE, "Sharing of a file", documentEntry.getName(), documentEntry.getDocument().getSize(), documentEntry.getDocument().getType(),
	        	recipient.getMail(), recipient.getFirstName(), recipient.getLastName(), recipient.getDomainId(), expirationDate);
	       
	    logEntryService.create(logEntry);
	    
//	    mailElementsFactory.buildMailNewSharingWithRecipient(actor, mailContainer, owner, recipientMail, docs, linShareUrl, linShareUrlParam, password, hasToDecrypt, jwsEncryptUrl)
//	    mailElementsFactory.buildMailNewSharingWithRecipient(actor, mailContainer, owner, recipient, docs, linShareUrl, linShareUrlParam, password, hasToDecrypt, jwsEncryptUrl)
	    
		return null;
	}



	private void updateGuestExpirationDate(User recipient) {
		// update guest account expiry date
		if (recipient.getAccountType().equals(AccountType.GUEST)) {
			
			// get new guest expiry date
			Calendar guestExpiryDate = Calendar.getInstance();
			TimeUnitValueFunctionality guestFunctionality = functionalityService.getGuestAccountExpiryTimeFunctionality(recipient.getDomain());
	        guestExpiryDate.add(guestFunctionality.toCalendarValue(), guestFunctionality.getValue());
	        
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
				logger.error("Could not create the sharing for owner " + sender.getLsUid() + " to user " + recipient.getLsUid() + " of doc " + documentEntry.getUuid(), e);
			}
		}
		return returnItems;
	}


	@Override
	public void deleteShare(String shareUuid, User actor) throws BusinessException {
		ShareEntry share = shareEntryBusinessService.findById(shareUuid);
		if(share == null) {
			logger.error("Share not found : " + shareUuid);
			throw new BusinessException(BusinessErrorCode.SHARED_DOCUMENT_NOT_FOUND, "Share entry not found : " + shareUuid);
		}
		deleteShare(share, actor);
	}



	private void deleteShare(ShareEntry share, User actor) throws BusinessException {
		if(share.getEntryOwner().equals(actor) || share.getRecipient().equals(actor)) {
			ShareLogEntry logEntry = new ShareLogEntry(actor.getMail(), actor.getFirstName(), actor.getLastName(),
					actor.getDomainId(),
	        		LogAction.SHARE_DELETE, "Delete a sharing", 
	        		share.getDocumentEntry().getName(), share.getDocumentEntry().getDocument().getSize(), share.getDocumentEntry().getDocument().getType(),
	        		share.getRecipient().getMail(), share.getRecipient().getFirstName(), share.getRecipient().getLastName(), share.getRecipient().getDomainId(), null);
	        
	        logEntryService.create(logEntry);
			
	        logger.info("delete share : " + share.getUuid());
	        
	        shareEntryBusinessService.deleteShare(share);
			
		} else {
			logger.error("Actor " + actor.getAccountReprentation() + " does not own the share : " + share.getUuid());
			throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to delete this share, it does not belong to you.");
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
		
		ShareEntry share = shareEntryBusinessService.findById(shareUuid);
		if(share == null) {
			logger.error("Share not found : " + shareUuid);
			throw new BusinessException(BusinessErrorCode.SHARED_DOCUMENT_NOT_FOUND, "Share entry not found : " + shareUuid);
		}
		
		if(share.getEntryOwner().equals(actor) || share.getRecipient().equals(actor)) {
			
			User sender = (User)share.getEntryOwner();
			User user = share.getRecipient();
			 //log the copy
			ShareLogEntry logEntryShare = new ShareLogEntry(sender.getMail(), 
					sender.getFirstName(), sender.getLastName(), sender.getDomainId(),
					LogAction.SHARE_COPY, "Copy of a sharing", share.getName(), share.getSize(), share.getType(), user.getMail(), user.getFirstName(), user.getLastName(), user.getDomainId(), null);
	
			logEntryService.create(logEntryShare);
			
			DocumentEntry newDocumentEntry = documentEntryService.duplicateDocumentEntry(actor, share.getDocumentEntry().getUuid());
			
			
			ShareLogEntry logEntry = new ShareLogEntry(actor.getMail(), actor.getFirstName(), actor.getLastName(),
					actor.getDomainId(),
	        		LogAction.SHARE_DELETE, "Remove a received sharing (Copy of a sharing)", 
	        		share.getName(), share.getSize(), share.getType(),
	        		user.getMail(), user.getFirstName(), user.getLastName(), user.getDomainId(), null);
	        
	        logEntryService.create(logEntry);
	        
	        logger.info("delete share : " + share.getUuid());
	        
	        shareEntryBusinessService.deleteShare(share);
	        
	        return newDocumentEntry;
		} else {
			logger.error("Actor " + actor.getAccountReprentation() + " does not own the share : " + shareUuid);
			throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to delete this share, it does not belong to you.");
		}
	}



	@Override
	public void deleteAllShareEntriesWithDocumentEntries(String docEntryUuid, User actor) {
		try {
			DocumentEntry entry = documentEntryService.findById(actor, docEntryUuid);
			for (ShareEntry share : entry.getShareEntries()) {
				deleteShare(share, actor);
			}
			documentEntryService.deleteDocumentEntry(actor, entry.getUuid());
			
		} catch (BusinessException e) {
			logger.error("Document not found : " + docEntryUuid);
		}
	}



	@Override
	public ShareEntry findById(User actor, String shareUuid) throws BusinessException {
		ShareEntry share = shareEntryBusinessService.findById(shareUuid);
		if(share == null) {
			logger.error("Share not found : " + shareUuid);
			throw new BusinessException(BusinessErrorCode.SHARED_DOCUMENT_NOT_FOUND, "Share entry not found : " + shareUuid);
		}
		if(share.getEntryOwner().equals(actor) || share.getRecipient().equals(actor)) {
			return share;
		} else {
			logger.error("Actor " + actor.getAccountReprentation() + " does not own the share : " + shareUuid);
			throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to delete this share, it does not belong to you.");
		}
	}



	@Override
	public void updateShareComment(User actor, String shareUuid, String comment) throws BusinessException {
		ShareEntry share = shareEntryBusinessService.findById(shareUuid);
		if(share == null) {
			logger.error("Share not found : " + shareUuid);
			throw new BusinessException(BusinessErrorCode.SHARED_DOCUMENT_NOT_FOUND, "Share entry not found : " + shareUuid);
		}
		if(share.getRecipient().equals(actor)) {
			shareEntryBusinessService.updateShareComment(share, comment);
		} else {
			logger.error("Actor " + actor.getAccountReprentation() + " does not own the share : " + shareUuid);
			throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to update comment on this share, it does not belong to you.");
		}
		
	}

}
