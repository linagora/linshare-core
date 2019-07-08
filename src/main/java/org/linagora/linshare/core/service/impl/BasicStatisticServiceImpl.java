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
import java.util.stream.Collectors;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.BasicStatisticType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.service.BasicStatisticService;
import org.linagora.linshare.mongo.entities.BasicStatistic;
import org.linagora.linshare.mongo.repository.BasicStatisticMongoRepository;
import org.linagora.linshare.webservice.utils.StatisticServiceUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import com.google.common.collect.Lists;

public class BasicStatisticServiceImpl extends StatisticServiceUtils implements BasicStatisticService {

	protected BasicStatisticMongoRepository basicStatisticMongoRepository;

	protected MongoTemplate mongoTemplate;

	public BasicStatisticServiceImpl(
			BasicStatisticMongoRepository basicStatisticMongoRepository,
			MongoTemplate mongoTemplate) {
		this.basicStatisticMongoRepository = basicStatisticMongoRepository;
		this.mongoTemplate = mongoTemplate;
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

	@Override
	public Long countBasicStatistic(String domainUuid, LogAction actions, Date beginDate, Date endDate,
			AuditLogEntryType resourceType, BasicStatisticType type) {
		return basicStatisticMongoRepository.countBasicStatistic(domainUuid, actions, beginDate, endDate, resourceType,
				type);
	}

	@Override
	public List<BasicStatistic> insert(List<BasicStatistic> basicStatisticList) {
		return basicStatisticMongoRepository.insert(basicStatisticList);
	}

	@Override
	public long countValueStatisticBetweenTwoDates(User authUser, String domainUuid, List<LogAction> actions,
			String beginDate, String endDate, List<AuditLogEntryType> resourceTypes, BasicStatisticType type) {
		if (type == null) {
			type = BasicStatisticType.ONESHOT;
		}
		List<String> logActions = actions.stream().map(Enum::name).collect(Collectors.toList());
		List<String> resources = resourceTypes.stream().map(Enum::name).collect(Collectors.toList());
		Pair<Date, Date> dates = checkDatesInitialization(beginDate, endDate);
		Date bDate = dates.getLeft();
		Date eDate = dates.getRight();
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("domainUuid").is(domainUuid)
						.and("action").in(logActions)
						.and("creationDate").gte(bDate).lt(eDate)
						.and("resourceType").in(resources)
						.and("type").is(type.toString())),
						Aggregation.group().sum("value").as("value"));
		BasicStatistic results = mongoTemplate.aggregate(aggregation, "basic_statistic", BasicStatistic.class).getUniqueMappedResult();
		return results != null ? results.getValue() : 0L;
	}

	@Override
	public Long countBeforeDate(Date endDate, String domainUuid) {
		return basicStatisticMongoRepository.countBeforeDate(endDate, domainUuid);
	}

	@Override
	public Date getFirstStatisticCreationDate() {
		return basicStatisticMongoRepository.findCreationDateByOrderByIdAsc().getCreationDate();
	}
}
