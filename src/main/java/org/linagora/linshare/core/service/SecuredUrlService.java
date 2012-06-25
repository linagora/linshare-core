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
package org.linagora.linshare.core.service;

import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.SecuredUrl;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.LinShareNotSuchElementException;

/**
 * SecuredUrl service to check create and delete securedUrl.
 * 
 */
public interface SecuredUrlService {

	/**
	 * Create a secure url with a set of documents
	 * 
	 * @param documents
	 *            a List of documents
	 * @param sender
	 *            the user that creates the secured url
	 * @param password
	 *            the password (can be null)
	 * @return the create secured URL
	 */
	SecuredUrl create(List<Document> documents, User sender, String password, List<Contact> recipients, Calendar expirationDate);
	
	/**
	 * Create a secure url with a set of documents
	 * @param documents a List of documents
	 * @param sender the user that creates the secured url
	 * @param password the password (can be null)
	 * @param urlPath the base url
	 * @return the create secured URL
	 */
	SecuredUrl create(List<Document> documents, User sender, String password,String urlPath,List<Contact> recipients, Calendar expirationDate);

	/**
	 * Verify that the given URL is a valid secured URL for the share id
	 * 
	 * @param alea
	 *            a share identifier
	 * @param urlPath
	 *            the URL
	 * @return true if the URL is valid
	 */
	boolean isValid(String alea, String urlPath);

	/**
	 * Verify that the given URL is a valid secured URL for the share id with a
	 * given password
	 * 
	 * @param alea
	 *            a share identifier
	 * @param urlPath
	 *            the URL
	 * @param password
	 *            a given password (can be null)
	 * @return true if the URL is valid
	 */
	boolean isValid(String alea, String urlPath, String password);

	/**
	 * Get the document list bound to the secured url
	 * 
	 * @param alea
	 *            a share identifier
	 * @param urlPath
	 *            the URL
	 * @return a list of documents
	 * @throws BusinessException
	 */
	List<Document> getDocuments(String alea, String urlPath)
			throws BusinessException;

	/**
	 * Get the document list bound to the secured url
	 * 
	 * @param alea
	 *            a share identifier
	 * @param urlPath
	 *            the URL
	 * @param password
	 *            a given password (can be null)
	 * @return a list of documents
	 * @throws BusinessException
	 */
	List<Document> getDocuments(String alea, String urlPath, String password)
			throws BusinessException;

	/**
	 * Get a document
	 * 
	 * @param alea
	 *            a share identifier
	 * @param urlPath
	 *            the url
	 * @param documentId
	 *            a document identifier
	 * @return a document
	 */
	Document getDocument(String alea, String urlPath, Integer documentId)
			throws BusinessException;

	/**
	 * Get a document
	 * 
	 * @param alea
	 *            a share identifier
	 * @param urlPath
	 *            the url
	 * @param password
	 *            a given password (can be null)
	 * @param documentId
	 *            a document identifier
	 * @return a document
	 */
	Document getDocument(String alea, String urlPath, String password,
			Integer documentId) throws BusinessException;

	/**
	 * Delete the secured url matching path. If no secured url matches path,
	 * does nothing
	 * 
	 * @param alea
	 *            a share identifier
	 * @param urlPath
	 *            the url
	 */
	void delete(String alea, String urlPath);
	
	
	/**
	 * Verify if the secured url is password protected
	 * @param alea a share identifier
	 * @param urlPath the url
	 * @return true if the secured url is password protected
	 * @throws LinShareNotSuchElementException 
	 */
	boolean isProtectedByPassword(String alea, String urlPath) throws LinShareNotSuchElementException;

	/**
	 * Remove all outdated secured url.
	 */
	void removeOutdatedSecuredUrl();

	/**
	 * Check that the given URL exists
	 * @param alea
	 *            a share identifier
	 * @param urlPath
	 *            the URL
	 * @return true if the URL exists
	 */
	boolean exists(String alea, String urlPath);
	
	/**
	 * Add an entry in the history, stating the anonymous download of a file
	 * @param alea a share identifier
	 * @param urlPath the URL
	 * @param password the password, if any
	 * @param documentId the documentId, if any
	 */
	public void logDownloadedDocument(String alea, String urlPath, String password,
			Integer documentId, String email); 
	
	/**
	 * Fetch the Owner of the secured url
	 * @param alea
	 * @param urlPath
	 * @return
	 */
	public User getSecuredUrlOwner(String alea, String urlPath);

	
	/**
	 * Retrieve all the sharings of a file by a user (email)
	 * @param sender the user
	 * @param document
	 * @return
	 */
	public List<SecuredUrl> getUrlsByMailAndFile(User sender, DocumentVo document);
	
}
