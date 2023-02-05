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
import org.linagora.linshare.core.domain.constants.TargetKind;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.CopyResource;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupEntryDto;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupLightDto;
import org.linagora.linshare.core.facade.webservice.user.WorkGroupEntryAsyncFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AsyncTaskService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.mto.CopyMto;
import org.linagora.linshare.webservice.userv1.task.context.WorkGroupEntryTaskContext;

public class WorkGroupEntryAsyncFacadeImpl extends GenericAsyncFacadeImpl implements WorkGroupEntryAsyncFacade {

	private final WorkGroupNodeService service;

	private final DocumentEntryService documentEntryService;

	private final ThreadService threadService;

	public WorkGroupEntryAsyncFacadeImpl(AccountService accountService,
			AsyncTaskService asyncTaskService,
			ThreadService threadService,
			DocumentEntryService documentEntryService,
			WorkGroupNodeService service) {
		super(accountService, asyncTaskService);
		this.service = service;
		this.threadService = threadService;
		this.documentEntryService = documentEntryService;
	}

	@Override
	public WorkGroupEntryDto upload(WorkGroupEntryTaskContext tetc) {
		User authUser = checkAuthentication(tetc);
		User actor = getActor(tetc);
		Validate.notNull(tetc.getFile(),
				"Missing required file (check parameter named file)");
		Validate.notEmpty(tetc.getThreadUuid(), "Missing required thread uuid");
		Validate.notEmpty(tetc.getFileName(), "Missing required file name");
		WorkGroup workGroup = threadService.find(authUser, actor, tetc.getThreadUuid());
		WorkGroupNode node = service.create(authUser, actor, workGroup, tetc.getFile(), tetc.getFileName(), tetc.getWorkGroupFolderUuid(), tetc.getStrictModeActivated());
		WorkGroupEntryDto dto = new WorkGroupEntryDto((WorkGroupDocument) node);
		dto.setWorkGroup(new WorkGroupLightDto(workGroup));
		return dto;
	}

	@Override
	public WorkGroupEntryDto copy(WorkGroupEntryTaskContext tetc) {
		User authUser = checkAuthentication(tetc);
		User actor = getActor(tetc);
		Validate.notNull(tetc, "Missing WorkGroupEntryTaskContext");
		Validate.notEmpty(tetc.getActorUuid(), "Missing required actor uuid");
		Validate.notEmpty(tetc.getThreadUuid(), "Missing required thread uuid");
		Validate.notEmpty(tetc.getDocEntryUuid(), "Missing required document entry uuid");
		// Check if we have the right to access to the specified thread
		WorkGroup workGroup = threadService.find(authUser, actor, tetc.getThreadUuid());
		// Check if we have the right to download the specified document entry
		DocumentEntry de = documentEntryService.findForDownloadOrCopyRight(authUser, actor, tetc.getDocEntryUuid());
		CopyResource cr = new CopyResource(TargetKind.PERSONAL_SPACE, de);
		WorkGroupNode node = service.copy(authUser, actor, workGroup, null, cr);
		documentEntryService.markAsCopied(authUser, actor, de, new CopyMto(node, workGroup));
		WorkGroupEntryDto dto = new WorkGroupEntryDto((WorkGroupDocument) node);
		dto.setWorkGroup(new WorkGroupLightDto(workGroup));
		return dto;
	}
}
