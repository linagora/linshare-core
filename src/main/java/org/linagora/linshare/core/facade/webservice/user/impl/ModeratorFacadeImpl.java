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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Moderator;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.impl.AdminGenericFacadeImpl;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.AccountLightDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.ModeratorDto;
import org.linagora.linshare.core.facade.webservice.user.ModeratorFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.ModeratorService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;

public class ModeratorFacadeImpl extends AdminGenericFacadeImpl implements ModeratorFacade {

	private ModeratorService moderatorService;

	private AuditLogEntryService auditLogEntryService;

	private GuestService guestService;

	private UserService userService;

	public ModeratorFacadeImpl(
			AccountService accountService,
			ModeratorService moderatorService,
			AuditLogEntryService auditLogEntryService,
			GuestService guestService,
			UserService userService) {
		super(accountService);
		this.moderatorService = moderatorService;
		this.auditLogEntryService = auditLogEntryService;
		this.guestService = guestService;
		this.userService = userService;
	}

	@Override
	public ModeratorDto create(String actorUuid, String guestUuid, ModeratorDto dto) {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		Validate.notNull(dto, "Moderator to create should be set.");
		Validate.notNull(dto.getGuest(), "Moderator's guest should be set.");
		Validate.notEmpty(dto.getGuest().getUuid(), "Moderator's guest uuid should be set");
		Validate.notNull(dto.getRole(), "Moderator's role should be set.");
		Validate.notNull(dto.getAccount(), "Moderator's account should be set.");
		Validate.isTrue(!StringUtils.isBlank(dto.getAccount().getUuid())
						|| (!StringUtils.isBlank(dto.getAccount().getEmail())
						&& dto.getAccount().getDomain() != null
						&& !StringUtils.isBlank(dto.getAccount().getDomain().getUuid()))
				, "Either moderator's account uuid or moderator's email and domain uuid pair should be set");
		Account account = findOrCreateAccount(dto);
		Guest guest = (Guest) accountService.findAccountByLsUuid(dto.getGuest().getUuid());
		checkGuest(guestUuid, guest.getLsUuid());
		boolean onGuestCreation = false;
		Moderator moderator = moderatorService.create(authUser, actor, dto.toModeratorObject(dto, account, guest), onGuestCreation);
		return ModeratorDto.from(moderator);
	}

	private Account findOrCreateAccount(ModeratorDto dto) {
		AccountLightDto accountDto = dto.getAccount();
		Account account = null;

		if (!StringUtils.isBlank(accountDto.getUuid())) {
			account = accountService.findByLsUuid(accountDto.getUuid());
		}

		if (account == null
				&& accountDto.getDomain() != null
				&& !StringUtils.isBlank(accountDto.getDomain().getUuid())
				&& !StringUtils.isBlank(accountDto.getEmail())) {
			account = userService.findOrCreateUserWithDomainPolicies(accountDto.getEmail(), accountDto.getDomain().getUuid());
		}
		return account;
	}

	private void checkGuest(String guestInPath, String guestInDto) {
		if (!guestInPath.equals(guestInDto)) {
			throw new BusinessException(BusinessErrorCode.GUEST_MODERATOR_WRONG,
					"Please check the intered guest information.");
		}
	}

	@Override
	public ModeratorDto find(String actorUuid, String guestUuid, String uuid) {
		User authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		Moderator moderator = moderatorService.find(authUser, actor, uuid);
		Guest guest = (Guest) accountService.findAccountByLsUuid(moderator.getGuest().getLsUuid());
		checkGuest(guestUuid, guest.getLsUuid());
		return ModeratorDto.from(moderator);
	}

	@Override
	public ModeratorDto update(String actorUuid, String guestUuid, String uuid, ModeratorDto dto) {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		Validate.notNull(dto, "Moderator to update should be set.");
		Validate.notNull(dto.getRole(), "Moderator role should be set.");
		String dtoUuid = (Objects.nonNull(dto)) ? dto.getUuid() : null;
		String moderatorUuid = Optional.ofNullable(dtoUuid).orElse(uuid);
		Validate.notEmpty(moderatorUuid, "Moderator's uuid must be set");
		Moderator entity = moderatorService.find(authUser, actor, moderatorUuid);
		checkGuest(guestUuid, entity.getGuest().getLsUuid());
		Moderator moderatorToUpdate = moderatorService.update(authUser, actor, entity, dto);
		return ModeratorDto.from(moderatorToUpdate);
	}

	@Override
	public ModeratorDto delete(String actorUuid, String guestUuid, String uuid, ModeratorDto dto) {
		User authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		String dtoUuid = (Objects.nonNull(dto)) ? dto.getUuid() : null;
		String moderatorUuid = Optional.ofNullable(dtoUuid).orElse(uuid);
		Validate.notEmpty(moderatorUuid, "Moderator's uuid must be set");
		Moderator moderator = moderatorService.find(authUser, actor, moderatorUuid);
		checkGuest(guestUuid, moderator.getGuest().getLsUuid());
		return ModeratorDto.from(moderatorService.delete(authUser, actor, moderator));
	}

	@Override
	public List<ModeratorDto> findAllByGuest(String actorUuid, String guestUuid, ModeratorRole role, String pattern) {
		User authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		List<Moderator> moderators = moderatorService.findAllByGuest(authUser, actor, guestUuid, role, pattern);
		return moderators
				.stream()
				.map(ModeratorDto::from)
				.collect(Collectors.toUnmodifiableList());
	}

	@Override
	public Set<AuditLogEntryUser> findAllAudits(String actorUuid, String guestUuid, String moderatorUuid,
			List<LogAction> actions, List<AuditLogEntryType> types, String beginDate, String endDate) {
		Account authUser = checkAuthentication();
		Account actor = (User) getActor(authUser, actorUuid);
		Validate.notEmpty(guestUuid, "guestUuid required");
		Validate.notEmpty(moderatorUuid, "moderatorUuid required");
		Moderator moderator = moderatorService.find(authUser, actor, moderatorUuid);
		Guest guest = guestService.find(authUser, actor, moderator.getGuest().getLsUuid());
		checkGuest(guestUuid, guest.getLsUuid());
		return auditLogEntryService.findAllModeratorAudits(authUser, actor, moderator.getUuid(), actions, types, beginDate, endDate);
	}

}
