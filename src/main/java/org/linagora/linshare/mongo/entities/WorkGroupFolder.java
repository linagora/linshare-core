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
import java.util.List;
import java.util.UUID;

import javax.persistence.GeneratedValue;
import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.google.common.collect.Lists;

@XmlRootElement(name = "WorkGroupFolder")
@Document(collection = "Work_group_folders")
@CompoundIndexes({ @CompoundIndex(name = "name", unique = true, def = "{'name': 1, 'parent': 1, 'workGroup': 1}") })
public class WorkGroupFolder {

	@Id
	@GeneratedValue
	protected String uuid;

	protected String name;

	protected String parent;

	protected String workGroup;

	protected List<String> ancestors;

	// @CreatedDate -- to be used
	protected Date creationDate;

	// @LastModifiedDate -- to be used
	protected Date modificationDate;

	protected List<WorkGroupEntry> entries;

	public WorkGroupFolder() {
		super();

	}
	/*
	 * @CreatedDate
	 * 
	 * @CreatedBy
	 * 
	 * @LastModifiedBy
	 * 
	 * @LastModifiedDate 11.2. General auditing configuration
	 */

	public WorkGroupFolder(String name, String parent, String workGroup) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.name = name;
		this.parent = parent;
		this.workGroup = workGroup;
		this.ancestors = Lists.newArrayList();
		this.creationDate = new Date();
		this.modificationDate = new Date();
		this.entries = Lists.newArrayList();
	}

	public WorkGroupFolder(WorkGroupFolder wgf) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.name = wgf.getName();
		this.parent = wgf.getParent();
		this.workGroup = wgf.getWorkGroup();
		this.ancestors = Lists.newArrayList();
		this.creationDate = new Date();
		this.modificationDate = new Date();
		this.entries = Lists.newArrayList();
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

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public List<String> getAncestors() {
		return ancestors;
	}

	public void setAncestors(List<String> ancestors) {
		this.ancestors = ancestors;
	}

	public String getWorkGroup() {
		return workGroup;
	}

	public void setWorkGroup(String workGroup) {
		this.workGroup = workGroup;
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

	public List<WorkGroupEntry> getEntries() {
		return entries;
	}

	public void addWorkGroupEntry(ThreadEntry threadEntry, AccountMto account) {
		if (entries == null) {
			entries = Lists.newArrayList();
		}
		entries.add(new WorkGroupEntry(threadEntry, account));
	}

	public void setEntries(List<WorkGroupEntry> entries) {
		this.entries = entries;
	}

	@Override
	public String toString() {
		return "WorkGroupFolder [uuid=" + uuid + ", name=" + name + ", parent=" + parent + ", workGroup=" + workGroup
				+ ", ancestors=" + ancestors + ", entries=" + entries + "]";
	}

}
