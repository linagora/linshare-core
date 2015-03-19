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
package org.linagora.linshare.core.facade;

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.domain.vo.AbstractDomainVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;


/**
 *  Facade entry for user management.
 */
public interface UserFacade {

    /** Create a guest.
     * @param mail user email (natural key).
     * @param firstName first name.
     * @param lastName last name.
     * @param canUpload if the user can upoad.
     * @param comment : the comment about the user
     * @param owner user who create the guest.
     * @param restricted TODO
     * @param restrictedMails TODO
     * @throws BusinessException if user already exist.
     */
	UserVo createGuest(String mail, String firstName, String lastName,
			Boolean canUpload, Boolean canCreateGuest, String comment,
			UserVo owner, boolean restricted, List<String> restrictedMails,
			Date expirationDate) throws BusinessException;

    /**
     * update a guest (edit)
     * @param guestUuid 
     * @param mail
     * @param firstName
     * @param lastName
     * @param canUpload
     * @param owner
     * @throws BusinessException
     */
	public void updateGuest(String guestUuid, String domain, String mail,
			String firstName, String lastName, Boolean canUpload, UserVo owner,
			boolean restricted, List<String> restrictedMails,
			Date expirationDate) throws BusinessException;

    /**
     * update an user (only the role)
     * @param userUuid 
     * @param mail
     * @param role
     * @param owner
     * @throws BusinessException
     */
    public void updateUserRole(String userUuid, String domain, String mail, Role role, UserVo owner) throws BusinessException;


    /** Search a user.
     * @param mail user email.
     * @param firstName user first name.
     * @param lastName user last name.
     * @return a list of matching users.
     */
    List<UserVo> searchUser(String mail, String firstName, String lastName, UserVo currentUser) throws BusinessException;


    /** Search a user.
     * @param mail user email.
     * @param firstName user first name.
     * @param lastName user last name.
     * @param userType the type of the user.
     * @return a list of matching users.
     */
    List<UserVo> searchUser(String mail, String firstName, String lastName,AccountType userType, UserVo currentUser) throws BusinessException;

    /** Get all guests created by a user.
     * @param actorVo owner mail.
     * @return the list of guests created by their owner.
     */
    List<UserVo> searchGuest(UserVo actorVo);

    /** Delete a guest, purge an internal user.
     * @param login login of the user to delete.
     * @param owner : the actor that has to be the guest creator or an admin of the application
     */
    void deleteUser(String login, UserVo actor);


    List<String> findMails(String beginWith);

	/**
	 * Update a user locale
	 * @param user
	 * @param locale
	 * @throws BusinessException
	 */
	public void updateUserLocale(UserVo user, SupportedLanguage locale) throws BusinessException;

	/**
	 * Update a user externalMailLocale
	 * @param user
	 * @param externalMailLocale
	 * @throws BusinessException
	 */
	public void updateUserExternalMailLocale(UserVo user, Language externalMailLocale) throws BusinessException;

    /** Load a User.
     * If the user doesn't exist in database, search informations in LDAP and create a user entry before returning it.
     * @param login user login.
     * @return user details or null if user is neither in database or LDAP.
     */
    UserVo loadUserDetails(String login, String domainId);

    /** Get user password.
     * @param login user login.
     * @return password or null if empty or null.
     */
    String getPassword(String login);

    /**
     * Change a user password
     * @param user
     * @param oldPassword
     * @param newPassword
     * @throws BusinessException  AUTHENTICATION_ERROR if the password supplied is wrong
     */
    void changePassword(UserVo user, String oldPassword, String newPassword) throws BusinessException;

    /**
     * Set a new password to a guest 
     * @param guest
     * @param actor
     */
    void resetPassword(UserVo guest) throws BusinessException;

	/**
	 * Retrieve the list of contacts of the guest
	 * @param actorVo 
	 * 
	 * @param login
	 * @return
	 */
	List<UserVo> fetchGuestContacts(UserVo actorVo, String login) throws BusinessException;

	void updateUserDomain(String mail, AbstractDomainVo selectedDomain,
			UserVo userLoggedIn) throws BusinessException;

	/**
	 * Search user that are internal and in the DB but not in domains (=removed from ldap).
	 * 
	 * @param userLoggedIn
	 * @return
	 */
	List<UserVo> searchAllBreakedUsers(UserVo userLoggedIn);


	/**
	 * Search a guest with his mail and the user logged in if he's his owner.
	 * 
	 * @param userLoggedIn
	 * @param mail
	 * @return User entity
	 */
	UserVo findGuestWithMailAndUserLoggedIn(UserVo userLoggedIn, String mail);

	UserVo findGuestByLsUuid(UserVo actorVo, String guestUuid);

	UserVo findUserByLsUuid(UserVo actorVo, String uuid);



	/**
	 * This method is design to find an user in the database from its mail and domain. 
	 * The communication rules are used to determine all authorized domains you are allowed to search in, the domainId parameter is the starting point.
	 * Usually, we use the domainId from the current logged in user as domainId parameter.
	 * @param domainId
	 * @param mail
	 * @return User entity
	 */
	UserVo findUserFromAuthorizedDomainOnly(String domainId, String mail);

	/**
	 * This method search a particular guest in the database. The domainId parameter is optional, if it is null, the guest is search in all existing domain except root domain.
	 * @param mail
	 * @param optionalDomainId
	 * @return User entity
	 */
	UserVo findUserForResetPassordForm(String mail, String optionalDomainId);


	 /** Search a user using its mail.
     * @param mail user mail.
     * @return founded user.
     */
    UserVo findUserInDb(String mail, String domain);
    UserVo findUser(String domain, String mail) throws BusinessException;


    /**
	 * Check if the actorVo is authorized to manage the second user (actorVo).
	 * @param actorVo
	 * @param userToManageVo
	 * @return
	 */
	public boolean isAdminForThisUser(UserVo actorVo, UserVo userToManageVo) throws BusinessException;

	Date getGuestCreationExpirationDate(UserVo actorVo) throws BusinessException;
	Date getGuestUpdateExpirationDate(UserVo actorVo, String currentUserUuid) throws BusinessException;

}