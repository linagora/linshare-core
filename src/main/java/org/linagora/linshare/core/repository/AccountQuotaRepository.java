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

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.fields.AccountQuotaDtoField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.webservice.utils.PageContainer;

public interface AccountQuotaRepository extends GenericQuotaRepository<AccountQuota> {

	AccountQuota find(Account account);

	/**
	 * Return all domain's uuid from all updated quota accounts today (between
	 * today 00:00:00 and now) Precondition : StatisticDailyUserBatchImpl and
	 * StatisticDailyThreadBatchImpl batches were run previously to update these
	 * quota accounts.
	 * @param startDate TODO
	 * 
	 * @return List<String>
	 */
	List<String> findDomainUuidByBatchModificationDate(Date startDate);

	Long sumOfCurrentValue(ContainerQuota ensembleQuota);

	PageContainer<AccountQuota> findAll(AbstractDomain domain, boolean includeNestedDomains,
			SortOrder sortOrder,
			AccountQuotaDtoField sortField,
			Optional<Long> greaterThanOrEqualTo, Optional<Long> lessThanOrEqualTo,
			Optional<ContainerQuotaType> containerQuotaType,
			Optional<LocalDate> beginDate, Optional<LocalDate> endDate,
			PageContainer<AccountQuota> container);
}
