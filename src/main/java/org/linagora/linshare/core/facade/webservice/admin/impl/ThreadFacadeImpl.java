package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.entities.Role;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadMember;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.ThreadFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.webservice.dto.ThreadDto;
import org.linagora.linshare.webservice.dto.ThreadMemberDto;

public class ThreadFacadeImpl extends AdminGenericFacadeImpl implements
		ThreadFacade {

	private ThreadService threadService;

	public ThreadFacadeImpl(final AccountService accountService,
			final ThreadService threadService) {
		super(accountService);
		this.threadService = threadService;
	}

	@Override
	public List<ThreadDto> getAll() {
		List<ThreadDto> ret = new ArrayList<ThreadDto>();

		for (Thread t : threadService.findAll())
			ret.add(new ThreadDto(t));
		return ret;
	}

	@Override
	public ThreadDto get(String uuid) {
		return new ThreadDto(threadService.findByLsUuid(uuid));
	}

	@Override
	public List<ThreadMemberDto> getMembers(String uuid) {
		List<ThreadMemberDto> ret = new ArrayList<ThreadMemberDto>();

		for (ThreadMember m : threadService.findByLsUuid(uuid).getMyMembers())
			ret.add(new ThreadMemberDto(m));
		return ret;
	}

	@Override
	public void addMember(String uuid, ThreadMemberDto member) throws BusinessException {
		User actor = super.getAuthentication();
		Thread thread = threadService.findByLsUuid(member.getThreadUuid());
		User user = (User) accountService.findByLsUuid(member.getUserUuid());
		boolean readOnly = member.isReadonly();

		threadService.addMember(actor, thread, user, readOnly);
	}

	@Override
	public void delete(String uuid) throws BusinessException {
		User actor = super.getAuthentication();
		Thread thread = threadService.findByLsUuid(uuid);

		threadService.deleteThread(actor, thread);
	}
}
