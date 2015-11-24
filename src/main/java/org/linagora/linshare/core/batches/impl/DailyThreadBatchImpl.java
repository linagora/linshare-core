package org.linagora.linshare.core.batches.impl;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.business.service.ThreadDailyStatBusinessService;
import org.linagora.linshare.core.domain.constants.EnsembleType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.AccountBatchResultContext;
import org.linagora.linshare.core.job.quartz.Context;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.ThreadService;

public class DailyThreadBatchImpl extends GenericBatchImpl {

	private final ThreadService threadService;
	private OperationHistoryBusinessService operationHistoryBusinessService;
	private AccountQuotaBusinessService accountQuotaBusinessService;
	private ThreadDailyStatBusinessService threadDailyStatBusinessService;

	public DailyThreadBatchImpl(final ThreadService threadService,
			final OperationHistoryBusinessService operationHistoryBusinessService,
			final AccountQuotaBusinessService accountQuotaBusinessService,
			final ThreadDailyStatBusinessService threadDailyStatBusinessService,
			AccountRepository<Account> accountRepository) {
		super(accountRepository);
		this.threadService = threadService;
		this.operationHistoryBusinessService = operationHistoryBusinessService;
		this.accountQuotaBusinessService = accountQuotaBusinessService;
		this.threadDailyStatBusinessService = threadDailyStatBusinessService;
	}

	@Override
	public List<String> getAll() {
		logger.info("DailyThreadBatchImpl job starting ...");
		List<String> threads = operationHistoryBusinessService.findUuidAccountBeforeDate(yesterday(),
				EnsembleType.THREAD);
		logger.info(threads.size() + "thread(s) have been found in OperationHistory table.");
		return threads;
	}

	@Override
	public Context execute(String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		Date yseterday = yesterday();
		Thread resource = threadService.findByLsUuidUnprotected(identifier);
		Context context = new AccountBatchResultContext(resource);
		try {
			logInfo(total, position, "processing user : " + resource.getAccountReprentation());
			threadDailyStatBusinessService.create(resource, yseterday);
		} catch (BusinessException businessException) {
			logError(total, position, "Error while trying to create a threadDailyStat.");
			logger.info("Error occured while creating a daily statistics for a thread.", businessException);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to create a ThreadDailyStat.");
			exception.setBusinessException(businessException);
			throw exception;
		}
		try {
			logInfo(total, position, "processing user : " + resource.getAccountReprentation());
			accountQuotaBusinessService.createOrUpdate(resource, yseterday);
		} catch (BusinessException businessException) {
			logError(total, position, "Error while trying to update or create a threadQuota.");
			logger.info("Error occurred while update or create a thread quota.", businessException);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to update or create a ThreadQuota.");
			exception.setBusinessException(businessException);
			throw exception;
		}
		try {
			logInfo(total, position, "processing user : " + resource.getAccountReprentation());
			operationHistoryBusinessService.deleteBeforeDateByAccount(yseterday, resource);
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
		logInfo(total, position, "the DailyThreadStatistics and the ThreadQuota for " + thread.getAccountReprentation()
				+ " have been successfully created.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position) {
		AccountBatchResultContext context = (AccountBatchResultContext) exception.getContext();
		Account thread = context.getResource();
		logError(total, position,
				"creating DailyThreadStatistic and threadQuota has failed : " + thread.getAccountReprentation());
		logger.error("Error occured while creating DailyThreadStatisticand ThreadQuota for a thread "
				+ thread.getAccountReprentation() + ". BatchBusinessException ", exception);
	}

	@Override
	public void terminate(List<String> all, long errors, long unhandled_errors, long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " DailyThreadStatistic and ThreadQuota for thread(s) have bean created.");
		if (errors > 0) {
			logger.info(errors + " DailyThreadStatistic and ThreadQuota for thread(s) failed to be created");
		}
		if (unhandled_errors > 0){
			logger.error(unhandled_errors + " DailyThreadStatistic and ThreadQuota for thread(s) failed to be created (unhandled error.)");
		}
		logger.info("DailyThreadBatchImpl job terminated");
	}

	private Date yesterday() {
		GregorianCalendar dateCalender = new GregorianCalendar();
		dateCalender.add(GregorianCalendar.DATE, -1);
		dateCalender.set(GregorianCalendar.HOUR, 23);
		dateCalender.set(GregorianCalendar.MINUTE, 59);
		dateCalender.set(GregorianCalendar.SECOND, 59);
		return dateCalender.getTime();
	}
}
