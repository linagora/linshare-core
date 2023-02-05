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
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.AccountBatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.ShareWarnSenderAboutShareExpirationEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.ShareEntryRepository;
import org.linagora.linshare.core.service.NotifierService;

public class WarnSenderAboutShareExpirationWithoutDownloadBatchImpl extends GenericBatchImpl {

	protected ShareEntryRepository shareEntryRepository;

	protected MailBuildingService mailBuildingService;

	protected NotifierService notifierService;

	protected int daysLeftExpiration;

	public WarnSenderAboutShareExpirationWithoutDownloadBatchImpl(AccountRepository<Account> accountRepository,
			ShareEntryRepository shareEntryRepository,
			MailBuildingService mailBuildingService,
			NotifierService notifierService,
			int daysLeftExpiration) {
		super(accountRepository);
		this.shareEntryRepository = shareEntryRepository;
		this.mailBuildingService = mailBuildingService;
		this.notifierService = notifierService;
		this.daysLeftExpiration = daysLeftExpiration;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info(getClass().toString() + " job is starting ...");
		List<String> entries = shareEntryRepository.findAllSharesExpirationWithoutDownloadEntries(daysLeftExpiration);
		logger.info(entries.size() + " share(s) have been found.");
		return entries;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		ShareEntry shareEntry = shareEntryRepository.findByUuid(identifier);
		if (shareEntry == null) {
			return null;
		}
		Account owner = shareEntry.getEntryOwner();
		console.logInfo(batchRunContext, total, position, "processing shareEntry : " + shareEntry.getRepresentation());
		console.logInfo(batchRunContext, total, position, "processing owner account : " + owner.getAccountRepresentation());
		ResultContext context = new AccountBatchResultContext(owner);
		try {
			EmailContext ctx = new ShareWarnSenderAboutShareExpirationEmailContext(shareEntry, daysLeftExpiration);
			MailContainerWithRecipient mail = mailBuildingService.build(ctx);
			notifierService.sendNotification(mail);
		} catch (BusinessException businessException) {
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to send a notification about fileshare expiration");
			exception.setBusinessException(businessException);
			console.logError(batchRunContext, total, position,
					"Error while trying to send a notification about sharefile expiration", exception);
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
