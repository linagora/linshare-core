package org.linagora.linshare.service;

import static org.assertj.core.api.Assertions.assertThat;
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

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.linagora.linshare.core.business.service.MailingListBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountContactLists;
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
	 * Creates a mock {@link ShareEntry} for testing getRecipientWithVisibility.
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
	void testCreateRecipientDataAgainstContactListViewStatus(
			@Nonnull final String scenario,
			boolean ownerIsGuest,
			@Nullable final String contactListUuid,
			@Nullable final String contactListName,
			@Nullable final Boolean canViewMembers,
			@Nullable final BusinessErrorCode businessErrorCode,
			@Nullable final String businessErrorMessage,
			@Nullable final Optional<String> auditLogName,
			@Nullable final RuntimeException runtimeException,
			boolean isRecipientInContactList,
			@Nullable final String expectedEmail,
			@Nullable final String expectedContactListName) {

		final ShareEntry shareEntry = createShareEntry(ownerIsGuest, contactListUuid);
		final ShareFileDownloadEmailContext context = new ShareFileDownloadEmailContext(
				shareEntry, this.mailingListBusinessService, this.accountService, this.functionalityReadOnlyService, this.auditLogEntryService);

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
		} else {
			assertThat(result.getMail())
					.as("Email should be null when showing contact list name for scenario: " + scenario)
					.isNull();
			assertThat(result.getContactListName())
					.as("Contact list name should match expected for scenario: " + scenario)
					.isEqualTo(expectedContactListName);
		}
	}
}