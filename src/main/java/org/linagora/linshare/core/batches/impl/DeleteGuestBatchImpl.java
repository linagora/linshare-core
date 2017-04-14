/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 *
 * Copyright (C) 2015-2016 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.AccountBatchResultContext;
import org.linagora.linshare.core.job.quartz.Context;
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
	}

	@Override
	public List<String> getAll() {
		logger.info("DeleteGuestBatchImpl job starting ...");
		List<String> guests = service.findOudatedGuests(getSystemAccount());
		logger.info(guests.size() + " guest(s) have been found to be removed");
		return guests;
	}

	@Override
	public Context execute(String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		SystemAccount actor = getSystemAccount();
		Guest resource = service.findOudatedGuest(actor, identifier);
		Context context = new AccountBatchResultContext(resource);
		try {
			logInfo(total, position,
					"processing guest : " + resource.getAccountRepresentation());
			service.deleteUser(actor, resource.getLsUuid());
			logger.info("Removed expired user : "
					+ resource.getAccountRepresentation());
		} catch (BusinessException businessException) {
			logError(total, position,
					"Error while trying to delete expired guest ");
			logger.info("Error occured while cleaning outdated guests ",
					businessException);
			BatchBusinessException exception = new BatchBusinessException(
					context, "Error while trying to delete expired guest");
			exception.setBusinessException(businessException);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(Context context, long total, long position) {
		AccountBatchResultContext guestContext = (AccountBatchResultContext) context;
		Account guest = guestContext.getResource();
		logInfo(total, position, "The Guest "
				+ guest.getAccountRepresentation()
				+ " has been successfully removed ");
	}

	@Override
	public void notifyError(BatchBusinessException exception,
			String identifier, long total, long position) {
		AccountBatchResultContext context = (AccountBatchResultContext) exception.getContext();
		Account guest = context.getResource();
		logError(
				total,
				position,
				"cleaning Guest has failed : "
						+ guest.getAccountRepresentation());
		logger.error(
				"Error occured while cleaning outdated guest "
						+ guest.getAccountRepresentation()
						+ ". BatchBusinessException ", exception);
	}

}
