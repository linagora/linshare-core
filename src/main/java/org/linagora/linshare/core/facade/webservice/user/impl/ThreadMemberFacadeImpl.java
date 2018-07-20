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
package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.entities.WorkgroupMember;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupMemberDto;
import org.linagora.linshare.core.facade.webservice.user.WorkGroupMemberFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.core.service.SharedSpaceRoleService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;

import com.google.common.collect.Lists;

public class ThreadMemberFacadeImpl extends UserGenericFacadeImp implements
		WorkGroupMemberFacade {

	private final ThreadService threadService;

	private final UserService userService;

	protected final SharedSpaceMemberService ssMemberService;

	protected final SharedSpaceRoleService ssRoleService;

	protected final SharedSpaceNodeService sharedSpaceNodeService;

	public ThreadMemberFacadeImpl(ThreadService threadService,
			AccountService accountService,
			UserService userService,
			SharedSpaceMemberService ssMemberService,
			SharedSpaceRoleService ssRoleService,
			SharedSpaceNodeService sharedSpaceNodeService) {
		super(accountService);
		this.threadService = threadService;
		this.userService = userService;
		this.ssMemberService = ssMemberService;
		this.ssRoleService = ssRoleService;
		this.sharedSpaceNodeService = sharedSpaceNodeService;
	}

	@Override
	public List<WorkGroupMemberDto> findAll(String threadUuid)
			throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		User authUser = checkAuthentication();
		WorkGroup workGroup = threadService.find(authUser, authUser, threadUuid);
		List<WorkGroupMemberDto> res = Lists.newArrayList();
		for (WorkgroupMember m : threadService.findAllThreadMembers(authUser, authUser, workGroup)) {
			res.add(new WorkGroupMemberDto(m));
		}
		return res;
	}

	@Override
	public WorkGroupMemberDto find(String threadUuid, String userUuid)
			throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(userUuid,
				"Missing required user uuid");
		User authUser = checkAuthentication();
		WorkGroup workGroup = threadService.find(authUser, authUser, threadUuid);
		return new WorkGroupMemberDto(threadService.getMemberFromUser(workGroup, userService.findByLsUuid(userUuid)));
	}

	@Override
	public WorkGroupMemberDto create(String threadUuid, String domainId, String userMail, boolean readOnly,
			boolean admin) throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(domainId, "Missing required domain id");
		Validate.notEmpty(userMail, "Missing required mail");
		User authUser = checkAuthentication();
		User user = userService.findOrCreateUser(userMail, domainId);
		WorkGroup workGroup = threadService.find(authUser, authUser, threadUuid);
		WorkgroupMember createdWorkGroupMember = threadService.addMember(authUser, authUser, workGroup, user, admin,
				!readOnly);
		// TODO Retrieve the role from the restService once the front will pass the info
		SharedSpaceNode foundSharedSpaceNode = sharedSpaceNodeService.find(authUser, authUser, threadUuid);
		SharedSpaceRole defaultRole = getDefaultRole(authUser, admin);
		ssMemberService.create(authUser, authUser,
				new GenericLightEntity(foundSharedSpaceNode.getUuid(), foundSharedSpaceNode.getName()),
				new GenericLightEntity(defaultRole.getUuid(), defaultRole.getName()),
				new GenericLightEntity(user.getLsUuid(), user.getFullName()));
		return new WorkGroupMemberDto(createdWorkGroupMember);
	}

	@Override
	public WorkGroupMemberDto update(String threadUuid,
			WorkGroupMemberDto threadMember) throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notNull(threadMember, "Missing required thread member");
		User authUser = checkAuthentication();
		WorkgroupMember updatedMember = threadService.updateMember(authUser, authUser,
				threadUuid, threadMember.getUserUuid(), threadMember.isAdmin(),
				!threadMember.isReadonly());
		// New SharedSpaceNode architecture
		SharedSpaceRole defaultRole = getDefaultRole(authUser, threadMember.isAdmin());
		User user = userService.findByLsUuid(threadMember.getUserUuid());
		SharedSpaceNode nodeOfMemberToUpdate = new SharedSpaceNode();
		nodeOfMemberToUpdate.setUuid(threadUuid);
		SharedSpaceMember ssMemberToUpdate = ssMemberService.findMember(authUser, authUser, user, nodeOfMemberToUpdate);
		ssMemberService.updateRole(authUser, authUser, ssMemberToUpdate.getUuid(),
				new GenericLightEntity(defaultRole.getUuid(), defaultRole.getName()));
		return new WorkGroupMemberDto(updatedMember);
	}

	@Override
	public WorkGroupMemberDto delete(String threadUuid, String userUuid)
			throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(userUuid, "Missing required user uuid");
		User authUser = checkAuthentication();
		WorkgroupMember member = threadService.deleteMember(authUser, authUser, threadUuid, userUuid);
		// New SharedSpaceNode architecture
		User user = userService.findByLsUuid(userUuid);
		SharedSpaceNode nodeOfMemberToDelete = new SharedSpaceNode();
		nodeOfMemberToDelete.setUuid(threadUuid);
		SharedSpaceMember ssMemberToDelete = ssMemberService.findMember(authUser, authUser, user, nodeOfMemberToDelete);
		ssMemberService.delete(authUser, authUser, ssMemberToDelete);
		return new WorkGroupMemberDto(member);
	}

	private SharedSpaceRole getDefaultRole(User authUser, boolean admin) {
		if (admin) {
			return ssRoleService.findByName(authUser, authUser, "ADMIN");
		}
		return ssRoleService.findByName(authUser, authUser, "READER");
	}
}
