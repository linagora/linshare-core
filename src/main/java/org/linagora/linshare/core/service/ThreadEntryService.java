package org.linagora.linshare.core.service;

import java.io.InputStream;
import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.exception.BusinessException;

public interface ThreadEntryService {

	
	public ThreadEntry createThreadEntry(Account actor, Thread thread, InputStream stream, Long size, String fileName) throws BusinessException;
	
	public ThreadEntry findById(Account actor, String threadEntryUuid) throws BusinessException;
	
	public void deleteThreadEntry(Account actor, ThreadEntry threadEntry) throws BusinessException;
	
	public List<ThreadEntry> findAllThreadEntries(Account actor, Thread thread) throws BusinessException;

	public InputStream getDocumentStream(Account actor, String uuid) throws BusinessException;

	public boolean documentHasThumbnail(Account actor, String identifier);

	public InputStream getDocumentThumbnailStream(Account owner, String uuid) throws BusinessException;

	public List<ThreadEntry> findAllThreadEntriesTaggedWith(Account actor, Thread thread, String[] names);

	public void updateFileProperties(Account actor, String threadEntryUuid, String fileComment) throws BusinessException;
	
}
