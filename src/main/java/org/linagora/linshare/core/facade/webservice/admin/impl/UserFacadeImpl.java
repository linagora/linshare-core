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
package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.UserFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.InconsistentSearchDto;
import org.linagora.linshare.core.facade.webservice.common.dto.PasswordDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UserSearchDto;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.InconsistentUserService;
import org.linagora.linshare.core.service.QuotaService;
import org.linagora.linshare.core.service.UserProviderService;
import org.linagora.linshare.core.service.UserService;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class UserFacadeImpl extends AdminGenericFacadeImpl implements
		UserFacade {

	final private static int AUTO_COMPLETE_LIMIT = 20;

	private final UserService userService;

	private final GuestService guestService;

	protected final QuotaService quotaService;

	private final InconsistentUserService inconsistentUserService;

	private final AbstractDomainService abstractDomainService;

	private final UserProviderService userProviderService;

	public UserFacadeImpl(final AccountService accountService,
			final UserService userService,
			final InconsistentUserService inconsistentUserService,
			final GuestService guestService,
			final QuotaService quotaService,
			final AbstractDomainService abstractDomainService,
			final UserProviderService userProviderService) {
		super(accountService);
		this.userService = userService;
		this.inconsistentUserService = inconsistentUserService;
		this.guestService = guestService;
		this.quotaService = quotaService;
		this.abstractDomainService = abstractDomainService;
		this.userProviderService = userProviderService;
	}

	@Override
	public List<UserDto> search(UserSearchDto userSearchDto)
			throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		return searchUsers(userSearchDto.getFirstName(),
				userSearchDto.getLastName(), userSearchDto.getMail(), null);
	}

	@Override
	public Set<UserDto> searchInternals(String pattern)
			throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		return searchUsers(pattern, AccountType.INTERNAL);
	}

	@Override
	public Set<UserDto> searchGuests(String pattern) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		return searchUsers(pattern, AccountType.GUEST);
	}

	/**
	 * Search users using firstname, lastname and mail as search criteria. Each
	 * param can be null. If all parameters are null, return all.
	 * 
	 * @param firstName
	 * @param lastName
	 * @param mail
	 * @param type
	 * @return
	 * @throws BusinessException
	 */
	private List<UserDto> searchUsers(String firstName, String lastName,
			String mail, AccountType type) throws BusinessException {
		User currentUser = super.checkAuthentication(Role.ADMIN);

		List<UserDto> usersDto = Lists.newArrayList();
		Set<User> users = new HashSet<User>();
		users.addAll(userService.searchUser(mail, firstName, lastName, type,
				currentUser));
		for (User user : users) {
			UserDto userDto = UserDto.getFull(user);

			if (user.isGuest() && user.isRestricted()) {
				Guest guest = guestService.find(currentUser, currentUser,
						user.getLsUuid());
				Set<AllowedContact> contacts = guest.getRestrictedContacts();
				for (AllowedContact contact : contacts) {
					userDto.getRestrictedContacts().add(
							UserDto.getSimple(contact.getContact()));
				}
			}
			usersDto.add(userDto);
		}
		return usersDto;
	}

	private Set<UserDto> searchUsers(String pattern, AccountType type)
			throws BusinessException {
		Set<UserDto> usersDto = new HashSet<UserDto>();
		usersDto.addAll(searchUsers(pattern, null, null, type));
		usersDto.addAll(searchUsers(null, pattern, null, type));
		usersDto.addAll(searchUsers(null, null, pattern, type));
		return usersDto;
	}

	@Override
	public UserDto update(UserDto userDto) throws BusinessException {
		Validate.notNull(userDto, "user must be set.");
		Validate.notEmpty(userDto.getUuid(), "uuid must be set.");
		Validate.notNull(userDto.getLocale(), "locale must be set.");
		User actor = checkAuthentication(Role.ADMIN);
		User entity = userService.findByLsUuid(userDto.getUuid());
		if (entity == null) {
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND,
					"Can not find user");
		}
		User userToUpdate = userDto.toUserObject(entity.isGuest());
		User update;
		if (entity.isGuest()) {
			List<String> ac = null;
			if (userDto.isRestricted()) {
				ac = Lists.newArrayList();
				for (UserDto contactDto : userDto.getRestrictedContacts()) {
					ac.add(contactDto.getMail());
				}
			}
			update = guestService.update(actor, (User) entity.getOwner(),
					(Guest) userToUpdate, ac);
		} else {
			update = userService.updateUser(actor, userToUpdate,
					userDto.getDomain());
		}
		return UserDto.getSimple(update);
	}

	@Override
	public UserDto delete(UserDto userDto) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		String uuid = userDto.getUuid();
		Validate.notEmpty(uuid, "user unique identifier must be set.");
		User user = userService.deleteUser(actor, uuid);
		return UserDto.getFull(user);
	}

	@Override
	public Set<UserDto> findAllInconsistent() throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		Set<UserDto> ret = Sets.newHashSet();

		for (User user : inconsistentUserService.findAllInconsistent(actor)) {
			ret.add(UserDto.getFull(user));
		}
		return ret;
	}

	@Override
	public void updateInconsistent(UserDto userDto) throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		update(userDto);
		inconsistentUserService.updateDomain(actor, userDto.getUuid(),
				userDto.getDomain());
	}

	@Override
	public List<InconsistentSearchDto> checkInconsistentUserStatus(UserSearchDto searchDto) {
		Validate.notNull(searchDto, "searchDto must not be null");
		String mail = searchDto.getMail();
		Validate.notEmpty(mail, "User mail must not be empty");
		checkAuthentication(Role.SUPERADMIN);
		List<InconsistentSearchDto> res = Lists.newArrayList();
		for (AbstractDomain domain : abstractDomainService.getAllDomains()) {
			User user = userService.findUserInDB(domain.getUuid(), mail);
			if (user != null) {
				if (!(user.isGuest() || user.isInternal())) {
					// we exclude technical users and root
					continue;
				}
				InconsistentSearchDto dto = new InconsistentSearchDto(domain, mail);
				dto.setDatabase(true);
				dto.setGuest(user.isGuest());
				dto.setUuid(user.getLsUuid());
				if (user.isInternal()) {
					dto.setLdap(abstractDomainService.isUserExist(domain, mail));
					if (dto.isLdap()) {
						if (user.isInconsistent()) {
							user.setInconsistent(false);
							accountService.update(user);
						}
					} else {
						if (!user.isInconsistent()) {
							user.setInconsistent(true);
							accountService.update(user);
						}
					}
				}
				res.add(dto);
			} else {
				if (abstractDomainService.isUserExist(domain, mail)) {
					InconsistentSearchDto dto = new InconsistentSearchDto(domain, mail);
					dto.setLdap(true);
					res.add(dto);
				}
			}
		}
		return res;
	}

	@Override
	public List<String> autocompleteInconsistent(UserSearchDto dto)
			throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		Set<String> res = Sets.newHashSet();
		List<User> internals = abstractDomainService
				.autoCompleteUserWithoutDomainPolicies(actor, dto.getMail());
		for (User user : internals) {
			res.add(user.getMail());
		}
		res.addAll(accountService.findAllKnownEmails(actor, dto.getMail()));
		int range = (res.size() < AUTO_COMPLETE_LIMIT ? res.size() : AUTO_COMPLETE_LIMIT);
		return Lists.newArrayList(res).subList(0, range);
	}

	@Override
	public void changePassword(PasswordDto password) throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		userService.changePassword(actor.getLsUuid(), actor.getMail(),
				password.getOldPwd(), password.getNewPwd());
	}

	@Override
	public UserDto findUser(String uuid) throws BusinessException {
		User currentUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(uuid, "User uuid must be set.");
		UserDto userDto = null;
		User user = userService.findByLsUuid(uuid);
		if (user.isGuest() && user.isRestricted()) {
			Guest guest = guestService.find(currentUser, currentUser,
					uuid);
			userDto =  UserDto.getFull(guest);
		} else {
			userDto = UserDto.getFull(user);
		}
		// get the quota for the current user.
		AccountQuota quota = quotaService.findByRelatedAccount(user);
		userDto.setQuotaUuid(quota.getUuid());
		return userDto;
	}

	@Override
	public boolean exist(String uuid) throws BusinessException {
		checkAuthentication(Role.ADMIN);
		Validate.notEmpty(uuid, "User uuid must be set.");
		return userService.exist(uuid);
	}

	@Override
	public UserDto create(UserDto userDto) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		Validate.notNull(userDto, "User dto must be set.");
		String uuid = userDto.getUuid();
		if (uuid != null) {
			if (userService.exist(uuid)) {
				return UserDto.getFull(userService.findByLsUuid(uuid));
			}
		}
		String mail = userDto.getMail();
		String domain = userDto.getDomain();
		Validate.notEmpty(mail, "User mail must be set.");
		Validate.notEmpty(domain, "User domain identifier must be set.");
		User user = userService.findOrCreateUserWithDomainPolicies(domain, mail, actor.getDomainId());
		return UserDto.getFull(user);
	}

	@Override
	public boolean updateEmail(String currentEmail, String newEmail) {
		User actor = checkAuthentication(Role.SUPERADMIN);
		logger.info("Start email migration...");
		logger.info("Step 1: Find and update user's email ...");
		boolean hasBeenUpdated = userService.updateUserEmail(actor, currentEmail, newEmail);

		if(hasBeenUpdated) {
			logger.info("Step 2: start updateMailingListEmail ...");
			userService.updateMailingListEmail(actor, currentEmail, newEmail);
			logger.info("Step 3: start updateRecipientFavourite ...");
			userService.updateRecipientFavourite(actor, currentEmail, newEmail);
		}
		logger.info("End of email migration...");
		return hasBeenUpdated;
	}
}
