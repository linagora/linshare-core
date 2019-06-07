/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */

package org.linagora.linshare.core.facade.webservice.admin.impl;

import org.apache.commons.lang.Validate;
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
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;

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
		SharedSpaceNode foundSharedSpaceNode = sharedSpaceNodeService.find(authUser, authUser, dto.getThreadUuid(), false);
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
		SharedSpaceMember ssMemberToUpdate = ssMemberService.findMemberByUuid(authUser, authUser, dto.getUserUuid(),
				dto.getThreadUuid());
		ssMemberToUpdate.setRole(new GenericLightEntity(defaultRole.getUuid(), defaultRole.getName()));
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
		SharedSpaceMember ssMemberToDelete = ssMemberService.findMemberByUuid(authUser, authUser, dto.getUserUuid(),
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
