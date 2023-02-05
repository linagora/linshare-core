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
package org.linagora.linshare.core.facade.webservice.external.impl;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ResetPasswordDto;
import org.linagora.linshare.core.facade.webservice.external.ResetGuestPasswordFacade;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.ResetGuestPasswordService;
import org.linagora.linshare.mongo.entities.ResetGuestPassword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class ResetGuestPasswordFacadeImpl implements ResetGuestPasswordFacade {

	protected static final Logger logger = LoggerFactory.getLogger(ResetGuestPasswordFacadeImpl.class);

	protected final ResetGuestPasswordService service;

	protected final GuestService guestService;

	public ResetGuestPasswordFacadeImpl(ResetGuestPasswordService service, GuestService guestService) {
		super();
		this.service = service;
		this.guestService = guestService;
	}

	@Override
	public ResetGuestPassword find(String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Missing ResetGuestPassword uuid");
		logger.debug("getting ResetGuestPassword with uuid : " + uuid);
		SystemAccount authUser = service.getGuestSystemAccount();
		return service.find(authUser, authUser, uuid);
	}

	@Override
	public ResetGuestPassword update(String uuid, ResetGuestPassword reset) throws BusinessException {
		Validate.notNull(reset, "Missing ResetGuestPassword object");
		if (!Strings.isNullOrEmpty(uuid)) {
			reset.setUuid(uuid);
		}
		Validate.notEmpty(reset.getUuid(), "Missing ResetGuestPassword uuid");
		logger.debug("getting ResetGuestPassword with uuid : " + reset.getUuid());
		SystemAccount authUser = service.getGuestSystemAccount();
		return service.update(authUser, authUser, reset);
	}

	@Override
	public void create(String domainUuid, ResetPasswordDto resetDto) throws BusinessException {
		SystemAccount authUser = service.getGuestSystemAccount();
		guestService.triggerResetPassword(authUser, resetDto.getMail(), domainUuid);
	}
}
