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

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.ExceptionStatisticType;
import org.linagora.linshare.core.domain.constants.ExceptionType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ExceptionStatisticService;
import org.linagora.linshare.mongo.entities.ExceptionStatistic;
import org.linagora.linshare.mongo.repository.ExceptionStatisticMongoRepository;
import org.linagora.linshare.webservice.utils.StatisticServiceUtils;

import com.google.common.collect.Lists;

public class ExceptionStatisticServiceImpl extends StatisticServiceUtils implements ExceptionStatisticService {

	protected ExceptionStatisticMongoRepository exceptionStatisticMongoRepository;

	protected AccountService accountService;

	public ExceptionStatisticServiceImpl(ExceptionStatisticMongoRepository exceptionStatisticMongoRepository,
			AccountService accountService) {
		this.exceptionStatisticMongoRepository = exceptionStatisticMongoRepository;
		this.accountService = accountService;
	}

	@Override
	public ExceptionStatistic createExceptionStatistic(BusinessErrorCode errorCode, String stackTrace,
			ExceptionType type, User authUser) {
		Validate.notNull(type);
		String domainUuid = authUser.getDomain().getUuid();
		return exceptionStatisticMongoRepository
				.insert(new ExceptionStatistic(1L, domainUuid, getParentDomainUuid(authUser.getDomain()), errorCode, stackTrace,
						type, ExceptionStatisticType.ONESHOT));
	}

	@Override
	public List<ExceptionStatistic> insert(List<ExceptionStatistic> exceptionStatistics) {
		return exceptionStatisticMongoRepository.insert(exceptionStatistics);
	}

	@Override
	public Long countExceptionStatistic(String domainUuid, ExceptionType exceptionType, Date beginDate, Date endDate,
			ExceptionStatisticType type) {
		return exceptionStatisticMongoRepository.countExceptionStatistic(domainUuid, exceptionType, beginDate, endDate,
				type);
	}

	@Override
	public Set<ExceptionStatistic> findBetweenTwoDates(Account actor, String domainUuid, String beginDate, String endDate,
			List<ExceptionType> exceptionTypes, ExceptionStatisticType type) {
		Validate.notNull(actor);
		if ((exceptionTypes == null) || (exceptionTypes.isEmpty())) {
			exceptionTypes = Lists.newArrayList(ExceptionType.class.getEnumConstants());
		}
		Pair<Date, Date> dates = checkDatesInitialization(beginDate, endDate);
		Date bDate = dates.getLeft();
		Date eDate = dates.getRight();
		if (type == null) {
			type = ExceptionStatisticType.DAILY;
		}
		return exceptionStatisticMongoRepository.findBetweenTwoDates(domainUuid, exceptionTypes, bDate, eDate,
				type);
	}

	protected String getParentDomainUuid(AbstractDomain domain) {
		String parentDomainUuid = null;
		AbstractDomain parentDomain = domain.getParentDomain();
		if (parentDomain != null) {
			parentDomainUuid = parentDomain.getUuid();
		}
		return parentDomainUuid;
	}
}
