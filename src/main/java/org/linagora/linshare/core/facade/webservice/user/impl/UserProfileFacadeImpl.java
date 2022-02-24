/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
import java.util.Optional;
import java.util.stream.Collectors;

import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.AbstractUserProfileDto;
import org.linagora.linshare.core.facade.webservice.common.dto.FavouriteRecipientDto;
import org.linagora.linshare.core.facade.webservice.common.dto.GuestProfileDto;
import org.linagora.linshare.core.facade.webservice.common.dto.RestrictedContactDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UserProfileDto;
import org.linagora.linshare.core.facade.webservice.user.UserProfileFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.UserService;

public class UserProfileFacadeImpl extends UserGenericFacadeImp implements UserProfileFacade {

	private final UserService userService;

	public UserProfileFacadeImpl(AccountService accountService,
				UserService userService) {
		super(accountService);
		this.userService = userService;
	}

	@Override
	public AbstractUserProfileDto find() throws BusinessException {
		User authUser = checkAuthentication();
		if (authUser.isGuest()) {
			return GuestProfileDto.from(authUser, userService.findByLsUuid(authUser.getOwner().getLsUuid()));
		}
		return UserProfileDto.from(authUser);
	}

	@Override
	public AbstractUserProfileDto update(AbstractUserProfileDto dto) throws BusinessException {
		dto.validation();
		AbstractUserProfileDto storedDto = find();
		if (!storedDto.getAccountType().equals(dto.getAccountType())) {
			throw new BusinessException(BusinessErrorCode.USER_PROFILE_INCOMPATIBLE_ACCOUNT_TYPES, "Account type between stored and given user didn't match.");
		}
		if (!storedDto.equalsElseLocale(dto)) {
			throw new BusinessException(BusinessErrorCode.USER_PROFILE_ONLY_LOCALE_CAN_BE_MODIFIED, "Only the locale of the user can be modified.");
		}
		User user = userService.findByLsUuid(dto.getUuid());
		userService.updateUserLocale(user.getDomainId(), dto.getMail(), dto.getLocale().convert());
		return dto;
	}

	@Override
	public List<RestrictedContactDto> restrictedContacts() throws BusinessException {
		User authUser = checkAuthentication();
		if (!authUser.isGuest()) {
			throw new BusinessException(BusinessErrorCode.USER_PROFILE_INCOMPATIBLE_ACCOUNT_TYPES, "Only Guest have restricted contacts.");
		}
		return userService.findAllRestrictedContacts(authUser)
			.stream()
			.map(RestrictedContactDto::from)
			.collect(Collectors.toUnmodifiableList());
	}

	@Override
	public List<FavouriteRecipientDto> favouriteRecipients(Optional<String> mailFilter) throws BusinessException {
		User authUser = checkAuthentication();
		return userService.findRecipientFavourite(authUser)
			.stream()
			.filter(recipient -> {
				if (mailFilter.isPresent()) {
					return recipient.getRecipient().contains(mailFilter.get());
				}
				return true;
			})
			.map(FavouriteRecipientDto::from)
			.collect(Collectors.toUnmodifiableList());
	}
}
