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

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.objects.UploadRequestContainer;
import org.linagora.linshare.core.exception.BusinessException;

public interface UploadRequestService {

	List<UploadRequest> findAll(Account actor, Account owner, UploadRequestGroup uploadRequestGroup, List<UploadRequestStatus> statusList);

	UploadRequest find(Account actor, Account owner, String uuid) throws BusinessException;

	UploadRequest updateRequest(Account actor, Account owner, UploadRequest req)
			throws BusinessException;

	UploadRequest updateStatus(Account actor, Account owner, String uuid, UploadRequestStatus status, boolean copy)
			throws BusinessException;

	UploadRequest update(Account actor, Account owner, String uuid, UploadRequest object, boolean fromGroup)
			throws BusinessException;

	UploadRequest closeRequestByRecipient(UploadRequestUrl url) throws BusinessException;

	UploadRequest deleteRequest(Account actor, Account owner, String uuid) throws BusinessException;

	Set<UploadRequest> findAll(Account actor, List<UploadRequestStatus> status, Date afterDate, Date beforeDate)
			throws BusinessException;

	List<String> findOutdatedRequests(Account actor);

	List<String> findUnabledRequests(Account actor);

	List<String> findAllRequestsToBeNotified(Account actor);

	UploadRequestContainer create(Account authUser, Account owner, UploadRequest uploadRequest,
			UploadRequestContainer container);

	List<UploadRequestEntry> findAllEntries(Account authUser, Account actor, UploadRequest uploadRequestUuid) throws BusinessException;

	List<UploadRequestEntry> findAllExtEntries(UploadRequestUrl requestUrl);

	Integer countNbrUploadedFiles(UploadRequest uploadRequest);

	Long computeEntriesSize(UploadRequest request);

	List<UploadRequest> findUploadRequestsToUpdate(Account authUser, Account actor,
			UploadRequestGroup uploadRequestGroup, List<UploadRequestStatus> listAllowedStatusToUpdate);
}
