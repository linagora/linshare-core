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
package org.linagora.linshare.core.facade.webservice.delegation;

import java.io.File;
import java.util.List;

import javax.ws.rs.core.Response;

import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupEntryDto;

public interface WorkGroupEntryFacade extends DelegationGenericFacade {

	WorkGroupEntryDto create(String actorUuid, String threadUuid,
			File file, String fileName, Boolean strict) throws BusinessException;

	WorkGroupEntryDto copy(String actorUuid, String threadUuid, String entryUuid)
			throws BusinessException;

	WorkGroupEntryDto find(String actorUuid, String threadUuid, String uuid)
			throws BusinessException;

	List<WorkGroupEntryDto> findAll(String actorUuid, String threadUuid)
			throws BusinessException;

	WorkGroupEntryDto delete(String actorUuid, String threadUuid, WorkGroupEntryDto threadEntry)
			throws BusinessException;

	WorkGroupEntryDto delete(String actorUuid, String threadUuid, String uuid)
			throws BusinessException;

	Response download(String actorUuid, String threadUuid, String uuid)
			throws BusinessException;

	Response thumbnail(String actorUuid, String threadUuid, String uuid, ThumbnailType kind)
			throws BusinessException;

	WorkGroupEntryDto update(String actoruuid, String threadUuid,
			String threadEntryUuid, WorkGroupEntryDto threadEntryDto)
			throws BusinessException;

}
