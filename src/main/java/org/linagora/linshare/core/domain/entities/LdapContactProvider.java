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

public class LdapContactProvider extends ContactProvider {

	private String baseDn;

	private ContactLdapPattern ldapPattern;

	private LdapConnection ldapConnexion;

	public LdapContactProvider() {
		super();
	}

	public LdapContactProvider(String baseDn, ContactLdapPattern ldapPattern,
			LdapConnection ldapConnexion) {
		super();
		this.baseDn = baseDn;
		this.ldapPattern = ldapPattern;
		this.ldapConnexion = ldapConnexion;
	}

	public String getBaseDn() {
		return baseDn;
	}

	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}

	public ContactLdapPattern getLdapPattern() {
		return ldapPattern;
	}

	public void setLdapPattern(ContactLdapPattern ldapPattern) {
		this.ldapPattern = ldapPattern;
	}

	public LdapConnection getLdapConnexion() {
		return ldapConnexion;
	}

	public void setLdapConnexion(LdapConnection ldapConnexion) {
		this.ldapConnexion = ldapConnexion;
	}

	@Override
	public String toString() {
		return "LdapContactProvider [baseDn=" + baseDn + ", ldapPattern="
				+ ldapPattern.getUuid() + ", ldapConnexion=" + ldapConnexion.getUuid() + "]";
	}
}
