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
package org.linagora.linShare.core.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.linagora.linShare.core.dao.FileSystemDao;
import org.linagora.linShare.core.domain.constants.Language;
import org.linagora.linShare.core.domain.constants.LogAction;
import org.linagora.linShare.core.domain.constants.UserType;
import org.linagora.linShare.core.domain.entities.Contact;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.FileLogEntry;
import org.linagora.linShare.core.domain.entities.Group;
import org.linagora.linShare.core.domain.entities.GroupMember;
import org.linagora.linShare.core.domain.entities.Guest;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.entities.MailContainerWithRecipient;
import org.linagora.linShare.core.domain.entities.SecuredUrl;
import org.linagora.linShare.core.domain.entities.Share;
import org.linagora.linShare.core.domain.entities.ShareLogEntry;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.objects.SuccessesAndFailsItems;
import org.linagora.linShare.core.domain.objects.TimeUnitBooleanValueFunctionality;
import org.linagora.linShare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.DocumentRepository;
import org.linagora.linShare.core.repository.GroupRepository;
import org.linagora.linShare.core.repository.GuestRepository;
import org.linagora.linShare.core.repository.SecuredUrlRepository;
import org.linagora.linShare.core.repository.ShareRepository;
import org.linagora.linShare.core.repository.UserRepository;
import org.linagora.linShare.core.service.AbstractDomainService;
import org.linagora.linShare.core.service.FunctionalityService;
import org.linagora.linShare.core.service.LogEntryService;
import org.linagora.linShare.core.service.MailContentBuildingService;
import org.linagora.linShare.core.service.NotifierService;
import org.linagora.linShare.core.service.SecuredUrlService;
import org.linagora.linShare.core.service.ShareExpiryDateService;
import org.linagora.linShare.core.service.ShareService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShareServiceImpl implements ShareService{


	
	private final UserRepository<User> userRepository;
	private final ShareRepository shareRepository;
	private final LogEntryService logEntryService;
	private final SecuredUrlRepository securedUrlRepository;
	private final DocumentRepository documentRepository;
	private final SecuredUrlService secureUrlService;
	private final FileSystemDao fileSystemDao;
	private final ShareExpiryDateService shareExpiryDateService;
	private final NotifierService notifierService;
	private final MailContentBuildingService mailBuilder;
	private final GroupRepository groupRepository;
	private final GuestRepository guestRepository;
	private List<Integer> datesForNotifyUpcomingOutdatedShares;
	private final String urlBase;
	private final FunctionalityService functionalityService;
	private final AbstractDomainService abstractDomainService;
	
	
//    private final DocumentService documentService;
	
	private static final Logger logger = LoggerFactory.getLogger(ShareServiceImpl.class);

	public ShareServiceImpl(final UserRepository<User> userRepository,
			final ShareRepository shareRepository,
			final LogEntryService logEntryService,
			final SecuredUrlRepository securedUrlRepository,
			final DocumentRepository documentRepository, final SecuredUrlService secureUrlService, 
			final FileSystemDao fileSystemDao, final ShareExpiryDateService shareExpiryDateService,
			final NotifierService notifierService, final MailContentBuildingService mailBuilder,
			final GroupRepository groupRepository, final GuestRepository guestRepository,
			final String datesForNotifyUpcomingOutdatedShares, final String urlBase,
			final FunctionalityService functionalityService,
			final AbstractDomainService abstractDomainService) {
		
		this.userRepository=userRepository;
		this.shareRepository=shareRepository;
		this.logEntryService=logEntryService;
		this.securedUrlRepository=securedUrlRepository;
		this.documentRepository = documentRepository;
        this.secureUrlService = secureUrlService;
        this.fileSystemDao = fileSystemDao;
        this.shareExpiryDateService = shareExpiryDateService;
        this.notifierService = notifierService;
        this.mailBuilder = mailBuilder;
        this.groupRepository = groupRepository;
        this.guestRepository = guestRepository;
        this.datesForNotifyUpcomingOutdatedShares = new ArrayList<Integer>();
        String[] dates = datesForNotifyUpcomingOutdatedShares.split(",");
        for (String date : dates) {
        	this.datesForNotifyUpcomingOutdatedShares.add(Integer.parseInt(date));
		}
        this.urlBase = urlBase;
        this.functionalityService = functionalityService;
        this.abstractDomainService = abstractDomainService;
	}
	/**
	 * @see org.linagora.linShare.core.service.ShareService#getReceivedDocumentsByUser(User)
	 */
	public List<Document> getReceivedDocumentsByUser(User user) {

		Set<Share> shares=user.getReceivedShares();
		ArrayList<Document> sharesDocument=new ArrayList<Document>();
		for(Share share:shares){
			sharesDocument.add(share.getDocument());
		}

		return sharesDocument;
	}
	/**
	 * @see org.linagora.linShare.core.service.ShareService#getSentDocumentsByUser(User)

	 */
	public List<Document> getSentDocumentsByUser(User user) {
		Set<Share> shares=user.getShares();
		ArrayList<Document> sharesDocument=new ArrayList<Document>();
		for(Share share:shares){
			sharesDocument.add(share.getDocument());
		}
		return sharesDocument;
	}
	/**
	 * @see org.linagora.linShare.core.service.ShareService#getReceivedSharesByUser(User)

	 */
	public Set<Share> getReceivedSharesByUser(User user) {
		return user.getReceivedShares();
	}

	/**
	 * @see org.linagora.linShare.core.service.ShareService#getReceivedSharesByUser(User)

	 */
	public Set<Share> getSentSharesByUser(User user) {
		return user.getShares();
	}


	/**
	 * @see org.linagora.linShare.core.service.ShareService#getReceivedSharesByUser(User)

	 */
	public void removeReceivedShareForUser(Share share, User user, User actor) throws BusinessException {
		user.deleteReceivedShare(share);
		userRepository.update(user);
		
		ShareLogEntry logEntry = new ShareLogEntry(actor.getMail(), actor.getFirstName(), actor.getLastName(),
				actor.getDomainId(),
        		LogAction.SHARE_DELETE, "Remove a received sharing", 
        		share.getDocument().getName(), share.getDocument().getSize(), share.getDocument().getType(),
        		user.getMail(), user.getFirstName(), user.getLastName(), user.getDomainId(), null);
        
        logEntryService.create(logEntry);
	}

	/**
	 * @see org.linagora.linShare.core.service.ShareService#removeReceivedSharesForUser(List, User)

	 */
	public void removeReceivedSharesForUser(List<Share> shares, User user, User actor) throws BusinessException {
		for(Share currentShare:shares){
			user.deleteReceivedShare(currentShare);
			userRepository.update(user);
			
			ShareLogEntry logEntry = new ShareLogEntry(actor.getMail(), actor.getFirstName(), actor.getLastName(),
					actor.getDomainId(),
	        		LogAction.SHARE_DELETE, "Remove a received sharing", 
	        		currentShare.getDocument().getName(), currentShare.getDocument().getSize(), currentShare.getDocument().getType(),
	        		user.getMail(), user.getFirstName(), user.getLastName(), user.getDomainId(), null);
	        
	        logEntryService.create(logEntry);
		}

	}

	/**
	 * @see org.linagora.linShare.core.service.ShareService#removeSentShareForUser(Share, User)

	 */
	public void removeSentShareForUser(Share share, User user, User actor) throws BusinessException {
		user.deleteShare(share);
		userRepository.update(user);
		
		ShareLogEntry logEntry = new ShareLogEntry(actor.getMail(), actor.getFirstName(), actor.getLastName(),
				actor.getDomainId(),
        		LogAction.SHARE_DELETE, "Cancel a sharing", 
        		share.getDocument().getName(), share.getDocument().getSize(), share.getDocument().getType(),
        		user.getMail(), user.getFirstName(), user.getLastName(), user.getDomainId(), null);
        
        logEntryService.create(logEntry);
        
	}

	/**
	 * @see org.linagora.linShare.core.service.ShareService#removeSentSharesForUser(List, User)

	 */
	public void removeSentSharesForUser(List<Share> shares, User user, User actor)  throws BusinessException{
		for(Share currentShare:shares){
			user.deleteShare(currentShare);
			userRepository.update(user);
			
			ShareLogEntry logEntry = new ShareLogEntry(actor.getMail(), actor.getFirstName(), actor.getLastName(),
					actor.getDomainId(),
	        		LogAction.SHARE_DELETE, "Cancel a sharing", 
	        		currentShare.getDocument().getName(), currentShare.getDocument().getSize(), currentShare.getDocument().getType(),
	        		user.getMail(), user.getFirstName(), user.getLastName(), user.getDomainId(), null);
	        
	        logEntryService.create(logEntry);
	        
		}
	}
	
	
	public void deleteShare(Share share, User actor) throws BusinessException {
		
		shareRepository.delete(share);
		
		
		ShareLogEntry logEntry = new ShareLogEntry(actor.getMail(), actor.getFirstName(), actor.getLastName(),
				actor.getDomainId(),
        		LogAction.SHARE_DELETE, "Delete a sharing", 
        		share.getDocument().getName(), share.getDocument().getSize(), share.getDocument().getType(),
        		share.getReceiver().getMail(), share.getReceiver().getFirstName(), share.getReceiver().getLastName(), share.getReceiver().getDomainId(), null);
        
        logEntryService.create(logEntry);
        
	}
	
	/**
	 * @see org.linagora.linShare.core.service.ShareService#shareDocumentToUser(Document, User, User, String)

	 */
	public SuccessesAndFailsItems<Share> shareDocumentsToUser(List<Document> documents, User sender, List<User> recipients,String comment, Calendar expiryDate){
		
		SuccessesAndFailsItems<Share> returnItems = new SuccessesAndFailsItems<Share>();
        
		
		for (User recipient : recipients) {
			
			boolean allowedToShareWithHim = true;
			
//			try {
//				allowedToShareWithHim = abstractDomainService.userIsAllowedToShareWith(sender, recipient);
//				
//			} catch (BusinessException e1) {
//				logger.error("Failed to read domain of sender while sharing documents", e1);
//				allowedToShareWithHim = false;
//			}
			
			if (!allowedToShareWithHim) {
				logger.debug("The current user is not allowed to share with : " + recipient); 
				generateFailItemsForUser(documents, sender, comment, returnItems, recipient);
				break;
			}
			
			
			for (Document document : documents) {
				//Creating a shareDocument
				
				Share failSharing=new Share(sender,recipient,document,comment,new GregorianCalendar(),true,false);
			
				try{

					// If the user have not selected an expiration date, compute default date
					if (expiryDate == null) {
						expiryDate=shareExpiryDateService.computeShareExpiryDate(document, sender);
					}						 					

		
					Share share=new Share(sender,recipient,document,comment,expiryDate,true,false);
					Share shareEntity=shareRepository.create(share);
		
		
					recipient.addReceivedShare(shareEntity);
					sender.addShare(shareEntity);
					
					if (recipient.getUserType().equals(UserType.GROUP)) {
						document.setSharedWithGroup(true);
					}
					else {
						document.setShared(true);
					}
					
					// update guest account expiry date
					if (recipient.getUserType().equals(UserType.GUEST)) {
						
						// get new guest expiry date
						Calendar guestExpiryDate = Calendar.getInstance();
						TimeUnitValueFunctionality guestFunctionality = functionalityService.getGuestAccountExpiryTimeFunctionality(recipient.getDomain());
				        guestExpiryDate.add(guestFunctionality.toCalendarValue(), guestFunctionality.getValue());
				        
						Guest guest = guestRepository.findByLogin(recipient.getLogin());
						guest.setExpiryDate(guestExpiryDate.getTime());
						guestRepository.update(guest);
					}
	
					userRepository.update(recipient);
					userRepository.update(sender);
		
					ShareLogEntry logEntry = new ShareLogEntry(sender.getMail(), sender.getFirstName(), sender.getLastName(),
							sender.getDomainId(),
				        	LogAction.FILE_SHARE, "Sharing of a file", document.getName(), document.getSize(), document.getType(),
				        	recipient.getMail(), recipient.getFirstName(), recipient.getLastName(), recipient.getDomainId(), expiryDate);
				       
				    logEntryService.create(logEntry);
				        
				    returnItems.addSuccessItem(shareEntity);

				} catch (IllegalArgumentException e) {
					logger.error("Could not create the sharing for owner " + sender.getLogin() + " to user " + recipient.getLogin() +
								" of doc " + document.getIdentifier(), e);
					returnItems.addFailItem(failSharing);
				} catch (BusinessException e) {
					logger.error("Could not create the sharing for owner " + sender.getLogin() + " to user " + recipient.getLogin() +
						" of doc " + document.getIdentifier(), e);
					returnItems.addFailItem(failSharing);
				}
			}
		}
		return returnItems;

	}
	private void generateFailItemsForUser(List<Document> documents,
			User sender, String comment,
			SuccessesAndFailsItems<Share> returnItems, User recipient) {
		for (Document doc : documents) {
			Share failSharing=new Share(sender,recipient,doc,comment,new GregorianCalendar(),true,false);
			returnItems.addFailItem(failSharing);
		}
	}
	public void deleteAllSharesWithDocument(Document doc, User actor, MailContainer mailContainer)
			throws BusinessException {
		
		//1)delete normal share
		List<Share> listShare = shareRepository.getSharesLinkedToDocument(doc);
		
		
		List<MailContainerWithRecipient> mailContainerWithRecipient = new ArrayList<MailContainerWithRecipient>();

			
		for (Share share : listShare) {
			if (mailContainer!=null) { //if notification is needed
				if (share.getReceiver().getUserType().equals(UserType.GROUP)) { //group sharing
					Group group = groupRepository.findByName(share.getReceiver().getLastName());
					notifyGroupSharingDeleted(doc, actor, group, mailContainer);
				}
				else { //user sharing
					
					mailContainerWithRecipient.add(mailBuilder.buildMailSharedFileDeletedWithRecipient(actor, mailContainer, doc, actor, share.getReceiver()));
					
				}
			}
			deleteShare(share, actor);
		}
		
		notifierService.sendAllNotifications(mailContainerWithRecipient);

		
		doc.setShared(false);
		doc.setSharedWithGroup(false);
		
		//2)delete secure url if we need
		
		mailContainerWithRecipient.clear();
		
		List<SecuredUrl> listSecuredUrl = securedUrlRepository.getSecureUrlLinkedToDocument(doc);
		for (SecuredUrl securedUrl : listSecuredUrl) {
			if (mailContainer!=null) { //if notification is needed
				List<Contact> recipients = securedUrl.getRecipients();
								
				for (Contact contact : recipients) {	
					mailContainerWithRecipient.add(mailBuilder.buildMailSharedFileDeletedWithRecipient(actor, mailContainer, doc, actor, contact));
				}
			}
			
			securedUrlRepository.delete(securedUrl);
		}
		notifierService.sendAllNotifications( mailContainerWithRecipient);

	}
	
	
	private void refreshShareAttributeOfDoc(Document doc, Boolean deteteInitialSharedDoc) throws BusinessException {
		List<Share> listShare = shareRepository.getSharesLinkedToDocument(doc);
		List<SecuredUrl> listSecuredUrl = securedUrlRepository.getSecureUrlLinkedToDocument(doc);
		
		if (((listShare==null)||(listShare.size()==0)) && ((listSecuredUrl==null)||(listSecuredUrl.size()==0))) {
			
			if(deteteInitialSharedDoc){
				//we delete the original file if all its shares are outdated.
			
				User owner  = doc.getOwner();
				String fileUUID = doc.getIdentifier();
				String thumbnailUUID = doc.getThmbUUID();
				
				//we log the deletion of this file with FILE_EXPIRE
				User systemUser = userRepository.findByLogin("system");
				FileLogEntry logEntry = new FileLogEntry(systemUser.getMail(), systemUser
							.getFirstName(), systemUser.getLastName(), null,
							LogAction.FILE_EXPIRE, "Deletion of outdated file", doc
									.getName(), doc.getSize(), doc.getType());
				
				owner.deleteDocument(doc);
				userRepository.update(owner);
				
				
				InputStream stream = fileSystemDao.getFileContentByUUID(fileUUID);
				if(stream!=null) {
					fileSystemDao.removeFileByUUID(fileUUID);
					if (thumbnailUUID != null && thumbnailUUID.length() > 0) {
						fileSystemDao.removeFileByUUID(thumbnailUUID);
					}
				}
				
				logEntryService.create(logEntry);
			
			} else {
				doc.setShared(false);
				doc.setSharedWithGroup(false);
			}
		} else { //there is some shares and/or secured url
			if (listShare!=null && listShare.size()>0) { //there is some shares, test if it is with groups or user
				List<Share> listShareToGroup = new ArrayList<Share>(); //shares with groups
				for (Share share : listShare) {
					if (share.getReceiver().getUserType().equals(UserType.GROUP)) {
						listShareToGroup.add(share);
					}
				}

				if (listShareToGroup!=null && listShareToGroup.size()>0) { //there is shares with groups
					int nbSharedNotToGroup = listShare.size()-listShareToGroup.size();
					if (nbSharedNotToGroup < 1) { //there is shares only with groups
						doc.setShared(false);
					} //else: shared with groups and user
				}
				else { //shared but not with group
					doc.setSharedWithGroup(false);
				}
			}
		}
	}

	public void refreshShareAttributeOfDoc(Document doc) {
		try {
			refreshShareAttributeOfDoc(doc,false);
		} catch (BusinessException ex) {
            logger.warn("Unable to refresh share attribute for document: \n" + ex.toString());
        }
	}
	
	
    /** Clean all outdated shares. */
    public void cleanOutdatedShares() {
        User owner = userRepository.findByLogin("system");
        List<Share> shares = shareRepository.getOutdatedShares();
        logger.info(shares.size() + " expired share(s) found to be delete.");
        List<SecuredUrl> securedUrlList = securedUrlRepository.getOutdatedSecuredUrl();
        logger.info(securedUrlList.size() + " expired secured Url(s) found to be delete.");
        
        //read the linshare config (we need to know if we need to delete the original doc which was shared to many people)
        
		for (SecuredUrl securedUrl : securedUrlList) {
			
			try {
				List<Document> docs = securedUrl.getDocuments();
				secureUrlService.delete(securedUrl.getAlea(), securedUrl.getUrlPath());
				
				for (Document document : docs) {
					TimeUnitBooleanValueFunctionality deleteDocWithShareExpiryTimeFunctionality = functionalityService.getDefaultShareExpiryTimeFunctionality(document.getOwner().getDomain());
					refreshShareAttributeOfDoc(document,deleteDocWithShareExpiryTimeFunctionality.isBool());
				}
            } catch (BusinessException ex) {
                logger.warn("Unable to remove a securedUrl : \n" + ex.toString());
            }
		}
        
        for (Share share : shares) {
            try {
                Document sharedDocument = share.getDocument();
				
                TimeUnitBooleanValueFunctionality deleteDocWithShareExpiryTimeFunctionality = functionalityService.getDefaultShareExpiryTimeFunctionality(sharedDocument.getOwner().getDomain());
                deleteShare(share, owner);
                refreshShareAttributeOfDoc(sharedDocument,deleteDocWithShareExpiryTimeFunctionality.isBool());
            } catch (BusinessException ex) {
                logger.warn("Unable to remove a share : \n" + ex.toString());
            }
        }
        
    }
    
    public void notifyUpcomingOutdatedShares() {
		MailContainer mailContainer = new MailContainer("", Language.FRENCH);
        
        for (Integer date : this.datesForNotifyUpcomingOutdatedShares) {
	        List<Share> shares = shareRepository.getUpcomingOutdatedShares(date);
	        logger.info(shares.size() + " upcoming (in "+date.toString()+" days) outdated share(s) found to be notified.");

	        for (Share share : shares) {
	        	if (!share.getDownloaded()) {
	        		sendUpcomingOutdatedShareNotification(mailContainer, share, date);
	        	}
	        }
	        
	        List<SecuredUrl> securedUrlList = securedUrlRepository.getUpcomingOutdatedSecuredUrl(date);
	        logger.info(securedUrlList.size() + " upcoming (in "+date.toString()+" days) outdated secured Url(s) found to be notified.");
	        
			for (SecuredUrl securedUrl : securedUrlList) {
				sendUpcomingOutdatedSecuredUrlNotification(mailContainer, securedUrl, date);
			}
        }
    	
    }
    
    
	private void sendUpcomingOutdatedSecuredUrlNotification(MailContainer mailContainer, 
			SecuredUrl securedUrl, Integer days) {
		
		//compose the secured url to give in mail
		StringBuffer httpUrlBase = new StringBuffer();
		httpUrlBase.append(urlBase);
		if(!urlBase.endsWith("/")) httpUrlBase.append("/");
		httpUrlBase.append(securedUrl.getUrlPath());
		if(!securedUrl.getUrlPath().endsWith("/")) httpUrlBase.append("/");
		httpUrlBase.append(securedUrl.getAlea());
		
		//securedUrl must be ended with a "/" if no parameter (see urlparam)
		String securedUrlBase = httpUrlBase.toString();
		
		List<MailContainerWithRecipient> mailContainerWithRecipient = new ArrayList<MailContainerWithRecipient>();

		try {
		
			for (Contact recipient : securedUrl.getRecipients()) {
				String securedUrlWithParam = securedUrlBase+"?email=" + recipient.getMail();
				
				mailContainerWithRecipient.add(mailBuilder.buildMailUpcomingOutdatedSecuredUrlWithRecipient(securedUrl.getSender(), mailContainer, securedUrl, recipient, days, securedUrlWithParam));
			}
			notifierService.sendAllNotifications( mailContainerWithRecipient);
		
		} catch (BusinessException e) {
			logger.error("Error while trying to notify upcoming outdated secured url", e);
		}

	}
	
	private void sendUpcomingOutdatedShareNotification(MailContainer mailContainer, 
			Share share, Integer days) {
		try {

			notifierService.sendAllNotifications(mailBuilder.buildMailUpcomingOutdatedShareWithOneRecipient(share.getSender(), mailContainer, share, days));
		} catch (BusinessException e) {
				logger.error("Error while trying to notify upcoming outdated share", e);
		}
	}
	
	public SecuredUrl shareDocumentsWithSecuredUrlToUser(
			UserVo owner, List<Document> docList, String password,
			List<Contact> recipients, Calendar expiryDate) throws IllegalArgumentException, BusinessException {

		
		SecuredUrl securedUrl =  null;
		
		User sender = userRepository.findByLogin(owner.getLogin());
		//set the password associated with this secured url in mail
		//can be null for unsecure url
		securedUrl = secureUrlService.create(docList, sender, password, recipients, expiryDate);
	
			
		for (Document doc : docList) {
				
				doc.setShared(true);
				documentRepository.update(doc);
			
				for (Contact oneContact : recipients) {
				
				ShareLogEntry logEntry = new ShareLogEntry(owner.getMail(), owner.getFirstName(), owner.getLastName(),
						owner.getDomainIdentifier(),
		        		LogAction.FILE_SHARE, "Sharing of a file", doc.getName(), doc.getSize(), doc.getType(),
		        		oneContact.getMail(), "", "", "", securedUrl.getExpirationTime());
		       
				logEntryService.create(logEntry);
			}
		}
		
		return securedUrl;
		
	}
	
	public List<SecuredUrl> getSecureUrlLinkedToDocument(Document doc) throws BusinessException {
		 return securedUrlRepository.getSecureUrlLinkedToDocument(doc);
	}
	public List<Share> getSharesLinkedToDocument(Document doc) {
		 return shareRepository.getSharesLinkedToDocument(doc);
	}
	public void logLocalCopyOfDocument(Share share, User user) throws IllegalArgumentException, BusinessException {
		ShareLogEntry logEntryShare = new ShareLogEntry(share.getSender().getMail(),
				share.getSender().getFirstName(), share
						.getSender().getLastName(), share.getSender().getDomainId(),
				LogAction.SHARE_COPY, "Copy of a sharing", share.getDocument()
						.getName(), share.getDocument().getSize(), share.getDocument().getType(), user
						.getMail(), user.getFirstName(), user
						.getLastName(), user.getDomainId(), null);

		ShareLogEntry logEntryDelete = new ShareLogEntry(share.getSender().getMail(),
				share.getSender().getFirstName(), share
						.getSender().getLastName(), share.getSender().getDomainId(),
				LogAction.SHARE_DELETE, "Copy of a sharing", share.getDocument()
						.getName(), share.getDocument().getSize(), share.getDocument().getType(), user
						.getMail(), user.getFirstName(), user
						.getLastName(), user.getDomainId(), null);
		
		logEntryService.create(logEntryShare);
		logEntryService.create(logEntryDelete);
	}
	

	public void notifyGroupSharingDeleted(Document doc, User manager, Group group,
			MailContainer mailContainer) throws BusinessException {

		/**
		 * Send notification ; if a functional mailBox is given, to this mailBox, 
		 * otherwise to each group member.
		 */
		String functionalMail = group.getFunctionalEmail();
		
		
		List<MailContainerWithRecipient> mailContainerWithRecipient = new ArrayList<MailContainerWithRecipient>();
		
		if (functionalMail != null && functionalMail.length() > 0) {
			
			mailContainerWithRecipient.add(mailBuilder.buildMailGroupSharingDeletedWithRecipient(manager, mailContainer, manager, group, doc));
			
		} else {
			
			for (GroupMember member : group.getMembers()) {
				
				mailContainerWithRecipient.add(mailBuilder.buildMailGroupSharingDeletedWithRecipient(manager, mailContainer, manager, member.getUser(), group, doc));
				
			}
		}
		notifierService.sendAllNotifications(manager.getMail(),mailContainerWithRecipient);
	}
	

}
