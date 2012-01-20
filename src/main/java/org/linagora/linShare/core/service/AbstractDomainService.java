package org.linagora.linShare.core.service;

import java.io.IOException;
import java.util.List;

import javax.naming.NamingException;

import org.linagora.linShare.core.domain.entities.AbstractDomain;
import org.linagora.linShare.core.domain.entities.GuestDomain;
import org.linagora.linShare.core.domain.entities.LdapUserProvider;
import org.linagora.linShare.core.domain.entities.RootDomain;
import org.linagora.linShare.core.domain.entities.SubDomain;
import org.linagora.linShare.core.domain.entities.TopDomain;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.exception.BusinessException;

public interface AbstractDomainService {
	
	public TopDomain createTopDomain(TopDomain topDomain) throws BusinessException;
	public SubDomain createSubDomain(SubDomain subDomain) throws BusinessException;
	public GuestDomain createGuestDomain(GuestDomain guestDomain) throws BusinessException;
	
	public AbstractDomain retrieveDomain(String identifier) throws BusinessException;
	public void updateDomain(AbstractDomain domain) throws BusinessException;
	public void deleteDomain(String identifier) throws BusinessException;
	public List<String> getAllDomainIdentifiers();
	public List<String> getAllMyDomainIdentifiers(String personalDomainIdentifer);
	
	
	/**
	 * This method returns all domain except the root domain.
	 * @return AbstractDomain list
	 * @throws BusinessException
	 */
	public List<AbstractDomain> getAllDomains();
	public List<AbstractDomain> getAllTopDomain();
	public List<AbstractDomain> getAllSubDomain();
	public GuestDomain getGuestDomain(String topDomainIdentifier);
	
	
	
	public RootDomain getUniqueRootDomain()throws BusinessException;
	
	/**
	 * This method returns a list containing all the authorized domains for the input domain.
	 * This used to filter communications between domains.
	 * @param domain identifier
	 * @return List of domains.
	 */
	public List<AbstractDomain> getAllAuthorizedDomains(String domainIdentifier);
	/**
	 * This method is designed to search in a particular domain and its SubDomain.
	 * @param domainIdentifier
	 * @param mail
	 * @return An user object List (Ldap entry) containing directory informations. (mail, first name and last name). It is not an entity !
	 * @throws BusinessException
	 */
	public List<User> searchUserWithDomainPolicies(String domainIdentifier, String mail, String firstName, String lastName) throws BusinessException;
	/**
	 * This method is designed to search in all existing domains.
	 * @param mail
	 * @param firstName
	 * @param lastName
	 * @return An user object (Ldap entry) containing directory informations. (mail, first name and last name). It is not an entity !
	 * @throws BusinessException
	 */
	public List<User> searchUserRecursivelyWithoutRestriction(String mail, String firstName, String lastName) throws BusinessException;
	
	/**
	 * This method is designed to search in a particular domain and its SubDomain.
	 * @param domainIdentifier
	 * @param mail
	 * @return An user object List (Ldap entry) containing directory informations. (mail, first name and last name). It is not an entity !
	 * @throws BusinessException
	 */
	public List<User> searchUserRecursivelyWithoutRestriction(String domainIdentifier, String mail, String firstName, String lastName) throws BusinessException;
	
	
	/**
	 * This method is designed to search users in a particular domain.
	 * @param domainIdentifier
	 * @param mail
	 * @return An user object List (Ldap entry) containing directory informations. (mail, first name, last name, domain and default role). It is not an entity !
	 * @throws BusinessException
	 */
	public List<User> searchUserWithoutRestriction(AbstractDomain domain, String mail, String firstName, String lastName) throws BusinessException;
	
	
	
	/**
	 * This method is designed to search in a particular domain and its SubDomain.
	 * @param domainIdentifier
	 * @param mail
	 * @return An user object (Ldap entry) containing directory informations. (mail, first name and last name). It is not an entity !
	 * @throws BusinessException
	 */
	public User searchOneUserRecursivelyWithoutRestriction(String domainIdentifier, String mail) throws BusinessException;
	
	public User auth(AbstractDomain domain,	String login, String password) throws BusinessException, NamingException, IOException;
	
	public boolean userCanCreateGuest(User user);
	public boolean canCreateGuestDomain(AbstractDomain domain) ;
	public boolean hasRightsToShareWithExternals(User sender) throws BusinessException;
}
