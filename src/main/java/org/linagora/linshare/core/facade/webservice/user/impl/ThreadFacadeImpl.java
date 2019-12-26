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
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupDto;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupMemberDto;
import org.linagora.linshare.core.facade.webservice.user.WorkGroupFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.QuotaService;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.core.service.SharedSpaceRoleService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Deprecated(since = "2.0", forRemoval = true)
public class ThreadFacadeImpl extends UserGenericFacadeImp implements
		WorkGroupFacade {

	protected final ThreadService threadService;

	protected final UserService userService;

	protected final QuotaService quotaService;

	protected final FunctionalityReadOnlyService functionalityReadOnlyService;

	protected final AuditLogEntryService auditLogEntryService;

	protected final SharedSpaceNodeService ssNodeService;

	protected final SharedSpaceMemberService ssMemberService;

	protected final SharedSpaceRoleService ssRoleService;

	protected final WorkGroupNodeService workGroupNodeService;

	public ThreadFacadeImpl(
			final ThreadService threadService,
			final AccountService accountService,
			final UserService userService,
			final QuotaService quotaService,
			final FunctionalityReadOnlyService functionalityService,
			final AuditLogEntryService auditLogEntryService,
			final SharedSpaceNodeService ssNodeService,
			final SharedSpaceMemberService ssMemberService,
			final SharedSpaceRoleService ssRoleService,
			WorkGroupNodeService workGroupNodeService) {
		super(accountService);
		this.threadService = threadService;
		this.functionalityReadOnlyService = functionalityService;
		this.userService = userService;
		this.auditLogEntryService = auditLogEntryService;
		this.quotaService = quotaService;
		this.ssNodeService = ssNodeService;
		this.ssMemberService = ssMemberService;
		this.ssRoleService = ssRoleService;
		this.workGroupNodeService = workGroupNodeService;
	}

	@Override
	public User checkAuthentication() throws BusinessException {
		User user = super.checkAuthentication();
		Functionality functionality = functionalityReadOnlyService.getWorkGroupFunctionality(user.getDomain());

		if (!functionality.getActivationPolicy().getStatus()) {
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		}
		return user;
	}

	@Override
	public List<WorkGroupDto> findAll() throws BusinessException {
		User authUser = checkAuthentication();
		List<WorkGroupDto> res = Lists.newArrayList();
		for (SharedSpaceNodeNested ssnode : ssNodeService.findAllByAccount(authUser, authUser)) {
			WorkGroup workGroup = threadService.find(authUser, authUser, ssnode.getUuid());
			res.add(new WorkGroupDto(workGroup));
		}
		return res;
	}

	@Override
	public WorkGroupDto find(String uuid, Boolean members) throws BusinessException {
		Validate.notEmpty(uuid, "Missing required thread uuid");
		User authUser = checkAuthentication();
		WorkGroup workGroup = threadService.find(authUser, authUser, uuid);
		SharedSpaceNode ssnode = ssNodeService.find(authUser, authUser, uuid);
		WorkGroupDto dto = null;
		if (members) {
			List<SharedSpaceMember> ssMembers = ssNodeService.findAllMembers(authUser, authUser, uuid, null);
			Set<WorkGroupMemberDto> memberDtos = Sets.newHashSet();
			for (SharedSpaceMember ssMember : ssMembers) {
				WorkGroupMemberDto memberDto = new WorkGroupMemberDto(ssMember, userService.findByLsUuid(ssMember.getAccount().getUuid()));
				memberDtos.add(memberDto);
			}
			dto = new WorkGroupDto(workGroup, ssnode, memberDtos);
		} else {
			dto = new WorkGroupDto(workGroup, ssnode);
		}
		AccountQuota quota = quotaService.findByRelatedAccount(workGroup);
		dto.setQuotaUuid(quota.getUuid());
		return dto;
	}

	@Override
	public WorkGroupDto create(WorkGroupDto threadDto) throws BusinessException {
		Validate.notNull(threadDto, "Missing required thread");
		Validate.notEmpty(threadDto.getName(), "Missing required thread dto name");
		User authUser = checkAuthentication();
		SharedSpaceNode node = new SharedSpaceNode(threadDto.getName(), null, NodeType.WORK_GROUP);
		return ssNodeService.createWorkGroupDto(authUser, authUser, node);
	}

	@Override
	public WorkGroupDto delete(WorkGroupDto threadDto) throws BusinessException {
		Validate.notNull(threadDto, "Missing required thread dto");
		Validate.notEmpty(threadDto.getUuid(), "Missing required thread dto uuid");
		User authUser = checkAuthentication();
		SharedSpaceNode node = ssNodeService.find(authUser, authUser, threadDto.getUuid());
		return ssNodeService.deleteWorkgroupDto(authUser, authUser, node);
	}

	@Override
	public WorkGroupDto delete(String threadUuid) throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		User authUser = checkAuthentication();
		SharedSpaceNode node = ssNodeService.find(authUser, authUser, threadUuid);
		return ssNodeService.deleteWorkgroupDto(authUser, authUser, node);
	}

	@Override
	public WorkGroupDto update(String threadUuid, WorkGroupDto threadDto) throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notNull(threadDto, "Missing required ThreadDto");
		Validate.notEmpty(threadDto.getName(), "Missing required thread name");
		User authUser = checkAuthentication();
		WorkGroup workGroup = threadService.findByLsUuidUnprotected(threadDto.getUuid());
		SharedSpaceNode node = ssNodeService.find(authUser, authUser, threadUuid);
		node.setName(threadDto.getName());
		node = ssNodeService.update(authUser, authUser, node);
		return new WorkGroupDto(workGroup, node);
	}

	@Override
	public Set<AuditLogEntryUser> findAll(String workGroupUuid, List<LogAction> actions, List<AuditLogEntryType> types,
			String beginDate, String endDate, String nodeUuid) {
		Account authUser = checkAuthentication();
		User actor = (User) getActor(authUser, null);
		WorkGroup workGroup = threadService.find(authUser, actor, workGroupUuid);
		WorkGroupNode node = new WorkGroupNode();
		if (!Strings.isNullOrEmpty(nodeUuid)) {
			node = workGroupNodeService.find(authUser, actor, workGroup, nodeUuid, false);
			Validate.notNull(node, "Missing required node");
		}
		return auditLogEntryService.findAll(authUser, actor, workGroup, node, actions, types, beginDate, endDate);
	}
}
