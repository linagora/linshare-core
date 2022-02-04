/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
package org.linagora.linshare.mongo.entities.mto;

import org.linagora.linshare.core.domain.entities.ShareEntry;

public class ShareEntryMto extends EntryMto {

	protected AccountMto sender;

	protected AccountMto recipient;

	protected Long downloaded;

	protected String shareUuid;

	protected String type;

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
}
