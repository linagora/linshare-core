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

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "LDAPUserProvider")
@Schema(name = "LDAPUserProvider", description = "Used to provide users from an LDAP directory")
public class LDAPUserProviderDto {

	@Schema(description = "uuid")
	private String uuid;

	@Schema(description = "LdapConnectionUuid")
	private String ldapConnectionUuid = "";

	@Schema(description = "UserLdapPatternUuid")
	private String userLdapPatternUuid = "";

	@Schema(description = "BaseDn")
	private String baseDn = "";

	public LDAPUserProviderDto() {
		super();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getLdapConnectionUuid() {
		return ldapConnectionUuid;
	}

	public void setLdapConnectionUuid(String ldapConnectionUuid) {
		this.ldapConnectionUuid = ldapConnectionUuid;
	}

	public String getUserLdapPatternUuid() {
		return userLdapPatternUuid;
	}

	public void setUserLdapPatternUuid(String userLdapPatternUuid) {
		this.userLdapPatternUuid = userLdapPatternUuid;
	}

	public String getBaseDn() {
		return baseDn;
	}

	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}
}
