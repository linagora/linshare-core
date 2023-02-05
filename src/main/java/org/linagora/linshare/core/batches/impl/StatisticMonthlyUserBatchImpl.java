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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.linagora.linshare.core.business.service.BatchHistoryBusinessService;
import org.linagora.linshare.core.business.service.UserMonthlyStatBusinessService;
import org.linagora.linshare.core.business.service.UserWeeklyStatBusinessService;
import org.linagora.linshare.core.domain.constants.BatchType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.AccountBatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.UserService;

public class StatisticMonthlyUserBatchImpl extends GenericBatchWithHistoryImpl {

	private final UserWeeklyStatBusinessService userWeeklyStatBusinessService;

	private final UserMonthlyStatBusinessService userMonthlyStatBusinessService;

	private final UserService userService;

	public StatisticMonthlyUserBatchImpl(
			final UserWeeklyStatBusinessService userWeeklyStatBusinessService,
			final UserMonthlyStatBusinessService userMonthlyStatBusinessService,
			final UserService userService,
			final AccountRepository<Account> accountRepository,
			final BatchHistoryBusinessService batchHistoryBusinessService) {
		super(accountRepository, batchHistoryBusinessService);
		this.userMonthlyStatBusinessService = userMonthlyStatBusinessService;
		this.userWeeklyStatBusinessService = userWeeklyStatBusinessService;
		this.userService = userService;
	}

	@Override
	public BatchType getBatchType() {
		return BatchType.MONTHLY_USER_BATCH;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info("MonthlyUserBatchImpl job starting");
		List<String> users = userWeeklyStatBusinessService.findUuidAccountBetweenTwoDates(getFirstDayOfLastMonth(),
				getLastDayOfLastMonth());
		logger.info(users.size() + " user(s) have been found in UserWeeklyStat table.");
		return users;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		User resource = userService.findByLsUuid(identifier);
		ResultContext context = new AccountBatchResultContext(resource);
		try {
			console.logInfo(batchRunContext, total, position, "processing user : " + resource.getAccountRepresentation());
			userMonthlyStatBusinessService.create(resource, getFirstDayOfLastMonth(), getLastDayOfLastMonth());
		} catch (BusinessException businessException) {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.add(GregorianCalendar.MONTH, -1);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to create a UserMonthlyStat for user " + resource.getAccountRepresentation()
							+ " in the month "
							+ calendar.getDisplayName(GregorianCalendar.MONTH, GregorianCalendar.LONG, Locale.US));
			exception.setBusinessException(businessException);
			console.logError(batchRunContext, total, position,
					"Error while trying to create a UserMonthlyStat for user " + resource.getAccountRepresentation()
							+ " in the month "
							+ calendar.getDisplayName(GregorianCalendar.MONTH, GregorianCalendar.LONG, Locale.US), exception);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		AccountBatchResultContext userContext = (AccountBatchResultContext) context;
		Account user = userContext.getResource();
		console.logInfo(batchRunContext, total,
				position, "the MonthlyUserStat for " + user.getAccountRepresentation() + " has been successfully created.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		AccountBatchResultContext context = (AccountBatchResultContext) exception.getContext();
		Account user = context.getResource();
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.MONTH, -1);
		console.logError(batchRunContext, total, position,
				"creating MonthlyUserStatistic has failed for " + user.getAccountRepresentation() + " in the month "
						+ calendar.getDisplayName(GregorianCalendar.MONTH, GregorianCalendar.LONG, Locale.US));
	}

	@Override
	public void terminate(BatchRunContext batchRunContext, List<String> all, long errors, long unhandled_errors,
			long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " MonthlyUserStatistic for user(s) have bean created.");
		if (errors > 0) {
			logger.info(errors + "  MonthlyUserStatistic for user(s) failed to be created");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors + " MonthlyUserStatistic failed to be created (unhandled error.)");
		}
		logger.info("MonthlyUserBatchImpl job terminated");
	}

	private Date getLastDayOfLastMonth() {
		GregorianCalendar dateCalendar = new GregorianCalendar();
		dateCalendar.add(GregorianCalendar.MONTH, -1);
		int nbDay = dateCalendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		dateCalendar.set(GregorianCalendar.DATE, nbDay);
		dateCalendar.set(GregorianCalendar.HOUR_OF_DAY, 23);
		dateCalendar.set(GregorianCalendar.MINUTE, 59);
		dateCalendar.set(GregorianCalendar.SECOND, 59);
		dateCalendar.set(GregorianCalendar.MILLISECOND, 999);
		return dateCalendar.getTime();
	}

	private Date getFirstDayOfLastMonth() {
		GregorianCalendar dateCalendar = new GregorianCalendar();
		dateCalendar.add(GregorianCalendar.MONTH, -1);
		dateCalendar.set(GregorianCalendar.DATE, 1);
		dateCalendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		dateCalendar.set(GregorianCalendar.MINUTE, 0);
		dateCalendar.set(GregorianCalendar.SECOND, 0);
		dateCalendar.set(GregorianCalendar.MILLISECOND, 0);
		return dateCalendar.getTime();
	}

	private Date getFirstDayOfMonth() {
		GregorianCalendar dateCalendar = new GregorianCalendar();
		dateCalendar.set(GregorianCalendar.DATE, 1);
		dateCalendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		dateCalendar.set(GregorianCalendar.MINUTE, 0);
		dateCalendar.set(GregorianCalendar.SECOND, 0);
		dateCalendar.set(GregorianCalendar.MILLISECOND, 0);
		return dateCalendar.getTime();
	}

	@Override
	public boolean needToRun() {
		return !batchHistoryBusinessService.exist(getFirstDayOfMonth(), BatchType.MONTHLY_USER_BATCH);
	}

}
