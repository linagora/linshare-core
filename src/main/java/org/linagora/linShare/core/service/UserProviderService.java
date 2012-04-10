package org.linagora.linShare.core.service;

import java.io.IOException;
import java.util.List;

import javax.naming.NamingException;

import org.linagora.linShare.core.domain.entities.DomainPattern;
import org.linagora.linShare.core.domain.entities.LDAPConnection;
import org.linagora.linShare.core.domain.entities.LdapUserProvider;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.exception.BusinessException;

public interface UserProviderService {

	public List<String> findAllDomainPatternIdentifiers();
	public List<DomainPattern> findAllDomainPatterns() throws BusinessException;
	public DomainPattern createDomainPattern(DomainPattern domainPattern) throws BusinessException;
	public DomainPattern retrieveDomainPattern(String identifier) throws BusinessException;
	public void updateDomainPattern(DomainPattern domainPattern) throws BusinessException;
	public void deletePattern(String patternToDelete) throws BusinessException;
	
	public List<String> findAllLDAPConnectionIdentifiers();
	public List<LDAPConnection> findAllLDAPConnections() throws BusinessException;
	public LDAPConnection createLDAPConnection(LDAPConnection ldapConnection) throws BusinessException;
	public LDAPConnection retrieveLDAPConnection(String identifier) throws BusinessException;
	public void updateLDAPConnection(LDAPConnection ldapConnection) throws BusinessException;
	public void deleteConnection(String connectionToDelete) throws BusinessException;
	
	public void create(LdapUserProvider userProvider) throws BusinessException;
	public void delete(LdapUserProvider userProvider) throws BusinessException;
	public void update(LdapUserProvider userProvider) throws BusinessException;

	public List<User> searchUser(LdapUserProvider userProvider, String mail) throws BusinessException, NamingException, IOException;
	public List<User> searchUser(LdapUserProvider userProvider, String mail, String firstName, String lastName) throws BusinessException, NamingException, IOException;
	public User getUser(LdapUserProvider userProvider, String mail) throws BusinessException, NamingException, IOException;
	public User auth(LdapUserProvider userProvider,	String mail, String userPasswd) throws BusinessException, NamingException, IOException;

	public boolean patternIsDeletable(String patternToDelete);
	public boolean connectionIsDeletable(String connectionToDelete);
	
}
