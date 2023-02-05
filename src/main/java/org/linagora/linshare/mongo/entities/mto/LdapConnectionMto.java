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
package org.linagora.linshare.mongo.entities.mto;

import org.linagora.linshare.core.domain.entities.LdapConnection;

public class LdapConnectionMto {

	protected String uuid;

	protected String label;

	protected String providerUrl;

	protected String securityAuth;

	protected String securityPrincipal;

	protected String securityCredentials;

	public LdapConnectionMto() {
	}

	public LdapConnectionMto(LdapConnection connection) {
		this.uuid = connection.getUuid();
		this.label = connection.getLabel();
		this.providerUrl = connection.getProviderUrl();
		this.securityAuth = connection.getSecurityAuth();
		this.securityCredentials = connection.getSecurityCredentials();
		this.securityPrincipal = connection.getSecurityPrincipal();
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
