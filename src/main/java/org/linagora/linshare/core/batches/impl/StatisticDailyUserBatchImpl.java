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
import org.linagora.linshare.core.business.service.UserDailyStatBusinessService;
import org.linagora.linshare.core.domain.constants.BatchType;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.UserRepository;

/**
 * For each account with activity yesterday (uploading or deleting files) :
 * yesterday = from 00:00:00 to 23:59:59.
 * - create daily statistic
 * - update account quota with the new current value of used space.
 * - delete all operation in operation_history table related to the current account.
 * This batch is only run once a day.
 */
public class StatisticDailyUserBatchImpl extends GenericBatchWithHistoryImpl {

	private final OperationHistoryBusinessService operationHistoryBusinessService;

	private final AccountQuotaBusinessService accountQuotaBusinessService;

	private final UserDailyStatBusinessService userDailyStatBusinessService;

	private final UserRepository<User> userRepository;

	public StatisticDailyUserBatchImpl(
			final OperationHistoryBusinessService operationHistoryBusinessService,
			final AccountQuotaBusinessService accountQuotaBusinessService,
			final UserDailyStatBusinessService userDailyStatBusinessService,
			final AccountRepository<Account> accountRepository,
			final BatchHistoryBusinessService batchHistoryBusinessService,
			final UserRepository<User> userRepository) {
		super(accountRepository, batchHistoryBusinessService);
		this.operationHistoryBusinessService = operationHistoryBusinessService;
		this.accountQuotaBusinessService = accountQuotaBusinessService;
		this.userDailyStatBusinessService = userDailyStatBusinessService;
		this.userRepository = userRepository;
	}

	@Override
	public BatchType getBatchType() {
		return BatchType.DAILY_USER_BATCH;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		return operationHistoryBusinessService.findUuidAccountBeforeDate(getYesterdayEnd(), ContainerQuotaType.USER);
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		User resource = userRepository.findActivateAndDestroyedByLsUuid(identifier);
		BatchResultContext<User> context = new BatchResultContext<>(resource);
		context.setProcessed(false);
		if (resource == null) {
			context.setIdentifier(identifier);
			return context;
		}
		try {
			console.logInfo(batchRunContext, total, position, "processing user : " + resource.getAccountRepresentation());
			// compute once, used three times
			Date yesterday = getYesterdayEnd();
			AccountQuota quota = accountQuotaBusinessService.createOrUpdate(resource, yesterday);
			userDailyStatBusinessService.create(resource, quota.getCurrentValue(), yesterday);
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
		@SuppressWarnings("unchecked")
		BatchResultContext<User> userContext = (BatchResultContext<User>) context;
		Account user = userContext.getResource();
		if (userContext.getProcessed()) {
			console.logInfo(batchRunContext, total, position,
					"DailyUserStatistic was created and AccountQuota updated for " + user.getAccountRepresentation());
		} else {
			console.logInfo(batchRunContext, total, position,
					"DailyUserStatistic was skiped because the user does not exist"
							+ userContext.getIdentifier());
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		@SuppressWarnings("rawtypes")
		BatchResultContext context = (BatchResultContext) exception.getContext();
		Account user = (Account) context.getResource();
		console.logError(batchRunContext, total, position,
				"creating DailyUserStatistic and AccountQuota has failed : " + user.getAccountRepresentation());
	}
}
