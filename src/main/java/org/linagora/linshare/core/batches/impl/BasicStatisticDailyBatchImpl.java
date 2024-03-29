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
package org.linagora.linshare.core.batches.impl;

import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.linagora.linshare.core.business.service.BatchHistoryBusinessService;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.BasicStatisticType;
import org.linagora.linshare.core.domain.constants.BatchType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.DomainBatchResultContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.BasicStatisticService;
import org.linagora.linshare.mongo.entities.BasicStatistic;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.google.common.collect.Lists;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;

public class BasicStatisticDailyBatchImpl extends GenericBatchWithHistoryImpl {

	private final AbstractDomainService abstractDomainService;

	private final BasicStatisticService basicStatisticService;

	private final DomainBusinessService domainBusinessService;

	private final MongoTemplate mongoTemplate;

	public BasicStatisticDailyBatchImpl(
			final AccountRepository<Account> accountRepository,
			final AbstractDomainService abstractDomainService,
			final BasicStatisticService basicStatisticService,
			final DomainBusinessService domainBusinessService,
			final BatchHistoryBusinessService batchHistoryBusinessService,
			final MongoTemplate mongoTemplate) {
		super(accountRepository, batchHistoryBusinessService);
		this.abstractDomainService = abstractDomainService;
		this.basicStatisticService = basicStatisticService;
		this.domainBusinessService = domainBusinessService;
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public BatchType getBatchType() {
		return BatchType.DAILY_BASIC_STATISTIC_BATCH;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info(getClass().toString() + " job starting ...");
		Query query = new Query();
		query.addCriteria(Criteria.where("creationDate").gte(getYesterdayBegin()).lt(getYesterdayEnd()));
		query.addCriteria(Criteria.where("type").is("ONESHOT"));
		MongoCollection<Document> basicStatistic = mongoTemplate.getCollection("basic_statistic");
		DistinctIterable<String> results = basicStatistic.distinct("domainUuid", query.getQueryObject(), String.class);
		return Lists.newArrayList(results);
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		AbstractDomain domain = abstractDomainService.findById(identifier);
		ResultContext context = new DomainBatchResultContext(domain);
		context.setProcessed(false);
		try {
			console.logInfo(batchRunContext, total, position, "processing domain : " + domain.toString());
			String parentDomainUuid = null;
			AbstractDomain parentDomain = domainBusinessService.findById(identifier).getParentDomain();
			if (parentDomain != null) {
				parentDomainUuid = parentDomain.getUuid();
			}
			List<BasicStatistic> dailyBasicStatisticList = Lists.newArrayList();
			for (AuditLogEntryType resourceType : AuditLogEntryType.values()) {
				for (LogAction action : LogAction.values()) {
					Long value = basicStatisticService.countBasicStatistic(identifier, action, getYesterdayBegin(),
							getYesterdayEnd(), resourceType, BasicStatisticType.ONESHOT);
					if (value != 0L) {
						dailyBasicStatisticList.add(new BasicStatistic(value, identifier, parentDomainUuid, action,
								new Date(), resourceType, BasicStatisticType.DAILY));
					}
				}
			}
			if (!dailyBasicStatisticList.isEmpty()) {
				basicStatisticService.insert(dailyBasicStatisticList);
				context.setProcessed(true);
			}
		} catch (BusinessException businessException) {
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while creating DailybasicStatistic");
			exception.setBusinessException(businessException);
			console.logError(batchRunContext, total, position, "Error while trying to creating DailybasicStatistic",
					exception);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		DomainBatchResultContext domainContext = (DomainBatchResultContext) context;
		AbstractDomain domain = domainContext.getResource();
		console.logInfo(batchRunContext, total, position,
				"DailyDomainBasicStatistics : " + domain.getUuid() + " have been successfully created");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		DomainBatchResultContext context = (DomainBatchResultContext) exception.getContext();
		AbstractDomain domain = context.getResource();
		console.logError(batchRunContext, total, position, "creating DailyDomainBasicStatistic : " + domain.getUuid());
	}
}
