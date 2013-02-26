package org.linagora.linshare.core.facade.impl;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadMember;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.WebServiceThreadFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.webservice.dto.ThreadDto;
import org.linagora.linshare.webservice.dto.ThreadMemberDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class WebServiceThreadFacadeImpl implements WebServiceThreadFacade {

	private final ThreadService threadService;

	private final AccountService accountService;

	public WebServiceThreadFacadeImpl(ThreadService threadService, AccountService accountService) {
		super();
		this.threadService = threadService;
		this.accountService = accountService;
	}

	@Override
	public User checkAuthentication() throws BusinessException {
		User actor = getAuthentication();

		if (actor == null) {
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_UNAUTHORIZED, "You are not authorized to use this service");
		}

		return actor;
	}

	private User getAuthentication() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = (auth != null) ? auth.getName() : null; // get logged in
																// username
		if (name == null) {
			return null;
		}
		User user = (User) accountService.findByLsUid(name);
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

}
