/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */

package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.ThreadFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupDto;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupMemberDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;

import com.google.common.collect.Sets;

@Deprecated(since = "2.0", forRemoval = true)
public class ThreadFacadeImpl extends AdminGenericFacadeImpl implements ThreadFacade {

	private ThreadService threadService;

	private final SharedSpaceNodeService ssNodeService;

	private final SharedSpaceMemberService ssMemberService;

	private final UserService userService;

	public ThreadFacadeImpl(final AccountService accountService,
			final ThreadService threadService,
			final SharedSpaceNodeService ssNodeService,
			final SharedSpaceMemberService ssMemberService,
			final UserService userService) {
		super(accountService);
		this.threadService = threadService;
		this.ssNodeService = ssNodeService;
		this.ssMemberService = ssMemberService;
		this.userService = userService;
	}

	@Override
	public WorkGroupDto find(String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(uuid, "uuid must be set.");
		SharedSpaceNode sSNode = ssNodeService.find(authUser, authUser, uuid);
		return new WorkGroupDto(threadService.find(authUser, authUser, uuid), sSNode);
	}

	@Override
	public Set<WorkGroupDto> findAll() throws BusinessException {
		return findAll(null, null, null);
	}

	@Override
	public Set<WorkGroupDto> findAll(String pattern, String threadName, String memberName) throws BusinessException {
		User authUser = super.checkAuthentication(Role.SUPERADMIN);
		Set<SharedSpaceNode> ssnodes = Sets.newHashSet();
		if (pattern == null || pattern.isEmpty()) {
			ssnodes.addAll(ssNodeService.findAll(authUser, authUser));
//			workGroups.addAll(threadService.findAll(authUser, authUser));
		} else {
			ssnodes.addAll(ssNodeService.searchByName(authUser, authUser, pattern));
			
//			if (memberName != null) {
//				ssnodes.addAll(ssNodeService.findAllNodesBySSMember(authUser, memberName));
////				workGroups.addAll(threadService.searchByMembers(authUser, memberName));
//			}
//			if (threadName != null) {
//				ssnodes.addAll(ssNodeService.searchByName(authUser, authUser, threadName));
//				workGroups.addAll(threadService.searchByName(authUser, threadName));
//			}
//			if (pattern != null) {
//				ssnodes.addAll(ssNodeService.searchByName(authUser, authUser, pattern));
//				ssnodes.addAll(ssNodeService.findAllNodesBySSMember(authUser, pattern));
//				workGroups.addAll(threadService.searchByName(authUser, pattern));
//				workGroups.addAll(threadService.searchByMembers(authUser, pattern));
//			}
		}
		Set<WorkGroupDto> ret = Sets.newHashSet();
		for (SharedSpaceNode node : ssnodes) {
			ret.add(find(node.getUuid()));
		}
		return ret;

	}

	@Override
	public Set<WorkGroupMemberDto> members(String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(uuid, "uuid must be set.");
		Set<WorkGroupMemberDto> ret = new HashSet<WorkGroupMemberDto>();
		List<SharedSpaceMember> sharedSpaceMembers = ssMemberService.findAll(authUser, authUser, uuid);
		for (SharedSpaceMember m : sharedSpaceMembers) {
			ret.add(new WorkGroupMemberDto(m, userService.findByLsUuid(m.getAccount().getUuid())));
		}
		return ret;
	}

	@Override
	public WorkGroupDto update(WorkGroupDto threadDto) throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(threadDto, "thread must be set.");
		WorkGroup workGroup = threadService.findByLsUuidUnprotected(threadDto.getUuid());
		SharedSpaceNode node = ssNodeService.find(authUser, authUser, threadDto.getUuid());
		node.setName(threadDto.getName());
		ssNodeService.update(authUser, authUser, node);
		return new WorkGroupDto(workGroup,node);
	}

	@Override
	public WorkGroupDto delete(String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(uuid, "uuid must be set.");
		WorkGroup workGroup = threadService.find(authUser, authUser, uuid);
		SharedSpaceNode node = ssNodeService.find(authUser, authUser, workGroup.getLsUuid());
		return ssNodeService.deleteWorkgroupDto(authUser, authUser, node);
	}
}
