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
package org.linagora.linshare.webservice.delegationv2;

import java.util.List;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ShareEntryGroupDto;

public interface ShareEntryGroupRestService {

	List<ShareEntryGroupDto> findAll(String actorUuid, boolean full) throws BusinessException;

	ShareEntryGroupDto find(String actorUuid, String uuid, boolean full) throws BusinessException;

	void head(String actorUuid, String uuid) throws BusinessException;

	ShareEntryGroupDto update(String actorUuid, ShareEntryGroupDto shareEntryGroupDto) throws BusinessException;

	ShareEntryGroupDto delete(String actorUuid, String uuid) throws BusinessException;

	ShareEntryGroupDto delete(String actorUuid, ShareEntryGroupDto shareEntryGroupDto) throws BusinessException;
}
