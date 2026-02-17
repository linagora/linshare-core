package org.linagora.linshare.service;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AccountContactLists;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.ContactListContact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.notifications.dto.MailContact;

public class EmailTestUtils {
	private EmailTestUtils() {}

	/**
	 * Creates a mock {@link User} with the specified guest status.
	 *
	 * @param isGuest true if the user should be a guest, false for regular user
	 * @return a mocked {@link User} instance with the specified guest configuration
	 */
	public static @Nonnull User createMockUser(boolean isGuest) {
		final User user = mock(User.class);
		lenient().when(user.isGuest()).thenReturn(isGuest);
		final AbstractDomain domain = mock(AbstractDomain.class);
		when(user.getDomain()).thenReturn(domain);
		lenient().when(user.getLsUuid()).thenReturn("user-uuid");
		return user;
	}

	/**
	 * Extracts email addresses from a list of {@link MailContact} objects.
	 *
	 * @param result the list of mail contacts to process
	 * @return a list of lowercase email addresses extracted from the mail contacts
	 */
	public static List<String> extractEmailsFromResult(@Nonnull final List<MailContact> result) {
		return result.stream()
				.map(MailContact::getMail)
				.filter(Objects::nonNull)
				.map(String::toLowerCase)
				.collect(Collectors.toList());
	}

	/**
	 * Normalizes email addresses by converting them to lowercase.
	 *
	 * @param emails the list of email addresses to normalize
	 * @return a list of lowercase email addresses, preserving null values
	 */
	public static @Nonnull List<String> normalizeEmails(final @Nonnull List<String> emails) {
		return emails.stream()
				.map(email -> email != null ? email.toLowerCase() : null)
				.collect(Collectors.toList());
	}

	/**
	 * Creates a mock ContactList with specified name, uuid and members.
	 * Used when you need a ContactList without AccountContactLists wrapper.
	 */
	public static @Nonnull ContactList createContactList(@Nonnull final String listName, @Nonnull final String uuid, @Nonnull final List<String> memberEmails) {
		final ContactList contactList = mock(ContactList.class);
		lenient().when(contactList.getIdentifier()).thenReturn(listName);
		lenient().when(contactList.getUuid()).thenReturn(uuid);
		final Set<ContactListContact> contacts = createContactListContacts(memberEmails);
		lenient().when(contactList.getContactListContacts()).thenReturn(contacts);
		return contactList;
	}

	/**
	 * Creates a mock AccountContactLists with embedded ContactList.
	 * Used when you need the relationship between account and contact list.
	 */
	public static @Nonnull AccountContactLists createAccountContactListsWithEmbeddedList(
			@Nonnull final String listName, boolean canViewMembers, @Nonnull final List<String> memberEmails) {
		final AccountContactLists acl = mock(AccountContactLists.class);
		final ContactList contactList = createContactList(listName, "uuid-" + listName, memberEmails);
		when(acl.getContactList()).thenReturn(contactList);
		when(acl.getCanViewContactListMembers()).thenReturn(canViewMembers);
		return acl;
	}

	/**
	 * Creates a mock AccountContactLists with an existing ContactList.
	 * Used when you already have a ContactList instance.
	 */
	public static @Nonnull AccountContactLists createAccountContactLists(@Nonnull final ContactList contactList, boolean canViewMembers) {
		final AccountContactLists acl = mock(AccountContactLists.class);
		lenient().when(acl.getContactList()).thenReturn(contactList);
		lenient().when(acl.getCanViewContactListMembers()).thenReturn(canViewMembers);
		return acl;
	}

	/**
	 * Helper method to create ContactListContact mocks.
	 */
	private static @Nonnull Set<ContactListContact> createContactListContacts(@Nonnull final List<String> memberEmails) {
		return memberEmails.stream()
				.map(email -> {
					final ContactListContact contact = mock(ContactListContact.class);
					lenient().when(contact.getMail()).thenReturn(email);
					return contact;
				})
				.collect(Collectors.toSet());
	}

	/**
	 * Extracts contact list names from a list of {@link MailContact} objects.
	 *
	 * @param result the list of mail contacts to process
	 * @return a list of contact list names present in the mail contacts
	 */
	public static @Nonnull List<String> extractContactListNamesFromResult(@Nonnull final List<MailContact> result) {
		return result.stream()
				.map(MailContact::getContactListName)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}
}
