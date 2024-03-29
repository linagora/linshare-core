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

public abstract class Entry {

	protected long id;

	protected Account entryOwner;

	protected Calendar creationDate;

	protected Calendar modificationDate;

	protected Calendar expirationDate;

	protected String name;

	protected String comment;

	protected String uuid;

	protected String metaData;

	protected boolean cmisSync;

	public Entry() {
	}

	public Entry(Account entryOwner, String name, String comment) {
		this.entryOwner = entryOwner;
		this.name = name;
		this.comment = comment;
	}

	public abstract EntryType getEntryType();

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Account getEntryOwner() {
		return entryOwner;
	}

	public void setEntryOwner(Account entryOwner) {
		this.entryOwner = entryOwner;
	}

	public Calendar getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}

	public Calendar getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Calendar modificationDate) {
		this.modificationDate = modificationDate;
	}

	public Calendar getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Calendar expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}

	public boolean isCmisSync() {
		return cmisSync;
	}

	public void setCmisSync(boolean cmisSync) {
		this.cmisSync = cmisSync;
	}

	@Override
	public String toString() {
		return "Entry [name=" + name + ", uuid=" + uuid + "]";
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
		Entry other = (Entry) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

/**
 * Business setters.
 */

	public void setBusinessName(String name) {
		if (name != null) {
			this.name = name;
		}
	}

	public void setBusinessComment(String comment) {
		if (comment != null) {
			this.comment = comment;
		}
	}

	public void setBusinessMetaData(String metadata) {
		if (metadata != null) {
			this.metaData = metadata;
		}
	}

	public String getRepresentation() {
		return this.uuid + " " + "(" + name + ")";
	}
}
