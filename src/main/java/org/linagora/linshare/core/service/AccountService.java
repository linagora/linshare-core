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
package org.linagora.linshare.core.service;

import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountContactLists;
import org.linagora.linshare.core.exception.BusinessException;

import javax.annotation.Nonnull;

public interface AccountService {

	/**
	 * 
	 * @param uuid
	 *            user identifier, should be lsUuid (or mail for compatibility,
	 *            but this will be remove asap.)
	 * @return Account
	 */
	public Account findByLsUuid(String uuid);

	public Account update(Account account) throws BusinessException;

	List<String> findAllKnownEmails(Account actor, String pattern);

	/**
	 * The goal of this method is to raise an exception when the account is not
	 * found, and to not change the old behavior with the old method findByLsUuid
	 */
	Account findAccountByLsUuid(String uuid);

	/**
	 * Finds the list of contact lists associated with a given account.
	 *
	 * @param user     The target account whose contact lists are to be retrieved. Must not be {@code null}.
	 * @return A list of {@link AccountContactLists} associated with the target account.
	 * @throws BusinessException If an error occurs during the operation,
	 *                           such as missing permissions or an invalid account.
	 */
	public @Nonnull List<AccountContactLists> findAccountContactListsByAccount(@Nonnull final Account user) throws BusinessException;

	/**
	 * Retrieves the contact lists for an account identified by its UUID.
	 *
	 * @param accountUuid The UUID of the account, which must not be null or empty.
	 * @return A list of {@link AccountContactLists} related to the guest, possibly empty.
	 * @throws IllegalArgumentException if the UUID is empty.
	 */
	public @Nonnull List<AccountContactLists> findAccountContactListsByAccount(@Nonnull final String accountUuid);
}
