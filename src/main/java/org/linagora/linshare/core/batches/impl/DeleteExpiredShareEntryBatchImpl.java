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

import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.EntryBatchResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.ShareEntryService;

public class DeleteExpiredShareEntryBatchImpl extends GenericBatchImpl {

	private final ShareEntryService service;

	private final FunctionalityReadOnlyService functionalityService;

	private final DocumentEntryService documentEntryService;

	public DeleteExpiredShareEntryBatchImpl(
			final AccountRepository<Account> accountRepository,
			final ShareEntryService shareEntryService,
			final FunctionalityReadOnlyService functionalityService,
			final DocumentEntryService documentEntryService) {
		super(accountRepository);
		this.service = shareEntryService;
		this.functionalityService = functionalityService;
		this.documentEntryService = documentEntryService;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info(getClass().toString() + " job starting ...");
		SystemAccount actor = getSystemAccount();
		List<String> allShares = service.findAllExpiredEntries(actor, actor);
		logger.info(allShares.size()
				+ " share(s) have been found to be deleted");
		return allShares;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		SystemAccount actor = getSystemAccount();
		ShareEntry resource = service.find(actor, actor, identifier);
		ResultContext context = new EntryBatchResultContext(resource);
		try {
			console.logInfo(batchRunContext, total,
					position, "processing share : " + resource.getRepresentation());
			AbstractDomain domain = resource.getEntryOwner().getDomain();
			TimeUnitValueFunctionality func = functionalityService
					.getDefaultShareExpiryTimeFunctionality(domain);
			// test if this functionality is enable for the current domain.
			if (func.getActivationPolicy().getStatus()) {
				service.delete(actor, actor, identifier, LogActionCause.EXPIRATION);
				documentEntryService.deleteOrComputeExpiryDate(actor, domain,
						resource.getDocumentEntry());
			} else {
				logger.warn("Expiration date is set for the current share" + resource.getRepresentation()
						+ " but functionnality is disabled for its domain " + domain.getUuid());
			}
			logger.info("Expired share was deleted : "
					+ resource.getRepresentation());
		} catch (BusinessException businessException) {
			BatchBusinessException exception = new BatchBusinessException(
					context, "Error while trying to delete expired shares.");
			exception.setBusinessException(businessException);
			console.logError(batchRunContext, total, position,
					"Error while trying to delete expired share : " + resource.getRepresentation(), exception);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		EntryBatchResultContext shareContext = (EntryBatchResultContext) context;
		Entry entry = shareContext.getResource();
		console.logInfo(batchRunContext, total, position, "The share " + entry.getRepresentation()
				+ " has been successfully deleted.");
	}

	@Override
	public void notifyError(BatchBusinessException exception,
			String identifier, long total, long position, BatchRunContext batchRunContext) {
		EntryBatchResultContext shareContext = (EntryBatchResultContext) exception
				.getContext();
		Entry entry = shareContext.getResource();
		console.logError(batchRunContext, total, position,
				"cleaning share has failed : " + entry.getRepresentation());
	}

	@Override
	public void terminate(BatchRunContext batchRunContext, List<String> all, long errors,
			long unhandled_errors, long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " share(s) have been deleted.");
		if (errors > 0) {
			logger.error(errors + " share(s) failed to be deleted.");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors
					+ " share(s) failed to be deleted (unhandled error).");
		}
		logger.info(getClass().toString() + " job terminated.");
	}
}
