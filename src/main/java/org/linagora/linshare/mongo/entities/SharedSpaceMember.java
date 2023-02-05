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
package org.linagora.linshare.mongo.entities;

import java.util.Date;
import java.util.UUID;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.ldap.Role;
import org.linagora.linshare.mongo.entities.light.LightSharedSpaceRole;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Indication: If the type field is NOT set in the payload at shared space member creation, 
 * a simple shared space member (workgroup member) is created 
 * otherwise a shared space member WorkSpace (WorkSpace member) is created.
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, defaultImpl = SharedSpaceMember.class, property = "type")
@JsonSubTypes({ 
	@Type(value = SharedSpaceMemberDrive.class, name = "WORK_SPACE"),
	@Type(value = SharedSpaceMemberDrive.class, name = "DRIVE"),
	@Type(value = SharedSpaceMemberWorkgroup.class, name = "WORK_GROUP")
	})
@XmlSeeAlso({ SharedSpaceMemberDrive.class,
	SharedSpaceMemberWorkgroup.class
	})
@XmlRootElement(name = "SharedSpaceMember")
@Document(collection = "shared_space_members")
public class SharedSpaceMember {

	@JsonIgnore
	@Id
	@GeneratedValue
	protected String id;

	@Schema(description = "Uuid of shared space member")
	protected String uuid;

	protected SharedSpaceNodeNested node;

	@Schema(description = "Role of the shared space member.")
	protected LightSharedSpaceRole role;

	@Schema(description = "This field show the sharedSpaceMember's account.")
	protected SharedSpaceAccount account;

	@Schema(description = "CreationDate of the shared space member.")
	protected Date creationDate;

	@Schema(description = "ModificationDate of the shared space member.")
	protected Date modificationDate;

	@Schema(description = "This field is used to show if the shared space member exists in a nested sharedSpace.")
	protected boolean nested = false;

	/*
	 * if true, the membership is seen as part of a nested SharedSpace (nested workgroup in a WorkSpace)
	 * if false, the membership is not seen a part of a root SharedSpace (root workgroup), 
	 */
	@JsonIgnore
	protected boolean seeAsNested = false;

	@Schema(description = "This field is used to show the type of the sharedSpace to which the shared space member belongs.")
	protected NodeType type;

	// WorkAround
	// we have to duplicate account information
	// to avoid a side effect on front-end
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected SharedSpaceAccount user;

	public SharedSpaceMember() {
		super();
	}

	public SharedSpaceMember(SharedSpaceNodeNested node, LightSharedSpaceRole role, SharedSpaceAccount account) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.node = node;
		this.role = role;
		this.account = account;
		this.creationDate = new Date();
		this.modificationDate = new Date();
	}

	public SharedSpaceMember(SharedSpaceMember member) {
		super();
		this.uuid = member.getUuid();
		this.node = member.getNode();
		this.role = member.getRole();
		this.account = member.getAccount();
		this.creationDate = member.getCreationDate();
		this.modificationDate = member.getModificationDate();
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

	public SharedSpaceNodeNested getNode() {
		return node;
	}

	public void setNode(SharedSpaceNodeNested node) {
		this.node = node;
	}

	public LightSharedSpaceRole getRole() {
		return role;
	}

	public void setRole(LightSharedSpaceRole role) {
		this.role = role;
	}

	public SharedSpaceAccount getAccount() {
		return account;
	}

	public void setAccount(SharedSpaceAccount account) {
		this.account = account;
	}

	public SharedSpaceAccount getUser() {
		return user;
	}

	public void setUser(SharedSpaceAccount user) {
		this.user = user;
	}

	public boolean isNested() {
		return nested;
	}

	public void setNested(boolean nested) {
		this.nested = nested;
	}

	public boolean isSeeAsNested() {
		return seeAsNested;
	}

	public void setSeeAsNested(boolean seeAsNested) {
		this.seeAsNested = seeAsNested;
	}

	@Schema(description = "If true, sharedSpaceMember belongs to workGroup and its parent (WorkSpace), else he belongs only to the nested workgroup.")
	public boolean isBelongingToWorkSpace() {
		return seeAsNested;
	}

	public void setBelongingToWorkSpace(boolean seeAsNested) {
	}

	public NodeType getType() {
		return type;
	}

	public void setType(NodeType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "SharedSpaceMember [id=" + id + ", uuid=" + uuid + ", node=" + node + ", role=" + role + ", account="
				+ account + ", creationDate=" + creationDate + ", modificationDate=" + modificationDate + ", nested="
				+ nested + ", type=" + type + ", user=" + user + "]";
	}

	/**
	 * Workaround to display roles in email notifications. DO NOT USE IT
	 * 
	 * @return
	 */

	@XmlTransient
	@JsonIgnore
	public boolean getAdmin() {
		return this.getRole().getName().equals("ADMIN");
	}

	/**
	 * Workaround to display roles in WorkgroupMemberDto.
	 * 
	 * @return
	 */

	@JsonIgnore
	public boolean hasAdminRight() {
		return this.getRole().getName().equals("ADMIN");
	}

	/**
	 * Workaround to display roles in WorkgroupMemberDto
	 * 
	 * @return
	 */

	@JsonIgnore
	public boolean hasUploadRight() {
		return this.getRole().getName().equals("WRITER") || this.getRole().getName().equals("CONTRIBUTOR");
	}

	/**
	 * Workaround to display roles in email notifications. DO NOT USE IT
	 * 
	 * @return
	 */
	@XmlTransient
	@JsonIgnore
	public boolean getCanUpload() {
		return this.getRole().getName().equals("READER");
	}

	/**
	 * Workaround to display roles in WorkgroupMemberDto
	 * 
	 * @return
	 */

	@JsonIgnore
	public boolean hasReadOnlyRight() {
		return this.getRole().getName().equals("READER");
	}

	@XmlTransient
	@JsonIgnore
	public boolean isWorkSpaceAdmin() {
		return this.getRole().getName().equals(Role.WORK_SPACE_ADMIN.toString());
	}

	@XmlTransient
	@JsonIgnore
	public boolean isWorkSpaceWriter() {
		return this.getRole().getName().equals(Role.WORK_SPACE_WRITER.toString());
	}

	@XmlTransient
	@JsonIgnore
	public boolean isWorkSpaceReader() {
		return this.getRole().getName().equals(Role.WORK_SPACE_READER.toString());
	}

}
