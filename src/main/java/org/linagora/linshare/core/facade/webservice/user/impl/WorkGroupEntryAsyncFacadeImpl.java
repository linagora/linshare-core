/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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
		if (workGroup == null) {
			throw new BusinessException(BusinessErrorCode.THREAD_NOT_FOUND,
					"Current thread was not found : " + tetc.getThreadUuid());
		}
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
