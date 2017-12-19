/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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
