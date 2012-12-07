package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.domain.constants.AccountType;

public class TechnicalAccount extends User {

	private TechnicalAccountPermission permission;
	
	@Override
	public AccountType getAccountType() {
		return AccountType.TECHNICALACCOUNT;
	}
	
	@Override
	public String getAccountReprentation() {
		return lsUuid ;
	}

	public TechnicalAccountPermission getPermission() {
		return permission;
	}

	public void setPermission(TechnicalAccountPermission permission) {
		this.permission = permission;
	}

}
