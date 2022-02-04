/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2022. Contribute to
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
package org.linagora.linshare.core.facade.webservice.adminv5.dto;

import java.util.Date;

import org.linagora.linshare.core.domain.constants.WorkSpaceProviderType;
import org.linagora.linshare.core.domain.entities.WorkSpaceProvider;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
	name = "WorkSpaceProvider",
	description = "A WorkSpaceProvider",
	discriminatorProperty = "type",
	discriminatorMapping = {
		@DiscriminatorMapping(value = "LDAP_PROVIDER", schema = LDAPWorkSpaceProviderDto.class),
	}
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
	@Type(value = LDAPWorkSpaceProviderDto.class, name="LDAP_PROVIDER")
})
public abstract class AbstractWorkSpaceProviderDto {

	@Schema(description = "WorkSpaceProvider's uuid", required = false)
	protected String uuid;

	@Schema(description = "workSpaceProvider's creation date", required = false)
	protected Date creationDate;

	@Schema(description = "WorkSpaceProvider's modification date", required = false)
	protected Date modificationDate;

	@Schema(description = "WorkSpaceProvider's domain", required = false)
	protected DomainLightDto domain;

	@Schema(required = true, description = "Default value is the only allowed and mandatory value.")
	public abstract WorkSpaceProviderType getType();

	protected WorkSpaceProviderType type;

	protected AbstractWorkSpaceProviderDto() {
		super();
	}

	public AbstractWorkSpaceProviderDto(WorkSpaceProvider workSpaceProvider) {
		super();
		this.uuid = workSpaceProvider.getUuid();
		this.creationDate = workSpaceProvider.getCreationDate();
		this.modificationDate = workSpaceProvider.getModificationDate();
		this.domain = new DomainLightDto(workSpaceProvider.getDomain());
		this.type = workSpaceProvider.getType();
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
		return "AbstractWorkSpaceProviderDto [uuid=" + uuid + ", domain=" + domain + ", type=" + type + "]";
	}
}
