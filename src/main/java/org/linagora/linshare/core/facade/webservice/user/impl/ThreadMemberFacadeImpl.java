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
package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadMember;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupMemberDto;
import org.linagora.linshare.core.facade.webservice.user.WorkGroupMemberFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.UserService;

import com.google.common.collect.Lists;

public class ThreadMemberFacadeImpl extends UserGenericFacadeImp implements
		WorkGroupMemberFacade {

	private final ThreadService threadService;

	private final UserService userService;

	public ThreadMemberFacadeImpl(ThreadService threadService,
			AccountService accountService, UserService userService) {
		super(accountService);
		this.threadService = threadService;
		this.userService = userService;
	}

	@Override
	public List<WorkGroupMemberDto> findAll(String threadUuid)
			throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		User actor = checkAuthentication();
		Thread thread = threadService.find(actor, actor, threadUuid);
		List<WorkGroupMemberDto> res = Lists.newArrayList();
		for (ThreadMember m : threadService.findAllThreadMembers(actor, actor, thread)) {
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
		User actor = checkAuthentication();
		Thread thread = threadService.find(actor, actor, threadUuid);
		return new WorkGroupMemberDto(threadService.getMemberFromUser(thread,
				userService.findByLsUuid(userUuid)));
	}

	@Override
	public WorkGroupMemberDto create(String threadUuid, String domainId,
			String userMail, boolean readOnly, boolean admin)
			throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(domainId, "Missing required domain id");
		Validate.notEmpty(userMail, "Missing required mail");
		User actor = checkAuthentication();
		User user = userService.findOrCreateUser(userMail, domainId);
		Thread thread = threadService.find(actor, actor, threadUuid);
		return new WorkGroupMemberDto(threadService.addMember(actor, actor,
				thread, user, admin, !readOnly));
	}

	@Override
	public WorkGroupMemberDto update(String threadUuid,
			WorkGroupMemberDto threadMember) throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notNull(threadMember, "Missing required thread member");
		User actor = checkAuthentication();
		return new WorkGroupMemberDto(threadService.updateMember(actor, actor,
				threadUuid, threadMember.getUserUuid(), threadMember.isAdmin(),
				!threadMember.isReadonly()));
	}

	@Override
	public WorkGroupMemberDto delete(String threadUuid, String userUuid)
			throws BusinessException {
		Validate.notEmpty(threadUuid, "Missing required thread uuid");
		Validate.notEmpty(userUuid, "Missing required user uuid");
		User actor = checkAuthentication();
		ThreadMember member = threadService.deleteMember(actor, actor, threadUuid, userUuid);
		return new WorkGroupMemberDto(member);
	}
}
