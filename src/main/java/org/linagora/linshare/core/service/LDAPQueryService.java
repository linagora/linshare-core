package org.linagora.linshare.core.service;

import java.io.IOException;
import java.util.List;

import javax.naming.NamingException;

import org.linagora.linshare.core.domain.entities.DomainPattern;
import org.linagora.linshare.core.domain.entities.LDAPConnection;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface LDAPQueryService {
	
	public User auth(LDAPConnection ldapConnection,	String baseDn, DomainPattern domainPattern, String userId, String userPasswd) throws BusinessException, NamingException, IOException;
	public List<User> searchUser(LDAPConnection ldapConnection,	String baseDn, DomainPattern domainPattern, String mail, String firstName, String lastName) throws BusinessException, NamingException, IOException;
	
}
