package org.linagora.linshare.core.Facade.impl;

import java.io.InputStream;

import org.linagora.linshare.core.Facade.ThreadFacade;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.domain.transformers.impl.ThreadEntryTransformer;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ThreadEntryService;
import org.linagora.linshare.core.service.ThreadService;

public class ThreadFacadeImpl implements ThreadFacade {

	private final AccountService accountService;
	
	private final ThreadService threadService;
	
	private final ThreadEntryService threadEntryService;
	
	private final ThreadEntryTransformer threadEntryTransformer;
	
	
	public ThreadFacadeImpl(AccountService accountService, ThreadService threadService, ThreadEntryService threadEntryService,
			ThreadEntryTransformer threadEntryTransformer) {
		super();
		this.accountService = accountService;
		this.threadService = threadService;
		this.threadEntryService = threadEntryService;
		this.threadEntryTransformer = threadEntryTransformer;
	}


	@Override
	public DocumentVo insertFile(UserVo actorVo, ThreadVo threadVo, InputStream stream, Long size, String fileName) throws BusinessException {
		Account actor = accountService.findByLsUid(actorVo.getLsUid());
		Thread thread = threadService.findByLsUuid(threadVo.getLsUuid());
		
		ThreadEntry threadEntry = threadEntryService.createThreadEntry(actor, thread, stream, size, fileName);
		
		return threadEntryTransformer.disassemble(threadEntry);
	
	}

}
