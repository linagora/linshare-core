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

import org.apache.commons.lang.Validate;
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
		User actor = checkAuthentication(dtc);
		User owner = getOwner(dtc);
		Validate.notNull(dtc.getFile(),
				"Missing required file (check parameter named file)");
		Validate.notEmpty(dtc.getFileName(), "Missing required file name");
		if ((owner.isGuest() && !owner.getCanUpload()))
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		DocumentEntry res = service.create(actor, owner,
				dtc.getFile(), dtc.getFileName(),
				dtc.getDescription(), false,
				dtc.getMetaData());
		return new DocumentDto(res);
	}

	@Override
	public DocumentDto update(DocumentTaskContext dtc) {
		Validate.notNull(dtc, "Missing dtc");
		User actor = checkAuthentication(dtc);
		User owner = getOwner(dtc);
		Validate.notNull(dtc.getFile(),
				"Missing required file (check parameter named file)");
		Validate.notEmpty(dtc.getFileName(), "Missing required file name");
		Validate.notNull(dtc.getDocEntryUuid(), "Missing docEntryUuid for the updated document");
		if ((owner.isGuest() && !owner.getCanUpload()))
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		DocumentEntry update = service.update(actor, owner, dtc.getDocEntryUuid(), dtc.getFile(), dtc.getFileName());
		return new DocumentDto(update);
	}
}
