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
package org.linagora.linshare.core.service.impl;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.GuestBusinessService;
import org.linagora.linshare.core.business.service.ModeratorBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Moderator;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.ModeratorResourceAccessControl;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.service.ModeratorService;

public class ModeratorServiceImpl extends GenericServiceImpl<Account, Moderator> implements ModeratorService {

	private ModeratorBusinessService moderatorBusinessService;

	private GuestBusinessService guestBusinessService;

	private GuestRepository guestRepository;

	public ModeratorServiceImpl(
			ModeratorResourceAccessControl rac,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
			ModeratorBusinessService moderatorBusinessService,
			GuestBusinessService guestBusinessService,
			GuestRepository guestRepository) {
		super(rac, sanitizerInputHtmlBusinessService);
		this.moderatorBusinessService = moderatorBusinessService;
		this.guestBusinessService = guestBusinessService;
		this.guestRepository = guestRepository;
	}

	@Override
	public Moderator create(Account authUser, Account actor, Moderator moderator) {
		preChecks(authUser, actor);
		Validate.notNull(moderator, "Moderator must be set.");
		Validate.notNull(moderator.getAccount(), "Moderator's account should be set");
		Validate.notNull(moderator.getGuest(), "Moderator's guest should be set");
		Validate.notEmpty(moderator.getGuest().getLsUuid(), "Guest's uuid must be set");
		checkCreatePermission(authUser, actor, Moderator.class, BusinessErrorCode.GUEST_MODERATOR_CANNOT_CREATE, moderator);
		Guest guest = guestBusinessService.findByLsUuid(moderator.getGuest().getLsUuid());
		List<Moderator> moderators = findAllByGuest(authUser, actor, guest.getLsUuid());
		if (moderators.contains(moderator)) {
			throw new BusinessException(BusinessErrorCode.GUEST_MODERATOR_ALREADY_EXISTS, "Moderator already exists.");
		}
		moderator = moderatorBusinessService.create(moderator);
		guest.addModerator(moderator);
		guestRepository.update(guest);
		return moderator;
	}

	@Override
	public Moderator find(Account authUser, Account actor, String uuid) {
		preChecks(authUser, actor);
		Validate.notEmpty(uuid, "Moderator uuid must be set.");
		Moderator moderator = moderatorBusinessService.find(uuid);
		checkReadPermission(authUser, actor, Moderator.class, BusinessErrorCode.GUEST_MODERATOR_CANNOT_GET, moderator);
		return moderator;
	}

	@Override
	public Moderator update(Account authUser, Account actor, Moderator moderator) {
		preChecks(authUser, actor);
		Validate.notNull(moderator, "Moderator to update must be set.");
		Validate.notNull(moderator.getRole(), "Moderator role must be set.");
		Validate.notNull(moderator.getGuest(), "Moderator's guest should be set");
		Validate.notEmpty(moderator.getGuest().getLsUuid(), "Guest's uuid must be set");
		checkUpdatePermission(authUser, actor, Moderator.class, BusinessErrorCode.GUEST_MODERATOR_CANNOT_UPDATE, moderator);
		moderator = moderatorBusinessService.update(moderator);
		return moderator;
	}

	@Override
	public Moderator delete(Account authUser, Account actor, Moderator moderator) {
		preChecks(authUser, actor);
		Validate.notNull(moderator, "Moderator must be set.");
		Validate.notNull(moderator.getGuest(), "Moderator must be set.");
		Validate.notEmpty(moderator.getGuest().getLsUuid(), "Guest's uuid must be set");
		checkDeletePermission(authUser, actor, Moderator.class, BusinessErrorCode.GUEST_MODERATOR_CANNOT_DELETE, moderator);
		Guest guest = guestBusinessService.findByLsUuid(moderator.getGuest().getLsUuid());
		moderatorBusinessService.delete(moderator);
		guest.removeModerator(moderator);
		guestRepository.update(guest);
		return moderator;
	}

	@Override
	public List<Moderator> findAllByGuest(Account authUser, Account actor, String guestUuid) {
		preChecks(authUser, actor);
		Validate.notEmpty(guestUuid, "Guest's uuid must be set.");
		checkListPermission(actor, actor, Moderator.class, BusinessErrorCode.GUEST_MODERATORS_CANNOT_GET, null);
		Guest guest = guestBusinessService.findByLsUuid(guestUuid);
		List<Moderator> moderators = moderatorBusinessService.findAllByGuest(guest);
		return moderators;
	}
}
