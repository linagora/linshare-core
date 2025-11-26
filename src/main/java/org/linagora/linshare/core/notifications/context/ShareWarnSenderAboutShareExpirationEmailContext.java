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

import java.util.Optional;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.MailActivationType;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountContactLists;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.ContactListService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShareWarnSenderAboutShareExpirationEmailContext extends EmailContext {

	private static Logger logger = LoggerFactory.getLogger(ShareWarnSenderAboutShareExpirationEmailContext.class);

	protected ShareEntry shareEntry;

	protected Integer daysLeft;

	private ContactListService contactListService;

	private AccountService accountService;

	private FunctionalityReadOnlyService functionalityReadOnlyService;

	private AuditLogEntryService auditLogEntryService;

	public ShareWarnSenderAboutShareExpirationEmailContext(@Nonnull final ShareEntry shareEntry,
			@Nonnull final ContactListService contactListService, @Nonnull final AccountService accountService, @Nonnull final FunctionalityReadOnlyService functionalityReadOnlyService, @Nonnull final AuditLogEntryService auditLogEntryService, final Integer daysLeft) {
		super(shareEntry.getEntryOwner().getDomain(), false);
		this.shareEntry = shareEntry;
		this.contactListService = contactListService;
		this.accountService = accountService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.auditLogEntryService = auditLogEntryService;
		this.daysLeft = daysLeft;
		this.language = shareEntry.getEntryOwner().getMailLocale();
	}

	public ShareEntry getShareEntry() {
		return shareEntry;
	}

	public void setShareEntry(ShareEntry shareEntry) {
		this.shareEntry = shareEntry;
	}

	public Integer getDaysLeft() {
		return daysLeft;
	}

	public void setDaysLeft(Integer daysLeft) {
		this.daysLeft = daysLeft;
	}

	@Override
	public MailContentType getType() {
		return MailContentType.SHARE_WARN_SENDER_ABOUT_SHARE_EXPIRATION_WITHOUT_DOWNLOAD;
	}

	@Override
	public MailActivationType getActivation() {
		return MailActivationType.SHARE_WARN_SENDER_ABOUT_SHARE_EXPIRATION_WITHOUT_DOWNLOAD;
	}

	@Override
	public String getMailRcpt() {
		return shareEntry.getEntryOwner().getMail();
	}

	@Override
	public String getMailReplyTo() {
		return shareEntry.getRecipient().getMail();
	}

	@Override
	public void validateRequiredField() {
		Validate.notNull(shareEntry, "Missing shareEntry");
		Validate.notNull(daysLeft, "Missing daysLeft");
	}

	/**
	 * Creates a MailContact with appropriate visibility based on contact list settings.
	 * For guest users sharing with contact lists, the recipient details may be anonymized
	 * if the contact list members are not visible to the owner. Explicitly selected contacts
	 * are always shown with their details even if they belong to an invisible contact list.
	 *
	 * @return MailContact containing either recipient details or anonymized contact list information
	 * @throws RuntimeException if an unexpected error occurs during processing
	 */
	public @Nonnull MailContact createRecipientDataAgainstContactListViewStatus() {
		try {
			final Account entryOwner = this.shareEntry.getEntryOwner();
			final User recipient = this.shareEntry.getRecipient();
			final String contactListUuid = this.shareEntry.getContactListUuid();
			if (!entryOwner.isGuest()) {
				return this.createMailContact(recipient);
			}
			if (contactListUuid == null || contactListUuid.trim().isEmpty()) {
				return this.createMailContact(recipient);
			}
			return this.processContactListShare(entryOwner, recipient, contactListUuid);

		} catch (Exception e) {
			logger.error("Unexpected error processing recipient visibility for share entry: " +
					this.shareEntry.getUuid(), e);
			return this.createMailContact(this.shareEntry.getRecipient());
		}
	}

	/**
	 * Processes a contact list share to determine recipient visibility.
	 * For guest users, checks if the recipient should be anonymized based on contact list
	 * visibility settings and whether the recipient was explicitly selected.
	 *
	 * @param entryOwner the account that owns the share entry
	 * @param recipient the recipient of the share
	 * @param contactListUuid the UUID of the contact list associated with the share
	 * @return MailContact with appropriate visibility (recipient details or contact list name)
	 * @throws BusinessException if business service operations fail
	 */
	private @Nonnull MailContact processContactListShare(
			@Nonnull final Account entryOwner,
			@Nonnull final User recipient,
			@Nonnull final String contactListUuid) {
		try {
			final ContactList contactList = this.contactListService.findByUuid(
					entryOwner.getLsUuid(),
					contactListUuid
			);
			if (isContactFromContactListOnly(recipient, contactList)) {
				final Optional<AccountContactLists> accountContactLists = this.accountService.findAccountContactListByAccountAndContactList(
						entryOwner, contactList);
				if (accountContactLists.isPresent()) {
					final boolean canViewMembers = this.auditLogEntryService.canViewContactListMembers(
							accountContactLists.get());
					if (!canViewMembers) {
						final MailContact anonymizedContact = new MailContact();
						anonymizedContact.setContactListName(contactList.getIdentifier());
						return anonymizedContact;
					}
				}
				return this.createMailContact(recipient);
			}
			return this.createMailContact(recipient);
		} catch (final BusinessException e) {
			if (e.getErrorCode() == BusinessErrorCode.LIST_DO_NOT_EXIST) {
				return this.createDeletedListContact(contactListUuid);
			}
			throw e;
		}
	}

	/**
	 * Determines if a recipient is included only via contact list membership
	 * and was not explicitly selected by the user.
	 *
	 * <p>This method checks whether the recipient's email exists in the contact list.
	 * If the recipient is in the contact list and the ShareEntry has a contactListUuid,
	 * they are considered to be included only via list membership.</p>
	 *
	 * <p><b>Business Logic:</b>
	 * <ul>
	 *   <li>If recipient is in the contact list → considered "list-only" member</li>
	 *   <li>If recipient is not in the contact list → considered explicitly selected</li>
	 *   <li>If recipient has no email → treated as list-only (conservative approach)</li>
	 * </ul>
	 *
	 * @param recipient the recipient to check for list membership
	 * @param contactList the contact list to search for the recipient
	 * @return true if the recipient is only included via contact list membership,
	 *         false if the recipient was explicitly selected or not in the list
	 */
	private boolean isContactFromContactListOnly(
			@Nonnull final User recipient,
			@Nonnull final ContactList contactList) {

		if (recipient == null) {
			return true;
		}

		if (recipient.getMail() == null) {
			return true;
		}
		final String recipientEmail = recipient.getMail().toLowerCase();
		if (contactList.getContactListContacts() == null) {
			return false;
		}
		return contactList.getContactListContacts().stream()
				.anyMatch(contact -> contact != null &&
						contact.getMail() != null &&
						contact.getMail().toLowerCase().equals(recipientEmail));
	}

	/**
	 * Creates a MailContact for a deleted contact list using audit log information.
	 * Attempts to retrieve the last known name from audit logs, falls back to
	 * the contact list UUID if audit information is unavailable.
	 *
	 * <p><b>Naming Strategy:</b></p>
	 * <ul>
	 *   <li><b>Audit log available:</b> Uses the original contact list name without modifications</li>
	 *   <li><b>No audit log:</b> Uses the contact list UUID as the display name</li>
	 *   <li><b>Audit log error:</b> Falls back to using the contact list UUID as the display name</li>
	 * </ul>
	 *
	 * @param contactListUuid the UUID of the deleted contact list
	 * @return MailContact containing either:
	 *         - The original contact list name from audit logs, or
	 *         - The contact list UUID as the display name
	 */
	private MailContact createDeletedListContact(final String contactListUuid) {
		String contactListName = contactListUuid;
		try {
			final Optional<String> auditLogName = this.auditLogEntryService.findLastDeletedContactListName(contactListUuid);
			if (auditLogName.isPresent()) {
				contactListName = auditLogName.get();
			}
		} catch (final Exception e) {
			logger.warn("Error retrieving deleted contact list name for uuid: " + contactListUuid + ", using default name", e);
		}
		final MailContact anonymizedContact = new MailContact();
		anonymizedContact.setContactListName(contactListName);
		return anonymizedContact;
	}

	/**
	 * Creates a MailContact from a User recipient with proper null-safety.
	 * Handles null recipient cases gracefully by returning an empty MailContact.
	 *
	 * @param recipient the user recipient to convert to MailContact
	 * @return MailContact containing recipient details, or empty MailContact if recipient is null
	 */
	private @Nonnull MailContact createMailContact(final User recipient) {
		if (recipient == null) {
			logger.warn("Recipient is null for share entry: " + this.shareEntry.getUuid());
			return new MailContact();
		}
		return new MailContact(recipient);
	}
}
