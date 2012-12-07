package org.linagora.linshare.core.service;

import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Tag;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface TagService {

	public Tag findByOwnerAndName(Account actor, Thread owner, String name) throws BusinessException;
	
	public Tag findByOwnerAndName(User owner, String name) throws BusinessException;
	
	public void setTagToThreadEntries(Account actor, Thread owner, List<ThreadEntry> threadEntries, String tagName, String value) throws BusinessException;
	
}
