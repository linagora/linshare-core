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

import org.linagora.linshare.core.domain.constants.GroupProviderType;
import org.linagora.linshare.core.domain.entities.GroupProvider;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
	name = "GroupProvider",
	description = "A GroupProvider",
	discriminatorProperty = "type",
	discriminatorMapping = {
		@DiscriminatorMapping(value = "LDAP_PROVIDER", schema = LDAPGroupProviderDto.class),
	}
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
	@Type(value = LDAPGroupProviderDto.class, name="LDAP_PROVIDER")
})
public abstract class AbstractGroupProviderDto {

	@Schema(description = "GroupProvider's uuid", required = false)
	protected String uuid;

	@Schema(description = "GroupProvider's creation date", required = false)
	protected Date creationDate;

	@Schema(description = "GroupProvider's modification date", required = false)
	protected Date modificationDate;

	@Schema(description = "GroupProvider's domain", required = false)
	protected DomainLightDto domain;

	@Schema(required = true, description = "Default value is the only allowed and mandatory value.")
	public abstract GroupProviderType getType();

	protected GroupProviderType type;

	protected AbstractGroupProviderDto() {
		super();
	}

	public AbstractGroupProviderDto(GroupProvider groupProvider) {
		super();
		this.uuid = groupProvider.getUuid();
		this.creationDate = groupProvider.getCreationDate();
		this.modificationDate = groupProvider.getModificationDate();
		this.domain = new DomainLightDto(groupProvider.getDomain());
		this.type = groupProvider.getType();
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

	public DomainLightDto getDomain() {
		return domain;
	}

	public void setDomain(DomainLightDto domain) {
		this.domain = domain;
	}

	@Override
	public String toString() {
		return "AbstractGroupProviderDto [uuid=" + uuid + ", domain=" + domain + ", type=" + type + "]";
	}
}
