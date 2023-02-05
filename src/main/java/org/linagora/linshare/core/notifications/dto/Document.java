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
package org.linagora.linshare.core.notifications.dto;

import java.util.Date;
import java.util.UUID;

import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;

/**
 * @author FMartin
 *
 */
public class Document {

	protected String uuid;

	protected String name;

	protected Long size;

	protected String href;

	protected Date creationDate;

	protected Date expirationDate;

	protected Boolean displayHref;

	protected Boolean mine;

	public Document(String name) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.name = name;
	}

	public Document(DocumentEntry de) {
		super();
		this.uuid = de.getUuid();
		this.name = de.getName();
	}

	public Document(DocumentEntry de, boolean full) {
		this(de);
		if (full) {
			this.creationDate = de.getCreationDate().getTime();
			if (de.getExpirationDate() != null) {
				this.expirationDate = de.getExpirationDate().getTime();
			}
		}
	}

	public Document(DocumentEntry de, String href) {
		super();
		this.uuid = de.getUuid();
		this.name = de.getName();
		setHref(href);
	}

	public Document(UploadRequestEntry entry) {
		super();
		this.uuid = entry.getUuid();
		this.name = entry.getName();
		this.size = entry.getSize();
		this.creationDate = entry.getCreationDate().getTime();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHref() {
		return href;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public void setHref(String href) {
		if (href == null) {
			displayHref = false;
		} else {
			displayHref = true;
		}
		this.href = href;
	}

	public Boolean isDisplayHref() {
		return displayHref;
	}

	public void setDisplayHref(Boolean displayHref) {
		this.displayHref = displayHref;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Boolean getDisplayHref() {
		return displayHref;
	}

	public Boolean getMine() {
		return mine;
	}

	public void setMine(Boolean mine) {
		this.mine = mine;
	}

	@Override
	public String toString() {
		return "Document [uuid=" + uuid + ", name=" + name + ", href=" + href + "]";
	}

}
