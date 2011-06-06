package org.linagora.linShare.core.service;

import java.util.List;

import org.linagora.linShare.core.domain.entities.Domain;
import org.linagora.linShare.core.domain.entities.DomainPattern;
import org.linagora.linShare.core.domain.entities.LDAPConnection;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.vo.DomainPatternVo;
import org.linagora.linShare.core.domain.vo.DomainVo;
import org.linagora.linShare.core.domain.vo.LDAPConnectionVo;
import org.linagora.linShare.core.exception.BusinessException;

public interface DomainService {
	
	public Domain createDomain(DomainVo domainVo) throws BusinessException;
	public DomainPattern createDomainPattern(DomainPatternVo domainPatternVo) throws BusinessException;
	public LDAPConnection createLDAPConnection(LDAPConnectionVo ldapConnectionVo) throws BusinessException;
	public LDAPConnection retrieveLDAPConnection(String identifier) throws BusinessException;
	public Domain retrieveDomain(String identifier) throws BusinessException;
	public DomainPattern retrieveDomainPattern(String identifier) throws BusinessException;
	public void deleteDomain(String identifier) throws BusinessException;
	public List<String> getAllDomainIdentifiers() throws BusinessException;
	public List<Domain> findAllDomains() throws BusinessException;
	public List<User> searchUser(String mail, String firstName, String lastName, String domainId, User currentUser, boolean multiDomain) throws BusinessException;
	public boolean userIsAllowedToShareWith(User sender, User recipient) throws BusinessException;
	public boolean hasRightsToShareWithExternals(User sender) throws BusinessException;
	public boolean userCanCreateGuest(User user) throws BusinessException;
	public List<DomainPattern> findAllDomainPatterns() throws BusinessException;
	public List<LDAPConnection> findAllLDAPConnections() throws BusinessException;
	public void updateLDAPConnection(LDAPConnection ldapConnection) throws BusinessException;
	public void updateDomainPattern(DomainPattern domainPattern) throws BusinessException;
	public void updateDomain(String identifier, String differentialKey, LDAPConnection ldapConnection, DomainPattern domainPattern) throws BusinessException;
	public void deleteConnection(String connectionToDelete) throws BusinessException;
	public void deletePattern(String patternToDelete) throws BusinessException;

}
