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
package org.linagora.linshare.core.notifications.context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.MailActivationType;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.AccountContactLists;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.ContactListContact;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.notifications.dto.MailContact;

public class ShareNewShareAcknowledgementEmailContext extends EmailContext {

	protected User shareOwner;

	protected ShareContainer shareContainer;

	protected Set<Entry> shares;

	public ShareNewShareAcknowledgementEmailContext(User sender, ShareContainer shareContainer, Set<Entry> shares) {
		super(sender.getDomain(), false);
		this.shareOwner = sender;
		this.shares = shares;
		this.shareContainer = shareContainer;
		this.language = shareOwner.getMailLocale();
	}

	public User getShareOwner() {
		return shareOwner;
	}

	public void setShareOwner(User shareOwner) {
		this.shareOwner = shareOwner;
	}

	public Set<Entry> getShares() {
		return shares;
	}

	public void setShares(Set<Entry> shares) {
		this.shares = shares;
	}

	public ShareContainer getShareContainer() {
		return shareContainer;
	}

	public void setShareContainer(ShareContainer shareContainer) {
		this.shareContainer = shareContainer;
	}

	@Override
	public MailContentType getType() {
		return MailContentType.SHARE_NEW_SHARE_ACKNOWLEDGEMENT_FOR_SENDER;
	}

	@Override
	public MailActivationType getActivation() {
		return MailActivationType.SHARE_NEW_SHARE_ACKNOWLEDGEMENT_FOR_SENDER;
	}

	@Override
	public String getMailRcpt() {
		return shareOwner.getMail();
	}

	@Override
	public String getMailReplyTo() {
		return null;
	}

	/**
	 * Builds the recipient list while applying contact list visibility rules.
	 *
	 * <p>For guest users, invisible contact lists are shown as single entries while
	 * their members remain hidden unless explicitly shared as individual contacts.</p>
	 *
	 * <p><b>Visibility Rules:</b>
	 * <ul>
	 *   <li><b>Non-guest users:</b> All recipients are always visible</li>
	 *   <li><b>Guest users with visible contact lists:</b> All list members are shown individually</li>
	 *   <li><b>Guest users with invisible contact lists:</b>
	 *     <ul>
	 *       <li>List name is shown as a single entry</li>
	 *       <li>List members are hidden by default</li>
	 *       <li><b>Exception:</b> Members that are also in {@code explicitRecipientEmails} remain visible</li>
	 *     </ul>
	 *   </li>
	 * </ul>
	 *
	 * <p><b>Key Business Rule:</b>
	 * Contacts explicitly shared individually override contact list visibility restrictions.
	 * This allows guests to share with specific members of invisible lists while keeping
	 * the overall list membership confidential.</p>
	 *
	 * <p><b>Example:</b>
	 * If sharing with an invisible contact list "Team A" containing
	 * {@code [user1@linshare.org, user2@linshare.org]} and an explicit contact
	 * {@code user3@linshare.org}, and {@code user1@linshare.org} is also explicitly shared:
	 * <ul>
	 *   <li>{@code explicitRecipientEmails} = {@code ["user1@linshare.org", "user3@linshare.org"]}</li>
	 *   <li><b>Result:</b> {@code ["Team A", "user1@linshare.org", "user3@linshare.org"]}</li>
	 *   <li><b>Hidden:</b> {@code "user2@linshare.org"} (only in invisible list, not explicit)</li>
	 * </ul>
	 * </p>
	 *
	 * @return an unmodifiable list of mail contacts with visibility rules applied, never {@code null};
	 *         contains either all original recipients (if no filtering is needed) or a filtered list
	 *         with contact list entries and visible individual contacts
	 * @see #explicitRecipientEmails
	 */
	public @Nonnull List<MailContact> getRecipientsWithVisibility() {
		final List<AccountContactLists> accountContactLists = this.shareContainer.getAccountContactLists();
		final List<MailContact> allRecipients = this.shareContainer.getMailContactRecipients();

		if (!this.shareOwner.isGuest() || accountContactLists == null || accountContactLists.isEmpty()) {
			return allRecipients;
		}

		final List<MailContact> finalRecipients = new ArrayList<>();
		final Set<String> restrictedContactEmails = new HashSet<>();
		final Set<String> explicitEmails = this.shareContainer.getExplicitRecipientEmails();
		for (final AccountContactLists accountContactList : accountContactLists) {
			final ContactList contactList = accountContactList.getContactList();
			if (Boolean.FALSE.equals(accountContactList.getCanViewContactListMembers())) {
				final MailContact contactListContact = new MailContact();
				contactListContact.setContactListName(contactList.getIdentifier());
				finalRecipients.add(contactListContact);

				for (final ContactListContact contact : contactList.getContactListContacts()) {
					final String email = contact.getMail().toLowerCase();
					if (!explicitEmails.contains(email)) {
						restrictedContactEmails.add(email);
					}
				}
			}
		}
		allRecipients.forEach(recipient -> {
			final String email = recipient.getMail();
			if (email == null || !restrictedContactEmails.contains(email.toLowerCase())) {
				finalRecipients.add(recipient);
			}
		});
		return finalRecipients;
	}

	@Override
	public void validateRequiredField() {
		Validate.notNull(shareOwner, "Missing shareEntry");
		Validate.notNull(shareContainer, "Missing shareEntry");
		Validate.notNull(shares, "Missing shareEntry");
	}

}
