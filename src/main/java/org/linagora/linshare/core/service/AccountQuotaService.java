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
import java.util.Optional;

import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.fields.AccountQuotaDtoField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.webservice.utils.PageContainer;

public interface AccountQuotaService {

	AccountQuota find(Account actor, String uuid);

	/**
	 * Only for Admins
	 * find a quota and check if it belongs to the right account 
	 * @param authUser Account authenticated user
	 * @param actor Account account that make the operation
	 * @param userUuid String uuid of the account whom quota belongs
	 * @param quotaUuid uuid of the quota
	 * @return {@link AccountQuota}
	 */
	AccountQuota find(Account authUser, Account actor, String userUuid, String quotaUuid);

	List<AccountQuota> findAll(Account actor);

	AccountQuota update(Account actor, AccountQuota entity);

	/**
	 * Only for Admins
	 * Update a quota of an account
	 * @param authUser Account authenticated user
	 * @param actor Account account that make the operation
	 * @param userUuid String uuid of the account whom quota belongs
	 * @param accountQuota {@link AccountQuota} account quota entity
	 * @return {@link AccountQuota}
	 */
	AccountQuota update(Account authUser, Account actor, String userUuid, AccountQuota accountQuota);

	PageContainer<AccountQuota> findAll(
			Account authUser,
			AbstractDomain domain, boolean includeNestedDomains,
			SortOrder sortOrder, AccountQuotaDtoField sortField,
			Optional<Long> greaterThanOrEqualTo, Optional<Long> lessThanOrEqualTo,
			Optional<ContainerQuotaType> containerQuotaType,
			Optional<String> beginDate, Optional<String> endDate,
			PageContainer<AccountQuota> container);

	AccountQuota find(Account actor, AbstractDomain domain, String uuid);

}
