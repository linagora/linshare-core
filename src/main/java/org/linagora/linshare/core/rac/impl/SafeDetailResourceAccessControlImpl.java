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

import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.rac.SafeDetailResourceAccessControl;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.mongo.entities.SafeDetail;

public class SafeDetailResourceAccessControlImpl
		extends AbstractResourceAccessControlImpl<Account, Account, SafeDetail>
		implements SafeDetailResourceAccessControl {

	private AccountRepository<Account> accountRepository;

	public SafeDetailResourceAccessControlImpl(AccountRepository<Account> accountRepository,
			FunctionalityReadOnlyService functionalityService) {
		super(functionalityService);
		this.accountRepository = accountRepository;
	}

	@Override
	protected boolean hasReadPermission(Account actor, Account account, SafeDetail entry, Object... opt) {
		if (actor.hasSafeRole()) {
			return true;
		} else if (actor.hasDelegationRole()) {
			return hasPermission(actor, TechnicalAccountPermissionType.SAFE_DETAIL_GET);
		} else if (actor.isInternal() || actor.isGuest()) {
			return (account != null && actor.equals(account)
					&& actor.getLsUuid().equals(entry.getAccountUuid()));
		}
		return false;
	}

	@Override
	protected boolean hasListPermission(Account actor, Account account, SafeDetail entry, Object... opt) {
		return defaultPermissionCheck(actor, account, entry, TechnicalAccountPermissionType.SAFE_DETAIL_LIST, false);
	}

	@Override
	protected boolean hasDeletePermission(Account actor, Account account, SafeDetail entry, Object... opt) {
		return defaultPermissionCheck(actor, account, entry, TechnicalAccountPermissionType.SAFE_DETAIL_DELETE);
	}

	@Override
	protected boolean hasCreatePermission(Account actor, Account account, SafeDetail entry, Object... opt) {
		return defaultPermissionCheck(actor, account, entry, TechnicalAccountPermissionType.SAFE_DETAIL_CREATE, false);
	}

	@Override
	protected boolean hasUpdatePermission(Account actor, Account account, SafeDetail entry, Object... opt) {
		return false;
	}

	@Override
	protected String getTargetedAccountRepresentation(Account targetedAccount) {
		return targetedAccount.getAccountRepresentation();
	}

	@Override
	protected String getEntryRepresentation(SafeDetail entry) {
		return entry.getUuid();
	}

	@Override
	protected Account getOwner(SafeDetail entry, Object... opt) {
		return accountRepository.findByLsUuid(entry.getAccountUuid());
	}
}
