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
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupMemberDto;
import org.linagora.linshare.core.facade.webservice.user.WorkGroupMemberFacade;
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

public class ThreadMemberFacadeImpl extends UserGenericFacadeImp implements
		WorkGroupMemberFacade {

	private final UserService userService;

	protected final SharedSpaceMemberService ssMemberService;

	protected final SharedSpaceRoleService ssRoleService;

	protected final SharedSpaceNodeService sharedSpaceNodeService;

	public ThreadMemberFacadeImpl(AccountService accountService,
			UserService userService,
			SharedSpaceMemberService ssMemberService,
			SharedSpaceRoleService ssRoleService,
			SharedSpaceNodeService sharedSpaceNodeService) {
		super(accountService);
		this.userService = userService;
		this.ssMemberService = ssMemberService;
		this.ssRoleService = ssRoleService;
		this.sharedSpaceNodeService = sharedSpaceNodeService;
	}
	
	@Override
	public List<WorkGroupMemberDto> findAll(String threadUuid) throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		User authUser = checkAuthentication();
		List<WorkGroupMemberDto> res = Lists.newArrayList();
		for (SharedSpaceMember member : sharedSpaceNodeService.findAllMembers(authUser, authUser, threadUuid, null)) {
			res.add(new WorkGroupMemberDto(member, userService.findByLsUuid(member.getAccount().getUuid())));
		}
		return res;
	}

	@Override
	public WorkGroupMemberDto find(String threadUuid, String userUuid) throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(userUuid, "Missing required user uuid");
		User authUser = checkAuthentication();
		SharedSpaceMember found = ssMemberService.findMemberByAccountUuid(authUser, authUser, userUuid, threadUuid);
		return new WorkGroupMemberDto(found, userService.findByLsUuid(userUuid));
	}

	@Override
	public WorkGroupMemberDto create(String threadUuid, WorkGroupMemberDto workGroupMember) throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notNull(workGroupMember, "WorkGroupMember must be set");
		Validate.notEmpty(workGroupMember.getUserDomainId(), "Missing required domain id");
		Validate.notEmpty(workGroupMember.getUserMail(), "Missing required mail");
		User authUser = checkAuthentication();
		User newMember = userService.findOrCreateUser(workGroupMember.getUserMail(), workGroupMember.getUserDomainId());
		SharedSpaceNode foundSharedSpaceNode = sharedSpaceNodeService.find(authUser, authUser, threadUuid);
		SharedSpaceRole defaultRole = getDefaultRole(authUser, workGroupMember.isAdmin());
		SharedSpaceMember create = ssMemberService.create(authUser, authUser, foundSharedSpaceNode, defaultRole,
				new SharedSpaceAccount(newMember));
		return new WorkGroupMemberDto(create, newMember);
	}

	@Override
	public WorkGroupMemberDto update(String threadUuid, WorkGroupMemberDto threadMember) throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notNull(threadMember, "Missing required thread member");
		User authUser = checkAuthentication();
		SharedSpaceRole defaultRole = getDefaultRole(authUser, threadMember.isAdmin());
		User user = userService.findByLsUuid(threadMember.getUserUuid());
		SharedSpaceMember ssMemberToUpdate = ssMemberService.findMemberByAccountUuid(authUser, authUser,
				threadMember.getUserUuid(), threadUuid);
		ssMemberToUpdate.setRole(new LightSharedSpaceRole(defaultRole));
		SharedSpaceMember updated = ssMemberService.update(authUser, authUser, ssMemberToUpdate);
		return new WorkGroupMemberDto(updated, user);
	}

	@Override
	public WorkGroupMemberDto delete(String threadUuid, String userUuid) throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(userUuid, "Missing required user uuid");
		User authUser = checkAuthentication();
		User user = userService.findByLsUuid(userUuid);
		SharedSpaceNode nodeOfMemberToDelete = new SharedSpaceNode();
		nodeOfMemberToDelete.setUuid(threadUuid);
		SharedSpaceMember ssMemberToDelete = ssMemberService.findMemberByAccountUuid(authUser, authUser, userUuid, threadUuid);
		SharedSpaceMember deleted = ssMemberService.delete(authUser, authUser, ssMemberToDelete.getUuid());
		return new WorkGroupMemberDto(deleted, user);
	}

	private SharedSpaceRole getDefaultRole(User authUser, boolean admin) {
		if (admin) {
			return ssRoleService.findByName(authUser, authUser, "ADMIN");
		}
		return ssRoleService.findByName(authUser, authUser, "READER");
	}
}
