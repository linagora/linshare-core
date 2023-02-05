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

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.UserFilterType;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "UserFilter")
public abstract class AbstractUserFilterDto {

	@Schema(description = "Unique identifier of the resource.", required = true)
	protected String uuid;

	@Schema(description = "User filter's name", required = true)
	protected String name;

	@Schema(description = "User filter's description", required = false)
	protected String description;

	@Schema(description = "User filter's type", required = true)
	protected UserFilterType type;

	@Schema(description = "User filter's creation date", required = false)
	protected Date creationDate;

	@Schema(description = "User filter's modification date", required = false)
	protected Date modificationDate;

	protected AbstractUserFilterDto() {
		super();
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

	public UserFilterType getType() {
		return type;
	}

	public void setType(UserFilterType type) {
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
}
