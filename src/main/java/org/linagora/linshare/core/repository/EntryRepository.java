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

import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.User;

public interface EntryRepository extends AbstractRepository<Entry> {

	/**
	 * Find a document using its uuid.
	 * 
	 * @param uuid
	 * @return found document (null if no document found).
	 */
	public Entry findById(String uuid);

	public List<Entry> getOutdatedEntry();

	public List<Entry> findAllMyShareEntries(User owner);

	public List<Entry> findAllMyEntries(final User owner);
}
