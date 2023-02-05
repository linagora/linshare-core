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
package org.linagora.linshare.core.facade.webservice.adminv5.dto;

import java.util.Date;

import org.linagora.linshare.core.domain.constants.WorkSpaceFilterType;
import org.linagora.linshare.core.domain.entities.LdapWorkSpaceFilter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.google.common.base.MoreObjects;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
	name = "LdapWorkSpaceFilter",
	description = "A LdapWorkSpaceFilter",
	discriminatorProperty = "type",
	discriminatorMapping = {
		@DiscriminatorMapping(value = "LDAP", schema = LDAPWorkSpaceFilterDto.class)
	}
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
	@Type(value = LDAPWorkSpaceFilterDto.class, name="LDAP"),
})
public abstract class AbstractWorkSpaceFilterDto {

	@Schema(description = "Unique identifier of the resource.", required = false)
	protected String uuid;

	@Schema(description = "WorkSpace filter's name", required = true)
	protected String name;

	@Schema(description = "WorkSpace filter's description", required = false)
	protected String description;

	@Schema(description = "WorkSpace filter's type", required = true)
	public abstract WorkSpaceFilterType getType();

	protected WorkSpaceFilterType type;

	@Schema(description = "WorkSpace filter's creation date", required = false)
	protected Date creationDate;

	@Schema(description = "WorkSpace filter's modification date", required = false)
	protected Date modificationDate;

	@Schema(description = "Shows if the workSpace filter is model or not.", required = false)
	protected boolean template;

	protected AbstractWorkSpaceFilterDto() {
		super();
	}

	public AbstractWorkSpaceFilterDto(LdapWorkSpaceFilter workSpaceLdapFilter) {
		this.uuid = workSpaceLdapFilter.getUuid();
		this.name = workSpaceLdapFilter.getLabel();
		this.description = workSpaceLdapFilter.getDescription();
		this.type = workSpaceLdapFilter.getType();
		this.creationDate = workSpaceLdapFilter.getCreationDate();
		this.modificationDate = workSpaceLdapFilter.getModificationDate();
		this.template = workSpaceLdapFilter.getSystem();
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setType(WorkSpaceFilterType type) {
		this.type = type;
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

	public boolean isTemplate() {
		return template;
	}

	public void setTemplate(boolean template) {
		this.template = template;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("uuid", uuid)
				.add("name", name)
				.add("description", description)
				.add("type", type)
				.add("creationDate", creationDate)
				.add("modificationDate", modificationDate)
				.add("template", template)
				.toString();
	}
}
