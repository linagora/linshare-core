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
package org.linagora.linshare.core.repository;

import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.utils.DocumentCount;

public interface DocumentEntryRepository extends
		AbstractRepository<DocumentEntry> {

	/**
	 * Find a document using its uuid.
	 *
	 * @param uuid
	 * @return found document (null if no document found).
	 */
	DocumentEntry findById(String uuid);

	List<DocumentEntry> findAllMyDocumentEntries(Account owner);

	DocumentEntry findMoreRecentByName(Account owner, String fileName);

	List<DocumentEntry> findAllMySyncEntries(Account owner);

	long getRelatedEntriesCount(DocumentEntry documentEntry);

	long getRelatedDocumentEntryCount(Document document);

	List<String> findAllExpiredEntries();

	@Deprecated
	long getUsedSpace(Account owner) throws BusinessException;

	void syncUniqueDocument(Account owner, String fileName) throws BusinessException;

	List<DocumentCount> countAndGroupByMimeType(AbstractDomain domain, Calendar bDate, Calendar eDate);

	/**
	 * find unshared documents with a null expiration date due to a legacy bug  
	 * @return List uuid's 
	 * @throws BusinessException
	 */
	List<String> findDocumentsWithNullExpiration(List<AbstractDomain> domains) throws BusinessException;

}
