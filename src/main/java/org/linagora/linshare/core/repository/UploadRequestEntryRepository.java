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
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.utils.DocumentCount;

public interface UploadRequestEntryRepository extends
		AbstractRepository<UploadRequestEntry> {

	/**
	 * Find a uploadRequestEntry using its uuid.
	 * 
	 * @param uuid
	 * @return found uploadRequestEntry (null if no uploadRequestEntry found).
	 */
	UploadRequestEntry findByUuid(String uuid);

	List<DocumentCount> findByDomainsBetweenTwoDates(AbstractDomain domain, Calendar beginDate, Calendar endDate);

	List<UploadRequestEntry> findAllExtEntries(UploadRequestUrl uploadRequestUrl);

	long getRelatedUploadRequestEntryCount(Document document);

	List<UploadRequestEntry> findAllEntries(UploadRequest uploadRequest);

	List<String> findAllEntriesForArchivedDeletedPurgedUR();

	Boolean exist(UploadRequest uploadRequest, String entryUuid);
}
