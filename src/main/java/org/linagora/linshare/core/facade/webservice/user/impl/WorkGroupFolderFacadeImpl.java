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

import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.WorkGroupFolderFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.WorkGroupFolderService;
import org.linagora.linshare.mongo.entities.WorkGroupFolder;

public class WorkGroupFolderFacadeImpl extends UserGenericFacadeImp implements WorkGroupFolderFacade {

	protected final ThreadService threadService;

	protected final WorkGroupFolderService service;

	public WorkGroupFolderFacadeImpl(AccountService accountService,
			WorkGroupFolderService service,
			ThreadService threadService) {
		super(accountService);
		this.service = service;
		this.threadService = threadService;
	}

	@Override
	public List<WorkGroupFolder> findAll(String ownerUuid, String workGroupUuid) throws BusinessException {
		Validate.notEmpty(workGroupUuid, "Missing required workGroup uuid");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		Thread workGroup = threadService.find(actor, owner, workGroupUuid);
		return service.findAll(actor, owner, workGroup);
	}

	@Override
	public WorkGroupFolder find(String ownerUuid, String workGroupUuid, String workGroupFolderUuid)
			throws BusinessException {
		Validate.notEmpty(workGroupUuid, "Missing required workGroup uuid");
		Validate.notEmpty(workGroupFolderUuid, "Missing required workGroup folder uuid");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		Thread workGroup = threadService.find(actor, owner, workGroupUuid);
		return service.find(actor, owner, workGroup, workGroupFolderUuid);
	}

	@Override
	public WorkGroupFolder create(String ownerUuid, String workGroupUuid, WorkGroupFolder workGroupFolder)
			throws BusinessException {
		Validate.notEmpty(workGroupUuid, "Missing required workGroup uuid");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		Thread workGroup = threadService.find(actor, owner, workGroupUuid);
		return service.create(actor, owner, workGroup, workGroupFolder);
	}

	@Override
	public WorkGroupFolder update(String ownerUuid, String workGroupUuid, WorkGroupFolder workGroupFolder)
			throws BusinessException {
		Validate.notEmpty(workGroupUuid, "Missing required workGroup uuid");
		Validate.notNull(workGroupFolder, "Missing required workGroupFolder");
		Validate.notEmpty(workGroupFolder.getUuid(), "Missing required workGroupFolderUuid");
		Validate.notEmpty(workGroupFolder.getName(), "Missing required name");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		Thread workGroup = threadService.find(actor, owner, workGroupUuid);
		return service.update(actor, owner, workGroup, workGroupFolder);
	}

	@Override
	public WorkGroupFolder delete(String ownerUuid, String workGroupUuid, String workGroupFolderUuid)
			throws BusinessException {
		Validate.notEmpty(workGroupUuid, "Missing required workGroup uuid");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		Thread workGroup = threadService.find(actor, owner, workGroupUuid);
		return service.delete(actor, owner, workGroup, workGroupFolderUuid);
	}

	@Override
	public WorkGroupFolder delete(String ownerUuid, String workGroupUuid, WorkGroupFolder workGroupFolder)
			throws BusinessException {
		Validate.notEmpty(workGroupUuid, "Missing required workGroup uuid");
		Validate.notNull(workGroupFolder, "Missing required workGroup folder");
		Validate.notEmpty(workGroupFolder.getUuid(), "Missing required workGroup folder uuid");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		Thread workGroup = threadService.find(actor, owner, workGroupUuid);
		return service.delete(actor, owner, workGroup, workGroupFolder.getUuid());
	}

}
