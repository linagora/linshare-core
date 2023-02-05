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
package org.linagora.linshare.core.facade.webservice.delegation.impl;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.GenericUserDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDetailsDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDto;
import org.linagora.linshare.core.facade.webservice.delegation.UserFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.QuotaService;
import org.linagora.linshare.core.service.UserService;

public class UserFacadeImpl extends DelegationGenericFacadeImpl implements
		UserFacade {

	private final QuotaService quotaService;

	public UserFacadeImpl(
			AccountService accountService,
			UserService userService,
			QuotaService quotaService) {
		super(accountService, userService);
		this.quotaService = quotaService;
	}

	@Override
	public GenericUserDto getUser(String mail, String domainId)
			throws BusinessException {
		User authUser = checkAuthentication();
		if(domainId == null) {
			domainId = authUser.getDomainId();
		}
		// We can not accept this wildcard. 
		mail = mail.replace("*", "");
		User user = userService.findOrCreateUserWithDomainPolicies(mail, domainId);
		return new GenericUserDto(user);
	}

	@Override
	public UserDto findUser(UserDetailsDto userDetailsDto) throws BusinessException {
		User authUser = checkAuthentication();
		Validate.notEmpty(userDetailsDto.getMail(), "User mail must be set.");
		if (userDetailsDto.getDomain() == null) {
			userDetailsDto.setDomain(authUser.getDomainId());
		}
		User user = userService.findOrCreateUserWithDomainPolicies(userDetailsDto.getMail(),
				userDetailsDto.getDomain());
		UserDto dto = UserDto.getFull(user);
		AccountQuota quota = quotaService.findByRelatedAccount(user);
		dto.setQuotaUuid(quota.getUuid());
		return dto;
	}

	@Override
	public UserDto findUser(String uuid) throws BusinessException {
		checkAuthentication();
		Validate.notEmpty(uuid, "User uuid must be set.");
		User user = userService.findByLsUuid(uuid);
		UserDto dto = UserDto.getFull(user);
		AccountQuota quota = quotaService.findByRelatedAccount(user);
		dto.setQuotaUuid(quota.getUuid());
		return dto;
	}
}
