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
package org.linagora.linshare.core.facade.webservice.admin.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.LdapWorkSpaceProvider;
import org.linagora.linshare.core.facade.webservice.common.dto.LightCommonDto;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "LDAPWorkSpaceProvider")
@Schema(name = "LDAPWorkSpaceProvider", description = "Used to provide workSpaces from an LDAP directory")
public class LDAPWorkSpaceProviderDto {

	@Schema(description = "uuid")
	private String uuid;

	@Schema(description = "LdapConnection")
	private LightCommonDto connection;

	@Schema(description = "GroupLdapPattern")
	private LightCommonDto pattern;

	@Schema(description = "BaseDn")
	private String baseDn;

	@Schema(description = "SearchInOtherDomains")
	private Boolean searchInOtherDomains;

	public LDAPWorkSpaceProviderDto() {
		super();
	}

	public LDAPWorkSpaceProviderDto(LdapWorkSpaceProvider workSpaceProvider) {
		this.uuid = workSpaceProvider.getUuid();
		this.connection = new LightCommonDto(workSpaceProvider.getLdapConnection().getLabel(),
				workSpaceProvider.getLdapConnection().getUuid());
		this.pattern = new LightCommonDto(workSpaceProvider.getWorkSpaceFilter().getLabel(),
				workSpaceProvider.getWorkSpaceFilter().getUuid());
		this.baseDn = workSpaceProvider.getBaseDn();
		this.searchInOtherDomains = workSpaceProvider.getSearchInOtherDomains();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getBaseDn() {
		return baseDn;
	}

	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}

	public LightCommonDto getConnection() {
		return connection;
	}

	public void setConnection(LightCommonDto connection) {
		this.connection = connection;
	}

	public LightCommonDto getPattern() {
		return pattern;
	}

	public void setPattern(LightCommonDto pattern) {
		this.pattern = pattern;
	}

	public Boolean getSearchInOtherDomains() {
		return searchInOtherDomains;
	}

	public void setSearchInOtherDomains(Boolean searchInOtherDomains) {
		this.searchInOtherDomains = searchInOtherDomains;
	}

}
