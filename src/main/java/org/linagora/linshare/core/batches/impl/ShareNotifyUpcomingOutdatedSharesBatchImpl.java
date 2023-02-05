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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.ShareWarnRecipientBeforeExpiryEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.ShareEntryRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.NotifierService;

public class ShareNotifyUpcomingOutdatedSharesBatchImpl extends GenericBatchImpl {

	private final ShareEntryRepository shareEntryRepository;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final NotifierService notifierService;

	private final MailBuildingService mailBuildingService;

	public ShareNotifyUpcomingOutdatedSharesBatchImpl(AccountRepository<Account> accountRepository,
			ShareEntryRepository shareEntryRepository,
			FunctionalityReadOnlyService functionalityReadOnlyService,
			NotifierService notifierService,
			MailBuildingService mailBuildingService) {
		super(accountRepository);
		this.shareEntryRepository = shareEntryRepository;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.notifierService = notifierService;
		this.mailBuildingService = mailBuildingService;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		SystemAccount systemAccount = accountRepository.getBatchSystemAccount();
		StringValueFunctionality notificationBeforeExpirationFunctionality = functionalityReadOnlyService
				.getShareNotificationBeforeExpirationFunctionality(systemAccount.getDomain());
		String[] dates = notificationBeforeExpirationFunctionality.getValue().split(",");
		List<String> shares = new ArrayList<String>();
		for (String date : dates) {
			shares.addAll(shareEntryRepository.findUpcomingExpiredEntries(Integer.parseInt(date)));
		}
		return shares;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		ShareEntry shareEntry = shareEntryRepository.findByUuid(identifier);
		ResultContext context = new BatchResultContext<ShareEntry>(shareEntry);
		context.setProcessed(true);
		Integer daysLeft = Days.daysBetween(new DateTime(new Date()), new DateTime(shareEntry.getExpirationDate()))
				.getDays();
		if (shareEntry.getDownloaded() < 1) {
			EmailContext emailContext = new ShareWarnRecipientBeforeExpiryEmailContext(shareEntry, daysLeft);
			MailContainerWithRecipient mail = mailBuildingService.build(emailContext);
			notifierService.sendNotification(mail, true);
		}
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<ShareEntry> shareEntry = (BatchResultContext<ShareEntry>) context;
		if (shareEntry.getProcessed()) {
			ShareEntry entry = shareEntry.getResource();
			console.logInfo(batchRunContext, total, position,
					" Notification for upcoming outdated share successfully sent {}.", entry.getRepresentation());
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<ShareEntry> context = (BatchResultContext<ShareEntry>) exception.getContext();
		ShareEntry entry = context.getResource();
		console.logError(batchRunContext, total, position,
				"Failed to send notification for upcoming outdated share : {}", entry.getRepresentation(), exception);
	}
}
