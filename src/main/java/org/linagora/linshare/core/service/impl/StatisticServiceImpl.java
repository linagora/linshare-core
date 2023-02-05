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
package org.linagora.linshare.core.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.business.service.StatisticBusinessService;
import org.linagora.linshare.core.domain.constants.StatisticType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Statistic;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.domain.entities.fields.StorageConsumptionStatisticField;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.StatisticService;
import org.linagora.linshare.core.service.TimeService;
import org.linagora.linshare.webservice.utils.PageContainer;

public class StatisticServiceImpl implements StatisticService {

	private final StatisticBusinessService statisticBusinessService;
	private final DomainPermissionBusinessService permissionService;
	private final TimeService timeService;

	public StatisticServiceImpl(
			StatisticBusinessService statisticBusinessService,
			TimeService timeService,
			DomainPermissionBusinessService permissionService) {
		super();
		this.statisticBusinessService = statisticBusinessService;
		this.permissionService = permissionService;
		this.timeService = timeService;
	}

	@Deprecated
	@Override
	public List<Statistic> findBetweenTwoDates(Account authUser, Account actor, AbstractDomain domain, String beginDate,
			String endDate, StatisticType statisticType) throws BusinessException {
		Validate.notNull(authUser, "authUser must be set.");
		Pair<Date, Date> dates = checkDatesInitialization(beginDate, endDate);
		Date bDate = dates.getLeft();
		Date eDate = dates.getRight();
		return statisticBusinessService.findBetweenTwoDates(authUser, domain, null, bDate, eDate, statisticType);
	}

	@Deprecated
	public Pair<Date, Date> checkDatesInitialization(String beginDate, String endDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date bDate = null;
		Date eDate = null;
		try {
			if (beginDate != null) {
				bDate = formatter.parse(beginDate);
			}
			if (endDate != null) {
				eDate = formatter.parse(endDate);
			}
		} catch (ParseException e) {
			throw new BusinessException(BusinessErrorCode.STATISTIC_DATE_PARSING_ERROR, "Can not parse the dates.");
		}
		return new ImmutablePair<>(bDate, eDate);
	}

	@Override
	public PageContainer<Statistic> findAll(
			Account authUser, AbstractDomain domain,
			boolean includeNestedDomains, Optional<String> accountUuid,
			SortOrder sortOrder, StorageConsumptionStatisticField sortField,
			StatisticType statisticType,
			Optional<String> beginDate, Optional<String> endDate,
			PageContainer<Statistic> container) {
		Validate.notNull(authUser, "authUser must be set.");
		if (!permissionService.isAdminforThisDomain(authUser, domain)) {
			throw new BusinessException(
					BusinessErrorCode.STATISTIC_FORBIDDEN,
					"You are not allowed to query this domain");
		}
		LocalDate begin = timeService.now().minusYears(1);
		LocalDate end = timeService.now();
		try {
			if (beginDate.isPresent()) {
				begin = LocalDate.parse(beginDate.get());
			}
			if (endDate.isPresent()) {
				end = LocalDate.parse(endDate.get());
			}
			// just to be sure that data from current date is included. 
			end = end.plusDays(1);
			if (end.isBefore(begin)) {
				throw new BusinessException(
					BusinessErrorCode.STATISTIC_DATE_RANGE_ERROR,
					String.format("begin date (%s) must be before end date (%s)", begin, end)
				);
			}
		} catch (DateTimeParseException e) {
			throw new BusinessException(BusinessErrorCode.STATISTIC_DATE_PARSING_ERROR, e.getMessage());
		}
		return statisticBusinessService.findAll(authUser, domain, includeNestedDomains, null, sortOrder, sortField, statisticType, begin, end, container);
	}
}
