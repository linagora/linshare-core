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
package org.linagora.linShare.core.Facade.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.linagora.linShare.core.Facade.ShareFacade;
import org.linagora.linShare.core.domain.constants.UserType;
import org.linagora.linShare.core.domain.entities.Contact;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.Group;
import org.linagora.linShare.core.domain.entities.GroupMember;
import org.linagora.linShare.core.domain.entities.Guest;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.entities.MailContainerWithRecipient;
import org.linagora.linShare.core.domain.entities.SecuredUrl;
import org.linagora.linShare.core.domain.entities.Share;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.objects.SuccessesAndFailsItems;
import org.linagora.linShare.core.domain.transformers.impl.DocumentTransformer;
import org.linagora.linShare.core.domain.transformers.impl.GroupTransformer;
import org.linagora.linShare.core.domain.transformers.impl.ShareTransformer;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.GroupVo;
import org.linagora.linShare.core.domain.vo.ShareDocumentVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.exception.TechnicalErrorCode;
import org.linagora.linShare.core.exception.TechnicalException;
import org.linagora.linShare.core.repository.DocumentRepository;
import org.linagora.linShare.core.repository.GroupRepository;
import org.linagora.linShare.core.repository.UserRepository;
import org.linagora.linShare.core.service.AbstractDomainService;
import org.linagora.linShare.core.service.DocumentService;
import org.linagora.linShare.core.service.MailContentBuildingService;
import org.linagora.linShare.core.service.NotifierService;
import org.linagora.linShare.core.service.ShareService;
import org.linagora.linShare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShareFacadeImpl implements ShareFacade {

	private static final Logger logger = LoggerFactory.getLogger(ShareFacadeImpl.class);
	
	private final ShareService shareService;
	
	private final ShareTransformer shareTransformer;
	
	private final UserRepository<User> userRepository;

	private final DocumentRepository documentRepository;

	private final GroupRepository groupRepository;
	
	private final NotifierService notifierService;
    
    private final MailContentBuildingService mailElementsFactory;
	
	private final UserService userService;

    private final DocumentService documentService;

    private final GroupTransformer groupTransformer;
	
	private final DocumentTransformer documentTransformer;
	
	private final AbstractDomainService abstractDomainService;
	
	private final String urlBase;
	
	private final String urlInternal;
    
	public ShareFacadeImpl(
			final ShareService shareService,
			final ShareTransformer shareTransformer,
			final UserRepository<User> userRepository,
			final DocumentRepository documentRepository,
			final GroupRepository groupRepository,
			final NotifierService mailNotifierService,
			final UserService userService,
            final DocumentService documentService,
    		final MailContentBuildingService mailElementsFactory,
    		final GroupTransformer groupTransformer,
			final DocumentTransformer documentTransformer,
			final String urlBase,
			final String urlInternal,
			final AbstractDomainService abstractDomainService) {
		super();
		this.shareService = shareService;
		this.shareTransformer = shareTransformer;
		this.userRepository = userRepository;
		this.documentRepository = documentRepository;
		this.groupRepository = groupRepository;
		this.notifierService=mailNotifierService;
		this.userService = userService;
        this.documentService = documentService;
		this.mailElementsFactory = mailElementsFactory;
		this.groupTransformer = groupTransformer;
		this.documentTransformer = documentTransformer;
		this.urlBase = urlBase;
		this.urlInternal = urlInternal;
		this.abstractDomainService = abstractDomainService;
	}
	
	public SuccessesAndFailsItems<ShareDocumentVo> createSharing(UserVo owner, List<DocumentVo> documents, List<UserVo> recipients, String comment, Calendar expirationDate) throws BusinessException {
		logger.debug("createSharing:Begin");
		List<User> recipientsList = new ArrayList<User>();
		
		for (UserVo userVo : recipients) {
			try {
//				recipientsList.add(userService.findOrCreateUser(userVo.getMail(), userVo.getDomainIdentifier()));
				recipientsList.add(userService.findOrCreateUserWithDomainPolicies(userVo.getMail(), owner.getDomainIdentifier()));
			} catch (BusinessException e) {
				logger.error("Could not find the recipient " + userVo.getMail() + " in the database nor in the ldap");
				throw e;
			}
		}
		
		List<Document> docList = new ArrayList<Document>();
		for (DocumentVo documentVo : documents) {
			docList.add(documentRepository.findById(documentVo.getIdentifier()));
		}
		SuccessesAndFailsItems<Share> successAndFails = shareService.shareDocumentsToUser(docList, userRepository.findByLogin(owner.getLogin()), recipientsList, comment, expirationDate);
		
		
		SuccessesAndFailsItems<ShareDocumentVo> results = new SuccessesAndFailsItems<ShareDocumentVo>();
		results.setFailsItem(shareTransformer.disassembleList(successAndFails.getFailsItem()));
		results.setSuccessesItem(shareTransformer.disassembleList(successAndFails.getSuccessesItem()));
		logger.debug("createSharing:End");
		return results;
	}

	public SuccessesAndFailsItems<ShareDocumentVo> createSharingWithMail(UserVo owner, List<DocumentVo> documents, List<UserVo> recipients, MailContainer mailContainer, Calendar expirationDate, boolean isOneDocEncrypted, String jwsEncryptUrlString) throws BusinessException {
		logger.debug("createSharingWithMail:Begin");
		SuccessesAndFailsItems<ShareDocumentVo> result = createSharing(owner,documents,recipients, mailContainer.getPersonalMessage(), expirationDate);
		
		//Sending the mails
		List<UserVo> successfullRecipient = new ArrayList<UserVo>();
		for (ShareDocumentVo successfullSharing : result.getSuccessesItem()) {
			logger.debug("share:result:" + result);
			if (!successfullRecipient.contains(successfullSharing.getReceiver())) {
				successfullRecipient.add(successfullSharing.getReceiver());
			}
			
		}
		
		User owner_ = userRepository.findByLogin(owner.getLogin());
		
		
		List<MailContainerWithRecipient> mailContainerWithRecipient = new ArrayList<MailContainerWithRecipient>();
		
		
		for(UserVo userVo : successfullRecipient){
			logger.debug("Sending sharing notification to user " + userVo.getLogin());
			User recipient = userRepository.findByLogin(userVo.getLogin());
			String linshareUrl = userVo.isGuest() ? urlBase : urlInternal;
			
			mailContainerWithRecipient.add(mailElementsFactory.buildMailNewSharingWithRecipient(owner_, mailContainer, owner_, recipient, documents, linshareUrl, "", null, isOneDocEncrypted, jwsEncryptUrlString));

		}
		
		notifierService.sendAllNotifications(owner.getMail(),mailContainerWithRecipient);
		logger.debug("createSharingWithMail:End");
		return result;
	}
	

	public List<ShareDocumentVo> getAllSharingReceivedByUser(UserVo recipient) {
		User userRecipient = userRepository.findByLogin(recipient.getLogin());
		if (userRecipient==null) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Could not find the user");
		}
		
		return shareTransformer.disassembleList(new ArrayList<Share>(userRecipient.getReceivedShares()));		
	}

	public List<ShareDocumentVo> getSharingsByUserAndFile(UserVo sender, DocumentVo document) {
		User userSender = userRepository.findByLogin(sender.getLogin());
		if (userSender==null) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Could not find the user");
		}
		
		Set<Share> shares = shareService.getSentSharesByUser(userSender);

		List<Share> sharingsOfDocument = new ArrayList<Share>();
		for (Share share : shares) {			
			if (share.getDocument().getIdentifier().equalsIgnoreCase(document.getIdentifier())) {
				sharingsOfDocument.add(share);
			}
		}
		
		return shareTransformer.disassembleList(sharingsOfDocument);
	}

	public void deleteSharing(ShareDocumentVo share, UserVo actor) throws BusinessException {
		User actorUser = userRepository.findByLogin(actor.getLogin());
		
		Share shareToDelete = shareTransformer.assemble(share);
	
		shareService.deleteShare(shareToDelete, actorUser);
		
		shareService.refreshShareAttributeOfDoc(shareToDelete.getDocument());
		
	}

    public DocumentVo createLocalCopy(ShareDocumentVo shareDocumentVo, UserVo userVo) throws BusinessException {
        Share share = shareTransformer.assemble(shareDocumentVo);
		User user = userRepository.findByLogin(userVo.getLogin());
        
        // create a copy of the document :
        Document copyDoc = documentService.duplicateDocument(share.getDocument(), user);
        DocumentVo copyDocVo = documentTransformer.disassemble(copyDoc);
        copyDoc = null; // prevent Hibernate StaleStateException
		
        // remove the share :
        shareService.removeReceivedShareForUser(share, user, user);
        
        //log the copy
        shareService.logLocalCopyOfDocument(share, user);
		
        // no more sharing of this file?
		shareService.refreshShareAttributeOfDoc(share.getDocument());
		
		return copyDocVo;
    }


    public SuccessesAndFailsItems<ShareDocumentVo> createSharingWithMailUsingRecipientsEmail(
			UserVo ownerVo, List<DocumentVo> documents,
			List<String> recipientsEmail,
			boolean secureSharing, MailContainer mailContainer)
			throws BusinessException {
		return createSharingWithMailUsingRecipientsEmailAndExpiryDate(ownerVo, documents, recipientsEmail, secureSharing, mailContainer,null);
	}


    public SuccessesAndFailsItems<ShareDocumentVo> createSharingWithMailUsingRecipientsEmailAndExpiryDate(
			UserVo ownerVo, List<DocumentVo> documents,
			List<String> recipientsEmailInput,
			boolean secureSharing, MailContainer mailContainer,
			Calendar expiryDateSelected)
			throws BusinessException {
    	
    	// Find the owner
    	User owner = userService.findOrCreateUserWithDomainPolicies(ownerVo.getMail(), ownerVo.getDomainIdentifier());
		SuccessesAndFailsItems<ShareDocumentVo> result = new SuccessesAndFailsItems<ShareDocumentVo>();
		List<UserVo> knownRecipients = new ArrayList<UserVo>();
		List<Contact> unKnownRecipientsEmail = new ArrayList<Contact>();
		
		logger.debug("The current user is : " + owner.getMail());
		logger.debug("recipientsEmailInput size : " + recipientsEmailInput.size());
		List<String> recipientsEmail = new ArrayList<String>();
		if(owner.getUserType().equals(UserType.GUEST) && ((Guest)owner).isRestricted()) {
			List<String> guestAllowedContacts= userService.getGuestEmailContacts(owner.getLogin());
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
			logger.debug("Only " + recipientsEmail.size() + " contacts are authorized for " + owner.getMail());
		} else {
			recipientsEmail.addAll(recipientsEmailInput);
		}
		logger.debug("recipientsEmail size : " + recipientsEmail.size());
		logger.debug("unKnownRecipientsEmail size : " + unKnownRecipientsEmail.size());
		logger.debug("unKnownRecipientsEmail  : " + unKnownRecipientsEmail.toString());
    	
    	boolean isOneDocEncrypted = false;
		for (DocumentVo oneDoc : documents) {
			if(oneDoc.getEncrypted()==true) isOneDocEncrypted = true;
		}
		
		//if one document is encrypted include message html or txt asset for "crypted" in the final message
		String jwsEncryptUrlString = "";
		if(isOneDocEncrypted){
			StringBuffer jwsEncryptUrl = new StringBuffer();
			jwsEncryptUrl.append(urlBase);
			if(!urlBase.endsWith("/")) jwsEncryptUrl.append("/");
			jwsEncryptUrl.append("localDecrypt");
			jwsEncryptUrlString = jwsEncryptUrl.toString();
		}
		
		// find known and unknown recipients of the share
		User tempRecipient = null;
		for (String mail : recipientsEmail) {
			
			try {
				tempRecipient= userService.findOrCreateUserWithDomainPolicies(mail, owner.getDomainId());
				knownRecipients.add(new UserVo(tempRecipient));
			} catch (BusinessException e) {
				unKnownRecipientsEmail.add(new Contact(mail));
			}
			
//			if(tempRecipient!=null){
//				knownRecipients.add(new UserVo(tempRecipient));
//			}
//			else {
//				unKnownRecipientsEmail.add(new Contact(mail));
//			}
		}
		
		logger.debug("knownRecipients size : " + knownRecipients.size());
		logger.debug("knownRecipients  : " + knownRecipients.toString());
		logger.debug("unKnownRecipientsEmail size : " + unKnownRecipientsEmail.size());
		logger.debug("unKnownRecipientsEmail  : " + unKnownRecipientsEmail.toString());
		
		if(unKnownRecipientsEmail.size()>0){ //secureUrl for these users (no need to have an account to activate sharing)
			
			boolean hasRightsToShareWithExternals = false;
			
			try {
				hasRightsToShareWithExternals = abstractDomainService.hasRightsToShareWithExternals(owner);
			} catch (BusinessException e) {
				logger.error("Could not retrieve domain of sender while sharing to externals: "+owner.getMail());
			}
			
			if (hasRightsToShareWithExternals) {
			
				List<Document> docList = new ArrayList<Document>();
				for (DocumentVo documentVo : documents) {
					docList.add(documentRepository.findById(documentVo.getIdentifier()));
				}
				
				SecuredUrl securedUrl =  null;
				String password = null;
				
				if(secureSharing) {
					//generate password for this sharing 
					password = userService.generatePassword();
				} 
				
				//password is null for unprotected secured url
				securedUrl = shareService.shareDocumentsWithSecuredUrlToUser(ownerVo, docList, password, unKnownRecipientsEmail, expiryDateSelected);
				
				
				//compose the secured url to give in mail
				StringBuffer httpUrlBase = new StringBuffer();
				httpUrlBase.append(urlBase);
				if(!urlBase.endsWith("/")) httpUrlBase.append("/");
				httpUrlBase.append(securedUrl.getUrlPath());
				if(!securedUrl.getUrlPath().endsWith("/")) httpUrlBase.append("/");
				httpUrlBase.append(securedUrl.getAlea());
				
				//securedUrl must be ended with a "/" if no parameter (see urlparam)
				String linShareUrl = httpUrlBase.toString();
				
				List<MailContainerWithRecipient> mailContainerWithRecipient = new ArrayList<MailContainerWithRecipient>();

				for (Contact oneContact : unKnownRecipientsEmail) {
					
					//give email as a parameter, useful to quickly know who is here
					String linShareUrlParam = "?email=" + oneContact.getMail();
					User owner_ = userRepository.findByLogin(ownerVo.getLogin());
					
					mailContainerWithRecipient.add(mailElementsFactory.buildMailNewSharingWithRecipient(owner_, mailContainer, owner_, oneContact.getMail(), documents, linShareUrl, linShareUrlParam, password, isOneDocEncrypted, jwsEncryptUrlString));	
				}
				
				notifierService.sendAllNotifications(ownerVo.getMail(), mailContainerWithRecipient);
			
			} else {
				for (DocumentVo doc : documents) {
					for (Contact oneContact : unKnownRecipientsEmail) {
						UserVo recipient = new UserVo(oneContact.getMail(), "", "", oneContact.getMail(), null);
						ShareDocumentVo failSharing=new ShareDocumentVo(doc, ownerVo, recipient, new GregorianCalendar(), false, "", new GregorianCalendar());
						result.addFailItem(failSharing);
					}
				}
			}
		}
		
		//keep old method to share with user referenced in db
		result.addAll(createSharingWithMail(ownerVo, documents, knownRecipients, mailContainer, expiryDateSelected, isOneDocEncrypted, jwsEncryptUrlString));
		return result;
    }
    
    
    public void sendDownloadNotification(ShareDocumentVo sharedDocument, UserVo currentUser, MailContainer mailContainer) throws BusinessException {
		
		UserVo ownerVo = sharedDocument.getSender();

		User user = userRepository.findByLogin(currentUser.getLogin());
		User owner = userRepository.findByLogin(ownerVo.getLogin());
		
		Document doc = documentRepository.findById(sharedDocument.getIdentifier());
		List<Document> docList = new ArrayList<Document>();
		docList.add(doc);
		
		notifierService.sendAllNotifications(currentUser.getMail(),mailElementsFactory.buildMailRegisteredDownloadWithOneRecipient(owner, mailContainer, docList, user, owner));
    }
    
    public void sendSharedUpdateDocNotification(DocumentVo currentDoc,
    		UserVo currentUser, String fileSizeTxt, String oldFileName,
    		MailContainer mailContainer) throws BusinessException {
    	
    	//1) share with secured url, notification to users.

		User user = userRepository.findByLogin(currentUser.getLogin());
    	Document doc = documentRepository.findById(currentDoc.getIdentifier());
    	
    	List<SecuredUrl> urls = shareService.getSecureUrlLinkedToDocument(doc);
    	
    	String sUrlBase = this.urlBase;
    	if (!(sUrlBase.charAt(sUrlBase.length()-1)=='/')) {
    		sUrlBase = sUrlBase.concat("/");
		}
    	String sUrlDownload = "";
    	
		List<MailContainerWithRecipient> mailContainerWithRecipient = new ArrayList<MailContainerWithRecipient>();
 	
    	for (SecuredUrl securedUrl : urls) {
			List<Contact> recipients = securedUrl.getRecipients();
			sUrlDownload = sUrlBase.concat(securedUrl.getUrlPath()+"/");
			sUrlDownload = sUrlDownload.concat(securedUrl.getAlea());
			
			for (Contact contact : recipients) {
    			String urlparam = "?email="+contact.getMail();
    			mailContainerWithRecipient.add(mailElementsFactory.buildMailSharedDocUpdatedWithRecipient(user, mailContainer, user, contact.getMail(), doc, oldFileName, fileSizeTxt, sUrlDownload, urlparam));
			}
		}
		
    	notifierService.sendAllNotifications(currentUser.getMail(),mailContainerWithRecipient);
    	
    	//2) normal share, notification to guest and internal user
		List<Share> listShare = shareService.getSharesLinkedToDocument(doc);
			
		mailContainerWithRecipient.clear();
		
		for (Share share : listShare) {
			sUrlDownload = share.getReceiver().getUserType().equals(UserType.GUEST) ? urlBase : urlInternal;
			
			mailContainerWithRecipient.add(mailElementsFactory.buildMailSharedDocUpdatedWithRecipient(user, mailContainer, user, share.getReceiver(), doc, oldFileName, fileSizeTxt, sUrlDownload, ""));
		}
		notifierService.sendAllNotifications(currentUser.getMail(),mailContainerWithRecipient);

    	
    }
    
    public SuccessesAndFailsItems<ShareDocumentVo> createSharingWithGroups(
    		UserVo ownerVo, List<DocumentVo> documents, List<GroupVo> recipients,
    		MailContainer mailContainer)
    		throws BusinessException {
		User owner = userRepository.findByLogin(ownerVo.getLogin());
    	List<User> groupUserObjectsList = new ArrayList<User>();
    	List<Group> groupList = new ArrayList<Group>();
		
		for (GroupVo groupVo : recipients) {
			try {
				groupUserObjectsList.add(userService.findOrCreateUserWithDomainPolicies(groupVo.getGroupLogin(), ownerVo.getDomainIdentifier()));
				groupList.add(groupRepository.findByName(groupVo.getName()));
			} catch (BusinessException e) {
				logger.error("Could not find the recipient " + groupVo.getGroupLogin() + " in the database");
				throw e;
			}
		}

		
		boolean isOneDocEncrypted = false;
		
		List<Document> docList = new ArrayList<Document>();
		for (DocumentVo documentVo : documents) {
			if(documentVo.getEncrypted()==true) isOneDocEncrypted = true;
			docList.add(documentRepository.findById(documentVo.getIdentifier()));
		}
		
		//if one document is encrypted include message html or txt asset for "crypted" in the final message
		String jwsEncryptUrlString = "";
		if(isOneDocEncrypted){
			StringBuffer jwsEncryptUrl = new StringBuffer();
			jwsEncryptUrl.append(urlBase);
			if(!urlBase.endsWith("/")) jwsEncryptUrl.append("/");
			jwsEncryptUrl.append("localDecrypt");
			jwsEncryptUrlString = jwsEncryptUrl.toString();
		}
		
		SuccessesAndFailsItems<Share> successAndFails = shareService.shareDocumentsToUser(docList, 
				userRepository.findByLogin(owner.getLogin()),
				groupUserObjectsList, "", null);
		
		
		SuccessesAndFailsItems<ShareDocumentVo> results = new SuccessesAndFailsItems<ShareDocumentVo>();
		results.setFailsItem(shareTransformer.disassembleList(successAndFails.getFailsItem()));
		results.setSuccessesItem(shareTransformer.disassembleList(successAndFails.getSuccessesItem()));
		
		/**
		 * Send notification ; if a functional mailBox is given, to this mailBox, 
		 * otherwise to each group member.
		 */
		if (results.getSuccessesItem() != null && results.getSuccessesItem().size() > 0) {
			
			
			List<MailContainerWithRecipient> mailContainerWithRecipient = new ArrayList<MailContainerWithRecipient>();
			
			for (Group group : groupList) {
				String functionalMail = group.getFunctionalEmail();
				if (functionalMail != null && functionalMail.length() > 0) {
					
					mailContainerWithRecipient.add(mailElementsFactory.buildMailNewGroupSharingWithRecipient(owner, mailContainer, owner, group, results.getSuccessesItem(), urlBase, "groups", isOneDocEncrypted, jwsEncryptUrlString));
		
				} else {
				
					for (GroupMember member : group.getMembers()) {	
						mailContainerWithRecipient.add(mailElementsFactory.buildMailNewGroupSharingWithRecipient(owner, mailContainer, owner, member.getUser(), group, results.getSuccessesItem(), urlBase, "groups", isOneDocEncrypted, jwsEncryptUrlString));
						
					}
				}
			}
			notifierService.sendAllNotifications(owner.getMail(),mailContainerWithRecipient);
		}
		
		return results;
    }
	
	public void notifyGroupSharingDeleted(ShareDocumentVo shareddoc, UserVo managerVo,
			GroupVo groupVo, MailContainer mailContainer)
			throws BusinessException {
		Group group = groupTransformer.assemble(groupVo);
		Document doc = documentRepository.findById(shareddoc.getIdentifier());
		User manager = userRepository.findByLogin(managerVo.getLogin());
		
		shareService.notifyGroupSharingDeleted(doc, manager, group, mailContainer);
	}
}
