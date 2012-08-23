package org.linagora.linshare.core.service;

import org.linagora.linshare.core.domain.entities.Thread;

public interface ThreadService {
	
	public Thread findByLsUuid(String uuid);
	
}
