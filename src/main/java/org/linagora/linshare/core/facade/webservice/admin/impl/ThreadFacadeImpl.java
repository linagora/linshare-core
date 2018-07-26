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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.entities.WorkgroupMember;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.ThreadFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupDto;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupMemberDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;

import com.google.common.collect.Sets;

public class ThreadFacadeImpl extends AdminGenericFacadeImpl implements ThreadFacade {

	private ThreadService threadService;

	private final SharedSpaceNodeService ssNodeService;

	private final SharedSpaceMemberService ssMemberService;

	public ThreadFacadeImpl(final AccountService accountService, final ThreadService threadService,
			final SharedSpaceNodeService ssNodeService, final SharedSpaceMemberService ssMemberService) {
		super(accountService);
		this.threadService = threadService;
		this.ssNodeService = ssNodeService;
		this.ssMemberService = ssMemberService;
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
		User authUser = super.checkAuthentication(Role.ADMIN);
		Set<WorkGroup> workGroups = Sets.newHashSet();
		Set<SharedSpaceNode> ssnodes = Sets.newHashSet();
		if (pattern == null && threadName == null && memberName == null) {
			ssnodes.addAll(ssNodeService.findAll(authUser));
			workGroups.addAll(threadService.findAll(authUser, authUser));
		} else {
			if (memberName != null) {
				ssnodes.addAll(ssNodeService.findAllNodesBySSMember(authUser, memberName));
				workGroups.addAll(threadService.searchByMembers(authUser, memberName));
			}
			if (threadName != null) {
				ssnodes.addAll(ssNodeService.searchByName(authUser, authUser, threadName));
				workGroups.addAll(threadService.searchByName(authUser, threadName));
			}
			if (pattern != null) {
				ssnodes.addAll(ssNodeService.searchByName(authUser, authUser, pattern));
				ssnodes.addAll(ssNodeService.findAllNodesBySSMember(authUser, pattern));
				workGroups.addAll(threadService.searchByName(authUser, pattern));
				workGroups.addAll(threadService.searchByMembers(authUser, pattern));
			}
		}
		Set<WorkGroupDto> ret = Sets.newHashSet();
		for (WorkGroup workGroup : workGroups) {
			ret.add(new WorkGroupDto(workGroup));
		}
		// TODO : put as a return when WorkgroupDto is disconected.
		Set<SharedSpaceNode> ssnodeRet = Sets.newHashSet();
		for (SharedSpaceNode node : ssnodes) {
			ssnodeRet.add(node);
		}
		return ret;

	}

	@Override
	public Set<WorkGroupMemberDto> members(String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(uuid, "uuid must be set.");
		Set<WorkGroupMemberDto> ret = new HashSet<WorkGroupMemberDto>();
		WorkGroup workGroup = threadService.find(authUser, authUser, uuid);
		List<WorkgroupMember> workgroupMembers = threadService.findAllThreadMembers(authUser, authUser, workGroup);
		for (WorkgroupMember m : workgroupMembers)
			ret.add(new WorkGroupMemberDto(m));
		// TODO : put as a return when WorkgroupDto is disconected.
		List<SharedSpaceMember> sharedSpaceMembers = ssMemberService.findAll(authUser, authUser, uuid);
		return ret;
	}

	@Override
	public WorkGroupDto update(WorkGroupDto threadDto) throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(threadDto, "thread must be set.");
		WorkGroupDto workGroupDto = new WorkGroupDto(
				threadService.update(authUser, authUser, threadDto.getUuid(), threadDto.getName()));
		SharedSpaceNode ssNodeFoundToUpdate = ssNodeService.find(authUser, authUser, workGroupDto.getUuid());
		ssNodeFoundToUpdate.setName(threadDto.getName());
		SharedSpaceNode ssnodeUpdated = ssNodeService.update(authUser, authUser, ssNodeFoundToUpdate);
		List<SharedSpaceMember> ssmembers = ssMemberService.findByNode(authUser, authUser, ssnodeUpdated.getUuid());
		for (SharedSpaceMember ssmember : ssmembers) {
			ssMemberService.updateAllSsNodes(ssmember,
					new GenericLightEntity(ssnodeUpdated.getUuid(), ssnodeUpdated.getName()));
		}
		return workGroupDto;
	}

	@Override
	public WorkGroupDto delete(String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(uuid, "uuid must be set.");
		WorkGroup workGroup = threadService.find(authUser, authUser, uuid);
		SharedSpaceNode node = ssNodeService.find(authUser, authUser, workGroup.getLsUuid());
		ssNodeService.delete(authUser, workGroup, node);
		threadService.deleteThread(authUser, authUser, workGroup);
		return new WorkGroupDto(workGroup);
	}
}
