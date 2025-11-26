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
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.MailActivationType;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.AccountContactLists;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.ContactListContact;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.ContactListService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShareWarnUndownloadedFilesharesEmailContext extends EmailContext {

	private static Logger logger = LoggerFactory.getLogger(ShareWarnUndownloadedFilesharesEmailContext.class);
	protected ShareEntryGroup shareEntryGroup;
	private final ContactListService contactListService;
	private final AccountService accountService;
	private final FunctionalityReadOnlyService functionalityReadOnlyService;
	private final AuditLogEntryService auditLogEntryService;

	public ShareWarnUndownloadedFilesharesEmailContext(@Nonnull final ShareEntryGroup shareEntryGroup ,
			@Nonnull final ContactListService contactListService, @Nonnull final AccountService accountService, @Nonnull final FunctionalityReadOnlyService functionalityReadOnlyService,
			@Nonnull final AuditLogEntryService auditLogEntryService) {
		super(shareEntryGroup.getOwner().getDomain(), false);
		this.shareEntryGroup = shareEntryGroup;
		this.contactListService = contactListService;
		this.accountService = accountService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.auditLogEntryService = auditLogEntryService;
		this.language = shareEntryGroup.getOwner().getMailLocale();
	}

	public ShareEntryGroup getShareEntryGroup() {
		return shareEntryGroup;
	}

	public void setShareEntryGroup(ShareEntryGroup shareEntryGroup) {
		this.shareEntryGroup = shareEntryGroup;
	}

	@Override
	public MailContentType getType() {
		return MailContentType.SHARE_WARN_UNDOWNLOADED_FILESHARES;
	}

	@Override
	public MailActivationType getActivation() {
		return MailActivationType.SHARE_WARN_UNDOWNLOADED_FILESHARES;
	}

	@Override
	public String getMailRcpt() {
		return shareEntryGroup.getOwner().getMail();
	}

	@Override
	public String getMailReplyTo() {
		return null;
	}

	@Override
	public void validateRequiredField() {
		Validate.notNull(shareEntryGroup, "Missing shareEntry");
	}

	/**
	 * Builds the final list of visible recipients for the "undownloaded fileshares warning" email
	 * while applying contact list visibility rules.
	 *
	 * <p>This method determines which recipients must appear in the email depending on:
	 * <ul>
	 *   <li>The owner's account type (guest vs non-guest)</li>
	 *   <li>The visibility settings of each contact list (visible vs invisible)</li>
	 *   <li>The presence of explicit individual shares</li>
	 *   <li>The existence status of contact lists (active vs deleted)</li>
	 * </ul>
	 *
	 * <p><b>Visibility Rules:</b></p>
	 * <ul>
	 *   <li><b>Non-guest owners:</b> all recipients are always shown individually.</li>
	 *   <li><b>Guest + visible list:</b> all members of the list are shown individually.</li>
	 *   <li><b>Guest + invisible list:</b>
	 *     <ul>
	 *       <li>The list is shown as a single entry (its name only)</li>
	 *       <li>Members are hidden by default</li>
	 *       <li>Download statistics are computed (notDownloadedCount / totalMembersCount)</li>
	 *       <li>Explicit individual shares remain visible even if they belong to the list</li>
	 *     </ul>
	 *   </li>
	 *   <li><b>Guest + deleted contact list:</b>
	 *     <ul>
	 *       <li>The list is shown as a single entry with its last known name from audit logs</li>
	 *       <li>If no audit log entry exists, the contact list UUID is used as the name</li>
	 *       <li>Visibility rules no longer apply as the list configuration is unavailable</li>
	 *       <li>All members are considered part of an "invisible" list by default</li>
	 *       <li>Download statistics are still computed based on share entries</li>
	 *       <li>Explicit individual shares from the same list remain visible</li>
	 *     </ul>
	 *   </li>
	 * </ul>
	 *
	 * <p><b>Deleted Contact List Handling:</b></p>
	 * When a contact list no longer exists (deleted):
	 * <ul>
	 *   <li>The system attempts to retrieve the last known name from audit log entries</li>
	 *   <li>If audit logs are available, the original name is used without modifications</li>
	 *   <li>If no audit log entry exists, the contact list UUID is used as the display name</li>
	 *   <li>Visibility settings cannot be determined, so the list is treated as invisible by default</li>
	 * </ul>
	 *
	 * @return an unmodifiable list of {@link MailContact} representing all visible recipients.
	 *         For invisible lists, entries include {@code contactListName}, {@code notDownloadedCount}
	 *         and {@code totalMembersCount}. For individual recipients, only email properties are set.
	 *         For deleted lists, the contactListName is either from audit logs or the UUID.
	 */
	public @Nonnull List<MailContact> createRecipientDataAgainstContactListViewStatus() {
		if (!this.shareEntryGroup.getOwner().isGuest()) {
			return this.shareEntryGroup.getAllRecipients();
		}
		final List<MailContact> finalRecipients = new ArrayList<>();
		final Set<String> processedContactLists = new HashSet<>();
		for (final ShareEntry share : this.shareEntryGroup.getShareEntries()) {
			final String contactListUuid = share.getContactListUuid();
			if (contactListUuid == null && share.getRecipient() != null) {
				finalRecipients.add(this.createMailContactFromRecipient(share.getRecipient()));
			} else if (contactListUuid != null && !processedContactLists.contains(contactListUuid)) {
				this.processContactList(contactListUuid, finalRecipients, processedContactLists);
			} else {
				// No action needed for:
				// - Already processed contact lists (avoid duplicates)
				// - Shares with null recipient (invalid entries)
				// These cases are intentionally skipped
			}
		}
		return finalRecipients;
	}

	/**
	 * Processes a contact list and applies visibility rules.
	 */
	private void processContactList(final @Nonnull String contactListUuid,
			final @Nonnull List<MailContact> finalRecipients,
			final @Nonnull Set<String> processedContactLists) {
		processedContactLists.add(contactListUuid);

		try {
			final ContactList contactList = this.contactListService.findByUuid(
					this.shareEntryGroup.getOwner().getLsUuid(), contactListUuid);
			final Optional<AccountContactLists> accountContactLists = this.accountService.findAccountContactListByAccountAndContactList(
					this.shareEntryGroup.getOwner(), contactList);
			if(accountContactLists.isPresent()) {
				if (this.auditLogEntryService.canViewContactListMembers(accountContactLists.get())) {
					this.addVisibleContactListMembers(contactList, finalRecipients);
				} else {
					this.addInvisibleContactList(contactList, finalRecipients);
				}
			}
		} catch (final BusinessException e) {
			if (e.getErrorCode() == BusinessErrorCode.LIST_DO_NOT_EXIST) {
				String contactListName = contactListUuid;
				final Optional<String> auditLogName = this.auditLogEntryService.findLastDeletedContactListName(contactListUuid);

				if(auditLogName.isPresent()) {
					contactListName = auditLogName.get();
				}
				logger.debug("Using name for deleted list: {}", contactListName);
				this.addDeletedContactList(contactListName, contactListUuid, finalRecipients);
			} else {
				logger.warn("Could not process contact list with uuid: " + contactListUuid, e);
			}
		} catch (Exception e) {
			logger.warn("Could not process contact list with uuid: " + contactListUuid, e);
		}
	}

	/**
	 * Adds all members of a visible contact list as individual recipients
	 */
	private void addVisibleContactListMembers(final @Nonnull ContactList contactList,
			final @Nonnull List<MailContact> finalRecipients) {
		for (final ContactListContact contact : contactList.getContactListContacts()) {
			finalRecipients.add(createMailContactFromContact(contact));
		}
	}

	/**
	 * Adds an invisible contact list as a single entry with download statistics
	 */
	private void addInvisibleContactList(final @Nonnull ContactList contactList,
			final @Nonnull List<MailContact> finalRecipients) {
		int notDownloadedCount = 0;
		int totalNonExplicitMembersCount = 0;
		final Set<String> explicitIndividualEmails = this.shareEntryGroup.getExplicitIndividualEmails();
		for (final ContactListContact contact : contactList.getContactListContacts()) {
			final String email = contact.getMail().toLowerCase();
			if (!explicitIndividualEmails.contains(email)) {
				totalNonExplicitMembersCount++;
				if (!hasMemberDownloaded(email)) {
					notDownloadedCount++;
				}
			}
		}
		final MailContact listContact = new MailContact();
		listContact.setContactListName(contactList.getIdentifier());
		listContact.setNotDownloadedCount(notDownloadedCount);
		listContact.setTotalMembersCount(totalNonExplicitMembersCount);
		finalRecipients.add(listContact);
	}

	/**
	 * Adds a deleted contact list as a single entry
	 */
	private void addDeletedContactList(final @Nonnull String contactListName,
			final @Nonnull String contactListUuid,
			final @Nonnull List<MailContact> finalRecipients) {
		int notDownloadedCount = 0;
		int totalSharesFromList = 0;
		for (final ShareEntry share : this.shareEntryGroup.getShareEntries()) {
			if (contactListUuid.equals(share.getContactListUuid())) {
				totalSharesFromList++;
				if (share.getDownloaded() == 0) {
					notDownloadedCount++;
				}
			}
		}
		final MailContact mailContact = new MailContact();
		mailContact.setContactListName(contactListName);
		mailContact.setNotDownloadedCount(notDownloadedCount);
		mailContact.setTotalMembersCount(totalSharesFromList);
		finalRecipients.add(mailContact);
	}

	/**
	 * Creates a MailContact from a recipient User
	 */
	private MailContact createMailContactFromRecipient(final @Nonnull org.linagora.linshare.core.domain.entities.User recipient) {
		return new MailContact(recipient);
	}

	/**
	 * Creates a MailContact from a ContactListContact
	 */
	private MailContact createMailContactFromContact(final @Nonnull ContactListContact contact) {
		MailContact mailContact = new MailContact();
		mailContact.setMail(contact.getMail());
		mailContact.setFirstName(contact.getFirstName());
		mailContact.setLastName(contact.getLastName());
		return mailContact;
	}

	/**
	 * Checks if member has downloaded any shared file.
	 */
	private boolean hasMemberDownloaded(final @Nonnull String email) {
		for (final ShareEntry entry : this.shareEntryGroup.getShareEntries()) {
			if (entry.getRecipient() != null &&
					entry.getRecipient().getMail() != null &&
					email.equalsIgnoreCase(entry.getRecipient().getMail()) &&
					entry.getDownloaded() > 0) {
				return true;
			}
		}
		return false;
	}
}