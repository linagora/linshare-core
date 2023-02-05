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
package org.linagora.linshare.core.facade.webservice.user.impl;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.AccountDto;
import org.linagora.linshare.core.facade.webservice.user.GenericFacade;
import org.linagora.linshare.core.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class GenericFacadeImpl implements GenericFacade {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected final AccountService accountService;

	public GenericFacadeImpl(AccountService accountService) {
		super();
		this.accountService = accountService;
	}

	protected User getAuthentication() {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		// get logged in username
		String name = (auth != null) ? auth.getName() : null;

		logger.debug("Authentication with principal : " + name);
		if (name == null)
			return null;
		User user = (User) accountService.findByLsUuid(name);
		logger.debug("Authenticated user : " + user.getAccountRepresentation());
		return user;
	}

	protected User checkAuthentication() throws BusinessException {
		User authUser = getAuthentication();

		if (authUser == null)
			throw new BusinessException(
					BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		return authUser;
	}

	/**
	 * This function will check if user is authenticated using
	 * checkAuthentication, if he has the required role to grant access to the
	 * current Facade. This function must be used for using AsyncFacade purpose
	 * only.
	 * 
	 * @return It will return an AccountDto of the current authenticated
	 *         account.
	 * @throws BusinessException
	 */
	@Override
	public AccountDto getAuthenticatedAccountDto() throws BusinessException {
		User authUser = checkAuthentication();
		return new AccountDto(authUser, false);
	}

	protected User getActor(String actorUuid) {
		User actor = (User) accountService.findByLsUuid(actorUuid);
		if (actor == null) {
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND,
					"actor not found");
		}
		return actor;
	}

	protected User getActor(Account authUser, String actorUuid) {
		if (actorUuid != null) {
			logger.trace("trying to find actor with uuid : " + actorUuid);
			User actor = (User) accountService.findByLsUuid(actorUuid);
			if (actor == null) {
				logger.error("actor with uuid : " + actorUuid + " not found.");
				throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND,
						"actor not found");
			}
			return actor;
		} else {
			logger.trace("actor uuid null, returning the authUser as actor.");
			return (User) authUser;
		}
	}
}
