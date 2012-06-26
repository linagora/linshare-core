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
package org.linagora.linshare.core.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.linagora.linshare.core.dao.FileSystemDao;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.constants.UserType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.FileLogEntry;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.entities.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.entities.SecuredUrl;
import org.linagora.linshare.core.domain.entities.Share;
import org.linagora.linshare.core.domain.entities.ShareLogEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.SuccessesAndFailsItems;
import org.linagora.linshare.core.domain.objects.TimeUnitBooleanValueFunctionality;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.repository.SecuredUrlRepository;
import org.linagora.linshare.core.repository.ShareRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.MailContentBuildingService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.PasswordService;
import org.linagora.linshare.core.service.SecuredUrlService;
import org.linagora.linshare.core.service.ShareExpiryDateService;
import org.linagora.linshare.core.service.ShareService;
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
	private final GuestRepository guestRepository;
	private List<Integer> datesForNotifyUpcomingOutdatedShares;
	private final String urlBase;
	private final FunctionalityService functionalityService;
	private final PasswordService passwordService;
	
	private static final Logger logger = LoggerFactory.getLogger(ShareServiceImpl.class);

	public ShareServiceImpl(final UserRepository<User> userRepository,
			final ShareRepository shareRepository,
			final LogEntryService logEntryService,
			final SecuredUrlRepository securedUrlRepository,
			final DocumentRepository documentRepository, final SecuredUrlService secureUrlService, 
			final FileSystemDao fileSystemDao, final ShareExpiryDateService shareExpiryDateService,
			final NotifierService notifierService, final MailContentBuildingService mailBuilder,
			final GuestRepository guestRepository,
			final String datesForNotifyUpcomingOutdatedShares, final String urlBase,
			final FunctionalityService functionalityService,
			final PasswordService passwordService) {
		
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
        this.guestRepository = guestRepository;
        this.datesForNotifyUpcomingOutdatedShares = new ArrayList<Integer>();
        String[] dates = datesForNotifyUpcomingOutdatedShares.split(",");
        for (String date : dates) {
        	this.datesForNotifyUpcomingOutdatedShares.add(Integer.parseInt(date));
		}
        this.urlBase = urlBase;
        this.functionalityService = functionalityService;
        this.passwordService = passwordService;
	}

	
	@Override
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

	
	@Override
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


	@Override
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


	@Override
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
	
	
	@Override
	public void deleteShare(Share share, User actor) throws BusinessException {
		
		shareRepository.delete(share);
		share.getReceiver().deleteReceivedShare(share);
		share.getSender().deleteShare(share);
		
		ShareLogEntry logEntry = new ShareLogEntry(actor.getMail(), actor.getFirstName(), actor.getLastName(),
				actor.getDomainId(),
        		LogAction.SHARE_DELETE, "Delete a sharing", 
        		share.getDocument().getName(), share.getDocument().getSize(), share.getDocument().getType(),
        		share.getReceiver().getMail(), share.getReceiver().getFirstName(), share.getReceiver().getLastName(), share.getReceiver().getDomainId(), null);
        
        logEntryService.create(logEntry);
        
	}
	
	
	@Override
	public SuccessesAndFailsItems<Share> shareDocumentsToUser(List<Document> documents, User sender, List<User> recipients) {
		return shareDocumentsToUser(documents, sender, recipients,null);
	}


	@Override
	public SuccessesAndFailsItems<Share> shareDocumentsToUser(List<Document> documents, User sender, List<User> recipients,Calendar expiryDate){
		
		SuccessesAndFailsItems<Share> returnItems = new SuccessesAndFailsItems<Share>();
		
		for (User recipient : recipients) {
			
			for (Document document : documents) {
				//Creating a shareDocument
				
				Share failSharing=new Share(sender,recipient, document, document.getFileComment(), new GregorianCalendar(),true,false);
			
				try{

					// If the user have not selected an expiration date, compute default date
					if (expiryDate == null) {
						expiryDate=shareExpiryDateService.computeShareExpiryDate(document, sender);
					}						 					

					
					// We test if the share already exists :
					Share shareEntity;
					Share current_share = shareRepository.getShare(document, sender, recipient);
					if(current_share == null) {
						// if not, we create one
						logger.debug("Creation of a new share between sender " + sender.getMail() + " and recipient " + recipient.getMail());
						Share share=new Share(sender,recipient,document,document.getFileComment(),expiryDate,true,false);
						shareEntity=shareRepository.create(share);
					} else {
						// if it does, we update the expiration date
						logger.debug("The share (" + document.getIdentifier() +") between sender " + sender.getMail() + " and recipient " + recipient.getMail() + " already exists. Just updating expiration date.");
						shareEntity = current_share; 
						shareEntity.setExpirationDate(expiryDate);
						shareRepository.update(shareEntity);
					}

		
		
					recipient.addReceivedShare(shareEntity);
					sender.addShare(shareEntity);
					
					document.setShared(true);
					
					// update guest account expiry date
					if (recipient.getAccountType().equals(UserType.GUEST)) {
						
						// get new guest expiry date
						Calendar guestExpiryDate = Calendar.getInstance();
						TimeUnitValueFunctionality guestFunctionality = functionalityService.getGuestAccountExpiryTimeFunctionality(recipient.getDomain());
				        guestExpiryDate.add(guestFunctionality.toCalendarValue(), guestFunctionality.getValue());
				        
						Guest guest = guestRepository.findByMail(recipient.getLogin());
						guest.setExpirationDate(guestExpiryDate.getTime());
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

	
	@Override
	public void deleteAllSharesWithDocument(Document doc, User actor, MailContainer mailContainer)
			throws BusinessException {
		
		//1)delete normal share
		List<Share> listShare = shareRepository.getSharesLinkedToDocument(doc);
		
		
		List<MailContainerWithRecipient> mailContainerWithRecipient = new ArrayList<MailContainerWithRecipient>();

			
		for (Share share : listShare) {
			if (mailContainer!=null) { //if notification is needed
				mailContainerWithRecipient.add(mailBuilder.buildMailSharedFileDeletedWithRecipient(actor, mailContainer, doc, actor, share.getReceiver()));
			}
			deleteShare(share, actor);
		}
		
		notifierService.sendAllNotifications(mailContainerWithRecipient);

		
		doc.setShared(false);
		
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
				String thumbnailUUID = doc.getThmbUuid();
				
				//we log the deletion of this file with FILE_EXPIRE
				User systemUser = userRepository.findByMail("system");
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
			}
		} else {
			//there is some shares and/or secured url
			doc.setDeletionDate(null);
		}
	}

	
	@Override
	public void refreshShareAttributeOfDoc(Document doc) {
		try {
			refreshShareAttributeOfDoc(doc,false);
		} catch (BusinessException ex) {
            logger.warn("Unable to refresh share attribute for document: \n" + ex.toString());
        }
	}

	
    /** Clean all outdated shares. */
	@Override
    public void cleanOutdatedShares() {
        User owner = userRepository.findByMail("system");
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
    
	
	@Override
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
	
	
	@Override
	public List<SecuredUrl> getSecureUrlLinkedToDocument(Document doc) throws BusinessException {
		 return securedUrlRepository.getSecureUrlLinkedToDocument(doc);
	}
	
	
	@Override
	public List<Share> getSharesLinkedToDocument(Document doc) {
		 return shareRepository.getSharesLinkedToDocument(doc);
	}
	
	@Override
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
	

	/**
	 * Log file sharing action.
	 * @param sender
	 * @param recipients
	 * @param securedUrl
	 * @param doc
	 * @throws BusinessException
	 */
	private void logFileSharing(User sender, List<Contact> recipients, SecuredUrl securedUrl, Document doc) throws BusinessException {
		for (Contact oneContact : recipients) {
			ShareLogEntry logEntry = new ShareLogEntry(sender.getMail(), sender.getFirstName(), sender.getLastName(),sender.getDomainId(), 
				LogAction.FILE_SHARE, "Sharing of a file", doc.getName(), doc.getSize(), doc.getType(), oneContact.getMail(), "", "", "", securedUrl.getExpirationTime());
			logEntryService.create(logEntry);
		}
	}
	
	
	/**
	 * This method create a share using anonymous url and log it.
	 * @param sender
	 * @param docList
	 * @param password
	 * @param recipients
	 * @param expiryDate
	 * @return
	 * @throws IllegalArgumentException
	 * @throws BusinessException
	 */
	private SecuredUrl shareDocumentsWithSecuredAnonymousUrlToUser(User sender, List<Document> docList, String password, List<Contact> recipients, Calendar expiryDate) throws IllegalArgumentException, BusinessException {
		
		// Secured Url creation
		SecuredUrl securedUrl = secureUrlService.create(docList, sender, password, recipients, expiryDate);
			
		for (Document doc : docList) {
			
			// We need to update this field only if it is not already set to true.
			if(!doc.getShared()) {
				doc.setShared(true);
				documentRepository.update(doc);
			}
			// log this action.
			logFileSharing(sender, recipients, securedUrl, doc);
		}

		return securedUrl;
	}

	
	@Override
	public boolean isSauAllowed(AbstractDomain domain) { 
		
		Functionality funcAU = functionalityService.getAnonymousUrlFunctionality(domain);
		// We chekc if Anonymous Url are activated.
		if(funcAU.getActivationPolicy().getStatus()) {
			Functionality funcSAU = functionalityService.getSecuredAnonymousUrlFunctionality(domain);
			return funcSAU.getActivationPolicy().getPolicy().equals(Policies.ALLOWED);
		}
		return false;
	}
	

	public boolean isSauMadatory(AbstractDomain domain) { 
		Functionality func = functionalityService.getSecuredAnonymousUrlFunctionality(domain);
		return func.getActivationPolicy().getPolicy().equals(Policies.MANDATORY);
	}
	
	
	@Override
	public boolean getDefaultSauValue(AbstractDomain domain) {
		Functionality func = functionalityService.getSecuredAnonymousUrlFunctionality(domain);
		return func.getActivationPolicy().getStatus();
	}
	
	
	@Override
	public SecuredUrl shareDocumentsWithSecuredAnonymousUrlToUser(
			User sender, List<Document> docList, boolean secured, List<Contact> recipients, Calendar expiryDate) throws IllegalArgumentException, BusinessException {
		
		// if we need to secure the url, we generate a password, otherwise we will persist a null password. 
		String password = null;
		
		if(isSauMadatory(sender.getDomain())) {
			password = passwordService.generatePassword();
		} else if(isSauAllowed(sender.getDomain())) {
			if (secured) {
				password = passwordService.generatePassword();
			}
		}
		SecuredUrl url= shareDocumentsWithSecuredAnonymousUrlToUser(sender, docList, password, recipients, expiryDate);
		// Nasty fix : we need to store the plain text password for mail notification
		url.setTemporaryPlainTextpassword(password);
		return url;
	}


	@Override
	public Share getShare(long persistenceId) {
		return shareRepository.getShare(persistenceId);
	}


	@Override
	public void updateShare(Share share) throws IllegalArgumentException, BusinessException {
		shareRepository.update(share);
	}
	
	
	
}
