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
import org.linagora.linShare.core.domain.entities.AbstractDomain;
import org.linagora.linShare.core.domain.entities.Contact;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.Guest;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.entities.MailContainerWithRecipient;
import org.linagora.linShare.core.domain.entities.SecuredUrl;
import org.linagora.linShare.core.domain.entities.Share;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.objects.SuccessesAndFailsItems;
import org.linagora.linShare.core.domain.transformers.impl.DocumentTransformer;
import org.linagora.linShare.core.domain.transformers.impl.ShareTransformer;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.ShareDocumentVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessErrorCode;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.exception.TechnicalErrorCode;
import org.linagora.linShare.core.exception.TechnicalException;
import org.linagora.linShare.core.repository.DocumentRepository;
import org.linagora.linShare.core.repository.UserRepository;
import org.linagora.linShare.core.service.AbstractDomainService;
import org.linagora.linShare.core.service.DocumentService;
import org.linagora.linShare.core.service.FunctionalityService;
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

	private final NotifierService notifierService;
    
    private final MailContentBuildingService mailElementsFactory;
	
	private final UserService userService;

    private final DocumentService documentService;

	private final DocumentTransformer documentTransformer;
	
	private final AbstractDomainService abstractDomainService;
	private final FunctionalityService functionalityService;
	
	private final String urlBase;
	
	private final String urlInternal;
    
	public ShareFacadeImpl(
			final ShareService shareService,
			final ShareTransformer shareTransformer,
			final UserRepository<User> userRepository,
			final DocumentRepository documentRepository,
			final NotifierService mailNotifierService,
			final UserService userService,
            final DocumentService documentService,
    		final MailContentBuildingService mailElementsFactory,
			final DocumentTransformer documentTransformer,
			final String urlBase,
			final String urlInternal,
			final AbstractDomainService abstractDomainService,
			final FunctionalityService functionalityService) {
		super();
		this.shareService = shareService;
		this.shareTransformer = shareTransformer;
		this.userRepository = userRepository;
		this.documentRepository = documentRepository;
		this.notifierService=mailNotifierService;
		this.userService = userService;
        this.documentService = documentService;
		this.mailElementsFactory = mailElementsFactory;
		this.documentTransformer = documentTransformer;
		this.urlBase = urlBase;
		this.urlInternal = urlInternal;
		this.abstractDomainService = abstractDomainService;
		this.functionalityService = functionalityService;
	}
	
	
	@Override
	public SuccessesAndFailsItems<ShareDocumentVo> createSharing(UserVo owner, List<DocumentVo> documents, List<UserVo> recipients, Calendar expirationDate) throws BusinessException {
		logger.debug("createSharing:Begin");
		List<User> recipientsList = new ArrayList<User>();
		
		for (UserVo userVo : recipients) {
			try {
				recipientsList.add(userService.findOrCreateUserWithDomainPolicies(userVo.getMail(), owner.getDomainIdentifier()));
			} catch (BusinessException e) {
				logger.error("Could not find the recipient " + userVo.getMail() + " in the database nor in the ldap");
				throw e;
			}
		}
		
		List<Document> docList = getDocumentEntitiesFromVo(documents);
		SuccessesAndFailsItems<Share> successAndFails = shareService.shareDocumentsToUser(docList, userRepository.findByLogin(owner.getLogin()), recipientsList, expirationDate);
		
		SuccessesAndFailsItems<ShareDocumentVo> results = disassembleShareResultList(successAndFails);
		logger.debug("createSharing:End");
		return results;
	}

	
	@Override
	public SuccessesAndFailsItems<ShareDocumentVo> createSharingWithMail(UserVo owner, List<DocumentVo> documents, List<UserVo> recipients, MailContainer mailContainer, Calendar expirationDate, boolean isOneDocEncrypted, String jwsEncryptUrlString) throws BusinessException {
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
		
		User owner_ = userRepository.findByLogin(owner.getLogin());
		
		
		List<MailContainerWithRecipient> mailContainerWithRecipient = new ArrayList<MailContainerWithRecipient>();
		
		
		for(UserVo userVo : successfullRecipient){
			logger.debug("Sending sharing notification to user " + userVo.getLogin());
			User recipient = userRepository.findByLogin(userVo.getLogin());
			String linshareUrl = userVo.isGuest() ? urlBase : urlInternal;
			
			mailContainerWithRecipient.add(mailElementsFactory.buildMailNewSharingWithRecipient(owner_, mailContainer, owner_, recipient, documents, linshareUrl, "", null, isOneDocEncrypted, jwsEncryptUrlString));

		}
		
		notifierService.sendAllNotifications(mailContainerWithRecipient);
		logger.debug("createSharingWithMail:End");
		return result;
	}
	
	
	@Override
	public List<ShareDocumentVo> getAllSharingReceivedByUser(UserVo recipient) {
		User userRecipient = userRepository.findByLogin(recipient.getLogin());
		if (userRecipient==null) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Could not find the user");
		}
		return shareTransformer.disassembleList(new ArrayList<Share>(userRecipient.getReceivedShares()));		
	}

	
	@Override
	public List<ShareDocumentVo> getSharingsByUserAndFile(UserVo sender, DocumentVo document) {
		User userSender = userRepository.findByLogin(sender.getLogin());
		if (userSender==null) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Could not find the user");
		}
		
		Set<Share> shares = userSender.getShares();

		List<Share> sharingsOfDocument = new ArrayList<Share>();
		for (Share share : shares) {			
			if (share.getDocument().getIdentifier().equalsIgnoreCase(document.getIdentifier())) {
				sharingsOfDocument.add(share);
			}
		}
		
		return shareTransformer.disassembleList(sharingsOfDocument);
	}

	
	@Override
	public void deleteSharing(ShareDocumentVo share, UserVo actor) throws BusinessException {
		User actorUser = userRepository.findByLogin(actor.getLogin());
		
		Share shareToDelete = shareTransformer.assemble(share);
	
		shareService.deleteShare(shareToDelete, actorUser);
		
		shareService.refreshShareAttributeOfDoc(shareToDelete.getDocument());
		
	}

	
	@Override
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


	@Override
    public SuccessesAndFailsItems<ShareDocumentVo> createSharingWithMailUsingRecipientsEmail(
    		UserVo ownerVo, List<DocumentVo> documents, List<String> recipientsEmail, boolean secureSharing, MailContainer mailContainer) throws BusinessException {
		return createSharingWithMailUsingRecipientsEmailAndExpiryDate(ownerVo, documents, recipientsEmail, secureSharing, mailContainer,null);
	}


	@Override
    public SuccessesAndFailsItems<ShareDocumentVo> createSharingWithMailUsingRecipientsEmailAndExpiryDate(
			UserVo ownerVo, List<DocumentVo> documents, List<String> recipientsEmailInput, boolean secureSharing, MailContainer mailContainer, Calendar expiryDateSelected) throws BusinessException {
    	
    	// Find the owner
    	User owner = userService.findOrCreateUser(ownerVo.getMail(), ownerVo.getDomainIdentifier());
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
		
		String jwsEncryptUrlString = getJwsEncryptUrlString(isOneDocEncrypted);
		
		// find known and unknown recipients of the share
		User tempRecipient = null;
		for (String mail : recipientsEmail) {			
			try {
				tempRecipient= userService.findOrCreateUserWithDomainPolicies(mail, owner.getDomainId());
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
				hasRightsToShareWithExternals = abstractDomainService.hasRightsToShareWithExternals(owner);
			} catch (BusinessException e) {
				logger.error("Could not retrieve domain of sender while sharing to externals: "+owner.getMail());
			}
			
			if (hasRightsToShareWithExternals) {
			
				List<Document> docList = getDocumentEntitiesFromVo(documents);
				
				SecuredUrl securedUrl = shareService.shareDocumentsWithSecuredAnonymousUrlToUser(owner, docList, secureSharing, unKnownRecipientsEmail, expiryDateSelected);
				
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
					
					mailContainerWithRecipient.add(mailElementsFactory.buildMailNewSharingWithRecipient(
							owner_, mailContainer, owner_, oneContact.getMail(), documents, linShareUrl, linShareUrlParam, securedUrl.getTemporaryPlainTextpassword(), isOneDocEncrypted, jwsEncryptUrlString));	
				}
				
				notifierService.sendAllNotifications(mailContainerWithRecipient);
			
			} else {
				for (DocumentVo doc : documents) {
					for (Contact oneContact : unKnownRecipientsEmail) {
						UserVo recipient = new UserVo(oneContact.getMail(), "", "", oneContact.getMail(), null);
						ShareDocumentVo failSharing=new ShareDocumentVo(doc, ownerVo, recipient, new GregorianCalendar(), false, "", new GregorianCalendar(),0);
						result.addFailItem(failSharing);
					}
				}
			}
		}
		
		//keep old method to share with user referenced in db
		result.addAll(createSharingWithMail(ownerVo, documents, knownRecipients, mailContainer, expiryDateSelected, isOneDocEncrypted, jwsEncryptUrlString));
		return result;
    }
    
    
	@Override
    public void sendDownloadNotification(ShareDocumentVo sharedDocument, UserVo currentUser, MailContainer mailContainer) throws BusinessException {
		
		UserVo ownerVo = sharedDocument.getSender();

		User user = userRepository.findByLogin(currentUser.getLogin());
		User owner = userRepository.findByLogin(ownerVo.getLogin());
		
		Document doc = documentRepository.findById(sharedDocument.getIdentifier());
		List<Document> docList = new ArrayList<Document>();
		docList.add(doc);
		
		notifierService.sendAllNotifications(mailElementsFactory.buildMailRegisteredDownloadWithOneRecipient(owner, mailContainer, docList, user, owner));
    }
    
	
	@Override
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
		
    	notifierService.sendAllNotifications(mailContainerWithRecipient);
    	
    	//2) normal share, notification to guest and internal user
		List<Share> listShare = shareService.getSharesLinkedToDocument(doc);
			
		mailContainerWithRecipient.clear();
		
		for (Share share : listShare) {
			sUrlDownload = share.getReceiver().getUserType().equals(UserType.GUEST) ? urlBase : urlInternal;
			
			mailContainerWithRecipient.add(mailElementsFactory.buildMailSharedDocUpdatedWithRecipient(user, mailContainer, user, share.getReceiver(), doc, oldFileName, fileSizeTxt, sUrlDownload, ""));
		}
		notifierService.sendAllNotifications(mailContainerWithRecipient);

    	
    }
    
//	
//	@Override
//    public SuccessesAndFailsItems<ShareDocumentVo> createSharingWithGroups(UserVo ownerVo, List<DocumentVo> documents, List<GroupVo> recipients, MailContainer mailContainer) throws BusinessException {
//		
//    	logger.debug("ownerVo.getMail():" + ownerVo.getMail());
//    	logger.debug("ownerVo.getDomainIdentifier():" + ownerVo.getDomainIdentifier());
//    	
//		User owner = userRepository.findByMailAndDomain(ownerVo.getDomainIdentifier(), ownerVo.getMail());
//		
//    	List<User> groupUserObjectsList = new ArrayList<User>();
//    	List<Group> groupList = new ArrayList<Group>();
//		
//		for (GroupVo groupVo : recipients) {
//			groupUserObjectsList.add(userService.findUnkownUserInDB(groupVo.getGroupLogin()));
//			groupList.add(groupRepository.findByName(groupVo.getName()));
//		}
//		
//		// Convert DocVo to Doc Entities
//		List<Document> docList = getDocumentEntitiesFromVo(documents);
//		
//		// Share docs to groups.
//		SuccessesAndFailsItems<ShareDocumentVo> successesAndFailsItems = disassembleShareResultList(shareService.shareDocumentsToUser(docList,owner, groupUserObjectsList, null));
//		
//		// Check if at least one doc is encrypted.
//		boolean oneDocIsEncrypted = oneDocIsEncrypted(docList);
//		
//		// Send success notifications id needed
//	 	groupNotification(mailContainer, owner, groupList, oneDocIsEncrypted, successesAndFailsItems);
//		
//		return successesAndFailsItems;
//    }
//    
//	
//	@Override
//    public SuccessesAndFailsItems<ShareDocumentVo> createSharingWithGroup(UserVo ownerVo, List<DocumentVo> documents, String targetGroupID, MailContainer mailContainer) throws BusinessException {
//		
//    	logger.debug("ownerVo.getMail():" + ownerVo.getMail());
//    	logger.debug("ownerVo.getDomainIdentifier():" + ownerVo.getDomainIdentifier());
//    	
//		User owner = userRepository.findByMailAndDomain(ownerVo.getDomainIdentifier(), ownerVo.getMail());
//		
//    	List<User> groupUserObjectsList = new ArrayList<User>();
//    	List<Group> groupList = new ArrayList<Group>();
//		
//		groupUserObjectsList.add(userService.findUnkownUserInDB(targetGroupID));
//		String groupName = targetGroupID.split("@",targetGroupID.length())[0];
//		logger.debug("groupName : " +groupName);
//		
//		groupList.add(groupRepository.findByName(groupName));
//		
//		// Convert DocVo to Doc Entities
//		List<Document> docList = getDocumentEntitiesFromVo(documents);
//		
//		// Share docs to groups.
//		SuccessesAndFailsItems<ShareDocumentVo> successesAndFailsItems = disassembleShareResultList(shareService.shareDocumentsToUser(docList, owner, groupUserObjectsList));
//		
//		// Check if at least one doc is encrypted.
//		boolean oneDocIsEncrypted = oneDocIsEncrypted(docList);
//		
//		// Send success notifications id needed
//	 	groupNotification(mailContainer, owner, groupList, oneDocIsEncrypted, successesAndFailsItems);
//		
//		return successesAndFailsItems;
//    }


	private SuccessesAndFailsItems<ShareDocumentVo> disassembleShareResultList(SuccessesAndFailsItems<Share> successAndFails) {
		SuccessesAndFailsItems<ShareDocumentVo> results = new SuccessesAndFailsItems<ShareDocumentVo>();
		results.setFailsItem(shareTransformer.disassembleList(successAndFails.getFailsItem()));
		results.setSuccessesItem(shareTransformer.disassembleList(successAndFails.getSuccessesItem()));
		return results;
	}

	private String getJwsEncryptUrlString(boolean isOneDocEncrypted) {
		String jwsEncryptUrlString = "";
		if(isOneDocEncrypted){
			StringBuffer jwsEncryptUrl = new StringBuffer();
			jwsEncryptUrl.append(urlBase);
			if(!urlBase.endsWith("/")) jwsEncryptUrl.append("/");
			jwsEncryptUrl.append("localDecrypt");
			jwsEncryptUrlString = jwsEncryptUrl.toString();
		}
		return jwsEncryptUrlString;
	}

	private boolean oneDocIsEncrypted(List<Document> docList) {
		boolean isOneDocEncrypted = false;
		for(Document doc : docList) {
			if(doc.getEncrypted()==true) isOneDocEncrypted = true;
		}
		return isOneDocEncrypted;
	}

	private List<Document> getDocumentEntitiesFromVo(List<DocumentVo> documents) {
		List<Document> docList = new ArrayList<Document>();
		for (DocumentVo documentVo : documents) {
			docList.add(documentRepository.findById(documentVo.getIdentifier()));
		}
		return docList;
	}

//	/**
//	 * 	Send success notification only to the functional mailBox if it is given, otherwise notification is sent to each group member.
//	 * @param mailContainer
//	 * @param owner
//	 * @param groupList
//	 * @param isOneDocEncrypted
//	 * @param successesAndFailsItems
//	 * @throws BusinessException
//	 */
//	private void groupNotification(MailContainer mailContainer, User owner,	List<Group> groupList, boolean oneDocIsEncrypted, SuccessesAndFailsItems<ShareDocumentVo> successesAndFailsItems) throws BusinessException {
//		if (successesAndFailsItems.getSuccessesItem() != null && successesAndFailsItems.getSuccessesItem().size() > 0) {
//			
//			//if one document is encrypted include message html or txt asset for "crypted" in the final message
//			String jwsEncryptUrlString = getJwsEncryptUrlString(oneDocIsEncrypted);
//
//			List<MailContainerWithRecipient> mailContainerWithRecipient = new ArrayList<MailContainerWithRecipient>();
//			
//			for (Group group : groupList) {
//				String functionalMail = group.getFunctionalEmail();
//				if (functionalMail != null && functionalMail.length() > 0) {
//					
//					mailContainerWithRecipient.add(mailElementsFactory.buildMailNewGroupSharingWithRecipient(owner, mailContainer, owner, group, successesAndFailsItems.getSuccessesItem(), urlBase, "groups", oneDocIsEncrypted, jwsEncryptUrlString));
//		
//				} else {
//				
//					for (GroupMember member : group.getMembers()) {	
//						mailContainerWithRecipient.add(mailElementsFactory.buildMailNewGroupSharingWithRecipient(owner, mailContainer, owner, member.getUser(), group, successesAndFailsItems.getSuccessesItem(), urlBase, "groups", oneDocIsEncrypted, jwsEncryptUrlString));
//						
//					}
//				}
//			}
//			notifierService.sendAllNotifications(mailContainerWithRecipient);
//		}
//	}
//	
	
//	@Override
//	public void notifyGroupSharingDeleted(ShareDocumentVo shareddoc, UserVo managerVo,
//			GroupVo groupVo, MailContainer mailContainer)
//			throws BusinessException {
//		Group group = groupTransformer.assemble(groupVo);
//		Document doc = documentRepository.findById(shareddoc.getIdentifier());
//		User manager = userRepository.findByLogin(managerVo.getLogin());
//		
//		shareService.notifyGroupSharingDeleted(doc, manager, group, mailContainer);
//	}
//	
	
	@Override
	public boolean isVisibleSecuredAnonymousUrlCheckBox(String domainIdentifier) {
		try {
			AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);
			return shareService.isSauAllowed(domain);
		} catch (BusinessException e) {
			logger.error("Can't find domain : " + domainIdentifier);
			logger.debug(e.getMessage());
		}
		return false;
	}

	
	@Override
	public boolean getDefaultSecuredAnonymousUrlCheckBoxValue(String domainIdentifier) {
		try {
			AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);
			return shareService.getDefaultSauValue(domain);
		} catch (BusinessException e) {
			logger.error("Can't find domain : " + domainIdentifier);
			logger.debug(e.getMessage());
		}
		return false;
	}

	
	@Override
	public ShareDocumentVo getShareDocumentVoById(long persistenceId) {
		return shareTransformer.disassemble(shareService.getShare(persistenceId));		
	}

	
	@Override
	public void updateShareComment(String persistenceId, String comment) throws IllegalArgumentException, BusinessException {
		logger.debug("updateShareComment:" + String.valueOf(persistenceId));
		Share share = shareService.getShare(Long.valueOf(persistenceId));
		share.setComment(comment);
		logger.debug("comment : " + comment);
		shareService.updateShare(share);
	}
}
