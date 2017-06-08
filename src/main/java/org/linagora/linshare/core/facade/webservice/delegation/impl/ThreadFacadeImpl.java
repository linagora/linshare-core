/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupDto;
import org.linagora.linshare.core.facade.webservice.delegation.ThreadFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.QuotaService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.UserService;

import com.google.common.collect.Lists;

public class ThreadFacadeImpl extends DelegationGenericFacadeImpl implements
		ThreadFacade {

	private final ThreadService threadService;

	protected final QuotaService quotaService;

	public ThreadFacadeImpl(
			final AccountService accountService,
			final UserService userService,
			final QuotaService quotaService,
			final ThreadService threadService) {
		super(accountService, userService);
		this.threadService = threadService;
		this.quotaService = quotaService;
	}

	@Override
	public WorkGroupDto find(String ownerUuid, String uuid)
			throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(uuid, "Missing required thread uuid");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		Thread thread = threadService.find(actor, owner, uuid);
		WorkGroupDto dto = new WorkGroupDto(thread);
		AccountQuota quota = quotaService.findByRelatedAccount(thread);
		dto.setQuotaUuid(quota.getUuid());
		return dto;
	}

	@Override
	public List<WorkGroupDto> findAll(String ownerUuid) throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		List<WorkGroupDto> res = Lists.newArrayList();
		List<Thread> threads = threadService.findAll(actor, owner);
		for (Thread thread : threads) {
			res.add(new WorkGroupDto(thread));
		}
		return res;
	}

	@Override
	public WorkGroupDto create(String ownerUuid, WorkGroupDto threadDto)
			throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notNull(threadDto, "Missing required thread dto");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		return new WorkGroupDto(threadService.create(actor, owner, threadDto.getName()));

	}

	@Override
	public WorkGroupDto delete(String ownerUuid, WorkGroupDto threadDto)
			throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notNull(threadDto, "Missing required thread dto");
		Validate.notEmpty(threadDto.getUuid(), "Missing required thread dto uuid");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		Thread thread = threadService.find(actor, owner, threadDto.getUuid());
		threadService.deleteThread(actor, owner, thread);
		return new WorkGroupDto(thread);
	}

	@Override
	public WorkGroupDto update(String ownerUuid, String threadUuid,
			WorkGroupDto threadDto) throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notNull(threadDto, "Missing required ThreadDto");
		Validate.notEmpty(threadDto.getName(), "Missing required thread name");

		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		return new WorkGroupDto(threadService.update(actor, owner, threadUuid,
				threadDto.getName()));
	}
}
