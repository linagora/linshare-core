package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.domain.constants.AccountType;

public class Root extends User {

	@Override
	public AccountType getAccountType() {
		return AccountType.ROOT;
	}

}
