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
package org.linagora.linshare.core.facade.webservice.delegation.impl;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupMemberDto;
import org.linagora.linshare.core.facade.webservice.delegation.WorkgroupMemberFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.core.service.SharedSpaceRoleService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.LightSharedSpaceRole;

import com.google.common.collect.Lists;

public class WorkgroupMemberFacadeImpl extends DelegationGenericFacadeImpl
		implements WorkgroupMemberFacade {

	private final UserService userService;

	private final SharedSpaceNodeService ssNodeService;

	private final SharedSpaceRoleService ssRoleService;

	private final SharedSpaceMemberService ssMemberService;

	public WorkgroupMemberFacadeImpl(
			final AccountService accountService,
			final UserService userService,
			final SharedSpaceNodeService ssNodeService,
			final SharedSpaceRoleService ssRoleService,
			final SharedSpaceMemberService ssMemberService) {
		super(accountService, userService);
		this.userService = userService;
		this.ssNodeService = ssNodeService;
		this.ssRoleService = ssRoleService;
		this.ssMemberService = ssMemberService;
	}

	@Override
	public List<WorkGroupMemberDto> findAll(String actorUuid, String threadUuid)
			throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		List<WorkGroupMemberDto> res = Lists.newArrayList();
		for (SharedSpaceMember member : ssNodeService.findAllMembers(authUser, actor, threadUuid, null)) {
			res.add(new WorkGroupMemberDto(member, userService.findByLsUuid(member.getAccount().getUuid())));
		}
		return res;
	}

	@Override
	public WorkGroupMemberDto create(String actorUuid, String threadUuid,
			String domainId, String mail, boolean readonly, boolean admin)
			throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(domainId, "Missing required domain id");
		Validate.notEmpty(mail, "Missing required mail");
		User authActor = checkAuthentication();
		User actor = getActor(actorUuid);
		User user = userService.findOrCreateUser(mail, domainId);
		SharedSpaceNode foundSharedSpaceNode = ssNodeService.find(authActor, actor, threadUuid);
		SharedSpaceRole defaultRole = getDefaultRole(actor, admin);
		SharedSpaceMember create = ssMemberService.create(authActor, actor, foundSharedSpaceNode, defaultRole,
				new SharedSpaceAccount(user));
		return new WorkGroupMemberDto(create, user);
	}

	@Override
	public WorkGroupMemberDto update(String actorUuid, String threadUuid, WorkGroupMemberDto threadMember) {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notNull(threadMember, "Missing required thread member");
		User authActor = checkAuthentication();
		User actor = getActor(actorUuid);
		User user = userService.findByLsUuid(threadMember.getUserUuid());
		SharedSpaceMember ssMemberToUpdate = ssMemberService.findMemberByAccountUuid(authActor, actor,
				threadMember.getUserUuid(), threadUuid);
		SharedSpaceRole defaultRole = getDefaultRole(authActor, threadMember.isAdmin());
		ssMemberToUpdate.setRole(new LightSharedSpaceRole(defaultRole));
		SharedSpaceMember updated = ssMemberService.update(authActor, actor, ssMemberToUpdate);
		return new WorkGroupMemberDto(updated, user);
	}

	@Override
	public WorkGroupMemberDto delete(String actorUuid, String threadUuid, String userUuid)
			throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notEmpty(userUuid, "Missing required user uuid");
		User authActor = checkAuthentication();
		User actor = getActor(actorUuid);
		User user = userService.findByLsUuid(userUuid);
		SharedSpaceNode nodeOfMemberToDelete = new SharedSpaceNode();
		nodeOfMemberToDelete.setUuid(threadUuid);
		SharedSpaceMember ssMemberToDelete = ssMemberService.findMemberByAccountUuid(authActor, actor, userUuid, threadUuid);
		SharedSpaceMember deleted = ssMemberService.delete(authActor, actor, ssMemberToDelete.getUuid());
		return new WorkGroupMemberDto(deleted, user);
	}

	private SharedSpaceRole getDefaultRole(User authUser, boolean admin) {
		if (admin) {
			return ssRoleService.findByName(authUser, authUser, "ADMIN");
		}
		return ssRoleService.findByName(authUser, authUser, "READER");
	}
}
