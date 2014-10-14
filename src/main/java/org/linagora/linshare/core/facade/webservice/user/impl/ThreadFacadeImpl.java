/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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

import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ThreadDto;
import org.linagora.linshare.core.facade.webservice.user.ThreadFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.UserService;

import com.google.common.collect.Lists;

public class ThreadFacadeImpl extends UserGenericFacadeImp implements
		ThreadFacade {

	private final ThreadService threadService;

	private final UserService userService;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	public ThreadFacadeImpl(
			final ThreadService threadService,
			final AccountService accountService,
			final UserService userService,
			final FunctionalityReadOnlyService functionalityService) {
		super(accountService);
		this.threadService = threadService;
		this.functionalityReadOnlyService = functionalityService;
		this.userService = userService;
	}

	@Override
	public User checkAuthentication() throws BusinessException {
		User user = super.checkAuthentication();
		Functionality functionality = functionalityReadOnlyService
				.getThreadTabFunctionality(user.getDomain());

		if (!functionality.getActivationPolicy().getStatus()) {
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		}
		return user;
	}

	@Override
	public List<ThreadDto> getAllMyThread() throws BusinessException {
		User actor = checkAuthentication();
		List<ThreadDto> res = Lists.newArrayList();

		for (Thread thread : threadService.findAllWhereMember(actor)) {
			res.add(new ThreadDto(thread));
		}
		return res;
	}

	@Override
	public ThreadDto getThread(String uuid) throws BusinessException {
		User actor = checkAuthentication();
		Thread thread = threadService.findByLsUuid(actor, actor, uuid);
		return new ThreadDto(thread, thread.getMyMembers());
	}

	@Override
	public void addMember(String threadUuid, String domainId, String mail,
			boolean readonly) throws BusinessException {
		User actor = checkAuthentication();
		Thread thread = threadService.findByLsUuid(actor, actor, threadUuid);
		User user = userService.findOrCreateUserWithDomainPolicies(mail,
				domainId, actor.getDomainId());
		threadService.addMember(actor, actor, thread, user, false, !readonly);
	}

}
