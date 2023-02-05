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
package org.linagora.linshare.core.facade.webservice.adminv5;

import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.fields.SharedSpaceField;
import org.linagora.linshare.core.domain.entities.fields.SharedSpaceMemberField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.PatchDto;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;
import org.linagora.linshare.webservice.utils.PageContainer;

public interface SharedSpaceFacade {

	PageContainer<SharedSpaceNodeNested> findAll(String actorUuid, String accountUuid, List<String> domainUuids, SortOrder sortOrder,
			SharedSpaceField sortField, Set<NodeType> nodeTypes, Set<String> sharedSpaceRoles, String name, Integer greaterThanOrEqualTo, Integer lessThanOrEqualTo, Integer pageNumber, Integer pageSize);

	SharedSpaceNode find(String actorUuid, String uuid) throws BusinessException;

	SharedSpaceNode create(String actorUuid, SharedSpaceNode node) throws BusinessException;

	SharedSpaceNode delete(String actorUuid, SharedSpaceNode node, String uuid) throws BusinessException;

	SharedSpaceNode update(String actorUuid, SharedSpaceNode node, String uuid) throws BusinessException;

	SharedSpaceNode updatePartial(String actorUuid, PatchDto patchNode, String uuid) throws BusinessException;

	PageContainer<SharedSpaceMember> members(String actorUuid, String sharedSpaceUuid, String accountUuid, Set<String> roles, String email, String firstName, String lastName, String pattern, String type, SortOrder sortOrder, SharedSpaceMemberField sortField, Integer pageNumber, Integer pageSize) throws BusinessException;

	Set<AuditLogEntryUser> findAllSharedSpaceAudits(String sharedSpaceUuid, List<LogAction> actions, List<AuditLogEntryType> types,
			String beginDate, String endDate, String nodeUuid);
}
