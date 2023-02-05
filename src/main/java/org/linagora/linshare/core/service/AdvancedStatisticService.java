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

import java.util.Optional;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.AdvancedStatisticType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.fields.MimeTypeStatisticField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.mongo.entities.MimeTypeStatistic;
import org.linagora.linshare.webservice.utils.PageContainer;

public interface AdvancedStatisticService {

	@Deprecated
	Set<MimeTypeStatistic> findBetweenTwoDates(User authUser, String domainUuid, String beginDate, String endDate,
			String mimeType);

	PageContainer<MimeTypeStatistic> findAll(Account authUser, AbstractDomain domain, boolean includeNestedDomains,
			Optional<String> accountUuid,
			SortOrder sortOrder, MimeTypeStatisticField sortField, AdvancedStatisticType statisticType,
			Optional<String> mimeType,
			boolean sum, Optional<String> beginDate, Optional<String> endDate, PageContainer<MimeTypeStatistic> container);

}
