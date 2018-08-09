/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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

import org.linagora.linshare.core.facade.webservice.admin.dto.LDAPGroupProviderDto;
import org.linagora.linshare.core.facade.webservice.common.dto.LightCommonDto;

public class LdapGroupProvider extends GroupProvider {

	protected GroupLdapPattern groupPattern;

	protected String baseDn;

	protected LdapConnection ldapConnection;

	protected Boolean automaticUserCreation;

	protected Boolean forceCreation;

	public LdapGroupProvider() {
		super();
	}

	public LdapGroupProvider(GroupLdapPattern groupPattern, String baseDn, LdapConnection ldapConnection,
			Boolean automaticUserCreation, Boolean forceCreation) {
		super();
		this.groupPattern = groupPattern;
		this.baseDn = baseDn;
		this.ldapConnection = ldapConnection;
		this.automaticUserCreation = automaticUserCreation;
		this.forceCreation = forceCreation;
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

	public Boolean getAutomaticUserCreation() {
		return automaticUserCreation;
	}

	public void setAutomaticUserCreation(Boolean automaticUserCreation) {
		this.automaticUserCreation = automaticUserCreation;
	}

	public Boolean getForceCreation() {
		return forceCreation;
	}

	public void setForceCreation(Boolean forceCreation) {
		this.forceCreation = forceCreation;
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
		groupProvider.setAutomaticUserCreation(this.automaticUserCreation);
		groupProvider.setForceCreation(this.forceCreation);
		return groupProvider;
	}
}
