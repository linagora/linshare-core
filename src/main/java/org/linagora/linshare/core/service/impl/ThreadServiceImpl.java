package org.linagora.linshare.core.service.impl;

import java.util.List;

import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.repository.ThreadRepository;
import org.linagora.linshare.core.service.ThreadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadServiceImpl implements ThreadService {

	
	final private static Logger logger = LoggerFactory.getLogger(ThreadServiceImpl.class);
	
	private final ThreadRepository<Thread> threadRepository;
    
	
	public ThreadServiceImpl(ThreadRepository<Thread> threadRepository) {
		super();
		this.threadRepository = threadRepository;
	}

	
	@Override
	public Thread findByLsUuid(String uuid) {
		Thread thread = threadRepository.findByLsUuid(uuid);
		if(thread == null) {
			logger.error("Can't find thread  : " + uuid);
		}
		return thread;
	}


	@Override
	public List<Thread> findAll() {
		List<Thread> all = threadRepository.findAll();
		logger.debug("count : " + all.size());
		return all;
	}
	

}
