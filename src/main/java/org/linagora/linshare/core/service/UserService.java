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

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalException;

/** Services for User management.
 */
public interface UserService {

	/**
	 * Search a user on ldap and database for completion ONLY.
	 * Search a user using pattern as mail, or the concatenation of first name fragment and last name fragment. 
	 * The method is using space to split pattern in order to build first name and last name 
	 * @throws BusinessException
	 */
	public List<User> autoCompleteUser(Account actor, String pattern) throws BusinessException;

	/** Search a user.
	 * @param mail user email.
	 * @param firstName user first name.
	 * @param lastName user last name.
	 * @param userType type of user (internal/guest)
	 * @param currentUser the current user can be null, if not null and if user is a guest, the result includes the guests
	 * created by the current User
	 * @return a list of matching users.
	 */
	List<User> searchUser(String mail, String firstName, String lastName,AccountType userType,User currentUser) throws BusinessException;

	User deleteUser(Account actor, String uuid) throws BusinessException;

	/**
	 * Delete all users from domain (and all the related data )
	 */
	void deleteAllUsersFromDomain(User actor, String domainIdentifier) throws BusinessException;

	/**
	 * Update a user locale
	 * @param mail : the user email
	 * @param locale : the new local
	 * @throws BusinessException 
	 */
	public void updateUserLocale(String domainId, String mail, SupportedLanguage locale) throws BusinessException;

	/**
	 * Update a user externalMailLocale
	 * @param mail : the user email
	 * @param externalMailLocale : the new externalMailLocal
	 * @throws BusinessException
	 */
	public void updateUserExternalMailLocale(String domainId, String mail, Language externalMailLocale) throws BusinessException;

	/**
	 * Update a user locale
	 * @param mail : the user email
	 * @param locale : the new local
	 * @throws BusinessException 
	 */
	public void updateUserLocale(String domainId, String mail, SupportedLanguage locale,Language externalMailLocale, String cmisLocale) throws BusinessException;
	/**
	 * change a guest or superadmin password
	 * @throws BusinessException : AUTHENTICATION_ERROR if the password supplied is wrong
	 */
	public void changePassword(String uuid, String mail, String oldPassword, String newPassword) throws BusinessException;

	/**
	 * Search user that are internal and in the DB but not in domains (=removed from ldap).
	 * @return
	 */
	List<User> searchAllBreakedUsers(User actor);
	
	/**
	 * This method create a new user entity from a valid user object, or update an existing one. 
	 * @return user entity created or updated.
	 */
	public User saveOrUpdateUser(User user) throws TechnicalException ;
	
	/** Find a  user (based on mail address).
	 * Search first in database, then on ldap if not found.
	 * If the user isn't found on DB, then it is created from the ldap info.
	 * If the user isn't found in the ldap, an exception is raised.
	 * @param mail user mail.
	 * @param domainId domain identifier.
	 * @return founded user.
	 * @throws BusinessException if the user could not be found
	 */
	public User findOrCreateUser(String mail, String domainId) throws BusinessException ;


	 /** Find a  user (based on mail address).
	 * Search first in database, then on ldap if not found, but only in authorized domains. The starting point of the research is domainId.
	 * If the user isn't found on DB, then it is created from the ldap info. 
	 * @param mail user mail.
	 * @param domainId domain identifier.
	 * @return founded user.
	 * @throws BusinessException if the user could not be found
	 */
	public User findOrCreateUserWithDomainPolicies(String mail, String domainId) throws BusinessException ;


	/** Find a  user (based on mail address).
	 * Search first in database, then on ldap if not found, but only in authorized domains. The starting point of the research is domainId.
	 * If the user isn't found on DB, then it is created from the ldap info
	 * @param domainId domain identifier.
	 * @param mail user mail.
	 * @param actorDomainId domain identifier, it is useful to determine which domains we are authorized to search in. 
	 * if this parameter is null, domainId is used as starting point for the research.
	 * @return founded user.
	 * @throws BusinessException if the user could not be found
	 */
	public User findOrCreateUserWithDomainPolicies(String domainId, String mail, String actorDomainId) throws BusinessException ;

	public User findByLsUuid(String lsUuid);

	boolean exist(String lsUuid);

	public User findUserInDB(String domain, String mail);

	public List<User> findUsersInDB(String domain);

	/**
	 * Check if the actor is authorized to manage the second user (userToManage).
	 */
	public boolean isAdminForThisUser(Account actor, User user);


	/**
	 * 
	 * New implementation created to not use old tapestry version.
	 * Destined to be more clear and to replace the old implementation when tapestry will be destroy.
	 * 
	 */

	/**
	 * Find the user matching updateUser and
	 * update fields: first name, last name, role, create guest right and upload right
	 * @throws BusinessException
	 */
	User updateUser(Account actor, User updatedUser, String domainId) throws BusinessException;

	/**
	 * Find destroyed accounts
	 * @throws BusinessException
	 */
	List<String> findAllAccountsReadyToPurge() throws BusinessException;

	List<String> findAllDeletedAccountsToPurge(Date limit) throws BusinessException;

	void purge(Account actor, String lsUuid) throws BusinessException;

	void markToPurge(Account actor, String lsUuid) throws BusinessException;

	User findAccountsReadyToPurge(SystemAccount actor, String uuid);

	User findDeleted(SystemAccount actor, String uuid);

	boolean updateUserEmail(Account actor, String currentEmail, String newEmail);

	void updateMailingListEmail(Account actor, String currentEmail, String newEmail);

	void updateRecipientFavourite(Account actor, String currentEmail, String newEmail);
}
