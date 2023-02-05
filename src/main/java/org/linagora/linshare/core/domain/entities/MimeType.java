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

import java.util.Date;

import org.linagora.linshare.core.facade.webservice.common.dto.MimeTypeDto;

public class MimeType {

	private long id;

	private String uuid;

	private String mimeType;

	private String extensions;

	private boolean enable;

	private boolean displayable;

	private Date creationDate;

	private Date modificationDate;

	private MimePolicy mimePolicy;

	public MimeType() {
		super();
	}

	public MimeType(MimePolicy mimePolicy, String mimeType, String extensions,
			boolean enable, boolean displayable) {
		this(mimeType, extensions, enable, displayable);
		this.mimePolicy = mimePolicy;
	}

	public MimeType(String mimeType, String extensions,
			boolean enable, boolean displayable) {
		this.mimeType = mimeType;
		this.extensions = extensions;
		this.enable = enable;
		this.displayable = displayable;
	}

	public MimeType(MimeTypeDto m) {
		this.uuid = m.getUuid();
		this.extensions = m.getExtensions();
		this.enable = m.isEnable();
	}

	public long getId() {
		return id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getMimeType() {
		return mimeType;
	}

	public String getExtensions() {
		return extensions;
	}

	public void setExtensions(String extensions) {
		this.extensions = extensions;
	}

	public boolean getEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public boolean getDisplayable() {
		return displayable;
	}

	public void setDisplayable(boolean displayable) {
		this.displayable = displayable;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public MimePolicy getMimePolicy() {
		return mimePolicy;
	}

	public void setMimePolicy(MimePolicy mimePolicy) {
		this.mimePolicy = mimePolicy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((mimeType == null) ? 0 : mimeType.hashCode());
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
		MimeType other = (MimeType) obj;
		if (mimeType == null) {
			if (other.mimeType != null)
				return false;
		} else if (!mimeType.equals(other.mimeType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MimeType [uuid=" + uuid + ", mimeType=" + mimeType + "]";
	}
}
