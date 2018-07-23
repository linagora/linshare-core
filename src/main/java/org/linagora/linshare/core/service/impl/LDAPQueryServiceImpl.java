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
