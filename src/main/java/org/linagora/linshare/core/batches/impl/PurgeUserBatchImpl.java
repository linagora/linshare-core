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

import org.linagora.linshare.core.batches.utils.OperationKind;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.AccountBatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.UserService;


public class PurgeUserBatchImpl extends GenericBatchImpl {

	protected final UserService service;

	public PurgeUserBatchImpl(final UserService userService,
			AccountRepository<Account> accountRepository) {
		super(accountRepository);
		this.service = userService;
		this.operationKind = OperationKind.PURGED;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		List<String> allUsers = service.findAllAccountsReadyToPurge();
		console.logInfo(batchRunContext, allUsers.size() + " user(s) have been found to be purged");
		return allUsers;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		SystemAccount actor = getSystemAccount();
		User resource = service.findAccountsReadyToPurge(actor, identifier);
		ResultContext context = new AccountBatchResultContext(resource);
		try {
			console.logInfo(batchRunContext, total, position, "processing user : " + resource.getAccountRepresentation());
			service.purge(actor, resource.getLsUuid());
			context.setProcessed(true);
		} catch (BusinessException businessException) {
			BatchBusinessException exception = new BatchBusinessException(
					context, "Error while trying to purge expired user");
			exception.setBusinessException(businessException);
			console.logError(batchRunContext, total, position,
					"Error while trying to purge users", exception);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		AccountBatchResultContext guestContext = (AccountBatchResultContext) context;
		Account user = guestContext.getResource();
		console.logInfo(batchRunContext, total, position, "The User "
				+ user.getAccountRepresentation()
				+ " has been successfully purged ");
	}

	@Override
	public void notifyError(BatchBusinessException exception,
			String identifier, long total, long position, BatchRunContext batchRunContext) {
		AccountBatchResultContext context = (AccountBatchResultContext) exception.getContext();
		Account user = context.getResource();
		console.logError(
				batchRunContext,
				total,
				position,
				"Purging User has failed : "
						+ user.getAccountRepresentation());
	}
}
