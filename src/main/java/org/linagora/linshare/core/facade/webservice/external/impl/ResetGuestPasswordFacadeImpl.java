/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
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
