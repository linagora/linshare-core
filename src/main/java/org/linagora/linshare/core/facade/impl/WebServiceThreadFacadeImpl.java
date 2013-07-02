/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
package org.linagora.linshare.core.facade.impl;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.WebServiceThreadFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.webservice.dto.ThreadDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServiceThreadFacadeImpl extends WebServiceGenericFacadeImpl implements WebServiceThreadFacade {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(WebServiceThreadFacadeImpl.class);

	private final ThreadService threadService;

	private final UserService userService;

	private final FunctionalityService functionalityService;

	public WebServiceThreadFacadeImpl(ThreadService threadService, AccountService accountService, UserService userService, FunctionalityService functionalityService) {
		super(accountService);
		this.threadService = threadService;
		this.functionalityService = functionalityService;
		this.userService = userService;
	}

	@Override
	public User checkAuthentication() throws BusinessException {
		User user = super.checkAuthentication();
		Functionality functionality = functionalityService.getUserTabFunctionality(user.getDomain());
		if (!functionality.getActivationPolicy().getStatus()) {
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_UNAUTHORIZED, "You are not authorized to use this service");
		}
		return user;
	}

	@Override
	public List<ThreadDto> getAllMyThread() throws BusinessException {
		List<ThreadDto> res = new ArrayList<ThreadDto>();
		List<Thread> list = threadService.findAll();
		for (Thread thread : list) {
			res.add(new ThreadDto(thread));
		}
		return res;
	}

	@Override
	public ThreadDto getThread(String uuid) throws BusinessException {
		Thread thread = threadService.findByLsUuid(uuid);
		ThreadDto res = new ThreadDto(thread, thread.getMyMembers());
		return res;
	}

	@Override
	public void addMember(Account actor, String threadUuid, String domainId, String mail, boolean readonly) throws BusinessException {
		Thread thread = threadService.findByLsUuid(threadUuid);
		User user = userService.findOrCreateUserWithDomainPolicies(domainId, mail, actor.getDomainId());
		threadService.addMember(actor, thread, user, readonly);
	}
}
