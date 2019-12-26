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
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;

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
