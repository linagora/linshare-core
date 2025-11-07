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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.batches.impl.AnonymousShareNotifyUpcomingOudatedSharesBatchImpl;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.repository.AnonymousShareEntryRepository;
import org.linagora.linshare.core.service.NotifierService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnonymousShareNotifyUpcomingOudatedSharesBatchImplTest {

	@InjectMocks
	private AnonymousShareNotifyUpcomingOudatedSharesBatchImpl batch;

	@Mock
	private AnonymousShareEntryRepository anonymousShareEntryRepository;

	@Mock
	private NotifierService notifierService;

	@Mock
	private MailBuildingService mailBuildingService;

	@Mock
	private BatchRunContext batchRunContext;

	@Mock
	private AnonymousShareEntry anonymousShareEntry;

	@Mock
	private MailContainerWithRecipient mailContainer;

	@Mock
	private AbstractDomain domain;

	@Mock
	private User entryOwner;

	@Mock
	private Language mailLocale;

	@BeforeEach
	void setUp() {
		when(this.anonymousShareEntry.getEntryOwner()).thenReturn(this.entryOwner);
		when(this.entryOwner.getDomain()).thenReturn(this.domain);
		when(this.entryOwner.getMailLocale()).thenReturn(this.mailLocale);
	}

	/**
	 * Tests that when the mail building service returns null,
	 * the batch execution continues normally without throwing exceptions.
	 * This ensures robust error handling in the batch process.
	 */
	@Test
	void testExecute_WhenMailIsNull_ShouldNotSendNotification()
			throws BatchBusinessException, BusinessException {
		final String identifier = "test-share-id";
		final long total = 1L;
		final long position = 0L;
		when(this.anonymousShareEntryRepository.findById(identifier)).thenReturn(this.anonymousShareEntry);
		when(this.anonymousShareEntry.getDownloaded()).thenReturn(0L);
		when(this.anonymousShareEntry.getExpirationDate()).thenReturn(Calendar.getInstance());
		when(this.mailBuildingService.build(any(EmailContext.class))).thenReturn(null);
		final ResultContext result = batch.execute(batchRunContext, identifier, total, position);
		assertNotNull(result);
		assertTrue(result.getProcessed());
		verify(this.notifierService, never()).sendNotification(any(MailContainerWithRecipient.class));
	}

	/**
	 * Tests the scenario where multiple shares are processed and some mails are null
	 * while others are valid, ensuring proper handling of mixed cases.
	 */
	@Test
	void execute_WhenMailIsNotNull_ShouldSendNotification()
			throws BatchBusinessException, BusinessException {
		final String identifier = "test-share-id";
		final long total = 1L;
		final long position = 0L;
		when(this.anonymousShareEntryRepository.findById(identifier)).thenReturn(this.anonymousShareEntry);
		when(this.anonymousShareEntry.getDownloaded()).thenReturn(0L);
		when(this.anonymousShareEntry.getExpirationDate()).thenReturn(Calendar.getInstance());
		when(this.mailBuildingService.build(any(EmailContext.class
		))).thenReturn(this.mailContainer);
		final ResultContext result = batch.execute(this.batchRunContext, identifier, total, position);
		assertNotNull(result);
		assertTrue(result.getProcessed());
		verify(this.notifierService).sendNotification(this.mailContainer);
	}
}