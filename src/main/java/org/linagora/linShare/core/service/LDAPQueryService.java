package org.linagora.linShare.core.service;

import java.io.IOException;
import java.util.List;

import javax.naming.NamingException;

import org.linagora.linShare.core.domain.entities.DomainPattern;
import org.linagora.linShare.core.domain.entities.LDAPConnection;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.exception.BusinessException;

public interface LDAPQueryService {
	
	public User getUser(LDAPConnection ldapConnection,	String baseDn, DomainPattern domainPattern, String userId) throws BusinessException;
	public List<User> getAllDomainUsers(LDAPConnection ldapConnection,	String baseDn, DomainPattern domainPattern) throws BusinessException;
	public User auth(LDAPConnection ldapConnection,	String baseDn, DomainPattern domainPattern, String userId, String userPasswd) throws BusinessException, NamingException, IOException;
	public List<User> searchUser(LDAPConnection ldapConnection,	String baseDn, DomainPattern domainPattern, String mail, String firstName, 
			String lastName) throws BusinessException;
	
}
