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
package org.linagora.linshare.ldap;

import java.util.Map;

import javax.naming.directory.SearchControls;

import org.linagora.linshare.core.domain.entities.LdapAttribute;

public class ControlContext {

	protected Map<String, LdapAttribute> ldapDbAttributes;
	
	protected SearchControls searchControls;

	public ControlContext(Map<String, LdapAttribute> ldapDbAttributes, SearchControls searchControls) {
		super();
		this.ldapDbAttributes = ldapDbAttributes;
		this.searchControls = searchControls;
	}

	public Map<String, LdapAttribute> getLdapDbAttributes() {
		return ldapDbAttributes;
	}

	public void setLdapDbAttributes(Map<String, LdapAttribute> ldapDbAttributes) {
		this.ldapDbAttributes = ldapDbAttributes;
	}

	public SearchControls getSearchControls() {
		return searchControls;
	}

	public void setSearchControls(SearchControls searchControls) {
		this.searchControls = searchControls;
	}
}
