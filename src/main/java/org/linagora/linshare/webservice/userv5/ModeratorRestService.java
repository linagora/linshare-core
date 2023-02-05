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
package org.linagora.linshare.webservice.userv5;

import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.ModeratorDto;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;

public interface ModeratorRestService {

	ModeratorDto create(String guestUuid, ModeratorDto dto);

	ModeratorDto find(String guestUuid, String uuid);

	ModeratorDto update(String guestUuid, String uuid, ModeratorDto dto);

	ModeratorDto delete(String guestUuid, String uuid, ModeratorDto dto);

	List<ModeratorDto> findAllByGuest(String guestUuid, ModeratorRole role, String pattern);

	Set<AuditLogEntryUser> findAllAudits(String guestUuid, String moderatorUuid, List<LogAction> actions, List<AuditLogEntryType> types,
			String beginDate, String endDate);
}
