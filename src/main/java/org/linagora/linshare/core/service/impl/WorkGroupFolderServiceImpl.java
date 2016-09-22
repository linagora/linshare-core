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
package org.linagora.linshare.core.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.domain.entities.ThreadMember;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.WorkGroupFolderService;
import org.linagora.linshare.mongo.entities.WorkGroupEntry;
import org.linagora.linshare.mongo.entities.WorkGroupFolder;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.repository.WorkGroupFolderMongoRepository;
import org.springframework.dao.support.DataAccessUtils;

import com.google.common.collect.Lists;

public class WorkGroupFolderServiceImpl extends GenericServiceImpl<Account, WorkGroupFolder>
		implements WorkGroupFolderService {

	protected final WorkGroupFolderMongoRepository repository;

	protected final ThreadService threadService;

	protected final LogEntryService logEntryService;

	public WorkGroupFolderServiceImpl(WorkGroupFolderMongoRepository repository, LogEntryService logEntryService,
			ThreadService threadService) {
		super(null);
		this.repository = repository;
		this.threadService = threadService;
		this.logEntryService = logEntryService;
	}

	@Override
	public List<WorkGroupFolder> findAll(Account actor, User owner, Thread workGroup) throws BusinessException {
		preChecks(actor, owner);
		Validate.notNull(workGroup, "Missing workGroup");
		checkUploadRights(owner, workGroup);
		return repository.findByWorkGroup(workGroup.getLsUuid());
	}

	@Override
	public List<WorkGroupFolder> findAll(Account actor, User owner, Thread workGroup, String parentUuid)
			throws BusinessException {
		preChecks(actor, owner);
		Validate.notNull(workGroup, "Missing workGroup");
		Validate.notEmpty(parentUuid, "Missing workGroup ancestorUuid");
		checkUploadRights(owner, workGroup);
		return repository.findByWorkGroupAndParent(workGroup.getLsUuid(), parentUuid);
	}

	@Override
	public WorkGroupFolder find(Account actor, User owner, Thread workGroup, String workGroupFolderUuid)
			throws BusinessException {
		preChecks(actor, owner);
		checkUploadRights(owner, workGroup);
		WorkGroupFolder folder = repository.findByWorkGroupAndUuid(workGroup.getLsUuid(), workGroupFolderUuid);
		if (folder == null) {
			logger.error("Folder not found " + workGroupFolderUuid);
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_FOLDER_NOT_FOUND,
					"Folder not found : " + workGroupFolderUuid);
		}
		return folder;
	}

	@Override
	public WorkGroupFolder create(Account actor, User owner, Thread workGroup, WorkGroupFolder workGroupFolder)
			throws BusinessException {
		preChecks(actor, owner);
		checkUploadRights(owner, workGroup);
		String workGroupUuid = workGroup.getLsUuid();
		WorkGroupFolder wgfParent = null;
		if (workGroupFolder.getParent() == null) {
			wgfParent = getRootFolder(workGroup);
		} else {
			if (workGroupFolder.getParent().equals(workGroupUuid)) {
				wgfParent = getRootFolder(workGroup);
			} else {
				wgfParent = find(actor, owner, workGroup, workGroupFolder.getParent());
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

	@Override
	public WorkGroupFolder addEntry(Account actor, User owner, Thread workGroup, String workGroupFolderUuid,
			ThreadEntry threadEntry) throws BusinessException {
		preChecks(actor, owner);
		checkUploadRights(owner, workGroup);
		WorkGroupFolder folder = null;
		if (workGroupFolderUuid == null) {
			folder = getRootFolder(workGroup);
		} else {
			folder = find(actor, owner, workGroup, workGroupFolderUuid);
		}
		folder.getEntries().add(new WorkGroupEntry(threadEntry, new AccountMto(owner)));
		return repository.save(folder);
	}

	@Override
	public WorkGroupFolder getRootFolder(Account actor, User owner, Thread workGroup) throws BusinessException {
		preChecks(actor, owner);
		checkUploadRights(owner, workGroup);
		WorkGroupFolder wgfParent = getRootFolder(workGroup);
		return wgfParent;
	}

	@Override
	public WorkGroupFolder delEntry(Account actor, User owner, Thread workGroup, ThreadEntry threadEntry)
			throws BusinessException {
		preChecks(actor, owner);
		checkUploadRights(owner, workGroup);
		Validate.notNull(threadEntry, "Missing entry !");
		Validate.notEmpty(threadEntry.getUuid(), "Missing entry uuid !");
		WorkGroupFolder folder = getFolder(workGroup, threadEntry);
		// TODO : HACK : To be improved !
		folder.getEntries().remove(new WorkGroupEntry(threadEntry, new AccountMto(owner)));
		repository.save(folder);
		return folder;
	}

	@Override
	public WorkGroupFolder updateEntry(Account actor, User owner, Thread workGroup, ThreadEntry threadEntry)
			throws BusinessException {
		preChecks(actor, owner);
		checkUploadRights(owner, workGroup);
		Validate.notNull(threadEntry, "Missing entry !");
		Validate.notEmpty(threadEntry.getUuid(), "Missing entry uuid !");
		WorkGroupFolder folder = getFolder(workGroup, threadEntry);
		// TODO : HACK : Very ugly : To be improved !
		List<WorkGroupEntry> entries = folder.getEntries();
		for (WorkGroupEntry workGroupEntry : entries) {
			if (workGroupEntry.getUuid().equals(threadEntry.getUuid())) {
				workGroupEntry.setName(threadEntry.getName());
				workGroupEntry.setModificationDate(threadEntry.getModificationDate().getTime());
				workGroupEntry.setLastAuthor(new AccountMto(owner));
				break;
			}
		}
		repository.save(folder);
		return folder;
	}

	@Override
	public WorkGroupFolder getFolder(Account actor, User owner, Thread workGroup, ThreadEntry threadEntry)
			throws BusinessException {
		preChecks(actor, owner);
		checkUploadRights(owner, workGroup);
		Validate.notNull(threadEntry, "Missing entry !");
		Validate.notEmpty(threadEntry.getUuid(), "Missing entry uuid !");
		return getFolder(workGroup, threadEntry);
	}

	private WorkGroupFolder getFolder(Thread workGroup, ThreadEntry threadEntry) {
		WorkGroupFolder folder = repository.findByWorkGroupAndEntriesUuid(workGroup.getLsUuid(), threadEntry.getUuid());
		if (folder == null) {
			logger.error("An entry is not linked to a folder : {}, entry {}.", workGroup, threadEntry);
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_FOLDER_NOT_FOUND, "Missing related folder.");
		}
		return folder;
	}

	@Override
	public WorkGroupFolder update(Account actor, User owner, Thread workGroup, WorkGroupFolder workGroupFolder)
			throws BusinessException {
		preChecks(actor, owner);
		WorkGroupFolder wgf = find(actor, owner, workGroup, workGroupFolder.getUuid());
		wgf.setName(workGroupFolder.getName());
		wgf.setModificationDate(new Date());
		wgf = repository.save(wgf);
		// TODO Check if we have to move folder to another folder
		// temporary disable : work is incomplete.
		String parent = workGroupFolder.getParent();
		if (parent != null) {
			if (parent != wgf.getParent()) {
				WorkGroupFolder newParent = find(actor, owner, workGroup, parent);
				wgf.setParent(newParent.getUuid());
				wgf.setAncestors(newParent.getAncestors());
				wgf.getAncestors().add(newParent.getUuid());
				wgf = repository.save(wgf);
			}
		}
		// TODO: manage WGF entries.
		return wgf;
	}

	@Override
	public WorkGroupFolder delete(Account actor, User owner, Thread workGroup, String workGroupFolderUuid)
			throws BusinessException {
		preChecks(actor, owner);
		WorkGroupFolder wgf = find(actor, owner, workGroup, workGroupFolderUuid);
		Validate.isTrue((wgf.getEntries().size() == 0), "You can not delete a folder if it is not empty.");
		repository.delete(wgf);
		return wgf;
	}

	private ThreadMember checkUploadRights(User owner, Thread thread) {
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

	private WorkGroupFolder getRootFolder(Thread workGroup) {
		WorkGroupFolder wgfParent = null;
		String workGroupUuid = workGroup.getLsUuid();
		wgfParent = DataAccessUtils.singleResult(repository.findByWorkGroupAndParent(workGroupUuid, workGroupUuid));
		if (wgfParent == null) {
			// creation of the root folder.
			wgfParent = new WorkGroupFolder(workGroup.getName(), workGroupUuid, workGroupUuid);
			wgfParent = repository.insert(wgfParent);
		}
		return wgfParent;
	}

}
