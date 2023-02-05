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
import org.linagora.linshare.core.domain.entities.MimeType;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MimeTypeFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.MimeTypeDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.MimeTypeService;

public class MimeTypeFacadeImpl extends AdminGenericFacadeImpl implements
		MimeTypeFacade {

	private final MimeTypeService mimeTypeService;

	public MimeTypeFacadeImpl(final AccountService accountService,
			final MimeTypeService MimeTypeService) {

		super(accountService);
		this.mimeTypeService = MimeTypeService;
	}

	@Override
	public MimeTypeDto find(String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(uuid, "MimeType uuid must be set.");
		return new MimeTypeDto(mimeTypeService.find(authUser, uuid));
	}

	@Override
	public MimeTypeDto update(MimeTypeDto dto) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notNull(dto, "MimeType dto must be set.");
		Validate.notEmpty(dto.getUuid(), "MimeType uuid must be set.");
		MimeType MimeType = mimeTypeService.update(authUser, new MimeType(dto));
		return new MimeTypeDto(MimeType);
	}

}
