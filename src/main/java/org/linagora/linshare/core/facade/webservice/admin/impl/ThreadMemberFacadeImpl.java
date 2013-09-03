package org.linagora.linshare.core.facade.webservice.admin.impl;

import org.linagora.linshare.core.domain.entities.ThreadMember;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.ThreadMemberFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.webservice.dto.ThreadMemberDto;

public class ThreadMemberFacadeImpl extends AdminGenericFacadeImpl implements
		ThreadMemberFacade {
	
	private ThreadService threadService;

	public ThreadMemberFacadeImpl(final AccountService accountService,
			final ThreadService threadService) {
		super(accountService);
		this.threadService = threadService;
	}

	@Override
	public ThreadMemberDto get(Long id) throws BusinessException {
		return new ThreadMemberDto(threadService.getThreadMemberById(id));
	}

	@Override
	public void update(ThreadMemberDto dto) throws BusinessException {
		User actor = super.getAuthentication();
		ThreadMember member = threadService.getThreadMemberById(dto.getId());
		boolean admin = dto.isAdmin();
		boolean readonly = dto.isReadonly();

		this.threadService.updateMember(actor, member, admin, !readonly);
	}

	@Override
	public void delete(ThreadMemberDto dto) throws BusinessException {
		User actor = super.getAuthentication();
		ThreadMember member = threadService.getThreadMemberById(dto.getId());

		this.threadService.deleteMember(actor, member.getThread(), member);
	}
}
