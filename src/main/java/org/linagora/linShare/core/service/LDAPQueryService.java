package org.linagora.linShare.core.service;

import java.io.IOException;
import java.util.List;

import javax.naming.NamingException;

import org.linagora.linShare.core.domain.entities.Domain;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.exception.BusinessException;

public interface LDAPQueryService {
	
	public User getUser(String userId, Domain domain, User actor) throws BusinessException;
	public List<User> getAllDomainUsers(Domain domain, User actor) throws BusinessException;
	public User auth(String userId, String userPasswd, Domain domain) throws BusinessException, NamingException, IOException;
	public List<User> searchUser(String mail, String firstName, 
			String lastName, Domain domain, User actor) throws BusinessException;
	
}
