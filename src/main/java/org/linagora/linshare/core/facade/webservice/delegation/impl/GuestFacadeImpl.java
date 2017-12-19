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

import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.GenericUserDto;
import org.linagora.linshare.core.facade.webservice.common.dto.GuestDto;
import org.linagora.linshare.core.facade.webservice.delegation.GuestFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.UserService;

import com.google.common.collect.Lists;

public class GuestFacadeImpl extends DelegationGenericFacadeImpl implements
		GuestFacade {

	private GuestService guestService;

	public GuestFacadeImpl(
			final AccountService accountService,
			final UserService userService,
			final GuestService guestService) {
		super(accountService, userService);
		this.guestService = guestService;
	}

	@Override
	public GuestDto find(String actorUuid, String uuid)
			throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notEmpty(uuid, "Missing required guest uuid");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		return GuestDto.getFull(guestService.find(authUser, actor, uuid));
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
	public GuestDto create(String actorUuid, GuestDto guestDto)
			throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notNull(guestDto, "Missing required guest dto");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		Guest guest = guestDto.toUserObject();
		List<String> ac = Lists.newArrayList();
		if (guest.isRestricted()) {
			for (GenericUserDto contactDto : guestDto.getRestrictedContacts()) {
				ac.add(contactDto.getMail());
			}
		}
		return GuestDto.getFull(guestService.create(authUser, actor, guest, ac));
	}

	@Override
	public GuestDto update(String actorUuid, GuestDto guestDto)
			throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notNull(guestDto, "Missing required guest dto");
		Validate.notEmpty(guestDto.getUuid(), "Missing required guest dto uuid");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
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
		return GuestDto.getFull(guestService.update(authUser, actor, guest, ac));
	}

	@Override
	public GuestDto delete(String actorUuid, GuestDto guestDto)
			throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notNull(guestDto, "Missing required guest dto");
		Validate.notEmpty(guestDto.getUuid(), "Missing required guest dto uuid");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		return GuestDto.getFull(guestService.delete(authUser, actor, guestDto.getUuid()));
	}

	@Override
	public GuestDto delete(String actorUuid, String uuid) throws BusinessException {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notEmpty(uuid, "Missing required guest uuid");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		return GuestDto.getFull(guestService.delete(authUser, actor, uuid));
	}

}
