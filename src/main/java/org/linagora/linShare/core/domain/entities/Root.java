package org.linagora.linShare.core.domain.entities;

import org.linagora.linShare.core.domain.constants.UserType;

public class Root extends Account {

	@Override
	public UserType getAccountType() {
		return UserType.ROOT;
	}

}
