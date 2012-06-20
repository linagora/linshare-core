package org.linagora.linShare.core.domain.entities;

import org.linagora.linShare.core.domain.constants.UserType;

public class Root extends User {

	@Override
	public UserType getAccountType() {
		return UserType.ROOT;
	}

}
