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
import java.util.Map;

import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.exception.LinShareNotSuchElementException;

public interface SecuredUrlFacade {
	
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
	List<DocumentVo> getDocuments(String alea, String urlPath)
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
	List<DocumentVo> getDocuments(String alea, String urlPath, String password)
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
	DocumentVo getDocument(String alea, String urlPath, Integer documentId)
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
	DocumentVo getDocument(String alea, String urlPath, String password,
			Integer documentId) throws BusinessException;

	/**
	 * Verify if the secured url is password protected
	 * @param alea a share identifier
	 * @param urlPath the url
	 * @return true if the secured url is password protected
	 * @throws LinShareNotSuchElementException 
	 */
	boolean isPasswordProtected(String alea, String urlPath) throws LinShareNotSuchElementException;
	
	/**
	 * Add an entry in the history, stating the anonymous download of a file
	 * @param alea a share identifier
	 * @param urlPath the URL
	 * @param password the password, if any
	 * @param documentId the documentId, if any
	 * @param email of the user who has downloaded the document
	 */
	void logDownloadedDocument(String alea, String urlPath, String password,
			Integer documentId, String email);

	/**
	 * Send an email notification to the owner of the secured url when the documents are downloaded
	 * @param alea
	 * @param urlPath
	 * @param subject
	 * @param anonymousDownloadTemplateContent
	 * @param anonymousDownloadTemplateContentTxt
	 * @param docs 
	 * @param email of the user who has downloaded the documents
	 */
	void sendEmailNotification(String alea, String urlPath, String subject, String anonymousDownloadTemplateContent,String anonymousDownloadTemplateContentTxt, List<DocumentVo> docs, String email);


	
	/**
	 * Retrieve all the sharing urls of a file by a user (email)
	 * @param sender the user
	 * @param document
	 * @return a list of couples : the mail of the recipient and the expiration of the url
	 */
	public Map<String, Calendar> getSharingsByMailAndFile(UserVo sender, DocumentVo document);
}
