package org.linagora.linShare.core.service;

import java.io.IOException;
import java.util.List;

import javax.naming.NamingException;

import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.exception.BusinessException;

public interface LDAPQueryService {
	
	public User getUser(String userId, String domainId, User actor) throws BusinessException;
	public List<User> getAllDomainUsers(String domainId, User actor) throws BusinessException;
	public boolean isAdmin(String userId, String domainId) throws BusinessException;
	public User auth(String userId, String userPasswd, String domainId) throws BusinessException, NamingException, IOException;
	public List<User> searchUser(String mail, String firstName, 
			String lastName, String domainId, User actor) throws BusinessException;
	
}
