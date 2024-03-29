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
package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.SharedSpaceMemberFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.core.service.SharedSpaceRoleService;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberContext;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberDrive;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;

import com.google.common.base.Strings;

public class SharedSpaceMemberFacadeImpl extends GenericFacadeImpl implements SharedSpaceMemberFacade {
	
	private final SharedSpaceMemberService memberService;

	private final SharedSpaceNodeService nodeService;

	private final SharedSpaceRoleService roleService;

	public SharedSpaceMemberFacadeImpl(SharedSpaceMemberService memberService,
			AccountService accountService,
			SharedSpaceNodeService nodeService,
			SharedSpaceRoleService roleService) {
		super(accountService);
		this.memberService = memberService;
		this.nodeService = nodeService;
		this.roleService = roleService;
	}

	public SharedSpaceMember find(String actorUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Missing required uuid");
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		return memberService.find(authUser, actor, uuid);
	}

	@Override
	public SharedSpaceMember create(String actorUuid, SharedSpaceMember member) throws BusinessException {
		Validate.notNull(member, "Shared space member must be set.");
		Validate.notNull(member.getAccount(), "Account must be set.");
		Validate.notNull(member.getRole(), "Role must be set.");
		Validate.notNull(member.getNode(), "Node must be set.");
		Validate.notNull(member.getAccount().getUuid(), "Account uuid must be set.");
		Validate.notNull(member.getRole().getUuid(), "Role uuid must be set.");
		Validate.notNull(member.getNode().getUuid(), "Node uuid must be set.");
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		SharedSpaceNode foundSharedSpaceNode = nodeService.find(authUser, actor, member.getNode().getUuid());
		SharedSpaceRole foundSharedSpaceRole = roleService.find(authUser, actor, member.getRole().getUuid());
		SharedSpaceMemberContext context = new SharedSpaceMemberContext(foundSharedSpaceRole);
		// if current node is a workSpace
		if (NodeType.WORK_SPACE.equals(foundSharedSpaceNode.getNodeType())) {
			// if current member is a workSpace member
			if (NodeType.WORK_SPACE.equals(member.getType())) {
				GenericLightEntity nestedRole = ((SharedSpaceMemberDrive) member).getNestedRole();
				if (nestedRole != null) {
					context.setNestedRole(roleService.find(authUser, actor, nestedRole.getUuid()));
				}
			}
			/* it is not a workSpace member or nested role is null,
			 * but we are in a workSpace. we force default nested role value (default workgroup value)
			 */
			if (context.getNestedRole() == null) {
				context.setNestedRole(roleService.findByName(authUser, actor, "READER"));
			}
		}
		return memberService.create(authUser, actor, foundSharedSpaceNode, context, member.getAccount());
	}

	@Override
	public SharedSpaceMember update(String actorUuid, SharedSpaceMember member, String uuid, boolean force, Boolean propagate) throws BusinessException {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		Validate.notNull(member, "Shared space member must be set.");
		Validate.notNull(member.getAccount().getUuid(), "account UUID of the member must be set.");
		Validate.notNull(member.getNode().getUuid(), "node UUID of the member must be set.");
		Validate.notNull(member.getRole().getUuid(), "role UUID of the member must be set.");
		if (!Strings.isNullOrEmpty(uuid)) {
			member.setUuid(uuid);
		} else {
			Validate.notEmpty(member.getUuid(), "The shared space member uuid to update must be set.");
		}
		return memberService.update(authUser, actor, member, force, propagate);
	}

	@Override
	public SharedSpaceMember delete(String actorUuid, SharedSpaceMember member, String uuid) throws BusinessException {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		if (Strings.isNullOrEmpty(uuid)) {
			Validate.notNull(member, "Missing sharedSpace member");
			Validate.notEmpty(member.getUuid(), "Missing required sharedSpace member uuid");
			uuid = member.getUuid();
		}
		return memberService.delete(authUser, actor, uuid);
	}

	@Override
	public SharedSpaceMember findByNodeAndMemberUuid(String actorUuid, String nodeUuid, String memberUuid) {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		Validate.notEmpty(memberUuid, "The member uuid must be set.");
		Validate.notEmpty(nodeUuid, "The node uuid must be set.");
		return memberService.findMemberByNodeAndUuid(authUser, actor, nodeUuid, memberUuid);
	}

	@Override
	public List<SharedSpaceMember> findAll(String actorUuid) {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		return memberService.findAllUserMemberships(authUser, actor);
	}
}
