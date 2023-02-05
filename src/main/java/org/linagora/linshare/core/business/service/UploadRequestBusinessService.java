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
package org.linagora.linshare.core.business.service;

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.exception.BusinessException;

public interface UploadRequestBusinessService {

	List<UploadRequest> findAll(UploadRequestGroup uploadRequestGroup, List<UploadRequestStatus> statusList);

	List<UploadRequest> findAll(List<AbstractDomain> domains,
			List<UploadRequestStatus> status, Date afterDate, Date beforeDate);

	UploadRequest findByUuid(String uuid);

	UploadRequest create(UploadRequest req) throws BusinessException;

	UploadRequest update(UploadRequest req) throws BusinessException;

	UploadRequest update(UploadRequest req, UploadRequest object) throws BusinessException;

	void delete(UploadRequest req) throws BusinessException;

	List<String> findOutdatedRequests() throws BusinessException;

	List<String> findUnabledRequests() throws BusinessException;

	List<String> findAllRequestsToBeNotified() throws BusinessException;

	UploadRequest updateStatus(UploadRequest req, UploadRequestStatus status) throws BusinessException;

	Long computeEntriesSize(UploadRequest request);

	Integer countNbrUploadedFiles(UploadRequest uploadRequest);

	List<UploadRequest> findUploadRequestsToUpdate(UploadRequestGroup uploadRequestGroup,
			List<UploadRequestStatus> listAllowedStatusToUpdate);
}
