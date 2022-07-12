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
import org.linagora.linshare.mongo.repository.AdvancedStatisticMongoRepository;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.linagora.linshare.webservice.utils.StatisticServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

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
			Account authUser, AbstractDomain domain,
			Optional<String> accountUuid, SortOrder sortOrder,
			MimeTypeStatisticField sortField, AdvancedStatisticType statisticType,
			Optional<String> mimeType,
			Optional<String> beginDate, Optional<String> endDate,
			PageContainer<MimeTypeStatistic> container) {
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
		Query query = new Query();
		query.addCriteria(Criteria.where("domainUuid").is(domain.getUuid()));
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
