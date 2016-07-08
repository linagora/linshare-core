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
import org.linagora.linshare.core.domain.entities.ThreadMember;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.WorkGroupFolderFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.mongo.entities.WorkGroupFolder;
import org.linagora.linshare.mongo.repository.WorkGroupFolderMongoRepository;

import com.google.common.collect.Lists;

public class WorkGroupFolderFacadeImpl extends UserGenericFacadeImp implements WorkGroupFolderFacade {

	protected final WorkGroupFolderMongoRepository repository;

	protected final ThreadService threadService;

	public WorkGroupFolderFacadeImpl(AccountService accountService,
			WorkGroupFolderMongoRepository workGroupFolderMongoRepository, ThreadService threadService) {
		super(accountService);
		this.repository = workGroupFolderMongoRepository;
		this.threadService = threadService;
	}

	@Override
	public List<WorkGroupFolder> findAll(String ownerUuid, String workGroupUuid) throws BusinessException {
		Validate.notEmpty(workGroupUuid, "Missing required workGroup uuid");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		// Check existence, rights
		Thread thread = threadService.find(actor, owner, workGroupUuid);
		checkWriteRights(owner, thread);
		return repository.findByWorkGroup(workGroupUuid);
	}

	@Override
	public WorkGroupFolder find(String ownerUuid, String workGroupUuid, String workGroupFolderUuid)
			throws BusinessException {
		Validate.notEmpty(workGroupUuid, "Missing required workGroup uuid");
		Validate.notEmpty(workGroupFolderUuid, "Missing required workGroup folder uuid");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		// Check existence, rights
		Thread thread = threadService.find(actor, owner, workGroupUuid);
		checkWriteRights(owner, thread);
		return repository.findByWorkGroupAndUuid(workGroupUuid, workGroupFolderUuid);
	}

	@Override
	public WorkGroupFolder create(String ownerUuid, String workGroupUuidIn, WorkGroupFolder workGroupFolder)
			throws BusinessException {
		Validate.notEmpty(workGroupUuidIn, "Missing required workGroup uuid");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		// Check existence adn access rights
		Thread thread = threadService.find(actor, owner, workGroupUuidIn);
		checkWriteRights(owner, thread);
		String workGroupUuid = thread.getLsUuid();
		WorkGroupFolder wgfParent = null;
		if (workGroupFolder.getParent() == null) {
			wgfParent = repository.findByWorkGroupAndUuid(workGroupUuid, workGroupUuid);
			if (wgfParent == null) {
				// creation of the root folder.
				wgfParent = new WorkGroupFolder(thread.getName(), workGroupUuid, workGroupUuid);
				wgfParent = repository.insert(wgfParent);
			}
		} else {
			wgfParent = repository.findByWorkGroupAndUuid(workGroupUuid, workGroupFolder.getParent());
			if (wgfParent == null) {
				String msg = "Parent folder not found : " + workGroupFolder.getParent();
				logger.error(msg);
				throw new BusinessException(BusinessErrorCode.WORK_GROUP_FOLDER_NOT_FOUND, msg);
			}
		}
		WorkGroupFolder entity = new WorkGroupFolder(workGroupFolder);
		entity.setParent(wgfParent.getUuid());
		entity.setWorkGroup(workGroupUuid);
		entity.setAncestors(Lists.newArrayList(wgfParent.getAncestors()));
		entity.getAncestors().add(wgfParent.getUuid());
		try {
			entity = repository.insert(entity);
		} catch (org.springframework.dao.DuplicateKeyException e) {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_FOLDER_ALREADY_EXISTS,
					"Can not create a new folder, it already exists.");
		}
		return entity;
	}

	private ThreadMember checkWriteRights(User owner, Thread thread) {
		ThreadMember member = threadService.getMemberFromUser(thread, owner);
		if (member == null) {
			String msg = "You are not authorized to create folder or upload file in this work group.";
			logger.error(msg);
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_FOLDER_FORBIDDEN, msg);
		}
		if (!member.getCanUpload()) {
			String msg = "You are not authorized to create folder or upload file in this work group.";
			logger.error(msg);
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_FOLDER_FORBIDDEN, msg);
		}
		return member;
	}

	@Override
	public WorkGroupFolder update(String ownerUuid, String workGroupUuid, WorkGroupFolder workGroupFolder)
			throws BusinessException {
		Validate.notEmpty(workGroupUuid, "Missing required workGroup uuid");
		Validate.notNull(workGroupFolder, "Missing required workGroupFolder");
		Validate.notEmpty(workGroupFolder.getUuid(), "Missing required workGroupFolderUuid");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		// Check existence, rights
		threadService.find(actor, owner, workGroupUuid);
		WorkGroupFolder wgf = repository.findByWorkGroupAndUuid(workGroupUuid, workGroupFolder.getUuid());
		wgf.setName(workGroupFolder.getName());
		// workGroupFolderMongoRepository.
		return wgf;
	}

	@Override
	public WorkGroupFolder delete(String ownerUuid, String workGroupUuid, String workGroupFolderUuid)
			throws BusinessException {
		Validate.notEmpty(workGroupUuid, "Missing required workGroup uuid");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		// Check existence, rights
		threadService.find(actor, owner, workGroupUuid);
		WorkGroupFolder workGroupFolder = repository.findByWorkGroupAndUuid(workGroupUuid, workGroupFolderUuid);
		repository.delete(workGroupFolder);
		return workGroupFolder;
	}

	@Override
	public WorkGroupFolder delete(String ownerUuid, String workGroupUuid, WorkGroupFolder workGroupFolder)
			throws BusinessException {
		Validate.notEmpty(workGroupUuid, "Missing required workGroup uuid");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		// Check existence, rights
		threadService.find(actor, owner, workGroupUuid);
		WorkGroupFolder wgf = repository.findByWorkGroupAndUuid(workGroupUuid, workGroupFolder.getUuid());
		repository.delete(wgf);
		return wgf;
	}

}
