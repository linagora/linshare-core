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

import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;

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
		this.nodeType = node.getNodeType();
		this.creationDate = node.getCreationDate();
		this.modificationDate = node.getModificationDate();
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

	@Override
	public String toString() {
		return "SharedSpaceNodeNested [uuid=" + uuid + ", name=" + name + ", nodeType=" + nodeType + ", role=" + role
				+ ", creationDate=" + creationDate + ", modificationDate=" + modificationDate + "]";
	}
}
