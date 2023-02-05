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
package org.linagora.linshare.core.facade.webservice.user;

import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestEntryDto;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;

public interface UploadRequestFacade {

	UploadRequestDto find(String actorUuid, String uuid) throws BusinessException;

	UploadRequestDto updateStatus(String actorUuid, String uuid, UploadRequestStatus status, boolean copy) throws BusinessException;

	UploadRequestDto delete(String actorUuid, String uuid) throws BusinessException;

	UploadRequestDto delete(String actorUuid, UploadRequestDto uploadRequestDto) throws BusinessException;

	UploadRequestDto update(String actorUuid, UploadRequestDto uploadRequestDto, String uuid) throws BusinessException;

	List<UploadRequestEntryDto> findAllEntries(Integer version, String actorUuid, String uuid);

	Set<AuditLogEntryUser> findAllAudits(String actorUuid, String uuid, List<LogAction> actions, List<AuditLogEntryType> types);
}