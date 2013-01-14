package org.linagora.linshare.core.business.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface DocumentEntryBusinessService {

	/**
	 * Compute the MimeType from a file input stream 
	 * @param theFileStream
	 * @return
	 */
	public String getMimeType(BufferedInputStream theFileStream)  throws BusinessException;
	
	public DocumentEntry createDocumentEntry(Account owner, File myFile, Long size, String fileName, Boolean checkIfIsCiphered, String timeStampingUrl, String mimeType, Calendar expirationDate) throws BusinessException;
	
	public DocumentEntry updateDocumentEntry(Account owner, DocumentEntry docEntry, File myFile, Long size, String fileName, Boolean checkIfIsCiphered, String timeStampingUrl, String mimeType, Calendar expirationDate) throws BusinessException ;
	
	public void deleteDocumentEntry(DocumentEntry documentEntry) throws BusinessException ;
	
	public byte[] getTimeStamp(String fileName, File tempFile, String timeStampingUrl) throws BusinessException;
	
	public InputStream getDocumentThumbnailStream(DocumentEntry entry) ;
	
	public InputStream getDocumentStream(DocumentEntry entry) ;
	
	public DocumentEntry findById(String docEntryUuid);
	
	public List<DocumentEntry> findAllMyDocumentEntries(User owner);
	
	public void renameDocumentEntry(DocumentEntry entry, String newName) throws BusinessException;
	
	public void updateFileProperties(DocumentEntry entry, String newName, String fileComment) throws BusinessException;
	
	public DocumentEntry duplicateDocumentEntry(DocumentEntry originalEntry, Account owner, String timeStampingUrl, Calendar expirationDate) throws BusinessException;

	public long getRelatedEntriesCount(DocumentEntry documentEntry);
	
	
	
	public ThreadEntry createThreadEntry(Thread owner, File myFile, Long size, String fileName, Boolean checkIfIsCiphered, String timeStampingUrl, String mimeType) throws BusinessException;
	
	public ThreadEntry findThreadEntryById(String docEntryUuid);
	
	public List<ThreadEntry> findAllThreadEntries(Thread owner);

	public List<ThreadEntry> findAllThreadEntriesTaggedWith(Thread owner, String[] names);

	public InputStream getDocumentStream(ThreadEntry entry);

	public InputStream getThreadEntryThumbnailStream(ThreadEntry entry);

	public void deleteThreadEntry(ThreadEntry threadEntry) throws BusinessException;

	public void deleteSetThreadEntry(Set<Entry> setThreadEntry) throws BusinessException;

	public void updateFileProperties(ThreadEntry entry, String fileComment) throws BusinessException;

}
