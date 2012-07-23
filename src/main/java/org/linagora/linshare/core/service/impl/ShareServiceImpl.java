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

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.entities.SecuredUrl;
import org.linagora.linshare.core.domain.entities.Share;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AnonymousShareEntryService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.ShareEntryService;
import org.linagora.linshare.core.service.ShareService;
import org.linagora.linshare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShareServiceImpl implements ShareService{


	private final ShareEntryService shareEntryService;
	
	private final DocumentEntryService documentEntryService;
	
	private final AnonymousShareEntryService anonymousShareEntryService;
	
	private final UserService userService;
	
	private static final Logger logger = LoggerFactory.getLogger(ShareServiceImpl.class);


	public ShareServiceImpl(ShareEntryService shareEntryService, DocumentEntryService documentEntryService, AnonymousShareEntryService anonymousShareEntryService, UserService userService) {
		super();
		this.shareEntryService = shareEntryService;
		this.documentEntryService = documentEntryService;
		this.anonymousShareEntryService = anonymousShareEntryService;
		this.userService = userService;
	}


	@Override
	public void deleteAllShareEntriesWithDocumentEntry(String docEntryUuid, User actor, MailContainer mailContainer) {
		try {
			DocumentEntry entry = documentEntryService.findById(actor, docEntryUuid);
			
			List<String> a = new ArrayList<String>();
			List<String> b = new ArrayList<String>();
			
			for (AnonymousShareEntry anonymousShareEntry : entry.getAnonymousShareEntries()) {
				a.add(anonymousShareEntry.getUuid());
			}
			
			for (ShareEntry shareEntry : entry.getShareEntries()) {
				b.add(shareEntry.getUuid());
			}

			for (String uuid : a) {
				anonymousShareEntryService.deleteShare(uuid, actor, mailContainer);
			}
			
			for (String uuid : b) {
				shareEntryService.deleteShare(uuid, actor, mailContainer);
			}
			
			documentEntryService.deleteDocumentEntry(actor, entry.getUuid());
			
		} catch (BusinessException e) {
			logger.error("can not delete document : " + docEntryUuid);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	

































	private void refreshShareAttributeOfDoc(Document doc, Boolean deteteInitialSharedDoc) throws BusinessException {
//		List<Share> listShare = shareRepository.getSharesLinkedToDocument(doc);
//		List<SecuredUrl> listSecuredUrl = securedUrlRepository.getSecureUrlLinkedToDocument(doc);
//		
//		if (((listShare==null)||(listShare.size()==0)) && ((listSecuredUrl==null)||(listSecuredUrl.size()==0))) {
//			
//			if(deteteInitialSharedDoc){
//				//we delete the original file if all its shares are outdated.
//			
//				User owner  = doc.getOwner();
//				String fileUUID = doc.getUuid();
//				String thumbnailUUID = doc.getThmbUuid();
//				
//				//we log the deletion of this file with FILE_EXPIRE
//				User systemUser = userRepository.findByMail("system");
//				FileLogEntry logEntry = new FileLogEntry(systemUser.getMail(), systemUser
//							.getFirstName(), systemUser.getLastName(), null,
//							LogAction.FILE_EXPIRE, "Deletion of outdated file", doc
//									.getName(), doc.getSize(), doc.getType());
//				
//				owner.deleteDocument(doc);
//				userRepository.update(owner);
//				
//				
//				InputStream stream = fileSystemDao.getFileContentByUUID(fileUUID);
//				if(stream!=null) {
//					fileSystemDao.removeFileByUUID(fileUUID);
//					if (thumbnailUUID != null && thumbnailUUID.length() > 0) {
//						fileSystemDao.removeFileByUUID(thumbnailUUID);
//					}
//				}
//				
//				logEntryService.create(logEntry);
//			
//			} else {
//				doc.setShared(false);
//			}
//		} else {
//			//there is some shares and/or secured url
//			doc.setDeletionDate(null);
//		}
	}

	
    /** Clean all outdated shares. */
	@Override
    public void cleanOutdatedShares() {
//        User owner = userRepository.findByMail("system");
//        List<Share> shares = shareRepository.getOutdatedShares();
//        logger.info(shares.size() + " expired share(s) found to be delete.");
//        List<SecuredUrl> securedUrlList = securedUrlRepository.getOutdatedSecuredUrl();
//        logger.info(securedUrlList.size() + " expired secured Url(s) found to be delete.");
//        
//        //read the linshare config (we need to know if we need to delete the original doc which was shared to many people)
//        
//		for (SecuredUrl securedUrl : securedUrlList) {
//			
//			try {
//				List<Document> docs = securedUrl.getDocuments();
//				secureUrlService.delete(securedUrl.getAlea(), securedUrl.getUrlPath());
//				
//				for (Document document : docs) {
//					TimeUnitBooleanValueFunctionality deleteDocWithShareExpiryTimeFunctionality = functionalityService.getDefaultShareExpiryTimeFunctionality(document.getOwner().getDomain());
//					refreshShareAttributeOfDoc(document,deleteDocWithShareExpiryTimeFunctionality.isBool());
//				}
//            } catch (BusinessException ex) {
//                logger.warn("Unable to remove a securedUrl : \n" + ex.toString());
//            }
//		}
//        
//        for (Share share : shares) {
//            try {
//                Document sharedDocument = share.getDocument();
//				
//                TimeUnitBooleanValueFunctionality deleteDocWithShareExpiryTimeFunctionality = functionalityService.getDefaultShareExpiryTimeFunctionality(sharedDocument.getOwner().getDomain());
//                deleteShare(share, owner);
//                refreshShareAttributeOfDoc(sharedDocument,deleteDocWithShareExpiryTimeFunctionality.isBool());
//            } catch (BusinessException ex) {
//                logger.warn("Unable to remove a share : \n" + ex.toString());
//            }
//        }
//        
    }
    
	
	@Override
    public void notifyUpcomingOutdatedShares() {
//		MailContainer mailContainer = new MailContainer("", Language.FRENCH);
//        
//        for (Integer date : this.datesForNotifyUpcomingOutdatedShares) {
//	        List<Share> shares = shareRepository.getUpcomingOutdatedShares(date);
//	        logger.info(shares.size() + " upcoming (in "+date.toString()+" days) outdated share(s) found to be notified.");
//
//	        for (Share share : shares) {
//	        	if (!share.getDownloaded()) {
//	        		sendUpcomingOutdatedShareNotification(mailContainer, share, date);
//	        	}
//	        }
//	        
//	        List<SecuredUrl> securedUrlList = securedUrlRepository.getUpcomingOutdatedSecuredUrl(date);
//	        logger.info(securedUrlList.size() + " upcoming (in "+date.toString()+" days) outdated secured Url(s) found to be notified.");
//	        
//			for (SecuredUrl securedUrl : securedUrlList) {
//				sendUpcomingOutdatedSecuredUrlNotification(mailContainer, securedUrl, date);
//			}
//        }
    	
    }
    
//    
//	private void sendUpcomingOutdatedSecuredUrlNotification(MailContainer mailContainer, 
//			SecuredUrl securedUrl, Integer days) {
//		
//		//compose the secured url to give in mail
//		StringBuffer httpUrlBase = new StringBuffer();
//		httpUrlBase.append(urlBase);
//		if(!urlBase.endsWith("/")) httpUrlBase.append("/");
//		httpUrlBase.append(securedUrl.getUrlPath());
//		if(!securedUrl.getUrlPath().endsWith("/")) httpUrlBase.append("/");
//		httpUrlBase.append(securedUrl.getSalt());
//		
//		//securedUrl must be ended with a "/" if no parameter (see urlparam)
//		String securedUrlBase = httpUrlBase.toString();
//		
//		List<MailContainerWithRecipient> mailContainerWithRecipient = new ArrayList<MailContainerWithRecipient>();
//
//		try {
//		
//			for (Contact recipient : securedUrl.getRecipients()) {
//				String securedUrlWithParam = securedUrlBase+"?email=" + recipient.getMail();
//				
//				mailContainerWithRecipient.add(mailBuilder.buildMailUpcomingOutdatedSecuredUrlWithRecipient(securedUrl.getSender(), mailContainer, securedUrl, recipient, days, securedUrlWithParam));
//			}
//			notifierService.sendAllNotifications( mailContainerWithRecipient);
//		
//		} catch (BusinessException e) {
//			logger.error("Error while trying to notify upcoming outdated secured url", e);
//		}
//
//	}
	
//	private void sendUpcomingOutdatedShareNotification(MailContainer mailContainer, 
//			Share share, Integer days) {
//		try {
//
//			notifierService.sendAllNotifications(mailBuilder.buildMailUpcomingOutdatedShareWithOneRecipient(share.getSender(), mailContainer, share, days));
//		} catch (BusinessException e) {
//				logger.error("Error while trying to notify upcoming outdated share", e);
//		}
//	}

	
	

	@Override
	public void refreshShareAttributeOfDoc(Document doc) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public List<SecuredUrl> getSecureUrlLinkedToDocument(Document doc) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Share> getSharesLinkedToDocument(Document doc) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
