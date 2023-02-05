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

import java.util.List;

import org.linagora.linshare.core.batches.CheckIfUserStillInconsistentBatch;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.CheckIfUserStillInconsistentBatchResultContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.InconsistentUserService;
import org.linagora.linshare.core.service.UserService;

public class CheckIfUserStillInconsistentBatchImpl extends GenericBatchImpl implements CheckIfUserStillInconsistentBatch {

	private final InconsistentUserService internaleService;

	private final UserService userService;

	private final AbstractDomainService abstractDomainService;

	public CheckIfUserStillInconsistentBatchImpl(AccountRepository<Account> accountRepository,
			final InconsistentUserService service,
			final UserService userService,
			final AbstractDomainService abstractDomainService) {
		super(accountRepository);
		this.internaleService = service;
		this.userService = userService;
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		SystemAccount account = getSystemAccount();
		logger.info(getClass().toString() + " job starting ...");
		List<String> entries = internaleService.findAllIconsistentsUuid(account);
		logger.info(entries.size()
				+ " Inconsistent users have been found to be processed.");
		return entries;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		SystemAccount account = getSystemAccount();
		User user  = userService.findByLsUuid(identifier);
		ResultContext c = new CheckIfUserStillInconsistentBatchResultContext(user);
		console.logInfo(batchRunContext,
				total,
				position, "processing internal : "
						+ user.getAccountRepresentation());
		c.setProcessed(false);
		if (abstractDomainService.isUserExist(user.getDomain(), user.getMail())) {
			user.setInconsistent(false);
			userService.updateUser(account, user, user.getDomainId());
			c.setProcessed(true);
		}
		return c;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		CheckIfUserStillInconsistentBatchResultContext c = (CheckIfUserStillInconsistentBatchResultContext) context;
		User u = c.getResource();
		if (c.getProcessed()) {
			console.logInfo(batchRunContext, total, position, "The inconsistent user " + u.getLsUuid() + " has been successfully checked.");
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		CheckIfUserStillInconsistentBatchResultContext c = (CheckIfUserStillInconsistentBatchResultContext) exception.getContext();
		User u = c.getResource();
		console.logError(batchRunContext, total, position,
				"Flaging user has failed " + u.getLsUuid() + ". BatchBusinessException ", exception);
	}

	@Override
	public void terminate(BatchRunContext batchRunContext, List<String> all, long errors, long unhandled_errors, long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " user(s) have been checked.");
		logger.info(processed + " user(s) have been unflagged.");
		if (errors > 0) {
			logger.error(errors + " user(s) failed to be unflagged.");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors + " user(s) failed to be unflagged (unhandled error)");
		}
		logger.info(getClass().toString() + " job terminated.");
	}
}
