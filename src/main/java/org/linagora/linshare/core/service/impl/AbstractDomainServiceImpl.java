package org.linagora.linshare.core.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;

import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.UserType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainPattern;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.GuestDomain;
import org.linagora.linshare.core.domain.entities.LDAPConnection;
import org.linagora.linshare.core.domain.entities.LdapUserProvider;
import org.linagora.linshare.core.domain.entities.MessagesConfiguration;
import org.linagora.linshare.core.domain.entities.RootDomain;
import org.linagora.linshare.core.domain.entities.SubDomain;
import org.linagora.linshare.core.domain.entities.TopDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.MessagesRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.DomainPolicyService;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.core.service.UserProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractDomainServiceImpl implements AbstractDomainService {
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractDomainServiceImpl.class);
	
	private final AbstractDomainRepository abstractDomainRepository;
	private final DomainPolicyService domainPolicyService;
	private final FunctionalityService functionalityService;
	private final UserProviderService userProviderService;
	private final MessagesRepository messagesRepository;

	public AbstractDomainServiceImpl(AbstractDomainRepository abstractDomainRepository,
			DomainPolicyService domainPolicyService,
			FunctionalityService functionalityService,
			UserProviderService userProviderRepository,
			MessagesRepository messagesRepository) {
		this.abstractDomainRepository = abstractDomainRepository;
		this.domainPolicyService = domainPolicyService;
		this.functionalityService = functionalityService;
		this.userProviderService = userProviderRepository;
		this.messagesRepository = messagesRepository;
	}
	
	@Override
	public RootDomain getUniqueRootDomain() throws BusinessException {
		RootDomain domain = (RootDomain) abstractDomainRepository.findById(LinShareConstants.rootDomainIdentifier);
		if(domain == null) {
			throw new BusinessException(BusinessErrorCode.DATABASE_INCOHERENCE_NO_ROOT_DOMAIN,"No root domain found in the database.");
		}
		return domain;
	}

	private void createDomain(AbstractDomain domain, AbstractDomain parentDomain ) throws BusinessException {
		
		
		if(domain.getIdentifier()== null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_ID_NOT_FOUND,"This new domain has no identifier.");
		}
		
		if(abstractDomainRepository.findById(domain.getIdentifier()) != null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_ID_ALREADY_EXISTS,"This new domain identifier already exists.");
		}
		
		if(domain.getPolicy() == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_POLICY_NOT_FOUND,"This new domain has no domain policy.");
		}
		if(domain.getUserProvider()!=null) {
			if(domain.getUserProvider().getLdapconnexion() == null) {
				throw new BusinessException(BusinessErrorCode.LDAP_CONNECTION_NOT_FOUND,"This new domain has no ldap connection.");
			}
			if(domain.getUserProvider().getPattern() == null) {
				throw new BusinessException(BusinessErrorCode.DOMAIN_PATTERN_NOT_FOUND,"This new domain has no domain pattern.");
			}
			if(domain.getUserProvider().getBaseDn() == null) {
				throw new BusinessException(BusinessErrorCode.DOMAIN_BASEDN_NOT_FOUND,"This new domain has no BaseDn.");
			}
		} else {
			logger.debug("creation of a TopDomain without an UserProvider.");
		}
		
		DomainPolicy policy = domainPolicyService.findById(domain.getPolicy().getIdentifier());
		
		if(policy == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_POLICY_NOT_FOUND,"This new domain has a wrong domain policy identifier.");
		}
		
		domain.setPolicy(policy);
		domain.setParentDomain(parentDomain);
		MessagesConfiguration msg = new MessagesConfiguration(messagesRepository.loadDefault());
		domain.setMessagesConfiguration(msg);
		
		if (domain.getUserProvider() != null ) {
			userProviderService.create(domain.getUserProvider());
		}
		
		// Object creation
		abstractDomainRepository.create(domain);
		
		// Update ancestor relation
		parentDomain.addSubdomain(domain);
		abstractDomainRepository.update(parentDomain);
	}
	
	
	@Override
	public TopDomain createTopDomain(TopDomain topDomain) throws BusinessException {
		logger.debug("TopDomain creation attempt : " + topDomain.toString());
		createDomain(topDomain, getUniqueRootDomain());
		return topDomain; 
	}


	@Override
	public SubDomain createSubDomain(SubDomain subDomain) throws BusinessException {
	
		logger.debug("SubDomain creation attempt : " + subDomain.toString());
		
		if(subDomain.getParentDomain() == null || subDomain.getParentDomain().getIdentifier() == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_ID_NOT_FOUND,"This new domain has no parent domain defined.");
		}
		
		AbstractDomain parentDomain  = retrieveDomain(subDomain.getParentDomain().getIdentifier());
		if(parentDomain == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_ID_NOT_FOUND,"Parent domain not found.");
		}
		
		createDomain(subDomain, parentDomain);
		return subDomain;
	}
	
	@Override
	public GuestDomain createGuestDomain(GuestDomain guestDomain) throws BusinessException {
		
		logger.debug("SubDomain creation attempt : " + guestDomain.toString());
		
		if(guestDomain.getParentDomain() == null || guestDomain.getParentDomain().getIdentifier() == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_ID_NOT_FOUND,"This new domain has no parent domain defined.");
		}
		
		AbstractDomain parentDomain  = retrieveDomain(guestDomain.getParentDomain().getIdentifier());
		if(parentDomain == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_ID_NOT_FOUND,"Parent domain not found.");
		}
		
		createDomain(guestDomain, parentDomain);
		return guestDomain;
	}

	@Override
	public AbstractDomain retrieveDomain(String identifier) {
		return abstractDomainRepository.findById(identifier);
	}
	
	@Override
	public void deleteDomain(String identifier) throws BusinessException {
		AbstractDomain domain = retrieveDomain(identifier);
		
		abstractDomainRepository.delete(domain);
		// Remove element from its ancestor. It does not need to be updated. Do not know why, implicit update somewhere ?
		if(domain.getParentDomain()!=null) {
			for (Iterator<AbstractDomain> iterator = domain.getParentDomain().getSubdomain().iterator(); iterator.hasNext();) {
				AbstractDomain s= (AbstractDomain) iterator.next();
				if(s.getIdentifier().equals(identifier)) {
					iterator.remove();
					//					abstractDomainRepository.update(domain.getParentDomain());
					break;
				}
			}
		}
	}
	
	@Override
	public List<String> getAllDomainIdentifiers() {
		return abstractDomainRepository.findAllDomainIdentifiers();
	}
	
	
	private List<AbstractDomain> getMyDomainRecursively(AbstractDomain domain) {
		List<AbstractDomain> domains = new ArrayList<AbstractDomain>();
		if(domain != null) {
			domains.add(domain);
			if(domain.getSubdomain() != null) {
				for (AbstractDomain d : domain.getSubdomain()) {
					domains.addAll(getMyDomainRecursively(d));
				}
			}
		}
		return domains;
	}

	
	@Override
	public List<String> getAllMyDomainIdentifiers(String personalDomainIdentifer) {
		List<String> domains = new ArrayList<String>();
		
		AbstractDomain domain = retrieveDomain(personalDomainIdentifer);
		for (AbstractDomain abstractDomain : getMyDomainRecursively(domain)) {
			domains.add(abstractDomain.getIdentifier());
		}
		return domains;
	}

	@Override
	public List<AbstractDomain> getAllDomains(){
		return abstractDomainRepository.findAllDomain();
	}
	
	@Override
	public List<AbstractDomain> getAllTopAndSubDomain(){
		return abstractDomainRepository.findAllTopAndSubDomain();
	}

	@Override
	public List<AbstractDomain> getAllTopDomain(){
		return abstractDomainRepository.findAllTopDomain();
	}

	@Override
	public List<AbstractDomain> getAllSubDomain() {
		return abstractDomainRepository.findAllSubDomain();
	}

	@Override
	public void updateDomain(AbstractDomain domain) throws BusinessException {

		if(domain.getIdentifier()== null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_ID_NOT_FOUND,"This domain has no current identifier.");
		}
		AbstractDomain entity = abstractDomainRepository.findById(domain.getIdentifier()) ; 
		if(entity == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_DO_NOT_ALREADY_EXISTS,"This domain identifier does not exist.");
		}
		
		if(domain.getPolicy() == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_POLICY_NOT_FOUND,"This domain has no domain policy.");
		}
		if(domain.getUserProvider()!=null) {
			if(domain.getUserProvider().getLdapconnexion() == null) {
				throw new BusinessException(BusinessErrorCode.LDAP_CONNECTION_NOT_FOUND,"This domain has no ldap connection.");
			}
			if(domain.getUserProvider().getPattern() == null) {
				throw new BusinessException(BusinessErrorCode.DOMAIN_PATTERN_NOT_FOUND,"This domain has no domain pattern.");
			}
		}
		
		DomainPolicy policy = domainPolicyService.findById(domain.getPolicy().getIdentifier());
		
		if(policy == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_POLICY_NOT_FOUND,"This new domain has a wrong domain policy identifier.");
		}
		entity.updateDomainWith(domain);
		entity.setPolicy(policy);
		
		LdapUserProvider provider = entity.getUserProvider();
		DomainPattern domainPattern = null;
		LDAPConnection ldapConn = null;
		String baseDn = null;
		
		if(domain.getUserProvider()!=null) {
			domainPattern = domain.getUserProvider().getPattern();
			ldapConn = domain.getUserProvider().getLdapconnexion();
			baseDn = domain.getUserProvider().getBaseDn();
		}
		if(baseDn != null && domainPattern != null && ldapConn != null) {
			logger.debug("Update domain with provider");
			if(provider == null) {
				logger.debug("Update domain with provider creation ");
				provider = new LdapUserProvider(baseDn, ldapConn, domainPattern);
				userProviderService.create(provider);
				entity.setUserProvider(provider);
			} else {
				logger.debug("Update domain with provider update ");
				provider.setBaseDn(baseDn);
				provider.setLdapconnexion(ldapConn);
				provider.setPattern(domainPattern);
				userProviderService.update(provider);
			}
			abstractDomainRepository.update(entity);
		} else {
			logger.debug("Update domain without provider");
			if(provider != null) {
				logger.debug("delete old provider.");
				entity.setUserProvider(null);
				abstractDomainRepository.update(entity);
				userProviderService.delete(provider);
			} else {
				abstractDomainRepository.update(entity);
			}
		}
	}
	
	@Override
	public List<User> searchUserWithoutRestriction(AbstractDomain domain, String mail, String firstName, String lastName) throws BusinessException {
		List<User> users = new ArrayList<User>();
		
		if(domain.getUserProvider() != null) {
			try {
				List<User> list = userProviderService.searchUser(domain.getUserProvider(),mail,firstName,lastName);
				// For each user, we set the domain which he came from.
				for (User user : list) {
					user.setDomain(domain);
					user.setRole(user.getDomain().getDefaultRole());
				}
				users.addAll(list);
			} catch (NamingException e) {
				logger.error("Error while searching for a user in domain {}", domain.getIdentifier());
				logger.error(e.toString());
				throw new BusinessException(BusinessErrorCode.DIRECTORY_UNAVAILABLE, "Couldn't connect to the directory.");
			} catch (IOException e) {
				logger.error("Error while searching for a user in domain {}", domain.getIdentifier());
				logger.error(e.toString());
				throw new BusinessException(BusinessErrorCode.DIRECTORY_UNAVAILABLE, "Couldn't connect to the directory.");
			}
		} else {
			logger.debug("UserProvider is null for domain : " + domain.getIdentifier());
		}
		
		return users;
	}
	
	

	private  List<User> searchUserRecursivelyWithoutRestriction(AbstractDomain domain, String mail, String firstName, String lastName) throws BusinessException {
		List<User> users = new ArrayList<User>();
		
		try {
			users.addAll(searchUserWithoutRestriction(domain, mail, firstName, lastName));
		} catch (BusinessException e) {
			logger.error(e.getMessage());
		}

		for (AbstractDomain subDomain : domain.getSubdomain()) {
			users.addAll(searchUserRecursivelyWithoutRestriction(subDomain, mail, firstName, lastName));
		}
		
		return users;
	}
	
	private  List<User> searchUserWithDomainPolicies(AbstractDomain domain, String mail, String firstName, String lastName) throws BusinessException {
		List<User> users = new ArrayList<User>();

		List<AbstractDomain> allAuthorizedDomain = domainPolicyService.getAllAuthorizedDomain(domain);
		for (AbstractDomain d : allAuthorizedDomain) {
			
			if(d.getUserProvider() != null) {
				try {
					List<User> list = userProviderService.searchUser(d.getUserProvider(),mail,firstName,lastName);
					// For each user, we set the domain which he came from.
					for (User user : list) {
						user.setDomain(d);
						user.setRole(d.getDefaultRole());
					}
					users.addAll(list);
				} catch (NamingException e) {
					logger.error("Error while searching for a user in domain {}", d.getIdentifier());
					logger.error(e.toString());
				} catch (IOException e) {
					logger.error("Error while searching for a user in domain {}", d.getIdentifier());
					logger.error(e.toString());
				}
			} else {
				logger.debug("UserProvider is null for domain : " + domain.getIdentifier());
			}
		}

		return users;
	}

	@Override
	public List<User> searchUserRecursivelyWithoutRestriction(String mail, String firstName, String lastName) throws BusinessException {
		List<User> users = new ArrayList<User>();
		
		users.addAll(searchUserRecursivelyWithoutRestriction(getUniqueRootDomain(), mail, firstName, lastName));
		
		return users;
	}
	
	@Override
	public User searchOneUserRecursivelyWithoutRestriction(String domainIdentifier, String mail) throws BusinessException {
		
		// search domain
		AbstractDomain domain = retrieveDomain(domainIdentifier);
		if(domain == null) {
			logger.error("Impossible to find an user (ldap entry) from domain : " + domainIdentifier + ". This domain does not exist.");
			return null;
		}

		// search user mail in in specific directory and all its SubDomain
		List<User> users = searchUserRecursivelyWithoutRestriction(domain, mail, "", "");
		
		if (users != null) {
			if(users.size() == 1) {
				User userFound = users.get(0);
				logger.debug("User '" + mail + "'found in domain : " + userFound.getDomainId());
				return userFound;
			} else if(users.size() > 1) {
				logger.error("Impossible to find an user entity from domain : " + domainIdentifier + ". Multiple results with mail : " + mail);
			} else if(logger.isDebugEnabled()) {
				logger.error("Impossible to find an user entity from domain : " + domainIdentifier + ". No result with mail : " + mail);
			}
		} else if(logger.isDebugEnabled()) {
			logger.error("Impossible to find an user entity from domain : " + domainIdentifier + ". The searchUserRecursivelyWithoutRestriction method returns null.");
		}
		return null;
	}

	@Override
	public List<User> searchUserRecursivelyWithoutRestriction(String domainIdentifier, String mail, String firstName, String lastName) throws BusinessException {
		logger.debug("Begin searchUserRecursivelyWithoutRestriction");
		AbstractDomain domain = retrieveDomain(domainIdentifier);
		if(domain == null) {
			logger.error("Impossible to find domain : " + domainIdentifier + ". This domain does not exist.");
			return null;
		}
		
		List<User> users = new ArrayList<User>();
		users.addAll(searchUserRecursivelyWithoutRestriction(domain, mail, firstName, lastName));
		logger.debug("End searchUserRecursivelyWithoutRestriction");
		return users;
	}
	
	@Override
	public List<User> searchUserWithDomainPolicies(String domainIdentifier, String mail, String firstName, String lastName) throws BusinessException {
		logger.debug("Begin searchUserRecursivelyWithDomainPolicies");
		AbstractDomain domain = retrieveDomain(domainIdentifier);
		if(domain == null) {
			logger.error("Impossible to find domain : " + domainIdentifier + ". This domain does not exist.");
			return null;
		}
		
		List<User> users = new ArrayList<User>();
		users.addAll(searchUserWithDomainPolicies(domain, mail, firstName, lastName));
		logger.debug("End searchUserRecursivelyWithDomainPolicies");
		return users;
	}

	@Override
	public List<AbstractDomain> getAllAuthorizedDomains(String domainIdentifier) {
		logger.debug("Begin getAllAuthorizedDomains");
		AbstractDomain domain = retrieveDomain(domainIdentifier);
		if(domain == null) {
			logger.error("Impossible to find domain : " + domainIdentifier + ". This domain does not exist.");
			return null;
		}
		List<AbstractDomain> domains = domainPolicyService.getAllAuthorizedDomain(domain);
		logger.debug("End getAllAuthorizedDomains");
		return domains;
	}

	@Override
	public User auth(AbstractDomain domain, String login, String password)	throws BusinessException, NamingException, IOException {
		User user = null;
		try {
			user = userProviderService.auth(domain.getUserProvider(), login, password);
		} catch (NamingException e) {
			logger.error("Error while authentificating a user in domain {}", domain.getIdentifier());
			logger.error(e.toString());
		} catch (IOException e) {
			logger.error("Error while authentificating a user in domain {}", domain.getIdentifier());
			logger.error(e.toString());
		}
		if (user != null) {
			user.setDomain(domain);
		}
		return user;
	}
	
	@Override
	public boolean hasRightsToShareWithExternals(User sender) throws BusinessException {
		
		AbstractDomain domain = sender.getDomain();
    	if (domain != null) {
    		Functionality func = functionalityService.getAnonymousUrlFunctionality(domain);
    		return func.getActivationPolicy().getStatus();
    	}
    	return false;
	}
	
	@Override
	public boolean userCanCreateGuest(User user) {
		
		if (user.getAccountType()==UserType.GUEST) {
			return false;
		}

    	AbstractDomain domain = user.getDomain();
    	if (domain != null) {
    		Functionality func = functionalityService.getGuestFunctionality(domain);
    		if(func.getActivationPolicy().getStatus()) {
    			GuestDomain g = findGuestDomain(domain);
    			// if we found an existing GuestDomain, it means we can create guests.
    			if(g != null) {
    				return true;
    			} else {
    				logger.debug("Guest functionality is enable, but no guest domain found for domain : " + domain.getIdentifier());
    			}
    		} else {
    			logger.debug("Guest functionality is disable.");
    		}
    	} else {
    		logger.debug("User (actor) " + user.getMail() +" without domain.");
    	}
    	return false;
	}
	
	@Override
	public boolean canCreateGuestDomain(AbstractDomain domain) {

    	if (domain != null) {
    			
    			// search GuestDomain among subdomains
    			if(domain.getSubdomain() != null) {
    				for (AbstractDomain d : domain.getSubdomain() ) {
    					if(d.getDomainType().equals(DomainType.GUESTDOMAIN)) {
    						logger.debug("Guest domain already exist.");
    						return false;
    					}
    				}
    			}
    			return true;
    	}
    	return false;
	}
	
	private GuestDomain findGuestDomain(AbstractDomain domain) {

		// search GuestDomain among subdomains
		if(domain.getSubdomain() != null) {
			for (AbstractDomain d : domain.getSubdomain() ) {
				if(d.getDomainType().equals(DomainType.GUESTDOMAIN)) {
					return (GuestDomain)d;
				}
			}
		}
		
		//search among siblings
		if(domain.getParentDomain() != null) {
			return findGuestDomain(domain.getParentDomain());
		}
		
		return null;
	}

	@Override
	public GuestDomain getGuestDomain(String topDomainIdentifier) {
		AbstractDomain top;
		top = retrieveDomain(topDomainIdentifier);
		if(top == null) {
			logger.debug("No TopDomain found.");
			return null;
		}
		return findGuestDomain(top);
	}
}
