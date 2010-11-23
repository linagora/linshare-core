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
package org.linagora.linShare.core.service;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.linagora.linShare.core.domain.entities.Contact;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.Group;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.entities.SecuredUrl;
import org.linagora.linShare.core.domain.entities.Share;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.objects.SuccessesAndFailsItems;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;

public interface ShareService {
	/**
	 * Retrieve all sent shares for an user (include the documents by share).
	 * @param user the user.
	 * @return list of shares sent by the user.
	 */
	public Set<Share> getSentSharesByUser(User user);
	
	/**
	 * Retrieve all received shares for an user (include the documents by share)
	 * @param user the user.
	 * @return list of shares received by the user.
	 */
	public Set<Share> getReceivedSharesByUser(User user);
	
	/**
	 * Retrieve all sent documents for an user (without share information).
	 * @param user the user.
	 * @return list of sharedDocument sent by the user.
	 */
	public List<Document> getSentDocumentsByUser(User user);
	
	/**
	 * Retrieve all received documents for an user (without share information).
	 * @param user the user.
	 * @return list of sharedDocument received by the user.
	 */
	public List<Document> getReceivedDocumentsByUser(User user);

	/**
	 * Remove a sent share for an user.
	 * @param share the sent share to remove.
	 * @param user the user concerned by the share.
	 * @param actor : the actor of the action
	 */
	public void removeSentShareForUser(Share share,User user, User actor)  throws BusinessException;
	
	/**
	 * Remove sent shares for an user.
	 * @param shares the sent shares to remove.
	 * @param user the user concerned by the shares.
	 * @param actor : the actor of the action
	 */
	public void removeSentSharesForUser(List<Share> shares,User user,User actor)  throws BusinessException;
	
	/**
	 * Remove received share for an user.
	 * @param share the received share to remove.
	 * @param user the user concerned by the shares.
	 * @param actor : the actor of the action
	 */
	public void removeReceivedShareForUser(Share share,User user,User actor) throws BusinessException;
		
	/**
	 * Remove received shares for an user.
	 * @param shares the received shares to remove.
	 * @param user the user concerned by the shares.
	 * @param actor : the actor of the action
	 */
	public void removeReceivedSharesForUser(List<Share> shares,User user,User actor) throws BusinessException;
	
	
	/**
	 * Delete a share 
	 * @param share
	 * @param actor
	 * @throws BusinessException
	 */
	public void deleteShare(Share share, User actor) throws BusinessException;
	
	
	/**
	 * Delete all the shares related to a document (and log the action)
	 * @param share
	 * @param actor
	 * @param mailContainer
	 * @throws BusinessException
	 */
	public void deleteAllSharesWithDocument(Document doc, User actor, MailContainer mailContainer) throws BusinessException;
	
	/**
	 * Share a document from an user to other.
	 * @param document the document to share.
	 * @param sender the sender of the document.
	 * @param recipient the recipient of the document.
	 * @param comment the comment for the share.
	 */
	public SuccessesAndFailsItems<Share> shareDocumentsToUser(List<Document> document,User sender,List<User> recipient,String comment, Calendar expirationDate);
	
	
/**
 * Share a document from an user to other with secured Url.
 * @param owner owner of documents
 * @param docList list of document to share
 * @param password to secure url
 * @param mailrecipients list of emails
 * @return the secured url linked to the sharing process of the documents
 * @throws BusinessException 
 * @throws IllegalArgumentException 
 */
	public SecuredUrl shareDocumentsWithSecuredUrlToUser(UserVo owner, List<Document> docList,String password,List<Contact> mailrecipients, Calendar expiryDate) throws IllegalArgumentException, BusinessException;
	
	
	/**
	 * refresh the attribute share of a document to false if it isn't shared 
	 * @param doc
	 */
	public void refreshShareAttributeOfDoc(Document doc);

    /** Clean all outdated shares. */
    public void cleanOutdatedShares();
    
    
	/**
	 * find secured url linked to the given doc
	 * @param doc
	 * @return
	 * @throws BusinessException
	 */
    public List<SecuredUrl> getSecureUrlLinkedToDocument(Document doc) throws BusinessException;
    
    
    public List<Share> getSharesLinkedToDocument(Document doc);
    
    /**
     * Do the log entries for a local copy
     * @param doc
     * @param share
     */
    public void logLocalCopyOfDocument(Share share, User user) throws IllegalArgumentException, BusinessException;

    /**
     * Notify a group that a shared file was deleted
     */
    public void notifyGroupSharingDeleted(Document doc, User manager, Group group,
			MailContainer mailContainer) throws BusinessException;

	public void notifyUpcomingOutdatedShares();
}
