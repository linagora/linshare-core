package org.linagora.linshare.core.service;

import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadMember;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface ThreadService {
	
	public Thread findByLsUuid(String uuid);

	public List<Thread> findAll();

	public void create(Account actor, String name) throws BusinessException;
	
	public ThreadMember getThreadMemberById(String id);
	
	public ThreadMember getThreadMemberFromUser(Thread thread, User user);
}
