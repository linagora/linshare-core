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

import java.io.InputStream;
import java.security.cert.X509Certificate;

import org.linagora.linShare.core.domain.constants.Reason;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.SignatureVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;


/**
 * Service to deal with the documents
 * 
 * @author ncharles
 *
 */
public interface DocumentService {

	/**
	 * Compute the MimeType from a file input stream 
	 * @param theFileStream
	 * @param theFilePath
	 * @return
	 */
	public String getMimeType(InputStream theFileStream, String theFilePath)  throws BusinessException;
	

	
	
	/**
	 * Insert a file in the path identifiable by its filename.
	 * @param path the path inside the repository.
	 * @param file the stream content file.
	 * @param fileName the name of the file which permits to identify it.
	 * @param mimeType the mimeType of the file.
	 * @param owner : the user who uploads the document
	 * @return uuid the uuid of the inserted file.
	 * @throws BusinessException : FILE_TOO_LARGE if the file is too large to fit in user's space
	 */
	public Document insertFile(String path,InputStream file,long size,String fileName,String mimeType, User owner) throws BusinessException;
	
	
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
	 * update file / document content BUT also add log, send mail update of shared documents to users 
	 * @see updateFileContent
	 * 
	 * @param currentFileUUID
	 * @param file
	 * @param size
	 * @param fileName
	 * @param mimeType
	 * @param owner
	 * @return
	 * @throws BusinessException
	 */
	public Document updateDocumentContent(String currentFileUUID, InputStream file, long size,
			String fileName, String mimeType, User owner) throws BusinessException;
	
	
	/**
	 * Return in byte the available size in the user quota
	 * the quota is read from tha admin parameter table
	 * @param user : the user we want to check
	 * @return 
	 * 
	 */
	public long getAvailableSize(User user);
	
	/**
	 * Return in byte the user quota
	 * the quota is read from the admin parameter table
	 * @param user : the user we want to check
	 * @return 
	 * 
	 */
	public long getTotalSize(User user);	
		

	/**
	 * Delete a file by its uuid, and delete all the sharing linked to the document
	 * @param login
	 * @param uuid
	 * @param causeOfDeletion
	 * @throws BusinessException
	 */
	public void deleteFile(String login, String uuid, Reason causeOfDeletion) throws BusinessException;

	/**
	 * Delete a file and send a notification for users having a sharing of this file.
	 * @param login
	 * @param identifier
	 * @param causeOfDeletion
	 * @param mailContainer
	 * @throws BusinessException
	 */
	public void deleteFileWithNotification(String login, String identifier, Reason causeOfDeletion,
			MailContainer mailContainer) throws BusinessException;
	
	/**
	 * Find a doc by its uuid
	 * @param uuid
	 * @return
	 */
	public Document getDocument(String uuid);
	
	
	/**
	 * TODO : finish it when the domain will be changed
	 * Return a file stream by its uuid and actor
	 * if the DocumentVo isn't a share, we enforce that actor is the document owner
	 * @param doc the DocumentVo we want to download, or a SharedDocumentVo 
	 * @param actor the actor of the action
	 * @return the stream
	 * @throws BusinessException 
	 */
	public InputStream retrieveFileStream(DocumentVo doc, UserVo actor) throws BusinessException;
	
	
	/**
	 * allow to anyone to download a signature
	 * @param signaturedoc
	 * @return inputstream form jcr
	 */
	public InputStream retrieveSignatureFileStream(SignatureVo signaturedoc);
	
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

}
