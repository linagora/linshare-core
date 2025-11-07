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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.batches.impl.ShareNotifyUpcomingOutdatedSharesBatchImpl;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.ShareEntryRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.NotifierService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for {@link ShareNotifyUpcomingOutdatedSharesBatchImpl}
 *
 * This class tests the behavior of the batch responsible for sending notifications
 * for upcoming outdated shares (non-anonymous), with specific focus on the null-safety
 * handling added to the mail sending process.
 *
 * @see ShareNotifyUpcomingOutdatedSharesBatchImpl
 */
@ExtendWith(MockitoExtension.class)
class ShareNotifyUpcomingOutdatedSharesBatchImplTest {

	@InjectMocks
	private ShareNotifyUpcomingOutdatedSharesBatchImpl batch;

	@Mock
	private AccountRepository<Account> accountRepository;

	@Mock
	private ShareEntryRepository shareEntryRepository;

	@Mock
	private FunctionalityReadOnlyService functionalityReadOnlyService;

	@Mock
	private NotifierService notifierService;

	@Mock
	private MailBuildingService mailBuildingService;

	@Mock
	private BatchRunContext batchRunContext;

	@Mock
	private ShareEntry shareEntry;

	@Mock
	private SystemAccount systemAccount;

	@Mock
	private MailContainerWithRecipient mailContainer;

	@Mock
	private AbstractDomain domain;

	@Mock
	private User entryOwner;

	@Mock
	private Language mailLocale;

	@Mock
	private User recipient;

	/**
	 * Initializes the test environment before each test method execution.
	 * Creates a new instance of the batch with mocked dependencies.
	 */
	@BeforeEach
	void setUp() {
		when(this.shareEntry.getEntryOwner()).thenReturn(this.entryOwner);
		when(this.entryOwner.getDomain()).thenReturn(this.domain);
		lenient().when(this.entryOwner.getMailLocale()).thenReturn(this.mailLocale);
		when(this.shareEntry.getRecipient()).thenReturn(this.recipient);
	}

	/**
	 * Tests that when the mail building service returns a non-null mail container,
	 * the notification service is called to send the notification for a share entry.
	 */
	@Test
	void execute_WhenMailIsNotNull_ShouldSendNotification()
			throws BatchBusinessException, BusinessException {
		final String identifier = "test-share-uuid";
		final long total = 1L;
		final long position = 0L;
		when(this.shareEntryRepository.findByUuid(identifier)).thenReturn(this.shareEntry);
		when(this.shareEntry.getDownloaded()).thenReturn(0L);
		when(this.shareEntry.getExpirationDate()).thenReturn(Calendar.getInstance());
		when(this.mailBuildingService.build(any(EmailContext.class))).thenReturn(this.mailContainer);
		ResultContext result = this.batch.execute(this.batchRunContext, identifier, total, position);
		assertNotNull(result, "Result context should not be null");
		assertTrue(result.getProcessed(), "Result should be marked as processed");
		verify(this.notifierService).sendNotification(this.mailContainer);
	}

	/**
	 * Tests that when the mail building service returns null,
	 * the notification service is not called and no exception is thrown.
	 * This verifies the null-safety mechanism added to prevent NullPointerException.
	 */
	@Test
	void execute_WhenMailIsNull_ShouldNotSendNotification()
			throws BatchBusinessException, BusinessException {
		final String identifier = "test-share-uuid";
		final long total = 1L;
		final long position = 0L;
		when(this.shareEntryRepository.findByUuid(identifier)).thenReturn(this.shareEntry);
		when(this.shareEntry.getDownloaded()).thenReturn(0L);
		when(this.shareEntry.getExpirationDate()).thenReturn(Calendar.getInstance());
		when(this.mailBuildingService.build(any(EmailContext.class))).thenReturn(null);
		final ResultContext result = this.batch.execute(this.batchRunContext, identifier, total, position);
		assertNotNull(result, "Result context should not be null");
		assertTrue(result.getProcessed(), "Result should be marked as processed");
		verify(this.notifierService, never()).sendNotification(any(MailContainerWithRecipient.class));
	}
}