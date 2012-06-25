package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.domain.constants.UserType;

public class Root extends User {

	@Override
	public UserType getAccountType() {
		return UserType.ROOT;
	}

}
