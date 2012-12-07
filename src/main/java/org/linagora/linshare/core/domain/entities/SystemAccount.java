package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.domain.constants.AccountType;

public class SystemAccount extends Account {

	public SystemAccount() {
		super();
		role = Role.SUPERADMIN;
	}

	@Override
	public AccountType getAccountType() {
		return AccountType.SYSTEM;
	}

	@Override
	public String getAccountReprentation() {
		return this.lsUuid;
	}

}
