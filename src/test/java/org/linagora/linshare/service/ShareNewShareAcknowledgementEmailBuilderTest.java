package org.linagora.linshare.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.linagora.linshare.core.domain.entities.AccountContactLists;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.notifications.context.ShareNewShareAcknowledgementEmailContext;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests recipient visibility rules in share acknowledgement emails,
 * ensuring correct behavior for guest and non-guest users.
 */
@ExtendWith(MockitoExtension.class)
public class ShareNewShareAcknowledgementEmailBuilderTest {

	private static final String USER1_EMAIL = "user1@domain.com";
	private static final String USER2_EMAIL = "user2@domain.com";
	private static final String USER3_EMAIL = "user3@domain.com";
	private static final String EXTERNAL_USER_EMAIL = "external@domain.com";
	private static final String VISIBLE_TEAM_NAME = "Development Team";
	private static final String INVISIBLE_TEAM_NAME = "Management Team";
	private static final String INVISIBLE_TEAM_2_NAME = "HR Team";

	/**
	 * Provides test scenarios for parameterized testing of recipient visibility logic.
	 */
	 private static Stream<Arguments> guestSharingScenarios() {
		return Stream.of(
				arguments(
						"Guest shares with visible contact list - show all members",
						true,
						Arrays.asList(EmailTestUtils.createAccountContactListsWithEmbeddedList(VISIBLE_TEAM_NAME, true,
								Arrays.asList(USER1_EMAIL, USER2_EMAIL))),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						Collections.emptySet(),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						Collections.emptyList(),
						Collections.emptyList()
				),
				arguments(
						"Guest shares with invisible contact list - show only list name",
						true,
						Arrays.asList(EmailTestUtils.createAccountContactListsWithEmbeddedList(INVISIBLE_TEAM_NAME, false,
								Arrays.asList(USER1_EMAIL, USER2_EMAIL))),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						Collections.emptySet(),
						Collections.emptyList(),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						Arrays.asList(INVISIBLE_TEAM_NAME)
				),
				arguments(
						"Guest shares with mixed visible and invisible lists",
						true,
						Arrays.asList(
								EmailTestUtils.createAccountContactListsWithEmbeddedList(VISIBLE_TEAM_NAME, true, Arrays.asList(USER1_EMAIL)),
								EmailTestUtils.createAccountContactListsWithEmbeddedList(INVISIBLE_TEAM_NAME, false, Arrays.asList(USER2_EMAIL, USER3_EMAIL))
						),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL, USER3_EMAIL),
						Collections.emptySet(),
						Arrays.asList(USER1_EMAIL),
						Arrays.asList(USER2_EMAIL, USER3_EMAIL),
						Arrays.asList(INVISIBLE_TEAM_NAME)
				),
				arguments(
						"Guest shares with invisible list + explicit individual contact from same list",
						true,
						Arrays.asList(EmailTestUtils.createAccountContactListsWithEmbeddedList(INVISIBLE_TEAM_NAME, false,
								Arrays.asList(USER1_EMAIL, USER2_EMAIL))),
						Arrays.asList(USER1_EMAIL),
						new HashSet<>(Arrays.asList(USER1_EMAIL)),
						Arrays.asList(USER1_EMAIL),
						Arrays.asList(USER2_EMAIL),
						Arrays.asList(INVISIBLE_TEAM_NAME)
				),
				arguments(
						"Guest shares with multiple invisible lists",
						true,
						Arrays.asList(
								EmailTestUtils.createAccountContactListsWithEmbeddedList(INVISIBLE_TEAM_NAME, false, Arrays.asList(USER1_EMAIL)),
								EmailTestUtils.createAccountContactListsWithEmbeddedList(INVISIBLE_TEAM_2_NAME, false, Arrays.asList(USER2_EMAIL))
						),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						Collections.emptySet(),
						Collections.emptyList(),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						Arrays.asList(INVISIBLE_TEAM_NAME, INVISIBLE_TEAM_2_NAME)
				),
				arguments(
						"Guest shares with invisible list + external contact",
						true,
						Arrays.asList(EmailTestUtils.createAccountContactListsWithEmbeddedList(INVISIBLE_TEAM_NAME, false,
								Arrays.asList(USER1_EMAIL, USER2_EMAIL))),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL, EXTERNAL_USER_EMAIL),
						Collections.emptySet(),
						Arrays.asList(EXTERNAL_USER_EMAIL),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						Arrays.asList(INVISIBLE_TEAM_NAME)
				),
				arguments(
						"Non-guest with visible contact list - show all members",
						false,
						Arrays.asList(EmailTestUtils.createAccountContactListsWithEmbeddedList(VISIBLE_TEAM_NAME, true,
								Arrays.asList(USER1_EMAIL, USER2_EMAIL))),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						Collections.emptySet(),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						Collections.emptyList(),
						Collections.emptyList()
				), arguments(
						"Non-guest with invisible contact list - show all members",
						false,
						Arrays.asList(EmailTestUtils.createAccountContactListsWithEmbeddedList(INVISIBLE_TEAM_NAME, false,
								Arrays.asList(USER1_EMAIL, USER2_EMAIL))),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						Collections.emptySet(),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						Collections.emptyList(),
						Collections.emptyList()
				),
				arguments(
						"Non-guest with mixed contact lists - show all members",
						false,
						Arrays.asList(
								EmailTestUtils.createAccountContactListsWithEmbeddedList(VISIBLE_TEAM_NAME, true, Arrays.asList(USER1_EMAIL)),
								EmailTestUtils.createAccountContactListsWithEmbeddedList(INVISIBLE_TEAM_NAME, false, Arrays.asList(USER2_EMAIL))
						),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						Collections.emptySet(),
						Arrays.asList(USER1_EMAIL, USER2_EMAIL),
						Collections.emptyList(),
						Collections.emptyList()
				)
		);
	}

	/**
	 * Ensures that non-guest users receive all recipients in the acknowledgement email,
	 * ignoring any contact list visibility restrictions.
	 */
	@Test
	public void testNonGuestUser_ReturnsAllRecipients_RegardlessOfContactLists() {
		final User nonGuestUser = mock(User.class);
		when(nonGuestUser.isGuest()).thenReturn(false);
		final ShareContainer shareContainer = mock(ShareContainer.class);
		final List<MailContact> expectedRecipients = Arrays.asList(
				new MailContact(USER1_EMAIL),
				new MailContact(USER2_EMAIL)
		);
		when(shareContainer.getMailContactRecipients()).thenReturn(expectedRecipients);
		final ShareNewShareAcknowledgementEmailContext context =
				new ShareNewShareAcknowledgementEmailContext(nonGuestUser, shareContainer, new HashSet<>());
		final List<MailContact> result = context.getRecipientsWithVisibility();
		assertThat(result).isEqualTo(expectedRecipients);
	}

	/**
	 * Parameterized test for guest sharing scenarios with contact lists.
	 *
	 * <p>Tests the {@link ShareNewShareAcknowledgementEmailContext#getRecipientsWithVisibility()} method
	 * with various configurations of contact lists and recipient visibility rules.</p>
	 *
	 * @param scenario descriptive name of the test scenario (required)
	 * @param isGuest whether the user is a guest account
	 * @param accountContactLists list of account-contact list relationships to test (required)
	 * @param allRecipientEmails all recipient email addresses in the share (required)
	 * @param explicitEmails emails that are explicitly shared (not via contact lists) (required)
	 * @param expectedVisibleEmails emails expected to be visible in the result (required)
	 * @param expectedHiddenEmails emails expected to be hidden from the result (required)
	 * @param expectedContactListNames contact list names expected to appear in the result (required)
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("guestSharingScenarios")
	void testGuestSharingWithContactLists(
			@Nonnull final String scenario,
			boolean isGuest,
			@Nonnull final List<AccountContactLists> accountContactLists,
			@Nonnull final List<String> allRecipientEmails,
			@Nonnull final Set<String> explicitEmails,
			@Nonnull final List<String> expectedVisibleEmails,
			@Nonnull final List<String> expectedHiddenEmails,
			@Nonnull final List<String> expectedContactListNames) {
		final User user = EmailTestUtils.createMockUser(isGuest);
		final ShareContainer shareContainer = this.createMockShareContainer(accountContactLists, explicitEmails, allRecipientEmails);
		final ShareNewShareAcknowledgementEmailContext context =
				new ShareNewShareAcknowledgementEmailContext(user, shareContainer, new HashSet<>());
		final List<MailContact> result = context.getRecipientsWithVisibility();
		final List<String> resultEmails = EmailTestUtils.extractEmailsFromResult(result);
		final List<String> resultContactListNames = EmailTestUtils.extractContactListNamesFromResult(result);
		assertThat(resultEmails).containsExactlyInAnyOrderElementsOf(EmailTestUtils.normalizeEmails(expectedVisibleEmails));
		if (!expectedHiddenEmails.isEmpty()) {
			assertThat(resultEmails)
					.doesNotContainAnyElementsOf(EmailTestUtils.normalizeEmails(expectedHiddenEmails));
		}
		assertThat(resultContactListNames)
				.containsExactlyInAnyOrderElementsOf(expectedContactListNames);
		int expectedTotalItems = expectedVisibleEmails.size() + expectedContactListNames.size();
		assertThat(result).hasSize(expectedTotalItems);
	}

	/**
	 * Creates a mock {@link ShareContainer} with the specified contact lists and recipients.
	 *
	 * @param accountContactLists the account-contact list relationships to configure
	 * @param explicitEmails emails that are explicitly shared individually
	 * @param allRecipientEmails all recipient email addresses in the share
	 * @return a mocked {@link ShareContainer} instance with the specified configuration
	 */
	private ShareContainer createMockShareContainer(@Nonnull final List<AccountContactLists> accountContactLists,
			@Nonnull final Set<String> explicitEmails,
			@Nonnull final List<String> allRecipientEmails) {
		final ShareContainer shareContainer = mock(ShareContainer.class);
		final List<MailContact> allRecipients = allRecipientEmails.stream()
				.map(email -> {
					final MailContact contact = new MailContact();
					contact.setMail(email);
					return contact;
				})
				.collect(Collectors.toList());
		when(shareContainer.getMailContactRecipients()).thenReturn(allRecipients);
		lenient().when(shareContainer.getAccountContactLists()).thenReturn(accountContactLists);
		lenient().when(shareContainer.getExplicitRecipientEmails()).thenReturn(explicitEmails);
		return shareContainer;
	}
}