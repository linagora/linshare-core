/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */

package org.linagora.linshare.core.domain.objects;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.MailingListContact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.GenericUserDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDto;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.mongo.entities.EventNotification;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ShareContainer {

	protected String subject;

	protected String message;

	protected String locale;

	protected Boolean secured;

	protected Boolean creationAcknowledgement;

	protected String inReplyTo;

	protected String references;

	protected Date expiryDate;

	protected String sharingNote;

	/**
	 * This is the notification date for ShareEntryGroup
	 */
	protected Date notificationDateForUSDA;

	protected Boolean enableUSDA;


	/**
	 * True if at least one document is encrypted. It will be used by
	 * notification service to add a link towards the Applet used to decrypt the
	 * document.
	 */
	protected boolean encrypted;

	protected Set<String> documentUuids = Sets.newHashSet();

	/**
	 * recipients coming from out side world. Could me a mail, a mail and domain
	 * or a uuid
	 */
	protected Set<Recipient> recipients = Sets.newHashSet();

	/**
	 * This list will contain real user account
	 */
	protected Set<User> shareRecipients = Sets.newHashSet();

	/**
	 * This list will contain only email addresses which do not match with
	 * internal or external accounts.
	 */
	protected Set<Recipient> anonymousShareRecipients = Sets.newHashSet();

	protected Map<String, Recipient> allowedRecipients = null;

	protected Set<DocumentEntry> documents = Sets.newHashSet();

	protected List<MailContainerWithRecipient> mailContainers = Lists
			.newArrayList();

	protected List<AuditLogEntryUser> logs = Lists.newArrayList();

	protected List<EventNotification> events = Lists.newArrayList();

	public ShareContainer(String subject, String message, Boolean secured, Boolean creationAcknowledgement) {
		super();
		this.subject = subject;
		this.message = message;
		this.locale = "en";
		this.secured = secured;
		this.encrypted = false;
		this.creationAcknowledgement = creationAcknowledgement;
	}

	public ShareContainer() {
		super();
		this.encrypted = false;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		if (subject != null) {
			subject = subject.trim();
			if (subject.isEmpty()) {
				subject = null;
			}
		}
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		if (message != null) {
			message = message.trim();
			if (message.isEmpty()) {
				message = null;
			}
		}
		this.message = message;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public Boolean getSecured() {
		return secured;
	}

	public void setSecured(Boolean secured) {
		this.secured = secured;
	}

	public String getInReplyTo() {
		return inReplyTo;
	}

	public void setInReplyTo(String inReplyTo) {
		if (inReplyTo != null) {
			inReplyTo = inReplyTo.trim();
			if (inReplyTo.isEmpty()) {
				inReplyTo = null;
			}
		}
		this.inReplyTo = inReplyTo;
	}

	public String getReferences() {
		return references;
	}

	public void setReferences(String references) {
		if (references != null) {
			references = references.trim();
			if (references.isEmpty()) {
				references = null;
			}
		}
		this.references = references;
	}

	public boolean isEncrypted() {
		return encrypted;
	}

	public Set<Recipient> getRecipients() {
		return recipients;
	}

	public Set<DocumentEntry> getDocuments() {
		return documents;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public Calendar getExpiryCalendar() {
		if (expiryDate != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(expiryDate);
			return cal;
		}
		return null;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getSharingNote() {
		return sharingNote;
	}

	public void setSharingNote(String sharingNote) {
		this.sharingNote = sharingNote;
	}

	public Date getNotificationDateForUSDA() {
		return notificationDateForUSDA;
	}

	public void setNotificationDateForUSDA(Date notificationDate) {
		this.notificationDateForUSDA = notificationDate;
	}

	public Boolean getEnableUSDA() {
		return enableUSDA;
	}

	public void setEnableUSDA(Boolean enableUSDA) {
		this.enableUSDA = enableUSDA;
	}

	public Set<User> getShareRecipients() {
		return shareRecipients;
	}

	public Set<Recipient> getAnonymousShareRecipients() {
		return anonymousShareRecipients;
	}

	public List<MailContact> getMailContactRecipients() {
		List<MailContact> contacts = Lists.newArrayList();
		for (User rec : getShareRecipients()) {
			contacts.add(new MailContact(rec));
		}

		for (Recipient recipient : getAnonymousShareRecipients()) {
			contacts.add(new MailContact(recipient));
		}
		return contacts;
	}

	public Set<String> getDocumentUuids() {
		return documentUuids;
	}

	public List<MailContainerWithRecipient> getMailContainers() {
		return mailContainers;
	}

	public Map<String, Recipient> getAllowedRecipients() {
		return allowedRecipients;
	}

	/*
	 * Helpers
	 */

	public void addDocumentUuid(String uuid) {
		Validate.notEmpty(uuid, "uuid must not be null.");
		this.documentUuids.add(uuid);
	}

	public void addDocumentUuid(List<String> uuids) {
		Validate.notNull(uuids, "uuid list must not be null.");
		for (String uuid : uuids) {
			this.documentUuids.add(uuid);
		}
	}

	public void addDocumentDtos(Set<DocumentDto> documentDtos) {
		Validate.notNull(documentDtos, "documentDtos list must not be null.");
		for (DocumentDto d : documentDtos) {
			this.addDocumentUuid(d.getUuid());
		}
	}

	public void addDocumentEntry(DocumentEntry documentEntry) {
		this.documents.add(documentEntry);
	}

	public void addMail(String mail) {
		Validate.notEmpty(mail, "mail must not be null.");
		this.recipients.add(new Recipient(mail));
	}

	public void addMail(List<String> mails) {
		for (String mail : mails) {
			this.addMail(mail);
		}
	}

	public void addUserDto(UserDto userDto) {
		Validate.notNull(userDto, "user must not be null.");
		this.recipients.add(new Recipient(userDto));
	}

	public void addUserDto(GenericUserDto userDto) {
		Validate.notNull(userDto, "user must not be null.");
		this.recipients.add(new Recipient(userDto));
	}


	public void addContact(MailingListContact contact) {
		Validate.notNull(contact, "contact must be set.");
		this.recipients.add(new Recipient(contact));
	}

	public void addGenericUserDto(List<GenericUserDto> usersDto) {
		for (GenericUserDto userDto : usersDto) {
			this.addUserDto(userDto);
		}
	}

	public void addUserDto(List<UserDto> usersDto) {
		for (UserDto userDto : usersDto) {
			this.addUserDto(userDto);
		}
	}

	public void addShareRecipient(final User user) throws BusinessException {
		Validate.notNull(user, "user must not be null.");
		if (restrictedMode()) {
			// In restricted mode, the current user is only allowed to create share with internals and guests.
			if (allowedRecipients.get(user.getLsUuid()) == null) {
				// The current user is not an allowed recipient.
				throw new BusinessException(
						BusinessErrorCode.ANONYMOUS_SHARE_ENTRY_FORBIDDEN,
						"You are not authorized to create anonymous share entries.");
			}
		}
		this.shareRecipients.add(user);
	}

	public void addAnonymousShareRecipient(Recipient recipient) throws BusinessException {
		Validate.notNull(recipient, "recipient must not be null.");
		Validate.notEmpty(recipient.getMail(),
				"recipient mail must not be null.");
		if (restrictedMode()) {
			// In restricted mode, the current user is only allowed to create share with internals and guests.
			throw new BusinessException(
					BusinessErrorCode.ANONYMOUS_SHARE_ENTRY_FORBIDDEN,
					"You are not authorized to create anonymous share entries.");
		}
		this.anonymousShareRecipients.add(recipient);
	}

	public void updateEncryptedStatus() {
		this.encrypted = false;
		for (DocumentEntry d : this.documents) {
			if (d.getCiphered()) {
				this.encrypted = true;
				break;
			}
		}
	}

	public boolean needAnonymousShares() {
		return !anonymousShareRecipients.isEmpty();
	}

	public boolean canShare() {
		return (!anonymousShareRecipients.isEmpty()) || (!shareRecipients.isEmpty());
	}


	public void addMailContainer(MailContainerWithRecipient mailContainer) {
		this.mailContainers.add(mailContainer);
	}

	public void resetAllowedRecipients() {
		this.allowedRecipients = null;
	}

	public void addAllowedRecipient(User user) {
		Validate.notNull(user, "recipient must not be null.");
		Recipient recipient = new Recipient(user);
		if (allowedRecipients == null) {
			allowedRecipients = Maps.newHashMap();
		}
		allowedRecipients.put(recipient.getUuid(), recipient);
	}

	public void addAllowedRecipients(List<AllowedContact> allowedContacts) {
		Validate.notNull(allowedContacts, "allowedContacts must not be null.");
		for (AllowedContact allowedContact : allowedContacts) {
			this.addAllowedRecipient(allowedContact.getContact());
		}
	}

	public Boolean isAcknowledgement() {
		return creationAcknowledgement;
	}

	public void setAcknowledgement(Boolean creationAcknowledgement) {
		this.creationAcknowledgement = creationAcknowledgement;
	}

	public List<AuditLogEntryUser> getLogs() {
		return logs;
	}

	public void addLog(AuditLogEntryUser log) {
		this.logs.add(log);
	}

	public void addLogs(List<AuditLogEntryUser> logs) {
		this.logs.addAll(logs);
	}

	public List<EventNotification> getEvents() {
		return events;
	}

	public void addEvent(EventNotification event) {
		this.events.add(event);
	}

	public void addEvents(List<EventNotification> events) {
		this.events.addAll(events);
	}

	/*
	 * Private helpers
	 */

	private boolean restrictedMode() {
		return !(allowedRecipients == null);
	}
}
