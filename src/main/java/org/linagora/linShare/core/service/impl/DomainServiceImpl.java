package org.linagora.linShare.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linShare.core.domain.entities.Domain;
import org.linagora.linShare.core.domain.entities.DomainPattern;
import org.linagora.linShare.core.domain.entities.LDAPConnection;
import org.linagora.linShare.core.domain.entities.MessagesConfiguration;
import org.linagora.linShare.core.domain.entities.Parameter;
import org.linagora.linShare.core.domain.entities.Role;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.entities.UserType;
import org.linagora.linShare.core.domain.vo.DomainPatternVo;
import org.linagora.linShare.core.domain.vo.DomainVo;
import org.linagora.linShare.core.domain.vo.LDAPConnectionVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.DomainPatternRepository;
import org.linagora.linShare.core.repository.DomainRepository;
import org.linagora.linShare.core.repository.LDAPConnectionRepository;
import org.linagora.linShare.core.repository.MessagesRepository;
import org.linagora.linShare.core.repository.ParameterRepository;
import org.linagora.linShare.core.service.DomainService;
import org.linagora.linShare.core.service.LDAPQueryService;

public class DomainServiceImpl implements DomainService {
	
	private final LDAPConnectionRepository ldapConnectionRepository;
	private final DomainPatternRepository domainPatternRepository;
	private final DomainRepository domainRepository;
	private final ParameterRepository parameterRepository;
	private final LDAPQueryService ldapQueryService;
	private final MessagesRepository messagesRepository;

	public DomainServiceImpl(LDAPConnectionRepository ldapConnectionRepository,
			DomainPatternRepository domainPatternRepository,
			DomainRepository domainRepository,
			ParameterRepository parameterRepository,
			LDAPQueryService ldapQueryService,
			MessagesRepository messagesRepository) {
		this.ldapConnectionRepository = ldapConnectionRepository;
		this.domainPatternRepository = domainPatternRepository;
		this.domainRepository = domainRepository;
		this.parameterRepository = parameterRepository;
		this.ldapQueryService = ldapQueryService;
		this.messagesRepository = messagesRepository;
	}

	public Domain createDomain(DomainVo domainVo) throws BusinessException {
		DomainPattern pattern = domainPatternRepository.findById(domainVo.getPattern().getIdentifier());
		LDAPConnection conn = ldapConnectionRepository.findById(domainVo.getLdapConnection().getIdentifier());
		Parameter param = null;
		if (domainVo.getParameterVo() == null) {
			Parameter entity = Parameter.getDefault(domainVo.getIdentifier()+"Param");
			MessagesConfiguration defaultMessages = messagesRepository.loadDefault();
			entity.setMessagesConfiguration(defaultMessages);
			param = parameterRepository.create(entity);
		} else {
			param = parameterRepository.loadConfig(domainVo.getParameterVo().getIdentifier());
		}
		Domain domain = new Domain(domainVo.getIdentifier(), domainVo.getDifferentialKey(), pattern, conn, param);
		Domain createdDomain = domainRepository.create(domain);
		return createdDomain;
	}

	public DomainPattern createDomainPattern(DomainPatternVo domainPatternVo) throws BusinessException {
		DomainPattern domainPattern = new DomainPattern(domainPatternVo);
		DomainPattern createdDomain = domainPatternRepository.create(domainPattern);
		return createdDomain;
		
	}

	public LDAPConnection createLDAPConnection(LDAPConnectionVo ldapConnectionVo) throws BusinessException {
		LDAPConnection ldapConnection = new LDAPConnection(ldapConnectionVo);
		LDAPConnection createdLDAPConnection = ldapConnectionRepository.create(ldapConnection);
		return createdLDAPConnection;
	}
	
	public LDAPConnection retrieveLDAPConnection(String identifier) throws BusinessException {
		return ldapConnectionRepository.findById(identifier);
	}
	
	public Domain retrieveDomain(String identifier) throws BusinessException {
		return domainRepository.findById(identifier);
	}
	
	public DomainPattern retrieveDomainPattern(String identifier)
			throws BusinessException {
		return domainPatternRepository.findById(identifier);
	}
	
	public void deleteDomain(String identifier) throws BusinessException {
		Domain domain = retrieveDomain(identifier);
		domainRepository.delete(domain);
	}
	
	public void deleteConnection(String connectionToDelete)
			throws BusinessException {
		LDAPConnection conn = retrieveLDAPConnection(connectionToDelete);
		ldapConnectionRepository.delete(conn);
	}
	
	public void deletePattern(String patternToDelete)
			throws BusinessException {
		DomainPattern pattern = retrieveDomainPattern(patternToDelete);
		domainPatternRepository.delete(pattern);
	}
	
	public List<String> getAllDomainIdentifiers() throws BusinessException {
		return domainRepository.findAllIdentifiers();
	}
	
	public List<Domain> findAllDomains() throws BusinessException {
		return domainRepository.findAll();
	}
	
	public List<User> searchUser(String mail, String firstName,
			String lastName, String domainId, User currentUser) throws BusinessException {
		
		Domain domain = null;
		if (domainId!=null) {
			domain = retrieveDomain(domainId);
		}
		
		if ((domain == null && currentUser == null) || (domain == null && currentUser != null && (currentUser.getRole() != Role.SUPERADMIN))) {
			throw new BusinessException("Domain cannot be null for this user : "+ (currentUser == null ? "null" : currentUser.getMail()));
		}
		
		List<User> users = new ArrayList<User>();
		
		if (domain != null && domain.getParameter().getClosedDomain()) {
			users.addAll(ldapQueryService.searchUser(mail, firstName, lastName, domain, currentUser));
		} else { //domain==null&&superadmin || !closedDomain
			List<Domain> domains = findAllDomains();
			for (Domain domainFound : domains) {
				users.addAll(ldapQueryService.searchUser(mail, firstName, lastName, domainFound, currentUser));
			}
			
		}
		
		return users;
	}
	
	public boolean userIsAllowedToShareWith(User sender, User recipient)
			throws BusinessException {
		Domain domain = retrieveDomain(sender.getDomain().getIdentifier());
		if (domain.getParameter().getRestrictedDomain()) {
			if (recipient.getUserType()!=UserType.INTERNAL) {
				return false;
			}
		}
		return true;
	}
	
	public boolean hasRightsToShareWithExternals(User sender)
			throws BusinessException {
		Domain domain = retrieveDomain(sender.getDomain().getIdentifier());
		if (domain.getParameter().getRestrictedDomain()) {
			return false;
		}
		return true;
	}
	
	public boolean userCanCreateGuest(User user) throws BusinessException {
    	Domain domain = user.getDomain();
    	
    	if (domain == null) {
    		return false;
    	}

    	if (user.getUserType()==UserType.GUEST) {
    		return domain.getParameter().getGuestCanCreateOther();
    	}
    	return domain.getParameter().getDomainWithGuests();
	}
	
	public List<DomainPattern> findAllDomainPatterns() throws BusinessException {
		return domainPatternRepository.findAll();
	}
	
	public List<LDAPConnection> findAllLDAPConnections()
			throws BusinessException {
		return ldapConnectionRepository.findAll();
	}
	
	public void updateLDAPConnection(LDAPConnection ldapConnection)
			throws BusinessException {
		LDAPConnection ldapConn = ldapConnectionRepository.findById(ldapConnection.getIdentifier());
		ldapConn.setProviderUrl(ldapConnection.getProviderUrl());
		ldapConn.setSecurityAuth(ldapConnection.getSecurityAuth());
		ldapConn.setSecurityCredentials(ldapConnection.getSecurityCredentials());
		ldapConn.setSecurityPrincipal(ldapConnection.getSecurityCredentials());
		ldapConnectionRepository.update(ldapConn);
	}
	
	public void updateDomainPattern(DomainPattern domainPattern)
			throws BusinessException {
		DomainPattern pattern = domainPatternRepository.findById(domainPattern.getIdentifier());
		pattern.setDescription(domainPattern.getDescription());
		pattern.setGetUserCommand(domainPattern.getGetUserCommand());
		pattern.setGetAllDomainUsersCommand(domainPattern.getGetAllDomainUsersCommand());
		pattern.setAuthCommand(domainPattern.getAuthCommand());
		pattern.setSearchUserCommand(domainPattern.getSearchUserCommand());
		pattern.setGetUserResult(domainPattern.getGetUserResult());
		domainPatternRepository.update(pattern);
	}
	
	public void updateDomain(String identifier, String differentialKey,
			LDAPConnection ldapConnection, DomainPattern domainPattern)
			throws BusinessException {
		Domain domain = domainRepository.findById(identifier);
		domain.setDifferentialKey(differentialKey);
		LDAPConnection conn = ldapConnectionRepository.findById(ldapConnection.getIdentifier());
		domain.setLdapConnection(conn);
		DomainPattern pattern = domainPatternRepository.findById(domainPattern.getIdentifier());
		domain.setPattern(pattern);
		domainRepository.update(domain);
	}

}
