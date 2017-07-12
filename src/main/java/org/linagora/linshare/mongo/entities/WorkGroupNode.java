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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.entities.mto.WorkGroupLightNode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = WorkGroupFolder.class, name = "FOLDER"),
		@Type(value = WorkGroupDocument.class, name = "DOCUMENT"),
		@Type(value = WorkGroupAsyncTask.class, name = "ASYNC_TASK")
		})
@XmlSeeAlso({ WorkGroupFolder.class,
	WorkGroupDocument.class,
	WorkGroupAsyncTask.class
	})
@XmlRootElement(name = "WorkGroupNode")
@Document(collection = "work_group_nodes")
//@CompoundIndexes({ @CompoundIndex(name = "name", unique = true, sparse = true, def = "{'name': 1, 'parent': 1, 'workGroup': 1}") })
public class WorkGroupNode implements Cloneable {

	@JsonIgnore
	@Id @GeneratedValue
	protected String id;

	protected String uuid;

	protected String name;

	protected String parent;

	protected String workGroup;

	protected String description;

	protected String metaData;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	protected AccountMto lastAuthor;

	@JsonIgnore
	protected WorkGroupNodeType nodeType;

	@JsonIgnore
	protected String path;

	// @CreatedDate -- to be used
	protected Date creationDate;

	// @LastModifiedDate -- to be used
	protected Date modificationDate;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	List<WorkGroupLightNode> treePath;

	public WorkGroupNode() {
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

	public WorkGroupNode(AccountMto author, String name, String parent, String workGroup) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.name = name;
		this.parent = parent;
		this.workGroup = workGroup;
		this.path = null;
		this.creationDate = new Date();
		this.modificationDate = new Date();
		this.lastAuthor = author;
	}

	public WorkGroupNode(WorkGroupNode wgf) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.name = wgf.getName();
		this.parent = wgf.getParent();
		this.workGroup = wgf.getWorkGroup();
		this.path = null;
		this.creationDate = new Date();
		this.modificationDate = new Date();
		this.lastAuthor = wgf.getLastAuthor();
	}

	@XmlTransient
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

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	@XmlTransient
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}

	public AccountMto getLastAuthor() {
		return lastAuthor;
	}

	public void setLastAuthor(AccountMto lastAuthor) {
		this.lastAuthor = lastAuthor;
	}

	@XmlTransient
	public WorkGroupNodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(WorkGroupNodeType nodeType) {
		this.nodeType = nodeType;
	}

	public List<WorkGroupLightNode> getTreePath() {
		return treePath;
	}

	public void setTreePath(List<WorkGroupLightNode> treePath) {
		this.treePath = treePath;
	}

	@Override
	public String toString() {
		return "WorkGroupNode [id=" + id + ", uuid=" + uuid + ", name=" + name + ", parent=" + parent + ", workGroup="
				+ workGroup + ", description=" + description + ", nodeType=" + nodeType
				+ ", creationDate=" + creationDate + ", modificationDate=" + modificationDate + "]";
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * Helpers
	 */
	public void setPathFromParent(WorkGroupNode parent) {
		this.path = parent.getPath();
		if (this.path == null) {
			this.path = "," + parent.getUuid() + ",";
		} else {
			this.path += parent.getUuid() + ",";
		}
	}

}
