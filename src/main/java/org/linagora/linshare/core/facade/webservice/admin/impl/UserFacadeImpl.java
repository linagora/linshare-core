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
package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.UserFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.InconsistentUserService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.webservice.dto.PasswordDto;
import org.linagora.linshare.webservice.dto.UserDto;
import org.linagora.linshare.webservice.dto.UserSearchDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

public class UserFacadeImpl extends AdminGenericFacadeImpl implements
		UserFacade {

	private static final Logger logger = LoggerFactory
			.getLogger(UserFacadeImpl.class);

	private final UserService userService;

	private final GuestService guestService;

	private final InconsistentUserService inconsistentUserService;

	public UserFacadeImpl(final AccountService accountService,
			final UserService userService,
			final InconsistentUserService inconsistentUserService,
			final GuestService guestService) {
		super(accountService);
		this.userService = userService;
		this.inconsistentUserService = inconsistentUserService;
		this.guestService = guestService;
	}

	@Override
	public Set<UserDto> search(UserSearchDto userSearchDto)
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
	private Set<UserDto> searchUsers(String firstName, String lastName,
			String mail, AccountType type) throws BusinessException {
		User currentUser = super.checkAuthentication();

		Set<UserDto> usersDto = new HashSet<UserDto>();
		Set<User> users = new HashSet<User>();
		users.addAll(userService.searchUser(mail, firstName, lastName, type,
				currentUser));
		for (User user : users) {
			UserDto userDto = UserDto.getFull(user);

			if (user.isGuest() && user.isRestricted()) {
				Guest guest = guestService.findByLsUuid(currentUser,
						user.getLsUuid());
				Set<AllowedContact> contacts = guest.getContacts();
				for (AllowedContact contact : contacts) {
					userDto.getRestrictedContacts().add(
							contact.getContact().getMail());
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
		User actor = checkAuthentication(Role.ADMIN);
		User user = getUser(userDto);
		User update;
		if (userDto.isGuest()) {
			update = guestService.update(actor, (Guest) user, user.getOwner()
					.getLsUuid());
		} else {
			update = userService.updateUser(actor, user, userDto.getDomain());
		}
		return UserDto.getSimple(update);
	}

	@Override
	public void delete(UserDto userDto) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		String uuid = userDto.getUuid();
		Validate.notEmpty(uuid, "user unique identifier must be set.");
		userService.deleteUser(actor, uuid);
	}

	private User getUser(UserDto userDto) {
		Validate.notEmpty(userDto.getUuid(),
				"user unique identifier must be set.");
		if (userDto.isGuest()) {
			return new Guest(userDto);
		}
		return new Internal(userDto);
	}

	@Override
	public Set<UserDto> findAllInconsistent() throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		Set<UserDto> ret = Sets.newHashSet();

		for (User user : inconsistentUserService.findAll(actor)) {
			ret.add(UserDto.getFull(user));
		}
		return ret;
	}

	@Override
	public void updateInconsistent(UserDto userDto) throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		inconsistentUserService.updateDomain(actor, userDto.getUuid(),
				userDto.getDomain());
	}

	@Override
	public void changePassword(PasswordDto password) throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		userService.changePassword(actor.getLsUuid(), actor.getMail(),
				password.getOldPwd(), password.getNewPwd());
	}
}
