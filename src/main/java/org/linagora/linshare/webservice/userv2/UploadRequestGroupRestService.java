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
package org.linagora.linshare.webservice.userv2;

import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestCreationDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestGroupDto;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.ContactDto;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;

public interface UploadRequestGroupRestService {

	List<UploadRequestGroupDto> findAll(List<UploadRequestStatus> status) throws BusinessException;

	UploadRequestGroupDto find(String uuid) throws BusinessException;

	UploadRequestGroupDto create(UploadRequestCreationDto uploadRequestCreationDto, Boolean collectiveMode);

	UploadRequestGroupDto updateStatus(String uuid, UploadRequestStatus status, boolean copy) throws BusinessException;

	UploadRequestGroupDto addRecipient(String uuid, List<ContactDto> recipientEmail);

	Set<AuditLogEntryUser> findAllAuditsOfGroup(String uuid, boolean all, List<LogAction> actions,
			List<AuditLogEntryType> types);

	Set<AuditLogEntryUser> findAllAuditsForUploadRequest(String groupUuid, String uploadRequestUuid,
			List<LogAction> actions, List<AuditLogEntryType> types) throws BusinessException;

	UploadRequestGroupDto update(String uuid, UploadRequestGroupDto uploadRequestGroupDto, Boolean force);

	List<UploadRequestDto> findAllUploadRequests(String uuid, List<UploadRequestStatus> status);

	Response downloadEntries(String uuid, String requestUuid) throws BusinessException;

}