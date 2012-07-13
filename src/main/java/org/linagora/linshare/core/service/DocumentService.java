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

import java.io.InputStream;
import java.security.cert.X509Certificate;

import org.linagora.linshare.core.domain.constants.Reason;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.domain.vo.SignatureVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;


/**
 * Service to deal with the documents
 * 
 * @author ncharles
 *
 */
public interface DocumentService {

	
	/**
	 * update file content (put new file in jackrabbit and change uuid of the file in database)
	 * usefull method to replace file content (it is used for example by encrypted file or update action from ihm)
	 * @param currentFileUUID file we want to update the content
	 * @param file inputstream of the content to put in jackrabbit
	 * @param size of the data
	 * @param fileName the name of the file which permits to identify it.
	 * @param mimeType the mimeType of the file.
	 * @param boolean encrypted if the file is encrypted
	 * @param owner : the user who uploads the document
	 * @return
	 * @throws BusinessException
	 */
	public Document updateFileContent(String currentFileUUID, InputStream file, long size,
			String fileName, String mimeType, boolean encrypted, User owner) throws BusinessException;
	
	
	

	/**
	 * Find a doc by its uuid
	 * @param uuid
	 * @return
	 */
	public Document getDocument(String uuid);
	
	
	/**
	 * Return a file stream by its uuid and actor
	 * if the DocumentVo isn't a share, we enforce that actor is the document owner
	 * @param doc the DocumentVo we want to download, or a SharedDocumentVo 
	 * @param actor the actor of the action
	 * @return the stream
	 * @throws BusinessException 
	 */
	public InputStream retrieveFileStream(DocumentVo doc, UserVo actor) throws BusinessException;
	
	
	/**
	 * Retrieve a stream of a Document 
	 * @param doc the documentVo (or SharedDocumentVo)
	 * @param actor the user who is acting
	 * @return inputStream the stream of the document
	 */
	public InputStream retrieveFileStream(DocumentVo doc, String actor);
	
	public void insertSignatureFile(InputStream file, long size,
			String fileName, String mimeType, User owner, Document aDoc, X509Certificate signerCertificate) throws  BusinessException;
	
    /** Duplicate a document.
     * @param document the document to duplicate.
     * @param user the owner of the duplicate.
     * @return the created document.
     * @throws BusinessException if document is too large for user account or forbidden mime type.
     */
    public Document duplicateDocument(Document document, User user) throws BusinessException;


    /** Rename a file.
     * @param uuid the uuid that identifies the file.
     * @param newName the new name for the file.
     */
    public void renameFile(String uuid, String newName);
    /** Rename a file if needed and set a comment.
     * if newName is null it will be ignored in the updating process
     * if comment is null the comment is deleted
     * @param uuid the uuid that identifies the file.
     * @param newName the new name for the file.
     * @param fileComment comment on the file
     */
    public void updateFileProperties(String uuid, String newName, String fileComment);
    
    /**
     * Get the thumbnail (InputStream) of the document
     * @param uuid the identifier of the document
     * @return InputStream of the thumbnail
     */
    public InputStream getDocumentThumbnail(String uuid);

    /**
     * Thumbnail of the document exists ?
     * @param uuid the identifier of the document
     * @return true if the thumbnail exists, false otherwise
     */
	public boolean documentHasThumbnail(String uuid);
	
	/**
	 * return true if the signature functionality is enabled
	 * @param user
	 * @return
	 */
	public boolean isSignatureActive(User user);
	/**
	 * return true if the encipherment functionality is enabled
	 * @param user
	 * @return
	 */
	public boolean isEnciphermentActive(User user);
	
	/**
	 * return true if the global quota functionality is enabled
	 * @param user
	 * @return
	 * @throws BusinessException
	 */
	public boolean isGlobalQuotaActive(User user) throws BusinessException;

	/**
	 * return true if the user quota functionality is enabled
	 * @param user
	 * @return
	 * @throws BusinessException
	 */
	public boolean isUserQuotaActive(User user) throws BusinessException;

	/**
	 * return the global quota value
	 * @param user
	 * @return
	 * @throws BusinessException
	 */
	public Long getGlobalQuota(User user) throws BusinessException;
}
