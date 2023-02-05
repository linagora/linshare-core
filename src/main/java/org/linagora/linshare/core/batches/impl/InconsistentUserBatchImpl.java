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

import org.linagora.linshare.core.batches.InconsistentUserBatch;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.InconsistentUserBatchResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.InconsistentUserService;
import org.linagora.linshare.core.service.UserService;

public class InconsistentUserBatchImpl extends GenericBatchImpl implements InconsistentUserBatch {

	private InconsistentUserService service;

	private UserService userService;

	private AbstractDomainService abstractDomainService;

	public InconsistentUserBatchImpl(AccountRepository<Account> accountRepository,
			InconsistentUserService service,
			UserService userService,
			AbstractDomainService abstractDomainService) {
		super(accountRepository);
		this.service = service;
		this.userService = userService;
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info(getClass().toString() + " job is starting ...");
		SystemAccount account = getSystemAccount();
		List<String> entries = service.findAllUserUuids(account);
		logger.info(entries.size() + " users have found to be processed.");
		return entries;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		SystemAccount account = getSystemAccount();
		User u = userService.findByLsUuid(identifier);
		ResultContext context = new InconsistentUserBatchResultContext(u);
		context.setProcessed(false);
		console.logInfo(batchRunContext,
				total,
				position, "processing internal : "
						+ u.getAccountRepresentation());
		if (!abstractDomainService.isUserExist(u.getDomain(),
				u.getMail())) {
			console.logInfo(batchRunContext, total, position, "Flagging as inconsistent internal : " + u.getAccountRepresentation());
			u.setInconsistent(true);
			userService.updateUser(account, u, u.getDomainId());
			context.setProcessed(true);
		}
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		InconsistentUserBatchResultContext c = (InconsistentUserBatchResultContext) context;
		if (c.getProcessed()) {
			User u = c.getResource();
			console.logInfo(batchRunContext, total, position, "The inconsistent user " + u.getLsUuid() + " has been successfully checked.");
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		InconsistentUserBatchResultContext c = (InconsistentUserBatchResultContext) exception.getContext();
		User u = c.getResource();
		console.logError(batchRunContext, total, position, "Flaging user has failed " + u.getLsUuid() + ". BatchBusinessException ", exception);
	}

	@Override
	public void terminate(BatchRunContext batchRunContext, List<String> all, long errors, long unhandled_errors, long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " user(s) have been checked.");
		logger.info(processed + " user(s) have been flagged as inconsistent.");
		if (errors > 0) {
			logger.error(errors + " user(s) failed to be flagged as inconsistent.");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors + " user(s) failed to be flagged as inconsistent (unhandled error)");
		}
		logger.info(getClass().toString() + " job terminated.");
	}
}