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
package org.linagora.linshare.core.service.impl;

import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.support.LdapContextSource;

public abstract class LDAPQueryServiceImpl {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected ContextSource getLdapContext(LdapConnection ldapConnection, String baseDn) {
		LdapContextSource ldapContextSource = new LdapContextSource();
		ldapContextSource.setUrl(ldapConnection.getProviderUrl());
		ldapContextSource.setBase(baseDn);
		String userDn = ldapConnection.getSecurityPrincipal();
		String password = ldapConnection.getSecurityCredentials();
		if (userDn != null && password != null) {
			ldapContextSource.setUserDn(userDn);
			ldapContextSource.setPassword(password);
		}

		try {
			ldapContextSource.afterPropertiesSet();
			return ldapContextSource;
		} catch (Exception e) {
			logger.error("Can not set ldap context");
			return null;
		}
	}

}
