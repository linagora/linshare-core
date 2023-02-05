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
