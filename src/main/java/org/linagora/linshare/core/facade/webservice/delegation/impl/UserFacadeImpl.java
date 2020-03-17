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

package org.linagora.linshare.core.facade.webservice.delegation.impl;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.GenericUserDto;
import org.linagora.linshare.core.facade.webservice.common.dto.PasswordDto;
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
	public void changePassword(PasswordDto password) throws BusinessException {
		User authUser = checkAuthentication();
		userService.changePassword(authUser.getLsUuid(), authUser.getMail(),
				password.getOldPwd(), password.getNewPwd());
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
