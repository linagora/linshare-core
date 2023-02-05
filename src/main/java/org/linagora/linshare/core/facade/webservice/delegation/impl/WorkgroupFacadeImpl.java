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
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupDto;
import org.linagora.linshare.core.facade.webservice.delegation.WorkgroupFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.QuotaService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;

import com.google.common.collect.Lists;

@Deprecated(since = "2.0", forRemoval = true)
public class WorkgroupFacadeImpl extends DelegationGenericFacadeImpl implements
		WorkgroupFacade {

	private final ThreadService threadService;

	protected final QuotaService quotaService;

	protected final SharedSpaceNodeService ssNodeService;

	public WorkgroupFacadeImpl(
			final AccountService accountService,
			final UserService userService,
			final QuotaService quotaService,
			final ThreadService threadService,
			final SharedSpaceNodeService ssNodeService) {
		super(accountService, userService);
		this.threadService = threadService;
		this.quotaService = quotaService;
		this.ssNodeService = ssNodeService;
	}

	@Override
	public WorkGroupDto find(String actorUuid, String uuid)
			throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notEmpty(uuid, "Missing required thread uuid");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		WorkGroup workGroup = threadService.find(authUser, actor, uuid);
		SharedSpaceNode ssnode = ssNodeService.find(authUser, actor, uuid);
		WorkGroupDto dto = new WorkGroupDto(workGroup, ssnode);
		AccountQuota quota = quotaService.findByRelatedAccount(workGroup);
		dto.setQuotaUuid(quota.getUuid());
		return dto;
	}

	@Override
	public List<WorkGroupDto> findAll(String actorUuid) throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		List<WorkGroupDto> res = Lists.newArrayList();
		for (SharedSpaceNodeNested ssnode : ssNodeService.findAllByAccount(authUser, actor)) {
			WorkGroup workGroup = threadService.find(authUser, actor, ssnode.getUuid());
			res.add(new WorkGroupDto(workGroup));
		}
		return res;
	}

	@Override
	public WorkGroupDto create(String actorUuid, WorkGroupDto threadDto)
			throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notNull(threadDto, "Missing required thread dto");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		SharedSpaceNode node = new SharedSpaceNode(threadDto.getName(), null, NodeType.WORK_GROUP);
		return ssNodeService.createWorkGroupDto(authUser, actor, node);

	}

	@Override
	public WorkGroupDto delete(String actorUuid, WorkGroupDto threadDto)
			throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notNull(threadDto, "Missing required thread dto");
		Validate.notEmpty(threadDto.getUuid(), "Missing required thread dto uuid");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		SharedSpaceNode node = ssNodeService.find(authUser, actor, threadDto.getUuid());
		return ssNodeService.deleteWorkgroupDto(authUser, authUser, node);
	}

	@Override
	public WorkGroupDto update(String actorUuid, String threadUuid, WorkGroupDto threadDto) throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notNull(threadDto, "Missing required ThreadDto");
		Validate.notEmpty(threadDto.getName(), "Missing required thread name");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		WorkGroup workGroup = threadService.findByLsUuidUnprotected(threadDto.getUuid());
		SharedSpaceNode node = ssNodeService.find(authUser, actor, threadUuid);
		node.setName(threadDto.getName());
		node = ssNodeService.update(authUser, actor, node);
		return new WorkGroupDto(workGroup, node);
	}
}
