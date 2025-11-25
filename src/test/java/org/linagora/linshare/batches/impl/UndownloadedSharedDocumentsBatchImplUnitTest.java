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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.batches.impl.UndownloadedSharedDocumentsBatchImpl;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ContactListService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.ShareEntryGroupService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UndownloadedSharedDocumentsBatchImplUnitTest {

	@InjectMocks
	private UndownloadedSharedDocumentsBatchImpl undownloadedSharedDocumentsBatch;

	@Mock
	private ShareEntryGroupService shareEntryGroupService;

	@Mock
	private MailBuildingService mailBuildingService;

	@Mock
	private NotifierService notifierService;

	@Mock
	private LogEntryService logEntryService;

	@Mock
	private AccountRepository<Account> accountRepository;

	@Mock
	private ContactListService contactListService;
	@Mock
	private  AccountService accountService;
	private BatchRunContext batchRunContext;
	private SystemAccount systemAccount;

	@BeforeEach
	void setUp() {
		this.batchRunContext = new BatchRunContext();
		this.systemAccount = mock(SystemAccount.class);
		when(this.accountRepository.getBatchSystemAccount()).thenReturn(this.systemAccount);
	}

	/**
	 * Unit test that verifies the business logic of the execute method:
	 * When shareEntryGroup needs notification and mailBuildingService.build() returns a non-null mail container,
	 * then notifierService.sendNotification() MUST be called
	 */
	@Test
	void testExecute_WhenShareEntryGroupNeedsNotificationAndMailNotNull_ShouldSendNotification()
			throws BatchBusinessException, BusinessException {
		final String identifier = "share-entry-group-uuid";
		final long total = 1L;
		final long position = 0L;
		final ShareEntryGroup shareEntryGroup = this.createMockShareEntryGroup(true);
		final MailContainerWithRecipient mailContainer = mock(MailContainerWithRecipient.class);
		when(this.shareEntryGroupService.find(any(SystemAccount.class), any(SystemAccount.class), eq(identifier)))
				.thenReturn(shareEntryGroup);
		when(this.mailBuildingService.build(any(EmailContext.class))).thenReturn(mailContainer);
		when(this.shareEntryGroupService.update(any(SystemAccount.class), any(SystemAccount.class), eq(shareEntryGroup)))
				.thenReturn(shareEntryGroup);
		when(this.logEntryService.insert(any(List.class))).thenReturn(null);
		this.undownloadedSharedDocumentsBatch.execute(this.batchRunContext, identifier, total, position);
		verify(this.notifierService).sendNotification(mailContainer);
		verify(this.mailBuildingService).build(any(EmailContext.class));
		verify(this.shareEntryGroupService).update(any(SystemAccount.class), any(SystemAccount.class), eq(shareEntryGroup));
	}

	/**
	 * Unit test that verifies the business logic of the execute method:
	 * When shareEntryGroup needs notification but mailBuildingService.build() returns null,
	 * then notifierService.sendNotification() MUST NOT be called
	 */
	@Test
	void testExecute_WhenShareEntryGroupNeedsNotificationButMailNull_ShouldNotSendNotification()
			throws BatchBusinessException, BusinessException {
		final String identifier = "share-entry-group-uuid";
		final long total = 1L;
		final long position = 0L;
		final ShareEntryGroup shareEntryGroup = this.createMockShareEntryGroup(true);
		when(this.shareEntryGroupService.find(any(SystemAccount.class), any(SystemAccount.class), eq(identifier)))
				.thenReturn(shareEntryGroup);
		when(this.mailBuildingService.build(any(EmailContext.class))).thenReturn(null);
		when(this.shareEntryGroupService.update(any(SystemAccount.class), any(SystemAccount.class), eq(shareEntryGroup)))
				.thenReturn(shareEntryGroup);
		when(this.logEntryService.insert(any(List.class))).thenReturn(null);
		this.undownloadedSharedDocumentsBatch.execute(this.batchRunContext, identifier, total, position);
		verify(this.notifierService, never()).sendNotification(any(MailContainerWithRecipient.class));
		verify(this.mailBuildingService).build(any(EmailContext.class));
	}

	/**
	 * Unit test that verifies the business logic of the execute method:
	 * When shareEntryGroup does NOT need notification,
	 * then mailBuildingService.build() should NOT be called and notifierService.sendNotification() MUST NOT be called
	 */
	@Test
	void testExecute_WhenShareEntryGroupDoesNotNeedNotification_ShouldNotSendNotification()
			throws BatchBusinessException, BusinessException {
		final String identifier = "share-entry-group-uuid";
		final long total = 1L;
		final long position = 0L;
		final ShareEntryGroup shareEntryGroup = this.createMockShareEntryGroup(false);
		when(this.shareEntryGroupService.find(any(SystemAccount.class), any(SystemAccount.class), eq(identifier)))
				.thenReturn(shareEntryGroup);
		when(this.shareEntryGroupService.update(any(SystemAccount.class), any(SystemAccount.class), eq(shareEntryGroup)))
				.thenReturn(shareEntryGroup);
		when(this.logEntryService.insert(any(List.class))).thenReturn(null);
		this.undownloadedSharedDocumentsBatch.execute(this.batchRunContext, identifier, total, position);
		verify(this.mailBuildingService, never()).build(any(EmailContext.class));
		verify(this.notifierService, never()).sendNotification(any(MailContainerWithRecipient.class));
	}

	private @Nonnull ShareEntryGroup createMockShareEntryGroup(boolean needsNotification) {
		final ShareEntryGroup shareEntryGroup = mock(ShareEntryGroup.class);
		final Account owner = mock(Account.class);
		when(shareEntryGroup.getUuid()).thenReturn("share-entry-group-uuid");
		when(shareEntryGroup.needNotification()).thenReturn(needsNotification);
		when(shareEntryGroup.getOwner()).thenReturn(owner);
		when(shareEntryGroup.getShareEntries()).thenReturn(Collections.emptySet());
		when(shareEntryGroup.getAnonymousShareEntries()).thenReturn(Collections.emptySet());

		return shareEntryGroup;
	}
}