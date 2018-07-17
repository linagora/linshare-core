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

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.entities.WorkgroupMember;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupDto;
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
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;

import com.google.common.collect.Lists;

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

	public ThreadFacadeImpl(
			final ThreadService threadService,
			final AccountService accountService,
			final UserService userService,
			final QuotaService quotaService,
			final FunctionalityReadOnlyService functionalityService,
			final AuditLogEntryService auditLogEntryService,
			final SharedSpaceNodeService ssNodeService,
			final SharedSpaceMemberService ssMemberService,
			final SharedSpaceRoleService ssRoleService) {
		super(accountService);
		this.threadService = threadService;
		this.functionalityReadOnlyService = functionalityService;
		this.userService = userService;
		this.auditLogEntryService = auditLogEntryService;
		this.quotaService = quotaService;
		this.ssNodeService = ssNodeService;
		this.ssMemberService = ssMemberService;
		this.ssRoleService = ssRoleService;
	}

	@Override
	public User checkAuthentication() throws BusinessException {
		User user = super.checkAuthentication();
		Functionality functionality = functionalityReadOnlyService
				.getWorkGroupFunctionality(user.getDomain());

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
		for (WorkGroup workGroup : threadService.findAllWhereMember(authUser)) {
			res.add(new WorkGroupDto(workGroup));
		}
		return res;
	}

	@Override
	public WorkGroupDto find(String uuid, Boolean members) throws BusinessException {
		Validate.notEmpty(uuid, "Missing required thread uuid");
		User authUser = checkAuthentication();
		WorkGroup workGroup = threadService.find(authUser, authUser, uuid);
		WorkGroupDto dto = null;
		if (members) {
			List<WorkgroupMember> workgroupMembers = threadService.findAllThreadMembers(authUser, authUser, workGroup);
			dto = new WorkGroupDto(workGroup, workgroupMembers);
		} else {
			dto = new WorkGroupDto(workGroup);
		}
		AccountQuota quota = quotaService.findByRelatedAccount(workGroup);
		dto.setQuotaUuid(quota.getUuid());
		return dto;
	}

	@Override
	public void addMember(String threadUuid, String domainId, String mail,
			boolean readonly) throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(domainId, "Missing required domain id");
		Validate.notEmpty(mail, "Missing required mail");
		User authUser = checkAuthentication();
		WorkGroup workGroup = threadService.find(authUser, authUser, threadUuid);
		User user = userService.findOrCreateUserWithDomainPolicies(mail, domainId, authUser.getDomainId());
		threadService.addMember(authUser, authUser, workGroup, user, false, !readonly);
		// TODO Retrieve the role from the restService once the front will pass the info
		SharedSpaceNode foundSharedSpaceNode = ssNodeService.find(authUser, authUser, threadUuid);
		SharedSpaceRole defaultRole = ssRoleService.findByName(authUser, authUser, "CONTRIBUTOR");
		ssMemberService.create(authUser, authUser,
				new GenericLightEntity(foundSharedSpaceNode.getUuid(), foundSharedSpaceNode.getName()),
				new GenericLightEntity(defaultRole.getUuid(), defaultRole.getName()),
				new GenericLightEntity(user.getLsUuid(), user.getFullName()));
	}

	@Override
	public WorkGroupDto create(WorkGroupDto threadDto) throws BusinessException {
		Validate.notNull(threadDto, "Missing required thread");
		Validate.notEmpty(threadDto.getName(), "Missing required thread dto name");
		User authUser = checkAuthentication();
		WorkGroupDto toCreate = new WorkGroupDto(threadService.create(authUser, authUser, threadDto.getName()));
		SharedSpaceNode node = new SharedSpaceNode(threadDto.getName(), null, NodeType.WORK_GROUP);
		node.setUuid(toCreate.getUuid());
		ssNodeService.create(authUser, authUser, node);
		return toCreate;
	}

	@Override
	public WorkGroupDto delete(WorkGroupDto threadDto) throws BusinessException {
		Validate.notNull(threadDto, "Missing required thread dto");
		Validate.notEmpty(threadDto.getUuid(), "Missing required thread dto uuid");
		User authUser = checkAuthentication();
		WorkGroup workGroup = threadService.find(authUser, authUser, threadDto.getUuid());
		threadService.deleteThread(authUser, authUser, workGroup);
		SharedSpaceNode node = ssNodeService.find(authUser, authUser, threadDto.getUuid());
		ssNodeService.delete(authUser, authUser, node);
		return new WorkGroupDto(workGroup);
	}

	@Override
	public WorkGroupDto delete(String threadUuid) throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		User authUser = checkAuthentication();
		WorkGroup workGroup = threadService.find(authUser, authUser, threadUuid);
		threadService.deleteThread(authUser, authUser, workGroup);
		SharedSpaceNode node = ssNodeService.find(authUser, authUser, threadUuid);
		ssNodeService.delete(authUser, authUser, node);
		return new WorkGroupDto(workGroup);
	}

	@Override
	public WorkGroupDto update(String threadUuid, WorkGroupDto threadDto)
			throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notNull(threadDto, "Missing required ThreadDto");
		Validate.notEmpty(threadDto.getName(), "Missing required thread name");
		User authUser = checkAuthentication();
		return new WorkGroupDto(threadService.update(authUser, authUser, threadUuid,
				threadDto.getName()));
	}

	@Override
	public Set<AuditLogEntryUser> findAll(String workGroupUuid, List<LogAction> actions, List<AuditLogEntryType> types,
			String beginDate, String endDate) {
		Account authUser = checkAuthentication();
		User actor = (User) getActor(authUser, null);
		WorkGroup workGroup = threadService.find(authUser, actor, workGroupUuid);
		return auditLogEntryService.findAll(authUser, actor, workGroup, null, actions, types, beginDate, endDate);
	}
}
