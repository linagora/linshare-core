package org.linagora.linShare.core.service.impl;

import java.util.List;

import javax.naming.NamingException;

import org.linagora.linShare.core.domain.entities.Domain;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.service.LDAPQueryService;
import org.linagora.linShare.ldap.JScriptEvaluator;
import org.linagora.linShare.ldap.JScriptLdapQuery;
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
	public User getUser(String userId, Domain domain, User actor) throws BusinessException {

		LOGGER.debug("LDAPQueryServiceImpl.getUser("+ userId + ", " + domain.getDifferentialKey());
		JScriptLdapQuery query = new JScriptLdapQuery(getCurrentThreadJSE(), domain);
		return query.getUser(userId);		
		
	}

	@Override
	public List<User> getAllDomainUsers(Domain domain, User actor) throws BusinessException {
		
		LOGGER.debug("LDAPQueryServiceImpl.getAllDomainUsers(" + domain.getDifferentialKey());
		JScriptLdapQuery query = new JScriptLdapQuery(getCurrentThreadJSE(), domain);
		return query.getAllDomainUsers();
	}

	@Override
	public User auth(String login, String userPasswd, Domain domain) throws BusinessException, NamingException {
		
		LOGGER.debug("LDAPQueryServiceImpl.auth: BEGIN:" + login + ", " + domain.getDifferentialKey() );
		JScriptLdapQuery query = new JScriptLdapQuery(getCurrentThreadJSE(), domain);
		return query.auth(login, userPasswd);
	}

	@Override
	public List<User> searchUser(String mail, String firstName,	String lastName, Domain domain, User actor) throws BusinessException {
		
		LOGGER.debug("LDAPQueryServiceImpl.searchUser: " + ":"  + mail + ", " + firstName + ", " + lastName + ", " + domain.getDifferentialKey());
		JScriptLdapQuery query = new JScriptLdapQuery(getCurrentThreadJSE(), domain);
		return query.searchUser(mail, firstName, lastName);
	}
}
