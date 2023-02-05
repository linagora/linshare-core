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

import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.OperationHistoryTypeEnum;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.OperationHistory;

public interface OperationHistoryRepository extends AbstractRepository<OperationHistory> {

	List<OperationHistory> find(Account account, AbstractDomain domain, ContainerQuotaType containerQuotaType, Date date, OperationHistoryTypeEnum type);

	Long sumOperationValue(Account account, AbstractDomain domain, Date creationDate, OperationHistoryTypeEnum operationType, ContainerQuotaType containerQuotaType);

	Long countOperationValue(Account account, AbstractDomain domain, Date creationDate, OperationHistoryTypeEnum operationType, ContainerQuotaType containerQuotaType);

	List<AbstractDomain> findDomainBeforeDate(Date creationDate);

	List<String> findUuidAccountBeforeDate(Date date, ContainerQuotaType containerQuotaType);

	void deleteBeforeDateByAccount(Date date, Account account);

}
