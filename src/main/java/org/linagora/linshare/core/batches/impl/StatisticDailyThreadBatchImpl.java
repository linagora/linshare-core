/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.BatchHistoryBusinessService;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.business.service.ThreadDailyStatBusinessService;
import org.linagora.linshare.core.domain.constants.BatchType;
import org.linagora.linshare.core.domain.constants.EnsembleType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.BatchHistory;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.AccountBatchResultContext;
import org.linagora.linshare.core.job.quartz.Context;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.ThreadService;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class StatisticDailyThreadBatchImpl extends GenericBatchImpl {

	private final ThreadService threadService;
	private final OperationHistoryBusinessService operationHistoryBusinessService;
	private final AccountQuotaBusinessService accountQuotaBusinessService;
	private final ThreadDailyStatBusinessService threadDailyStatBusinessService;
	private final BatchHistoryBusinessService batchHistoryBusinessService;

	private static final String BATCH_HISTORY_UUID = "BatchHistoryUuid";

	public StatisticDailyThreadBatchImpl(final ThreadService threadService,
			final OperationHistoryBusinessService operationHistoryBusinessService,
			final AccountQuotaBusinessService accountQuotaBusinessService,
			final ThreadDailyStatBusinessService threadDailyStatBusinessService,
			final AccountRepository<Account> accountRepository, final BatchHistoryBusinessService batchHistoryBusinessService) {
		super(accountRepository);
		this.threadService = threadService;
		this.operationHistoryBusinessService = operationHistoryBusinessService;
		this.accountQuotaBusinessService = accountQuotaBusinessService;
		this.threadDailyStatBusinessService = threadDailyStatBusinessService;
		this.batchHistoryBusinessService = batchHistoryBusinessService;
	}

	@Override
	public List<String> getAll() {
		logger.info("DailyThreadBatchImpl job starting ...");
		Map<String, List<String>> res = Maps.newHashMap();
		List<String> threads = operationHistoryBusinessService.findUuidAccountBeforeDate(yesterday(),
				EnsembleType.THREAD);
//		BatchHistory batchHistory = new BatchHistory(BatchType.DAILY_THREAD_BATCH);
//		batchHistory = batchHistoryBusinessService.create(batchHistory);
//		res.put(INPUT_LIST, threads);
//		res.put(BATCH_HISTORY_UUID, Lists.newArrayList(batchHistory.getUuid()));
//		logger.info(threads.size() + "thread(s) have been found in OperationHistory table.");
		return threads;
	}

	@Override
	public Context execute(String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		Date yesterday = yesterday();
		Thread resource = threadService.findByLsUuidUnprotected(identifier);
		Context context = new AccountBatchResultContext(resource);
		try {
			logInfo(total, position, "processing thread : " + resource.getAccountRepresentation());
			threadDailyStatBusinessService.create(resource, yesterday);
		} catch (BusinessException businessException) {
			logError(total, position, "Error while trying to create a threadDailyStat.");
			logger.info("Error occured while creating a daily statistics for a thread.", businessException);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to create a ThreadDailyStat.");
			exception.setBusinessException(businessException);
			throw exception;
		}
		try {
			logInfo(total, position, "processing thread : " + resource.getAccountRepresentation());
			accountQuotaBusinessService.createOrUpdate(resource, yesterday);
		} catch (BusinessException businessException) {
			logError(total, position, "Error while trying to update or create a threadQuota.");
			logger.info("Error occurred while updating or creating a thread quota.", businessException);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to update or create a ThreadQuota.");
			exception.setBusinessException(businessException);
			throw exception;
		}
		try {
			logInfo(total, position, "processing thread : " + resource.getAccountRepresentation());
			operationHistoryBusinessService.deleteBeforeDateByAccount(yesterday, resource);
		} catch (BusinessException businessException) {
			logError(total, position, "Error while trying to delete operationHistory for a thread.");
			logger.info("Error occurred while cleaning operation history dor a thread.", businessException);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to delete operationHistory for a thread.");
			exception.setBusinessException(businessException);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(Context context, long total, long position) {
		AccountBatchResultContext threadContext = (AccountBatchResultContext) context;
		Account thread = threadContext.getResource();
		logInfo(total, position, "the DailyThreadStatistics and the ThreadQuota for " + thread.getAccountRepresentation()
				+ " have been successfully created.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position) {
		AccountBatchResultContext context = (AccountBatchResultContext) exception.getContext();
		Account thread = context.getResource();
		logError(total, position,
				"creating DailyThreadStatistic and threadQuota has failed : " + thread.getAccountRepresentation());
		logger.error("Error occured while creating DailyThreadStatisticand ThreadQuota for a thread "
				+ thread.getAccountRepresentation() + ". BatchBusinessException ", exception);
	}

	@Override
	public void terminate(List<String> all, long errors, long unhandled_errors, long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " DailyThreadStatistic and ThreadQuota for thread(s) have bean created.");
		if (errors > 0) {
			logger.info(errors + " DailyThreadStatistic and ThreadQuota for thread(s) failed to be created");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors
					+ " DailyThreadStatistic and ThreadQuota for thread(s) failed to be created (unhandled error.)");
		}

//		BatchHistory batchHistory = batchHistoryBusinessService.findByUuid(context.get(BATCH_HISTORY_UUID).get(0));
//		if (batchHistory != null) {
//			batchHistory.setStatus("terminated");
//			batchHistory.setErrors(errors);
//			batchHistory.setUnhandledErrors(unhandled_errors);
//			batchHistory = batchHistoryBusinessService.update(batchHistory);
//		}
		logger.info("DailyThreadBatchImpl job terminated");
	}

	private Date yesterday() {
		GregorianCalendar dateCalender = new GregorianCalendar();
		dateCalender.add(GregorianCalendar.DATE, -1);
		dateCalender.set(GregorianCalendar.HOUR_OF_DAY, 23);
		dateCalender.set(GregorianCalendar.MINUTE, 59);
		dateCalender.set(GregorianCalendar.SECOND, 59);
		return dateCalender.getTime();
	}

	@Override
	public boolean needToRun() {
		return !batchHistoryBusinessService.exist(today(), new Date(), BatchType.DAILY_THREAD_BATCH);
	}

	private Date today() {
		GregorianCalendar dateCalendar = new GregorianCalendar();
		dateCalendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		dateCalendar.set(GregorianCalendar.MINUTE, 0);
		dateCalendar.set(GregorianCalendar.SECOND, 0);
		return dateCalendar.getTime();
	}
}
