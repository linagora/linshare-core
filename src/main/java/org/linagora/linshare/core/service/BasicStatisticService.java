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

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.AuditGroupLogEntryType;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.BasicStatisticType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.fields.GenericStatisticField;
import org.linagora.linshare.core.domain.entities.fields.GenericStatisticGroupByField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.mongo.entities.BasicStatistic;
import org.linagora.linshare.webservice.utils.PageContainer;

public interface BasicStatisticService {

	@Deprecated
	Set<BasicStatistic> findBetweenTwoDates(Account actor, String domainUuid, List<LogAction> actions, String beginDate,
			String endDate, List<AuditLogEntryType> resourceType, BasicStatisticType type);

	Long countBasicStatistic(String domainUuid, LogAction action, Date beginDate, Date endDate,
			AuditLogEntryType resourceType, BasicStatisticType type);

	List<BasicStatistic> insert(List<BasicStatistic> basicStatisticList);

	Long countBeforeDate(Date endDate);

	Date getFirstStatisticCreationDate();

	@Deprecated
	long countValueStatisticBetweenTwoDates(User authUser, String domainUuid, List<LogAction> actions, String beginDate,
			String endDate, List<AuditLogEntryType> resourceTypes, BasicStatisticType type);

	PageContainer<BasicStatistic> findAll(Account authUser, AbstractDomain domain, boolean includeNestedDomains,
			Optional<String> accountUuid,
			SortOrder sortOrder, GenericStatisticField sortField, BasicStatisticType statisticType,
			Set<LogAction> logActions,
			Set<AuditLogEntryType> resourceTypes,
			Set<AuditGroupLogEntryType> resourceGroups,
			boolean sum, Set<GenericStatisticGroupByField> sumBy,
			Optional<String> beginDate, Optional<String> endDate, PageContainer<BasicStatistic> container);
}
