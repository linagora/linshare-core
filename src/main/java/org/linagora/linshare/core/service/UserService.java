/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linshare.core.service;

import java.util.List;

import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Role;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalException;

/** Services for User management.
 */
public interface UserService {

    /** Create a guest.
     * @param login login.
     * @param firstName first name.
     * @param lastName last name.
     * @param mail guest email address.
     * @param canUpload : if the user can upload file
     * @param comment : the comment about the user
     * @param ownerLogin login of the user who create the guest.
     * @return persisted guest.
     * @throws BusinessException in case of duplicated guest.
     */
    Guest createGuest(String login, String firstName, String lastName, String mail, Boolean canUpload, Boolean canCreateGuest, String comment, 
    		String ownerLogin, String ownerDomain) throws BusinessException;

    
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
    
    /**
     * Search available user for editing restricted email list owned by guest.
     * @param mail
     * @param currentGuest
     * @return
     * @throws BusinessException
     */
    public List<User> searchUserForRestrictedGuestEditionForm(String mail, String firstName, String lastName, User currentGuest) throws BusinessException;
    	
    /**
     * Delete a User (and all the corresponding share )
     * @param login
     * @param actor 
     * @param checkOwnership : if true, check that the owner is the creator of the user
     * 			useful for the batch
     * @throws BusinessException 
     */
    void deleteUser(String login, Account actor) throws BusinessException;
    
    /**
     * Delete all users from domain (and all the related data )
     * @param actor
     * @param domainIdentifier
     * @throws BusinessException
     */
    void deleteAllUsersFromDomain(User actor, String domainIdentifier) throws BusinessException;


    /** Clean outdated guest accounts. */
    void cleanExpiredGuestAcccounts(SystemAccount systemAccount);

	/**
	 * update a guest (edit mode)
	 * @param guestUuid 
	 * @param mail
	 * @param firstName
	 * @param lastName
	 * @param canUpload
	 * @param owner
	 * @throws BusinessException
	 */
    public void updateGuest(String guestUuid, String domain, String mail, String firstName, String lastName, Boolean canUpload, Boolean canCreateGuest, UserVo owner) throws BusinessException;
    
	/**
	 * update the role of a user (admin, simple)
	 * @param userUuid 
	 * @param mail
	 * @param role
	 * @param owner
	 * @throws BusinessException
	 */
	public void updateUserRole(String userUuid, String domain,String mail, Role role, UserVo owner) throws BusinessException;
    
	/**
	 * Update a user locale
	 * @param mail : the user email
	 * @param locale : the new local
	 */
	public void updateUserLocale(String uuid, String mail, String locale);
	
	/**
	 * change a guest or superadmin password
	 * @param mail
	 * @param oldPassword
	 * @param newPassword
	 * @param login
	 * @throws BusinessException : AUTHENTICATION_ERROR if the password supplied is wrong
	 */
	public void changePassword(String uuid, String mail, String oldPassword, String newPassword) throws BusinessException;
	
	/**
	 * Reset a guest password
	 * @param mail
	 * @param login
	 * @throws BusinessException
	 */
	public void resetPassword(String uuid, String mail) throws BusinessException;
	
	/**
	 * Update a guest as restricted and set his list of contacts
	 * 
	 * @param login of the guest
	 * @param mailContacts
	 */
	public void setGuestContactRestriction(String uuid, List<String> mailContacts) throws BusinessException;
	
	/**
	 * Set a guest as not restricted and remove his list of contacts
	 * 
	 * @param login
	 */
	public void removeGuestContactRestriction(String uuid) throws BusinessException;
	
	/**
	 * Add one contact to a restricted guest
	 * 
	 * @param ownerUuid
	 * @param contactUuid
	 */
	public void addGuestContactRestriction(String ownerUuid, String contactUuid) throws BusinessException;
	
	/**
	 * Retrieve the list of contacts of the guest
	 * 
	 * @param login
	 * @return
	 */
	public List<User> fetchGuestContacts(String uuid) throws BusinessException;
	
	public List<String> getGuestEmailContacts(String uuid) throws BusinessException;

	void updateUserDomain(String mail, String selectedDomain, UserVo actor) throws BusinessException;

	/**
	 * Search user that are internal and in the DB but not in domains (=removed from ldap).
	 * 
	 * @param userLoggedIn
	 * @return
	 */
	List<User> searchAllBreakedUsers(User actor);
	
	/**
	 * This method create a new user entity from a valid user object, or update an existing one. 
	 * @param user
	 * @return user entity created or updated.
	 */
	public void saveOrUpdateUser(User user) throws TechnicalException ;
	
	/** Find a  user (based on mail address).
     * Search first in database, then on ldap if not found.
     * If the user isn't found on DB, then it is created from the ldap info.
     * If the user isn't found in the ldap, an exception is raised.
     * @param mail user mail.
     * @param domainId domain identifier.
     * @return founded user.
     * @throws BusinessException if the user could not be found
     * @throws TechnicalError if the user cannot be created
     */
    public User findOrCreateUser(String mail, String domainId) throws BusinessException ;
    
    
	 /** Find a  user (based on mail address).
     * Search first in database, then on ldap if not found, but only in authorized domains. The starting point of the research is domainId.
     * If the user isn't found on DB, then it is created from the ldap info. 
     * @param mail user mail.
     * @param domainId domain identifier.
     * @return founded user.
     * @throws BusinessException if the user could not be found
     * @throws TechnicalError if the user cannot be created
     */
    public User findOrCreateUserWithDomainPolicies(String mail, String domainId) throws BusinessException ;
    
    
    /** Find a  user (based on mail address).
     * Search first in database, then on ldap if not found, but only in authorized domains. The starting point of the research is domainId.
     * If the user isn't found on DB, then it is created from the ldap info
     * @param mail user mail.
     * @param domainId domain identifier.
     * @param ActorDomainId domain identifier, it is useful to determine which domains we are authorized to search in. 
     * if this parameter is null, domainId is used as starting point for the research.
     * @return founded user.
     * @throws BusinessException if the user could not be found
     * @throws TechnicalError if the user cannot be created
     */
    public User findOrCreateUserWithDomainPolicies(String mail, String domainId, String ActorDomainId) throws BusinessException ;
	
    public User findUnkownUserInDB(String mail);
    
    public User findByLsUid(String lsUid);
    
	public User findUserInDB(String domain, String mail);
    public List<User> findUsersInDB(String domain);
    
    /**
	 * This method is designed to search in all existing domains, and create an user entity in the database if it was a successful research(got one only hit)  
	 * @param mail : this parameter should be unique in directories
	 * @return User entity
	 */
	public User searchAndCreateUserEntityFromUnkownDirectory(String mail) throws BusinessException;

	/**
	 * This method is designed to search in a specific domain, and create an user entity in the database if it was a successful research(got one only hit)
	 * This method is used by the authentication process to guess where a user is stored from his login. 
	 * @param mail : the mail and domain couple should be unique
	 * @return User entity
	 */
	public User searchAndCreateUserEntityFromDirectory(String domainIdentifier, String mail) throws BusinessException;
	
	/**
	 * Check if the actor is authorized to manage the second user (userToManage).
	 * @param actor
	 * @param userDomainToManage
	 * @param userMailToManage 
	 * @return
	 */
	public boolean isAdminForThisUser(Account actor, String userDomainToManage, String userMailToManage);
}
