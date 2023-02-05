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
package org.linagora.linshare.core.business.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.fields.AccountQuotaDtoField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.utils.PageContainer;

public interface AccountQuotaBusinessService {

	AccountQuota find(String uuid) throws BusinessException;

	AccountQuota find(Account account) throws BusinessException;

	List<AccountQuota> findAll() throws BusinessException;

	AccountQuota createOrUpdate(Account account, Date today) throws BusinessException;

	AccountQuota create(AccountQuota entity) throws BusinessException;

	AccountQuota update(AccountQuota entity, AccountQuota dto) throws BusinessException;

	List<String> findDomainUuidByBatchModificationDate(Date startDate);

	PageContainer<AccountQuota> findAll(Account authUser, AbstractDomain domain, boolean includeNestedDomains,
			SortOrder sortOrder, AccountQuotaDtoField sortField,
			Optional<Long> greaterThanOrEqualTo, Optional<Long> lessThanOrEqualTo,
			Optional<ContainerQuotaType> containerQuotaType,
			Optional<LocalDate> beginDate, Optional<LocalDate> endDate,
			PageContainer<AccountQuota> container);
}
