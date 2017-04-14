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
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupEntryDto;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupLightDto;
import org.linagora.linshare.core.facade.webservice.user.ThreadEntryAsyncFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AsyncTaskService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.webservice.userv1.task.context.ThreadEntryTaskContext;

public class ThreadEntryAsyncFacadeImpl extends GenericAsyncFacadeImpl implements ThreadEntryAsyncFacade {

	private final WorkGroupNodeService service;

	private final DocumentEntryService documentEntryService;

	private final ThreadService threadService;

	public ThreadEntryAsyncFacadeImpl(AccountService accountService,
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
	public WorkGroupEntryDto upload(ThreadEntryTaskContext tetc) {
		User actor = checkAuthentication(tetc);
		User owner = getOwner(tetc);
		Validate.notNull(tetc.getFile(),
				"Missing required file (check parameter named file)");
		Validate.notEmpty(tetc.getThreadUuid(), "Missing required thread uuid");
		Validate.notEmpty(tetc.getFileName(), "Missing required file name");
		Thread thread = threadService.find(actor, owner, tetc.getThreadUuid());
		if (thread == null) {
			throw new BusinessException(BusinessErrorCode.THREAD_NOT_FOUND,
					"Current thread was not found : " + tetc.getThreadUuid());
		}
		WorkGroupNode node = service.create(actor, owner, thread, tetc.getFile(), tetc.getFileName(), tetc.getWorkGroupFolderUuid(), false);
		WorkGroupEntryDto dto = new WorkGroupEntryDto((WorkGroupDocument) node);
		dto.setWorkGroup(new WorkGroupLightDto(thread));
		return dto;
	}

	@Override
	public WorkGroupEntryDto copy(ThreadEntryTaskContext tetc) {
		User actor = checkAuthentication(tetc);
		User owner = getOwner(tetc);
		Validate.notNull(tetc, "Missing ThreadEntryTaskContext");
		Validate.notEmpty(tetc.getOwnerUuid(), "Missing required owner uuid");
		Validate.notEmpty(tetc.getThreadUuid(), "Missing required thread uuid");
		Validate.notEmpty(tetc.getDocEntryUuid(), "Missing required document entry uuid");
		// Check if we have the right to access to the specified thread
		Thread thread = threadService.find(actor, owner, tetc.getThreadUuid());
		// Check if we have the right to access to the specified document entry
		DocumentEntry doc = documentEntryService.find(actor, owner, tetc.getDocEntryUuid());
		// Check if we have the right to download the specified document entry
		documentEntryService.checkDownloadPermission(actor, owner, tetc.getDocEntryUuid());
		WorkGroupNode node = service.copy(actor, owner, thread, doc, null);
		WorkGroupEntryDto dto = new WorkGroupEntryDto((WorkGroupDocument) node);
		dto.setWorkGroup(new WorkGroupLightDto(thread));
		return dto;
	}
}
