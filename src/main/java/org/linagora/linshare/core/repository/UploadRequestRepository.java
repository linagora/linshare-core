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

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;

public interface UploadRequestRepository extends
		AbstractRepository<UploadRequest> {

	/**
	 * Find a uploadRequestEntry using its uuid.
	 * 
	 * @param uuid
	 * @return found uploadRequest (null if no uploadRequestEntry found).
	 */
	UploadRequest findByUuid(String uuid);

	/**
	 * Find uploadRequests using their status.
	 *
	 * @param status
	 * @return found uploadRequests otherwise null.
	 */
	List<UploadRequest> findByStatus(UploadRequestStatus... status);

	/**
	 * Find uploadRequests using their status and their domains.
	 *
	 * @param domains
	 * @param status
	 * @param after based on creation date
	 * @param before based on creation date
	 * @return found uploadRequests otherwise null.
	 */
	List<UploadRequest> findByDomainsAndStatus(List<AbstractDomain> domains, List<UploadRequestStatus> status, Date after, Date before);

	List<String> findOutdatedRequests();

	List<String> findCreatedUploadRequests();

	List<String> findAllRequestsToBeNotified();

	List<String> findAllArchivedOrPurgedOlderThan(Date modificationDate);

	List<UploadRequest> findAll(UploadRequestGroup uploadRequestGroup, List<UploadRequestStatus> statusList);

	Integer countNbrUploadedFiles(UploadRequest uploadRequest);

	Long computeEntriesSize(UploadRequest request);

	List<UploadRequest> findUploadRequestsToUpdate(UploadRequestGroup uploadRequestGroup,
			List<UploadRequestStatus> listAllowedStatusToUpdate);
}
