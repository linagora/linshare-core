/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
package org.linagora.linshare.core.domain.entities.temp;

import org.linagora.linshare.core.domain.entities.DomainPattern;
import org.linagora.linshare.core.domain.entities.LDAPConnection;

public class LdapUserProvider extends UserProvider {

	private LDAPConnection primaryLdapConnection;
	
	private LDAPConnection secondaryLdapConnection;
	
	private DomainPattern domainPattern;

	private String baseDn;
	
	public LdapUserProvider(LDAPConnection primaryLdapConnection, LDAPConnection secondaryLdapConnection, DomainPattern domainPattern, String baseDn) {
		super();
		this.primaryLdapConnection = primaryLdapConnection;
		this.secondaryLdapConnection = secondaryLdapConnection;
		this.domainPattern = domainPattern;
		this.baseDn = baseDn;
	}
	
	public LdapUserProvider(LDAPConnection ldapConnection, DomainPattern domainPattern, String baseDn) {
		super();
		this.primaryLdapConnection = ldapConnection;
		this.secondaryLdapConnection = null;
		this.domainPattern = domainPattern;
		this.baseDn = baseDn;
	}


	public LDAPConnection getPrimaryLdapConnection() {
		return primaryLdapConnection;
	}

	public void setPrimaryLdapConnection(LDAPConnection primaryLdapConnection) {
		this.primaryLdapConnection = primaryLdapConnection;
	}

	public LDAPConnection getSecondaryLdapConnection() {
		return secondaryLdapConnection;
	}

	public void setSecondaryLdapConnection(LDAPConnection secondaryLdapConnection) {
		this.secondaryLdapConnection = secondaryLdapConnection;
	}

	public DomainPattern getDomainPattern() {
		return domainPattern;
	}

	public void setDomainPattern(DomainPattern domainPattern) {
		this.domainPattern = domainPattern;
	}

	public String getBaseDn() {
		return baseDn;
	}

	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}

	
}
