package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.domain.constants.UserType;

public class TechnicalAccount extends User {

	@Override
	public UserType getAccountType() {
		return UserType.TECHNICALACCOUNT;
	}

}
