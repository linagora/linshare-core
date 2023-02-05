/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.service;

import java.io.File;
import java.util.List;

import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.objects.CopyResource;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.mongo.entities.mto.CopyMto;

import com.google.common.io.ByteSource;

public interface DocumentEntryService {

	DocumentEntry create(Account actor, Account owner, File tempFile, String fileName, String comment,
			boolean isFromCmis, String metadata) throws BusinessException;

	DocumentEntry copy(Account actor, Account owner, CopyResource cr) throws BusinessException;

	void markAsCopied(Account actor, Account owner, DocumentEntry entry, CopyMto copiedTo) throws BusinessException;

	public DocumentEntry update(Account actor, Account owner, String docEntryUuid, File tempFile, String fileName) throws BusinessException ;

	/**
	 * Document suppression due to user action. 
	 * @param actor TODO
	 * @param owner
	 * @param documentUuid
	 * @return DocumentEntry
	 * @throws BusinessException
	 */
	public DocumentEntry delete(Account actor, Account owner, String documentUuid) throws BusinessException;

	/**
	 * This method is designed inconsistent document, the document exists into the database only (no on the file system) 
	 * @param actor 
	 * @param documentEntry
	 * @throws BusinessException
	 */
	public void deleteInconsistentDocumentEntry(SystemAccount actor, DocumentEntry documentEntry) throws BusinessException;


	/**
	 * This method is designed to delete expired documents (batches). 
	 * @param actor 
	 * @param documentEntry
	 * @throws BusinessException
	 */
	public void deleteExpiredDocumentEntry(SystemAccount actor, DocumentEntry documentEntry) throws BusinessException;

	 /**
     * Get the thumbnail (InputStream) of the document
	 * @param actor TODO
	 * @param uuid the identifier of the document
     * @return InputStream of the thumbnail
     */
    public ByteSource getThumbnailByteSource(Account actor, Account owner, String uuid, ThumbnailType kind) throws BusinessException;

    public ByteSource getByteSource(Account actor, Account owner, String uuid) throws BusinessException;

	/**
	 * looking for a document entry using the uuid parameter.
	 * Owner and actor rights will be check. A {@link BusinessException} 
	 * could be raise it the entry is not found or the actor is not allowed
	 * to access to it. 
	 * @param actor
	 * @param owner
	 * @param uuid
	 * @return DocumentEntry
	 * @throws BusinessException
	 */
	public DocumentEntry find(Account actor, Account owner, String uuid) throws BusinessException;

	DocumentEntry findForDownloadOrCopyRight(Account actor, Account owner, String uuid)  throws BusinessException;

	public List<DocumentEntry> findAll(Account actor, Account owner) throws BusinessException;

	public List<DocumentEntry> findAllMySyncEntries(Account actor, Account owner) throws BusinessException;

	public DocumentEntry findMoreRecentByName(Account actor, Account owner, String fileName) throws BusinessException;

	public void renameDocumentEntry(Account actor, Account owner, String docEntryUuid, String newName) throws BusinessException ;

	public boolean mimeTypeFilteringStatus(Account actor) throws BusinessException;

	public DocumentEntry updateFileProperties(Account actor, Account owner, String uuid, String newName, String fileComment, String meta) throws BusinessException;

	void updateFileProperties(Account actor, String docEntryUuid,
			String newName, String fileComment, boolean cmisSync)
			throws BusinessException;

	long getRelatedEntriesCount(Account actor, Account owner, DocumentEntry documentEntry);

	void deleteOrComputeExpiryDate(SystemAccount actor,
			AbstractDomain domain, DocumentEntry documentEntry);

	List<String> findAllExpiredEntries(Account actor, Account owner);
}
