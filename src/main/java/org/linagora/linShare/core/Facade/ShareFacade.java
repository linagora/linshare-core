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
package org.linagora.linShare.core.Facade;

import java.util.Calendar;
import java.util.List;

import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.objects.SuccessesAndFailsItems;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.GroupVo;
import org.linagora.linShare.core.domain.vo.ShareDocumentVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;

public interface ShareFacade {

	
	/**
	 * Create a whole set of sharing
	 * The expiration date will be defined in the config
	 * @param owner : the document owner
	 * @param documents : the list of documents to be shared
	 * @param recipients : the recipients list
	 * @throws BusinessException if a recipient cannot be found in the db nor in the ldap 
	 */
	public SuccessesAndFailsItems<ShareDocumentVo> createSharing(UserVo owner, List<DocumentVo> documents, List<UserVo> recipients, String comment, Calendar expiryDate) throws BusinessException;
	
	
	/**
	 * Create a whole set of shared documents
	 * The expiration date will be defined in the config
	 * Send the email only to the recipients who really received an email
	 * @param owner : the document owner
	 * @param documents : the list of documents to be shared
	 * @param recipients : the recipients list
	 * @param mailContainer : the information to build notifications
	 * @param expirationDate : the expiration date selected by user
	 * @param docsEncrypted : if there is encrypted documents
	 * @param jwsEncryptUrlString : the url for jws service
	 * @return SuccessesAndFailsItems<SharedDocumentVo> : the list of sharing that succedded and failed
	 * @throws BusinessException if a recipient cannot be found in the db nor in the ldap 
	 */
	public SuccessesAndFailsItems<ShareDocumentVo> createSharingWithMail(UserVo owner, List<DocumentVo> documents, 
			List<UserVo> recipients, MailContainer mailContainer,
			Calendar expirationDate, boolean docsEncrypted, String jwsEncryptUrlString) 
			throws BusinessException;

	
	
	/**
	 * same function as createSharingWithMail() BUT we give Recipients Emails which can be found or NOT FOUND in database.
	 * @param owner
	 * @param documents
	 * @param recipientsEmail
	 * @param secureSharing
	 * @param mailContainer
	 * @return
	 * @throws BusinessException
	 */
	public SuccessesAndFailsItems<ShareDocumentVo> createSharingWithMailUsingRecipientsEmail(
			UserVo owner, List<DocumentVo> documents,
			List<String> recipientsEmail,
			boolean secureSharing, MailContainer mailContainer)
			throws BusinessException;

	/**
	 * same function as createSharingWithMailUsingRecipientsEmail() BUT we give the expiration date selected by the user
	 * @param owner
	 * @param documents
	 * @param recipientsEmail
	 * @param secureSharing
	 * @param mailContainer
     * @param expiryDateSelected
	 * @return
	 * @throws BusinessException
	 */
	public SuccessesAndFailsItems<ShareDocumentVo> createSharingWithMailUsingRecipientsEmailAndExpiryDate(
			UserVo owner, List<DocumentVo> documents,
			List<String> recipientsEmail,
			boolean secureSharing, MailContainer mailContainer,
			Calendar expiryDateSelected)
			throws BusinessException;
	
	/**
	 * Retrieve all the sharing received by a user
	 * @param recipient the user
	 * @return
	 */
	public List<ShareDocumentVo> getAllSharingReceivedByUser(UserVo recipient);
	
	
	/**
	 * Retrieve all the sharings of a file by a user
	 * @param sender the user
	 * @param document
	 * @return
	 */
	public List<ShareDocumentVo> getSharingsByUserAndFile(UserVo sender, DocumentVo document);
	
	
	/**
	 * Delete a sharing
	 * @param share
	 * @param actor
	 * @throws BusinessException 
	 */
	void deleteSharing(ShareDocumentVo share, UserVo actor) throws BusinessException;
	
	
    /** Create a local copy of a shared document.
     * @param shareDocumentVo shared document.
     * @param userVo user that the document belongs to.
     * @return the DocumentVo corresponding to the local copy
     * @throws BusinessException if document is too large for user account or forbidden mime type.
     */
    public DocumentVo createLocalCopy(ShareDocumentVo shareDocumentVo, UserVo userVo) throws BusinessException;
    
    
    
    /**
     * Send a notification to the owner of the shared document which has been downloaded
     * 
     * @param sharedDocument
     * @param currentUser
     * @param mailContainer
     */
    public void sendDownloadNotification(ShareDocumentVo sharedDocument, UserVo currentUser, MailContainer mailContainer) throws BusinessException;
    
    
    /**
     * send a mail notification to all users which have received a given shared document which has been updated
     * @param currentDoc current document with an updated content
     * @param currentUser current user which does the update action
     * @param fileSizeTxt friendly size of the file
     * @param oldFileName old file name of the updated doc
     * @param mailContainer the informations to build the notification
     * @throws BusinessException
     */
    public void sendSharedUpdateDocNotification(DocumentVo currentDoc, UserVo currentUser, String fileSizeTxt,String oldFileName, MailContainer mailContainer) throws BusinessException;
    
	/**
	 * Share some documents with some groups.
	 * 
	 * @param owner the user sharing the documents
	 * @param documents the documents to share
	 * @param recipients the recipient groups
	 * @param mailContainer the informations to build the notification
	 * @throws BusinessException
	 */
	public SuccessesAndFailsItems<ShareDocumentVo> createSharingWithGroups(UserVo owner, List<DocumentVo> documents, List<GroupVo> recipients, MailContainer mailContainer) throws BusinessException;

	/**
	 * Notify a group that a sharing was deleted
	 * @param shareddoc
	 * @param manager
	 * @param group
	 * @param mailContainer
	 * @throws BusinessException
	 */
	public void notifyGroupSharingDeleted(ShareDocumentVo shareddoc, UserVo manager, GroupVo group, MailContainer mailContainer) throws BusinessException;
}
