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

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.RecipientFavourite;
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
	List<User> autoCompleteUser(Account actor, String pattern) throws BusinessException;

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
	 * Update a user locale
	 * @param mail : the user email
	 * @param locale : the new local
	 * @throws BusinessException 
	 */
	void updateUserLocale(String domainId, String mail, SupportedLanguage locale) throws BusinessException;

	/**
	 * Update a user mailLocale
	 * @param mail : the user email
	 * @param mailLocale : the new mailLocale
	 * @throws BusinessException
	 */
	void updateUserMailLocale(String domainId, String mail, Language mailLocale) throws BusinessException;

	/**
	 * Update a user locale
	 * @param mail : the user email
	 * @param locale : the new local
	 * @throws BusinessException 
	 */
	void updateUserLocale(String domainId, String mail, SupportedLanguage locale,Language externalMailLocale, String cmisLocale) throws BusinessException;

	/**
	 * Search user that are internal and in the DB but not in domains (=removed from ldap).
	 * @return List<User>
	 */
	List<User> searchAllBreakedUsers(User actor);
	
	/**
	 * This method create a new user entity from a valid user object, or update an existing one. 
	 * @return user entity created or updated.
	 */
	User saveOrUpdateUser(User user) throws TechnicalException ;
	
	/** Find a  user (based on mail address).
	 * Search first in database, then on ldap if not found.
	 * If the user isn't found on DB, then it is created from the ldap info.
	 * If the user isn't found in the ldap, an exception is raised.
	 * @param mail user mail.
	 * @param domainId domain identifier.
	 * @return founded user.
	 * @throws BusinessException if the user could not be found
	 */
	User findOrCreateUser(String mail, String domainId) throws BusinessException ;


	 /** Find a  user (based on mail address).
	 * Search first in database, then on ldap if not found, but only in authorized domains. The starting point of the research is domainId.
	 * If the user isn't found on DB, then it is created from the ldap info. 
	 * @param mail user mail.
	 * @param domainId domain identifier.
	 * @return founded user.
	 * @throws BusinessException if the user could not be found
	 */
	User findOrCreateUserWithDomainPolicies(String mail, String domainId) throws BusinessException ;


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
	User findOrCreateUserWithDomainPolicies(String domainId, String mail, String actorDomainId) throws BusinessException ;

	User findByLsUuid(String lsUuid);

	boolean exist(String lsUuid);

	User findUserInDB(String domain, String mail);

	List<User> findUsersInDB(String domain);

	/**
	 * Check if the actor is authorized to manage the second user (userToManage).
	 */
	boolean isAdminForThisUser(Account actor, User user);


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

	List<RecipientFavourite> findRecipientFavourite(User owner);

	void updateRecipientFavourite(Account actor, String currentEmail, String newEmail);

	void deleteRecipientFavourite(User owner, String recipient);

	void deleteAllUsersFromDomain(User actor, String domainIdentifier) throws BusinessException;

	void changePassword(User authUser, User actor, String oldPassword, String newPassword) throws BusinessException;

	User updateUserForSuccessfulAuthentication(String uuid) throws BusinessException;

	User updateUserForFailureAuthentication(String uuid) throws BusinessException;

	/**
	 * Method that allows administrator of a domain to unlock users.
	 * @param authUser {@link Account} the authenticated account
	 * @param accountToUnlock user that'll be unlocked
	 * @return {@link User}
	 * @throws BusinessException
	 */
	User unlockUser(Account authUser, User accountToUnlock) throws BusinessException;

	List<AllowedContact> findAllRestrictedContacts(User user);
}
