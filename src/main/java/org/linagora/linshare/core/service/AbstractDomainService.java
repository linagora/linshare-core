/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.service;

import java.util.List;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.GuestDomain;
import org.linagora.linshare.core.domain.entities.SubDomain;
import org.linagora.linshare.core.domain.entities.TopDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface AbstractDomainService {

	public TopDomain createTopDomain(Account actor, TopDomain topDomain) throws BusinessException;
	public SubDomain createSubDomain(Account actor, SubDomain subDomain) throws BusinessException;
	public GuestDomain createGuestDomain(Account actor, GuestDomain guestDomain) throws BusinessException;

	public AbstractDomain retrieveDomain(String identifier);
	AbstractDomain findById(String identifier) throws BusinessException;

	public AbstractDomain updateDomain(Account actor, AbstractDomain domain) throws BusinessException;
	public void deleteDomain(Account actor, String identifier) throws BusinessException;
	public List<String> getAllDomainIdentifiers();
	public List<String> getAllMyDomainIdentifiers(String personalDomainIdentifer);
	List<String> getAllSubDomainIdentifiers(String domain);


	/**
	 * This method returns all domain except the root domain.
	 * @return AbstractDomain list
	 * @throws BusinessException
	 */
	public List<AbstractDomain> getAllDomains();
	public List<AbstractDomain> getAllTopAndSubDomain();
	public List<AbstractDomain> getAllTopDomain();
	public List<AbstractDomain> getAllSubDomain();

	AbstractDomain findGuestDomain(String uuid);

	public AbstractDomain getUniqueRootDomain()throws BusinessException;

	/**
	 * This method returns a list containing all the authorized domains for the input domain.
	 * This used to filter communications between domains.
	 * @param domainIdentifier
	 * @return List of domains.
	 * @throws BusinessException 
	 */
	public List<AbstractDomain> getAllAuthorizedDomains(String domainIdentifier) throws BusinessException;

	/**
	 * This method returns a list containing all the authorized domains for the input domain.
	 * This used to filter communications between domains.
	 * @param domain : user's domain
	 * @return List of domains.
	 * @throws BusinessException
	 */
	public List<AbstractDomain> getAllAuthorizedDomains(AbstractDomain domain) throws BusinessException;

	/**
	 * This method is designed to search in a particular domain and its SubDomain.
	 * @param domainIdentifier
	 * @param mail
	 * @return An user object List (Ldap entry) containing directory informations. (mail, first name and last name). It is not an entity !
	 * @throws BusinessException
	 */
	public List<User> searchUserWithDomainPolicies(String domainIdentifier, String mail, String firstName, String lastName) throws BusinessException;

	/**
	 * This method is designed to search a user in all authorized Domain and SubDomain. Use ONLY for completion
	 * @param domainIdentifier
	 * @param pattern : first name, last name or mail fragment
	 * @return An user object List (Ldap entry) containing directory informations. (mail, first name and last name). It is not an entity !
	 * @throws BusinessException
	 */
	public List<User> autoCompleteUserWithDomainPolicies(String domainIdentifier, String pattern) throws BusinessException;
	/**
	 * This method is designed to search a user in all authorized Domain and SubDomain. Use ONLY for completion
	 * @param domainIdentifier
	 * @param firstName
	 * @param lastName
	 * @return
	 * @throws BusinessException
	 */
	public List<User> autoCompleteUserWithDomainPolicies(String domainIdentifier, String firstName, String lastName) throws BusinessException;
	/**
	 * This method is designed to search in all existing domains.
	 * @param mail
	 * @return An user object (Ldap entry) containing directory informations. (mail, first name and last name). It is not an entity !
	 * @throws BusinessException
	 */
	public List<User> searchUserRecursivelyWithoutRestriction(String mail) throws BusinessException;

	/**
	 * This method is designed to search in a particular domain and its SubDomain.
	 * @param domainIdentifier
	 * @param mail
	 * @return An user object List (Ldap entry) containing directory informations. (mail, first name and last name). It is not an entity !
	 * @throws BusinessException
	 */
	public List<User> searchUserRecursivelyWithoutRestriction(String domainIdentifier, String mail) throws BusinessException;


	/**
	 * This method is designed to search users in a particular domain.
	 * @param domain
	 * @param mail
	 * @return An user object List (Ldap entry) containing directory informations. (mail, first name, last name, domain and default role). It is not an entity !
	 * @throws BusinessException
	 */
	public User findUserWithoutRestriction(AbstractDomain domain, String mail) throws BusinessException;

	/**
	 * Test if a user exists or not in ldap. This method does not test domain policies.
	 * @param domain
	 * @param mail
	 * @return true if a user exists
	 * @throws BusinessException
	 */
	public Boolean isUserExist(AbstractDomain domain, String mail) throws BusinessException;

	/**
	 * This method is designed to search in a particular domain and its SubDomain.
	 * @param domainIdentifier
	 * @param mail
	 * @return An user object (Ldap entry) containing directory informations. (mail, first name and last name). It is not an entity !
	 * @throws BusinessException
	 */
	public User searchOneUserRecursivelyWithoutRestriction(String domainIdentifier, String mail) throws BusinessException;

	public boolean userCanCreateGuest(User user);
	public boolean canCreateGuestDomain(AbstractDomain domain) ;

	/**
	 * Retrieve the mail address for notifications (smtp sender)
	 * @param domain
	 * @return the mail
	 */
	public String getDomainMail(AbstractDomain domain);

	/**
	 * Retrieve a list of all domains visible by the current actor.
	 * @return
	 */
	public List<AbstractDomain> findAll(Account actor);
	/**
	 * This method returns all the domains using the welcome message parameter as currentwelcomeMessage
	 * @param actor
	 * @param uuid
	 * @return
	 * @throws BusinessException
	 */
	List<AbstractDomain> loadRelativeDomains(User actor, String uuid) throws BusinessException;

	List<User> autoCompleteUserWithoutDomainPolicies(Account actor, String pattern) throws BusinessException;
}
