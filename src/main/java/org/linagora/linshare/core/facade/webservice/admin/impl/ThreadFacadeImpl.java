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

package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadMember;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.ThreadFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupDto;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupMemberDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ThreadService;

import com.google.common.collect.Sets;

public class ThreadFacadeImpl extends AdminGenericFacadeImpl implements
		ThreadFacade {

	private ThreadService threadService;

	public ThreadFacadeImpl(final AccountService accountService,
			final ThreadService threadService) {
		super(accountService);
		this.threadService = threadService;
	}

	@Override
	public WorkGroupDto find(String uuid) throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(uuid, "uuid must be set.");
		return new WorkGroupDto(threadService.find(actor, actor, uuid));
	}

	@Override
	public Set<WorkGroupDto> findAll() throws BusinessException {
		return findAll(null, null, null);
	}

	@Override
	public Set<WorkGroupDto> findAll(String pattern, String threadName,
			String memberName) throws BusinessException {
		User actor = super.checkAuthentication(Role.ADMIN);
		Set<Thread> threads = Sets.newHashSet();
		if (pattern == null && threadName == null && memberName == null) {
			threads.addAll(threadService.findAll(actor, actor));
		} else {
			if (memberName != null) {
				threads.addAll(threadService.searchByMembers(actor, memberName));
			}
			if (threadName != null) {
				threads.addAll(threadService.searchByName(actor, threadName));
			}
			if (pattern != null) {
				threads.addAll(threadService.searchByName(actor, pattern));
				threads.addAll(threadService.searchByMembers(actor, pattern));
			}
		}
		Set<WorkGroupDto> ret = Sets.newHashSet();
		for (Thread thread : threads) {
			ret.add(new WorkGroupDto(thread));
		}
		return ret;
	}

	@Override
	public Set<WorkGroupMemberDto> members(String uuid) throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(uuid, "uuid must be set.");
		Set<WorkGroupMemberDto> ret = new HashSet<WorkGroupMemberDto>();
		Thread thread = threadService.find(actor, actor, uuid);
		List<ThreadMember> threadMember = threadService.findAllThreadMembers(actor, actor, thread);
		for (ThreadMember m : threadMember)
			ret.add(new WorkGroupMemberDto(m));
		return ret;
	}

	@Override
	public WorkGroupDto update(WorkGroupDto threadDto) throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(threadDto, "thread must be set.");
		return new WorkGroupDto(threadService.update(actor, actor,
				threadDto.getUuid(), threadDto.getName()));
	}

	@Override
	public WorkGroupDto delete(String uuid) throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(uuid, "uuid must be set.");
		Thread thread = threadService.find(actor, actor, uuid);
		threadService.deleteThread(actor, actor, thread);
		return new WorkGroupDto(thread);
	}
}
