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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.linagora.linshare.core.business.service.BatchHistoryBusinessService;
import org.linagora.linshare.core.business.service.DomainDailyStatBusinessService;
import org.linagora.linshare.core.business.service.DomainWeeklyStatBusinessService;
import org.linagora.linshare.core.domain.constants.BatchType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.BatchHistory;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.Context;
import org.linagora.linshare.core.job.quartz.DomainBatchResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.AbstractDomainService;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class StatisticWeeklyDomainBatchImpl extends GenericBatchImpl {

	private final DomainDailyStatBusinessService domainDailyStatBusinessService;
	private final DomainWeeklyStatBusinessService domainWeeklyStatBusinessService;
	private final AbstractDomainService abstractDomainService;
	private final BatchHistoryBusinessService batchHistoryBusinessService;

	private static final String BATCH_HISTORY_UUID = "BatchHistoryUuid";

	public StatisticWeeklyDomainBatchImpl(final DomainDailyStatBusinessService domainDailyStatBusinessService,
			final DomainWeeklyStatBusinessService domainWeeklyStatBusinessService,
			final AbstractDomainService abstractDomainService, AccountRepository<Account> accountRepository,
			final BatchHistoryBusinessService batchHistoryBusinessService) {
		super(accountRepository);
		this.domainDailyStatBusinessService = domainDailyStatBusinessService;
		this.domainWeeklyStatBusinessService = domainWeeklyStatBusinessService;
		this.abstractDomainService = abstractDomainService;
		this.batchHistoryBusinessService = batchHistoryBusinessService;
	}

	@Override
	public List<String> getAll() {
		logger.info("WeeklyDomainBatchImpl job starting");
		List<String> domains = domainDailyStatBusinessService
				.findIdentifierDomainBetweenTwoDates(getFirstDayOfLastWeek(), getLastDayOfLastWeek());
		logger.info(domains.size() + "domain(s) have been found in DomainDailyStat table.");
//		BatchHistory batchHistory = new BatchHistory(BatchType.WEEKLY_DOMAIN_BATCH);
//		batchHistory = batchHistoryBusinessService.create(batchHistory);
//		Map<String, List<String>> res = Maps.newHashMap();
//		res.put(INPUT_LIST, domains);
//		res.put(BATCH_HISTORY_UUID, Lists.newArrayList(batchHistory.getUuid()));
		return domains;
	}

	@Override
	public Context execute(String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		AbstractDomain resource = abstractDomainService.findById(identifier);
		Context context = new DomainBatchResultContext(resource);
		try {
			logInfo(total, position, "processing domain : " + resource.getDescription());
			domainWeeklyStatBusinessService.create(resource, getFirstDayOfLastWeek(), getLastDayOfLastWeek());
		} catch (BusinessException businessException) {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.add(GregorianCalendar.DATE, -7);
			logError(total, position,
					"Error while trying to create a DomainWeeklyStat for domain " + resource
							.getDescription() + " in the week "
					+ calendar.getDisplayName(GregorianCalendar.WEEK_OF_MONTH, GregorianCalendar.LONG, Locale.US));
			logger.info("Error occurred while creating a weekly statistics for domain " + resource.getDescription()
					+ " in the week "
					+ calendar.getDisplayName(GregorianCalendar.WEEK_OF_MONTH, GregorianCalendar.LONG, Locale.US),
					businessException);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to create a DomainWeeklyStat for domain " + resource.getDescription()
							+ " in the week " + calendar.getDisplayName(GregorianCalendar.WEEK_OF_MONTH,
									GregorianCalendar.LONG, Locale.US));
			exception.setBusinessException(businessException);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(Context context, long total, long position) {
		DomainBatchResultContext domainContext = (DomainBatchResultContext) context;
		AbstractDomain domain = domainContext.getResource();
		logInfo(total, position,
				"the WeeklyDomainStat for " + domain.getDescription() + "has been successfully created.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position) {
		DomainBatchResultContext domainContext = (DomainBatchResultContext) exception.getContext();
		AbstractDomain domain = domainContext.getResource();
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.DATE, -7);
		logError(total, position,
				"creating WeeklyDomainStatistic has failed for domain " + domain.getDescription() + " in the week "
						+ calendar.getDisplayName(GregorianCalendar.WEEK_OF_MONTH, GregorianCalendar.LONG, Locale.US));
		logger.error("Error occured while creating WeeklyDomainStatistic for domain " + domain.getDescription()
				+ " in the week "
				+ calendar.getDisplayName(GregorianCalendar.WEEK_OF_MONTH, GregorianCalendar.LONG, Locale.US)
				+ ". BatchBudinessException ", exception);
	}

	@Override
	public void terminate(List<String> all, long errors, long unhandled_errors, long total,
			long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " WeeklyDomainStatistic for domain(s) have bean created.");
		if (errors > 0) {
			logger.info(errors + "  WeeklyDomainStatistic for domain(s) failed to be created");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors + " WeeklyDomainStatistic failed to be created (unhandled error.)");
		}
//		BatchHistory batchHistory = batchHistoryBusinessService.findByUuid(context.get(BATCH_HISTORY_UUID).get(0));
//		if (batchHistory != null) {
//			batchHistory.setStatus("terminated");
//			batchHistory.setErrors(errors);
//			batchHistory.setUnhandledErrors(unhandled_errors);
//			batchHistory = batchHistoryBusinessService.update(batchHistory);
//		}
		logger.info("WeeklyDomainBatchImpl job terminated");
	}

	private Date getLastDayOfLastWeek() {
		GregorianCalendar dateCalendar = new GregorianCalendar();
		Date firstDate = getFirstDayOfLastWeek();
		dateCalendar.setTime(firstDate);
		int today = dateCalendar.get(GregorianCalendar.DAY_OF_MONTH);
		dateCalendar.set(GregorianCalendar.DAY_OF_MONTH, today + 6);
		dateCalendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		dateCalendar.set(GregorianCalendar.MINUTE, 0);
		dateCalendar.set(GregorianCalendar.SECOND, 0);
		return dateCalendar.getTime();
	}

	private Date getFirstDayOfLastWeek() {
		GregorianCalendar dateCalendar = new GregorianCalendar();
		dateCalendar.add(Calendar.DATE, -7);
		int firstDay = dateCalendar.getFirstDayOfWeek();
		if (firstDay == GregorianCalendar.SUNDAY)
			firstDay++;
		else {
			if (firstDay != GregorianCalendar.MONDAY)
				throw new BusinessException("the used local can not be supported");
		}
		dateCalendar.set(GregorianCalendar.DAY_OF_WEEK, firstDay);
		dateCalendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		dateCalendar.set(GregorianCalendar.MINUTE, 0);
		dateCalendar.set(GregorianCalendar.SECOND, 0);
		return dateCalendar.getTime();
	}

	private Date getFirstDayOfWeek() {
		GregorianCalendar dateCalendar = new GregorianCalendar();
		int firstDay = dateCalendar.getFirstDayOfWeek();
		if (firstDay == GregorianCalendar.SUNDAY)
			firstDay++;
		else {
			if (firstDay != GregorianCalendar.MONDAY)
				throw new BusinessException("the used local can not be supported");
		}
		dateCalendar.set(GregorianCalendar.DAY_OF_WEEK, firstDay);
		dateCalendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		dateCalendar.set(GregorianCalendar.MINUTE, 0);
		dateCalendar.set(GregorianCalendar.SECOND, 0);
		return dateCalendar.getTime();
	}

	@Override
	public boolean needToRun() {
		return !batchHistoryBusinessService.exist(getFirstDayOfWeek(), new Date(), BatchType.WEEKLY_DOMAIN_BATCH);
	}
}
