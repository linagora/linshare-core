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

import java.io.Serializable;
import java.util.UUID;

import org.linagora.linshare.core.domain.constants.EntryType;

public class UploadRequestEntry extends Entry implements Serializable {

	private static final long serialVersionUID = 54638444450061115L;

	protected Document document;

	protected UploadRequestUrl uploadRequestUrl;

	protected Long size;

	protected String type;

	protected String humanMimeType;

	protected String sha256sum;

	protected Boolean copied;

	protected Boolean ciphered;

	protected boolean hasThumbnail;

	public UploadRequestEntry() {
		super();
	}

	public UploadRequestEntry(String name) {
		this.uuid = UUID.randomUUID().toString();
		this.name = name;
	}

	public UploadRequestEntry(Account entryOwner, String name, String comment, Document document,
			UploadRequestUrl requestUrl) {
		super(entryOwner, name, comment);
		this.document = document;
		this.uploadRequestUrl = requestUrl;
		this.sha256sum = document.getSha256sum();
		this.size = document.getSize();
		this.type = document.getType();
		this.humanMimeType = document.getHumanMimeType();
		this.hasThumbnail = document.getHasThumbnail();
		this.copied = false;
		this.ciphered = false;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public EntryType getEntryType() {
		return EntryType.UPLOAD_REQUEST;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public UploadRequestUrl getUploadRequestUrl() {
		return uploadRequestUrl;
	}

	public void setUploadRequestUrl(UploadRequestUrl uploadRequestUrl) {
		this.uploadRequestUrl = uploadRequestUrl;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public Boolean getCopied() {
		return copied;
	}

	public void setCopied(Boolean copied) {
		this.copied = copied;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSha256sum() {
		return sha256sum;
	}

	public void setSha256sum(String sha256sum) {
		this.sha256sum = sha256sum;
	}

	public Boolean getCiphered() {
		return ciphered;
	}

	public void setCiphered(Boolean ciphered) {
		this.ciphered = ciphered;
	}

	public boolean isHasThumbnail() {
		return hasThumbnail;
	}

	public void setHasThumbnail(boolean hasThumbnail) {
		this.hasThumbnail = hasThumbnail;
	}

	public String getHumanMimeType() {
		return humanMimeType;
	}

	public void setHumanMimeType(String humanMimeType) {
		this.humanMimeType = humanMimeType;
	}
}
