/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
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
package org.linagora.linshare.core.domain.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.EntryType;

public class DocumentEntry extends Entry implements Serializable {

	private static final long serialVersionUID = -6168359253673278696L;

	protected Document document;

	protected Boolean ciphered;

	protected Set<ShareEntry> shareEntries = new HashSet<ShareEntry>();

	protected Set<AnonymousShareEntry> anonymousShareEntries = new HashSet<AnonymousShareEntry>();

	protected String type;

	protected Long size;

	protected String sha256sum;

	protected boolean hasThumbnail;

	protected long shared;

	protected DocumentEntry() {
	}

	@Override
	public EntryType getEntryType() {
		return EntryType.DOCUMENT;
	}

	public DocumentEntry(Account entryOwner, String name, String comment, Document document) {
		super(entryOwner, name, comment);
		this.document = document;
		this.ciphered = false;
		this.sha256sum = document.getSha256sum();
		this.size = document.getSize();
		this.type = document.getType();
		this.hasThumbnail = document.getHasThumbnail();
		this.shared = 0;
	}

	public DocumentEntry(Account entryOwner, String name, Document document) {
		super(entryOwner, name, "");
		this.document = document;
		this.ciphered = false;
		this.sha256sum = document.getSha256sum();
		this.size = document.getSize();
		this.type = document.getType();
		this.hasThumbnail = document.getHasThumbnail();
		this.shared = 0;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public Set<ShareEntry> getShareEntries() {
		return shareEntries;
	}

	public void setShareEntries(Set<ShareEntry> shareEntries) {
		this.shareEntries = shareEntries;
	}

	public Set<AnonymousShareEntry> getAnonymousShareEntries() {
		return anonymousShareEntries;
	}

	public void setAnonymousShareEntries(
			Set<AnonymousShareEntry> anonymousShareEntries) {
		this.anonymousShareEntries = anonymousShareEntries;
	}

	public Boolean getCiphered() {
		return ciphered;
	}

	public void setCiphered(Boolean ciphered) {
		this.ciphered = ciphered;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getSha256sum() {
		return sha256sum;
	}

	public void setSha256sum(String sha256sum) {
		this.sha256sum = sha256sum;
	}

	public boolean isHasThumbnail() {
		return hasThumbnail;
	}

	public void setHasThumbnail(boolean hasThumbnail) {
		this.hasThumbnail = hasThumbnail;
	}

	public long getShared() {
		return shared;
	}

	public void setShared(long shared) {
		this.shared = shared;
	}

	public void incrementShared() {
		shared ++;
	}

	public void decrementShared() {
		shared --;
	}

	@Override
	public String toString() {
		return "DocumentEntry [name=" + name + ", uuid=" + uuid + "]";
	}

}
