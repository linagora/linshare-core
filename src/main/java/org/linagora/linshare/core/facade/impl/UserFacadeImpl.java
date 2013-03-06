/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
package org.linagora.linshare.core.facade.impl;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Role;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.AbstractDomainVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.facade.UserFacade;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.EnciphermentService;
import org.linagora.linshare.core.service.UserService;
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

    private AccountService accountService;
    
    
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
    		GuestRepository guestRepository, EnciphermentService enciphermentService, AbstractDomainService abstractDomainService,AccountService accountService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.guestRepository = guestRepository;
        this.enciphermentService =enciphermentService;
        this.abstractDomainService = abstractDomainService;
        this.accountService = accountService;
    }

    
    /** Create a user.
     * @param mail user email (natural key).
     * @param firstName first name.
     * @param lastName last name.
     * @param canUpload if the user can upoad.
     * @param comment  the comment about the user
     * @param owner user who create the guest.
     * @param mailSubject mail subject.
     * @param mailContent content of the mail.
     * @return 
     * @throws BusinessException if user already exist.
     */
    @Override
    public UserVo createGuest(String mail, String firstName, String lastName, Boolean canUpload, Boolean canCreateGuest,String comment, UserVo owner) throws BusinessException {
    	
        Guest guest = userService.createGuest(mail, firstName, lastName, mail, canUpload, canCreateGuest, comment, owner.getLogin(), owner.getDomainIdentifier());
        return new UserVo(guest);
    }
    
    
    @Override
    public void updateGuest(String guestUuid, String domain, String mail, String firstName, String lastName, Boolean canUpload, Boolean canCreateGuest, UserVo owner) throws BusinessException{
    	
    	userService.updateGuest(guestUuid,domain, mail, firstName, lastName, canUpload, canCreateGuest, owner);
    }
    
    
    @Override
	public void updateUserRole(String userUuid, String domain,String mail, Role role, UserVo owner) throws BusinessException {
		userService.updateUserRole(userUuid, domain,mail, role, owner);
	}
    

    /** Search a user.
     * @param mail user email.
     * @param firstName user first name.
     * @param lastName user last name.
     * @return a list of matching users.
     */
    public List<UserVo> searchUser(String mail, String firstName, String lastName, UserVo currentUser) throws BusinessException {
    	User owner =  (User)accountService.findByLsUuid(currentUser.getLogin());
    	
    	List<User> users = userService.searchUser(mail, firstName, lastName, null, owner);
        return getUserVoList(users);
    }
    
    public List<UserVo> searchUser(String mail, String firstName, String lastName, AccountType userType,UserVo currentUser) throws BusinessException {
		User owner = userRepository.findByLsUuid(currentUser.getLogin());
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
        User owner = userRepository.findByMail(mail);
        if (owner == null) { // owner is not an internal -> probably a guest
            owner = guestRepository.findByMail(mail);
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
    
    
    @Override
    public void deleteUser(String login, UserVo actorVo) {
    	User actor = userRepository.findByLsUuid(actorVo.getLogin());
        try {
			userService.deleteUser(login, actor);
		} catch (BusinessException e) {
			logger.error("can't delete user : " + actorVo.getLsUuid() + " : " + e.getMessage());
			logger.debug(e.toString());
		}
    }

	public List<String> findMails(String beginWith) {
		return userRepository.findMails(beginWith);
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
        User user = userRepository.findByMail(login);
        if (user == null || user.getPassword() == null || user.getPassword().length() == 0) {
            return null;
        } else {
            return user.getPassword();
        }
    }
    
    public void changePassword(UserVo user, String oldPassword, String newPassword) throws BusinessException {
    	if (!(user.getUserType().equals(AccountType.GUEST) ||
    			user.getRole().equals(Role.SUPERADMIN))) {
    		throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "Only a guest or superadmin may change its password");
    	}
    	
    	userService.changePassword(user.getLsUuid(), user.getMail(), oldPassword, newPassword);
    	
    }

	public void resetPassword(UserVo user) throws BusinessException {
		if (!user.getUserType().equals(AccountType.GUEST)) {
    		throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE, "The user type is wrong, only a guest may change its password");
    	}
    	
    	userService.resetPassword(user.getLsUuid(), user.getMail());		
	}

	public void setGuestContactRestriction(String uuid, List<String> mailContacts) throws BusinessException {
		userService.setGuestContactRestriction(uuid, mailContacts);
	}
	
	public void removeGuestContactRestriction(String uuid) throws BusinessException {
		userService.removeGuestContactRestriction(uuid);
	}
	
	public void addGuestContactRestriction(String ownerUuid, String contactUuid) throws BusinessException {
		userService.addGuestContactRestriction(ownerUuid, contactUuid);
	}
	
	public List<UserVo> fetchGuestContacts(String uuid) throws BusinessException {
		List<User> contacts = userService.fetchGuestContacts(uuid);
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
		User actor = userRepository.findByMail(userLoggedIn.getLogin());
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
		if (((User)guest.getOwner()).getLogin().equals(userLoggedIn.getLogin())) {
			return new UserVo(guest);
		}
		return null;
	}
	

	@Override
	public UserVo findGuestByLsUuid(UserVo actorVo, String guestUuid) {
		Guest guest = guestRepository.findByLsUuid(guestUuid);
		if (guest != null) {
			return new UserVo(guest);
		}
		return null;
	}
	

	@Override
	public UserVo findUserByLsUuid(UserVo actorVo, String uuid) {
		User user = userRepository.findByLsUuid(uuid);
		if (user != null) {
			return new UserVo(user);
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
		User actor = userRepository.findByLsUuid(actorVo.getLogin());
		return userService.isAdminForThisUser(actor, userToManageVo.getDomainIdentifier(), userToManageVo.getMail());
	}
}
