/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2022. Contribute to
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
package org.linagora.linshare.core.facade.webservice.adminv5.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Moderator;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.impl.AdminGenericFacadeImpl;
import org.linagora.linshare.core.facade.webservice.adminv5.ModeratorFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.ModeratorDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ModeratorService;

import com.google.common.base.Strings;

public class ModeratorFacadeImpl extends AdminGenericFacadeImpl implements ModeratorFacade {

	private ModeratorService moderatorService;
	
	public ModeratorFacadeImpl(
			AccountService accountService,
			ModeratorService moderatorService) {
		super(accountService);
		this.moderatorService = moderatorService;
	}

	@Override
	public ModeratorDto create(String actorUuid, String guestUuid, ModeratorDto dto) {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		Validate.notNull(dto, "Moderator to create should be set.");
		Validate.notNull(dto.getAccount(), "Moderator's account should be set.");
		Validate.notEmpty(dto.getAccount().getUuid(), "Moderator's account uuid should be set");
		Validate.notNull(dto.getGuest(), "Moderator's guest should be set.");
		Validate.notEmpty(dto.getGuest().getUuid(), "Moderator's guest uuid should be set");
		Validate.notNull(dto.getRole(), "Moderator's role should be set.");
		Account account = accountService.findAccountByLsUuid(dto.getAccount().getUuid());
		Guest guest = (Guest) accountService.findAccountByLsUuid(dto.getGuest().getUuid());
		checkGuest(guestUuid, guest.getLsUuid());
		Moderator moderator = moderatorService.create(authUser, actor, dto.toModeratorObject(dto, account, guest));
		return ModeratorDto.from(moderator);
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
		String moderatorUuid = Optional.ofNullable(Strings.emptyToNull(uuid)).orElse(dto.getUuid());
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
		String moderatorUuid = Optional.ofNullable(Strings.emptyToNull(uuid)).orElse(dto.getUuid());
		Validate.notEmpty(moderatorUuid, "Moderator's uuid must be set");
		Moderator moderator = moderatorService.find(authUser, actor, moderatorUuid);
		checkGuest(guestUuid, moderator.getGuest().getLsUuid());
		return ModeratorDto.from(moderatorService.delete(authUser, actor, moderator));
	}

	@Override
	public List<ModeratorDto> findAllByGuest(String actorUuid, String guestUuid) {
		User authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		List<Moderator> moderators = moderatorService.findAllByGuest(authUser, actor, guestUuid);
		return moderators
				.stream()
				.map(ModeratorDto::from)
				.collect(Collectors.toUnmodifiableList());
	}

}
