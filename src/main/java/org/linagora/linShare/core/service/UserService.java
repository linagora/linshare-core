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
package org.linagora.linShare.core.service;

import java.util.List;

import org.linagora.linShare.core.domain.entities.Guest;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.entities.Role;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.entities.UserType;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;

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
     * @param mailContainer informations needed to construct the email
     * @param ownerLogin login of the user who create the guest.
     * @return persisted guest.
     * @throws BusinessException in case of duplicated guest.
     */
    Guest createGuest(String login, String firstName, String lastName, String mail, Boolean canUpload, Boolean canCreateGuest, String comment, 
    		MailContainer mailContainer, String ownerLogin, String ownerDomain) throws BusinessException;

    /**
     * generate the password of a guest (system generated)
     * or password for a secure URL
     * @return password in plain text
     */
    public String generatePassword();
    
    
    /** Search a user.
     * @param mail user email.
     * @param firstName user first name.
     * @param lastName user last name.
     * @param userType type of user (internal/guest)
     * @param currentUser the current user can be null, if not null and if user is a guest, the result includes the guests
     * created by the current User
     * @return a list of matching users.
     */
    List<User> searchUser(String mail, String firstName, String lastName,UserType userType,User currentUser) throws BusinessException;
    
    
    /** Find a user (based on mail address).
     * Search first in database, then on ldap if not found.
     * @param login user login.
     * @return founded user.
     */
    public User findUserInDB(String mail);
    public List<User> findUsersInDB(String domain);
    public User findUser(String mail, String domain) throws BusinessException;
    public User findUser(String mail, String domain, User actor) throws BusinessException;
    
    /** Find a  user (based on mail address).
     * Search first in database, then on ldap if not found.
     * If the user isn't found on DB, then it is created from the ldap info
     * @param mail user mail.
     * @return founded user.
     * @throws BusinessException if the user could not be found
     * @throws TechnicalError if the user cannot be created
     */
    public User findAndCreateUser(String mail, String domainId) throws BusinessException ;
    
    /**
     * Delete a User (and all the corresponding share )
     * @param login
     * @param owner : the intended owner
     * @param checkOwnership : if true, check that the owner is the creator of the user
     * 			useful for the batch
     * @throws BusinessException 
     */
    void deleteUser(String login, User owner, boolean checkOwnership) throws BusinessException;

    /** Clean outdated guest accounts. */
    void cleanExpiredGuestAcccounts();

	/**
	 * update a guest (edit mode)
	 * @param mail
	 * @param firstName
	 * @param lastName
	 * @param canUpload
	 * @param owner
	 * @throws BusinessException
	 */
    public void updateGuest(String mail, String firstName, String lastName, Boolean canUpload, Boolean canCreateGuest, UserVo owner) throws BusinessException;
    
	/**
	 * update the role of a user (admin, simple)
	 * @param mail
	 * @param role
	 * @param owner
	 * @throws BusinessException
	 */
	public void updateUser(String mail,Role role, UserVo owner) throws BusinessException;
    
	/**
	 * Update a user locale
	 * @param mail : the user email
	 * @param locale : the new local
	 */
	public void updateUserLocale(String mail, String locale);
	
	public void updateUserEnciphermentKey(String mail, byte[] challenge);
	
	/**
	 * change a guest or superadmin password
	 * @param login
	 * @param oldPassword
	 * @param newPassword
	 * @throws BusinessException : AUTHENTICATION_ERROR if the password supplied is wrong
	 */
	public void changePassword(String login, String oldPassword, String newPassword) throws BusinessException;
	
	/**
	 * Reset a guest password
	 * @param login
     * @param mailContainer informations needed to construct the email
	 * @throws BusinessException
	 */
	public void resetPassword(String login, MailContainer mailContainer) throws BusinessException;
	
	/**
	 * Update a guest as restricted and set his list of contacts
	 * 
	 * @param login of the guest
	 * @param mailContacts
	 */
	public void setGuestContactRestriction(String login, List<String> mailContacts) throws BusinessException;
	
	/**
	 * Set a guest as not restricted and remove his list of contacts
	 * 
	 * @param login
	 */
	public void removeGuestContactRestriction(String login) throws BusinessException;
	
	/**
	 * Add one contact to a restricted guest
	 * 
	 * @param ownerLogin
	 * @param contactLogin
	 */
	public void addGuestContactRestriction(String ownerLogin, String contactLogin) throws BusinessException;
	
	/**
	 * Retrieve the list of contacts of the guest
	 * 
	 * @param login
	 * @return
	 */
	public List<User> fetchGuestContacts(String login) throws BusinessException;
}
