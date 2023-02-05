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

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.PatchDto;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;

public interface SharedSpaceRestService {

	List<SharedSpaceNodeNested> findAll(boolean withRole, String parent) throws BusinessException;

	SharedSpaceNode find(String uuid, boolean withRole, boolean lastUpdater) throws BusinessException;

	SharedSpaceNode create(SharedSpaceNode node) throws BusinessException;

	SharedSpaceNode delete(SharedSpaceNode node, String uuid) throws BusinessException;

	SharedSpaceNode update(SharedSpaceNode node, String uuid) throws BusinessException;
	
	SharedSpaceNode update(PatchDto patchNode, String uuid) throws BusinessException;

	List<SharedSpaceMember> members(String uuid, String accountUuid);

	SharedSpaceMember findMemberByNodeAndUuid(String uuid, String accountUuid) throws BusinessException;

	SharedSpaceMember addMember(SharedSpaceMember member) throws BusinessException;

	SharedSpaceMember deleteMember(SharedSpaceMember member, String memberUuid) throws BusinessException;

	SharedSpaceMember updateMember(SharedSpaceMember member, String memberUuid, boolean force, Boolean propagate) throws BusinessException;

	Set<AuditLogEntryUser> findAllAudits(String sharedSpaceUuid, List<LogAction> actions, List<AuditLogEntryType> types,
			String beginDate, String endDate, String nodeUuid);
}
