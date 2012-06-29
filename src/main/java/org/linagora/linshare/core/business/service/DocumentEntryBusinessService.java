package org.linagora.linshare.core.business.service;

import java.io.File;
import java.io.InputStream;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.exception.BusinessException;

public interface DocumentEntryBusinessService {

	/**
	 * Compute the MimeType from a file input stream 
	 * @param theFileStream
	 * @return
	 */
	public String getMimeType(InputStream theFileStream)  throws BusinessException;
	
	public DocumentEntry createDocumentEntry(Account owner, File myFile, Long size, String fileName, Boolean checkIfIsCiphered, String timeStampingUrl, String mimeType) throws BusinessException;
	
	public DocumentEntry updateDocumentEntry(Account owner, DocumentEntry docEntry, File myFile, Long size, String fileName, Boolean checkIfIsCiphered, String timeStampingUrl, String mimeType) throws BusinessException ;
	
	public void deleteDocumentEntry(DocumentEntry documentEntry) throws BusinessException ;
	
	public File getFileFromStream(InputStream stream, String fileName);
	
	public byte[] getTimeStamp(String fileName, File tempFile, String timeStampingUrl) throws BusinessException;
	
	public InputStream getDocumentThumbnail(String uuid) ;
	
	public InputStream getDocument(String uuid) ;
	
	public DocumentEntry findById(Long id);
	
	public void renameDocumentEntry(DocumentEntry entry, String newName) throws BusinessException;
	
	public void updateFileProperties(DocumentEntry entry, String newName, String fileComment) throws BusinessException;
	
	public DocumentEntry duplicateDocumentEntry(DocumentEntry originalEntry, Account owner, String timeStampingUrl) throws BusinessException;

}
