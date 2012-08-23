package org.linagora.linshare.core.service;

import java.io.InputStream;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.exception.BusinessException;

public interface ThreadEntryService {

	
	public ThreadEntry createThreadEntry(Account actor, Thread thread, InputStream stream, Long size, String fileName) throws BusinessException;
	
	public ThreadEntry findById(Account actor, Thread thread, String currentDocEntryUuid) throws BusinessException;
	
	public void deleteThreadEntry(Account actor, Thread thread, String docEntryUuid) throws BusinessException;
}
