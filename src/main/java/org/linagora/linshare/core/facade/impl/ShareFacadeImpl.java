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
package org.linagora.linshare.core.facade.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.entities.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.SuccessesAndFailsItems;
import org.linagora.linshare.core.domain.transformers.impl.DocumentEntryTransformer;
import org.linagora.linshare.core.domain.transformers.impl.ShareEntryTransformer;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.facade.ShareFacade;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AnonymousShareEntryService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.core.service.MailContentBuildingService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.ShareEntryService;
import org.linagora.linshare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShareFacadeImpl implements ShareFacade {

	private static final Logger logger = LoggerFactory.getLogger(ShareFacadeImpl.class);
	
	private final ShareEntryTransformer shareEntryTransformer;
	
	private final UserRepository<User> userRepository;

	private final NotifierService notifierService;
    
    private final MailContentBuildingService mailElementsFactory;
	
	private final UserService userService;

    private final ShareEntryService shareEntryService;

	private final DocumentEntryTransformer documentEntryTransformer;
	
	private final DocumentEntryService documentEntryService;
	
	private final AbstractDomainService abstractDomainService;
	
	private final FunctionalityService functionalityService;
	
	private final AnonymousShareEntryService anonymousShareEntryService;
	
	private final String urlBase;
	
	private final String urlInternal;
    
	
	public ShareFacadeImpl(ShareEntryTransformer shareEntryTransformer, UserRepository<User> userRepository, NotifierService notifierService,
			MailContentBuildingService mailElementsFactory, UserService userService, ShareEntryService shareEntryService, DocumentEntryTransformer documentEntryTransformer,
			DocumentEntryService documentEntryService, AbstractDomainService abstractDomainService, FunctionalityService functionalityService, AnonymousShareEntryService anonymousShareEntryService,
			String urlBase, String urlInternal) {
		super();
		this.shareEntryTransformer = shareEntryTransformer;
		this.userRepository = userRepository;
		this.notifierService = notifierService;
		this.mailElementsFactory = mailElementsFactory;
		this.userService = userService;
		this.shareEntryService = shareEntryService;
		this.documentEntryTransformer = documentEntryTransformer;
		this.documentEntryService = documentEntryService;
		this.abstractDomainService = abstractDomainService;
		this.functionalityService = functionalityService;
		this.anonymousShareEntryService = anonymousShareEntryService;
		this.urlBase = urlBase;
		this.urlInternal = urlInternal;
	}
	

	private SuccessesAndFailsItems<ShareDocumentVo> createSharing(UserVo actorVo, List<DocumentVo> documents, List<UserVo> recipientsVo, Calendar expirationDate) throws BusinessException {
		logger.debug("createSharing:Begin");
		
		User actor = userService.findByLsUid(actorVo.getLsUid());
		
		List<User> recipients = new ArrayList<User>();
		
		for (UserVo userVo : recipientsVo) {
			try {
				recipients.add(userService.findOrCreateUserWithDomainPolicies(userVo.getMail(), actorVo.getDomainIdentifier()));
			} catch (BusinessException e) {
				logger.error("Could not find the recipient " + userVo.getMail() + " in the database nor in the ldap");
				throw e;
			}
		}
		
		List<DocumentEntry> documentEntries = documentEntryTransformer.assembleList(documents);
		
		SuccessesAndFailsItems<ShareEntry> successAndFails = shareEntryService.createShare(documentEntries, actor, recipients, expirationDate);
		
		SuccessesAndFailsItems<ShareDocumentVo> results = disassembleShareResultList(successAndFails);
		
		
		logger.debug("createSharing:End");
		return results;
	}

	
	private SuccessesAndFailsItems<ShareDocumentVo> createSharingWithMail(UserVo owner, List<DocumentVo> documents, List<UserVo> recipients, MailContainer mailContainer, Calendar expirationDate, boolean isOneDocEncrypted) throws BusinessException {
		logger.debug("createSharingWithMail:Begin");
		SuccessesAndFailsItems<ShareDocumentVo> result = createSharing(owner,documents,recipients, expirationDate);
		
		//Sending the mails
		List<UserVo> successfullRecipient = new ArrayList<UserVo>();
		for (ShareDocumentVo successfullSharing : result.getSuccessesItem()) {
			logger.debug("share:result:" + result);
			if (!successfullRecipient.contains(successfullSharing.getReceiver())) {
				successfullRecipient.add(successfullSharing.getReceiver());
			}
			
		}
		
		User owner_ = userRepository.findByLsUuid(owner.getLogin());
		
		
		List<MailContainerWithRecipient> mailContainerWithRecipient = new ArrayList<MailContainerWithRecipient>();
		
		List<String> documentNames = new ArrayList<String>();
		for (DocumentVo documentVo : documents) {
			documentNames.add(documentVo.getFileName());
		}
		
		for(UserVo userVo : successfullRecipient){
			logger.debug("Sending sharing notification to user " + userVo.getLogin());
			User recipient = userRepository.findByLsUuid(userVo.getLogin());
			String linshareUrl = userVo.isGuest() ? urlBase : urlInternal;
			
			mailContainerWithRecipient.add(mailElementsFactory.buildMailNewSharingWithRecipient(owner_, mailContainer, recipient, documentNames, linshareUrl, "", null, isOneDocEncrypted));

		}
		
		notifierService.sendAllNotifications(mailContainerWithRecipient);
		logger.debug("createSharingWithMail:End");
		return result;
	}
	
	
	@Override
	public List<ShareDocumentVo> getAllSharingReceivedByUser(UserVo recipientVo) {
		User actor = userService.findByLsUid(recipientVo.getLsUid());
		
		if (actor == null) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Could not find the user");
		}
		ArrayList<ShareEntry> arrayList = new ArrayList<ShareEntry>(actor.getShareEntries());
		logger.debug("AllSharingReceived size : " + arrayList.size());
		return shareEntryTransformer.disassembleList(arrayList);		
	}

	
	@Override
	public List<ShareDocumentVo> getSharingsByUserAndFile(UserVo senderVo, DocumentVo documentVo) {
		
		User actor = userService.findByLsUid(senderVo.getLsUid());
		if (actor==null) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Could not find the user");
		}
		
		DocumentEntry documentEntry;
		try {
			documentEntry = documentEntryService.findById(actor, documentVo.getIdentifier());
			return shareEntryTransformer.disassembleList(new ArrayList<ShareEntry>(documentEntry.getShareEntries()));
		} catch (BusinessException e) {
			logger.error("Document " + documentVo.getIdentifier() + " was not found ! " + e.getMessage() );
		}
		return new ArrayList<ShareDocumentVo>();
	}
	
	
	@Override
	public Map<String, Calendar> getAnonymousSharingsByUserAndFile(UserVo senderVo, DocumentVo documentVo) {
		
		User actor = userService.findByLsUid(senderVo.getLsUid());
		if (actor==null) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Could not find the user");
		}
		
		Map<String, Calendar> res = new HashMap<String, Calendar>();
		DocumentEntry documentEntry;
		try {
			documentEntry = documentEntryService.findById(actor, documentVo.getIdentifier());
			
			
			for (AnonymousShareEntry entry : documentEntry.getAnonymousShareEntries()) {
				res.put(entry.getAnonymousUrl().getContact().getMail(), entry.getExpirationDate());
			}
		} catch (BusinessException e) {
			logger.error("Document " + documentVo.getIdentifier() + " was not found ! " + e.getMessage() );
		}
		return res;
	}


	@Override
	public void deleteSharing(ShareDocumentVo share, UserVo actorVo) throws BusinessException {
		User actor = userService.findByLsUid(actorVo.getLsUid());
		shareEntryService.deleteShare(actor, share.getIdentifier());
	}

	
	@Override
    public DocumentVo createLocalCopy(ShareDocumentVo shareDocumentVo, UserVo userVo) throws BusinessException {
		User actor = userService.findByLsUid(userVo.getLsUid());
		DocumentEntry documentEntry = shareEntryService.copyDocumentFromShare(shareDocumentVo.getIdentifier(), actor);
		return documentEntryTransformer.disassemble(documentEntry);
    }


	@Override
    public SuccessesAndFailsItems<ShareDocumentVo> createSharingWithMailUsingRecipientsEmail(
    		UserVo ownerVo, List<DocumentVo> documents, List<String> recipientsEmail, boolean secureSharing, MailContainer mailContainer) throws BusinessException {
		
		logger.debug("createSharingWithMailUsingRecipientsEmail");
		return createSharingWithMailUsingRecipientsEmailAndExpiryDate(ownerVo, documents, recipientsEmail, secureSharing, mailContainer,null);
	}


	@Override
    public SuccessesAndFailsItems<ShareDocumentVo> createSharingWithMailUsingRecipientsEmailAndExpiryDate(
			UserVo actorVo, List<DocumentVo> documents, List<String> recipientsEmailInput, boolean secureSharing, MailContainer mailContainer, Calendar expiryDateSelected) throws BusinessException {
    	logger.debug("createSharingWithMailUsingRecipientsEmail");
    	
    	User sender = userService.findByLsUid(actorVo.getLsUid());
    	
		SuccessesAndFailsItems<ShareDocumentVo> result = new SuccessesAndFailsItems<ShareDocumentVo>();

		List<UserVo> knownRecipients = new ArrayList<UserVo>();
		List<Contact> unKnownRecipientsEmail = new ArrayList<Contact>();
		
		logger.debug("The current user is : " + sender.getAccountReprentation());
		logger.debug("recipientsEmailInput size : " + recipientsEmailInput.size());
		List<String> recipientsEmail = new ArrayList<String>();
		if(sender.getAccountType().equals(AccountType.GUEST) && ((Guest)sender).isRestricted()) {
			List<String> guestAllowedContacts= userService.getGuestEmailContacts(sender.getMail());
			logger.debug("guestAllowedContacts size : " + guestAllowedContacts.size());
			for (String mailInput : recipientsEmailInput) {
				if(guestAllowedContacts.contains(mailInput)) {
					logger.debug("The current user is allowed to share with : " + mailInput);
					recipientsEmail.add(mailInput);
				} else {
					logger.info("The current user is not allowed to share with : " + mailInput);
					unKnownRecipientsEmail.add(new Contact(mailInput));
				}
			}
			logger.debug("Only " + recipientsEmail.size() + " contacts are authorized for " + sender.getMail());
		} else {
			recipientsEmail.addAll(recipientsEmailInput);
		}
		logger.debug("recipientsEmail size : " + recipientsEmail.size());
		logger.debug("unKnownRecipientsEmail size : " + unKnownRecipientsEmail.size());
		logger.debug("unKnownRecipientsEmail  : " + unKnownRecipientsEmail.toString());
    	
		boolean isOneDocEncrypted = oneDocIsEncrypted(documents);
		
		
		
		// find known and unknown recipients of the share
		User tempRecipient = null;
		for (String mail : recipientsEmail) {
			try {
				tempRecipient= userService.findOrCreateUserWithDomainPolicies(mail, sender.getDomainId());
				knownRecipients.add(new UserVo(tempRecipient));
			} catch (BusinessException e) {
				if (e.getErrorCode() == BusinessErrorCode.USER_NOT_FOUND) {					
					logger.debug("unKnownRecipientsEmail  : adding a new contact : " + mail.toString());
					unKnownRecipientsEmail.add(new Contact(mail));
				}
				else
					throw e;
			}
		}
		
		logger.debug("knownRecipients size : " + knownRecipients.size());
		logger.debug("knownRecipients  : " + knownRecipients.toString());
		logger.debug("unKnownRecipientsEmail size : " + unKnownRecipientsEmail.size());
		logger.debug("unKnownRecipientsEmail  : " + unKnownRecipientsEmail.toString());
		
		if(unKnownRecipientsEmail.size()>0){ //secureUrl for these users (no need to have an account to activate sharing)
			
			boolean hasRightsToShareWithExternals = false;
			
			try {
				hasRightsToShareWithExternals = abstractDomainService.hasRightsToShareWithExternals(sender);
			} catch (BusinessException e) {
				logger.error("Could not retrieve domain of sender while sharing to externals: "+sender.getAccountReprentation());
				logger.debug(e.toString());
			}
			
			if (hasRightsToShareWithExternals) {
			
				List<DocumentEntry> documentEntries = documentEntryTransformer.assembleList(documents);
				
				for (Contact recipient : unKnownRecipientsEmail) {
					anonymousShareEntryService.createAnonymousShare(documentEntries, sender, recipient, expiryDateSelected, secureSharing, mailContainer);
					
				}
			} else {
				// Building all failed items for unkown recipients. 
				for (DocumentVo doc : documents) {
					for (Contact oneContact : unKnownRecipientsEmail) {
						UserVo recipient = new UserVo(oneContact.getMail(), "", "", oneContact.getMail(), null);
						ShareDocumentVo failSharing=new ShareDocumentVo(doc, actorVo, recipient);
						result.addFailItem(failSharing);
					}
				}
			}
		}
		
		//keep old method to share with user referenced in db
		result.addAll(createSharingWithMail(actorVo, documents, knownRecipients, mailContainer, expiryDateSelected, isOneDocEncrypted));
		return result;
    }
    
    
	@Override
    public void sendDownloadNotification(ShareDocumentVo sharedDocument, UserVo currentUser) throws BusinessException {
		
		User user = userRepository.findByLsUuid(currentUser.getLogin());
		ShareEntry shareEntry = shareEntryService.findByUuid(user, sharedDocument.getIdentifier());
		notifierService.sendAllNotification(mailElementsFactory.buildMailRegisteredDownloadWithOneRecipient(shareEntry));
    }
    
	
	private SuccessesAndFailsItems<ShareDocumentVo> disassembleShareResultList(SuccessesAndFailsItems<ShareEntry> successAndFails) {
		SuccessesAndFailsItems<ShareDocumentVo> results = new SuccessesAndFailsItems<ShareDocumentVo>();
		results.setFailsItem(shareEntryTransformer.disassembleList(successAndFails.getFailsItem()));
		results.setSuccessesItem(shareEntryTransformer.disassembleList(successAndFails.getSuccessesItem()));
		return results;
	}

	
	private boolean oneDocIsEncrypted(List<DocumentVo> docList) {
		for (DocumentVo doc : docList) {
			if (doc.getEncrypted()) {
				return true;
			}
		}
		return false;
	}

	
	@Override
	public boolean isVisibleSecuredAnonymousUrlCheckBox(String domainIdentifier) {
		return functionalityService.isSauAllowed(domainIdentifier);
	}

	
	@Override
	public boolean getDefaultSecuredAnonymousUrlCheckBoxValue(String domainIdentifier) {
		return functionalityService.getDefaultSauValue(domainIdentifier);
	}

	

	@Override
	public ShareDocumentVo getShareDocumentVoByUuid(UserVo actorVo, String uuid) throws BusinessException {
		User actor = userService.findByLsUid(actorVo.getLsUid());
		return shareEntryTransformer.disassemble(shareEntryService.findByUuid(actor, uuid));
	}


	@Override
	public void updateShareComment(UserVo actorVo, String uuid, String comment) throws IllegalArgumentException, BusinessException {
		logger.debug("updateShareComment:" + uuid);
		User actor = userService.findByLsUid(actorVo.getLsUid());
		ShareEntry shareEntry = shareEntryService.findByUuid(actor, uuid);
		shareEntry.setComment(comment);
		logger.debug("comment : " + comment);
		shareEntryService.updateShareComment(actor, uuid, comment);
	}
	
	
	@Override
    public boolean shareHasThumbnail(UserVo actorVo, String shareEntryUuid) {
		String lsUid = actorVo.getLsUid();
		if(lsUid == null) {
			logger.error("Can't find user with null parameter.");
			return false;
		}
		
		User actor = userService.findByLsUid(lsUid);
		if(actor == null) {
			logger.error("Can't find logged user.");
			return false;
		}
		return shareEntryService.shareHasThumbnail(actor, shareEntryUuid);
    }
	
	
	@Override
    public InputStream getShareThumbnailStream(UserVo actorVo, String shareEntryUuid) {
		String lsUid = actorVo.getLsUid();
		if(lsUid == null) {
			logger.error("Can't find user with null parametter.");
			return null;
		}
		
		User actor = userService.findByLsUid(lsUid);
		if(actor == null) {
			logger.error("Can't find logged user.");
			return null;
		}
		
		try {
			return shareEntryService.getShareThumbnailStream(actor, shareEntryUuid);
		} catch (BusinessException e) {
			logger.error("Can't get document thumbnail : " + shareEntryUuid + " : " + e.getMessage());
		}
    	return null;
    }
	

	@Override
	public InputStream getShareStream(UserVo actorVo, String shareEntryUuid) throws BusinessException {
		logger.debug("downloading share : " + shareEntryUuid);
		String lsUid = actorVo.getLsUid();
		if(lsUid == null) {
			logger.error("Can't find user with null parametter.");
			return null;
		}
		
		User actor = userService.findByLsUid(lsUid);
		if(actor == null) {
			logger.error("Can't find logged user.");
			return null;
		}
		
		try {
			return shareEntryService.getShareStream(actor, shareEntryUuid);
		} catch (BusinessException e) {
			logger.error("Can't get document thumbnail : " + shareEntryUuid + " : " + e.getMessage());
			throw e;
		}
	}



	@Override
	public boolean isSignedShare(UserVo actorVo, ShareDocumentVo shareVo) {
		boolean res = false;
		User actor = userService.findByLsUid(actorVo.getLsUid());
		try {
			ShareEntry share = shareEntryService.findByUuid(actor, shareVo.getIdentifier());
			Set<Signature> signatures = share.getDocumentEntry().getDocument().getSignatures();
			if(signatures!=null && signatures.size()>0) res = true;
		} catch (BusinessException e) {
			logger.error("Can't find document : " + shareVo.getIdentifier() + ": " + e.getMessage());
		}
		return res;
	}
	
}
