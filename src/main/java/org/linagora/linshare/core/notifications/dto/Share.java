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

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;

public class Share {

	protected String uuid;

	protected String name;

	protected Boolean downloaded;

	protected Date creationDate;

	protected Date expirationDate;

	protected Boolean displayHref;

	protected Boolean isDownloading;

	protected String href;

	public Share(String name, Boolean downloaded) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.name = name;
		this.downloaded = downloaded;
		this.creationDate = new Date();
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.MONTH, 1);
		this.expirationDate = instance.getTime();
		this.isDownloading = false;
	}

	public Share(ShareEntry se) {
		super();
		this.uuid = se.getUuid();
		this.name = se.getName();
		this.downloaded = se.getDownloaded() > 0;
		this.creationDate = se.getCreationDate().getTime();
		this.expirationDate = se.getExpirationDate() != null ? se.getExpirationDate().getTime() : null;
		this.isDownloading = false;
	}

	public Share(AnonymousShareEntry se) {
		super();
		this.uuid = se.getUuid();
		this.name = se.getName();
		this.downloaded = se.getDownloaded() > 0;
		this.creationDate = se.getCreationDate().getTime();
		this.expirationDate = se.getExpirationDate() != null ? se.getExpirationDate().getTime() : null;
		this.isDownloading = false;
	}

	public Share(String name) {
		this(name, false);
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

	public Boolean isDownloaded() {
		return downloaded;
	}

	public void setDownloaded(Boolean downloaded) {
		this.downloaded = downloaded;
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

	public String getHref() {
		return href;
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

	public Boolean isDownloading() {
		return isDownloading;
	}

	public void setDownloading(Boolean isDownloading) {
		this.isDownloading = isDownloading;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Share other = (Share) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Share [uuid=" + uuid + ", name=" + name + ", downloaded=" + downloaded + ", creationDate="
				+ creationDate + ", expirationDate=" + expirationDate + ", href=" + href + "]";
	}
}
