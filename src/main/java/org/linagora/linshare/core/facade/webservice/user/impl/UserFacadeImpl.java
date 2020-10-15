/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.BooleanValueFunctionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDto;
import org.linagora.linshare.core.facade.webservice.user.UserFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.JwtService;
import org.linagora.linshare.core.service.QuotaService;
import org.linagora.linshare.core.service.UserService;

public class UserFacadeImpl extends UserGenericFacadeImp implements UserFacade {

	private final UserService userService;

	protected final QuotaService quotaService;

	protected final JwtService jwtService;

	protected final FunctionalityReadOnlyService functionalityReadOnlyService;

	public UserFacadeImpl(
			final AccountService accountService,
			final UserService userService,
			final QuotaService quotaService,
			final JwtService jwtService,
			final FunctionalityReadOnlyService functionalityReadOnlyService
			) {
		super(accountService);
		this.userService = userService;
		this.quotaService = quotaService;
		this.jwtService = jwtService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
	}

	@Override
	public User checkAuthentication() throws BusinessException {
		User authUser = super.checkAuthentication();
		return authUser;
	}

	@Override
	public List<UserDto> findAll() throws BusinessException {
		User authUser = checkAuthentication();
		List<UserDto> res = new ArrayList<UserDto>();
		// we return all users without any filters
		List<User> users = userService.searchUser(null, null, null, null, authUser);

		for (User user : users)
			res.add(UserDto.getSimple(user));
		logger.debug("user found : " + res.size());
		return res;
	}

	@Override
	public UserDto isAuthorized(Integer version) throws BusinessException {
		User authUser = checkAuthentication();
		UserDto dto = UserDto.getFull(authUser);
		// get the quota for the current logged in user.
		AccountQuota quota = quotaService.findByRelatedAccount(authUser);
		dto.setQuotaUuid(quota.getUuid());
		if (version >= 4) {
			BooleanValueFunctionality twofaFunc = functionalityReadOnlyService.getSecondFactorAuthenticationFunctionality(authUser.getDomain());
			if (twofaFunc.getActivationPolicy().getStatus()) {
				dto.setSecondFAUuid(authUser.getLsUuid());
				dto.setSecondFAEnabled(authUser.isUsing2FA());
				dto.setSecondFARequired(twofaFunc.getMaxValue());
			} else {
				dto.setSecondFAUuid(null);
				dto.setSecondFAEnabled(false);
				dto.setSecondFARequired(false);
			}
		}
		return dto;
	}

	@Override
	public String generateToken() throws BusinessException {
		User authUser = checkAuthentication();
		if (!authUser.isInternal()) {
			String message = "You can not generate JWT token for account which is not internal user.";
			throw new BusinessException(BusinessErrorCode.METHOD_NOT_ALLOWED, message);
		}
		return jwtService.generateToken(authUser);
	}
}
