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
package org.linagora.linshare.core.facade.webservice.admin.impl;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.UtilGuestAuthor;
import org.linagora.linshare.core.facade.webservice.admin.GuestFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.GuestDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.GuestService;
import org.springframework.data.mongodb.core.MongoTemplate;

public class GuestFacadeImpl extends AdminGenericFacadeImpl implements
		GuestFacade {

	private final GuestService guestService;

	private final UtilGuestAuthor utilGuestAuthor;

	public GuestFacadeImpl(
			final AccountService accountService,
			final GuestService guestService,
			final MongoTemplate mongoTemplate
			) {
		super(accountService);
		this.guestService = guestService;
		this.utilGuestAuthor = new UtilGuestAuthor(mongoTemplate);
	}

	@Override
	public GuestDto find(String uuid) throws BusinessException {
		User currentUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(uuid, "Guest uuid must be set.");
		Guest guest = guestService.find(currentUser, currentUser, uuid);
		return GuestDto.getFull(guest, utilGuestAuthor.getAuthor(uuid));
	}

}
