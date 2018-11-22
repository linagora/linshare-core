
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
import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.mongo.entities.light.GenericLightEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "SharedSpaceRole")
@Document(collection = "shared_space_roles")
public class SharedSpaceRole {

	@JsonIgnore
	@Id
	@GeneratedValue
	protected String id;

	@ApiModelProperty(value = "uuid")
	protected String uuid;

	protected String name;

	protected Boolean enabled;

	protected SharedSpaceAuthor author;

	protected GenericLightEntity domain;

	protected Date creationDate;

	protected Date modificationDate;

	public SharedSpaceRole() {
	}

	public SharedSpaceRole(SharedSpaceRole role) {
		this.uuid = role.getUuid();
		this.name = role.getName();
		this.enabled = role.isEnabled();
		this.author = role.getAuthor();
		this.domain = role.getDomain();
		this.creationDate = role.getCreationDate();
		this.modificationDate = role.getModificationDate();
	}

	public SharedSpaceRole(String name, Boolean enabled, GenericLightEntity domain, SharedSpaceAuthor author) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.name = name;
		this.enabled = enabled;
		this.author = author;
		this.domain = domain;
		this.creationDate = new Date();
		this.modificationDate = new Date();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enable) {
		this.enabled = enable;
	}

	public SharedSpaceAuthor getAuthor() {
		return author;
	}

	public void setAuthor(SharedSpaceAuthor author) {
		this.author = author;
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

	public GenericLightEntity getDomain() {
		return domain;
	}

	public void setDomain(GenericLightEntity domain) {
		this.domain = domain;
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

	@Override
	public String toString() {
		return "SharedSpaceRoles [id=" + id + ", uuid=" + uuid + ", name=" + name + ", enable=" + enabled
				+ ", sharedSpaceAccount=" + author + ", creationDate=" + creationDate + ", modificationDate="
				+ modificationDate + ", sharedSpaceDomain=" + domain + "]";

	}

}
