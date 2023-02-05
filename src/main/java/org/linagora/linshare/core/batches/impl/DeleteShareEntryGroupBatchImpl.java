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

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.ShareEntryGroupService;

public class DeleteShareEntryGroupBatchImpl extends GenericBatchImpl {

	private final ShareEntryGroupService service;

	public DeleteShareEntryGroupBatchImpl(
			AccountRepository<Account> accountRepository,
			ShareEntryGroupService service) {
		super(accountRepository);
		this.service = service;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info("DeleteShareEntryGroupBatchImpl job starting ...");
		SystemAccount actor = getSystemAccount();
		List<String> allShareEntries = service
				.findAllToPurge(actor, actor);
		logger.info(allShareEntries.size()
				+ " unconsistent shareEntryGroups to delete");
		return allShareEntries;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		SystemAccount actor = getSystemAccount();
		ShareEntryGroup shareEntryGroup = service.find(actor, actor, identifier);
		ResultContext context = new BatchResultContext<ShareEntryGroup>(
				shareEntryGroup);
		try {
			console.logInfo(batchRunContext, total, position, "processing shareEntryGroup : "
					+ shareEntryGroup.getUuid());
			service.delete(actor, actor,
					shareEntryGroup);
			logger.info("shareEntryGroup " + shareEntryGroup.getUuid()
					+ " has been deleted");
		} catch (BusinessException businessException) {
			BatchBusinessException exception = new BatchBusinessException(
					context, "Error while trying to delete ShareEntryGroup");
			exception.setBusinessException(businessException);
			console.logError(batchRunContext, total, position,
					"Error while trying to delete shareEntryGroup", exception);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<ShareEntryGroup> shareEntryGroupContext = (BatchResultContext<ShareEntryGroup>) context;
		console.logInfo(batchRunContext, total,
				position, "The shareEntryGroup "
						+ shareEntryGroupContext.getResource().getUuid()
						+ " has been successfully deleted");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier,
			long total, long position, BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<ShareEntryGroup> shareEntryGroupContext = (BatchResultContext<ShareEntryGroup>) exception
				.getContext();
		console.logError(batchRunContext, total, position, "Deleting shareEntryGroup has failed "
				+ shareEntryGroupContext.getResource().getUuid());
	}

	@Override
	public void terminate(BatchRunContext batchRunContext, List<String> all, long errors,
			long unhandled_errors, long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " ShareEntryGroup have been deleted");
		if (errors > 0) {
			logger.error(errors + " shareEntryGroup failed to delete");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors
					+ " shareEntryGroup failed to delete (unhandled error).");
		}
		logger.info("DeleteShareEntryGroupBatchImpl job terminated.");
	}
}
