package org.linagora.linShare.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linShare.core.domain.entities.Domain;
import org.linagora.linShare.core.domain.entities.DomainPattern;
import org.linagora.linShare.core.domain.entities.LDAPConnection;
import org.linagora.linShare.core.domain.entities.Parameter;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.vo.DomainPatternVo;
import org.linagora.linShare.core.domain.vo.DomainVo;
import org.linagora.linShare.core.domain.vo.LDAPConnectionVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.DomainPatternRepository;
import org.linagora.linShare.core.repository.DomainRepository;
import org.linagora.linShare.core.repository.LDAPConnectionRepository;
import org.linagora.linShare.core.repository.ParameterRepository;
import org.linagora.linShare.core.service.DomainService;
import org.linagora.linShare.core.service.LDAPQueryService;

public class DomainServiceImpl implements DomainService {
	
	private final LDAPConnectionRepository ldapConnectionRepository;
	private final DomainPatternRepository domainPatternRepository;
	private final DomainRepository domainRepository;
	private final ParameterRepository parameterRepository;
	private final LDAPQueryService ldapQueryService;

	public DomainServiceImpl(LDAPConnectionRepository ldapConnectionRepository,
			DomainPatternRepository domainPatternRepository,
			DomainRepository domainRepository,
			ParameterRepository parameterRepository,
			LDAPQueryService ldapQueryService) {
		this.ldapConnectionRepository = ldapConnectionRepository;
		this.domainPatternRepository = domainPatternRepository;
		this.domainRepository = domainRepository;
		this.parameterRepository = parameterRepository;
		this.ldapQueryService = ldapQueryService;
	}

	public Domain createDomain(DomainVo domainVo) throws BusinessException {
		DomainPattern pattern = domainPatternRepository.findById(domainVo.getPattern().getIdentifier());
		LDAPConnection conn = ldapConnectionRepository.findById(domainVo.getLdapConnection().getIdentifier());
		Parameter param = parameterRepository.loadConfig(domainVo.getParameterVo().getIdentifier());
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
		Domain domain = domainRepository.findById(identifier);
		domainRepository.delete(domain);
		
	}
	
	public List<String> getAllDomainIdentifiers() throws BusinessException {
		return domainRepository.findAllIdentifiers();
	}
	
	public List<Domain> findAllDomains() throws BusinessException {
		return domainRepository.findAll();
	}
	
	public List<User> searchUser(String mail, String firstName,
			String lastName, String domainId, User currentUser) throws BusinessException {
		Domain domain = retrieveDomain(domainId);
		List<User> users = new ArrayList<User>();
		
		if (domain.getParameter().getClosedDomain()) {
			users.addAll(ldapQueryService.searchUser(mail, firstName, lastName, domain, currentUser));
		} else {
			List<Domain> domains = findAllDomains();
			for (Domain domainFound : domains) {
				users.addAll(ldapQueryService.searchUser(mail, firstName, lastName, domainFound, currentUser));
			}
			
		}
		
		return users;
	}

}
