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

import org.linagora.linshare.core.domain.entities.LdapConnection;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "LdapConnection")
@Schema(name = "LdapConnection", description = "An LDAP directory connection descriptor")
public class LdapConnectionDto {

	@Schema(description = "Uuid")
	private String uuid;

	@Schema(description = "Label")
	private String label;

	@Schema(description = "ProviderUrl")
	private String providerUrl;

	@Schema(description = "SecurityAuth")
	private String securityAuth;

	@Schema(description = "SecurityPrincipal")
	private String securityPrincipal;

	@Schema(description = "SecurityCredentials")
	private String securityCredentials;

	public LdapConnectionDto(LdapConnection ldapConnection) {
		this.uuid = ldapConnection.getUuid();
		this.label= ldapConnection.getLabel();
		this.providerUrl = ldapConnection.getProviderUrl();
		this.securityAuth = ldapConnection.getSecurityAuth();
		this.securityPrincipal = ldapConnection.getSecurityPrincipal();
		this.securityCredentials = ldapConnection.getSecurityCredentials();
	}

	public LdapConnectionDto() {
		super();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getProviderUrl() {
		return providerUrl;
	}

	public void setProviderUrl(String providerUrl) {
		this.providerUrl = providerUrl;
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
