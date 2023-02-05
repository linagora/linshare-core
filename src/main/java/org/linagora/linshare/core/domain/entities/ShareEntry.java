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
package org.linagora.linshare.core.domain.entities;

import java.util.Calendar;

import org.linagora.linshare.core.domain.constants.EntryType;

/**
 * @author fred
 *
 */
public class ShareEntry extends Entry {

	protected User recipient;

	protected DocumentEntry documentEntry;

	protected Long downloaded;

	protected ShareEntryGroup shareEntryGroup;

	public ShareEntry() {
		super();
	}

	public ShareEntry(Account entryOwner, String name, String comment,
			User recipient, DocumentEntry documentEntry,
			Calendar expirationDate, ShareEntryGroup shareEntryGroup) {
		super(entryOwner, name, comment);
		this.recipient = recipient;
		this.documentEntry = documentEntry;
		this.downloaded = Long.valueOf(0);
		this.expirationDate = expirationDate;
		this.shareEntryGroup = shareEntryGroup;
	}

	@Override
	public EntryType getEntryType() {
		return EntryType.SHARE;
	}

	public User getRecipient() {
		return recipient;
	}

	public void setRecipient(User recipient) {
		this.recipient = recipient;
	}

	public DocumentEntry getDocumentEntry() {
		return documentEntry;
	}

	public void setDocumentEntry(DocumentEntry documentEntry) {
		this.documentEntry = documentEntry;
	}

	public Long getDownloaded() {
		return downloaded;
	}

	public void setDownloaded(Long downloaded) {
		this.downloaded = downloaded;
	}

	public ShareEntryGroup getShareEntryGroup() {
		return shareEntryGroup;
	}

	public void setShareEntryGroup(ShareEntryGroup shareEntryGroup) {
		this.shareEntryGroup = shareEntryGroup;
	}

	public void incrementDownloaded() {
		++downloaded;
	}

	@Override
	public String toString() {
		return "ShareEntry [downloaded=" + downloaded + ", name=" + name
				+ ", uuid=" + uuid + "]";
	}

	/* useful getters */
	public long getSize() {
		return documentEntry.getSize();
	}

	public String getType() {
		return documentEntry.getType();
	}

	public String getHumanMimeType() {
		return documentEntry.getHumanMimeType();
	}

	/*
	 * helpers
	 */
	public boolean hasThumbnail() {
		return documentEntry.isHasThumbnail();
	}
}
