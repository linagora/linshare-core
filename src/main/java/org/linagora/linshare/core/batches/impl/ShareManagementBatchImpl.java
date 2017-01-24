/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
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

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.batches.ShareManagementBatch;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.ShareWarnRecipientBeforeExpiryEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.AnonymousShareEntryRepository;
import org.linagora.linshare.core.repository.ShareEntryRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.NotifierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class provides shares management methods.
 */
public class ShareManagementBatchImpl implements ShareManagementBatch {

	private static final Logger logger = LoggerFactory
			.getLogger(ShareManagementBatchImpl.class);

	private final ShareEntryRepository shareEntryRepository;

	private final AnonymousShareEntryRepository anonymousShareEntryRepository;

	private final AccountRepository<Account> accountRepository;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final NotifierService notifierService;

	private final MailBuildingService mailBuildingService;

	public ShareManagementBatchImpl(ShareEntryRepository shareEntryRepository,
			AnonymousShareEntryRepository anonymousShareEntryRepository,
			AccountRepository<Account> accountRepository,
			FunctionalityReadOnlyService functionalityReadOnlyService,
			NotifierService notifierService,
			MailBuildingService mailBuildingService) {
		super();
		this.shareEntryRepository = shareEntryRepository;
		this.anonymousShareEntryRepository = anonymousShareEntryRepository;
		this.accountRepository = accountRepository;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.notifierService = notifierService;
		this.mailBuildingService = mailBuildingService;
	}

	@Override
	public void notifyUpcomingOutdatedShares() {

		SystemAccount systemAccount = accountRepository.getBatchSystemAccount();

		StringValueFunctionality notificationBeforeExpirationFunctionality = functionalityReadOnlyService
				.getShareNotificationBeforeExpirationFunctionality(systemAccount
						.getDomain());

		List<Integer> datesForNotifyUpcomingOutdatedShares = new ArrayList<Integer>();

		String[] dates = notificationBeforeExpirationFunctionality.getValue()
				.split(",");
		for (String date : dates) {
			datesForNotifyUpcomingOutdatedShares.add(Integer.parseInt(date));
		}

		for (Integer day : datesForNotifyUpcomingOutdatedShares) {

			List<ShareEntry> shares = shareEntryRepository
					.findUpcomingExpiredEntries(day);
			logger.info(shares.size() + " upcoming (in " + day.toString()
					+ " days) outdated share(s) found to be notified.");
			for (ShareEntry share : shares) {
				if (share.getDownloaded() < 1) {
					try {
						EmailContext contex = new ShareWarnRecipientBeforeExpiryEmailContext(share, day);
						MailContainerWithRecipient mail = mailBuildingService.build(contex);
						notifierService.sendNotification(mail);
					} catch (BusinessException e) {
						logger.error(
								"Error while trying to notify upcoming outdated share",
								e);
					}
				}
			}

			List<AnonymousShareEntry> anonymousShareEntries = anonymousShareEntryRepository
					.findUpcomingExpiredEntries(day);
			logger.info(anonymousShareEntries.size()
					+ " upcoming (in "
					+ day.toString()
					+ " days) outdated anonymous share Url(s) found to be notified.");

			for (AnonymousShareEntry anonymousShareEntry : anonymousShareEntries) {
				if (anonymousShareEntry.getDownloaded() < 1) {
					try {
						EmailContext contex = new ShareWarnRecipientBeforeExpiryEmailContext(anonymousShareEntry, day);
						MailContainerWithRecipient mail = mailBuildingService.build(contex);
						notifierService.sendNotification(mail);
					} catch (BusinessException e) {
						logger.error("Error while trying to notify upcoming outdated share", e);
					}
				}
			}
		}
	}
}
