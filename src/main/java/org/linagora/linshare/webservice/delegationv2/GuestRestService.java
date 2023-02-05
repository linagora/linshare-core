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
import org.linagora.linshare.core.facade.webservice.common.dto.GuestDto;

public interface GuestRestService {

	GuestDto create(String actorUuid, GuestDto guest) throws BusinessException;

	GuestDto get(String actorUuid, String identifier, Boolean isMail, String domain) throws BusinessException;

	void head(String actorUuid, String identifier, Boolean isMail, String domain) throws BusinessException;

	List<GuestDto> getAll(String actorUuid) throws BusinessException;

	GuestDto update(String actorUuid, GuestDto guest, String uuid) throws BusinessException;

	GuestDto delete(String actorUuid, GuestDto guest, String uuid) throws BusinessException;

}
