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

import org.linagora.linshare.core.domain.constants.GroupFilterType;
import org.linagora.linshare.core.domain.entities.GroupLdapPattern;

import com.google.common.base.MoreObjects;

import io.swagger.v3.oas.annotations.media.Schema;

public abstract class AbstractGroupFilterDto {

	@Schema(description = "Unique identifier of the resource.", required = true)
	protected String uuid;

	@Schema(description = "Group filter's name", required = true)
	protected String name;

	@Schema(description = "Group filter's description", required = false)
	protected String description;

	@Schema(description = "Group filter's type", required = true)
	protected GroupFilterType type;

	@Schema(description = "Group filter's creation date", required = false)
	protected Date creationDate;

	@Schema(description = "Group filter's modification date", required = false)
	protected Date modificationDate;

	@Schema(description = "Shows if the group filter is model or not.", required = false)
	protected boolean template;

	protected AbstractGroupFilterDto() {
		super();
	}

	public AbstractGroupFilterDto(GroupLdapPattern groupLdapPattern) {
		this.uuid = groupLdapPattern.getUuid();
		this.name = groupLdapPattern.getLabel();
		this.description = groupLdapPattern.getDescription();
		this.type = groupLdapPattern.getType();
		this.creationDate = groupLdapPattern.getCreationDate();
		this.modificationDate = groupLdapPattern.getModificationDate();
		this.template = groupLdapPattern.getSystem();
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

	public GroupFilterType getType() {
		return type;
	}

	public void setType(GroupFilterType type) {
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
