package org.linagora.linshare.core.batches.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.linagora.linshare.core.business.service.ThreadDailyStatBusinessService;
import org.linagora.linshare.core.business.service.ThreadWeeklyStatBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.AccountBatchResultContext;
import org.linagora.linshare.core.job.quartz.Context;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.ThreadService;

public class WeeklyThreadBatchImpl extends GenericBatchImpl {

	private final ThreadDailyStatBusinessService threadDailyStatBusinessService;
	private final ThreadWeeklyStatBusinessService threadWeeklyStatBusinessService;
	private final ThreadService threadService;

	public WeeklyThreadBatchImpl(final ThreadDailyStatBusinessService threadDailyStatBusinessService,
			final ThreadWeeklyStatBusinessService threadWeeklyStatBusinessService, final ThreadService threadService,
			AccountRepository<Account> accountRepository) {
		super(accountRepository);
		this.threadDailyStatBusinessService = threadDailyStatBusinessService;
		this.threadWeeklyStatBusinessService = threadWeeklyStatBusinessService;
		this.threadService = threadService;
	}

	@Override
	public List<String> getAll() {
		logger.info("WeeklyThreadBatchImpl job starting");
		List<String> threads = threadDailyStatBusinessService.findUuidAccountBetweenTwoDates(getFirstDayOfLastWeek(), getLastDayOfLastWeek());
		logger.info(threads.size() + "thread(s) have been found in ThreadDailyStat table.");
		return threads;
	}

	@Override
	public Context execute(String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		Thread resource = threadService.findByLsUuidUnprotected(identifier);
		Context context = new AccountBatchResultContext(resource);
		try {
			logInfo(total, position, "processing thread : " + resource.getAccountReprentation());
			threadWeeklyStatBusinessService.create(resource, getFirstDayOfLastWeek(), getLastDayOfLastWeek());
		} catch (BusinessException businessException) {
			logError(total, position, "Error while trying to create a ThreadWeeklyStat");
			logger.info("Error occurred while creating a weekly statistics for a thread", businessException);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to create a ThreadWeeklyStat");
			exception.setBusinessException(businessException);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(Context context, long total, long position) {
		AccountBatchResultContext threadContext = (AccountBatchResultContext) context;
		Account thread = threadContext.getResource();
		logInfo(total, position,
				"the WeeklyThreadStat for " + thread.getAccountReprentation() + " has been successfully created.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position) {
		AccountBatchResultContext context = (AccountBatchResultContext) exception.getContext();
		Account thread = context.getResource();
		logError(total, position,
				"creating WeeklyThreadStatistic has failed : " + thread.getAccountReprentation());
		logger.error("Error occured while creating WeeklyThreadStatistic for a thread "
				+ thread.getAccountReprentation() + ". BatchBusinessException ", exception);
	}

	@Override
	public void terminate(List<String> all, long errors, long unhandled_errors, long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " WeeklyThreadStatistic for thread(s) have bean created.");
		if (errors > 0) {
			logger.info(errors + "  WeeklyThreadStatistic for thread(s) failed to be created");
		}
		if (unhandled_errors > 0){
			logger.error(unhandled_errors + " WeeklyThreadStatistic failed to be created (unhandled error.)");
		}
		logger.info("WeeklyThreadBatchImpl job terminated");
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
