/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
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

package org.linagora.linshare.core.batches.impl;

import java.util.List;

import org.bson.Document;
import org.linagora.linshare.core.business.service.BatchHistoryBusinessService;
import org.linagora.linshare.core.domain.constants.BatchType;
import org.linagora.linshare.core.domain.constants.ExceptionStatisticType;
import org.linagora.linshare.core.domain.constants.ExceptionType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.DomainBatchResultContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.ExceptionStatisticService;
import org.linagora.linshare.mongo.entities.ExceptionStatistic;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.google.common.collect.Lists;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;

public class ExceptionStatisticDailyBatchImpl extends GenericBatchWithHistoryImpl {

	private final AbstractDomainService abstractDomainService;

	private final ExceptionStatisticService exceptionStatisticService;

	private final MongoTemplate mongoTemplate;

	public ExceptionStatisticDailyBatchImpl(
			final AccountRepository<Account> accountRepository,
			final AbstractDomainService abstractDomainService,
			final ExceptionStatisticService exceptionStatisticService,
			final BatchHistoryBusinessService batchHistoryBusinessService,
			final MongoTemplate mongoTemplate) {
		super(accountRepository, batchHistoryBusinessService);
		this.abstractDomainService = abstractDomainService;
		this.exceptionStatisticService = exceptionStatisticService;
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public BatchType getBatchType() {
		return BatchType.DAILY_EXCEPTION_STATISTIC_BATCH;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info(getClass().toString() + " job starting ...");
		Query query = new Query();
		query.addCriteria(Criteria.where("creationDate").gte(getYesterdayBegin()).lt(getYesterdayEnd()));
		query.addCriteria(Criteria.where("type").is("ONESHOT"));
		MongoCollection<Document> exceptionStatistic = mongoTemplate.getCollection("exception_statistic");
		DistinctIterable<String> results = exceptionStatistic.distinct("domainUuid", query.getQueryObject(), String.class);
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
			AbstractDomain parentDomain = domain.getParentDomain();
			if (parentDomain != null) {
				parentDomainUuid = parentDomain.getUuid();
			}
			List<ExceptionStatistic> dailyExceptionStatisticList = Lists.newArrayList();
			for (ExceptionType type : ExceptionType.values()) {
				Long value = exceptionStatisticService.countExceptionStatistic(identifier, type, getYesterdayBegin(),
						getYesterdayEnd(), ExceptionStatisticType.ONESHOT);
				if (value != 0L) {
					dailyExceptionStatisticList.add(new ExceptionStatistic(value, identifier, parentDomainUuid, null,
							null, type, ExceptionStatisticType.DAILY));
				}
			}
			if (!dailyExceptionStatisticList.isEmpty()) {
				exceptionStatisticService.insert(dailyExceptionStatisticList);
				context.setProcessed(true);
			}
		} catch (BusinessException businessException) {
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while creating DailyExceptionStatistic");
			exception.setBusinessException(businessException);
			console.logError(batchRunContext, total, position, "Error while trying to creating DailyExceptionStatistic",
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
				"DailyDomainExceptionStatistics : " + domain.getUuid() + " have been successfully created");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		DomainBatchResultContext context = (DomainBatchResultContext) exception.getContext();
		AbstractDomain domain = context.getResource();
		console.logError(batchRunContext, total, position, "creating DailyDomainExceptionStatistic : " + domain.getUuid());
	}
}