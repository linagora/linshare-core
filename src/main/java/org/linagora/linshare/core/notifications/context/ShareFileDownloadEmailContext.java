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

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.MailActivationType;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.notifications.dto.Document;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.core.notifications.dto.Share;

public class ShareFileDownloadEmailContext extends EmailContext {

	protected Boolean anonymous = null;

	protected Entry entry;

	protected Date actionDate;

	public ShareFileDownloadEmailContext(ShareEntry shareEntry) {
		super(shareEntry.getEntryOwner().getDomain(), true);
		this.entry = shareEntry;
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

	public void setEntry(Entry entry) {
		this.entry = entry;
	}

	public Boolean getAnonymous() {
		return anonymous;
	}

	public void setAnonymous(Boolean anonymous) {
		this.anonymous = anonymous;
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
		Validate.notNull(anonymous, "Missing shareEntry / anonymousShareEntry");
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

}
