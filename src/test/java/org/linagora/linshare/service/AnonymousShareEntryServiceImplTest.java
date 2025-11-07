package org.linagora.linshare.service;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.AnonymousShareEntryBusinessService;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.BooleanValueFunctionality;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.rac.AnonymousShareEntryResourceAccessControl;
import org.linagora.linshare.core.repository.FavouriteRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.impl.AnonymousShareEntryServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnonymousShareEntryServiceImplTest {

	@InjectMocks
	private AnonymousShareEntryServiceImpl anonymousShareEntryService;

	@Mock
	private FunctionalityReadOnlyService functionalityService;

	@Mock
	private AnonymousShareEntryBusinessService anonymousShareEntryBusinessService;

	@Mock
	private LogEntryService logEntryService;

	@Mock
	private MailBuildingService mailBuildingService;

	@Mock
	private DocumentEntryBusinessService documentEntryBusinessService;

	@Mock
	private FavouriteRepository<String, User, org.linagora.linshare.core.domain.entities.RecipientFavourite> recipientFavouriteRepository;

	@Mock
	private SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService;

	@Mock
	private NotifierService notifierService;

	@Mock
	private AnonymousShareEntryResourceAccessControl rac;

	@Mock
	private AbstractDomain domain;

	/**
	 * Tests that when getting anonymous share entry byte source for first download
	 * and mail is not null, the notification service is called to send the download notification.
	 */
	@Test
	void testGetAnonymousShareEntryByteSource_WhenFirstDownloadAndMailNotNull_ShouldSendNotification() throws BusinessException {
		String shareUuid = "test-share-uuid";
		Account actor = createMockActor();
		AnonymousShareEntry shareEntry = createMockShareEntry(0L);
		MailContainerWithRecipient mailContainer = mock(MailContainerWithRecipient.class);
		when(anonymousShareEntryBusinessService.findByUuid(shareUuid)).thenReturn(shareEntry);
		when(mailBuildingService.build(any(EmailContext.class))).thenReturn(mailContainer);
		when(anonymousShareEntryBusinessService.updateDownloadCounter(shareEntry)).thenReturn(shareEntry);
		when(documentEntryBusinessService.getByteSource(any(DocumentEntry.class))).thenReturn(null);
		doNothing().when(rac).checkDownloadPermission(any(Account.class), any(), any(), any(), any());
		anonymousShareEntryService.getAnonymousShareEntryByteSource(actor, shareUuid);
		verify(notifierService).sendNotification(mailContainer);
	}

	/**
	 * Tests that when getting anonymous share entry byte source for first download
	 * and mail is null, the notification service is not called.
	 */
	@Test
	void testGetAnonymousShareEntryByteSource_WhenFirstDownloadAndMailNull_ShouldNotSendNotification() throws BusinessException {
		String shareUuid = "test-share-uuid";
		Account actor = createMockActor();
		AnonymousShareEntry shareEntry = createMockShareEntry(0L);
		when(anonymousShareEntryBusinessService.findByUuid(shareUuid)).thenReturn(shareEntry);
		when(mailBuildingService.build(any(EmailContext.class))).thenReturn(null);
		when(anonymousShareEntryBusinessService.updateDownloadCounter(shareEntry)).thenReturn(shareEntry);
		when(documentEntryBusinessService.getByteSource(any(DocumentEntry.class))).thenReturn(null);
		doNothing().when(rac).checkDownloadPermission(any(Account.class), any(), any(), any(), any());
		anonymousShareEntryService.getAnonymousShareEntryByteSource(actor, shareUuid);
		verify(notifierService, never()).sendNotification(any(MailContainerWithRecipient.class));
	}

	/**
	 * Tests that when getting anonymous share entry byte source for subsequent download
	 * and functionality is enabled, the notification is sent when mail is not null.
	 */
	@Test
	void testGetAnonymousShareEntryByteSource_WhenSubsequentDownloadAndFunctionalityEnabled_ShouldSendNotification() throws BusinessException {
		String shareUuid = "test-share-uuid";
		Account actor = createMockActor();
		AnonymousShareEntry shareEntry = createMockShareEntry(1L); // Subsequent download
		MailContainerWithRecipient mailContainer = mock(MailContainerWithRecipient.class);
		BooleanValueFunctionality functionality = mock(BooleanValueFunctionality.class);
		when(anonymousShareEntryBusinessService.findByUuid(shareUuid)).thenReturn(shareEntry);
		when(functionalityService.getAnonymousUrlNotification(any(AbstractDomain.class))).thenReturn(functionality);
		when(functionality.getValue()).thenReturn(true); // Functionality enabled
		when(mailBuildingService.build(any(EmailContext.class))).thenReturn(mailContainer);
		when(anonymousShareEntryBusinessService.updateDownloadCounter(shareEntry)).thenReturn(shareEntry);
		when(documentEntryBusinessService.getByteSource(any(DocumentEntry.class))).thenReturn(null);
		doNothing().when(rac).checkDownloadPermission(any(Account.class), any(), any(), any(), any());
		anonymousShareEntryService.getAnonymousShareEntryByteSource(actor, shareUuid);
		verify(notifierService).sendNotification(mailContainer);
	}

	/**
	 * Tests that when getting anonymous share entry byte source for subsequent download
	 * and functionality is disabled, no notification is sent even if mail is not null.
	 */
	@Test
	void testGetAnonymousShareEntryByteSource_WhenSubsequentDownloadAndFunctionalityDisabled_ShouldNotSendNotification() throws BusinessException {
		String shareUuid = "test-share-uuid";
		Account actor = createMockActor();
		AnonymousShareEntry shareEntry = createMockShareEntry(1L);
		BooleanValueFunctionality functionality = mock(BooleanValueFunctionality.class);
		when(anonymousShareEntryBusinessService.findByUuid(shareUuid)).thenReturn(shareEntry);
		when(functionalityService.getAnonymousUrlNotification(any(AbstractDomain.class))).thenReturn(functionality);
		when(functionality.getValue()).thenReturn(false);
		when(anonymousShareEntryBusinessService.updateDownloadCounter(shareEntry)).thenReturn(shareEntry);
		when(documentEntryBusinessService.getByteSource(any(DocumentEntry.class))).thenReturn(null);
		doNothing().when(rac).checkDownloadPermission(any(Account.class), any(), any(), any(), any());
		anonymousShareEntryService.getAnonymousShareEntryByteSource(actor, shareUuid);
		verify(mailBuildingService, never()).build(any(EmailContext.class));
		verify(notifierService, never()).sendNotification(any(MailContainerWithRecipient.class));
	}

	/**
	 * Tests that when getting anonymous share entry byte source for subsequent download,
	 * functionality is enabled, but mail is null, no notification is sent.
	 */
	@Test
	void testGetAnonymousShareEntryByteSource_WhenSubsequentDownloadAndMailNull_ShouldNotSendNotification() throws BusinessException {
		String shareUuid = "test-share-uuid";
		Account actor = createMockActor();
		AnonymousShareEntry shareEntry = createMockShareEntry(1L);
		BooleanValueFunctionality functionality = mock(BooleanValueFunctionality.class);
		when(anonymousShareEntryBusinessService.findByUuid(shareUuid)).thenReturn(shareEntry);
		when(functionalityService.getAnonymousUrlNotification(any(AbstractDomain.class))).thenReturn(functionality);
		when(functionality.getValue()).thenReturn(true);
		when(mailBuildingService.build(any(EmailContext.class))).thenReturn(null);
		when(anonymousShareEntryBusinessService.updateDownloadCounter(shareEntry)).thenReturn(shareEntry);
		when(documentEntryBusinessService.getByteSource(any(DocumentEntry.class))).thenReturn(null);
		doNothing().when(rac).checkDownloadPermission(any(Account.class), any(), any(), any(), any());
		anonymousShareEntryService.getAnonymousShareEntryByteSource(actor, shareUuid);
		verify(notifierService, never()).sendNotification(any(MailContainerWithRecipient.class));
	}

	private Account createMockActor() {
		Account actor = mock(Account.class);
		when(actor.getLsUuid()).thenReturn("test-actor-uuid");
		when(actor.getDomain()).thenReturn(domain);
		return actor;
	}

	private AnonymousShareEntry createMockShareEntry(Long downloadedCount) {
		AnonymousShareEntry shareEntry = mock(AnonymousShareEntry.class);
		AnonymousUrl anonymousUrl = mock(AnonymousUrl.class);
		Contact contact = mock(Contact.class);
		DocumentEntry documentEntry = mock(DocumentEntry.class);
		User entryOwner = mock(User.class);
		AbstractDomain domain = mock(AbstractDomain.class);

		when(shareEntry.getDownloaded()).thenReturn(downloadedCount);
		when(shareEntry.getAnonymousUrl()).thenReturn(anonymousUrl);
		when(shareEntry.getDocumentEntry()).thenReturn(documentEntry);
		when(shareEntry.getEntryOwner()).thenReturn(entryOwner);
		when(shareEntry.getCreationDate()).thenReturn(Calendar.getInstance());
		when(shareEntry.getModificationDate()).thenReturn(Calendar.getInstance());
		when(shareEntry.getUuid()).thenReturn("test-share-uuid");
		when(shareEntry.getName()).thenReturn("test-document.pdf");

		when(anonymousUrl.getContact()).thenReturn(contact);
		when(contact.getMail()).thenReturn("contact@test.com");

		when(entryOwner.getDomain()).thenReturn(domain);
		when(entryOwner.getDomainId()).thenReturn("test-domain-uuid");
		when(entryOwner.getLsUuid()).thenReturn("test-owner-uuid");
		when(entryOwner.getMail()).thenReturn("owner@test.com");
		when(entryOwner.getMailLocale()).thenReturn(org.linagora.linshare.core.domain.constants.Language.ENGLISH);

		when(domain.getUuid()).thenReturn("test-domain-uuid");
		when(domain.getLabel()).thenReturn("Test Domain");

		when(documentEntry.getUuid()).thenReturn("test-document-uuid");
		lenient().when(documentEntry.getCreationDate()).thenReturn(Calendar.getInstance());
		lenient().when(documentEntry.getModificationDate()).thenReturn(Calendar.getInstance());

		return shareEntry;
	}
}