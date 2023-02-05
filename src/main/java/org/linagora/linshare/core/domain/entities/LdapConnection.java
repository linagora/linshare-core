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
package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.domain.constants.ServerType;
import org.linagora.linshare.core.facade.webservice.admin.dto.LdapConnectionDto;

public class LdapConnection extends RemoteServer {

	protected String securityAuth;

	protected String securityPrincipal;

	protected String securityCredentials;

	public LdapConnection() {
		super();
		this.serverType = ServerType.LDAP;
	}

	public LdapConnection(LdapConnectionDto ldapConnectionDto) {
		super();
		this.serverType = ServerType.LDAP;
		this.label = ldapConnectionDto.getLabel();
		this.uuid = ldapConnectionDto.getUuid();
		this.providerUrl = ldapConnectionDto.getProviderUrl();
		this.securityAuth = ldapConnectionDto.getSecurityAuth();
		this.securityPrincipal = ldapConnectionDto.getSecurityPrincipal();
		this.securityCredentials = ldapConnectionDto.getSecurityCredentials();
	}

	public LdapConnection(String label, String providerUrl, String securityAuth) {
		super();
		this.serverType = ServerType.LDAP;
		this.label = label;
		this.providerUrl = providerUrl;
		this.securityAuth = securityAuth;
		this.securityCredentials = null;
		this.securityPrincipal = null;
	}

	public String getSecurityAuth() {
		return securityAuth;
	}

	public void setSecurityAuth(String securityAuth) {
		this.securityAuth = securityAuth;
	}

	public String getSecurityPrincipal() {
		return securityPrincipal;
	}

	public void setSecurityPrincipal(String securityPrincipal) {
		this.securityPrincipal = securityPrincipal;
	}

	public String getSecurityCredentials() {
		return securityCredentials;
	}

	public void setSecurityCredentials(String securityCredentials) {
		this.securityCredentials = securityCredentials;
	}
}
