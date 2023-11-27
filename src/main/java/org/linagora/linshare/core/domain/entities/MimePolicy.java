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
import java.util.HashSet;
import java.util.Set;

import org.linagora.linshare.core.facade.webservice.common.dto.MimePolicyDto;

public class MimePolicy {

	private long id;

	private String uuid;

	private String name;

	private int mode;

	private int displayable;

	private Date creationDate;

	private Date modificationDate;

	private Set<MimeType> mimeTypes = new HashSet<MimeType>();

	private AbstractDomain domain;

	private boolean unknownTypeAllowed = false;

	public MimePolicy() {
		super();
	}

	public MimePolicy(final MimePolicyDto m) {
		this.uuid = m.getUuid();
		this.name = m.getName();
		this.unknownTypeAllowed = m.isUnknownTypeAllowed();
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getDisplayable() {
		return displayable;
	}

	public void setDisplayable(int displayable) {
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

	public Set<MimeType> getMimeTypes() {
		return mimeTypes;
	}

	public void setMimeTypes(Set<MimeType> mimeTypes) {
		this.mimeTypes = mimeTypes;
	}

	public AbstractDomain getDomain() {
		return domain;
	}

	public void setDomain(AbstractDomain domain) {
		this.domain = domain;
	}

	public boolean isUnknownTypeAllowed() {
		return unknownTypeAllowed;
	}

	public void setUnknownTypeAllowed(boolean unknownTypeAllowed) {
		this.unknownTypeAllowed = unknownTypeAllowed;
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
		MimePolicy other = (MimePolicy) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MimePolicy [uuid=" + uuid + ", name=" + name + "]";
	}
}
