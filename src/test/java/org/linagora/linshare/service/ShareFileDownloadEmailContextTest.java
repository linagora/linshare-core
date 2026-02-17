package org.linagora.linshare.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.linagora.linshare.core.business.service.MailingListBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountContactLists;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.ShareFileDownloadEmailContext;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShareFileDownloadEmailContextTest {

	private static final String RECIPIENT_EMAIL = "recipient@domain.com";
	private static final String CONTACT_LIST_UUID = "uuid-list-1";
	private static final String CONTACT_LIST_NAME = "Development Team";

	@Mock
	private MailingListBusinessService mailingListBusinessService;

	@Mock
	private AccountService accountService;

	@Mock
	private AuditLogEntryService auditLogEntryService;

	@Mock
	private FunctionalityReadOnlyService functionalityReadOnlyService;

	/**
	 * <p>Provides test scenarios for instantiating {@link ShareFileDownloadEmailContext} with different entry types.</p>
	 * <p>Tests that the {@code anonymous} flag is correctly set based on the entry type:</p>
	 * <ul>
	 *   <li>For {@link ShareEntry}: {@code anonymous = false}</li>
	 *   <li>For {@link AnonymousShareEntry}: {@code anonymous = true}</li>
	 * </ul>
	 *
	 * @return {@link Stream} of {@link Arguments} for parameterized tests
	 */
	@Nonnull
	private static Stream<Arguments> instantiationScenarios() {
		return Stream.of(
			arguments("ShareEntry context", createShareEntry(false, null), false),
			arguments("AnonymousShareEntry context", createAnonymousShareEntry("test@external.com"), true)
		);
	}

	/**
	 * <p><strong>TEST CONSTRUCTOR:</strong> Verify that {@link ShareFileDownloadEmailContext} correctly sets
	 * the anonymous flag based on the entry type passed to the constructor.</p>
	 * <p>This ensures proper initialization of the context for both anonymous and non-anonymous shares.</p>
	 *
	 * @param scenario description of the test scenario
	 * @param entry the {@link Entry} used to initialize fields (either {@link ShareEntry} or {@link AnonymousShareEntry})
	 * @param expectedAnonymous expected value of the anonymous flag
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("instantiationScenarios")
	void instantiate(@Nonnull final String scenario, @Nonnull final Object entry, final boolean expectedAnonymous) {
		final ShareFileDownloadEmailContext context;

		if (entry instanceof AnonymousShareEntry) {
			context = new ShareFileDownloadEmailContext((AnonymousShareEntry) entry);
		} else if (entry instanceof ShareEntry) {
			context = new ShareFileDownloadEmailContext(
				(ShareEntry) entry,
				this.mailingListBusinessService,
				this.accountService,
				this.auditLogEntryService
			);
		} else {
			fail("Unsupported entry type: " + entry.getClass().getName());
			return;
		}

		assertEquals(expectedAnonymous, context.getAnonymous(),
			"Anonymous flag should be " + expectedAnonymous + " for scenario: " + scenario);
	}

	/**
	 * <p>Creates a mock {@link ShareEntry} for testing getRecipientWithVisibility.
	 */
	private static @Nonnull ShareEntry createShareEntry(final boolean ownerIsGuest, final @Nullable String contactListUuid) {
		final ShareEntry shareEntry = mock(ShareEntry.class);
		final Account owner = EmailTestUtils.createMockUser(ownerIsGuest);
		final User recipient = createMockUser();
		final DocumentEntry documentEntry = mock(DocumentEntry.class);
		lenient().when(shareEntry.getEntryOwner()).thenReturn(owner);
		lenient().when(shareEntry.getRecipient()).thenReturn(recipient);
		lenient().when(shareEntry.getContactListUuid()).thenReturn(contactListUuid);
		lenient().when(shareEntry.getDocumentEntry()).thenReturn(documentEntry);
		return shareEntry;
	}

	private static @Nonnull User createMockUser() {
		final User user = mock(User.class);
		lenient().when(user.getMail()).thenReturn(ShareFileDownloadEmailContextTest.RECIPIENT_EMAIL);
		return user;
	}

	/**
	 * Provides comprehensive test scenarios for all test cases including explicit contact scenarios.
	 */
	@Nonnull
	private static Stream<Arguments> comprehensiveRecipientScenarios() {
		return Stream.of(
				arguments(
						"Non-guest with individual share - show recipient details",
						false, null, CONTACT_LIST_NAME, true, null, null, null, null, false,
						RECIPIENT_EMAIL, null
				),
				arguments(
						"Non-guest with contact list share - show recipient details",
						false, CONTACT_LIST_UUID, CONTACT_LIST_NAME, false, null, null, null, null, false,
						RECIPIENT_EMAIL, null
				),
				arguments(
						"Guest with individual share - show recipient details",
						true, null, CONTACT_LIST_NAME, true, null, null, null, null, false,
						RECIPIENT_EMAIL, null
				),
				arguments(
						"Guest with visible contact list - show recipient details",
						true, CONTACT_LIST_UUID, CONTACT_LIST_NAME, true, null, null, null, null, false,
						RECIPIENT_EMAIL, null
				),
				arguments(
						"Guest with invisible contact list (via list only) - show list name",
						true, CONTACT_LIST_UUID, CONTACT_LIST_NAME, false, null, null, null, null, true,
						null, CONTACT_LIST_NAME
				),
				arguments(
						"Guest with invisible contact list BUT explicit contact - show recipient email",
						true, null, CONTACT_LIST_NAME, false, null, null, null, null, true,
						RECIPIENT_EMAIL, null
				),
				arguments(
						"Guest with invisible contact list and recipient not in list - show list name",
						true, CONTACT_LIST_UUID, CONTACT_LIST_NAME, false, null, null, null, null, false,
						null, CONTACT_LIST_NAME
				),
				arguments(
						"Guest with deleted contact list - show fallback with UUID when audit log empty",
						true, CONTACT_LIST_UUID, null, false,
						BusinessErrorCode.LIST_DO_NOT_EXIST, "Contact list not found",
						Optional.empty(), null, false,
						null, CONTACT_LIST_UUID
				),
				arguments(
						"Guest with deleted contact list - show fallback with UUID on audit log error",
						true, CONTACT_LIST_UUID, null, false,
						BusinessErrorCode.LIST_DO_NOT_EXIST, "Contact list not found",
						null, new RuntimeException("Audit log error"), false,
						null, CONTACT_LIST_UUID
				),
				arguments(
						"Exception handling - fallback to recipient email",
						true, CONTACT_LIST_UUID, CONTACT_LIST_NAME, false,
						null, null, null, new RuntimeException("Database error"), false,
						RECIPIENT_EMAIL, null
				),
				arguments(
						"Null contact list handling - fallback to recipient email",
						true, CONTACT_LIST_UUID, null, false, null, null, null, null, false,
						RECIPIENT_EMAIL, null
				),
				arguments(
						"Empty contact list UUID handling - show recipient email",
						true, "   ", CONTACT_LIST_NAME, true, null, null, null, null, false,
						RECIPIENT_EMAIL, null
				),
				arguments(
						"AccountContactLists with null visibility setting - show recipient email",
						true, CONTACT_LIST_UUID, CONTACT_LIST_NAME, null, null, null, null, null, false,
						RECIPIENT_EMAIL, null
				),
				arguments(
						"Other BusinessExceptions - fallback to recipient email",
						true, CONTACT_LIST_UUID, CONTACT_LIST_NAME, false,
						BusinessErrorCode.INVALID_CONFIGURATION, "Invalid configuration",
						null, null, false,
						RECIPIENT_EMAIL, null
				)
		);
	}

	/**
	 * Comprehensive parameterized test for all scenarios.
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("comprehensiveRecipientScenarios")
	void createRecipientDataAgainstContactListViewStatus(
			@Nonnull final String scenario,
			final boolean ownerIsGuest,
			@Nullable final String contactListUuid,
			@Nullable final String contactListName,
			@Nullable final Boolean canViewMembers,
			@Nullable final BusinessErrorCode businessErrorCode,
			@Nullable final String businessErrorMessage,
			@Nullable final Optional<String> auditLogName,
			@Nullable final RuntimeException runtimeException,
			final boolean isRecipientInContactList,
			@Nullable final String expectedEmail,
			@Nullable final String expectedContactListName) {

		final ShareEntry shareEntry = createShareEntry(ownerIsGuest, contactListUuid);
		final ShareFileDownloadEmailContext context = new ShareFileDownloadEmailContext(
				shareEntry, this.mailingListBusinessService, this.accountService, this.auditLogEntryService);

		if (contactListUuid != null && !contactListUuid.trim().isEmpty()) {
			if (businessErrorCode != null) {
				BusinessException businessException = new BusinessException(businessErrorCode, businessErrorMessage);
				lenient().when(this.mailingListBusinessService.findByUuid(contactListUuid))
						.thenThrow(businessException);
				if (businessErrorCode == BusinessErrorCode.LIST_DO_NOT_EXIST) {
					if (runtimeException != null && runtimeException.getMessage().contains("Audit log")) {
						when(this.auditLogEntryService.findLastDeletedContactListName(contactListUuid))
								.thenThrow(runtimeException);
					} else if (auditLogName != null) {
						when(this.auditLogEntryService.findLastDeletedContactListName(contactListUuid))
								.thenReturn(auditLogName);
					} else {
						lenient().when(this.auditLogEntryService.findLastDeletedContactListName(contactListUuid))
								.thenReturn(Optional.empty());
					}
				}
			} else if (runtimeException != null) {
				when(this.mailingListBusinessService.findByUuid(contactListUuid))
						.thenThrow(runtimeException);
			} else if (contactListName == null) {
				when(this.mailingListBusinessService.findByUuid(contactListUuid))
						.thenReturn(null);
			} else {
				final ContactList contactList = EmailTestUtils.createContactList(
						contactListName, contactListUuid,
						isRecipientInContactList ? Arrays.asList(RECIPIENT_EMAIL) : Arrays.asList("other@domain.com"));

				lenient().when(this.mailingListBusinessService.findByUuid(contactListUuid))
						.thenReturn(contactList);

				if (canViewMembers != null) {
					final AccountContactLists acl = EmailTestUtils.createAccountContactLists(contactList, canViewMembers);
					lenient().when(this.accountService.findAccountContactListByAccountAndContactList(
									any(Account.class), eq(contactList)))
							.thenReturn(Optional.of(acl));
					lenient().when(this.auditLogEntryService.canViewContactListMembers(acl))
						.thenReturn(canViewMembers);
				} else if (canViewMembers == null) {
					final AccountContactLists acl = mock(AccountContactLists.class);
					lenient().when(acl.getContactList()).thenReturn(contactList);
					lenient().when(acl.getCanViewContactListMembers()).thenReturn(null);
					when(this.accountService.findAccountContactListByAccountAndContactList(
							any(Account.class), eq(contactList)))
							.thenReturn(Optional.of(acl));
					lenient().when(this.auditLogEntryService.canViewContactListMembers(acl))
						.thenReturn(true);
				} else {
					when(this.accountService.findAccountContactListByAccountAndContactList(
							any(Account.class), eq(contactList)))
							.thenReturn(Optional.empty());
				}
			}
		}
		final MailContact result = context.createRecipientDataAgainstContactListViewStatus();
		if (expectedEmail != null) {
			assertEquals(expectedEmail, result.getMail(),
					"Email should match expected for scenario: " + scenario);
			assertNull(result.getContactListName(),
					"Contact list name should be null when showing email for scenario: " + scenario);
		} else {
			assertNull(result.getMail(),
					"Email should be null when showing contact list name for scenario: " + scenario);
			assertEquals(expectedContactListName, result.getContactListName(),
					"Contact list name should match expected for scenario: " + scenario);
		}
	}

	/**
	 * Test that verifies createRecipientDataAgainstContactListViewStatus() is forbidden for anonymous shares.
	 * This method should only be called with ShareEntry (non-anonymous).
	 * An IllegalArgumentException must be thrown when called with AnonymousShareEntry.
	 */
	@Test
	void createRecipientDataAgainstContactListViewStatusThrowsForAnonymousShare() {
		final AnonymousShareEntry anonymousShareEntry = createAnonymousShareEntry("anonymous@external.com");
		final ShareFileDownloadEmailContext context = new ShareFileDownloadEmailContext(anonymousShareEntry);
		assertTrue(context.getAnonymous());
		assertTrue(assertThrows(IllegalArgumentException.class, context::createRecipientDataAgainstContactListViewStatus).getMessage().contains("AnonymousShareEntry forbidden here"));
	}

	/**
	 * Provides test scenarios for getRecipient() with anonymous shares.
	 */
	@Nonnull
	private static Stream<Arguments> getRecipientAnonymousScenarios() {
		return Stream.of(
			arguments("Anonymous contact with simple email", "anonymous@external.com"),
			arguments("Anonymous contact with different domain", "guest@external.org"),
			arguments("Anonymous contact with numeric email", "user123@test.com")
		);
	}

	/**
	 * Test that verifies getRecipient() works correctly for anonymous shares.
	 * For anonymous shares, getRecipient() returns contact information from AnonymousUrl.
	 * Note: Contact entity only contains email field, so firstName and lastName are null.
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("getRecipientAnonymousScenarios")
	void getRecipientForAnonymousShare(
		@Nonnull final String scenario,
		@Nonnull final String expectedEmail) {

		final AnonymousShareEntry anonymousShareEntry = createAnonymousShareEntry(expectedEmail);
		final ShareFileDownloadEmailContext context = new ShareFileDownloadEmailContext(anonymousShareEntry);
		assertTrue(context.getAnonymous(), "Context should be flagged as anonymous for scenario: " + scenario);

		final MailContact result = context.getRecipient();
		assertEquals(expectedEmail, result.getMail(), "Email should match for scenario: " + scenario);
		assertNull(result.getFirstName(), "First name should be null (Contact entity has no name fields)");
		assertNull(result.getLastName(), "Last name should be null (Contact entity has no name fields)");
	}

	/**
	 * Creates a mock {@link AnonymousShareEntry} for testing anonymous scenarios.
	 * Note: Contact entity only contains email field, not first/last names.
	 */
	private static @Nonnull AnonymousShareEntry createAnonymousShareEntry(final @Nonnull String email) {
		final AnonymousShareEntry anonymousShareEntry = mock(AnonymousShareEntry.class);
		final AnonymousUrl anonymousUrl = mock(AnonymousUrl.class);
		final Contact contact = mock(Contact.class);
		final Account owner = EmailTestUtils.createMockUser(false);
		final DocumentEntry documentEntry = mock(DocumentEntry.class);
		lenient().when(contact.getMail()).thenReturn(email);
		lenient().when(anonymousUrl.getContact()).thenReturn(contact);
		lenient().when(anonymousShareEntry.getAnonymousUrl()).thenReturn(anonymousUrl);
		lenient().when(anonymousShareEntry.getEntryOwner()).thenReturn(owner);
		lenient().when(anonymousShareEntry.getDocumentEntry()).thenReturn(documentEntry);

		return anonymousShareEntry;
	}
}

