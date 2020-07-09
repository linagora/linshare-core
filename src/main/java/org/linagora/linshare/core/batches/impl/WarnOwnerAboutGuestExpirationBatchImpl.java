/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
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
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.AccountBatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.WarnOwnerAboutGuestExpirationEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.NotifierService;

public class WarnOwnerAboutGuestExpirationBatchImpl extends GenericBatchImpl {

	protected GuestRepository guestRepository;

	protected AccountService accountService;

	protected MailBuildingService mailBuildingService;

	protected NotifierService notifierService;

	protected int nbDaysBeforeExpiration;

	public WarnOwnerAboutGuestExpirationBatchImpl(AccountRepository<Account> accountRepository,
			GuestRepository guestRepository,
			MailBuildingService mailBuildingService,
			NotifierService notifierService,
			AccountService accountService,
			int daysBeforeExpiration) {
		super(accountRepository);
		this.guestRepository = guestRepository;
		this.mailBuildingService = mailBuildingService;
		this.notifierService = notifierService;
		this.accountService = accountService;
		this.nbDaysBeforeExpiration = daysBeforeExpiration;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info(getClass().toString() + " job is starting ...");
		List<String> entries = guestRepository.findGuestsAboutToExpire(nbDaysBeforeExpiration);
		logger.info(entries.size() + " guest(s) have been found.");
		return entries;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		Guest guest = guestRepository.findByLsUuid(identifier);
		if (guest == null) {
			return null;
		}
		Account owner = accountService.findByLsUuid(guest.getOwner().getLsUuid());
		if (owner == null) {
			logger.warn("No owner found for this guest : " + guest.getAccountRepresentation());
			return null;
		}
		console.logInfo(batchRunContext, total, position, "Processing guest account " + guest.getAccountRepresentation());
		ResultContext context = new AccountBatchResultContext(owner);
		try {
			EmailContext ctx = new WarnOwnerAboutGuestExpirationEmailContext(guest, nbDaysBeforeExpiration);
			MailContainerWithRecipient mail = mailBuildingService.build(ctx);
			notifierService.sendNotification(mail);
		} catch (BusinessException businessException) {
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to send a notification for expiration guest");
			exception.setBusinessException(businessException);
			console.logError(batchRunContext, total, position,
					"Error while trying to send a notification for expiration guest", exception);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		AccountBatchResultContext ownerContext = (AccountBatchResultContext) context;
		Account user = ownerContext.getResource();
		console.logInfo(batchRunContext, total, position,
				"The User " + user.getAccountRepresentation() + " has been successfully notified ");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		AccountBatchResultContext context = (AccountBatchResultContext) exception.getContext();
		Account user = context.getResource();
		console.logError(batchRunContext, total, position,
				"User notification has failed : " + user.getAccountRepresentation());
	}

}
