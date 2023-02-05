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

import org.linagora.linshare.core.facade.webservice.admin.dto.LDAPUserProviderDto;

public class LdapUserProvider extends UserProvider {

	private String baseDn;

	private LdapConnection ldapConnection;

	private UserLdapPattern pattern;

	protected LdapUserProvider() {
	}

	public LdapUserProvider(AbstractDomain domain, String baseDn, LdapConnection ldapConnection,
			UserLdapPattern pattern) {
		super(domain);
		this.baseDn = baseDn;
		this.ldapConnection = ldapConnection;
		this.pattern = pattern;
	}

	public UserLdapPattern getPattern() {
		return pattern;
	}

	public void setPattern(UserLdapPattern pattern) {
		this.pattern = pattern;
	}

	public String getBaseDn() {
		return baseDn;
	}

	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}

	public void setLdapConnection(LdapConnection ldapConnection) {
		this.ldapConnection = ldapConnection;
	}

	public LdapConnection getLdapConnection() {
		return ldapConnection;
	}

	@Override
	public String toString() {
		return "LdapUserProvider [baseDn=" + baseDn + ", uuid=" + uuid + "]";
	}

	@Override
	public LDAPUserProviderDto toLDAPUserProviderDto() {
		LDAPUserProviderDto l = new LDAPUserProviderDto();
		l.setUuid(uuid);
		l.setBaseDn(baseDn);
		l.setUserLdapPatternUuid(this.pattern.getUuid());
		l.setLdapConnectionUuid(this.ldapConnection.getUuid());
		return l;
	}
}
