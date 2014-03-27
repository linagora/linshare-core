/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.facade;

import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.List;

import org.linagora.linshare.core.domain.entities.MimeTypeStatus;
import org.linagora.linshare.core.domain.vo.DisplayableAccountOccupationEntryVo;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.SignatureVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.view.tapestry.beans.AccountOccupationCriteriaBean;

public interface DocumentFacade {
		
	/**
	 * Insert a file in the path identifiable by its filename.
	 * @param file the stream content file.
	 * @param fileName the name of the file which permits to identify it.
	 * @return DocumentVo : the created document
	 * @throws BusinessException  FILE_TOO_LARGE if the file is too large to fit in user's space
	 */
	public DocumentVo insertFile(InputStream file,String fileName, UserVo owner) throws BusinessException;
		
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
	 * @param owner owner of the file (the signer)
	 * @param document which is associated with this signature
	 * @param signerCertificate signer certificate (additional information will be put in database)
	 * @throws BusinessException
	 */
	public void insertSignatureFile(InputStream file,long size,String fileName,UserVo owner, DocumentVo document, X509Certificate signerCertificate) throws  BusinessException;
	
	/**
	 * check if the current user is the signer of the document.
	 * @param currentSigner current UserVo
	 * @param document document to check
	 * @return true if the document is signed by the user
	 * @throws BusinessException 
	 */
	public boolean isSignedDocumentByCurrentUser(UserVo currentSigner, DocumentVo document) throws BusinessException;
	
	/**
	 * check if the document is already signed by someone.
	 * @param userlogin 
	 * @param document the doc to check
	 * @return false if no signature at all.
	 */
	public boolean isSignedDocument(String userLsUuid, DocumentVo document);
	/**
	 * get the list of signatures (co signature)
	 * @param userVo 
	 * @param document the document to get the signatures
	 * @return list of signature
	 */
	public List<SignatureVo> getAllSignatures(UserVo userVo, DocumentVo document);
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
	public Long getUserAvailableQuota(UserVo user) throws BusinessException;
	
	
	/**
	 * Return the total space for a given user
	 * @param user
	 * @return
	 */
	public Long getUserTotalQuota(UserVo user) throws BusinessException;
	
	/**
	 * Return the total space for a given user
	 * @param user
	 * @return
	 */
	public Long getGlobalQuota(UserVo user) throws BusinessException;
	
	/**
	 * Return in byte the max size for an attachment 
	 * @param user
	 * @return
	 */
	public Long getUserMaxFileSize(UserVo user) throws BusinessException;

	/**
	 * Return the current available size in byte for an upload
	 * depending on user's quota or max file size.
	 * 
	 * @param userVo
	 * @return the available size
	 * @throws BusinessException
	 */
	public Long getUserAvailableSize(UserVo userVo) throws BusinessException;
	
	/**
	 * Return the occupation of accounts statistics
	 * @param criteria
	 * @return
	 */
	public List<DisplayableAccountOccupationEntryVo> getAccountOccupationStat(AccountOccupationCriteriaBean criteria) throws BusinessException;
	
	/**
	 * to update document content without change properties
	 * @param currentFileUUID
	 * @param file
	 * @param size
	 * @param fileName
	 * @param owner
	 * @param friendlySize 
	 * @return
	 * @throws BusinessException
	 */
	public DocumentVo updateDocumentContent(String currentFileUUID, InputStream file, long size, String fileName, UserVo owner, String friendlySize) throws BusinessException;
	
	
	public DocumentVo encryptDocument(DocumentVo doc,UserVo user,String password) throws BusinessException;
	public DocumentVo decryptDocument(DocumentVo doc, UserVo user,String password) throws BusinessException;
	public InputStream retrieveSignatureFileStream(SignatureVo signaturedoc);
	
    /**
     * Rename a file.
     * @param uuid the uuid that identifies the file.
     * @param newName the new name for the file.
     */
	public void renameFile(String userlogin, String docEntryUuid, String newName);
    
    public void  updateFileProperties(String userlogin, String docEntryUuid, String newName, String comment);

    /**
     * Get the thumbnail (InputStream) of the document
     * @param actorUuid : user uuid
     * @param actorUuid the identifier of the document
     * @return InputStream of the thumbnail
     */
    public InputStream getDocumentThumbnail(String actorUuid, String docEntryUuid);
    
    /**
     * Thumbnail of the document exists ?
     * @param actorUuid : user uuid
     * @param uuid the identifier of the document
     * @return true if the thumbnail exists, false otherwise
     */
    public boolean documentHasThumbnail(String actorUuid, String docEntryUuid);
    
    /**
	 * return true if the signature functionality is enabled
	 * @param user
	 * @return
	 */
	public boolean isSignatureActive(UserVo user);
	/**
	 * return true if the encipherment functionality is enabled
	 * @param user
	 * @return
	 */
	public boolean isEnciphermentActive(UserVo user);

	/**
	 * return true if the global quota functionality is enabled
	 * @param user
	 * @return
	 * @throws BusinessException
	 */
	public boolean isGlobalQuotaActive(UserVo user) throws BusinessException;

	/**
	 * return true if the user quota functionality is enabled
	 * @param user
	 * @return
	 * @throws BusinessException
	 */
	public boolean isUserQuotaActive(UserVo user) throws BusinessException;

	/**
	 * return the Mime type status of a document
	 * @param login
	 * @param uuid
	 * @return
	 */
	public MimeTypeStatus getMimeTypeStatus(String login, String uuid);

}
