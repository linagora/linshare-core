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
import java.util.Calendar;

import org.linagora.linshare.core.domain.constants.EntryType;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;

public class ThreadEntry extends Entry implements Serializable {

	private static final long serialVersionUID = 9142518443629423165L;

	protected Document document;
	
	protected Boolean ciphered;

	protected String type;

	protected String humanMimeType;

	protected Long size;

	protected String sha256sum;

	protected boolean hasThumbnail;

	public ThreadEntry() {
		super();
	}
	
	public ThreadEntry(Account entryOwner, String name, Document document) {
		super(entryOwner, name, "");
		this.document = document;
		this.ciphered = false;
		this.sha256sum = document.getSha256sum();
		this.size = document.getSize();
		this.type = document.getType();
		this.humanMimeType= document.getHumanMimeType();
		this.hasThumbnail = document.getThumbnails() != null;
	}

	// cmis
	public ThreadEntry(WorkGroupDocument node) {
		this.ciphered = false;
		this.sha256sum = node.getSha256sum();
		this.size = node.getSize();
		this.type = node.getMimeType();
		this.hasThumbnail = document.getThumbnails() != null;
		this.name = node.getName();
		this.comment = node.getDescription();
		this.uuid = node.getUuid();
		this.creationDate = Calendar.getInstance();
		this.creationDate.setTime(node.getCreationDate());
		this.modificationDate = Calendar.getInstance();
		this.modificationDate.setTime(node.getModificationDate());
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	@Override
	public EntryType getEntryType() {
		return EntryType.THREAD;
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

	public String getHumanMimeType() {
		return humanMimeType;
	}

	public void setHumanMimeType(String humanMimeType) {
		this.humanMimeType = humanMimeType;
	}
}
