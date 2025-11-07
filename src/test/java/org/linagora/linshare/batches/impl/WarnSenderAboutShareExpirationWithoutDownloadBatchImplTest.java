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
package org.linagora.linshare.batches.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.batches.impl.WarnSenderAboutShareExpirationWithoutDownloadBatchImpl;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.ShareEntryRepository;
import org.linagora.linshare.core.service.NotifierService;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WarnSenderAboutShareExpirationWithoutDownloadBatchImplTest {

	private WarnSenderAboutShareExpirationWithoutDownloadBatchImpl batch;
	@Mock
	private ShareEntryRepository shareEntryRepository;

	@Mock
	private BatchRunContext batchRunContext;

	@Mock
	private NotifierService notifierService;

	@Mock
	private MailBuildingService mailBuildingService;
	@Mock
	private AccountRepository<Account> accountRepository;
	@Mock
	private Account entryOwner;
	@Mock
	private ShareEntry shareEntry;

	@Mock
	private MailContainerWithRecipient mail;

	/**
	 * Initializes the test environment before each test method execution.
	 * Creates a new instance of the batch with mocked dependencies and a fixed
	 * days left expiration value for testing purposes.
	 */
	@BeforeEach
	void SetUp() {
		// TODO: The constructor of WarnSenderAboutShareExpirationWithoutDownloadBatchImpl has not to have a parameter
		//  about the expiration days. As the expiration days are initialized by the Spring property
		//  'linshare.warn.owner.about.share.expiration.days.before', it can be defined directly in
		//  WarnSenderAboutShareExpirationWithoutDownloadBatchImpl at field with
		//  '@Value("${linshare.warn.owner.about.share.expiration.days.before}")'. And so, you can use '@InjectMocks' in this unit test.
		this.batch = new WarnSenderAboutShareExpirationWithoutDownloadBatchImpl(this.accountRepository, this.shareEntryRepository,
				this.mailBuildingService, this.notifierService, 9);
	}

	/**
	 * Tests that when the mail building service returns a non-null mail container,
	 * the notification service is called to send the notification.
	 * This verifies the positive case of the null-safety mechanism.
	 */
	@Test
	void execute_WhenMailIsNotNull_ShouldSendNotification() throws BatchBusinessException, BusinessException {
		final String identifier = "warn-sender";
		final long total = 1L;
		final long position = 0L;
		when(this.shareEntryRepository.findByUuid(identifier)).thenReturn(this.shareEntry);
		when(this.shareEntry.getEntryOwner()).thenReturn(this.entryOwner);
		when(this.mailBuildingService.build(any(EmailContext.class))).thenReturn(this.mail);
		ResultContext context = this.batch.execute(this.batchRunContext, identifier, total, position);
		assertNotNull(context);
		verify(this.notifierService).sendNotification(this.mail);

	}

	/**
	 * Tests that when the mail building service returns null,
	 * the notification service is not called and no exception is thrown.
	 * This verifies the null-safety mechanism added to prevent NullPointerException
	 * when the mail building service cannot construct a valid mail.
	 */
	@Test
	void execute_WhenMailIsNull_ShouldNotSendNotification() throws BatchBusinessException, BusinessException {
		final String identifier = "warn-sender";
		final long total = 1L;
		final long position = 0L;
		when(this.shareEntryRepository.findByUuid(identifier)).thenReturn(this.shareEntry);
		when(this.shareEntry.getEntryOwner()).thenReturn(this.entryOwner);
		when(this.mailBuildingService.build(any(EmailContext.class))).thenReturn(null);
		ResultContext context = this.batch.execute(this.batchRunContext, identifier, total, position);
		assertNotNull(context);
		verify(this.notifierService, never()).sendNotification(any(MailContainerWithRecipient.class));

	}

}
