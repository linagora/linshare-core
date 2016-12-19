/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.GenericUserDto;
import org.linagora.linshare.core.facade.webservice.common.dto.GuestDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UserSearchDto;
import org.linagora.linshare.core.facade.webservice.user.GuestFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.GuestService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class GuestFacadeImpl extends UserGenericFacadeImp implements
		GuestFacade {

	private final GuestService guestService;

	public GuestFacadeImpl(final AccountService accountService,
			final GuestService guestService) {
		super(accountService);
		this.guestService = guestService;
	}

	@Override
	public List<GuestDto> findAll(Boolean mine, String pattern) throws BusinessException {
		User actor = checkAuthentication();
		User owner = getOwner(actor, null);
		List<Guest> guests = null;
		if (pattern == null) {
			guests = guestService.findAll(actor, owner, mine);
		} else {
			guests = guestService.search(actor, owner, pattern, mine);
		}
		return toGuestDto(guests);
	}

	@Override
	public List<GuestDto> search(UserSearchDto userSearchDto) throws BusinessException {
		User actor = checkAuthentication();
		User owner = getOwner(actor, null);
		List<Guest> guests = guestService.search(actor, owner, userSearchDto.getFirstName(),
				userSearchDto.getLastName(), userSearchDto.getMail(), true);
		return toGuestDto(guests);
	}

	@Override
	public GuestDto find(String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "guest uuid is required");
		User actor = checkAuthentication();
		return GuestDto.getFull(guestService.find(actor, actor, uuid));
	}

	@Override
	public GuestDto create(GuestDto guestDto) throws BusinessException {
		Validate.notNull(guestDto, "guest dto is required");
		User actor = checkAuthentication();
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
		return GuestDto.getFull(guestService.create(actor, actor, guest, ac));
	}

	@Override
	public GuestDto update(GuestDto dto, String uuid) throws BusinessException {
		Validate.notNull(dto, "guest dto is required");
		if (!Strings.isNullOrEmpty(uuid)) {
			dto.setUuid(uuid);
		}
		Validate.notEmpty(dto.getUuid(), "guest uuid is required");
		User actor = checkAuthentication();
		Guest guest = dto.toUserObject();
		List<String> ac = Lists.newArrayList();
		if (guest.isRestricted()) {
			for (GenericUserDto contactDto : dto.getRestrictedContacts()) {
				ac.add(contactDto.getMail());
			}
		}
		return GuestDto.getFull(guestService.update(actor, actor, guest, ac));
	}

	@Override
	public GuestDto delete(GuestDto guestDto) throws BusinessException {
		Validate.notNull(guestDto, "guest dto is required");
		Validate.notEmpty(guestDto.getUuid(), "guest uuid is required");
		User actor = checkAuthentication();
		Guest guest = guestService.delete(actor, actor, guestDto.getUuid());
		return GuestDto.getSimple(guest);
	}

	@Override
	public GuestDto delete(String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "guest uuid is required");
		User actor = checkAuthentication();
		Guest guest = guestService.delete(actor, actor, uuid);
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
}
