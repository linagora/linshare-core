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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.linagora.linshare.core.business.service.BatchHistoryBusinessService;
import org.linagora.linshare.core.domain.constants.BatchType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.BatchHistory;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.repository.AccountRepository;

public abstract class GenericBatchWithHistoryImpl extends GenericBatchImpl {

	protected BatchHistoryBusinessService batchHistoryBusinessService;

	public abstract BatchType getBatchType();

	public GenericBatchWithHistoryImpl(AccountRepository<Account> accountRepository, BatchHistoryBusinessService batchHistoryBusinessService) {
		super(accountRepository);
		this.batchHistoryBusinessService = batchHistoryBusinessService;
	}

	@Override
	public boolean needToRun() {
		return !batchHistoryBusinessService.exist(getTodayBegin(), getBatchType());
	}

	@Override
	public void start(BatchRunContext batchRunContext) {
		super.start(batchRunContext);
		batchHistoryBusinessService.create(new BatchHistory(getBatchType()));
	}

	@Override
	public void terminate(BatchRunContext batchRunContext, List<String> all, long errors, long unhandled_errors, long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info("{} resources  have bean processed.", success);
		if (errors > 0) {
			logger.info("There were {} error (s) durring the batch processing." , errors);
		}
		if (unhandled_errors > 0) {
			logger.error("There were {} at least one unhandled error durring the batch processing whoich stop it.");
		}
		BatchHistory batchHistory = batchHistoryBusinessService.findByBatchType(getTodayBegin(), null, getBatchType());
		if (batchHistory != null) {
			batchHistory.setStatus("OK");
			batchHistoryBusinessService.update(batchHistory);
		}
		logger.info("Job terminated.");
	}

	@Override
	public void fail(BatchRunContext batchRunContext, List<String> all, long errors, long unhandled_errors, long total, long processed) {
		BatchHistory batchHistory = batchHistoryBusinessService.findByBatchType(getTodayBegin(), null, getBatchType());
		if (batchHistory != null) {
			batchHistory.setStatus("FAILED");
			batchHistoryBusinessService.update(batchHistory);
		}
		super.fail(batchRunContext, all, errors, unhandled_errors, total, processed);
	}

	protected  Date getYesterdayBegin() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(GregorianCalendar.DATE, -1);
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		calendar.set(GregorianCalendar.MINUTE, 0);
		calendar.set(GregorianCalendar.SECOND, 0);
		calendar.set(GregorianCalendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	protected Date getYesterdayEnd() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(GregorianCalendar.DATE, -1);
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 23);
		calendar.set(GregorianCalendar.MINUTE, 59);
		calendar.set(GregorianCalendar.SECOND, 59);
		calendar.set(GregorianCalendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	protected Date getTodayBegin() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		calendar.set(GregorianCalendar.MINUTE, 0);
		calendar.set(GregorianCalendar.SECOND, 0);
		calendar.set(GregorianCalendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	protected Date getTodayEnd() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 23);
		calendar.set(GregorianCalendar.MINUTE, 59);
		calendar.set(GregorianCalendar.SECOND, 59);
		calendar.set(GregorianCalendar.MILLISECOND, 999);
		return calendar.getTime();
	}
}
