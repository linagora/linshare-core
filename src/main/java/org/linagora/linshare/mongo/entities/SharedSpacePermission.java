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
import java.util.List;
import java.util.UUID;

import javax.persistence.GeneratedValue;
import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.SharedSpaceActionType;
import org.linagora.linshare.core.domain.constants.SharedSpaceResourceType;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@XmlRootElement(name = "SharedSpacePermission")
@Document(collection = "shared_space_permissions")
public class SharedSpacePermission {

	@JsonIgnore
	@Id
	@GeneratedValue
	protected String id;

	@Schema(description = "uuid")
	protected String uuid;

	protected SharedSpaceActionType action;

	protected SharedSpaceResourceType resource;

	protected List<GenericLightEntity> roles;

	protected Date creationDate;

	protected Date modificationDate;

	public SharedSpacePermission() {
		super();
	}

	public SharedSpacePermission(SharedSpaceActionType action, SharedSpaceResourceType resource,
			List<GenericLightEntity> roles) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.action = action;
		this.resource = resource;
		this.roles = roles;
		this.creationDate = new Date();
		this.modificationDate = new Date();
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

	public SharedSpaceActionType getAction() {
		return action;
	}

	public void setAction(SharedSpaceActionType action) {
		this.action = action;
	}

	public SharedSpaceResourceType getResource() {
		return resource;
	}

	public void setResource(SharedSpaceResourceType resourceType) {
		this.resource = resourceType;
	}

	public List<GenericLightEntity> getRoles() {
		return roles;
	}

	public void setRoles(List<GenericLightEntity> roles) {
		this.roles = roles;

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
		return "SharedSpacePermissions [id=" + id + ", uuid=" + uuid + ", action=" + action + ", resource=" + resource
				+ ", SharedSpacerole=" + roles + ", creationDate=" + creationDate + ", modificationDate="
				+ modificationDate + "]";
	}

}
