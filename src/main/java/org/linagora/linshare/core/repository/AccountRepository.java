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
package org.linagora.linshare.core.repository;

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.SystemAccount;

public interface AccountRepository<U extends Account> extends
		AbstractRepository<U> {

	U findByLsUuid(String lsUuid);

	/**
	 * Find account by lsUuid, to find activate and deleted user.
	 * @param lsUuid uuid of the account
	 * @return The Account
	 */
	U findActivateAndDestroyedByLsUuid(String lsUuid);

	boolean exist(String lsUuid);

	List<U> findByDomain(String domain);
	
	U findByMailAndDomain(String domain, String mail);

	SystemAccount getBatchSystemAccount();

	SystemAccount getUploadRequestSystemAccount();

	SystemAccount getAnonymousShareSystemAccount();

	U findDeleted(String lsUuid);

	void markToPurge(U entity);

	void purge(U entity);

	U findAccountsReadyToPurge(String lsUuid);

	List<String> findAllAccountsReadyToPurge();

	List<String> findAllDeletedAccountsToPurge(Date limit);

	List<String> findAllNonAnonymizedPurgedAccounts(Date modificationDate);

	List<String> findAllKnownEmails(String pattern);

	List<String> findAllAccountWithMissingQuota();

	List<String> findAllModeratorUuidsByGuest(Account guest);
}
