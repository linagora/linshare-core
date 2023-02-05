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

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.exception.BusinessException;

public interface ThreadEntryRepository extends AbstractRepository<ThreadEntry> {

	/**
	 * Find a ThreadEntry using its uuid.
	 * 
	 * @param uuid
	 * @return found ThreadEntry (null if no ThreadEntry found).
	 */
	public ThreadEntry findByUuid(String uuid);

	List<ThreadEntry> findAllThreadEntries(WorkGroup owner);

	long count(WorkGroup workGroup);

	List<ThreadEntry> findAllDistinctEntries(WorkGroup workGroup);

	long getUsedSpace(Account account);

	ThreadEntry update(ThreadEntry entity) throws BusinessException, IllegalArgumentException;

	List<String> findAllThreadEntries();

}
