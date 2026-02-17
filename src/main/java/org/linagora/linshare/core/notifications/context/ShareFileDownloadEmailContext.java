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

import java.util.Date;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.MailingListBusinessService;
import org.linagora.linshare.core.domain.constants.MailActivationType;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.AccountContactLists;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.dto.Document;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.core.notifications.dto.Share;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShareFileDownloadEmailContext extends EmailContext {

	private static final Logger logger = LoggerFactory.getLogger(ShareFileDownloadEmailContext.class);

	/**
	 * <p>Indicates whether this context represents an anonymous share.</p>
	 * <p>This field is final to ensure consistency with the entry type:</p>
	 * <ul><li>{@code true}: entry is an {@link AnonymousShareEntry}</li>
	 * <li>{@code false}: entry is a {@link ShareEntry}</li></ul>
	 */
	private final boolean anonymous;

	/**
	 * <p>The share entry - can only be {@link ShareEntry} or {@link AnonymousShareEntry}.</p>
	 * <p>NOTE: This field is final to prevent modification after construction and ensure type safety.
	 * Setting this field to other {@link Entry} types (DocumentEntry, ThreadEntry or UploadRequestEntry) is forbidden.</p>
	 */
	private final Entry entry;

	protected Date actionDate;
	protected AccountService accountService;
	private MailingListBusinessService contactListBusinessService;
	private AuditLogEntryService auditLogEntryService;

	public ShareFileDownloadEmailContext(@Nonnull final ShareEntry shareEntry, @Nonnull final MailingListBusinessService contactListBusinessService, @Nonnull final AccountService accountService, @Nonnull final AuditLogEntryService auditLogEntryService) {
		super(shareEntry.getEntryOwner().getDomain(), true);
		this.entry = shareEntry;
		this.contactListBusinessService = contactListBusinessService;
		this.accountService = accountService;
		this.auditLogEntryService = auditLogEntryService;
		this.anonymous = false;
		this.actionDate = new Date();
		this.language = shareEntry.getEntryOwner().getMailLocale();
	}

	public ShareFileDownloadEmailContext(AnonymousShareEntry shareEntry) {
		super(shareEntry.getEntryOwner().getDomain(), true);
		this.entry = shareEntry;
		this.anonymous = true;
		this.actionDate = new Date();
		this.language = shareEntry.getEntryOwner().getMailLocale();
	}

	public Entry getEntry() {
		return entry;
	}

	public boolean getAnonymous() {
		return anonymous;
	}

	public Date getActionDate() {
		return actionDate;
	}

	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	@Override
	public MailContentType getType() {
		return MailContentType.SHARE_FILE_DOWNLOAD;
	}

	@Override
	public MailActivationType getActivation() {
		if (anonymous) {
			return MailActivationType.SHARE_FILE_DOWNLOAD_ANONYMOUS;
		}
		return MailActivationType.SHARE_FILE_DOWNLOAD_USERS;
	}

	@Override
	public String getMailRcpt() {
		// shareOwner
		return entry.getEntryOwner().getMail();
	}

	@Override
	public String getMailReplyTo() {
		if (anonymous) {
			return ((AnonymousShareEntry) entry).getAnonymousUrl().getContact().getMail();
		}
		return ((ShareEntry) entry).getRecipient().getMail();
	}

	@Override
	public void validateRequiredField() {
		Validate.notNull(entry, "Missing shareEntry");
		Validate.notNull(actionDate, "Missing actionDate");
	}

	public Share getShare() {
		if (anonymous) {
			return new Share((AnonymousShareEntry) entry);
		}
		return new Share((ShareEntry) entry);
	}

	public Document getDocument() {
		if (anonymous) {
			return new Document(((AnonymousShareEntry) entry).getDocumentEntry());
		}
		return new Document(((ShareEntry) entry).getDocumentEntry());
	}

	public MailContact getRecipient() {
		if (anonymous) {
			return new MailContact(((AnonymousShareEntry) entry).getAnonymousUrl().getContact());
		}
		return new MailContact(((ShareEntry) entry).getRecipient());
	}

	public AnonymousShareEntry getAnonymousShareEntry() {
		return (AnonymousShareEntry) entry;
	}

	public ShareEntry getShareEntry() {
		return (ShareEntry) entry;
	}

	/**
	 * Creates a MailContact with appropriate visibility based on contact list settings.
	 * For guest users sharing with contact lists, the recipient details may be anonymized
	 * if the contact list members are not visible to the owner, except when the recipient
	 * is an explicit contact from the invisible contact list.
	 *
	 * @return MailContact containing either recipient details or anonymized contact list information
	 */
	public @Nonnull MailContact createRecipientDataAgainstContactListViewStatus() {
		Validate.isTrue(!anonymous, "AnonymousShareEntry forbidden here");
		final ShareEntry shareEntry = (ShareEntry) entry;

		if (shouldShowRecipientDirectly(shareEntry)) {
			return new MailContact(shareEntry.getRecipient());
		}
		final String contactListUuid = shareEntry.getContactListUuid();
		if (isIndividualShare(contactListUuid)) {
			return new MailContact(shareEntry.getRecipient());
		}
		return processContactListShare(shareEntry, contactListUuid);
	}

	/**
	 * Determines if recipient details should be shown directly without contact list checks.
	 * Non-guest users always see recipient details regardless of contact list settings.
	 *
	 * @param shareEntry the share entry to check
	 * @return true if recipient should be shown directly, false otherwise
	 */
	private boolean shouldShowRecipientDirectly(@Nonnull final ShareEntry shareEntry) {
		return !shareEntry.getEntryOwner().isGuest();
	}

	/**
	 * Checks if this is an individual share (not using a contact list).
	 * Individual shares always show recipient details.
	 *
	 * @param contactListUuid the contact list UUID to check
	 * @return true if this is an individual share, false if it uses a contact list
	 */
	private boolean isIndividualShare(@Nullable final String contactListUuid) {
		return contactListUuid == null || contactListUuid.isEmpty();
	}

	/**
	 * Processes a contact list share, handling business logic and exceptions.
	 * This method coordinates the processing of contact list shares with proper error handling.
	 *
	 * @param shareEntry the share entry being processed
	 * @param contactListUuid the UUID of the contact list
	 * @return MailContact with appropriate visibility based on contact list settings
	 */
	private @Nonnull MailContact processContactListShare(
			@Nonnull final ShareEntry shareEntry,
			@Nonnull final String contactListUuid) {
		try {
			return processContactListWithBusinessLogic(shareEntry, contactListUuid);
		} catch (final BusinessException e) {
			return handleBusinessException(e, contactListUuid, shareEntry);
		} catch (final Exception e) {
			return handleUnexpectedException(e, contactListUuid, shareEntry);
		}
	}

	/**
	 * Processes contact list share using business services to determine visibility.
	 * Retrieves contact list and account contact list settings to decide whether
	 * to show recipient details or anonymized contact list information.
	 *
	 * @param shareEntry the share entry being processed
	 * @param contactListUuid the UUID of the contact list
	 * @return MailContact with appropriate visibility
	 * @throws BusinessException if business service operations fail
	 */
	private @Nonnull MailContact processContactListWithBusinessLogic(
			@Nonnull final ShareEntry shareEntry,
			@Nonnull final String contactListUuid) throws BusinessException {

		final ContactList contactList = this.contactListBusinessService.findByUuid(contactListUuid);
		if (contactList == null) {
			return new MailContact(shareEntry.getRecipient());
		}
		if (shouldAnonymizeRecipient(shareEntry, contactList)) {
			return createAnonymizedContact(contactList);
		}
		return new MailContact(shareEntry.getRecipient());
	}

	/**
	 * Determines whether recipient should be anonymized based on contact list visibility settings.
	 * Uses the canViewContactListMembers method to check visibility, which includes domain functionality fallback.
	 * If the recipient is an explicit contact from an invisible contact list, their details are shown.
	 * However, if this is the ONLY recipient and it's from an invisible contact list, we show the list name.
	 *
	 * @param shareEntry the share entry being processed
	 * @param contactList the contact list to check visibility for
	 * @return true if recipient should be anonymized, false otherwise
	 */
	private boolean shouldAnonymizeRecipient(
			@Nonnull final ShareEntry shareEntry,
			@Nonnull final ContactList contactList) {

		final Optional<AccountContactLists> accountContactLists = this.accountService
				.findAccountContactListByAccountAndContactList(
						shareEntry.getEntryOwner(),
						contactList
				);
		if(accountContactLists.isPresent()) {
		boolean canViewMembers = this.auditLogEntryService.canViewContactListMembers(accountContactLists.get());
		if (canViewMembers) {
			return false;
		}
		}
		final String shareContactListUuid = shareEntry.getContactListUuid();
		if (shareContactListUuid == null) {
			return false;
		}
		return true;
	}

	/**
	 * Creates an anonymized MailContact showing only the contact list name.
	 * Used when contact list members are not visible to the share owner.
	 *
	 * @param contactList the contact list to use for anonymization
	 * @return MailContact containing only the contact list name
	 */
	private @Nonnull MailContact createAnonymizedContact(@Nonnull final ContactList contactList) {
		final MailContact anonymizedContact = new MailContact();
		anonymizedContact.setContactListName(contactList.getIdentifier());
		return anonymizedContact;
	}

	/**
	 * Handles BusinessException during contact list processing.
	 * Special handling for deleted contact lists using audit log fallback,
	 * other business exceptions fall back to showing recipient details.
	 *
	 * @param e the BusinessException that occurred
	 * @param contactListUuid the UUID of the contact list being processed
	 * @param shareEntry the share entry being processed
	 * @return MailContact with appropriate fallback information
	 */
	private @Nonnull MailContact handleBusinessException(
			@Nonnull final BusinessException e,
			@Nonnull final String contactListUuid,
			@Nonnull final ShareEntry shareEntry) {

		if (e.getErrorCode() == BusinessErrorCode.LIST_DO_NOT_EXIST) {
			logger.debug("Contact list deleted, using audit log name for uuid: {}", contactListUuid);
			return createDeletedListContact(contactListUuid);
		}
		logger.warn("Business exception processing contact list with uuid: " + contactListUuid, e);
		return new MailContact(shareEntry.getRecipient());
	}

	/**
	 * Handles unexpected exceptions during contact list processing.
	 * Logs the exception and falls back to showing recipient details.
	 *
	 * @param e the Exception that occurred
	 * @param contactListUuid the UUID of the contact list being processed
	 * @param shareEntry the share entry being processed
	 * @return MailContact with recipient details as fallback
	 */
	private @Nonnull MailContact handleUnexpectedException(
			@Nonnull final Exception e,
			@Nonnull final String contactListUuid,
			@Nonnull final ShareEntry shareEntry) {

		logger.warn("Unexpected error processing contact list with uuid: " + contactListUuid, e);
		return new MailContact(shareEntry.getRecipient());
	}

	/**
	 * Creates a MailContact for a deleted contact list using audit log information.
	 * Attempts to retrieve the last known name from audit logs, falls back to
	 * generic "Deleted list" if audit information is unavailable.
	 *
	 * @param contactListUuid the UUID of the deleted contact list
	 * @return MailContact containing the contact list name from audit logs or fallback
	 */
	private MailContact createDeletedListContact(final String contactListUuid) {
		String contactListName =contactListUuid;
		try {
			final Optional<String> auditLogName = this.auditLogEntryService.findLastDeletedContactListName(contactListUuid);
			if (auditLogName.isPresent()) {
				contactListName = auditLogName.get();
			}
		} catch (Exception e) {
			logger.warn("Error retrieving deleted contact list name for uuid: " + contactListUuid + ", using default name", e);
		}
		final MailContact anonymizedContact = new MailContact();
		anonymizedContact.setContactListName(contactListName);
		return anonymizedContact;
	}
}
