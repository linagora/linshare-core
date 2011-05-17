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
package org.linagora.linShare.core.Facade.impl;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.entities.Guest;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.entities.Role;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.entities.UserType;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.exception.TechnicalErrorCode;
import org.linagora.linShare.core.exception.TechnicalException;
import org.linagora.linShare.core.repository.GuestRepository;
import org.linagora.linShare.core.repository.UserRepository;
import org.linagora.linShare.core.service.EnciphermentService;
import org.linagora.linShare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Facade entry for user management.
 */
public class UserFacadeImpl implements UserFacade {

    Logger logger = LoggerFactory.getLogger(UserFacadeImpl.class);

    /** User repository. */
    private UserRepository<User> userRepository;

    /** User service. */
    private UserService userService;

    /** Guest repository. */
    private GuestRepository guestRepository;
    
    
    /**info on encipherment **/
    private EnciphermentService enciphermentService;
    
    
    /** Constructor.
     * @param userRepository repository.
     * @param userService service.
     */
    public UserFacadeImpl(UserRepository<User> userRepository, UserService userService,
    		GuestRepository guestRepository, EnciphermentService enciphermentService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.guestRepository = guestRepository;
        this.enciphermentService =enciphermentService;
    }

    /** Create a user.
     * @param mail user email (natural key).
     * @param firstName first name.
     * @param lastName last name.
     * @param canUpload if the user can upoad.
     * @param comment  the comment about the user
     * @param mailSubject mail subject.
     * @param mailContent content of the mail.
     * @param owner user who create the guest.
     * @throws BusinessException if user already exist.
     */
    public void createGuest(String mail, String firstName, String lastName, Boolean canUpload, Boolean canCreateGuest,String comment,
    		MailContainer mailContainer, UserVo owner) throws BusinessException {    	
        userService.createGuest(mail, firstName, lastName, mail, canUpload, canCreateGuest, comment, mailContainer, owner.getLogin(), owner.getDomainIdentifier());
    }
    
    public void updateGuest(String mail, String firstName, String lastName, Boolean canUpload, Boolean canCreateGuest, UserVo owner) throws BusinessException{
    	userService.updateGuest(mail, firstName, lastName, canUpload, canCreateGuest, owner);
    }
    
	public void updateUser(String mail,Role role, UserVo owner) throws BusinessException {
		userService.updateUser(mail,role, owner);
	}
    

    /** Search a user using its mail.
     * @param mail user mail.
     * @return founded user.
     * @throws BusinessException 
     */
    public UserVo findUser(String mail, String domain) throws BusinessException {
    	User user = userService.findUser(mail, domain);
    	if (user != null) {
    		return new UserVo(user);
    	}
    	return null;
    }
    
    

    /** Search a user.
     * @param mail user email.
     * @param firstName user first name.
     * @param lastName user last name.
     * @return a list of matching users.
     */
    public List<UserVo> searchUser(String mail, String firstName, String lastName, UserVo currentUser) throws BusinessException {
    	User owner = userRepository.findByLogin(currentUser.getLogin());
    	List<User> users = userService.searchUser(mail, firstName, lastName, null, owner);
        return getUserVoList(users);
    }

    /** Get all guests created by a user.
     * @param mail owner mail.
     * @return the list of guests created by their owner.
     */
    public List<UserVo> searchGuest(String mail) {
        User owner = userRepository.findByLogin(mail);
        if (owner == null) { // owner is not an internal -> probably a guest
            owner = guestRepository.findByLogin(mail);
        }
        List<Guest> users = guestRepository.searchGuest(null, null, null, owner);
        return getUserVoListFromGuest(users);
    }


    /** Convert a list of Users to a list of UserVo.
     * @param users a list of users.
     * @return a list of UserVo.
     */
    private List<UserVo> getUserVoList(List<User> users) {
        List<UserVo> userVOs = new ArrayList<UserVo>();
        for (User user : users) {
            userVOs.add(new UserVo(user));
        }
        return userVOs;
    }

    /** Convert a list of Guest to a list of UserVo.
     * @param users a list of users.
     * @return a list of UserVo.
     */
    private List<UserVo> getUserVoListFromGuest(List<Guest> users) {
        List<UserVo> userVOs = new ArrayList<UserVo>();
        for (Guest user : users) {
            userVOs.add(new UserVo(user));
        }
        return userVOs;
    }
    
    public void deleteUser(String login, UserVo actor) {
        try {
        	User owner = userRepository.findByLogin(actor.getLogin());
            userService.deleteUser(login, owner, true);
        } catch (BusinessException e) {
            logger.error(e.toString());
            throw new IllegalArgumentException("Provided login doesn't match an existing user");
        }
    }

    /**
     * @see UserFacade#searchUser(String, String, String, UserType)
     */
	public List<UserVo> searchUser(String mail, String firstName,
			String lastName, UserType userType,UserVo currentUser) throws BusinessException {
		User owner = userRepository.findByLogin(currentUser.getLogin());
		return getUserVoList(userService.searchUser(mail, firstName, lastName, userType, owner));
	}

	public List<String> findMails(String beginWith) {
		return userRepository.findMails(beginWith);
	}

	public void deleteTempAdminUser() throws  BusinessException {
		User user = userService.findUser(ADMIN_TEMP_MAIL, null);
		if(user!=null) userRepository.delete(user);
	}

	public UserVo searchTempAdminUser() throws BusinessException {
		return findUser(ADMIN_TEMP_MAIL, null);
	}

	public void updateUserLocale(UserVo user, String locale) {
		userService.updateUserLocale(user.getMail(), locale);
	}


    /** Load a User.
     * If the user doesn't exist in database, search informations in LDAP and create a user entry before returning it.
     * @param login user login.
     * @return user details or null if user is neither in database or LDAP.
     */
    public UserVo loadUserDetails(String login, String domainId) {
        User user = null;
        try {
            user = userService.findAndCreateUser(login, domainId);
        } catch (BusinessException ex) {
            throw new RuntimeException("User can't be created, please contact your administrator");
        }
        return new UserVo(user);
    }
    
    /** Get user password.
     * @param login user login.
     * @return password or null if empty or null.
     */
    public String getPassword(String login) {
        User user = userRepository.findByLogin(login);
        if (user == null || user.getPassword() == null || user.getPassword().length() == 0) {
            return null;
        } else {
            return user.getPassword();
        }
    }
    
    public void changePassword(UserVo user, String oldPassword, String newPassword) throws BusinessException {
    	if (!user.getUserType().equals(UserType.GUEST)) {
    		throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "The user type is wrong, only a guest may change its password");
    	}
    	
    	userService.changeGuestPassword(user.getLogin(), oldPassword, newPassword);
    	
    }

	public void resetPassword(UserVo user, MailContainer mailContainer) throws BusinessException {
		if (!user.getUserType().equals(UserType.GUEST)) {
    		throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "The user type is wrong, only a guest may change its password");
    	}
    	
    	userService.resetPassword(user.getLogin(), mailContainer);		
	}

	public void setGuestContactRestriction(String login, List<String> mailContacts) throws BusinessException {
		userService.setGuestContactRestriction(login, mailContacts);
	}
	
	public void removeGuestContactRestriction(String login) throws BusinessException {
		userService.removeGuestContactRestriction(login);
	}
	
	public void addGuestContactRestriction(String ownerLogin, String contactLogin) throws BusinessException {
		userService.addGuestContactRestriction(ownerLogin, contactLogin);
	}
	
	public List<UserVo> fetchGuestContacts(String login) throws BusinessException {
		List<User> contacts = userService.fetchGuestContacts(login);
		if (contacts!=null && !contacts.isEmpty()) {
			return getUserVoList(contacts);
		}
		return null;
	}
}
