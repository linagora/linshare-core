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

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.BatchHistoryBusinessService;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.business.service.UserDailyStatBusinessService;
import org.linagora.linshare.core.domain.constants.BatchType;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.AccountBatchResultContext;
import org.linagora.linshare.core.job.quartz.Context;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.UserService;

public class StatisticDailyUserBatchImpl extends GenericBatchImpl {

	private final UserService userService;

	private final OperationHistoryBusinessService operationHistoryBusinessService;

	private final AccountQuotaBusinessService accountQuotaBusinessService;

	private final UserDailyStatBusinessService userDailyStatBusinessService;

	private final BatchHistoryBusinessService batchHistoryBusinessService;

	public StatisticDailyUserBatchImpl(
			final UserService userService,
			final OperationHistoryBusinessService operationHistoryBusinessService,
			final AccountQuotaBusinessService accountQuotaBusinessService,
			final UserDailyStatBusinessService userDailyStatBusinessService,
			final AccountRepository<Account> accountRepository,
			final BatchHistoryBusinessService batchHistoryBusinessService) {
		super(accountRepository);
		this.userService = userService;
		this.operationHistoryBusinessService = operationHistoryBusinessService;
		this.accountQuotaBusinessService = accountQuotaBusinessService;
		this.userDailyStatBusinessService = userDailyStatBusinessService;
		this.batchHistoryBusinessService = batchHistoryBusinessService;
	}

	@Override
	public List<String> getAll() {
		logger.info("DailyUserBatchImpl job starting ...");
		List<String> users = operationHistoryBusinessService.findUuidAccountBeforeDate(yesterday(), ContainerQuotaType.USER);
		logger.info(users.size() + " user(s) have been found in OperationHistory table.");
		return users;
	}

	@Override
	public Context execute(String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		Date yesterday = yesterday();
		User resource = userService.findByLsUuid(identifier);
		Context context = new AccountBatchResultContext(resource);
		try {
			logInfo(total, position, "processing user : " + resource.getAccountRepresentation());
			userDailyStatBusinessService.create(resource, yesterday);
		} catch (BusinessException businessException) {
			logError(total, position, "Error while trying to create a UserDailyStat");
			logger.info("Error occured while creating a daily statistics for an user", businessException);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to create a UserDailyStat");
			exception.setBusinessException(businessException);
			throw exception;
		}
		try {
			logInfo(total, position, "processing user : " + resource.getAccountRepresentation());
			accountQuotaBusinessService.createOrUpdate(resource, yesterday);
		} catch (BusinessException businessException) {
			logError(total, position, "Error while trying to update or create userQuota");
			logger.info("Error occured while updating or creating an user quota for user", businessException);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to update or create a userQuota");
			exception.setBusinessException(businessException);
			throw exception;
		}
		try {
			logInfo(total, position, "processing user : " + resource.getAccountRepresentation());
			operationHistoryBusinessService.deleteBeforeDateByAccount(yesterday, resource);
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
		logInfo(total, position, "the DailyUserStatistic and the UserQuota for " + user.getAccountRepresentation()
				+ " have been successfully created");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position) {
		AccountBatchResultContext context = (AccountBatchResultContext) exception.getContext();
		Account user = context.getResource();
		logError(total, position,
				"creating DailyUserStatistic and UserQuota has failed : " + user.getAccountRepresentation());
		logger.error("Error occured while creating DailyUserStatistic and UserQuota for an user "
				+ user.getAccountRepresentation() + ". BatchBusinessException ", exception);
	}

	@Override
	public void terminate(List<String> all, long errors, long unhandled_errors, long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " DailyUserStatistic and UserQuota for user(s) have bean created.");
		if (errors > 0) {
			logger.info(errors + " DailyUserStatistic and UserQuota for user(s) failed to be created");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors
					+ " DailyUserStatistic and UserQuota for user(s) failed to be created (unhandled error.)");
		}
		logger.info("DailyUserBatchImpl job terminated");
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
		return !batchHistoryBusinessService.exist(today(), null, BatchType.DAILY_USER_BATCH);
	}

	private Date today() {
		GregorianCalendar dateCalendar = new GregorianCalendar();
		dateCalendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		dateCalendar.set(GregorianCalendar.MINUTE, 0);
		dateCalendar.set(GregorianCalendar.SECOND, 0);
		return dateCalendar.getTime();
	}
}
