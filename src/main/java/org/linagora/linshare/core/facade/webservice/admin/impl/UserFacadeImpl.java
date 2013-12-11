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
package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.UserFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.webservice.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserFacadeImpl extends AdminGenericFacadeImpl implements
		UserFacade {

	private static final Logger logger = LoggerFactory
			.getLogger(UserFacadeImpl.class);

	private final UserService userService;

	public UserFacadeImpl(final AccountService accountService,
			final UserService userService) {
		super(accountService);
		this.userService = userService;
	}

	@Override
	public Set<UserDto> completionUser(String pattern) throws BusinessException {
		return searchUsers(pattern, null);
	}
	
	@Override
	public Set<UserDto> getInternals(String pattern) throws BusinessException {
		return searchUsers(pattern, AccountType.INTERNAL);
	}

	@Override
	public Set<UserDto> getGuests(String pattern) throws BusinessException {
		return searchUsers(pattern, AccountType.GUEST);
	}
	
	private Set<UserDto> searchUsers(String pattern, AccountType type) throws BusinessException {
		User currentUser = super.checkAuthentication();

		Set<UserDto> usersDto = new HashSet<UserDto>();
		Set<User> users = new HashSet<User>();
		users.addAll(userService.searchUser(pattern, null, null, type,
				currentUser));
		users.addAll(userService.searchUser(null, pattern, null, type,
				currentUser));
		users.addAll(userService.searchUser(null, null, pattern, type,
				currentUser));
		for (User user : users) {
			UserDto userDto = UserDto.getFull(user);

			if (userDto.isGuest()) {
				if (user.isRestricted()) {
					for (User contact : userService.fetchGuestContacts(user.getLsUuid())) {
						userDto.getRestrictedContacts().add(contact.getMail());
					}
				}
			}
			usersDto.add(userDto);
		}		
		return usersDto;
	}

	@Override
	public void updateUser(UserDto userDto) throws BusinessException {
		User actor = super.checkAuthentication();
		User user = getUser(userDto);
		userService.updateUser(actor, user, userDto.getDomain());
		if (userDto.isGuest() && user.isRestricted()) {
			userService.setGuestContactRestriction(userDto.getUuid(), userDto.getRestrictedContacts());
		}
	}

	@Override
	public void deleteUser(UserDto userDto) throws BusinessException {
		User actor = super.checkAuthentication();
		userService.deleteUser(actor, userDto.getUuid());
	}
	
	private User getUser(UserDto userDto) {
		if (userDto.isGuest()) {
			return new Guest(userDto);
		} else {
			return new Internal(userDto);
		}
	}
}