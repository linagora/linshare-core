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

import org.linagora.linshare.core.business.service.ModeratorBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Moderator;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.AccountBatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.WarnModeratorAboutGuestExpirationEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.NotifierService;

import com.google.common.collect.Lists;

public class WarnModeratorAboutGuestExpirationBatchImpl extends GenericBatchImpl {

	protected GuestRepository guestRepository;

	protected AccountService accountService;

	private ModeratorBusinessService moderatorBusinessService;

	protected MailBuildingService mailBuildingService;

	protected NotifierService notifierService;

	protected int nbDaysBeforeExpiration;

	public WarnModeratorAboutGuestExpirationBatchImpl(AccountRepository<Account> accountRepository,
			GuestRepository guestRepository,
			ModeratorBusinessService moderatorBusinessService,
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
		this.moderatorBusinessService = moderatorBusinessService;
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
		console.logInfo(batchRunContext, total, position, "Processing guest account " + guest.getAccountRepresentation());
		ResultContext context = new AccountBatchResultContext(guest);
		try {
			List<MailContainerWithRecipient> mails = Lists.newArrayList();
			List<Moderator> moderators = moderatorBusinessService.findAllByGuest(guest, null, null);
			for (Moderator moderator : moderators) {
				EmailContext ctx = new WarnModeratorAboutGuestExpirationEmailContext(moderator, guest, nbDaysBeforeExpiration);
				mails.add(mailBuildingService.build(ctx));
			}
			notifierService.sendNotification(mails);
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
