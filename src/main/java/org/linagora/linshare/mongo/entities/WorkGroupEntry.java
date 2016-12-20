/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.mongo.entities;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@XmlRootElement(name = "WorkGroupEntry")
@Document(collection = "work_group_entries")
public class WorkGroupEntry {

	@JsonIgnore
	@Id @GeneratedValue
	protected String id;

	protected String uuid;

	protected String name;

	protected Date creationDate;

	protected Date modificationDate;

	protected String type;

	protected Long size;

	protected AccountMto lastAuthor;

	public WorkGroupEntry() {
		super();
	}

	public WorkGroupEntry(String uuid, String name, Date creationDate, Date modificationDate, String type, Long size,
			AccountMto lastAuthor) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.creationDate = creationDate;
		this.modificationDate = modificationDate;
		this.type = type;
		this.size = size;
		this.lastAuthor = lastAuthor;
	}

	public WorkGroupEntry(ThreadEntry threadEntry, AccountMto account) {
		this.uuid = threadEntry.getUuid();
		this.name = threadEntry.getName();
		this.creationDate = threadEntry.getCreationDate().getTime();
		this.modificationDate = threadEntry.getModificationDate().getTime();
		this.type = threadEntry.getType();
		this.size = threadEntry.getSize();
		this.lastAuthor = account;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
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

	public AccountMto getLastAuthor() {
		return lastAuthor;
	}

	public void setLastAuthor(AccountMto lastAuthor) {
		this.lastAuthor = lastAuthor;
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
		WorkGroupEntry other = (WorkGroupEntry) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "WorkGroupEntry [uuid=" + uuid + ", name=" + name + ", creationDate=" + creationDate
				+ ", modificationDate=" + modificationDate + ", type=" + type + ", size=" + size + "]";
	}

}
