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

import org.linagora.linShare.core.domain.entities.AbstractDomain;
import org.linagora.linShare.core.domain.entities.Contact;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.entities.SecuredUrl;
import org.linagora.linShare.core.domain.entities.Share;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.objects.SuccessesAndFailsItems;
import org.linagora.linShare.core.exception.BusinessException;

public interface ShareService {
	/**
	 * Remove a sent share for an user.
	 * @param share the sent share to remove.
	 * @param user the user concerned by the share.
	 * @param actor : the actor of the action
	 */
	public void removeSentShareForUser(Share share, User user, User actor) throws BusinessException;

	/**
	 * Remove sent shares for an user.
	 * @param shares the sent shares to remove.
	 * @param user the user concerned by the shares.
	 * @param actor : the actor of the action
	 */
	public void removeSentSharesForUser(List<Share> shares, User user, User actor) throws BusinessException;

	/**
	 * Remove received share for an user.
	 * @param share the received share to remove.
	 * @param user the user concerned by the shares.
	 * @param actor : the actor of the action
	 */
	public void removeReceivedShareForUser(Share share, User user, User actor) throws BusinessException;

	/**
	 * Remove received shares for an user.
	 * @param shares the received shares to remove.
	 * @param user the user concerned by the shares.
	 * @param actor : the actor of the action
	 */
	public void removeReceivedSharesForUser(List<Share> shares, User user, User actor) throws BusinessException;

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
	 * @param documents the document to share.
	 * @param sender the sender of the document.
	 * @param recipients the recipient of the document.
	 */
	public SuccessesAndFailsItems<Share> shareDocumentsToUser(List<Document> documents, User sender, List<User> recipients);
	
	/**
	 * Share a document from an user to other.
	 * @param document the document to share.
	 * @param sender the sender of the document.
	 * @param recipient the recipient of the document.
	 */
	public SuccessesAndFailsItems<Share> shareDocumentsToUser(List<Document> document, User sender, List<User> recipient, Calendar expirationDate);

	/**
	 * Share a document from an user to other with secured Url.
	 * @param sender owner of documents
	 * @param docList list of document to share
	 * @param secured to secure url
	 * @param mailrecipients list of emails
	 * @return the secured url linked to the sharing process of the documents
	 * @throws BusinessException 
	 * @throws IllegalArgumentException 
	 */
	public SecuredUrl shareDocumentsWithSecuredAnonymousUrlToUser(User sender, List<Document> docList, boolean secured, List<Contact> mailrecipients, Calendar expiryDate)
			throws IllegalArgumentException, BusinessException;

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

	public void notifyUpcomingOutdatedShares();

	/**
	 * Check if SecuredAnonymousUrl (SAU) is allowed
	 * @param domain : the current domain
	 * @return 
	 */
	public boolean isSauAllowed(AbstractDomain domain);

	/**
	 * return the default value for SecuredAnonymousUrl (SAU)
	 * @param domain : the current domain
	 * @return
	 */
	public boolean getDefaultSauValue(AbstractDomain domain);
	
	/**
	 * get a share using its unique persistenceId
	 * @param persistenceId
	 * @return
	 */
	public Share getShare(long persistenceId);
	
	/**
	 * This method is desinged to update an existing share entity
	 * @param share
	 */
	public void updateShare(Share share) throws IllegalArgumentException, BusinessException ;
}
