/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.service;

import java.util.List;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.GuestDomain;
import org.linagora.linshare.core.domain.entities.SubDomain;
import org.linagora.linshare.core.domain.entities.SystemAccount;
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
	 * @return List<User>
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
	 * @return List<AbstractDomain>
	 */
	public List<AbstractDomain> findAll(Account actor);
	/**
	 * This method returns all the domains using the welcome message parameter as currentwelcomeMessage
	 * @param actor
	 * @param uuid
	 * @return List<AbstractDomain>
	 * @throws BusinessException
	 */
	List<AbstractDomain> loadRelativeDomains(User actor, String uuid) throws BusinessException;

	List<User> autoCompleteUserWithoutDomainPolicies(Account actor, String pattern) throws BusinessException;
	
	AbstractDomain markToPurge(Account actor, String domainId);
	
	List<String> findAllDomainsReadyToPurge() throws BusinessException;

	AbstractDomain findDomainReadyToPurge(SystemAccount actor, String uuid);

	void purge(Account actor, String lsUuid) throws BusinessException;
	
	List<AbstractDomain> getSubDomainsByDomain(String uuid) throws BusinessException;
}
