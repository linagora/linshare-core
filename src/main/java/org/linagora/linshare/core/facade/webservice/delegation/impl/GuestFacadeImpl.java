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
	public GuestDto find(String ownerUuid, String uuid)
			throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(uuid, "Missing required guest uuid");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		return GuestDto.getFull(guestService.find(actor, owner, uuid));
	}

	@Override
	public GuestDto find(String ownerUuid, String domain, String mail)
			throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(mail, "Missing required guest mail");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		return GuestDto.getFull(guestService.find(actor, owner, domain, mail));
	}

	@Override
	public List<GuestDto> findAll(String ownerUuid) throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		List<GuestDto> res = Lists.newArrayList();
		List<Guest> guests = guestService.findAll(actor, owner, null);
		for (Guest guest : guests) {
			res.add(GuestDto.getFull(guest));
		}
		return res;
	}

	@Override
	public GuestDto create(String ownerUuid, GuestDto guestDto)
			throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notNull(guestDto, "Missing required guest dto");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		Guest guest = guestDto.toUserObject();
		List<String> ac = Lists.newArrayList();
		if (guest.isRestricted()) {
			for (GenericUserDto contactDto : guestDto.getRestrictedContacts()) {
				ac.add(contactDto.getMail());
			}
		}
		return GuestDto.getFull(guestService.create(actor, owner, guest, ac));
	}

	@Override
	public GuestDto update(String ownerUuid, GuestDto guestDto)
			throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notNull(guestDto, "Missing required guest dto");
		Validate.notEmpty(guestDto.getUuid(), "Missing required guest dto uuid");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
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
		return GuestDto.getFull(guestService.update(actor, owner, guest, ac));
	}

	@Override
	public GuestDto delete(String ownerUuid, GuestDto guestDto)
			throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notNull(guestDto, "Missing required guest dto");
		Validate.notEmpty(guestDto.getUuid(), "Missing required guest dto uuid");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		return GuestDto.getFull(guestService.delete(actor, owner, guestDto.getUuid()));
	}

	@Override
	public GuestDto delete(String ownerUuid, String uuid) throws BusinessException {
		Validate.notEmpty(ownerUuid, "Missing required owner uuid");
		Validate.notEmpty(uuid, "Missing required guest uuid");
		User actor = checkAuthentication();
		User owner = getOwner(ownerUuid);
		return GuestDto.getFull(guestService.delete(actor, owner, uuid));
	}

}
