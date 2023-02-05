/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.AutocompleteFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.UserService;

public class AutocompleteFacadeImpl extends AdminGenericFacadeImpl implements AutocompleteFacade {

	final private static int AUTO_COMPLETE_LIMIT = 20;

	private final UserService userService;

	public AutocompleteFacadeImpl(
			final AccountService accountService,
			final UserService userService) {
		super(accountService);
		this.userService = userService;
	}

	@Override
	public Set<UserDto> findUser(String pattern) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		List<User> users = userService.autoCompleteUser(authUser, pattern);
		logger.debug("nb result for completion : " + users.size());
		// TODO : FMA : Use database configuration for auto complete limit
		return getUserDtoList(users);
	}

	@Override
	public Set<String> getMail(String pattern) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(pattern, "pattern must be set.");
		List<User> users = userService.autoCompleteUser(authUser, pattern);
		logger.debug("nb result for completion : " + users.size());
		// TODO : FMA : Use database configuration for auto complete limit
		return getMailList(users, AUTO_COMPLETE_LIMIT);
	}

	private Set<UserDto> getUserDtoList(List<User> users) {
		HashSet<UserDto> hashSet = new HashSet<UserDto>();
		int range = (users.size() < AUTO_COMPLETE_LIMIT ? users.size() : AUTO_COMPLETE_LIMIT);
		for (User user : users.subList(0, range)) {
			hashSet.add(UserDto.getSimple(user));
		}
		return hashSet;
	}

	private Set<String> getMailList(List<User> users, int limit) {
		Set<String> res = new HashSet<String>();
		for (User user : users) {
			res.add(user.getMail());
		}
		return res;
	}
}
