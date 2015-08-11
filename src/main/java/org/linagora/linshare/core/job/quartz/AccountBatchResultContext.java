package org.linagora.linshare.core.job.quartz;

import org.linagora.linshare.core.domain.entities.Account;

public class AccountBatchResultContext extends BatchResultContext<Account> {

	public AccountBatchResultContext(Account resource) {
		super(resource);
		this.identifier = resource.getLsUuid();
	}

}
