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
import org.linagora.linShare.core.domain.constants.UserType;
import org.linagora.linShare.core.domain.entities.Guest;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.entities.Role;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.vo.AbstractDomainVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.exception.TechnicalErrorCode;
import org.linagora.linShare.core.exception.TechnicalException;
import org.linagora.linShare.core.repository.GuestRepository;
import org.linagora.linShare.core.repository.UserRepository;
import org.linagora.linShare.core.service.AbstractDomainService;
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
    
    /** Domain service. */
    private AbstractDomainService abstractDomainService;

    /** Guest repository. */
    private GuestRepository guestRepository;
    
    
    /**info on encipherment **/
    private EnciphermentService enciphermentService;
    
    
    /** Constructor.
     * @param userRepository repository.
     * @param userService service.
     */
    public UserFacadeImpl(UserRepository<User> userRepository, UserService userService,
    		GuestRepository guestRepository, EnciphermentService enciphermentService, AbstractDomainService abstractDomainService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.guestRepository = guestRepository;
        this.enciphermentService =enciphermentService;
        this.abstractDomainService = abstractDomainService;
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
    
    public void updateGuest(String domain, String mail, String firstName, String lastName, Boolean canUpload, Boolean canCreateGuest, UserVo owner) throws BusinessException{
    	userService.updateGuest(domain,mail, firstName, lastName, canUpload, canCreateGuest, owner);
    }
    
	public void updateUserRole(String domain, String mail,Role role, UserVo owner) throws BusinessException {
		userService.updateUserRole(domain, mail,role, owner);
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
    
    public List<UserVo> searchUser(String mail, String firstName, String lastName, UserType userType,UserVo currentUser) throws BusinessException {
		User owner = userRepository.findByLogin(currentUser.getLogin());
		return getUserVoList(userService.searchUser(mail, firstName, lastName, userType, owner));
	}


    @Override
	public List<UserVo> searchUserForRestrictedGuestEditionForm(String mail, String firstName, String lastName, String currentGuestEmail) throws BusinessException {
    	User guest = userRepository.findByMail(currentGuestEmail);
    	List<User> users = userService.searchUserForRestrictedGuestEditionForm(mail, firstName, lastName, guest);
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
    
    public void deleteUser(String login, UserVo actorVo) {
        try {
        	User actor = userRepository.findByLogin(actorVo.getLogin());
            userService.deleteUser(login, actor);
        } catch (BusinessException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

	public List<String> findMails(String beginWith) {
		return userRepository.findMails(beginWith);
	}

	public UserVo searchTempAdminUser() throws BusinessException {
		User user = userService.findUnkownUserInDB(ADMIN_TEMP_MAIL);
		UserVo userVo = new UserVo(user);
		if (userVo.isSuperAdmin()) {
			return null; // a super admin is not a temp admin, we need to keep this account !
		}
		return userVo;
	}

	public void updateUserLocale(UserVo user, String locale) {
		userService.updateUserLocale(user.getDomainIdentifier(),user.getMail(), locale);
	}


    /** Load a User.
     * If the user doesn't exist in database, search informations in LDAP and create a user entry before returning it.
     * @param login user login.
     * @return user details or null if user is neither in database or LDAP.
     */
    public UserVo loadUserDetails(String login, String domainId) {
        User user = null;
        try {
            user = userService.findOrCreateUserWithDomainPolicies(login, domainId);
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
    	if (!(user.getUserType().equals(UserType.GUEST) ||
    			user.getRole().equals(Role.SUPERADMIN))) {
    		throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Only a guest or superadmin may change its password");
    	}
    	
    	userService.changePassword(user.getLogin(), oldPassword, newPassword);
    	
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
	
	public void updateUserDomain(String mail, AbstractDomainVo selectedDomain,
			UserVo userLoggedIn) throws BusinessException {
		userService.updateUserDomain(mail, selectedDomain.getIdentifier(), userLoggedIn);
	}
	
	public List<UserVo> searchAllBreakedUsers(UserVo userLoggedIn) {
		User actor = userRepository.findByLogin(userLoggedIn.getLogin());
		if(actor.getRole().equals(Role.SUPERADMIN)) {
			return getUserVoList(userService.searchAllBreakedUsers(actor));
		} else {
			return new ArrayList<UserVo>();
		}
	}

	@Override
	public UserVo findUserInDb(String mail, String domain) {
		User user = userService.findUserInDB(domain, mail);
		if(user != null) {
			return new UserVo(user);
		} else {
			return null;
		}
	}
	
	/** Search a user using its mail.
     * @param mail user mail.
     * @return founded user.
     * @throws BusinessException 
     */
	@Override
    public UserVo findUserForAuth(String mail) throws BusinessException {
    	User user = userService.searchAndCreateUserEntityFromUnkownDirectory(mail);
    	if (user != null) {
    		return new UserVo(user);
    	}
    	return null;
    }

	/** Search a user using its mail.
     * @param mail user mail.
     * @return found user.
     * @throws BusinessException 
     */
	@Override
    public UserVo findUser(String domain, String mail) throws BusinessException {
		User user = userService.findOrCreateUser(mail,domain);
    	if (user != null) {
    		return new UserVo(user);
    	}
    	return null;
    }
	
	@Override
	public UserVo findGuestWithMailAndUserLoggedIn(UserVo userLoggedIn, String mail) {
		Guest guest = guestRepository.findByMail(mail);
		if (guest == null)
			return null;
		if (guest.getOwner().getLogin().equals(userLoggedIn.getLogin())) {
			return new UserVo(guest);
		}
		return null;
	}

	@Override
	public UserVo findUserFromAuthorizedDomainOnly(String domainId, String mail) {
		List<String> allMyDomainIdentifiers = abstractDomainService.getAllMyDomainIdentifiers(domainId);
		for (String string : allMyDomainIdentifiers) {
			User user = userRepository.findByMailAndDomain(string, mail);
			if(user != null) {
				return new UserVo(user);
			}
		}
		return null;
	}

	@Override
	public UserVo findUserForResetPassordForm(String mail, String optionalDomainId) {
		
		if(optionalDomainId == null) {
			List<String> allDomainIdentifiers = abstractDomainService.getAllDomainIdentifiers();
			for (String domain : allDomainIdentifiers) {
				User user = userRepository.findByMailAndDomain(domain, mail);
				if(user != null) {
					return new UserVo(user);
				}
			}
		} else {
			User user = userRepository.findByMailAndDomain(mail, optionalDomainId);
			if(user != null) {
				return new UserVo(user);
			}
		}
		
		return null;
	}

	@Override
	public boolean isAdminForThisUser(UserVo actorVo, UserVo userToManageVo) throws BusinessException {
		User actor = userService.findOrCreateUser(actorVo.getMail(), actorVo.getDomainIdentifier());
		return userService.isAdminForThisUser(actor, userToManageVo.getDomainIdentifier(), userToManageVo.getMail());
	}
}
