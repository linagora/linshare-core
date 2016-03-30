package org.linagora.linshare.core.job.quartz;

import org.linagora.linshare.core.domain.entities.User;

public class InconsistentUserBatchResultContext extends BatchResultContext<User> {

	public InconsistentUserBatchResultContext(User resource) {
		super(resource);
	}

}
