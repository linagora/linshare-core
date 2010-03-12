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
import org.linagora.linShare.core.domain.LogAction;
import org.linagora.linShare.core.domain.entities.Contact;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.FileLogEntry;
import org.linagora.linShare.core.domain.entities.Parameter;
import org.linagora.linShare.core.domain.entities.SecuredUrl;
import org.linagora.linShare.core.domain.entities.Share;
import org.linagora.linShare.core.domain.entities.ShareLogEntry;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.entities.UserType;
import org.linagora.linShare.core.domain.objects.SuccessesAndFailsItems;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.DocumentRepository;
import org.linagora.linShare.core.repository.LogEntryRepository;
import org.linagora.linShare.core.repository.SecuredUrlRepository;
import org.linagora.linShare.core.repository.ShareRepository;
import org.linagora.linShare.core.repository.UserRepository;
import org.linagora.linShare.core.service.ParameterService;
import org.linagora.linShare.core.service.SecuredUrlService;
import org.linagora.linShare.core.service.ShareExpiryDateService;
import org.linagora.linShare.core.service.ShareService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShareServiceImpl implements ShareService{


	
	private final UserRepository<User> userRepository;
	private final ShareRepository shareRepository;
	private final LogEntryRepository logEntryRepository;
	private final ParameterService parameterService;
	private final SecuredUrlRepository securedUrlRepository;
	private final DocumentRepository documentRepository;
	private final SecuredUrlService secureUrlService;
	private final FileSystemDao fileSystemDao;
	private final ShareExpiryDateService shareExpiryDateService;
	
	
//    private final DocumentService documentService;
	
	private static final Logger logger = LoggerFactory.getLogger(ShareServiceImpl.class);

	public ShareServiceImpl(final UserRepository<User> userRepository,
			final ShareRepository shareRepository,
			final LogEntryRepository logEntryRepository,
			final ParameterService parameterService, final SecuredUrlRepository securedUrlRepository,
			final DocumentRepository documentRepository, final SecuredUrlService secureUrlService, 
			final FileSystemDao fileSystemDao, final ShareExpiryDateService shareExpiryDateService) {
		
		this.userRepository=userRepository;
		this.shareRepository=shareRepository;
		this.logEntryRepository=logEntryRepository;
		this.parameterService = parameterService;
		this.securedUrlRepository=securedUrlRepository;
		this.documentRepository = documentRepository;
        this.secureUrlService = secureUrlService;
        this.fileSystemDao = fileSystemDao;
        this.shareExpiryDateService = shareExpiryDateService;
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
        		LogAction.SHARE_DELETE, "Remove a received sharing", 
        		share.getDocument().getName(), share.getDocument().getSize(), share.getDocument().getType(),
        		user.getMail(), user.getFirstName(), user.getLastName(), null);
        
        logEntryRepository.create(logEntry);
	}

	/**
	 * @see org.linagora.linShare.core.service.ShareService#removeReceivedSharesForUser(List, User)

	 */
	public void removeReceivedSharesForUser(List<Share> shares, User user, User actor) throws BusinessException {
		for(Share currentShare:shares){
			user.deleteReceivedShare(currentShare);
			userRepository.update(user);
			
			ShareLogEntry logEntry = new ShareLogEntry(actor.getMail(), actor.getFirstName(), actor.getLastName(),
	        		LogAction.SHARE_DELETE, "Remove a received sharing", 
	        		currentShare.getDocument().getName(), currentShare.getDocument().getSize(), currentShare.getDocument().getType(),
	        		user.getMail(), user.getFirstName(), user.getLastName(), null);
	        
	        logEntryRepository.create(logEntry);
		}

	}

	/**
	 * @see org.linagora.linShare.core.service.ShareService#removeSentShareForUser(Share, User)

	 */
	public void removeSentShareForUser(Share share, User user, User actor) throws BusinessException {
		user.deleteShare(share);
		userRepository.update(user);
		
		ShareLogEntry logEntry = new ShareLogEntry(actor.getMail(), actor.getFirstName(), actor.getLastName(),
        		LogAction.SHARE_DELETE, "Cancel a sharing", 
        		share.getDocument().getName(), share.getDocument().getSize(), share.getDocument().getType(),
        		user.getMail(), user.getFirstName(), user.getLastName(), null);
        
        logEntryRepository.create(logEntry);
        
	}

	/**
	 * @see org.linagora.linShare.core.service.ShareService#removeSentSharesForUser(List, User)

	 */
	public void removeSentSharesForUser(List<Share> shares, User user, User actor)  throws BusinessException{
		for(Share currentShare:shares){
			user.deleteShare(currentShare);
			userRepository.update(user);
			
			ShareLogEntry logEntry = new ShareLogEntry(actor.getMail(), actor.getFirstName(), actor.getLastName(),
	        		LogAction.SHARE_DELETE, "Cancel a sharing", 
	        		currentShare.getDocument().getName(), currentShare.getDocument().getSize(), currentShare.getDocument().getType(),
	        		user.getMail(), user.getFirstName(), user.getLastName(), null);
	        
	        logEntryRepository.create(logEntry);
	        
		}
	}
	
	
	public void deleteShare(Share share, User actor) throws BusinessException {
		shareRepository.delete(share);
		
		
		ShareLogEntry logEntry = new ShareLogEntry(actor.getMail(), actor.getFirstName(), actor.getLastName(),
        		LogAction.SHARE_DELETE, "Delete a sharing", 
        		share.getDocument().getName(), share.getDocument().getSize(), share.getDocument().getType(),
        		share.getReceiver().getMail(), share.getReceiver().getFirstName(), share.getReceiver().getLastName(), null);
        
        logEntryRepository.create(logEntry);
        
	}
	
	/**
	 * @see org.linagora.linShare.core.service.ShareService#shareDocumentToUser(Document, User, User, String)

	 */
	public SuccessesAndFailsItems<Share> shareDocumentsToUser(List<Document> documents, User sender,
			List<User> recipients,String comment, Calendar expiryDate){
		
		SuccessesAndFailsItems<Share> returnItems = new SuccessesAndFailsItems<Share>();
		
		for (User recipient : recipients) {
			for (Document document : documents) {
				//Creating a shareDocument
				
				Share failSharing=new Share(sender,recipient,document,comment,new GregorianCalendar(),true,false);
			
				try{

					// If the user have not selected an expiration date, compute default date
					if (expiryDate == null) {
						expiryDate=shareExpiryDateService.computeShareExpiryDate(document);
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
		
					userRepository.update(recipient);
					userRepository.update(sender);
		
					ShareLogEntry logEntry = new ShareLogEntry(sender.getMail(), sender.getFirstName(), sender.getLastName(),
				        		LogAction.FILE_SHARE, "Sharing of a file", document.getName(), document.getSize(), document.getType(),
				        		recipient.getMail(), recipient.getFirstName(), recipient.getLastName(), expiryDate);
				       
				    logEntryRepository.create(logEntry);
				        
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
	public void deleteAllSharesWithDocument(Document doc, User actor)
			throws BusinessException {
		
		//1)delete normal share
		List<Share> listShare = shareRepository.getSharesLinkedToDocument(doc);
		
		for (Share share : listShare) {
			deleteShare(share, actor);
		}
		
		doc.setShared(false);
		doc.setSharedWithGroup(false);
		
		//2)delete secure url if we need
		
		List<SecuredUrl> listSecuredUrl = securedUrlRepository.getSecureUrlLinkedToDocument(doc);
		for (SecuredUrl securedUrl : listSecuredUrl) {
			securedUrlRepository.delete(securedUrl);
		}
	}
	
	
	private void refreshShareAttributeOfDoc(Document doc, Boolean deteteInitialSharedDoc) throws BusinessException {
		List<Share> listShare = shareRepository.getSharesLinkedToDocument(doc);
		List<SecuredUrl> listSecuredUrl = securedUrlRepository.getSecureUrlLinkedToDocument(doc);
		
		if (((listShare==null)||(listShare.size()==0)) && ((listSecuredUrl==null)||(listSecuredUrl.size()==0))) {
			
			if(deteteInitialSharedDoc){
				//we delete the original file if all its shares are outdated.
			
				User owner  = doc.getOwner();
				owner.deleteDocument(doc);
				userRepository.update(owner);
				
				
				InputStream stream = fileSystemDao.getFileContentByUUID(doc.getIdentifier());
				if(stream!=null)
				fileSystemDao.removeFileByUUID(doc.getIdentifier());

				FileLogEntry logEntry;
				User systemUser = userRepository.findByLogin("system");
				
				//we log the deletion of this file with FILE_EXPIRE
				logEntry = new FileLogEntry(systemUser.getMail(), systemUser
							.getFirstName(), systemUser.getLastName(),
							LogAction.FILE_EXPIRE, "Deletion of outdated file", doc
									.getName(), doc.getSize(), doc.getType());
				
				logEntryRepository.create(logEntry);
			
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
        Parameter config =  parameterService.loadConfig();
        
		for (SecuredUrl securedUrl : securedUrlList) {
			
			try {
				List<Document> docs = securedUrl.getDocuments();
				secureUrlService.delete(securedUrl.getAlea(), securedUrl.getUrlPath());
				
				for (Document document : docs) {
					 refreshShareAttributeOfDoc(document,config.getDeleteDocWithShareExpiryTime());
				}
            } catch (BusinessException ex) {
                logger.warn("Unable to remove a securedUrl : \n" + ex.toString());
            }
		}
        
        for (Share share : shares) {
            try {
                Document sharedDocument = share.getDocument();
                deleteShare(share, owner);
                refreshShareAttributeOfDoc(sharedDocument,config.getDeleteDocWithShareExpiryTime());
            } catch (BusinessException ex) {
                logger.warn("Unable to remove a share : \n" + ex.toString());
            }
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
		        		LogAction.FILE_SHARE, "Sharing of a file", doc.getName(), doc.getSize(), doc.getType(),
		        		oneContact.getMail(), "", "", securedUrl.getExpirationTime());
		       
				logEntryRepository.create(logEntry);
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
						.getSender().getLastName(),
				LogAction.SHARE_COPY, "Copy of a sharing", share.getDocument()
						.getName(), share.getDocument().getSize(), share.getDocument().getType(), user
						.getMail(), user.getFirstName(), user
						.getLastName(), null);

		ShareLogEntry logEntryDelete = new ShareLogEntry(share.getSender().getMail(),
				share.getSender().getFirstName(), share
						.getSender().getLastName(),
				LogAction.SHARE_DELETE, "Copy of a sharing", share.getDocument()
						.getName(), share.getDocument().getSize(), share.getDocument().getType(), user
						.getMail(), user.getFirstName(), user
						.getLastName(), null);
		
		logEntryRepository.create(logEntryShare);
		logEntryRepository.create(logEntryDelete);
	}
}
