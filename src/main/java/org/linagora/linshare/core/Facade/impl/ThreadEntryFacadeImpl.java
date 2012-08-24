package org.linagora.linshare.core.Facade.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.Facade.ThreadEntryFacade;
import org.linagora.linshare.core.business.service.impl.DocumentEntryBusinessServiceImpl;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.domain.transformers.impl.ThreadEntryTransformer;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.ThreadEntryVo;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ThreadEntryService;
import org.linagora.linshare.core.service.ThreadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadEntryFacadeImpl implements ThreadEntryFacade {

	private static final Logger logger = LoggerFactory.getLogger(ThreadEntryFacadeImpl.class);

	private final AccountService accountService;
	
	private final ThreadService threadService;
	
	private final ThreadEntryService threadEntryService;
	
	private final ThreadEntryTransformer threadEntryTransformer;
	
	
	public ThreadEntryFacadeImpl(AccountService accountService, ThreadService threadService, ThreadEntryService threadEntryService, ThreadEntryTransformer threadEntryTransformer) {
		super();
		this.accountService = accountService;
		this.threadService = threadService;
		this.threadEntryService = threadEntryService;
		this.threadEntryTransformer = threadEntryTransformer;
	}


	@Override
	public DocumentVo insertFile(UserVo actorVo, ThreadVo threadVo, InputStream stream, Long size, String fileName) throws BusinessException {
		logger.debug("insert file for thread entries");
		
		Account actor = accountService.findByLsUid(actorVo.getLsUid());
		Thread thread = threadService.findByLsUuid(threadVo.getLsUuid());
		
		ThreadEntry threadEntry = threadEntryService.createThreadEntry(actor, thread, stream, size, fileName);
		
		return threadEntryTransformer.disassemble(threadEntry);
	
	}


	@Override
	public List<ThreadVo> getAllThread() {
		List<Thread> all = threadService.findAll();
		List<ThreadVo> res = new ArrayList<ThreadVo>(); 
		for (Thread thread : all) {
			res.add(new ThreadVo(thread));
		}
		return res;
	}


	@Override
	public List<ThreadEntryVo> getAllThreadEntryVo(UserVo actorVo, ThreadVo threadVo) throws BusinessException {
		Account actor = accountService.findByLsUid(actorVo.getLsUid());
		Thread thread = threadService.findByLsUuid(threadVo.getLsUuid());
		List<ThreadEntry> threadEntries = threadEntryService.findAllThreadEntries(actor, thread);
		
		List<ThreadEntryVo> res = new ArrayList<ThreadEntryVo>(); 
		for (ThreadEntry threadEntry : threadEntries) {
			res.add(new ThreadEntryVo(threadEntry));
		}
		return res;
	}

}
