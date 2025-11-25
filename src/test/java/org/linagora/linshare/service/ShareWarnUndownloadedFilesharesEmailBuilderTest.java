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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.ShareWarnUndownloadedFilesharesEmailContext;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.ContactListService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShareWarnUndownloadedFilesharesEmailBuilderTest {

	private static final String USER1_EMAIL = "user1@domain.com";
	private static final String USER2_EMAIL = "user2@domain.com";
	private static final String USER3_EMAIL = "user3@domain.com";
	private static final String EXTERNAL_USER_EMAIL = "external@domain.com";
	private static final String VISIBLE_TEAM_NAME = "Development Team";
	private static final String INVISIBLE_TEAM_NAME = "Management Team";
	private static final String CONTACT_LIST_UUID_1 = "uuid-list-1";

	@Mock
	private ContactListService contactListService;

	@Mock
	private AccountService accountService;

	@Mock
	private FunctionalityReadOnlyService functionalityReadOnlyService;

	@Mock
	private AuditLogEntryService auditLogEntryService;

	/**
	 * Creates a mock {@link ShareEntry} for a recipient from a contact list.
	 *
	 * @param recipientEmail the email of the recipient
	 * @param contactListUuid the UUID of the contact list (null for individual shares)
	 * @param downloaded the download count
	 * @return a mocked {@link ShareEntry} instance
	 */
	private static ShareEntry createShareEntry(@Nonnull final String recipientEmail,
			@Nullable final String contactListUuid,
			long downloaded) {
		final ShareEntry shareEntry = mock(ShareEntry.class);
		final User recipient = mock(User.class);
		when(recipient.getMail()).thenReturn(recipientEmail);
		when(shareEntry.getRecipient()).thenReturn(recipient);
		when(shareEntry.getContactListUuid()).thenReturn(contactListUuid);
		when(shareEntry.getDownloaded()).thenReturn(downloaded);
		return shareEntry;
	}

	/**
	 * Provides test scenarios for parameterized testing of recipient visibility logic.
	 */
	private static Stream<Arguments> guestSharingScenarios() {
		return Stream.of(
				arguments(
						"Guest shares with visible contact list - show all members",
						true,
						Arrays.asList(
								createShareEntry(USER1_EMAIL, CONTACT_LIST_UUID_1, 0),
								createShareEntry(USER2_EMAIL, CONTACT_LIST_UUID_1, 0)
						),
						VISIBLE_TEAM_NAME,
						CONTACT_LIST_UUID_1,
						true,
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						Collections.emptyList(),
						null,
						null,
						null
				),
				arguments(
						"Guest shares with invisible contact list - show only list name",
						true,
						Arrays.asList(
								createShareEntry(USER1_EMAIL, CONTACT_LIST_UUID_1, 0),
								createShareEntry(USER2_EMAIL, CONTACT_LIST_UUID_1, 0)
						),
						INVISIBLE_TEAM_NAME,
						CONTACT_LIST_UUID_1,
						false,
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						Collections.emptyList(),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						INVISIBLE_TEAM_NAME,
						2,
						2
				),
				arguments(
						"Guest shares with invisible list where some members downloaded",
						true,
						Arrays.asList(
								createShareEntry(USER1_EMAIL, CONTACT_LIST_UUID_1, 1),
								createShareEntry(USER2_EMAIL, CONTACT_LIST_UUID_1, 0),
								createShareEntry(USER3_EMAIL, CONTACT_LIST_UUID_1, 0)
						),
						INVISIBLE_TEAM_NAME,
						CONTACT_LIST_UUID_1,
						false,
						Arrays.asList(USER1_EMAIL, USER2_EMAIL, USER3_EMAIL),
						Collections.emptyList(),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL, USER3_EMAIL),
						INVISIBLE_TEAM_NAME,
						2,
						3
				),
				arguments(
						"Guest shares with invisible list + external contact",
						true,
						Arrays.asList(
								createShareEntry(USER1_EMAIL, CONTACT_LIST_UUID_1, 0),
								createShareEntry(USER2_EMAIL, CONTACT_LIST_UUID_1, 0),
								createShareEntry(EXTERNAL_USER_EMAIL, null, 0)
						),
						INVISIBLE_TEAM_NAME,
						CONTACT_LIST_UUID_1,
						false,
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						Arrays.asList(EXTERNAL_USER_EMAIL),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						INVISIBLE_TEAM_NAME,
						2,
						2
				),
				arguments(
						"Guest shares with invisible list + explicit contact from same list - show list name and explicit contact",
						true,
						Arrays.asList(
								createShareEntry(USER1_EMAIL, CONTACT_LIST_UUID_1, 0),
								createShareEntry(USER2_EMAIL, CONTACT_LIST_UUID_1, 0),
								createShareEntry(USER3_EMAIL, null, 0)
						),
						INVISIBLE_TEAM_NAME,
						CONTACT_LIST_UUID_1,
						false,
						Arrays.asList(USER1_EMAIL, USER2_EMAIL, USER3_EMAIL),
						Arrays.asList(USER3_EMAIL),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						INVISIBLE_TEAM_NAME,
						2,
						2
				),
				arguments(
						"Non-guest with visible contact list - show all members",
						false,
						Arrays.asList(
								createShareEntry(USER1_EMAIL, CONTACT_LIST_UUID_1, 0),
								createShareEntry(USER2_EMAIL, CONTACT_LIST_UUID_1, 0)
						),
						VISIBLE_TEAM_NAME,
						CONTACT_LIST_UUID_1,
						true,
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						Collections.emptyList(),
						null,
						null,
						null
				),
				arguments(
						"Non-guest with invisible contact list - show all members",
						false,
						Arrays.asList(
								createShareEntry(USER1_EMAIL, CONTACT_LIST_UUID_1, 0),
								createShareEntry(USER2_EMAIL, CONTACT_LIST_UUID_1, 0)
						),
						INVISIBLE_TEAM_NAME,
						CONTACT_LIST_UUID_1,
						false,
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						Collections.emptyList(),
						null,
						null,
						null
				),arguments(
						"Guest shares with invisible list + external explicit contact - show list name and external contact",
						true,
						Arrays.asList(
								createShareEntry(USER1_EMAIL, CONTACT_LIST_UUID_1, 0),
								createShareEntry(USER2_EMAIL, CONTACT_LIST_UUID_1, 0),
								createShareEntry(EXTERNAL_USER_EMAIL, null, 0)
						),
						INVISIBLE_TEAM_NAME,
						CONTACT_LIST_UUID_1,
						false,
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						Arrays.asList(EXTERNAL_USER_EMAIL),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						INVISIBLE_TEAM_NAME,
						2,
						2
				)
		);
	}

	/**
	 * Provides test scenarios for deleted contact lists.
	 */
	private static Stream<Arguments> deletedContactListScenarios() {
		return Stream.of(
				arguments(
						"Guest shares with deleted contact list - show 'Deleted List' with UUID as fallback",
						true,
						Arrays.asList(
								createShareEntry(USER1_EMAIL, CONTACT_LIST_UUID_1, 0),
								createShareEntry(USER2_EMAIL, CONTACT_LIST_UUID_1, 0)
						),
						CONTACT_LIST_UUID_1,
						Optional.empty(),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						Collections.emptyList(),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						CONTACT_LIST_UUID_1,
						2,
						2
				),
				arguments(
						"Guest shares with deleted contact list - show last known name from audit logs with UUID",
						true,
						Arrays.asList(
								createShareEntry(USER1_EMAIL, CONTACT_LIST_UUID_1, 1),
								createShareEntry(USER2_EMAIL, CONTACT_LIST_UUID_1, 0),
								createShareEntry(USER3_EMAIL, CONTACT_LIST_UUID_1, 0)
						),
						CONTACT_LIST_UUID_1,
						Optional.empty(),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL, USER3_EMAIL),
						Collections.emptyList(),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL, USER3_EMAIL),
						CONTACT_LIST_UUID_1,
						2,
						3
				),
				arguments(
						"Guest shares with deleted contact list + external individual share",
						true,
						Arrays.asList(
								createShareEntry(USER1_EMAIL, CONTACT_LIST_UUID_1, 0),
								createShareEntry(USER2_EMAIL, CONTACT_LIST_UUID_1, 0),
								createShareEntry(EXTERNAL_USER_EMAIL, null, 0)
						),
						CONTACT_LIST_UUID_1,
						Optional.empty(),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						Arrays.asList(EXTERNAL_USER_EMAIL),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						CONTACT_LIST_UUID_1,
						2,
						2
				),
				arguments(
						"Guest shares with deleted contact list + explicit contact from same list",
						true,
						Arrays.asList(
								createShareEntry(USER1_EMAIL, CONTACT_LIST_UUID_1, 0),
								createShareEntry(USER2_EMAIL, CONTACT_LIST_UUID_1, 0),
								createShareEntry(USER3_EMAIL, null, 0)
						),
						CONTACT_LIST_UUID_1,
						Optional.empty(),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL, USER3_EMAIL),
						Arrays.asList(USER3_EMAIL),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						CONTACT_LIST_UUID_1,
						2,
						2
				)
		);
	}

	/**
	 * Creates a mock {@link ShareEntryGroup} with the specified share entries.
	 */
	private ShareEntryGroup createMockShareEntryGroup(
			@Nonnull final User owner,
			@Nonnull final List<ShareEntry> shareEntries) {
		final ShareEntryGroup shareEntryGroup = mock(ShareEntryGroup.class);
		when(shareEntryGroup.getOwner()).thenReturn(owner);
		final Set<ShareEntry> shareEntriesSet = new HashSet<>(shareEntries);
		lenient().when(shareEntryGroup.getShareEntries()).thenReturn(shareEntriesSet);
		final List<MailContact> allRecipients = shareEntries.stream()
				.filter(se -> se.getRecipient() != null)
				.map(se -> {
					final MailContact mc = new MailContact(se.getRecipient());
					if (se.getContactListUuid() != null) {
					}
					return mc;
				})
				.collect(Collectors.toList());
		lenient().when(shareEntryGroup.getAllRecipients()).thenReturn(allRecipients);
		final Set<String> explicitEmails = shareEntries.stream()
				.filter(se -> se.getRecipient() != null && se.getContactListUuid() == null)
				.map(se -> se.getRecipient().getMail().toLowerCase())
				.collect(Collectors.toSet());
		lenient().when(shareEntryGroup.getExplicitIndividualEmails()).thenReturn(explicitEmails);
		final Set<String> contactListUuids = shareEntries.stream()
				.map(ShareEntry::getContactListUuid)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
		lenient().when(shareEntryGroup.getContactListUuids()).thenReturn(contactListUuids);
		return shareEntryGroup;
	}

	/**
	 * Parameterized test for guest and non-guest sharing scenarios with contact lists.
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("guestSharingScenarios")
	void testSharingWithContactLists(
			@Nonnull final String scenario,
			boolean isGuest,
			@Nonnull final List<ShareEntry> shareEntries,
			@Nonnull final String listName,
			@Nonnull final String listUuid,
			boolean canViewMembers,
			@Nonnull final List<String> listMembers,
			@Nonnull final List<String> expectedVisibleEmails,
			@Nonnull final List<String> expectedHiddenEmails,
			final String expectedContactListName,
			final Integer expectedUndownloadedCount,
			final Integer expectedTotalMembersCount) throws Exception {

		final User owner = EmailTestUtils.createMockUser(isGuest);
		final ShareEntryGroup shareEntryGroup = createMockShareEntryGroup(owner, shareEntries);
		final ContactList contactList = EmailTestUtils.createContactList(listName, listUuid, listMembers);
		final AccountContactLists acl = EmailTestUtils.createAccountContactLists(contactList, canViewMembers);
		lenient().when(this.contactListService.findByUuid(anyString(), eq(listUuid)))
				.thenReturn(contactList);
		lenient().when(this.accountService.findAccountContactListByAccountAndContactList(
						any(Account.class), eq(contactList)))
				.thenReturn(Optional.of(acl));
		final ShareWarnUndownloadedFilesharesEmailContext context =
				new ShareWarnUndownloadedFilesharesEmailContext(
						shareEntryGroup,
						this.contactListService,
						this.accountService, this.functionalityReadOnlyService, this.auditLogEntryService);
		final List<MailContact> result = context.createRecipientDataAgainstContactListViewStatus();
		final List<String> resultEmails = EmailTestUtils.extractEmailsFromResult(result);
		final MailContact contactListEntry = extractContactListFromResult(result);
		assertThat(resultEmails)
				.as("Visible emails should match expected")
				.containsExactlyInAnyOrderElementsOf(EmailTestUtils.normalizeEmails(expectedVisibleEmails));
		if (!expectedHiddenEmails.isEmpty()) {
			assertThat(resultEmails)
					.as("Hidden emails should not be present")
					.doesNotContainAnyElementsOf(EmailTestUtils.normalizeEmails(expectedHiddenEmails));
		}
		if (expectedContactListName != null) {
			assertThat(contactListEntry)
					.as("Contact list entry should be present")
					.isNotNull();
			assertThat(contactListEntry.getContactListName())
					.as("Contact list name should match")
					.isEqualTo(expectedContactListName);
			if (expectedUndownloadedCount != null) {
				assertThat(contactListEntry.getNotDownloadedCount())
						.as("Undownloaded count should match")
						.isEqualTo(expectedUndownloadedCount);
			}
			if (expectedTotalMembersCount != null) {
				assertThat(contactListEntry.getTotalMembersCount())
						.as("Total members count should match")
						.isEqualTo(expectedTotalMembersCount);
			}
		} else {
			assertThat(contactListEntry)
					.as("Contact list entry should not be present")
					.isNull();
		}
		int expectedContactListCount = (expectedContactListName != null) ? 1 : 0;
		int expectedTotalItems = expectedVisibleEmails.size() + expectedContactListCount;
		assertThat(result)
				.as("Total result size should match")
				.hasSize(expectedTotalItems);
	}

	/**
	 * Parameterized test for deleted contact lists with audit log fallback.
	 */
	@ParameterizedTest(name = "Deleted contact list: {0}")
	@MethodSource("deletedContactListScenarios")
	void testDeletedContactListHandling(
			@Nonnull final String scenario,
			boolean isGuest,
			@Nonnull final List<ShareEntry> shareEntries,
			@Nonnull final String listUuid,
			@Nonnull final Optional<String> auditLogName,
			@Nonnull final List<String> listMembers,
			@Nonnull final List<String> expectedVisibleEmails,
			@Nonnull final List<String> expectedHiddenEmails,
			final String expectedContactListName,
			final Integer expectedUndownloadedCount,
			final Integer expectedTotalMembersCount) throws Exception {

		final User owner = EmailTestUtils.createMockUser(isGuest);
		final ShareEntryGroup shareEntryGroup = createMockShareEntryGroup(owner, shareEntries);
		final BusinessException notFoundException = new BusinessException(BusinessErrorCode.LIST_DO_NOT_EXIST, "Contact list not found");
		when(this.contactListService.findByUuid(anyString(), eq(listUuid)))
				.thenThrow(notFoundException);
		when(this.auditLogEntryService.findLastDeletedContactListName(listUuid))
				.thenReturn(auditLogName);
		final ShareWarnUndownloadedFilesharesEmailContext context =
				new ShareWarnUndownloadedFilesharesEmailContext(
						shareEntryGroup,
						this.contactListService,
						this.accountService,
						this.functionalityReadOnlyService,
						this.auditLogEntryService);
		final List<MailContact> result = context.createRecipientDataAgainstContactListViewStatus();
		result.forEach(mc -> System.out.println("MailContact - Email: " + mc.getMail()
				+ ", ContactListName: " + mc.getContactListName()
				+ ", NotDownloaded: " + mc.getNotDownloadedCount()
				+ ", TotalMembers: " + mc.getTotalMembersCount()));
		final List<String> resultEmails = EmailTestUtils.extractEmailsFromResult(result);
		final MailContact contactListEntry = extractContactListFromResult(result);
		assertThat(resultEmails)
				.as("Visible emails should match expected")
				.containsExactlyInAnyOrderElementsOf(EmailTestUtils.normalizeEmails(expectedVisibleEmails));
		if (!expectedHiddenEmails.isEmpty()) {
			assertThat(resultEmails)
					.as("Hidden emails should not be present")
					.doesNotContainAnyElementsOf(EmailTestUtils.normalizeEmails(expectedHiddenEmails));
		}
		assertThat(contactListEntry)
				.as("Contact list entry should be present for deleted list")
				.isNotNull();

		assertThat(contactListEntry.getContactListName())
				.as("Contact list name should match expected")
				.isEqualTo(expectedContactListName);

		if (expectedUndownloadedCount != null) {
			assertThat(contactListEntry.getNotDownloadedCount())
					.as("Undownloaded count should match")
					.isEqualTo(expectedUndownloadedCount);
		}

		if (expectedTotalMembersCount != null) {
			assertThat(contactListEntry.getTotalMembersCount())
					.as("Total members count should match")
					.isEqualTo(expectedTotalMembersCount);
		}
		int expectedContactListCount = 1;
		int expectedTotalItems = expectedVisibleEmails.size() + expectedContactListCount;
		assertThat(result)
				.as("Total result size should match")
				.hasSize(expectedTotalItems);
	}

	/**
	 * Extracts the first contact list entry from a list of {@link MailContact} objects.
	 */
	private MailContact extractContactListFromResult(@Nonnull final List<MailContact> result) {
		return result.stream()
				.filter(mc -> mc.getContactListName() != null)
				.findFirst()
				.orElse(null);
	}
}