package org.linagora.linshare.core.service.impl;

import java.io.IOException;
import java.util.List;

import javax.naming.NamingException;

import org.linagora.linshare.core.domain.entities.DomainPattern;
import org.linagora.linshare.core.domain.entities.LDAPConnection;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.LDAPQueryService;
import org.linagora.linshare.ldap.JScriptEvaluator;
import org.linagora.linshare.ldap.JScriptLdapQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LDAPQueryServiceImpl implements LDAPQueryService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LDAPQueryServiceImpl.class);
	
	private static final ThreadLocal < JScriptEvaluator > threadLocal = new ThreadLocal < JScriptEvaluator > () {
             @Override 
             protected JScriptEvaluator initialValue() {
                 return new JScriptEvaluator();
	         }
	     };

     public static JScriptEvaluator getCurrentThreadJSE() {
         return threadLocal.get();
     }
	     
	@Override
	public User getUser(LDAPConnection ldapConnection,	String baseDn, DomainPattern domainPattern, String userId) throws BusinessException, NamingException, IOException {

		LOGGER.debug("LDAPQueryServiceImpl.getUser("+ userId + ", " + baseDn);
		JScriptLdapQuery query = new JScriptLdapQuery(getCurrentThreadJSE(),ldapConnection, baseDn, domainPattern);
		return query.getUser(userId);
		
	}

	@Override
	public List<User> getAllDomainUsers(LDAPConnection ldapConnection, String baseDn, DomainPattern domainPattern) throws BusinessException, NamingException, IOException {
		LOGGER.debug("LDAPQueryServiceImpl.getAllDomainUsers(" + baseDn);
		JScriptLdapQuery query = new JScriptLdapQuery(getCurrentThreadJSE(),ldapConnection, baseDn, domainPattern);
		return query.getAllDomainUsers();
	}


	@Override
	public User auth(LDAPConnection ldapConnection, String baseDn, DomainPattern domainPattern, String userId, String userPasswd) throws BusinessException, NamingException, IOException {
		LOGGER.debug("LDAPQueryServiceImpl.auth: BEGIN:" + userId + ", " + baseDn );
		JScriptLdapQuery query = new JScriptLdapQuery(getCurrentThreadJSE(),ldapConnection, baseDn, domainPattern);
		return query.auth(userId, userPasswd);
	}


	@Override
	public List<User> searchUser(LDAPConnection ldapConnection, String baseDn, DomainPattern domainPattern, String mail, String firstName, String lastName) throws BusinessException, NamingException, IOException {
		LOGGER.debug("LDAPQueryServiceImpl.searchUser:" + mail + "," + firstName + "," + lastName + "," + baseDn);
		JScriptLdapQuery query = new JScriptLdapQuery(getCurrentThreadJSE(),ldapConnection, baseDn, domainPattern);
		return query.searchUser(mail, firstName, lastName);
	}
}
