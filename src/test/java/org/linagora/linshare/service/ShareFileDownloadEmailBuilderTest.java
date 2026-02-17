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
package org.linagora.linshare.service;

import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.linagora.linshare.core.business.service.MailingListBusinessService;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.ShareFileDownloadEmailContext;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.core.notifications.emails.impl.ShareFileDownloadEmailBuilder;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.context.Context;

import junit.framework.AssertionFailedError;

/**
 * Unit tests of {@link ShareFileDownloadEmailBuilder}.
 */
@ExtendWith(MockitoExtension.class)
class ShareFileDownloadEmailBuilderTest {

	private static final String LINSHARE_URL = "My Linshare URL";
	private static final String DOCUMENT_UUID = "doc-uuid-123";
	private static final String DOCUMENT_HREF = "My document HREF";
	private static final String ANONYMOUS_CONTACT_MAIL = "anonymous@external.com";
	private static final String SHARE_ENTRY_MAIL = "recipient@domain.com";

   private MailingListBusinessService contactListBusinessService;
   private AccountService accountService;
   private AuditLogEntryService auditLogEntryService;

   @BeforeEach
   void setUp() {
      // Mock dependencies for ShareFileDownloadEmailContext
      this.contactListBusinessService = mock(MailingListBusinessService.class);
      this.accountService = mock(AccountService.class);
      this.auditLogEntryService = mock(AuditLogEntryService.class);
   }

   /**
    * <p>Provides test scenarios for both anonymous and non-anonymous shares.</p>
    *
    * @return {@link Stream} of {@link Arguments} for parameterized tests
    */
   @Nonnull
   private static Stream<Arguments> shareEntryScenarios() {
		return Stream.of(
			arguments("Anonymous share", createAnonymousShareEntry(ANONYMOUS_CONTACT_MAIL)),
			arguments("Non-anonymous share", createShareEntry(SHARE_ENTRY_MAIL)));
   }

   /**
	 * Verify that {@link ShareFileDownloadEmailBuilder#buildMailContainer} correctly set variables before to generate
	 * the mail from the right template.
	 *
	 * @param description
	 *                    Test case description. Not {@code null}.
	 * @param entry
	 *                    A share entry ({@link AnonymousShareEntry} or {@link ShareEntry}) to used to build the mail.
	 *                    Not {@code null}.
	 */
   @ParameterizedTest(name = "{0}")
   @MethodSource("shareEntryScenarios")
	void buildMailContainer(@Nonnull final String description, @Nonnull final Entry entry) {

		final ShareFileDownloadEmailContext context = spy(this.createContext(entry));
		final MockableShareFileDownloadEmailBuilder emailBuilder = new MockableShareFileDownloadEmailBuilder();
		
		// Mock local protected methods invoked by 'buildMailContainer(...)'
		final MockableShareFileDownloadEmailBuilder emailBuilderSpy = spy(emailBuilder);
		doReturn(LINSHARE_URL).when(emailBuilderSpy).getLinShareUrl(any(Account.class));
		doReturn(DOCUMENT_HREF).when(emailBuilderSpy).getOwnerDocumentLink(LINSHARE_URL, DOCUMENT_UUID);
		final ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
		final MailContainerWithRecipient mailContainerMock = mock(MailContainerWithRecipient.class);
		doReturn(mailContainerMock)
			.when(emailBuilderSpy).buildMailContainerThymeleaf(any(MailConfig.class),
				eq(MailContentType.SHARE_FILE_DOWNLOAD), contextCaptor.capture(), eq(context));

		final MailContainerWithRecipient result = emailBuilderSpy.buildMailContainer(context);

		assertSame(mailContainerMock, result);
		final Context capturedContext = contextCaptor.getValue();
		assertNotNull(capturedContext);
		assertInstanceOf(MailContact.class, capturedContext.getVariable("shareRecipient"));
		if (entry instanceof AnonymousShareEntry) {
         verify(context, times(0)).createRecipientDataAgainstContactListViewStatus();
         assertEquals(ANONYMOUS_CONTACT_MAIL, ((MailContact) capturedContext.getVariable("shareRecipient")).getMail());
			assertEquals(1, ((List) capturedContext.getVariable("shares")).size());
			assertEquals(1, capturedContext.getVariable("sharesCount"));
      } else {
         verify(context, times(1)).createRecipientDataAgainstContactListViewStatus();
			assertEquals(SHARE_ENTRY_MAIL, ((MailContact) capturedContext.getVariable("shareRecipient")).getMail());
			assertEquals(0, ((List) capturedContext.getVariable("shares")).size());
			assertEquals(0, capturedContext.getVariable("sharesCount"));
      }
   }

   /**
	 * Creates the right {@link ShareFileDownloadEmailContext} according to the given entry.
	 *
	 * @param entry
	 *              The entry ({@link AnonymousShareEntry} or {@link ShareEntry}) to use to create the context. Not
	 *              {@code null}.
	 * @return The {@link ShareFileDownloadEmailContext} created according to the given entry. Not {@code null}.
	 */
	private @Nonnull ShareFileDownloadEmailContext createContext(@Nonnull final Object entry) {
      if (entry instanceof AnonymousShareEntry) {
			return new ShareFileDownloadEmailContext((AnonymousShareEntry) entry);
      } else if (entry instanceof ShareEntry) {
			return new ShareFileDownloadEmailContext((ShareEntry) entry, this.contactListBusinessService,
				this.accountService, this.auditLogEntryService);
      } else {
			throw new AssertionFailedError("Unsupported entry type: " + entry.getClass().getName());
      }
   }

   /**
    * <p>Creates a mock {@link AnonymousShareEntry} for testing.</p>
    *
    * @param email the email address for the anonymous contact
    * @return a mocked {@link AnonymousShareEntry} instance
    */
   @Nonnull
   private static AnonymousShareEntry createAnonymousShareEntry(@Nonnull final String email) {
      final AnonymousShareEntry entry = mock(AnonymousShareEntry.class);
      final AnonymousUrl anonymousUrl = mock(AnonymousUrl.class);
      final Contact contact = mock(Contact.class);
      final Account owner = createMockOwner();
      final DocumentEntry documentEntry = createMockDocumentEntry();

      when(contact.getMail()).thenReturn(email);
		when(anonymousUrl.getContact()).thenReturn(contact);

      final Set<AnonymousShareEntry> anonymousShareEntries = new HashSet<>();
      anonymousShareEntries.add(entry);
		when(anonymousUrl.getAnonymousShareEntries()).thenReturn(anonymousShareEntries);

		when(entry.getAnonymousUrl()).thenReturn(anonymousUrl);
		when(entry.getEntryOwner()).thenReturn(owner);
		when(entry.getDocumentEntry()).thenReturn(documentEntry);
		when(entry.getCreationDate()).thenReturn(Calendar.getInstance());
		when(entry.getExpirationDate()).thenReturn(null);

      return entry;
   }

   /**
    * <p>Creates a mock {@link ShareEntry} for testing.</p>
    *
    * @param recipientEmail the email address for the recipient
    * @return a mocked {@link ShareEntry} instance
    */
   @Nonnull
   private static ShareEntry createShareEntry(@Nonnull final String recipientEmail) {
      final ShareEntry entry = mock(ShareEntry.class);
      final User recipient = mock(User.class);
      final Account owner = createMockOwner();
      final DocumentEntry documentEntry = createMockDocumentEntry();

		when(recipient.getMail()).thenReturn(recipientEmail);
		when(recipient.isGuest()).thenReturn(false);
		when(entry.getRecipient()).thenReturn(recipient);
		when(entry.getEntryOwner()).thenReturn(owner);
		when(entry.getDocumentEntry()).thenReturn(documentEntry);
		when(entry.getContactListUuid()).thenReturn(null);
		when(entry.getCreationDate()).thenReturn(Calendar.getInstance());
		when(entry.getExpirationDate()).thenReturn(null);

      return entry;
   }

   /**
    * <p>Creates a mock owner {@link Account} for testing.</p>
    *
    * @return a mocked {@link Account} instance with domain and mail configuration
    */
   @Nonnull
   private static Account createMockOwner() {
      final User owner = mock(User.class);
      final AbstractDomain domain = mock(AbstractDomain.class);
      final MailConfig mailConfig = mock(MailConfig.class);

      lenient().when(owner.getMail()).thenReturn("owner@domain.com");
      lenient().when(owner.getDomain()).thenReturn(domain);
      lenient().when(owner.getMailLocale()).thenReturn(Language.ENGLISH);
      lenient().when(owner.isGuest()).thenReturn(false);
      lenient().when(domain.getCurrentMailConfiguration()).thenReturn(mailConfig);

      return owner;
   }

   /**
    * <p>Creates a mock {@link DocumentEntry} for testing.</p>
    *
    * @return a mocked {@link DocumentEntry} instance
    */
   @Nonnull
   private static DocumentEntry createMockDocumentEntry() {
      final DocumentEntry doc = mock(DocumentEntry.class);
      lenient().when(doc.getName()).thenReturn("document.pdf");
		lenient().when(doc.getUuid()).thenReturn(DOCUMENT_UUID);
      lenient().when(doc.getSize()).thenReturn(1024L);
      lenient().when(doc.getCreationDate()).thenReturn(Calendar.getInstance());
      return doc;
   }

	/**
	 * An extended {@link ShareFileDownloadEmailBuilder} where protected methods can be mocked
	 */
	private static class MockableShareFileDownloadEmailBuilder extends ShareFileDownloadEmailBuilder {
		@Override
		protected String getLinShareUrl(Account recipient) {
			return super.getLinShareUrl(recipient);
		}

		@Override
		protected String getOwnerDocumentLink(String linshareURL, String documentUuid) {
			return super.getOwnerDocumentLink(linshareURL, documentUuid);
		}

		@Override
		protected MailContainerWithRecipient buildMailContainerThymeleaf(MailConfig cfg, MailContentType type,
			Context ctx, EmailContext emailCtx) throws BusinessException {
			return super.buildMailContainerThymeleaf(cfg, type, ctx, emailCtx);
		}
	}
}