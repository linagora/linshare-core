/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.PasswordService;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.GenericUserDto;
import org.linagora.linshare.core.facade.webservice.common.dto.GuestDto;
import org.linagora.linshare.core.facade.webservice.common.dto.PasswordDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UserSearchDto;
import org.linagora.linshare.core.facade.webservice.user.GuestFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.UserService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class GuestFacadeImpl extends GenericFacadeImpl implements
		GuestFacade {

	private final GuestService guestService;

	private final UserService userService;
	
	private final PasswordService passwordService;

	public GuestFacadeImpl(final AccountService accountService,
			final GuestService guestService,
			UserService userService,
			PasswordService passwordService) {
		super(accountService);
		this.guestService = guestService;
		this.userService = userService;
		this.passwordService = passwordService;
	}

	@Override
	public List<GuestDto> findAll(Boolean mine, String pattern) throws BusinessException {
		User authUser = checkAuthentication();
		User actor = getActor(authUser, null);
		List<Guest> guests = null;
		if (pattern == null) {
			guests = guestService.findAll(authUser, actor, mine);
		} else {
			guests = guestService.search(authUser, actor, pattern, mine);
		}
		return toGuestDto(guests);
	}

	@Override
	public GuestDto find(String actorUuid, String domain, String mail)
			throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notEmpty(mail, "Missing required guest mail");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		return GuestDto.getFull(guestService.find(authUser, actor, domain, mail));
	}

	@Override
	public List<GuestDto> findAll(String actorUuid) throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		List<GuestDto> res = Lists.newArrayList();
		List<Guest> guests = guestService.findAll(authUser, actor, null);
		for (Guest guest : guests) {
			res.add(GuestDto.getFull(guest));
		}
		return res;
	}

	@Override
	public List<GuestDto> search(UserSearchDto userSearchDto) throws BusinessException {
		User authUser = checkAuthentication();
		User actor = getActor(authUser, null);
		List<Guest> guests = guestService.search(authUser, actor, userSearchDto.getFirstName(),
				userSearchDto.getLastName(), userSearchDto.getMail(), true);
		return toGuestDto(guests);
	}

	@Override
	public GuestDto find(String actorUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "guest uuid is required");
		User authUser = checkAuthentication();
		return GuestDto.getFull(guestService.find(authUser, authUser, uuid));
	}

	@Override
	public GuestDto create(String actorUuid, GuestDto guestDto) throws BusinessException {
		Validate.notNull(guestDto, "guest dto is required");
		User authUser = checkAuthentication();
		Guest guest = guestDto.toUserObject();
		List<String> ac = null;
		if (guest.isRestricted()) {
			if (guestDto.getRestrictedContacts() != null) {
				ac = Lists.newArrayList();
				for (GenericUserDto contactDto : guestDto.getRestrictedContacts()) {
					ac.add(contactDto.getMail());
				}
			}
		}
		return GuestDto.getFull(guestService.create(authUser, authUser, guest, ac));
	}

	@Override
	public GuestDto update(String actorUuid, GuestDto dto, String uuid) throws BusinessException {
		Validate.notNull(dto, "guest dto is required");
		if (!Strings.isNullOrEmpty(uuid)) {
			dto.setUuid(uuid);
		}
		Validate.notEmpty(dto.getUuid(), "guest uuid is required");
		User authUser = checkAuthentication();
		Guest guest = dto.toUserObject();
		List<String> ac = Lists.newArrayList();
		if (guest.isRestricted()) {
			for (GenericUserDto contactDto : dto.getRestrictedContacts()) {
				ac.add(contactDto.getMail());
			}
		}
		return GuestDto.getFull(guestService.update(authUser, authUser, guest, ac));
	}

	@Override
	public GuestDto delete(String actorUuid, GuestDto guestDto, String uuid) throws BusinessException {
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		if (Strings.isNullOrEmpty(uuid)) {
			Validate.notNull(guestDto, "Missing required guest");
			Validate.notEmpty(guestDto.getUuid(), "Missing required guest uuid");
			uuid = guestDto.getUuid();
		}
		Guest guest = guestService.delete(authUser, actor, uuid);
		return GuestDto.getSimple(guest);
	}

	@Override
	public void resetPassword(GuestDto dto, String uuid) throws BusinessException {
		Validate.notNull(dto, "guest is required");
		if (!Strings.isNullOrEmpty(uuid)) {
			dto.setUuid(uuid);
		}
		Validate.notEmpty(dto.getUuid(), "guest uuid is required");
		guestService.triggerResetPassword(dto.getUuid());
	}

	private List<GuestDto> toGuestDto(List<Guest> col) {
		return ImmutableList.copyOf(Lists.transform(col, GuestDto.toDto()));
	}

	private void validatePasswordInputs(String password, String message) {
		String valid = Optional.ofNullable(password).orElse("");
		Validate.notEmpty(valid, message);
	}

	private User getAuthenticatedGuest() throws BusinessException {
		User authUser = checkAuthentication();
		if (!AccountType.GUEST.equals(authUser.getAccountType())) {
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"This service is used only for guests.");
		}
		return authUser;
	}

	@Override
	public void changePassword(PasswordDto password) {
		User authUser = getAuthenticatedGuest();
		User actor = getActor(authUser, null);
		Validate.notNull(password, "Password is required");
		validatePasswordInputs(password.getOldPwd(), "The old password is required");
		validatePasswordInputs(password.getNewPwd(), "The new password is required");
		userService.changePassword(authUser, actor, password.getOldPwd(),
				password.getNewPwd());
	}

	@Override
	public Map<String, Integer> getPasswordRules() throws BusinessException {
		checkAuthentication();
		return passwordService.getPasswordRules();
	}
}
