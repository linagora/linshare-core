/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.mongo.projections.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@XmlRootElement(name = "SharedSpace")
public class SharedSpaceNodeNested {

	protected String uuid;

	protected String name;

	protected String parentUuid;

	protected NodeType nodeType;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected GenericLightEntity role;

	protected Date creationDate;

	protected Date modificationDate;

	protected String domainUuid;

	public SharedSpaceNodeNested(String uuid, String name, String parentUuid, NodeType nodeType, Date creationDate,
			Date modificationDate) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.parentUuid = parentUuid;
		this.nodeType = nodeType;
		this.creationDate = creationDate;
		this.modificationDate = modificationDate;
	}

	public SharedSpaceNodeNested() {
	}

	public SharedSpaceNodeNested(SharedSpaceNode node) {
		this.uuid = node.getUuid();
		this.name = node.getName();
		this.parentUuid = node.getParentUuid();
		this.domainUuid = node.getDomainUuid();
		this.nodeType = node.getNodeType();
		this.creationDate = node.getCreationDate();
		this.modificationDate = node.getModificationDate();
		this.domainUuid = node.getDomainUuid();
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

	public String getParentUuid() {
		return parentUuid;
	}

	public void setParentUuid(String parentUuid) {
		this.parentUuid = parentUuid;
	}

	public NodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}

	public GenericLightEntity getRole() {
		return role;
	}

	public void setRole(GenericLightEntity role) {
		this.role = role;
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

	public String getDomainUuid() {
		return domainUuid;
	}

	public void setDomainUuid(String domainUuid) {
		this.domainUuid = domainUuid;
	}

	@XmlTransient
	@JsonIgnore
	public boolean isDrive() {
		return this.nodeType.equals(NodeType.WORK_SPACE) || this.nodeType.equals(NodeType.DRIVE);
	}

	@XmlTransient
	@JsonIgnore
	public boolean isWorkGroup() {
		return this.nodeType.equals(NodeType.WORK_GROUP);
	}

	@Override
	public String toString() {
		return "SharedSpaceNodeNested [uuid=" + uuid + ", name=" + name + ", nodeType=" + nodeType + ", role=" + role
				+ ", creationDate=" + creationDate + ", modificationDate=" + modificationDate + ", domainUuid="
				+ domainUuid + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
		result = prime * result + ((modificationDate == null) ? 0 : modificationDate.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((nodeType == null) ? 0 : nodeType.hashCode());
		result = prime * result + ((parentUuid == null) ? 0 : parentUuid.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
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
		SharedSpaceNodeNested other = (SharedSpaceNodeNested) obj;
		if (creationDate == null) {
			if (other.creationDate != null)
				return false;
		} else if (!creationDate.equals(other.creationDate))
			return false;
		if (modificationDate == null) {
			if (other.modificationDate != null)
				return false;
		} else if (!modificationDate.equals(other.modificationDate))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nodeType != other.nodeType)
			return false;
		if (parentUuid == null) {
			if (other.parentUuid != null)
				return false;
		} else if (!parentUuid.equals(other.parentUuid))
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
}
