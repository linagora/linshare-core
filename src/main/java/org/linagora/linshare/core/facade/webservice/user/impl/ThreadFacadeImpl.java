/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2016. Contribute to
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
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadMember;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupDto;
import org.linagora.linshare.core.facade.webservice.user.WorkGroupFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.QuotaService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;

import com.google.common.collect.Lists;

public class ThreadFacadeImpl extends UserGenericFacadeImp implements
		WorkGroupFacade {

	protected final ThreadService threadService;

	protected final UserService userService;

	protected final QuotaService quotaService;

	protected final FunctionalityReadOnlyService functionalityReadOnlyService;

	protected final AuditLogEntryService auditLogEntryService;

	public ThreadFacadeImpl(
			final ThreadService threadService,
			final AccountService accountService,
			final UserService userService,
			final QuotaService quotaService,
			final FunctionalityReadOnlyService functionalityService,
			final AuditLogEntryService auditLogEntryService) {
		super(accountService);
		this.threadService = threadService;
		this.functionalityReadOnlyService = functionalityService;
		this.userService = userService;
		this.auditLogEntryService = auditLogEntryService;
		this.quotaService = quotaService;
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

		User actor = checkAuthentication();

		List<WorkGroupDto> res = Lists.newArrayList();
		for (Thread thread : threadService.findAllWhereMember(actor)) {
			res.add(new WorkGroupDto(thread));
		}
		return res;
	}

	@Override
	public WorkGroupDto find(String uuid, Boolean members) throws BusinessException {
		Validate.notEmpty(uuid, "Missing required thread uuid");
		User actor = checkAuthentication();
		Thread thread = threadService.find(actor, actor, uuid);
		WorkGroupDto dto = null;
		if (members) {
			List<ThreadMember> threadMembers = threadService.findAllThreadMembers(actor, actor, thread);
			dto = new WorkGroupDto(thread, threadMembers);
		} else {
			dto = new WorkGroupDto(thread);
		}
		AccountQuota quota = quotaService.findByRelatedAccount(thread);
		dto.setQuotaUuid(quota.getUuid());
		return dto;
	}

	@Override
	public void addMember(String threadUuid, String domainId, String mail,
			boolean readonly) throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(domainId, "Missing required domain id");
		Validate.notEmpty(mail, "Missing required mail");
		User actor = checkAuthentication();
		Thread thread = threadService.find(actor, actor, threadUuid);
		User user = userService.findOrCreateUserWithDomainPolicies(mail,
				domainId, actor.getDomainId());
		threadService.addMember(actor, actor, thread, user, false, !readonly);
	}

	@Override
	public WorkGroupDto create(WorkGroupDto threadDto) throws BusinessException {
		Validate.notNull(threadDto, "Missing required thread");
		Validate.notEmpty(threadDto.getName(),
				"Missing required thread dto name");
		User actor = checkAuthentication();
		return new WorkGroupDto(threadService.create(actor, actor,
				threadDto.getName()));
	}

	@Override
	public WorkGroupDto delete(WorkGroupDto threadDto) throws BusinessException {
		Validate.notNull(threadDto, "Missing required thread dto");
		Validate.notEmpty(threadDto.getUuid(),
				"Missing required thread dto uuid");
		User actor = checkAuthentication();
		Thread thread = threadService.find(actor, actor,
				threadDto.getUuid());
		threadService.deleteThread(actor, actor, thread);
		return new WorkGroupDto(thread);
	}

	@Override
	public WorkGroupDto delete(String threadUuid) throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		User actor = checkAuthentication();
		Thread thread = threadService.find(actor, actor, threadUuid);
		threadService.deleteThread(actor, actor, thread);
		return new WorkGroupDto(thread);
	}

	@Override
	public WorkGroupDto update(String threadUuid, WorkGroupDto threadDto)
			throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notNull(threadDto, "Missing required ThreadDto");
		Validate.notEmpty(threadDto.getName(), "Missing required thread name");
		User actor = checkAuthentication();
		return new WorkGroupDto(threadService.update(actor, actor, threadUuid,
				threadDto.getName()));
	}

	@Override
	public Set<AuditLogEntryUser> findAll(String workGroupUuid, List<String> actions, List<String> types,
			String beginDate, String endDate) {
		Account actor = checkAuthentication();
		User owner = (User) getOwner(actor, null);
		Thread workGroup = threadService.find(actor, owner, workGroupUuid);
		return auditLogEntryService.findAll(actor, owner, workGroup, null, actions, types, beginDate, endDate);
	}
}
