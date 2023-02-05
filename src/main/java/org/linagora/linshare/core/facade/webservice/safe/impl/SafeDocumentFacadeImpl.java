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
package org.linagora.linshare.core.facade.webservice.safe.impl;

import java.io.File;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupEntryDto;
import org.linagora.linshare.core.facade.webservice.safe.SafeDocumentFacade;
import org.linagora.linshare.core.facade.webservice.user.impl.GenericFacadeImpl;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.SafeDetailService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.mongo.entities.SafeDetail;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupNode;

public class SafeDocumentFacadeImpl extends GenericFacadeImpl
		implements SafeDocumentFacade {

	private final SafeDetailService safeDetailService;

	private final ThreadService threadService;

	private final WorkGroupNodeService workGroupNodeService;

	public SafeDocumentFacadeImpl(SafeDetailService safeDetailService,
			ThreadService threadService,
			WorkGroupNodeService workGroupNodeService,
			final AccountService accountService) {
		super(accountService);
		this.threadService = threadService;
		this.workGroupNodeService = workGroupNodeService;
		this.safeDetailService = safeDetailService;
	}

	@Override
	public SafeDetail findSafeDetail(String actorUuid, String safeUuid) throws BusinessException {
		Validate.notEmpty(safeUuid);
		User authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		SafeDetail safeDetail = safeDetailService.find(authUser, actor, safeUuid);
		Validate.notNull(safeDetail);
		return safeDetail;
	}

	@Override
	public User findUser(String safeUuid) throws BusinessException {
		Validate.notEmpty(safeUuid);
		User authUser = checkAuthentication();
		Validate.notNull(authUser);
		return authUser;
	}

	@Override
	public WorkGroupEntryDto create(String actorUuid, String workgroupUuid,
			File file, String fileName, Boolean strict) {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		Validate.notEmpty(workgroupUuid, "Missing required thread uuid");
		Validate.notNull(file, "Missing required file");
		Validate.notNull(fileName, "Missing required fileName");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		WorkGroup workGroup = threadService.find(authUser, actor, workgroupUuid);
		WorkGroupNode node = workGroupNodeService.create(authUser, actor, workGroup, file, fileName, null, strict);
		WorkGroupEntryDto dto = new WorkGroupEntryDto((WorkGroupDocument)node);
		// why ?
//		dto.setWorkGroup(new WorkGroupLightDto(thread));
		return dto;
	}
}
