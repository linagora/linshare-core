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
package org.linagora.linshare.core.facade.webservice.user.impl;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.DocumentAsyncFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AsyncTaskService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.webservice.userv1.task.context.DocumentTaskContext;

public class DocumentAsyncFacadeImpl extends
		GenericAsyncFacadeImpl implements DocumentAsyncFacade {

	private final DocumentEntryService service;

	public DocumentAsyncFacadeImpl(AccountService accountService,
			AsyncTaskService asyncStatusService,
			DocumentEntryService documentEntryService) {
		super(accountService, asyncStatusService);
		this.service = documentEntryService;
	}

	@Override
	public DocumentDto upload(DocumentTaskContext dtc) {
		Validate.notNull(dtc, "Missing dtc");
		User authUser = checkAuthentication(dtc);
		User actor = getActor(dtc);
		Validate.notNull(dtc.getFile(),
				"Missing required file (check parameter named file)");
		Validate.notEmpty(dtc.getFileName(), "Missing required file name");
		if ((actor.isGuest() && !actor.isCanUpload()))
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		DocumentEntry res = service.create(authUser, actor,
				dtc.getFile(), dtc.getFileName(),
				dtc.getDescription(), false,
				dtc.getMetaData());
		return new DocumentDto(res);
	}

	@Override
	public DocumentDto update(DocumentTaskContext dtc) {
		Validate.notNull(dtc, "Missing dtc");
		User authUser = checkAuthentication(dtc);
		User actor = getActor(dtc);
		Validate.notNull(dtc.getFile(),
				"Missing required file (check parameter named file)");
		Validate.notEmpty(dtc.getFileName(), "Missing required file name");
		Validate.notNull(dtc.getDocEntryUuid(), "Missing docEntryUuid for the updated document");
		if ((actor.isGuest() && !actor.isCanUpload()))
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		DocumentEntry update = service.update(authUser, actor, dtc.getDocEntryUuid(), dtc.getFile(), dtc.getFileName());
		return new DocumentDto(update);
	}
}
