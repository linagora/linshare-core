/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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
import java.util.UUID;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;
import org.springframework.data.mongodb.core.mapping.Document;

@XmlRootElement(name = "SharedSpaceMember")
@Document(collection = "shared_space_members")
public class SharedSpaceMember {

	@JsonIgnore
	@Id
	@GeneratedValue
	protected String id;

	protected String uuid;

	protected GenericLightEntity node;

	protected GenericLightEntity role;

	protected GenericLightEntity account;

	@XmlTransient
	@JsonIgnore
	protected boolean canUpload;

	@XmlTransient
	@JsonIgnore
	protected boolean admin;

	protected Date creationDate;

	protected Date modificationDate;

	public SharedSpaceMember() {
		super();
	}

	public SharedSpaceMember(GenericLightEntity node, GenericLightEntity role, GenericLightEntity account) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.node = node;
		this.role = role;
		this.account = account;
		this.creationDate = new Date();
		this.modificationDate = new Date();
		this.admin = hasAdminRight();
		this.canUpload = hasUploadRight();
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

	public GenericLightEntity getNode() {
		return node;
	}

	public void setNode(GenericLightEntity node) {
		this.node = node;
	}

	public GenericLightEntity getRole() {
		return role;
	}

	public void setRole(GenericLightEntity role) {
		this.role = role;
	}

	public GenericLightEntity getAccount() {
		return account;
	}

	public void setAccount(GenericLightEntity account) {
		this.account = account;
	}

	@Override
	public String toString() {
		return "SharedSpaceMember [id=" + id + ", uuid=" + uuid + ", node=" + node + ", role=" + role + ", account="
				+ account + ", creationDate=" + creationDate + ", modificationDate=" + modificationDate + "]";
	}

	/**
	 * Workaround to display roles in email notifications. DO NOT USE IT
	 * 
	 * @return
	 */
	@XmlTransient
	@JsonIgnore
	public boolean hasAdminRight() {
		return this.getRole().getName().equals("ADMIN");
	}

	/**
	 * Workaround to display roles in email notifications. DO NOT USE IT
	 * 
	 * @return
	 */
	@XmlTransient
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
	public boolean hasReadOnlyRight() {
		return this.getRole().getName().equals("READER");
	}
}
