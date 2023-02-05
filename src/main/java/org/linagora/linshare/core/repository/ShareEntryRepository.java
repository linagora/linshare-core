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

import java.util.List;

import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;

public interface ShareEntryRepository extends AbstractRepository<ShareEntry> {

	final static int BEGIN = 0;

	final static int END = 1;

	final static int ANYWHERE = 2;

	/**
	 * Find a ShareEntry using its uuid.
	 * 
	 * @param uuid
	 * @return found document (null if no ShareEntry found).
	 */
	ShareEntry findByUuid(String uuid);

	ShareEntry getShareEntry(DocumentEntry documentEntry, User sender,
			User recipient);

	List<ShareEntry> findAllMyRecievedShareEntries(User owner);

	List<String> findAllExpiredEntries();

	List<String> findAllSharesExpirationWithoutDownloadEntries(int daysLeftExpiration);

	List<String> findUpcomingExpiredEntries(Integer date);

	List<ShareEntry> findAllMyShareEntries(User owner, DocumentEntry entry);
}
