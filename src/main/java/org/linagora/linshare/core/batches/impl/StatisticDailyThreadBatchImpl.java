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

import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.BatchHistoryBusinessService;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.business.service.ThreadDailyStatBusinessService;
import org.linagora.linshare.core.domain.constants.BatchType;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.AccountBatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.ThreadRepository;

public class StatisticDailyThreadBatchImpl extends GenericBatchWithHistoryImpl {

	private final ThreadRepository threadRepository;

	private final OperationHistoryBusinessService operationHistoryBusinessService;

	private final AccountQuotaBusinessService accountQuotaBusinessService;

	private final ThreadDailyStatBusinessService threadDailyStatBusinessService;

	public StatisticDailyThreadBatchImpl(
			final ThreadRepository threadRepository,
			final OperationHistoryBusinessService operationHistoryBusinessService,
			final AccountQuotaBusinessService accountQuotaBusinessService,
			final ThreadDailyStatBusinessService threadDailyStatBusinessService,
			final AccountRepository<Account> accountRepository,
			final BatchHistoryBusinessService batchHistoryBusinessService) {
		super(accountRepository, batchHistoryBusinessService);
		this.threadRepository = threadRepository;
		this.operationHistoryBusinessService = operationHistoryBusinessService;
		this.accountQuotaBusinessService = accountQuotaBusinessService;
		this.threadDailyStatBusinessService = threadDailyStatBusinessService;
	}

	@Override
	public BatchType getBatchType() {
		return BatchType.DAILY_THREAD_BATCH;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		return operationHistoryBusinessService.findUuidAccountBeforeDate(getYesterdayEnd(), ContainerQuotaType.WORK_GROUP);
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		Date yesterday = getYesterdayEnd();
		WorkGroup resource = threadRepository.findActivateAndDestroyedByLsUuid(identifier);
		if (resource == null) {
			return null;
		}
		ResultContext context = new AccountBatchResultContext(resource);
		try {
			console.logInfo(batchRunContext, total, position, "processing workgroup : " + resource.getAccountRepresentation());
			AccountQuota quota = accountQuotaBusinessService.createOrUpdate(resource, yesterday);
			threadDailyStatBusinessService.create(resource, quota.getCurrentValue(), yesterday);
			operationHistoryBusinessService.deleteBeforeDateByAccount(yesterday, resource);
			context.setProcessed(true);
		} catch (BusinessException businessException) {
			String batchClassName = this.getBatchClassName();
			String msg = "Error occured while running batch : " + batchClassName; 
			BatchBusinessException exception = new BatchBusinessException(context, msg);
			exception.setBusinessException(businessException);
			console.logError(batchRunContext, total, position,
					"Error while trying to process batch " + batchClassName + "for an user ", exception);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		AccountBatchResultContext threadContext = (AccountBatchResultContext) context;
		Account thread = threadContext.getResource();
		console.logInfo(batchRunContext, total, position, "DailyThreadStatistics was created and AccountQuota updated for " + thread.getAccountRepresentation());
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		AccountBatchResultContext context = (AccountBatchResultContext) exception.getContext();
		Account thread = context.getResource();
		console.logError(batchRunContext, total, position,
				"creating DailyThreadStatistic and AccountQuota has failed : " + thread.getAccountRepresentation());
	}
}
