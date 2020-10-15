/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */

package org.linagora.linshare.core.batches.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
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
import org.linagora.linshare.core.repository.AnonymousShareEntryRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.NotifierService;

public class AnonymousShareNotifyUpcomingOudatedSharesBatchImpl extends GenericBatchImpl {

	private final AnonymousShareEntryRepository anonymousShareEntryRepository;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final NotifierService notifierService;

	private final MailBuildingService mailBuildingService;

	public AnonymousShareNotifyUpcomingOudatedSharesBatchImpl(AccountRepository<Account> accountRepository,
			AnonymousShareEntryRepository anonymousShareEntryRepository,
			FunctionalityReadOnlyService functionalityReadOnlyService,
			NotifierService notifierService,
			MailBuildingService mailBuildingService) {
		super(accountRepository);
		this.anonymousShareEntryRepository = anonymousShareEntryRepository;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.notifierService = notifierService;
		this.mailBuildingService = mailBuildingService;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		SystemAccount systemAccount = accountRepository.getBatchSystemAccount();
		StringValueFunctionality notificationBeforeExpirationFunctionality = functionalityReadOnlyService
				.getShareNotificationBeforeExpirationFunctionality(systemAccount.getDomain());
		String[] dates = notificationBeforeExpirationFunctionality.getValueT().split(",");
		List<String> shares = new ArrayList<String>();
		for (String date : dates) {
			shares.addAll(anonymousShareEntryRepository.findUpcomingExpiredEntries(Integer.parseInt(date)));
		}
		return shares;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		AnonymousShareEntry anonymousShareEntry = anonymousShareEntryRepository.findById(identifier);
		ResultContext context = new BatchResultContext<AnonymousShareEntry>(anonymousShareEntry);
		context.setProcessed(true);
		Integer daysLeft = Days.daysBetween(new DateTime(new Date()), new DateTime(anonymousShareEntry.getExpirationDate()))
				.getDays();
		if (anonymousShareEntry.getDownloaded() < 1) {
			EmailContext emailContext = new ShareWarnRecipientBeforeExpiryEmailContext(anonymousShareEntry, daysLeft);
			MailContainerWithRecipient mail = mailBuildingService.build(emailContext);
			notifierService.sendNotification(mail, true);
		}
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<AnonymousShareEntry> anonymousShareEntries = (BatchResultContext<AnonymousShareEntry>) context;
		if (anonymousShareEntries.getProcessed()) {
			AnonymousShareEntry entry = anonymousShareEntries.getResource();
			console.logInfo(batchRunContext, total, position,
					" Notification for upcoming outdated share successfully sent {}.", entry.getRepresentation());
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<AnonymousShareEntry> context = (BatchResultContext<AnonymousShareEntry>) exception.getContext();
		AnonymousShareEntry entry = context.getResource();
		console.logError(batchRunContext, total, position,
				"Failed to send notification for upcoming outdated share : {}", entry.getRepresentation(), exception);
	}
}
