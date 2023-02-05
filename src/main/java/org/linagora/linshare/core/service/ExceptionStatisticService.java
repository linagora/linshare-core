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
import java.util.Set;

import org.linagora.linshare.core.domain.constants.ExceptionStatisticType;
import org.linagora.linshare.core.domain.constants.ExceptionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.mongo.entities.ExceptionStatistic;

public interface ExceptionStatisticService {

	ExceptionStatistic createExceptionStatistic(BusinessErrorCode errorCode, String stackTrace,ExceptionType type, User authUser);

	Long countExceptionStatistic(String domainUuid, ExceptionType exceptionType, Date beginDate, Date endDate,
			ExceptionStatisticType type);

	List<ExceptionStatistic> insert(List<ExceptionStatistic> exceptionStatistics);

	Set<ExceptionStatistic> findBetweenTwoDates(Account actor, String domainUuid, String beginDate, String endDate,
			List<ExceptionType> exceptionTypes, ExceptionStatisticType type);
}
