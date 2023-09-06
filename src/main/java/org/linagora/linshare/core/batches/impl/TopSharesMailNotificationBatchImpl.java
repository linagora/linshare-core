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

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ShareRecipientStatistic;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.job.quartz.SingleRunBatchResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.ShareEntryService;

import com.google.common.collect.Lists;

public class TopSharesMailNotificationBatchImpl extends GenericBatchImpl {

	private final ShareEntryService shareEntryService;

	public static final SimpleDateFormat DATE_FORMAT_TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static final SimpleDateFormat DATE_FORMAT_DAY = new SimpleDateFormat("yyyy-MM-dd");

	public TopSharesMailNotificationBatchImpl(
			final AccountRepository<Account> accountRepository,
			final ShareEntryService shareEntryService) {
		super(accountRepository);
		this.shareEntryService = shareEntryService;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info(getClass().toString() + " job starting ...");
		//It makes no sense to fetch each resource individually instead of doing it in a single request
		//So we are sending a single "fake" resource to trigger a single run
		return Lists.newArrayList("run_only_once_top_shared_mail_notification");
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		ResultContext context = new SingleRunBatchResultContext(identifier);
		context.setProcessed(false);
		try {
			console.logInfo(batchRunContext, total, position, identifier);
			File topSharesByFileSizeCsv = toCsv("Top_shares_by_file_size_" + getYesterdayDate(),
					shareEntryService.getTopSharesByFileSize(null, getYesterdayBegin(), getYesterdayEnd()));
			File topSharesByFileCountCsv = toCsv("Top_shares_by_file_count_" + getYesterdayDate(),
					shareEntryService.getTopSharesByFileCount(null, getYesterdayBegin(), getYesterdayEnd()));

			context.setProcessed(true);
		} catch (BusinessException businessException) {
			BatchBusinessException exception = new BatchBusinessException(context, "Error while creating top shares mail notification");
			exception.setBusinessException(businessException);
			console.logError(batchRunContext, total, position, "Error while trying to create top shares mail notification", exception);
			throw exception;
		}
		return context;
	}

	private File toCsv(String filename, List<ShareRecipientStatistic> topShares) throws BusinessException {
		File csvOutputFile = new File(filename);
		try (PrintWriter writer = new PrintWriter(csvOutputFile)) {
			writer.println(ShareRecipientStatistic.getCsvHeader());
			topShares.stream()
					.map(ShareRecipientStatistic::toCsvLine)
					.forEach(writer::println);
			return csvOutputFile;
		} catch (Exception e) {
			throw new BusinessException(BusinessErrorCode.BATCH_FAILURE, "Error while writing the top shares by file size CSV", e);
		}
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		console.logInfo(batchRunContext, total, position, "Top shares mail notification have been successfully sent");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		console.logError(batchRunContext, total, position, "creating top shares mail notification : " + exception.getContext().getIdentifier());
	}

	private String getYesterdayDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(GregorianCalendar.DATE, -1);
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		calendar.set(GregorianCalendar.MINUTE, 0);
		calendar.set(GregorianCalendar.SECOND, 0);
		calendar.set(GregorianCalendar.MILLISECOND, 0);
		return DATE_FORMAT_DAY.format(calendar.getTime());
	}

	private String getYesterdayBegin() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(GregorianCalendar.DATE, -1);
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		calendar.set(GregorianCalendar.MINUTE, 0);
		calendar.set(GregorianCalendar.SECOND, 0);
		calendar.set(GregorianCalendar.MILLISECOND, 0);
		return DATE_FORMAT_TIMESTAMP.format(calendar.getTime());
	}

	private String getYesterdayEnd() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(GregorianCalendar.DATE, -1);
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 23);
		calendar.set(GregorianCalendar.MINUTE, 59);
		calendar.set(GregorianCalendar.SECOND, 59);
		calendar.set(GregorianCalendar.MILLISECOND, 999);

		return DATE_FORMAT_TIMESTAMP.format(calendar.getTime());
	}
}