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

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.domain.constants.AdvancedStatisticType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.fields.MimeTypeStatisticField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AdvancedStatisticService;
import org.linagora.linshare.core.service.TimeService;
import org.linagora.linshare.mongo.entities.MimeTypeStatistic;
import org.linagora.linshare.mongo.projections.dto.AggregateNodeCountResult;
import org.linagora.linshare.mongo.repository.AdvancedStatisticMongoRepository;
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

public class AdvancedStatisticServiceImpl extends StatisticServiceUtils implements AdvancedStatisticService {

	protected AdvancedStatisticMongoRepository advancedStatisticMongoRepository;
	private final DomainPermissionBusinessService permissionService;
	private final TimeService timeService;
	protected final MongoTemplate mongoTemplate;

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public AdvancedStatisticServiceImpl(
			AdvancedStatisticMongoRepository advancedStatisticMongoRepository,
			DomainPermissionBusinessService permissionService,
			MongoTemplate mongoTemplate,
			TimeService timeService) {
		super();
		this.advancedStatisticMongoRepository = advancedStatisticMongoRepository;
		this.permissionService = permissionService;
		this.mongoTemplate = mongoTemplate;
		this.timeService = timeService;
	}

	@Deprecated
	@Override
	public Set<MimeTypeStatistic> findBetweenTwoDates(User authUser, String domainUuid, String beginDate,
			String endDate, String mimeType) {
		Validate.notNull(authUser);
		Pair<Date, Date> dates = checkDatesInitialization(beginDate, endDate);
		Date bDate = dates.getLeft();
		Date eDate = dates.getRight();
		return advancedStatisticMongoRepository.findBetweenTwoDates(domainUuid, bDate, eDate, mimeType);
	}

	@Override
	public PageContainer<MimeTypeStatistic> findAll(
			Account authUser,
			AbstractDomain domain, boolean includeNestedDomains,
			Optional<String> accountUuid, SortOrder sortOrder,
			MimeTypeStatisticField sortField, AdvancedStatisticType statisticType,
			Optional<String> mimeType,
			boolean sum, Optional<String> beginDate,
			Optional<String> endDate, PageContainer<MimeTypeStatistic> container) {
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
		if(sum) {
			return findAllWithSum(domain, includeNestedDomains, sortOrder, sortField, mimeType, container, begin, end);
		}
		return findAll(domain, includeNestedDomains, sortOrder, sortField, statisticType, mimeType, container, begin, end);
	}

	private PageContainer<MimeTypeStatistic> findAllWithSum(
			AbstractDomain domain, boolean includeNestedDomains,
			SortOrder sortOrder,
			MimeTypeStatisticField sortField, Optional<String> mimeType,
			PageContainer<MimeTypeStatistic> container,
			LocalDate begin, LocalDate end) {
		List<AggregationOperation> commonOperations = Lists.newArrayList();
		if (includeNestedDomains) {
			throw new BusinessException(BusinessErrorCode.NOT_IMPLEMENTED_YET, "You can not use Sum and includeNestedDomains at the same time. Not supported yet.");
//			if (!domain.isRootDomain()) {
//				commonOperations.add(
//						Aggregation.match(
//								Criteria.where("").orOperator(
//										Criteria.where("parentDomainUuid").is(domain.getUuid()),
//										Criteria.where("domainUuid").is(domain.getUuid())
//										)
//								)
//						);
//			}
		} else {
			commonOperations.add(
				Aggregation.match(
					Criteria.where("domainUuid").is(domain.getUuid())
				)
			);
		}
		commonOperations.add(
			Aggregation.match(
				Criteria.where("type").is(AdvancedStatisticType.DAILY)
					.and("creationDate").gte(begin).lt(end)
			)
		);
		if(mimeType.isPresent()) {
			commonOperations.add(Aggregation.match(Criteria.where("mimeType").is(mimeType.get())));
		}
		commonOperations.add(
			Aggregation.group(
				Fields.from(
					Fields.field("domainUuid", "domainUuid"),
					Fields.field("humanMimeType", "humanMimeType"),
					Fields.field("mimeType", "mimeType")
				)
			).sum("value").as("value").sum("totalSize").as("totalSize")
		);
		commonOperations.add(
			Aggregation.project(
				Fields.from(
					Fields.field("domainUuid", "domainUuid"),
					Fields.field("humanMimeType", "humanMimeType"),
					Fields.field("mimeType", "mimeType"),
					Fields.field("totalSize", "totalSize"),
					Fields.field("value", "value")
				)
			)
		);
		container.validateTotalPagesCount(
			count(MimeTypeStatistic.class, commonOperations)
		);
		if (sortField.equals(MimeTypeStatisticField.creationDate)) {
			commonOperations.add(Aggregation.sort(Sort.by(SortOrder.getSortDir(sortOrder), MimeTypeStatisticField.mimeType.toString())));
		} else {
			commonOperations.add(Aggregation.sort(Sort.by(SortOrder.getSortDir(sortOrder), sortField.toString())));
		}
		commonOperations.add(Aggregation.skip(Long.valueOf(container.getPageNumber() * container.getPageSize())));
		commonOperations.add(Aggregation.limit(Long.valueOf(container.getPageSize())));
		Aggregation agg = Aggregation.newAggregation(MimeTypeStatistic.class, commonOperations);
		List<MimeTypeStatistic> results = mongoTemplate.aggregate(agg, MimeTypeStatistic.class, MimeTypeStatistic.class).getMappedResults();
		Date now = timeService.dateNow();
		results.stream().forEach(a -> {
			a.setCreationDate(now);
		});
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

	private PageContainer<MimeTypeStatistic> findAll(AbstractDomain domain, boolean includeNestedDomains, SortOrder sortOrder,
			MimeTypeStatisticField sortField, AdvancedStatisticType statisticType, Optional<String> mimeType,
			PageContainer<MimeTypeStatistic> container, LocalDate begin, LocalDate end) {
		Query query = new Query();
		if (includeNestedDomains) {
			if (!domain.isRootDomain()) {
				query.addCriteria(
						Criteria.where("").orOperator(
								Criteria.where("parentDomainUuid").is(domain.getUuid()),
								Criteria.where("domainUuid").is(domain.getUuid())
						)
				);
			}
		} else {
			query.addCriteria(Criteria.where("domainUuid").is(domain.getUuid()));
		}
		query.addCriteria(Criteria.where("type").is(statisticType));
		if(mimeType.isPresent()) {
			query.addCriteria(Criteria.where("mimeType").is(mimeType.get()));
		}
		query.addCriteria(Criteria.where("creationDate").gte(begin).lt(end));
		long count = mongoTemplate.count(query, MimeTypeStatistic.class);
		logger.debug("Total of elements returned by the query without pagination: {}", count);
		if (count == 0) {
			return new PageContainer<MimeTypeStatistic>();
		}
		Pageable paging = PageRequest.of(container.getPageNumber(), container.getPageSize());
		query.with(paging);
		query.with(Sort.by(SortOrder.getSortDir(sortOrder), sortField.toString()));
		container.validateTotalPagesCount(count);
		List<MimeTypeStatistic> data = mongoTemplate.find(query, MimeTypeStatistic.class);
		return container.loadData(data);
	}

}
