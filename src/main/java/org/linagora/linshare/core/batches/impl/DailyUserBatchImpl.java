package org.linagora.linshare.core.batches.impl;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.business.service.UserDailyStatBusinessService;
import org.linagora.linshare.core.domain.constants.EnsembleType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.AccountBatchResultContext;
import org.linagora.linshare.core.job.quartz.Context;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.UserService;

public class DailyUserBatchImpl extends GenericBatchImpl {

	private final UserService userService;
	private final OperationHistoryBusinessService operationHistoryBusinessService;
	private final AccountQuotaBusinessService accountQuotaBusinessService;
	private final UserDailyStatBusinessService userDailyStatBusinessService;

	public DailyUserBatchImpl(final UserService userService,
			final OperationHistoryBusinessService operationHistoryBusinessService,
			final AccountQuotaBusinessService accountQuotaBusinessService,
			final UserDailyStatBusinessService userDailyStatBusinessService,
			AccountRepository<Account> accountRepository) {
		super(accountRepository);
		this.userService = userService;
		this.operationHistoryBusinessService = operationHistoryBusinessService;
		this.accountQuotaBusinessService = accountQuotaBusinessService;
		this.userDailyStatBusinessService = userDailyStatBusinessService;
	}

	@Override
	public List<String> getAll() {
		logger.info("DailyUserBatchImpl job starting ...");
		List<String> users = operationHistoryBusinessService.findUuidAccountBeforeDate(getToday(), EnsembleType.USER);
		logger.info(users.size() + "user(s) have benn found in OperationHistory table");
		return users;
	}

	@Override
	public Context execute(String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		Date todate = getToday();
		User resource = userService.findByLsUuid(identifier);
		Context context = new AccountBatchResultContext(resource);
		try {
			logInfo(total, position, "processing user : " + resource.getAccountReprentation());
			userDailyStatBusinessService.create(resource, todate);
		} catch (BusinessException businessException) {
			logError(total, position, "Error while trying to create a UserDailyStat");
			logger.info("Error occured while creating a daily statistique for an user", businessException);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to create a UserDailySta");
			exception.setBusinessException(businessException);
			throw exception;
		}
		try {
			logInfo(total, position, "processing user : " + resource.getAccountReprentation());
			accountQuotaBusinessService.createOrUpdate(resource, todate);
		} catch (BusinessException businessException) {
			logError(total, position, "Error while trying to create a userQuota");
			logger.info("Error occured while creating a user quota for an user", businessException);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to create a userQuota");
			exception.setBusinessException(businessException);
			throw exception;
		}
		try {
			logInfo(total, position, "processing user : " + resource.getAccountReprentation());
			operationHistoryBusinessService.deleteBeforeDate(todate);
		} catch (BusinessException businessException) {
			logError(total, position, "Error while trying to delete operationHistory for an user");
			logger.info("Error occured while cleaning operation history for an user", businessException);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to delete operationHistory for an user");
			exception.setBusinessException(businessException);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(Context context, long total, long position) {
		AccountBatchResultContext userContext = (AccountBatchResultContext) context;
		Account user = userContext.getResource();
		logInfo(total, position, "the DailyUserStatistic and the UserQuota " + user.getAccountReprentation()
				+ " have bean successfully created");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position) {
		AccountBatchResultContext context = (AccountBatchResultContext) exception.getContext();
		Account user = context.getResource();
		logError(total, position,
				"creating DailyUserStatistic and UserQuota has failed : " + user.getAccountReprentation());
		logger.error("Error occured while creating DailyUserStatistic and UserQuota for an user "
				+ user.getAccountReprentation() + ". BatchBusinessException ", exception);
	}

	@Override
	public void terminate(List<String> all, long errors, long unhandled_errors, long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " DailyUserStatistic and UserQuota for user(s) have bean created.");
		if (errors > 0) {
			logger.info(errors + " DailyUserStatistic and UserQuota for user(s) failed to be created");
		}
		if (unhandled_errors > 0){
			logger.error(unhandled_errors + " DailyUserStatistic and UserQuota for user(s) failed to be created (unhandled error.)");
		}
		logger.info("DailyUserBatchImpl job terminated");
	}

	private Date getToday() {
		GregorianCalendar dateCalender = new GregorianCalendar();
		dateCalender.set(GregorianCalendar.HOUR, 0);
		dateCalender.set(GregorianCalendar.MINUTE, 0);
		dateCalender.set(GregorianCalendar.SECOND, 0);
		return dateCalender.getTime();
	}
}
