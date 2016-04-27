package org.linagora.linshare.core.rac.impl;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.rac.UserPreferenceResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.mongo.entities.UserPreference;

public class UserPreferenceResourceAccessControlImpl
		extends AbstractResourceAccessControlImpl<Account, Account, UserPreference>
		implements UserPreferenceResourceAccessControl {

	public UserPreferenceResourceAccessControlImpl(FunctionalityReadOnlyService functionalityService) {
		super(functionalityService);
	}

	@Override
	protected boolean hasReadPermission(Account actor, Account account, UserPreference entry, Object... opt) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean hasListPermission(Account actor, Account account, UserPreference entry, Object... opt) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean hasDeletePermission(Account actor, Account account, UserPreference entry, Object... opt) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean hasCreatePermission(Account actor, Account account, UserPreference entry, Object... opt) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean hasUpdatePermission(Account actor, Account account, UserPreference entry, Object... opt) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected String getTargetedAccountRepresentation(Account targetedAccount) {
		return targetedAccount.getAccountRepresentation();
	}

	@Override
	protected String getEntryRepresentation(UserPreference entry) {
		return entry.toString();
	}

}
