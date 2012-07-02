package org.linagora.linshare.core.service;

import java.io.InputStream;

import org.linagora.linshare.core.domain.constants.Reason;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface DocumentEntryService {
	
	/**
	 * Compute the MimeType from a file input stream 
	 * @param theFileStream
	 * @return
	 */
	public String getMimeType(InputStream theFileStream)  throws BusinessException;

	public DocumentEntry createDocumentEntry(Account actor, InputStream stream, Long size, String fileName) throws BusinessException;
	
	public DocumentEntry updateDocumentEntry(Account actor, String docEntryUuid, InputStream stream, Long size, String fileName) throws BusinessException ;
	
	public DocumentEntry duplicateDocumentEntry(Account actor, String docEntryUuid) throws BusinessException;
	
	public void deleteDocumentEntry(Account actor, String docEntryUuid, Reason causeOfDeletion) throws BusinessException;
	
	public long getUserMaxFileSize(Account account) throws BusinessException;
	
	public long getAvailableSize(Account account) throws BusinessException;
	
	public long getTotalSize(Account account) throws BusinessException ;
	
	
	 /**
     * Thumbnail of the document exists ?
     * @param docUuid the identifier of the document
     * @return true if the thumbnail exists, false otherwise
     */
	public boolean documentHasThumbnail(Account owner, String docUuid);
	
	 /**
     * Get the thumbnail (InputStream) of the document
     * @param docEntryUuid the identifier of the document
     * @return InputStream of the thumbnail
     */
    public InputStream getDocumentThumbnailStream(Account owner, String docEntryUuid) throws BusinessException;
    
    public InputStream getDocumentStream(Account owner, String docEntryUuid) throws BusinessException;

	/**
	 * return true if the signature functionality is enabled
	 * @param user
	 * @return
	 */
	public boolean isSignatureActive(Account account);
	/**
	 * return true if the encipherment functionality is enabled
	 * @param user
	 * @return
	 */
	public boolean isEnciphermentActive(Account account);
	
	/**
	 * return true if the global quota functionality is enabled
	 * @param user
	 * @return
	 * @throws BusinessException
	 */
	public boolean isGlobalQuotaActive(Account account) throws BusinessException;

	/**
	 * return true if the user quota functionality is enabled
	 * @param user
	 * @return
	 * @throws BusinessException
	 */
	public boolean isUserQuotaActive(Account account) throws BusinessException;

	/**
	 * return the global quota value
	 * @param user
	 * @return
	 * @throws BusinessException
	 */
	public Long getGlobalQuota(Account account) throws BusinessException;
	
	public DocumentEntry findById(Account actor, String currentDocEntryUuid) throws BusinessException; 
	
	public void renameDocumentEntry(Account actor, String docEntryUuid, String newName) throws BusinessException ;
	
	public void updateFileProperties(Account actor, String docEntryUuid, String newName, String fileComment) throws BusinessException;
	
}
