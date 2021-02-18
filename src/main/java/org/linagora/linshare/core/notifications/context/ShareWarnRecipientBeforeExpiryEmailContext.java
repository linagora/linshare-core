/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2017-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.notifications.context;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.MailActivationType;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.notifications.dto.MailContact;

public class ShareWarnRecipientBeforeExpiryEmailContext extends EmailContext {

	protected Entry entry;

	protected User shareOwner;

	protected User shareRecipient;

	protected Integer day;

	protected boolean isAnonymous;

	public ShareWarnRecipientBeforeExpiryEmailContext(ShareEntry shareEntry, Integer day) {
		super(shareEntry.getEntryOwner().getDomain(), false);
		this.entry = shareEntry;
		this.shareOwner = (User) shareEntry.getEntryOwner();
		this.day = day;
		this.isAnonymous = false;
		this.language = shareEntry.getRecipient().getExternalMailLocale();
		this.shareRecipient = shareEntry.getRecipient();
	}

	public ShareWarnRecipientBeforeExpiryEmailContext(AnonymousShareEntry shareEntry, Integer day) {
		super(shareEntry.getEntryOwner().getDomain(), false);
		this.entry = shareEntry;
		this.shareOwner = (User) shareEntry.getEntryOwner();
		this.day = day;
		this.isAnonymous = true;
		this.language = shareEntry.getEntryOwner().getExternalMailLocale();
	}

	public ShareEntry getShareEntry() {
		return (ShareEntry) entry;
	}

	public AnonymousShareEntry getAnonymousShareEntry() {
		return (AnonymousShareEntry) entry;
	}

	public Entry getEntry() {
		return entry;
	}

	public void setEntry(Entry shareEntry) {
		this.entry = shareEntry;
	}

	public boolean isAnonymous() {
		return isAnonymous;
	}

	public void setAnonymous(boolean isAnonymous) {
		this.isAnonymous = isAnonymous;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public User getShareOwner() {
		return shareOwner;
	}

	public void setShareOwner(User shareOwner) {
		this.shareOwner = shareOwner;
	}

	public MailContact getMailContactRecipient() {
		if (isAnonymous) {
			return new MailContact(getAnonymousShareEntry().getAnonymousUrl().getContact());
		}
		return new MailContact(getShareEntry().getRecipient());
	}

	public User getShareRecipient() {
		return shareRecipient;
	}

	public void setShareRecipient(User shareRecipient) {
		this.shareRecipient = shareRecipient;
	}

	@Override
	public MailContentType getType() {
		return MailContentType.SHARE_WARN_RECIPIENT_BEFORE_EXPIRY;
	}

	@Override
	public MailActivationType getActivation() {
		return MailActivationType.SHARE_WARN_RECIPIENT_BEFORE_EXPIRY;
	}

	@Override
	public String getMailRcpt() {
		if (isAnonymous) {
			return getAnonymousShareEntry().getAnonymousUrl().getContact().getMail();
		}
		return getShareEntry().getRecipient().getMail();
	}

	@Override
	public String getMailReplyTo() {
		return shareOwner.getMail();
	}

	@Override
	public void validateRequiredField() {
		Validate.notNull(entry, "Missing shareEntry");
		Validate.notNull(day, "Missing days");
	}

}
