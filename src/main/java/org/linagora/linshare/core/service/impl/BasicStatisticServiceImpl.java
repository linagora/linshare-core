/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.BasicStatisticType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.fields.GenericStatisticField;
import org.linagora.linshare.core.domain.entities.fields.GenericStatisticGroupByField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.BasicStatisticService;
import org.linagora.linshare.core.service.TimeService;
import org.linagora.linshare.mongo.entities.BasicStatistic;
import org.linagora.linshare.mongo.projections.dto.AggregateNodeCountResult;
import org.linagora.linshare.mongo.repository.BasicStatisticMongoRepository;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.linagora.linshare.webservice.utils.StatisticServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.google.common.collect.Lists;

public class BasicStatisticServiceImpl extends StatisticServiceUtils implements BasicStatisticService {

	protected BasicStatisticMongoRepository basicStatisticMongoRepository;

	protected final DomainPermissionBusinessService permissionService;

	protected final TimeService timeService;

	protected final MongoTemplate mongoTemplate;

	protected static Logger logger = LoggerFactory.getLogger(BasicStatisticServiceImpl.class);

	public BasicStatisticServiceImpl(BasicStatisticMongoRepository basicStatisticMongoRepository,
			DomainPermissionBusinessService permissionService, TimeService timeService, MongoTemplate mongoTemplate) {
		super();
		this.basicStatisticMongoRepository = basicStatisticMongoRepository;
		this.permissionService = permissionService;
		this.timeService = timeService;
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
	public Long countBeforeDate(Date endDate) {
		return basicStatisticMongoRepository.countBeforeDate(endDate);
	}

	@Override
	public Date getFirstStatisticCreationDate() {
		return basicStatisticMongoRepository.findCreationDateByOrderByIdAsc().getCreationDate();
	}

	@Override
	public PageContainer<BasicStatistic> findAll(Account authUser, AbstractDomain domain, boolean includeNestedDomains, Optional<String> accountUuid,
			SortOrder sortOrder, GenericStatisticField sortField, BasicStatisticType statisticType,
			Set<LogAction> logActions, Set<AuditLogEntryType> resourceTypes,
			boolean sum, Set<GenericStatisticGroupByField> sumBy,
			Optional<String> beginDate, Optional<String> endDate,
			PageContainer<BasicStatistic> container) {
		Validate.notNull(authUser, "authUser must be set.");
		if (!permissionService.isAdminforThisDomain(authUser, domain)) {
			throw new BusinessException(
					BusinessErrorCode.STATISTIC_READ_DOMAIN_ERROR,
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
		if(sum) {
			return findAllWithSum(domain, includeNestedDomains, sortOrder, sortField, logActions, resourceTypes, sumBy, container, begin, end);
		}
		return findAll(domain, includeNestedDomains, sortOrder, sortField, statisticType, logActions, resourceTypes, container, begin, end);
	}

	private PageContainer<BasicStatistic> findAllWithSum(
			AbstractDomain domain, boolean includeNestedDomains,
			SortOrder sortOrder, GenericStatisticField sortField,
			Set<LogAction> logActions, Set<AuditLogEntryType> resourceTypes,
			Set<GenericStatisticGroupByField> sumBy,
			PageContainer<BasicStatistic> container,
			LocalDate begin, LocalDate end) {
		List<AggregationOperation> commonOperations = Lists.newArrayList();
		if (includeNestedDomains) {
			commonOperations.add(
				Aggregation.match(
					Criteria.where("").orOperator(
							Criteria.where("parentDomainUuid").is(domain.getUuid()),
							Criteria.where("domainUuid").is(domain.getUuid())
						)
				)
			);
		} else {
			commonOperations.add(
				Aggregation.match(
					Criteria.where("domainUuid").is(domain.getUuid())
				)
			);
		}
		commonOperations.add(
				Aggregation.match(
					Criteria.where("type").is(BasicStatisticType.DAILY)
						.and("creationDate").gte(begin).lt(end)
					)
				);
		if (!logActions.isEmpty()) {
			commonOperations.add(
				Aggregation.match(
					Criteria.where("action").in(logActions)
				)
			);
		}
		if (!resourceTypes.isEmpty()) {
			commonOperations.add(
				Aggregation.match(
					Criteria.where("resourceType").in(resourceTypes)
				)
			);
		}
		/* I had to add this group by condition, even if the value is always the same.
		 * Otherwise if I have only one field, ex "action" in the Aggregation.group,
		 * only value is projected (even if we add the field to the projection.
		 * Probably a bug, because the generated query returns the proper result in a proper
		 * format when run with mongo shell.
		 * Expected result : [ { "action": "CREATE", "type": "DAILY", "value": 76087 } ]
		 *  Observed result: [ { "action": "null", "type": "DAILY", "value": 76087 } ]
		 */
		Fields fields = Fields.from(Fields.field("type", "type"));
		for (GenericStatisticGroupByField field : sumBy) {
			fields = fields.and(Fields.field(field.toString(), field.toString()));
		}
		commonOperations.add(
			Aggregation.group(fields).sum("value").as("value")
		);
		commonOperations.add(
			Aggregation.project(
					fields.and(Fields.field("value", "value"))
			)
		);
		container.validateTotalPagesCount(
			count(BasicStatistic.class, commonOperations)
		);
		commonOperations.add(Aggregation.sort(Sort.by(SortOrder.getSortDir(sortOrder), sortField.toString())));
		commonOperations.add(Aggregation.skip(Long.valueOf(container.getPageNumber() * container.getPageSize())));
		commonOperations.add(Aggregation.limit(Long.valueOf(container.getPageSize())));
		Aggregation agg = Aggregation.newAggregation(BasicStatistic.class, commonOperations);
		List<BasicStatistic> results = mongoTemplate.aggregate(agg, BasicStatistic.class, BasicStatistic.class).getMappedResults();
		return container.loadData(results);
	}

	private Long count(Class<?> type, List<AggregationOperation> operations) {
		List<AggregationOperation> aggregationOperations = Lists.newArrayList(operations);
		aggregationOperations.add(Aggregation.count().as("count"));
		Aggregation countAggregation = Aggregation.newAggregation(type, aggregationOperations);
		List<AggregateNodeCountResult> countResults = mongoTemplate.aggregate(countAggregation, type, AggregateNodeCountResult.class).getMappedResults();
		Long count = 0L;
		if (countResults.size() > 0 && Objects.nonNull(countResults.get(0) != null)) {
			count = countResults.get(0).getCount();
		}
		return count;
	}

	private PageContainer<BasicStatistic> findAll(
			AbstractDomain domain, boolean includeNestedDomains,
			SortOrder sortOrder, GenericStatisticField sortField,
			BasicStatisticType statisticType,
			Set<LogAction> logActions, Set<AuditLogEntryType> resourceTypes,
			PageContainer<BasicStatistic> container, LocalDate begin, LocalDate end) {
		Query query = new Query();
		if (includeNestedDomains) {
			query.addCriteria(
				Criteria.where("").orOperator(
					Criteria.where("parentDomainUuid").is(domain.getUuid()),
					Criteria.where("domainUuid").is(domain.getUuid())
				)
			);
		} else {
			query.addCriteria(Criteria.where("domainUuid").is(domain.getUuid()));
		}
		query.addCriteria(Criteria.where("type").is(statisticType));
		if (!logActions.isEmpty()) {
			query.addCriteria(Criteria.where("action").in(logActions));
		}
		if (!resourceTypes.isEmpty()) {
			query.addCriteria(Criteria.where("resourceType").in(resourceTypes));
		}
		query.addCriteria(Criteria.where("creationDate").gte(begin).lt(end));
		long count = mongoTemplate.count(query, BasicStatistic.class);
		logger.debug("Total of elements returned by the query without pagination: {}", count);
		if (count == 0) {
			return new PageContainer<BasicStatistic>();
		}
		Pageable paging = PageRequest.of(container.getPageNumber(), container.getPageSize());
		query.with(paging);
		query.with(Sort.by(SortOrder.getSortDir(sortOrder), sortField.toString()));
		container.validateTotalPagesCount(count);
		List<BasicStatistic> data = mongoTemplate.find(query, BasicStatistic.class);
		return container.loadData(data);
	}
}
