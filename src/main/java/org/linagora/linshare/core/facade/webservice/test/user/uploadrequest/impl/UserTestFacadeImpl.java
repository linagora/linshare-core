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
package org.linagora.linshare.core.facade.webservice.test.user.uploadrequest.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.facade.webservice.test.user.dto.UserDto;
import org.linagora.linshare.core.facade.webservice.test.user.uploadrequest.UserTestFacade;
import org.linagora.linshare.core.facade.webservice.user.impl.GenericFacadeImpl;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.UserService;

public class UserTestFacadeImpl extends GenericFacadeImpl implements UserTestFacade {

	private final UserService userService;

	public UserTestFacadeImpl(AccountService accountService, UserService userService) {
		super(accountService);
		this.userService = userService;
	}

	@Override
	public List<UserDto> findAll() {
		User authUser = checkAuthentication();
		return userService.searchUser(null, null, null, null, authUser)
				.stream()
				.map(res -> new UserDto(
						res.getLsUuid(),
						res.getFirstName(),
						res.getLastName(),
						res.getMail(),
						res.getDomain().getUuid(),
						res.getDomain().getLabel(),
						res.isCanCreateGuest(),
						res.isCanUpload()))
				.collect(Collectors.toUnmodifiableList());
	}

	@Override
	public UserDto create(UserDto user) {
		User authUser = checkAuthentication();
		Validate.notNull(user, "User dto must be set.");
		String mail = user.getMail();
		String domain = user.getDomainUuid();
		if (domain == null) {
			domain = authUser.getDomainId();
		}
		Validate.notEmpty(mail, "User mail must be set.");
//		Validate.notEmpty(domain, "User domain identifier must be set.");
		User res = userService.findOrCreateUserWithDomainPolicies(domain, mail, domain, Optional.empty());
		Boolean canCreateGuest = Objects.isNull(user.isCanCreateGuest()) ? true : user.isCanCreateGuest();
		Boolean canUpload = Objects.isNull(user.isPersonalSpaceEnabled()) ? true : user.isPersonalSpaceEnabled();
		res.setCanCreateGuest(canCreateGuest);
		res.setCanUpload(canUpload);
		return new UserDto(
				res.getLsUuid(),
				res.getFirstName(),
				res.getLastName(),
				res.getMail(),
				res.getDomain().getUuid(),
				res.getDomain().getLabel(),
				res.isCanCreateGuest(),
				res.isCanUpload());
	}

}
