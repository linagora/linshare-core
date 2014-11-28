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
package org.linagora.linshare.core.business.service;

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
import org.linagora.linshare.core.exception.BusinessException;

public interface DocumentEntryBusinessService {

	/**
	 * Compute the MimeType from a file input stream 
	 * @param theFileStream
	 * @return
	 */
	public DocumentEntry createDocumentEntry(Account owner, File myFile, Long size, String fileName, Boolean checkIfIsCiphered, String timeStampingUrl, String mimeType, Calendar expirationDate) throws BusinessException;

	public DocumentEntry updateDocumentEntry(Account owner, DocumentEntry docEntry, File myFile, Long size, String fileName, Boolean checkIfIsCiphered, String timeStampingUrl, String mimeType, Calendar expirationDate) throws BusinessException ;

	public void deleteDocumentEntry(DocumentEntry documentEntry) throws BusinessException ;

	public byte[] getTimeStamp(String fileName, File tempFile, String timeStampingUrl) throws BusinessException;

	public InputStream getDocumentThumbnailStream(DocumentEntry entry) ;

	public InputStream getDocumentStream(DocumentEntry entry) ;

	public DocumentEntry find(String uuid);

	public List<DocumentEntry> findAllMyDocumentEntries(Account owner);

	public void renameDocumentEntry(DocumentEntry entry, String newName) throws BusinessException;

	public DocumentEntry updateFileProperties(DocumentEntry entry, String newName, String fileComment, String meta) throws BusinessException;

	public long getRelatedEntriesCount(DocumentEntry documentEntry);

	public ThreadEntry createThreadEntry(Thread owner, File myFile, Long size, String fileName, Boolean checkIfIsCiphered, String timeStampingUrl, String mimeType) throws BusinessException;

	public ThreadEntry findThreadEntryById(String docEntryUuid);

	public List<ThreadEntry> findAllThreadEntries(Thread owner);

	public List<ThreadEntry> findAllThreadEntriesTaggedWith(Thread owner, String[] names);

	public long countThreadEntries(Thread thread);

	public InputStream getDocumentStream(ThreadEntry entry);

	public InputStream getThreadEntryThumbnailStream(ThreadEntry entry);

	public void deleteThreadEntry(ThreadEntry threadEntry) throws BusinessException;

	public void deleteSetThreadEntry(Set<Entry> setThreadEntry) throws BusinessException;

	public ThreadEntry updateFileProperties(ThreadEntry entry, String fileComment, String metaData) throws BusinessException;

}
