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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
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

	@SuppressWarnings("rawtypes")
	@Override
	public AbstractUserProfileDto find() throws BusinessException {
		User authUser = checkAuthentication();
		if (authUser.isGuest()) {
			return GuestProfileDto.from(authUser, userService.findByLsUuid(authUser.getLsUuid()));
		}
		return UserProfileDto.from(authUser);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		userService.updateUserMailLocale(user.getDomainId(), dto.getMail(), dto.getMailLocale().convertToLanguage());
		userService.updateUserExternalMailLocale(user.getDomainId(), dto.getMail(), dto.getExternalMailLocale().convertToLanguage());
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

	@Override
	public FavouriteRecipientDto removeFavouriteRecipient(String recipient) {
		User authUser = checkAuthentication();
		Validate.notEmpty(recipient, "'recipient' is mandatory.");

		FavouriteRecipientDto favouriteRecipientDto = userService.findRecipientFavourite(authUser)
				.stream()
				.filter(recipientFavourite -> recipientFavourite.getRecipient().equals(recipient))
				.findFirst()
				.map(FavouriteRecipientDto::from)
				.orElseThrow(() -> new BusinessException(BusinessErrorCode.FAVOURITE_RECIPIENT_NOT_FOUND, "Recipient not found."));

		userService.deleteRecipientFavourite(authUser, recipient);
		return favouriteRecipientDto;
	}
}
