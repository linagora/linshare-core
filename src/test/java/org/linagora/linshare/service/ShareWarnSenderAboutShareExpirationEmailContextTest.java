package org.linagora.linshare.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountContactLists;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.ShareWarnSenderAboutShareExpirationEmailContext;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.ContactListService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShareWarnSenderAboutShareExpirationEmailContextTest {

	private static final String USER_EMAIL = "user@domain.com";
	private static final String RECIPIENT_EMAIL = "recipient@domain.com";
	private static final String CONTACT_LIST_UUID = "uuid-list-1";
	private static final String CONTACT_LIST_NAME = "Development Team";

	@Mock
	private ContactListService contactListService;

	@Mock
	private AccountService accountService;

	@Mock
	private FunctionalityReadOnlyService functionalityReadOnlyService;

	@Mock
	private AuditLogEntryService auditLogEntryService;

	/**
	 * Creates a mock {@link ShareEntry} for testing.
	 *
	 * @param ownerIsGuest whether the entry owner is a guest account
	 * @param contactListUuid the UUID of the contact list (null for individual shares)
	 * @param recipientEmail the recipient email (null for testing null recipient cases)
	 * @return a mocked {@link ShareEntry} instance
	 */
	private static ShareEntry createShareEntry(boolean ownerIsGuest, @Nullable String contactListUuid, @Nullable String recipientEmail) {
		final ShareEntry shareEntry = mock(ShareEntry.class);
		final Account owner = mock(Account.class);
		final User recipient = recipientEmail != null ? mock(User.class) : null;

		lenient().when(owner.isGuest()).thenReturn(ownerIsGuest);
		lenient().when(owner.getLsUuid()).thenReturn("owner-uuid");
		lenient().when(owner.getMail()).thenReturn(USER_EMAIL);

		if (recipient != null) {
			lenient().when(recipient.getMail()).thenReturn(recipientEmail);
		}

		when(shareEntry.getEntryOwner()).thenReturn(owner);
		when(shareEntry.getRecipient()).thenReturn(recipient);
		when(shareEntry.getContactListUuid()).thenReturn(contactListUuid);
		lenient().when(shareEntry.getUuid()).thenReturn("share-uuid");

		return shareEntry;
	}

	/**
	 * Provides comprehensive test scenarios for all recipient visibility cases.
	 */
	private static Stream<Arguments> comprehensiveRecipientScenarios() {
		return Stream.of(
				arguments(
						"Non-guest with individual share - show recipient details",
						false, null, RECIPIENT_EMAIL, CONTACT_LIST_NAME, true, null, null, null, null, true,
						RECIPIENT_EMAIL, null
				),
				arguments(
						"Non-guest with contact list share - show recipient details",
						false, CONTACT_LIST_UUID, RECIPIENT_EMAIL, CONTACT_LIST_NAME, false, null, null, null, null, true,
						RECIPIENT_EMAIL, null
				),
				arguments(
						"Guest with individual share - show recipient details",
						true, null, RECIPIENT_EMAIL, CONTACT_LIST_NAME, true, null, null, null, null, true,
						RECIPIENT_EMAIL, null
				),
				arguments(
						"Guest with visible contact list - show recipient details",
						true, CONTACT_LIST_UUID, RECIPIENT_EMAIL, CONTACT_LIST_NAME, true, null, null, null, null, true,
						RECIPIENT_EMAIL, null
				),
				arguments(
						"Guest with invisible contact list - recipient IN list - show list name",
						true, CONTACT_LIST_UUID, RECIPIENT_EMAIL, CONTACT_LIST_NAME, false, null, null, null, null, true,
						null, CONTACT_LIST_NAME
				),
				arguments(
						"Guest with invisible contact list - recipient NOT in list - show recipient email",
						true, CONTACT_LIST_UUID, RECIPIENT_EMAIL, CONTACT_LIST_NAME, false, null, null, null, null, false,
						RECIPIENT_EMAIL, null
				),
				arguments(
						"Guest with invisible contact list share - recipient IN list - show list name",
						true, CONTACT_LIST_UUID, RECIPIENT_EMAIL, CONTACT_LIST_NAME, false, null, null, null, null, true,
						null, CONTACT_LIST_NAME
				),
				arguments(
						"Guest with individual share (explicit contact from same list) - show recipient email",
						true, null, RECIPIENT_EMAIL, CONTACT_LIST_NAME, true, null, null, null, null, true,
						RECIPIENT_EMAIL, null
				),
				arguments(
						"Guest with deleted contact list - show audit log name with UUID",
						true, CONTACT_LIST_UUID, RECIPIENT_EMAIL, null, false,
						BusinessErrorCode.LIST_DO_NOT_EXIST, "Contact list not found",
						Optional.empty(), null, true,
						null,CONTACT_LIST_UUID
				),
				arguments(
						"Guest with deleted contact list - show fallback name with UUID",
						true, CONTACT_LIST_UUID, RECIPIENT_EMAIL, null, false,
						BusinessErrorCode.LIST_DO_NOT_EXIST, "Contact list not found",
						Optional.empty(), null, true,
						null, CONTACT_LIST_UUID
				),
				arguments(
						"Unexpected exception handling - fallback to recipient email",
						true, CONTACT_LIST_UUID, RECIPIENT_EMAIL, CONTACT_LIST_NAME, false,
						null, null, null, new RuntimeException("Database connection failed"), true,
						RECIPIENT_EMAIL, null
				),
				arguments(
						"Other BusinessException handling - fallback to recipient email",
						true, CONTACT_LIST_UUID, RECIPIENT_EMAIL, CONTACT_LIST_NAME, false,
						BusinessErrorCode.INVALID_CONFIGURATION, "Invalid configuration",
						null, null, true,
						RECIPIENT_EMAIL, null
				),
				arguments(
						"Null recipient handling - both email and contact list name null",
						false, null, null, null, true, null, null, null, null, false,
						null, null
				),
				arguments(
						"Empty contact list UUID handling - show recipient email",
						true, "   ", RECIPIENT_EMAIL, CONTACT_LIST_NAME, true, null, null, null, null, true,
						RECIPIENT_EMAIL, null
				),
				arguments(
						"Null contact list with guest - show recipient email",
						true, CONTACT_LIST_UUID, RECIPIENT_EMAIL, null, false, null, null, null, null, true,
						RECIPIENT_EMAIL, null
				),
				arguments(
						"Guest with invisible contact list and null recipient email - show list name",
						true, CONTACT_LIST_UUID, null, CONTACT_LIST_NAME, false, null, null, null, null, true,
						null, CONTACT_LIST_NAME
				),
				arguments(
						"Guest with invisible contact list and empty recipient email - show list name",
						true, CONTACT_LIST_UUID, "", CONTACT_LIST_NAME, false, null, null, null, null, true,
						null, CONTACT_LIST_NAME
				),
				arguments(
						"Guest with invisible contact list - case insensitive matching",
						true, CONTACT_LIST_UUID, "Recipient@Domain.COM", CONTACT_LIST_NAME, false, null, null, null, null, true,
						null, CONTACT_LIST_NAME
				),
				arguments(
						"Guest with invisible contact list - recipient with different email - show recipient email",
						true, CONTACT_LIST_UUID, "different@domain.com", CONTACT_LIST_NAME, false, null, null, null, null, false,
						"different@domain.com", null
				),
				arguments(
						"Guest with AccountContactLists containing null visibility - use domain functionality",
						true, CONTACT_LIST_UUID, RECIPIENT_EMAIL, CONTACT_LIST_NAME, null, null, null, null, null, true,
						RECIPIENT_EMAIL, null
				),
				arguments(
						"Guest with empty AccountContactLists Optional - use domain functionality",
						true, CONTACT_LIST_UUID, RECIPIENT_EMAIL, CONTACT_LIST_NAME, false, null, null, null, null, true,
						null, CONTACT_LIST_NAME
				),
				arguments(
						"Non-guest with deleted contact list - show recipient details",
						false, CONTACT_LIST_UUID, RECIPIENT_EMAIL, null, false,
						BusinessErrorCode.LIST_DO_NOT_EXIST, "Contact list not found",
						null, null, true,
						RECIPIENT_EMAIL, null
				),
				arguments(
						"Guest with deleted contact list but individual share - show recipient email",
						true, null, RECIPIENT_EMAIL, CONTACT_LIST_NAME, true, null, null, null, null, true,
						RECIPIENT_EMAIL, null
				)
		);
	}

	/**
	 * Comprehensive parameterized test for all recipient visibility scenarios.
	 *
	 * @param scenario descriptive name of the test scenario
	 * @param ownerIsGuest whether the entry owner is a guest account
	 * @param contactListUuid the UUID of the contact list (null for individual shares)
	 * @param recipientEmail the recipient email (null for null recipient cases)
	 * @param contactListName the name of the contact list (null for deleted lists)
	 * @param canViewMembers whether members are visible (for guest with contact list)
	 * @param businessErrorCode the business error code for exception scenarios
	 * @param businessErrorMessage the business error message
	 * @param auditLogName the name from audit logs for deleted lists
	 * @param runtimeException the runtime exception for unexpected error scenarios
	 * @param isRecipientInContactList whether the recipient is in the contact list
	 * @param expectedEmail the expected email in result (null if contact list name expected)
	 * @param expectedContactListName the expected contact list name in result (null if email expected)
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("comprehensiveRecipientScenarios")
	void testCreateRecipientDataAgainstContactListViewStatus(
			@Nonnull final String scenario,
			boolean ownerIsGuest,
			@Nullable final String contactListUuid,
			@Nullable final String recipientEmail,
			@Nullable final String contactListName,
			@Nullable final Boolean canViewMembers,
			@Nullable final BusinessErrorCode businessErrorCode,
			@Nullable final String businessErrorMessage,
			@Nullable final Optional<String> auditLogName,
			@Nullable final RuntimeException runtimeException,
			boolean isRecipientInContactList,
			@Nullable final String expectedEmail,
			@Nullable final String expectedContactListName) throws Exception {

		final ShareEntry shareEntry = createShareEntry(ownerIsGuest, contactListUuid, recipientEmail);
		final ShareWarnSenderAboutShareExpirationEmailContext context = createEmailContext(shareEntry);

		if (contactListUuid != null && !contactListUuid.trim().isEmpty()) {
			if (businessErrorCode != null) {
				BusinessException businessException = new BusinessException(businessErrorCode, businessErrorMessage);
				lenient().when(this.contactListService.findByUuid(anyString(), eq(contactListUuid)))
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
				when(this.contactListService.findByUuid(anyString(), eq(contactListUuid)))
						.thenThrow(runtimeException);
			} else if (contactListName != null) {
				final java.util.List<String> contactEmails;
				if (isRecipientInContactList && recipientEmail != null) {
					contactEmails = Arrays.asList(recipientEmail);
				} else {
					contactEmails = Arrays.asList("other@domain.com");
				}
				final ContactList contactList = EmailTestUtils.createContactList(
						contactListName, contactListUuid,
						contactEmails
						);

				lenient().when(this.contactListService.findByUuid(anyString(), eq(contactListUuid)))
						.thenReturn(contactList);

				if (canViewMembers != null) {
					final AccountContactLists acl = EmailTestUtils.createAccountContactLists(contactList, canViewMembers);
					lenient().when(this.accountService.findAccountContactListByAccountAndContactList(
									any(Account.class), eq(contactList)))
							.thenReturn(Optional.of(acl));
				} else if (canViewMembers == null) {
					final AccountContactLists acl = mock(AccountContactLists.class);
					lenient().when(acl.getContactList()).thenReturn(contactList);
					lenient().when(acl.getCanViewContactListMembers()).thenReturn(null);
					when(this.accountService.findAccountContactListByAccountAndContactList(
							any(Account.class), eq(contactList)))
							.thenReturn(Optional.of(acl));
				} else {
					when(this.accountService.findAccountContactListByAccountAndContactList(
							any(Account.class), eq(contactList)))
							.thenReturn(Optional.empty());
				}
			} else {
				when(this.contactListService.findByUuid(anyString(), eq(contactListUuid)))
						.thenReturn(null);
			}
		}

		final MailContact result = context.createRecipientDataAgainstContactListViewStatus();

		if (expectedEmail != null) {
			assertThat(result.getMail())
					.as("Email should match expected for scenario: " + scenario)
					.isEqualTo(expectedEmail);
			assertThat(result.getContactListName())
					.as("Contact list name should be null when showing email for scenario: " + scenario)
					.isNull();
		} else if (expectedContactListName != null) {
			assertThat(result.getMail())
					.as("Email should be null when showing contact list name for scenario: " + scenario)
					.isNull();
			assertThat(result.getContactListName())
					.as("Contact list name should match expected for scenario: " + scenario)
					.isEqualTo(expectedContactListName);
		} else {
			assertThat(result.getMail())
					.as("Email should be null for null recipient in scenario: " + scenario)
					.isNull();
			assertThat(result.getContactListName())
					.as("Contact list name should be null for null recipient in scenario: " + scenario)
					.isNull();
		}
	}

	/**
	 * Creates a ShareWarnSenderAboutShareExpirationEmailContext instance for testing.
	 */
	private ShareWarnSenderAboutShareExpirationEmailContext createEmailContext(final @Nonnull ShareEntry shareEntry) {
		return new ShareWarnSenderAboutShareExpirationEmailContext(
				shareEntry,
				this.contactListService,
				this.accountService,
				this.functionalityReadOnlyService,
				this.auditLogEntryService,
				7
		);
	}
}