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

import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.dto.WorkgroupMemberAutoCompleteResultDto;
import org.linagora.linshare.core.service.fragment.SharedSpaceMemberFragmentService;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;

public interface SharedSpaceMemberService extends SharedSpaceMemberFragmentService {

	SharedSpaceMember find(Account authUser, Account actor, String uuid) throws BusinessException;

	SharedSpaceMember findMemberByAccountUuid(Account authUser, Account actor, String userUuid, String nodeUuid)
			throws BusinessException;

	List<SharedSpaceMember> findAll(Account authUser, Account actor, String shareSpaceNodeUuid)
			throws BusinessException;

	List<SharedSpaceMember> findByNode(Account authUser, Account actor, String ssnodeUuid);

	List<SharedSpaceNodeNested> findAllSharedSpacesByAccountAndParentForUsers(Account authUser, Account actor, String accountUuid, boolean withRole, String parent, Set<NodeType> types);

	List<SharedSpaceMember> findAllByAccountAndRole(String accountUuid, String roleUuid);

	List<SharedSpaceMember> findAllUserMemberships(Account authUser, Account actor);

	SharedSpaceMember findMemberByNodeAndUuid(Account authUser, Account actor, String nodeUuid, String memberUuid)
			throws BusinessException;

	List<SharedSpaceNodeNested> findAllWorkGroupsInNode(Account authUser, Account actor, String parentUuid,
			String accountUuid);

	SharedSpaceMember create(Account authUser, Account actor, SharedSpaceNode node, SharedSpaceRole role,
			SharedSpaceAccount account) throws BusinessException;

	List<SharedSpaceMember> deleteAllUserMemberships(Account authUser, Account actor, String userUuid);

	SharedSpaceMember update(Account authUser, Account actor, SharedSpaceMember memberToUpdate);

	List<WorkgroupMemberAutoCompleteResultDto> autocompleteOnActiveMembers(Account authUser, Account actor, String nodeUuid, String pattern);

	List<WorkgroupMemberAutoCompleteResultDto> autocompleteOnAssetAuthor(Account authUser, Account actor, String nodeUuid, String pattern);

}