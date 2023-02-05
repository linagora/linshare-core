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


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import org.linagora.linshare.core.domain.constants.ServerType;

import java.util.Date;

@Schema(
	name = "Server",
	description = "A Server",
	discriminatorProperty = "serverType",
	discriminatorMapping = {
		@DiscriminatorMapping(value = "LDAP", schema = LDAPServerDto.class),
		@DiscriminatorMapping(value = "TWAKE", schema = TwakeServerDto.class)
	}
)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "serverType", visible = true)
@JsonSubTypes({
	@JsonSubTypes.Type(value = LDAPServerDto.class, name="LDAP"),
	@JsonSubTypes.Type(value = TwakeServerDto.class, name="TWAKE")
})
public abstract class AbstractServerDto {

	@Schema(description = "Server's uuid", required = true)
	protected final String uuid;

	@Schema(description = "Server's name", required = true)
	protected final String name;

	@Schema(description = "Server's description", required = false)
	protected final String description;

	@Schema(description = "Server's url", required = true)
	protected final String url;

	@Schema(description = "Server's type", required = true)
	protected final ServerType serverType;

	@Schema(description = "Server's creation date", required = true)
	protected final Date creationDate;

	@Schema(description = "Server's modification date", required = true)
	protected final Date modificationDate;

	protected AbstractServerDto(String uuid, String name, String description, String url, ServerType serverType, Date creationDate, Date modificationDate) {
		this.uuid = uuid;
		this.name = name;
		this.description = description;
		this.url = url;
		this.serverType = serverType;
		this.creationDate = creationDate;
		this.modificationDate = modificationDate;
	}

	public String getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getUrl() {
		return url;
	}

	public ServerType getServerType() {
		return serverType;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}
}
