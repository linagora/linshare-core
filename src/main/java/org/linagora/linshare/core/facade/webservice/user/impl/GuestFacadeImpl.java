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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.PasswordService;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountContactLists;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Moderator;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.UtilGuestAuthor;
import org.linagora.linshare.core.facade.webservice.common.dto.AccountContactListDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ContactListDto;
import org.linagora.linshare.core.facade.webservice.common.dto.GenericUserDto;
import org.linagora.linshare.core.facade.webservice.common.dto.GuestDto;
import org.linagora.linshare.core.facade.webservice.common.dto.GuestModeratorRole;
import org.linagora.linshare.core.facade.webservice.common.dto.ModeratorRoleEnum;
import org.linagora.linshare.core.facade.webservice.common.dto.PasswordDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UserSearchDto;
import org.linagora.linshare.core.facade.webservice.user.GuestFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.core.service.impl.ModeratorServiceImpl;
import org.linagora.linshare.utils.Version;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import javax.annotation.Nonnull;

public class GuestFacadeImpl extends GenericFacadeImpl implements
		GuestFacade {

	private final GuestService guestService;

	private final UserService userService;

	private final PasswordService passwordService;

	private final ModeratorServiceImpl moderatorService;

	private final UtilGuestAuthor utilGuestAuthor;

	public GuestFacadeImpl(final AccountService accountService,
			final GuestService guestService,
			UserService userService,
			PasswordService passwordService,
			ModeratorServiceImpl moderatorService,
			MongoTemplate mongoTemplate) {
		super(accountService);
		this.guestService = guestService;
		this.userService = userService;
		this.passwordService = passwordService;
		this.moderatorService = moderatorService;
		this.utilGuestAuthor = new UtilGuestAuthor(mongoTemplate);
	}

	@Override
	public List<GuestDto> findAll(Version version, String pattern, String role)
			throws BusinessException {
		User authUser = checkAuthentication();
		User actor = getActor(authUser, null);
		Optional<User> moderator = Optional.empty();
		Optional<ModeratorRole> moderatorRole = Optional.empty();
		if (!Strings.isNullOrEmpty(role)) {
			ModeratorRoleEnum roleEnum = ModeratorRoleEnum.fromString(role);
			moderator = Optional.of(actor);
			if(!roleEnum.equals(ModeratorRoleEnum.ALL)) {
				moderatorRole = Optional.of(ModeratorRoleEnum.toModeratorRole(roleEnum));
			}
		}
		List<Guest> guests = guestService.findAll(authUser, actor, moderator,
				Optional.ofNullable(pattern), moderatorRole);
		return toDtoList(version, authUser, actor, guests);
	}

	private List<GuestDto> toDtoList(Version version, Account authUser, Account actor, List<Guest> guests) {
		List<GuestDto> guestsWithOwners = Lists.newArrayList();
		for (Guest guest : guests) {
			GuestDto dto = GuestDto.getFull(guest, utilGuestAuthor.getFakeAuthor());
			guestsWithOwners.add(
				addModeratorRoletoGuestDto(version, authUser, actor, guest, dto)
			);
		}
		return guestsWithOwners;
	}

	/**
	 * Only used by delegation api. to be removed
	 */
	@Deprecated
	@Override
	public GuestDto find(String actorUuid, String domain, String mail)
			throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notEmpty(mail, "Missing required guest mail");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		Guest guest = guestService.find(authUser, actor, domain, mail);
		GuestDto dto = GuestDto.getFull(guest, utilGuestAuthor.getAuthor(guest.getLsUuid()));
		return dto;
	}

	/**
	 * Only used by delegation api. to be removed
	 */
	@Deprecated
	@Override
	public List<GuestDto> findAll(String actorUuid) throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		List<GuestDto> res = Lists.newArrayList();
		// Here we assume that we need to filter actor own guest (since delegation api is design to act like user api)
		Optional<User> moderator = Optional.of(actor);
		List<Guest> guests = guestService.findAll(authUser, actor, moderator, Optional.empty(), Optional.empty());
		for (Guest guest : guests) {
			res.add(GuestDto.getFull(guest, utilGuestAuthor.getAuthor(guest.getLsUuid())));
		}
		return res;
	}

	@Override
	public List<GuestDto> search(Version version, UserSearchDto userSearchDto) throws BusinessException {
		User authUser = checkAuthentication();
		User actor = getActor(authUser, null);
		Optional<String> pattern = Optional.ofNullable(userSearchDto.getMail());
		if (pattern.isEmpty()) {
			pattern = Optional.ofNullable(userSearchDto.getFirstName());
		}
		String patternStr = pattern.orElse(userSearchDto.getLastName());
		List<Guest> guests = guestService.findAll(authUser, actor, Optional.empty(), Optional.ofNullable(patternStr), Optional.empty());
		return toDtoList(version, authUser, actor, guests);
	}

	@Override
	public GuestDto find(Version version, String actorUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "guest uuid is required");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, null);
		Guest guest = guestService.find(authUser, actor, uuid);
		GuestDto dto = GuestDto.getFull(guest, utilGuestAuthor.getAuthor(guest.getLsUuid()));
		return addModeratorRoletoGuestDto(version, authUser, actor, guest, dto);
	}

	@Override
	public GuestDto create(Version version, String actorUuid, GuestDto guestDto) throws BusinessException {
		Validate.notNull(guestDto, "guest dto is required");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		Guest guest = guestDto.toUserObject();
		List<String> ac = null;
		List<String> contactUuid = null;
		if (guest.isRestricted()) {
			if (guestDto.getRestrictedContacts() != null) {
				ac = Lists.newArrayList();
				for (GenericUserDto contactDto : guestDto.getRestrictedContacts()) {
					ac.add(contactDto.getMail());
				}
			}
		}
			if (guestDto.getRestrictedContactList() != null) {
				contactUuid = Lists.newArrayList();
				for (ContactListDto contactListDto : guestDto.getRestrictedContactList()) {
					contactUuid.add(contactListDto.getUuid());
				}
			}

		guest = guestService.create(authUser, actor, guest, ac, contactUuid);
		GuestDto dto = GuestDto.getFull(guest, utilGuestAuthor.getAuthor(guest.getLsUuid()));
		return addModeratorRoletoGuestDto(version, authUser, actor, guest, dto);
	}

	@Override
	public GuestDto update(Version version, String actorUuid, GuestDto dtoIn, String uuid) throws BusinessException {
		Validate.notNull(dtoIn, "guest dto is required");
		if (!Strings.isNullOrEmpty(uuid)) {
			dtoIn.setUuid(uuid);
		}
		Validate.notEmpty(dtoIn.getUuid(), "guest uuid is required");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, null);
		Guest guest = dtoIn.toUserObject();
		List<String> ac = Lists.newArrayList();
		List<String> contactUuid = Lists.newArrayList();
		if (guest.isRestricted()) {
			for (GenericUserDto contactDto : dtoIn.getRestrictedContacts()) {
				ac.add(contactDto.getMail());
			}
		}
			for (ContactListDto contactListDto: dtoIn.getRestrictedContactList()) {
				contactUuid.add(contactListDto.getUuid());
			}
		logger.info("ancien contact list: {}", guest.getRestrictedContactLists());
		guest = guestService.update(authUser, authUser, guest, ac, contactUuid);
		logger.info("new contact list: {}", guest.getRestrictedContactLists());
		GuestDto dto = GuestDto.getFull(guest, utilGuestAuthor.getAuthor(guest.getLsUuid()));
		return addModeratorRoletoGuestDto(version, authUser, actor, guest, dto);
	}

	@Override
	public GuestDto delete(Version version, String actorUuid, GuestDto guestDto, String uuid) throws BusinessException {
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		if (Strings.isNullOrEmpty(uuid)) {
			Validate.notNull(guestDto, "Missing required guest");
			Validate.notEmpty(guestDto.getUuid(), "Missing required guest uuid");
			uuid = guestDto.getUuid();
		}
		Guest guest = guestService.delete(authUser, actor, uuid);
		GuestDto dto = GuestDto.getFull(guest, utilGuestAuthor.getAuthor(guest.getLsUuid()));
		return addModeratorRoletoGuestDto(version, authUser, actor, guest, dto);
	}

	private GuestDto addModeratorRoletoGuestDto(Version version, Account authUser, Account actor, Guest guest, GuestDto dto) {
		if(version.isGreaterThanOrEquals(Version.V5)) {
			dto.setMyRole(GuestModeratorRole.NONE);
			final Optional<Moderator> moderator = moderatorService.findByActorAndGuest(authUser, actor, guest);
			if (moderator.isPresent()) {
				dto.setMyModeratorRole(moderator.get().getRole());
			}
		}
		return dto;
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

	@Override
	public @Nonnull List<AccountContactListDto> findContactListsByGuest(@Nonnull final Version version, @Nonnull final String uuid)
			throws BusinessException {
		Validate.notNull(uuid,"uuid is required");
		List<AccountContactLists> accountContactLists = accountService.findAccountContactListsByAccount(uuid);
		logger.info("allowed contact list guest",accountContactLists);
		return new ArrayList<>(Lists.transform(accountContactLists, AccountContactListDto.toDto()));
	}

}
