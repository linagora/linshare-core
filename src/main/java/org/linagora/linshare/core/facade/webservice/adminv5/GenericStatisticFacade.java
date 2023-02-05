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
package org.linagora.linshare.core.facade.webservice.adminv5;

import java.util.Optional;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.AuditGroupLogEntryType;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.BasicStatisticType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.fields.GenericStatisticField;
import org.linagora.linshare.core.domain.entities.fields.GenericStatisticGroupByField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.mongo.entities.BasicStatistic;
import org.linagora.linshare.webservice.utils.PageContainer;

public interface GenericStatisticFacade {

	PageContainer<BasicStatistic> findAll(
		String domainUuid, Boolean includeNestedDomains,
		SortOrder sortOrder,
		GenericStatisticField sortField, BasicStatisticType statisticType,
		Set<LogAction> logActions, Set<AuditLogEntryType> resourceTypes,
		Set<AuditGroupLogEntryType> resourceGroups,
		boolean sum, Set<GenericStatisticGroupByField> sumBy,
		Optional<String> beginDate, Optional<String> endDate,
		Integer pageNumber, Integer pageSize);
}
