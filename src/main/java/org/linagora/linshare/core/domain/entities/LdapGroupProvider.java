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

import org.linagora.linshare.core.facade.webservice.admin.dto.LDAPGroupProviderDto;
import org.linagora.linshare.core.facade.webservice.common.dto.LightCommonDto;

public class LdapGroupProvider extends GroupProvider {

	protected GroupLdapPattern groupPattern;

	protected String baseDn;

	protected LdapConnection ldapConnection;

	protected Boolean searchInOtherDomains;

	public LdapGroupProvider() {
		super();
	}

	public LdapGroupProvider(GroupLdapPattern groupPattern, String baseDn, LdapConnection ldapConnection,
			Boolean searchInOtherDomains) {
		super();
		this.groupPattern = groupPattern;
		this.baseDn = baseDn;
		this.ldapConnection = ldapConnection;
		this.searchInOtherDomains = searchInOtherDomains != null ? searchInOtherDomains : true;
	}

	public LdapGroupProvider(AbstractDomain domain, GroupLdapPattern groupPattern, String baseDn, LdapConnection ldapConnection,
			Boolean searchInOtherDomains) {
		super(domain);
		this.groupPattern = groupPattern;
		this.baseDn = baseDn;
		this.ldapConnection = ldapConnection;
		this.searchInOtherDomains = searchInOtherDomains != null ? searchInOtherDomains : true;
	}

	public GroupLdapPattern getGroupPattern() {
		return groupPattern;
	}

	public void setGroupPattern(GroupLdapPattern groupPattern) {
		this.groupPattern = groupPattern;
	}

	public String getBaseDn() {
		return baseDn;
	}

	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}

	public LdapConnection getLdapConnection() {
		return ldapConnection;
	}

	public void setLdapConnection(LdapConnection ldapConnection) {
		this.ldapConnection = ldapConnection;
	}

	@Override
	public Boolean getSearchInOtherDomains() {
		return searchInOtherDomains;
	}

	public void setSearchInOtherDomains(Boolean searchInOtherDomains) {
		this.searchInOtherDomains = searchInOtherDomains;
	}

	@Override
	public LDAPGroupProviderDto toLDAPGroupProviderDto() {
		LDAPGroupProviderDto groupProvider = new LDAPGroupProviderDto();
		groupProvider.setUuid(uuid);
		groupProvider.setBaseDn(baseDn);
		groupProvider.setPattern(
				new LightCommonDto(this.groupPattern.getLabel(), this.groupPattern.getUuid()));
		groupProvider.setConnection(
				new LightCommonDto(this.ldapConnection.getLabel(), this.ldapConnection.getUuid()));
		groupProvider.setSearchInOtherDomains(this.searchInOtherDomains);
		return groupProvider;
	}
}
