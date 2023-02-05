/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.rac.impl;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.rac.UserPreferenceResourceAccessControl;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.mongo.entities.UserPreference;

public class UserPreferenceResourceAccessControlImpl
		extends AbstractResourceAccessControlImpl<Account, Account, UserPreference>
		implements UserPreferenceResourceAccessControl {

	private AccountRepository<Account> accountRepository;

	public UserPreferenceResourceAccessControlImpl(FunctionalityReadOnlyService functionalityService,
			AccountRepository<Account> accountRepository) {
		super(functionalityService);
		this.accountRepository = accountRepository;
	}

	@Override
	protected boolean hasReadPermission(Account authUser, Account actor, UserPreference entry, Object... opt) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean hasListPermission(Account authUser, Account actor, UserPreference entry, Object... opt) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean hasDeletePermission(Account authUser, Account actor, UserPreference entry, Object... opt) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean hasCreatePermission(Account authUser, Account actor, UserPreference entry, Object... opt) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean hasUpdatePermission(Account authUser, Account actor, UserPreference entry, Object... opt) {
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

	@Override
	protected Account getOwner(UserPreference entry, Object... opt) {
		return accountRepository.findByLsUuid(entry.getAccountUuid());
	}

}
