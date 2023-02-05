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
package org.linagora.linshare.core.facade.webservice.admin.impl;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.ThreadMemberFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupMemberDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.core.service.SharedSpaceRoleService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.LightSharedSpaceRole;

public class ThreadMemberFacadeImpl extends AdminGenericFacadeImpl implements
		ThreadMemberFacade {

	private UserService userService;

	protected final SharedSpaceMemberService ssMemberService;

	protected final SharedSpaceRoleService ssRoleService;

	protected final SharedSpaceNodeService sharedSpaceNodeService;

	public ThreadMemberFacadeImpl(final AccountService accountService,
			final ThreadService threadService, final UserService userService,
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
	public WorkGroupMemberDto create(WorkGroupMemberDto dto) throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(dto, "thread member must be set.");
		Validate.notEmpty(dto.getThreadUuid(), "thread member thread id must be set.");
		Validate.notEmpty(dto.getUserDomainId(), "thread member domain id must be set.");
		Validate.notEmpty(dto.getUserMail(), "thread member mail must be set.");
		User user = (User) accountService.findByLsUuid(dto.getUserUuid());
		if (user == null) {
			user = userService.findOrCreateUser(dto.getUserMail(), dto.getUserDomainId());
			if (user == null) {
				throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND,
						"Cannot find user with mail :" + dto.getUserMail() + " in domain :" + dto.getUserDomainId());
			}
		}
		boolean admin = dto.isAdmin();
		// TODO Retrieve the role from the restService once the front will pass the info
		SharedSpaceRole defaultRole = getDefaultRole(authUser, admin);
		SharedSpaceNode foundSharedSpaceNode = sharedSpaceNodeService.find(authUser, authUser, dto.getThreadUuid());
		SharedSpaceMember created = ssMemberService.create(authUser, authUser, foundSharedSpaceNode, defaultRole, new SharedSpaceAccount(user));
		return new WorkGroupMemberDto(created, user);
	}

	@Override
	public WorkGroupMemberDto update(WorkGroupMemberDto dto) throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(dto, "thread member must be set.");
		Validate.notNull(dto.getThreadUuid(), "thread uuid must be set.");
		Validate.notNull(dto.getUserUuid(), "user uuid must be set.");
		boolean admin = dto.isAdmin();
		SharedSpaceRole defaultRole = getDefaultRole(authUser, admin);
		User user = userService.findByLsUuid(dto.getUserUuid());
		SharedSpaceMember ssMemberToUpdate = ssMemberService.findMemberByAccountUuid(authUser, authUser, dto.getUserUuid(),
				dto.getThreadUuid());
		ssMemberToUpdate.setRole(new LightSharedSpaceRole(defaultRole));
		SharedSpaceMember updated = ssMemberService.update(authUser, authUser, ssMemberToUpdate);
		return new WorkGroupMemberDto(updated, user);
	}

	@Override
	public WorkGroupMemberDto delete(WorkGroupMemberDto dto) throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(dto, "thread member must be set.");
		Validate.notNull(dto.getThreadUuid(), "thread uuid must be set.");
		Validate.notNull(dto.getUserUuid(), "user uuid must be set.");
		User user = userService.findByLsUuid(dto.getUserUuid());
		SharedSpaceMember ssMemberToDelete = ssMemberService.findMemberByAccountUuid(authUser, authUser, dto.getUserUuid(),
				dto.getThreadUuid());
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
