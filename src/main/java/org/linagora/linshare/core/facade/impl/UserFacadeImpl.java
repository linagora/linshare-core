/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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
import java.util.Set;

import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.AbstractDomainVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.facade.UserFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.GuestDto;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.EnciphermentService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * Facade entry for user management.
 */
public class UserFacadeImpl implements UserFacade {

	Logger logger = LoggerFactory.getLogger(UserFacadeImpl.class);

	private final UserRepository<User> userRepository;

	private final UserService userService;

	private final AccountService accountService;

	private final AbstractDomainService abstractDomainService;

	private final GuestRepository guestRepository;

	private final EnciphermentService enciphermentService;

	private final GuestService guestService;

	/**
	 * Constructor.
	 * 
	 * @param userRepository
	 *            repository.
	 * @param userService
	 *            service.
	 */
	public UserFacadeImpl(final UserRepository<User> userRepository,
			final UserService userService,
			final GuestRepository guestRepository,
			final EnciphermentService enciphermentService,
			final AbstractDomainService abstractDomainService,
			final AccountService accountService,
			final GuestService guestService) {
		this.userRepository = userRepository;
		this.userService = userService;
		this.guestRepository = guestRepository;
		this.enciphermentService = enciphermentService;
		this.abstractDomainService = abstractDomainService;
		this.accountService = accountService;
		this.guestService = guestService;
	}

	/**
	 * Create a user.
	 * 
	 * @param mail
	 *            user email (natural key).
	 * @param firstName
	 *            first name.
	 * @param lastName
	 *            last name.
	 * @param canUpload
	 *            if the user can upoad.
	 * @param comment
	 *            the comment about the user
	 * @param owner
	 *            user who create the guest.
	 * @param mailSubject
	 *            mail subject.
	 * @param mailContent
	 *            content of the mail.
	 * @return
	 * @throws BusinessException
	 *             if user already exist.
	 */
	@Override
	public UserVo createGuest(String mail, String firstName, String lastName,
			Boolean canUpload, Boolean canCreateGuest, String comment,
			UserVo owner) throws BusinessException {

		User actor = userService.findByLsUuid(owner.getLsUuid());
		Guest guest = new Guest(firstName,lastName, mail);
		guest.setCanUpload(canUpload);
		guest.setComment(comment);
		return new UserVo(guestService.create(actor, actor, guest));
	}

	@Override
	public void updateGuest(String guestUuid, String domain, String mail,
			String firstName, String lastName, Boolean canUpload,
			UserVo owner) throws BusinessException {
		User actor = userService.findByLsUuid(owner.getLsUuid());
		Guest guest = new Guest(firstName, lastName, mail);
		guest.setCanUpload(canUpload);
		guest.setLsUuid(guestUuid);
		guestService.update(actor, actor, guest);
	}

	@Override
	public void updateUserRole(String userUuid, String domain, String mail,
			Role role, UserVo owner) throws BusinessException {
		userService.updateUserRole(userUuid, domain, mail, role, owner);
	}

	/**
	 * Search a user.
	 * 
	 * @param mail
	 *            user email.
	 * @param firstName
	 *            user first name.
	 * @param lastName
	 *            user last name.
	 * @return a list of matching users.
	 */
	public List<UserVo> searchUser(String mail, String firstName,
			String lastName, UserVo currentUser) throws BusinessException {
		User owner = (User) accountService
				.findByLsUuid(currentUser.getLsUuid());

		List<User> users = userService.searchUser(mail, firstName, lastName,
				null, owner);
		return getUserVoList(users);
	}

	public List<UserVo> searchUser(String mail, String firstName,
			String lastName, AccountType userType, UserVo currentUser)
			throws BusinessException {
		User owner = userRepository.findByLsUuid(currentUser.getLogin());
		return getUserVoList(userService.searchUser(mail, firstName, lastName,
				userType, owner));
	}

	/**
	 * Get all guests created by a user.
	 * 
	 * @param actorVo
	 * @return the list of guests created by the actor.
	 */
	@Override
	public List<UserVo> searchGuest(UserVo actorVo) {
		Account owner = accountService.findByLsUuid(actorVo.getLsUuid());
		List<Guest> users = guestRepository
				.searchGuest(owner, null, null, null);
		return getUserVoListFromGuest(users);
	}

	/**
	 * Convert a list of Users to a list of UserVo.
	 * 
	 * @param users
	 *            a list of users.
	 * @return a list of UserVo.
	 */
	private List<UserVo> getUserVoList(List<User> users) {
		List<UserVo> userVOs = new ArrayList<UserVo>();
		for (User user : users) {
			userVOs.add(new UserVo(user));
		}
		return userVOs;
	}

	/**
	 * Convert a list of Guest to a list of UserVo.
	 * 
	 * @param users
	 *            a list of users.
	 * @return a list of UserVo.
	 */
	private List<UserVo> getUserVoListFromGuest(List<Guest> users) {
		List<UserVo> userVOs = new ArrayList<UserVo>();
		for (Guest user : users) {
			userVOs.add(new UserVo(user));
		}
		return userVOs;
	}

	// TODO FMA
	@Override
	public void deleteUser(String login, UserVo actorVo) {
		User actor = userRepository.findByLsUuid(actorVo.getLogin());
		try {
			userService.deleteUser(actor, login);
		} catch (BusinessException e) {
			logger.error("can't delete user : " + actorVo.getLsUuid() + " : "
					+ e.getMessage());
			logger.debug(e.toString());
		}
	}

	public List<String> findMails(String beginWith) {
		return userRepository.findMails(beginWith);
	}

	public void updateUserLocale(UserVo user, String locale)
			throws BusinessException {
		userService.updateUserLocale(user.getDomainIdentifier(),
				user.getMail(), locale);
	}

	/**
	 * Load a User. If the user doesn't exist in database, search informations
	 * in LDAP and create a user entry before returning it.
	 * 
	 * @param login
	 *            user login.
	 * @return user details or null if user is neither in database or LDAP.
	 */
	public UserVo loadUserDetails(String login, String domainId) {
		User user = null;
		try {
			user = userService.findOrCreateUserWithDomainPolicies(login,
					domainId);
		} catch (BusinessException ex) {
			throw new RuntimeException(
					"User can't be created, please contact your administrator");
		}
		return new UserVo(user);
	}

	/**
	 * Get user password.
	 * 
	 * @param login
	 *            user login.
	 * @return password or null if empty or null.
	 */
	public String getPassword(String login) {
		User user = userRepository.findByMail(login);
		if (user == null || user.getPassword() == null
				|| user.getPassword().length() == 0) {
			return null;
		} else {
			return user.getPassword();
		}
	}

	public void changePassword(UserVo user, String oldPassword,
			String newPassword) throws BusinessException {
		if (!(
				user.isGuest()
				|| user.isSuperAdmin()
				|| user.hasDelegationRole()
				|| user.hasUploadPropositionRole()
			)) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE,
					"Only a guest or superadmin may change its password");
		}
		userService.changePassword(user.getLsUuid(), user.getMail(),
				oldPassword, newPassword);

	}

	@Override
	public void resetPassword(UserVo user) throws BusinessException {
		if (!user.getUserType().equals(AccountType.GUEST)) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE,
					"The user type is wrong, only a guest may change its password");
		}
		guestService.resetPassword(user.getLsUuid());
	}

	@Override
	public void setGuestContactRestriction(UserVo actorVo, String lsUuid,
			List<String> mailContacts) throws BusinessException {

		User actor = userService.findByLsUuid(actorVo.getLsUuid());
		GuestDto dto = GuestDto.getFull(guestService.find(actor, actor, lsUuid));
		Guest guest = dto.toUserObject();
		guest.setRestricted(true);
		for (String mail : mailContacts) {
			User user = userService.findUnkownUserInDB(mail);
			guest.addContact(new AllowedContact(guest, user));
		}
		guestService.update(actor, actor, guest);
	}

	@Override
	public void removeGuestContactRestriction(UserVo actorVo, String lsUuid)
			throws BusinessException {
		User actor = userService.findByLsUuid(actorVo.getLsUuid());
		GuestDto dto = GuestDto.getFull(guestService.find(actor, actor, lsUuid));
		Guest guest = dto.toUserObject();
		guest.setRestricted(false);
		guestService.update(actor, actor, guest);
	}

	public void addGuestContactRestriction(UserVo actorVo, String ownerLsUuid, String guestLsUuid)
			throws BusinessException {
		User actor = userService.findByLsUuid(actorVo.getLsUuid());
		GuestDto dto = GuestDto.getFull(guestService.find(actor, actor, guestLsUuid));
		Guest guest = dto.toUserObject();
		guest.setRestricted(true);
		guest.addContact(new AllowedContact(guest, actor));
		guestService.update(actor, actor, guest);
	}

	public List<UserVo> fetchGuestContacts(UserVo actorVo, String lsUuid)
			throws BusinessException {
		User actor = userService.findByLsUuid(actorVo.getLsUuid());
		Guest guest = guestService.find(actor, actor, lsUuid);
		Set<AllowedContact> contacts = guest.getRestrictedContacts();
		// compatibility
		if (contacts.isEmpty()) {
			return null;
		}
		List<UserVo> ret = Lists.newArrayList();
		for (AllowedContact contact : contacts) {
			ret.add(new UserVo(contact.getContact()));
		}
		return ret;
	}

	public void updateUserDomain(String mail, AbstractDomainVo selectedDomain,
			UserVo userLoggedIn) throws BusinessException {
		userService.updateUserDomain(mail, selectedDomain.getIdentifier(),
				userLoggedIn);
	}

	public List<UserVo> searchAllBreakedUsers(UserVo userLoggedIn) {
		User actor = userRepository.findByMail(userLoggedIn.getLogin());
		if (actor.getRole().equals(Role.SUPERADMIN)) {
			return getUserVoList(userService.searchAllBreakedUsers(actor));
		} else {
			return new ArrayList<UserVo>();
		}
	}

	@Override
	public UserVo findUserInDb(String mail, String domain) {
		User user = userService.findUserInDB(domain, mail);

		if (user == null)
			return null;
		return new UserVo(user);
	}

	/**
	 * Search a user using its mail.
	 * 
	 * @param mail
	 *            user mail.
	 * @return found user.
	 * @throws BusinessException
	 */
	@Override
	public UserVo findUser(String domain, String mail) throws BusinessException {
		User user = userService.findOrCreateUser(mail, domain);

		if (user == null)
			return null;
		return new UserVo(user);
	}

	@Override
	public UserVo findGuestWithMailAndUserLoggedIn(UserVo userLoggedIn,
			String mail) {
		Guest guest = guestRepository.findByMail(mail);

		if (guest == null)
			return null;
		if (!((User) guest.getOwner()).getLogin().equals(
				userLoggedIn.getLogin()))
			return null;
		return new UserVo(guest);
	}

	@Override
	public UserVo findGuestByLsUuid(UserVo actorVo, String guestUuid) {
		Guest guest = guestRepository.findByLsUuid(guestUuid);

		if (guest == null)
			return null;
		return new UserVo(guest);
	}

	@Override
	public UserVo findUserByLsUuid(UserVo actorVo, String uuid) {
		User user = userRepository.findByLsUuid(uuid);

		if (user == null)
			return null;
		return new UserVo(user);
	}

	@Override
	public UserVo findUserFromAuthorizedDomainOnly(String domainId, String mail) {
		List<String> allMyDomainIdentifiers = abstractDomainService
				.getAllMyDomainIdentifiers(domainId);
		for (String string : allMyDomainIdentifiers) {
			User user = userRepository.findByMailAndDomain(string, mail);
			if (user != null) {
				return new UserVo(user);
			}
		}
		return null;
	}

	@Override
	public UserVo findUserForResetPassordForm(String mail,
			String optionalDomainId) {

		if (optionalDomainId == null) {
			List<String> allDomainIdentifiers = abstractDomainService
					.getAllDomainIdentifiers();
			for (String domain : allDomainIdentifiers) {
				User user = userRepository.findByMailAndDomain(domain, mail);
				if (user != null) {
					return new UserVo(user);
				}
			}
		} else {
			User user = userRepository.findByMailAndDomain(mail,
					optionalDomainId);
			if (user != null) {
				return new UserVo(user);
			}
		}

		return null;
	}

	@Override
	public boolean isAdminForThisUser(UserVo actorVo, UserVo userVo) throws BusinessException {
		User actor = userRepository.findByLsUuid(actorVo.getLsUuid());
		User user = userService.findOrCreateUser(userVo.getMail(), userVo.getDomainIdentifier());
		return userService.isAdminForThisUser(actor, user);
	}
}
