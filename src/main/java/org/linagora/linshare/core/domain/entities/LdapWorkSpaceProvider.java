/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2021. Contribute to
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
package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.facade.webservice.admin.dto.LDAPWorkSpaceProviderDto;
import org.linagora.linshare.core.facade.webservice.common.dto.LightCommonDto;

public class LdapWorkSpaceProvider extends WorkSpaceProvider {

	protected LdapWorkSpaceFilter workSpaceFilter;

	protected String baseDn;

	protected LdapConnection ldapConnection;

	protected Boolean searchInOtherDomains;

	public LdapWorkSpaceProvider() {
		super();
	}

	public LdapWorkSpaceProvider(LdapWorkSpaceFilter workSpaceFilter, String baseDn, LdapConnection ldapConnection,
			Boolean searchInOtherDomains) {
		super();
		this.workSpaceFilter = workSpaceFilter;
		this.baseDn = baseDn;
		this.ldapConnection = ldapConnection;
		this.searchInOtherDomains = searchInOtherDomains != null ? searchInOtherDomains : true;
	}

	public LdapWorkSpaceProvider(AbstractDomain domain, LdapWorkSpaceFilter workSpaceFilter, String baseDn, LdapConnection ldapConnection,
			Boolean searchInOtherDomains) {
		super(domain);
		this.workSpaceFilter = workSpaceFilter;
		this.baseDn = baseDn;
		this.ldapConnection = ldapConnection;
		this.searchInOtherDomains = searchInOtherDomains != null ? searchInOtherDomains : true;
	}

	public LdapWorkSpaceFilter getWorkSpaceFilter() {
		return workSpaceFilter;
	}

	public void setWorkSpaceFilter(LdapWorkSpaceFilter workSpaceFilter) {
		this.workSpaceFilter = workSpaceFilter;
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
	public LDAPWorkSpaceProviderDto toLDAPWorkSpaceProviderDto() {
		LDAPWorkSpaceProviderDto workSpaceProvider = new LDAPWorkSpaceProviderDto();
		workSpaceProvider.setUuid(uuid);
		workSpaceProvider.setBaseDn(baseDn);
		workSpaceProvider.setPattern(
				new LightCommonDto(this.workSpaceFilter.getLabel(), this.workSpaceFilter.getUuid()));
		workSpaceProvider.setConnection(new LightCommonDto(this.ldapConnection.getLabel(), this.ldapConnection.getUuid()));
		workSpaceProvider.setSearchInOtherDomains(this.searchInOtherDomains);
		return workSpaceProvider;
	}
}
