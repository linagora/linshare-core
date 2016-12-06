/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2016. Contribute to
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
package org.linagora.linshare.core.facade.webservice.external.impl;

import org.apache.commons.lang.Validate;
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
		SystemAccount actor = service.getGuestSystemAccount();
		return service.find(actor, actor, uuid);
	}

	@Override
	public ResetGuestPassword update(String uuid, ResetGuestPassword reset) throws BusinessException {
		Validate.notNull(reset, "Missing ResetGuestPassword object");
		if (!Strings.isNullOrEmpty(uuid)) {
			reset.setUuid(uuid);
		}
		Validate.notEmpty(reset.getUuid(), "Missing ResetGuestPassword uuid");
		logger.debug("getting ResetGuestPassword with uuid : " + reset.getUuid());
		SystemAccount actor = service.getGuestSystemAccount();
		return service.update(actor, actor, reset);
	}

	@Override
	public void create(String domainUuid, ResetPasswordDto resetDto) throws BusinessException {
		SystemAccount actor = service.getGuestSystemAccount();
		guestService.triggerResetPassword(actor, resetDto.getMail(), domainUuid);
	}
}
