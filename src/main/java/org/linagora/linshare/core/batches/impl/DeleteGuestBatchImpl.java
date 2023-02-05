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
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.AccountBatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.GuestService;

/**
 * Batch to closed expired  guests accounts .
 */
public class DeleteGuestBatchImpl extends GenericBatchImpl {

	protected final GuestService service;

	public DeleteGuestBatchImpl(final GuestService guestService,
			AccountRepository<Account> accountRepository) {
		super(accountRepository);
		this.service = guestService;
		this.operationKind = OperationKind.REMOVED;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		List<String> guests = service.findOudatedGuests(getSystemAccount());
		console.logInfo(batchRunContext, guests.size() + " guest(s) have been found to be removed");
		return guests;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		SystemAccount actor = getSystemAccount();
		Guest resource = service.findOudatedGuest(actor, identifier);
		ResultContext context = new AccountBatchResultContext(resource);
		try {
			console.logInfo(batchRunContext, total,
					position, "processing guest : " + resource.getAccountRepresentation());
			service.deleteUser(actor, resource.getLsUuid());
			logger.info("Removed expired user : "
					+ resource.getAccountRepresentation());
			context.setProcessed(true);
		} catch (BusinessException businessException) {
			BatchBusinessException exception = new BatchBusinessException(
					context, "Error while trying to delete expired guest");
			exception.setBusinessException(businessException);
			console.logError(batchRunContext, total, position, "Error while trying to delete expired guest ",
					batchRunContext, exception);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		AccountBatchResultContext guestContext = (AccountBatchResultContext) context;
		Account guest = guestContext.getResource();
		console.logInfo(batchRunContext, total, position, "The Guest "
				+ guest.getAccountRepresentation()
				+ " has been successfully removed ");
	}

	@Override
	public void notifyError(BatchBusinessException exception,
			String identifier, long total, long position, BatchRunContext batchRunContext) {
		AccountBatchResultContext context = (AccountBatchResultContext) exception.getContext();
		Account guest = context.getResource();
		console.logError(
				batchRunContext,
				total,
				position,
				"cleaning Guest has failed : "
						+ guest.getAccountRepresentation(), batchRunContext);
	}
}
