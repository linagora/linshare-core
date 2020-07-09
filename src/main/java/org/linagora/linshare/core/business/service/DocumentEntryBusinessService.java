/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
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
import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AbstractDocumentBusinessService;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupNode;

import com.google.common.io.ByteSource;

public interface DocumentEntryBusinessService extends AbstractDocumentBusinessService {

	DocumentEntry createDocumentEntry(Account owner, File myFile, Long size, String fileName, String comment,
			Boolean checkIfIsCiphered, String timeStampingUrl, String mimeType, Calendar expirationDate,
			boolean isFromCmis, String metadata) throws BusinessException;

	Document findDocument(String documentUuid);

	DocumentEntry copy(Account owner, String documentUuid, String fileName, String comment, String metadata,
			Calendar expirationDate, boolean ciphered) throws BusinessException;

	public DocumentEntry updateDocumentEntry(Account owner, DocumentEntry docEntry, File myFile, Long size, String fileName, Boolean checkIfIsCiphered, String timeStampingUrl, String mimeType, Calendar expirationDate) throws BusinessException ;

	public void deleteDocumentEntry(DocumentEntry documentEntry) throws BusinessException ;

	ByteSource getThumbnailByteSource(DocumentEntry entry, ThumbnailType kind) ;

	ByteSource getByteSource(DocumentEntry entry) ;

	public DocumentEntry find(String uuid);

	public List<DocumentEntry> findAllMyDocumentEntries(Account owner);

	public DocumentEntry renameDocumentEntry(DocumentEntry entry, String newName) throws BusinessException;

	public DocumentEntry updateFileProperties(DocumentEntry entry, String newName, String fileComment, String meta) throws BusinessException;

	public long getRelatedEntriesCount(DocumentEntry documentEntry);

	WorkGroupDocument copy(Account actor, WorkGroup toWorkGroup, WorkGroupNode nodeParent,
			String documentUuid, String name, boolean ciphered)
			throws BusinessException;

	ByteSource getByteSource(WorkGroupDocument entry);

	ByteSource getThumbnailByteSource(WorkGroupDocument entry, ThumbnailType kind);

	@Deprecated
	long getUsedSpace(Account owner) throws BusinessException;

	public void update(DocumentEntry docEntry) throws BusinessException;

	DocumentEntry findMoreRecentByName(Account owner, String fileName) throws BusinessException;

	public void syncUniqueDocument(Account owner, String fileName) throws BusinessException;

	List<DocumentEntry> findAllMySyncEntries(Account owner) throws BusinessException;

	List<String> findAllExpiredEntries();

	void deleteDocument(Document document)
			throws BusinessException;

	void updateThumbnail(Document document, Account account) ;

	DocumentEntry copy(Account owner, UploadRequestEntry uploadRequestEntry);
}
