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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.linagora.linshare.core.business.service.BatchHistoryBusinessService;
import org.linagora.linshare.core.business.service.UserDailyStatBusinessService;
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

public class StatisticWeeklyUserBatchImpl extends GenericBatchImpl {

	private final UserDailyStatBusinessService userDailyStatBusinessService;

	private final UserWeeklyStatBusinessService userWeeklyStatBusinessService;

	private final UserService userService;

	private final BatchHistoryBusinessService batchHistoryBusinessService;

	public StatisticWeeklyUserBatchImpl(
			final UserDailyStatBusinessService userDailyStatBusinessService,
			final UserWeeklyStatBusinessService userWeeklyStatBusinessService,
			final UserService userService,
			final AccountRepository<Account> accountRepository,
			final BatchHistoryBusinessService batchHistoryBusinessService) {
		super(accountRepository);
		this.userDailyStatBusinessService = userDailyStatBusinessService;
		this.userWeeklyStatBusinessService = userWeeklyStatBusinessService;
		this.userService = userService;
		this.batchHistoryBusinessService = batchHistoryBusinessService;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info("WeeklyUserBathImpl job starting");
		List<String> users = userDailyStatBusinessService.findUuidAccountBetweenTwoDates(getFirstDayOfLastWeek(),
				getLastDayOfLastWeek());
		logger.info(users.size() + "user(s) have been found in UserdailyStat table.");
		return users;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		User resource = userService.findByLsUuid(identifier);
		ResultContext context = new AccountBatchResultContext(resource);
		try {
			logInfo(batchRunContext, total, position, "processing user : " + resource.getAccountRepresentation());
			userWeeklyStatBusinessService.create(resource, getFirstDayOfLastWeek(), getLastDayOfLastWeek());
		} catch (BusinessException businessException) {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.add(GregorianCalendar.DATE, -7);
			logError(total, position,
					"Error while trying to create a UserWeeklyStat for user " + resource
							.getAccountRepresentation() + " in the week "
					+ calendar.getDisplayName(GregorianCalendar.WEEK_OF_MONTH, GregorianCalendar.LONG, Locale.US), batchRunContext);
			logger.info("Error occurred while creating a weekly statistics for user "
					+ resource.getAccountRepresentation() + " in the week "
					+ calendar.getDisplayName(GregorianCalendar.WEEK_OF_MONTH, GregorianCalendar.LONG, Locale.US),
					businessException);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to create a UserWeeklyStat");
			exception.setBusinessException(businessException);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		AccountBatchResultContext userContext = (AccountBatchResultContext) context;
		Account user = userContext.getResource();
		logInfo(batchRunContext, total,
				position, "the WeeklyUserStat for " + user.getAccountRepresentation() + " has been successfully created.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		AccountBatchResultContext context = (AccountBatchResultContext) exception.getContext();
		Account user = context.getResource();
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.DATE, -7);
		logError(total, position, "creating WeeklyUserStatistic has failed for user " + user.getAccountRepresentation()
				+ calendar.getDisplayName(GregorianCalendar.WEEK_OF_MONTH, GregorianCalendar.LONG, Locale.US), batchRunContext);
		logger.error("Error occured while creating WeeklyUserStatistic for user " + user.getAccountRepresentation()
				+ " in the week "
				+ calendar.getDisplayName(GregorianCalendar.WEEK_OF_MONTH, GregorianCalendar.LONG, Locale.US)
				+ ". BatchBusinessException ", exception);
	}

	@Override
	public void terminate(BatchRunContext batchRunContext, List<String> all, long errors, long unhandled_errors,
			long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " WeeklyUserStatistic for user(s) have bean created.");
		if (errors > 0) {
			logger.info(errors + "  WeeklyUserStatistic for user(s) failed to be created");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors + " WeeklyUserStatistic failed to be created (unhandled error.)");
		}
		logger.info("WeeklyUserBatchImpl job terminated");
	}

	private Date getLastDayOfLastWeek() {
		GregorianCalendar dateCalendar = new GregorianCalendar();
		Date firstDate = getFirstDayOfLastWeek();
		dateCalendar.setTime(firstDate);
		int today = dateCalendar.get(GregorianCalendar.DAY_OF_MONTH);
		dateCalendar.set(GregorianCalendar.DAY_OF_MONTH, today + 6);
		dateCalendar.set(GregorianCalendar.HOUR_OF_DAY, 23);
		dateCalendar.set(GregorianCalendar.MINUTE, 59);
		dateCalendar.set(GregorianCalendar.SECOND, 59);
		dateCalendar.set(GregorianCalendar.MILLISECOND, 999);
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
		dateCalendar.set(GregorianCalendar.MILLISECOND, 0);
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
		dateCalendar.set(GregorianCalendar.MILLISECOND, 0);
		return dateCalendar.getTime();
	}

	@Override
	public boolean needToRun() {
		return !batchHistoryBusinessService.exist(getFirstDayOfWeek(), BatchType.WEEKLY_USER_BATCH);
	}
}
