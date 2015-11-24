package org.linagora.linshare.core.batches.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.linagora.linshare.core.business.service.UserDailyStatBusinessService;
import org.linagora.linshare.core.business.service.UserWeeklyStatBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.AccountBatchResultContext;
import org.linagora.linshare.core.job.quartz.Context;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.UserService;

public class WeeklyUserBatchImpl extends GenericBatchImpl {

	private final UserDailyStatBusinessService userDailyStatBusinessService;
	private final UserWeeklyStatBusinessService userWeeklyStatBusinessService;
	private final UserService userService;

	public WeeklyUserBatchImpl(final UserDailyStatBusinessService userDailyStatBusinessService,
			final UserWeeklyStatBusinessService userWeeklyStatBusinessService, final UserService userService,
			AccountRepository<Account> accountRepository) {
		super(accountRepository);
		this.userDailyStatBusinessService = userDailyStatBusinessService;
		this.userWeeklyStatBusinessService = userWeeklyStatBusinessService;
		this.userService = userService;
	}

	@Override
	public List<String> getAll() {
		logger.info("WeeklyUserBathImpl job starting");
		List<String> users = userDailyStatBusinessService.findUuidAccountBetweenTwoDates(getFirstDayOfLastWeek(),
				getLastDayOfLastWeek());
		logger.info(users.size() + "user(s) have been found in UserdailyStat table.");
		return users;
	}

	@Override
	public Context execute(String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		User resource = userService.findByLsUuid(identifier);
		Context context = new AccountBatchResultContext(resource);
		try {
			logInfo(total, position, "processing user : " + resource.getAccountReprentation());
			userWeeklyStatBusinessService.create(resource, getFirstDayOfLastWeek(), getLastDayOfLastWeek());
		} catch (BusinessException businessException) {
			logError(total, position, "Error while trying to create a UserWeeklyStat");
			logger.info("Error occurred while creating a weekly statistics for an user", businessException);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to create a UserWeeklyStat");
			exception.setBusinessException(businessException);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(Context context, long total, long position) {
		AccountBatchResultContext userContext = (AccountBatchResultContext) context;
		Account user = userContext.getResource();
		logInfo(total, position,
				"the WeeklyUserStat for " + user.getAccountReprentation() + " has been successfully created.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position) {
		AccountBatchResultContext context = (AccountBatchResultContext) exception.getContext();
		Account user = context.getResource();
		logError(total, position,
				"creating WeeklyUserStatistic has failed : " + user.getAccountReprentation());
		logger.error("Error occured while creating WeeklyUserStatistic an user "
				+ user.getAccountReprentation() + ". BatchBusinessException ", exception);
	}

	@Override
	public void terminate(List<String> all, long errors, long unhandled_errors, long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " WeeklyUserStatistic for user(s) have bean created.");
		if (errors > 0) {
			logger.info(errors + "  WeeklyUserStatistic for user(s) failed to be created");
		}
		if (unhandled_errors > 0){
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
		dateCalendar.set(GregorianCalendar.HOUR, 0);
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
		dateCalendar.set(GregorianCalendar.HOUR, 0);
		dateCalendar.set(GregorianCalendar.MINUTE, 0);
		dateCalendar.set(GregorianCalendar.SECOND, 0);
		return dateCalendar.getTime();
	}
}
