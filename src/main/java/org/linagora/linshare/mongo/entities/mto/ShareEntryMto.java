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
package org.linagora.linshare.mongo.entities.mto;

import org.linagora.linshare.core.domain.entities.ShareEntry;

public class ShareEntryMto extends EntryMto {

	protected AccountMto sender;

	protected AccountMto recipient;

	protected Long downloaded;

	protected String shareUuid;

	protected String type;

	protected String humanMimeType;

	protected Long size;

	protected String sha256Sum;

	public ShareEntryMto() {
		super();
	}

	public ShareEntryMto(ShareEntry entry) {
		super(entry);
		this.recipient = new AccountMto(entry.getRecipient());
		this.sender = new AccountMto(entry.getEntryOwner());
		this.type = entry.getType();
		this.humanMimeType = entry.getHumanMimeType();
		this.size = entry.getSize();
		this.sha256Sum = entry.getDocumentEntry().getSha256sum();
		this.downloaded = entry.getDownloaded();
	}

	public AccountMto getSender() {
		return sender;
	}

	public void setSender(AccountMto sender) {
		this.sender = sender;
	}

	public AccountMto getRecipient() {
		return recipient;
	}

	public void setRecipient(AccountMto recipient) {
		this.recipient = recipient;
	}

	public Long getDownloaded() {
		return downloaded;
	}

	public void setDownloaded(Long downloaded) {
		this.downloaded = downloaded;
	}

	public String getShareUuid() {
		return shareUuid;
	}

	public void setShareUuid(String shareUuid) {
		this.shareUuid = shareUuid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHumanMimeType() {
		return humanMimeType;
	}

	public void setHumanMimeType(String humanMimeType) {
		this.humanMimeType = humanMimeType;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getSha256Sum() {
		return sha256Sum;
	}

	public void setSha256Sum(String sha256Sum) {
		this.sha256Sum = sha256Sum;
	}

}
