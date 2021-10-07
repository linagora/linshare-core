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
package org.linagora.linshare.mongo.entities;

import java.util.Date;
import java.util.UUID;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "SharedSpace")
@Document(collection = "shared_space_nodes")
public class SharedSpaceNode {

	@JsonIgnore
	@Id
	@GeneratedValue
	protected String id;

	@Schema(description = "Uuid of shared space")
	protected String uuid;

	@Schema(description = "name of shared space")
	protected String name;

	@Schema(description = "parent Uuid of shared space only available for Workgroups inside a Drive")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String parentUuid;

	@Schema(description = "The type of shared space. See enum for possible values.")
	@Enumerated(EnumType.STRING)
	protected NodeType nodeType;

	protected Date creationDate;

	protected Date modificationDate;

	@Schema(description = "Indicate if versioning enabled, only available for Workgroup types")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected VersioningParameters versioningParameters;

	@Schema(description = "Quota uuid of the Workgroup")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String quotaUuid;

	@Schema(description = "Optional description of the shared space")
	protected String description;

	@Schema(description = "Role of the authenticated user in the shared space. Optionaly provided when requesting the detail of the resource.")
	@Transient
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected GenericLightEntity role;

	@Schema(description = "Used to recover the last update event related to an update or an action considered as an update of the shared space. Optionaly provided when requesting the detail of the resource.")
	@Transient
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected AuditLogEntryUser lastAuditEntry;
	
	@Schema(description = "Author of the shared space")
	protected SharedSpaceAccount author;

	@Schema(description = "The domain's uuid to which the current sharedSpace belongs.")
	protected String domainUuid;

	public SharedSpaceNode() {
		super();
	}

	public SharedSpaceNode(String name, NodeType nodeType) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.name = name;
		this.nodeType = nodeType;
		this.creationDate = new Date();
		this.modificationDate = new Date();
	}

	public SharedSpaceNode(String name, String parentUuid, NodeType nodeType) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.name = name;
		this.parentUuid = parentUuid;
		this.nodeType = nodeType;
		this.creationDate = new Date();
		this.modificationDate = new Date();
	}

	public SharedSpaceNode(String domainUuid, String name, String parentUuid, NodeType nodeType,
			VersioningParameters versioningParameters, String description, SharedSpaceAccount author) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.domainUuid = domainUuid;
		this.name = name;
		this.parentUuid = parentUuid;
		this.nodeType = nodeType;
		this.versioningParameters = versioningParameters;
		this.creationDate = new Date();
		this.modificationDate = new Date();
		this.description = description;
		this.author = author;
	}

	public SharedSpaceNode(String name, String uuid, NodeType nodeType, Date creationDate, Date modificationDate) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.nodeType = nodeType;
		this.creationDate = creationDate;
		this.modificationDate = modificationDate;
	}

	public SharedSpaceNode(SharedSpaceNode node) {
		super();
		this.uuid = node.getUuid();
		this.name = node.getName();
		this.parentUuid = node.getParentUuid();
		this.nodeType = node.getNodeType();
		this.versioningParameters = node.getVersioningParameters();
		this.creationDate = node.getCreationDate();
		this.modificationDate = node.getModificationDate();
		this.quotaUuid = node.getQuotaUuid();
		this.author = node.getAuthor();
		this.description = node.getDescription();
	}

	@XmlTransient
	@JsonIgnore
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

	public String getParentUuid() {
		return parentUuid;
	}

	public void setParentUuid(String parentUuid) {
		this.parentUuid = parentUuid;
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

	public NodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}

	public VersioningParameters getVersioningParameters() {
		return versioningParameters;
	}

	public void setVersioningParameters(VersioningParameters versioningParameters) {
		this.versioningParameters = versioningParameters;
	}

	public String getQuotaUuid() {
		return quotaUuid;
	}

	public void setQuotaUuid(String quotaUuid) {
		this.quotaUuid = quotaUuid;
	}

	public GenericLightEntity getRole() {
		return role;
	}

	public void setRole(GenericLightEntity role) {
		this.role = role;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public AuditLogEntryUser getLastAuditEntry() {
		return lastAuditEntry;
	}

	public void setLastAuditEntry(AuditLogEntryUser lastAuditEntry) {
		this.lastAuditEntry = lastAuditEntry;
	}

	public SharedSpaceAccount getAuthor() {
		return author;
	}

	public void setAuthor(SharedSpaceAccount author) {
		this.author = author;
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
		return this.nodeType.equals(NodeType.DRIVE);
	}

	@XmlTransient
	@JsonIgnore
	public boolean isWorkGroup() {
		return this.nodeType.equals(NodeType.WORK_GROUP);
	}

	@Override
	public String toString() {
		return "SharedSpaceNode [id=" + id + ", uuid=" + uuid + ", name=" + name + ", parentUuid=" + parentUuid
				+ ", nodeType=" + nodeType + ", creationDate=" + creationDate + ", modificationDate=" + modificationDate
				+ ", versioningParameters=" + versioningParameters + ", quotaUuid=" + quotaUuid + ", domainUuid="
				+ domainUuid + "]";
	}
}
