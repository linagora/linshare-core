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
import org.linagora.linshare.core.business.service.ThreadMonthlyStatBusinessService;
import org.linagora.linshare.core.business.service.ThreadWeeklyStatisticBusinessService;
import org.linagora.linshare.core.domain.constants.BatchType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.AccountBatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.ThreadService;

public class StatisticMonthlyThreadBatchImpl extends GenericBatchWithHistoryImpl {

	private final ThreadWeeklyStatisticBusinessService threadWeeklyStatBusinessService;

	private final ThreadMonthlyStatBusinessService threadMonthlyStatBusinessService;

	private final ThreadService threadService;

	public StatisticMonthlyThreadBatchImpl(
			final ThreadWeeklyStatisticBusinessService threadWeeklyStatBusinessService,
			final ThreadMonthlyStatBusinessService threadMonthlyStatBusinessService,
			final ThreadService threadService,
			final AccountRepository<Account> accountRepository,
			final BatchHistoryBusinessService batchHistoryBusinessService) {
		super(accountRepository, batchHistoryBusinessService);
		this.threadMonthlyStatBusinessService = threadMonthlyStatBusinessService;
		this.threadWeeklyStatBusinessService = threadWeeklyStatBusinessService;
		this.threadService = threadService;
	}

	@Override
	public BatchType getBatchType() {
		return BatchType.MONTHLY_THREAD_BATCH;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info("MonthlyThreadBatchImpl job starting.");
		List<String> threads = threadWeeklyStatBusinessService.findUuidAccountBetweenTwoDates(getFirstDayOfLastMonth(),
				getLastDayOfLastMonth());
		logger.info(threads.size() + "thread(s) have been found in ThreadWeeklyStat table.");
		return threads;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		WorkGroup resource = threadService.findByLsUuidUnprotected(identifier);
		ResultContext context = new AccountBatchResultContext(resource);
		try {
			console.logInfo(batchRunContext, total, position, "processing thread : " + resource.getAccountRepresentation());
			threadMonthlyStatBusinessService.create(resource, getFirstDayOfLastMonth(), getLastDayOfLastMonth());
		} catch (BusinessException businessException) {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.add(GregorianCalendar.MONTH, -1);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to create a ThreadMonthlyStat");
			exception.setBusinessException(businessException);
			console.logError(batchRunContext, total, position,
					"Error while trying to create a ThreadMonthlyStat for Thread " + resource.getAccountRepresentation()
							+ " in the month "
							+ calendar.getDisplayName(GregorianCalendar.MONTH, GregorianCalendar.LONG, Locale.US), exception);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		AccountBatchResultContext threadContext = (AccountBatchResultContext) context;
		Account thread = threadContext.getResource();
		console.logInfo(batchRunContext, total,
				position, "the MonthlyThreadStat for " + thread.getAccountRepresentation() + " has been successfully created.");

	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		AccountBatchResultContext context = (AccountBatchResultContext) exception.getContext();
		Account thread = context.getResource();
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.MONTH, -1);
		console.logError(batchRunContext, total, position,
				"creating MonthlyThreadStatistic has failed for " + thread.getAccountRepresentation() + " in the month "
						+ calendar.getDisplayName(GregorianCalendar.MONTH, GregorianCalendar.LONG, Locale.US));
	}

	@Override
	public void terminate(BatchRunContext batchRunContext, List<String> all, long errors, long unhandled_errors,
			long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " MonthlyThreadStatistic for thread(s) have bean created.");
		if (errors > 0) {
			logger.info(errors + "  MonthlyThreadStatistic for thread(s) failed to be created");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors + " MonthlyThreadStatistic failed to be created (unhandled error.)");
		}
		logger.info("MonthlyThreadBatchImpl job terminated");
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
		return !batchHistoryBusinessService.exist(getFirstDayOfMonth(), BatchType.MONTHLY_THREAD_BATCH);
	}

}
