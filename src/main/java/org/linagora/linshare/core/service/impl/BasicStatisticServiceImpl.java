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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.BasicStatisticType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.BasicStatisticService;
import org.linagora.linshare.mongo.entities.BasicStatistic;
import org.linagora.linshare.mongo.repository.BasicStatisticMongoRepository;

import com.google.common.collect.Lists;

public class BasicStatisticServiceImpl implements BasicStatisticService {

	private BasicStatisticMongoRepository basicStatisticMongoRepository;
	
	public BasicStatisticServiceImpl(
			BasicStatisticMongoRepository basicStatisticMongoRepository) {
		this.basicStatisticMongoRepository = basicStatisticMongoRepository;
	}

	@Override
	public Set<BasicStatistic> findBetweenTwoDates(Account actor, String domainUuid, List<LogAction> logActions,
			String beginDate, String endDate, List<AuditLogEntryType> resourceTypes, BasicStatisticType type) {
		Validate.notNull(actor);
		if ((logActions == null) || (logActions.isEmpty())) {
			logActions = Lists.newArrayList(LogAction.class.getEnumConstants());
		}
		if ((resourceTypes == null) || (resourceTypes.isEmpty())) {
			resourceTypes = Lists.newArrayList(AuditLogEntryType.class.getEnumConstants());
		}
		Pair<Date, Date> dates = checkDatesInitialization(beginDate, endDate);
		Date bDate = dates.getLeft();
		Date eDate = dates.getRight();
		if (type == null) {
			type = BasicStatisticType.DAILY;
		}
		return basicStatisticMongoRepository.findBetweenTwoDates(domainUuid, logActions, bDate, eDate, resourceTypes,
				type);
	}

	public Long countBasicStatistic(String domainUuid, LogAction actions, Date beginDate, Date endDate,
			AuditLogEntryType resourceType, BasicStatisticType type) {
		return basicStatisticMongoRepository.countBasicStatistic(domainUuid, actions, beginDate, endDate, resourceType,
				type);
	}

	@Override
	public List<BasicStatistic> insert(List<BasicStatistic> basicStatisticList) {
		return basicStatisticMongoRepository.insert(basicStatisticList);
	}

	private Pair<Date, Date> checkDatesInitialization(String beginDate, String endDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date bDate = null;
		Date eDate = null;
		try {
			if (endDate == null) {
				Calendar endCalendar = new GregorianCalendar();
				endCalendar.set(Calendar.HOUR_OF_DAY, 23);
				endCalendar.set(Calendar.MINUTE, 59);
				endCalendar.set(Calendar.SECOND, 59);
				endCalendar.add(Calendar.SECOND, 1);
				eDate = endCalendar.getTime();
			} else {
				eDate = formatter.parse(endDate);
			}
			if (beginDate == null) {
				Calendar cal = new GregorianCalendar();
				cal.setTime(eDate);
				cal.add(Calendar.YEAR, -1);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				bDate = cal.getTime();
			} else {
				bDate = formatter.parse(beginDate);
			}

		} catch (ParseException e) {
			throw new BusinessException(BusinessErrorCode.STATISTIC_DATE_PARSING_ERROR, "Can not parse the dates.");
		}
		return new ImmutablePair<>(bDate, eDate);
	}

	@Override
	public Long countBeforeDate(Date endDate) {
		return basicStatisticMongoRepository.countBeforeDate(endDate);
	}

	@Override
	public Date getFirstStatisticCreationDate() {
		return basicStatisticMongoRepository.findCreationDateByOrderByIdAsc().getCreationDate();
	}
}
