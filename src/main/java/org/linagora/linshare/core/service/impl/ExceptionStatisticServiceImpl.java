/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.helper.Validate;
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
	public ExceptionStatistic createExceptionStatistic(BusinessErrorCode errorCode, StackTraceElement[] stackTrace,
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
