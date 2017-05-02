/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2016. Contribute to
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

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.BatchHistoryBusinessService;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.business.service.UserDailyStatBusinessService;
import org.linagora.linshare.core.domain.constants.BatchType;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.AccountBatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.UserService;

/**
 * For each account with activity yesterday (uploading or deleting files) :
 * yesterday = from 00:00:00 to 23:59:59.
 * - create daily statistic
 * - update account quota with the new current value of used space.
 * - delete all operation in operation_history table related to the current account.
 * This batch is only run once a day.
 */
public class StatisticDailyUserBatchImpl extends GenericBatchWithHistoryImpl {

	private final UserService userService;

	private final OperationHistoryBusinessService operationHistoryBusinessService;

	private final AccountQuotaBusinessService accountQuotaBusinessService;

	private final UserDailyStatBusinessService userDailyStatBusinessService;

	public StatisticDailyUserBatchImpl(
			final UserService userService,
			final OperationHistoryBusinessService operationHistoryBusinessService,
			final AccountQuotaBusinessService accountQuotaBusinessService,
			final UserDailyStatBusinessService userDailyStatBusinessService,
			final AccountRepository<Account> accountRepository,
			final BatchHistoryBusinessService batchHistoryBusinessService) {
		super(accountRepository, batchHistoryBusinessService);
		this.userService = userService;
		this.operationHistoryBusinessService = operationHistoryBusinessService;
		this.accountQuotaBusinessService = accountQuotaBusinessService;
		this.userDailyStatBusinessService = userDailyStatBusinessService;
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
		User resource = userService.findByLsUuid(identifier);
		ResultContext context = new AccountBatchResultContext(resource);
		try {
			logInfo(batchRunContext, total, position, "processing user : " + resource.getAccountRepresentation());
			// compute once, used three times
			Date yesterday = getYesterdayEnd();
			userDailyStatBusinessService.create(resource, yesterday);
			accountQuotaBusinessService.createOrUpdate(resource, yesterday);
			operationHistoryBusinessService.deleteBeforeDateByAccount(yesterday, resource);
		} catch (BusinessException businessException) {
			String batchClassName = this.getBatchClassName();
			logError(total, position, "Error while trying to process batch " + batchClassName + "for an user ", batchRunContext);
			String msg = "Error occured while running batch : " + batchClassName;
			logger.info(msg, businessException);
			BatchBusinessException exception = new BatchBusinessException(context, msg);
			exception.setBusinessException(businessException);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		AccountBatchResultContext userContext = (AccountBatchResultContext) context;
		Account user = userContext.getResource();
		logInfo(batchRunContext, total, position, "DailyUserStatistic was created and AccountQuota updated for " + user.getAccountRepresentation());
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		AccountBatchResultContext context = (AccountBatchResultContext) exception.getContext();
		Account user = context.getResource();
		logError(total, position,
				"creating DailyUserStatistic and AccountQuota has failed : " + user.getAccountRepresentation(), batchRunContext);
		logger.error("Error occured while creating DailyUserStatistic and UserQuota for an user "
				+ user.getAccountRepresentation() + ". BatchBusinessException ", exception);
	}

}
