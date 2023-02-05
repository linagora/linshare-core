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

import java.util.Calendar;
import java.util.Date;

import org.linagora.linshare.core.domain.entities.Entry;

public abstract class EntryMto {

	protected String uuid;

	protected String name;

	protected Date creationDate;

	protected Date modificationDate;

	protected Date expirationDate;

	public EntryMto() {
		super();
	}

	public EntryMto(Entry entry) {
		this.uuid = entry.getUuid();
		this.name = entry.getName();
		this.creationDate = entry.getCreationDate().getTime();
		this.modificationDate = entry.getModificationDate().getTime();
		Calendar expDate = entry.getExpirationDate();
		if (expDate != null) {
			this.expirationDate = expDate.getTime();
		}
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

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
}