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

import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.List;

import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.SignatureVo;

import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;

public interface DocumentFacade {
		
	/**
	 * Compute the MimeType from a file input stream 
	 * @param theFileStream
	 * @param theFilePath
	 * @return
	 */
	public String getMimeType(InputStream theFileStream, String theFilePath)  throws BusinessException;
	
	
	/**
	 * Insert a file in the path identifiable by its filename.
	 * @param file the stream content file.
	 * @param fileName the name of the file which permits to identify it.
	 * @param mimeType the mimeType of the file.
	 * @return DocumentVo : the created document
	 * @throws BusinessException  FILE_TOO_LARGE if the file is too large to fit in user's space
	 */
	public DocumentVo insertFile(InputStream file,long size,String fileName,String mimeType, UserVo owner) throws BusinessException;
		
	/**
	 * Retrieve a DocumentVo given the user login and the id of the doc
	 * @param login
	 * @param uuid
	 * @return
	 */
	public DocumentVo getDocument(String login,String uuid);
	
	
	/** 
	 * Delete a Document (or a sharedDocument)
	 * @param actor : the actor that removes the document
	 * @param document : the document to be deleted (or a sharing)
	 * @throws BusinessException
	*/
	public void removeDocument(UserVo actor,DocumentVo document) throws BusinessException;
	
	/**
	 * Retrieve a stream of a Document 
	 * @param doc the documentVo (or SharedDocumentVo)
	 * @param actor the user who is acting
	 * @return inputStream the stream of the document
	 */
	public InputStream retrieveFileStream(DocumentVo doc, UserVo actor) throws BusinessException;
	
	/**
	 * Retrieve a stream of a Document 
	 * @param doc the documentVo (or SharedDocumentVo)
	 * @param actor the user who is acting
	 * @return inputStream the stream of the document
	 */
	public InputStream retrieveFileStream(DocumentVo doc, String actor) throws BusinessException;
	
	/**
	 * insert a signature file in repository
	 * @param file inputstream of the xml signature file
	 * @param size size of the file
	 * @param fileName name of the file
	 * @param mimeType mime type  (application/xml)
	 * @param owner owner of the file (the signer)
	 * @param document which is associated with this signature
	 * @param signerCertificate signer certificate (additional information will be put in database)
	 * @throws BusinessException
	 */
	public void insertSignatureFile(InputStream file,long size,String fileName,String mimeType, UserVo owner, DocumentVo document, X509Certificate signerCertificate) throws  BusinessException;
	
	/**
	 * check if the current user is the signer of the document.
	 * @param currentSigner current UserVo
	 * @param document document to check
	 * @return true if the document is signed by the user
	 */
	public boolean isSignedDocumentByCurrentUser(UserVo currentSigner, DocumentVo document);
	/**
	 * check if the document is already signed by someone.
	 * @param document the doc to check
	 * @return false if no signature at all.
	 */
	public boolean isSignedDocument(DocumentVo document);
	/**
	 * get the list of signatures (co signature)
	 * @param document the document to get the signatures
	 * @return list of signature
	 */
	public List<SignatureVo> getAllSignatures(DocumentVo document);
	/**
	 * get a signature for a given document and a given document
	 * @param currentSigner the signer
	 * @param document
	 * @return
	 */
	public SignatureVo getSignature(UserVo currentSigner,DocumentVo document);
	
	/**
	 * Return the available space for a given user
	 * @param user
	 * @return
	 */
	public Long getUserAvailableQuota(UserVo user);
	
	
	/**
	 * Return the total space for a given user
	 * @param user
	 * @return
	 */
	public Long getUserTotalQuota(UserVo user);
	
	/**
	 * to update document content without change properties
	 * @param currentFileUUID
	 * @param file
	 * @param size
	 * @param fileName
	 * @param mimeType
	 * @param owner
	 * @return
	 * @throws BusinessException
	 */
	public DocumentVo updateDocumentContent(String currentFileUUID, InputStream file, long size, String fileName, String mimeType,UserVo owner) throws BusinessException;
	
	
	public DocumentVo encryptDocument(DocumentVo doc,UserVo user,String password) throws BusinessException;
	public DocumentVo decryptDocument(DocumentVo doc, UserVo user,String password) throws BusinessException;
	public boolean isDocumentEncrypted(DocumentVo doc);
	public InputStream retrieveSignatureFileStream(SignatureVo signaturedoc);
	
    /**
     * Rename a file.
     * @param uuid the uuid that identifies the file.
     * @param newName the new name for the file.
     */
	public void renameFile(String uuid, String newName);
    
    public void  updateFileProperties(String uuid, String newName, String fileComment);
}
