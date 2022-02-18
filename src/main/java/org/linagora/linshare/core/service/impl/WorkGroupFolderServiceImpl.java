/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
package org.linagora.linshare.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.business.service.WorkGroupNodeBusinessService;
import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.ThreadMemberRepository;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.TimeService;
import org.linagora.linshare.core.service.WorkGroupFolderService;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.logs.WorkGroupNodeAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.repository.WorkGroupNodeMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;

public class WorkGroupFolderServiceImpl extends WorkGroupNodeAbstractServiceImpl implements WorkGroupFolderService {

	public WorkGroupFolderServiceImpl(
			WorkGroupNodeMongoRepository repository,
			ThreadMemberRepository threadMemberRepository,
			MongoTemplate mongoTemplate,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
			LogEntryService logEntryService,
			SharedSpaceMemberBusinessService sharedSpaceMemberBusinessService,
			MimeTypeMagicNumberDao mimeTypeIdentifier,
			TimeService timeService,
			WorkGroupNodeBusinessService workGroupNodeBusinessService) {
		super(repository, mongoTemplate, sanitizerInputHtmlBusinessService, threadMemberRepository, logEntryService, sharedSpaceMemberBusinessService, mimeTypeIdentifier, timeService, workGroupNodeBusinessService);
	}

	@Override
	protected BusinessErrorCode getBusinessExceptionAlreadyExists() {
		return BusinessErrorCode.WORK_GROUP_FOLDER_ALREADY_EXISTS;
	}

	@Override
	protected BusinessErrorCode getBusinessExceptionNotFound() {
		return BusinessErrorCode.WORK_GROUP_FOLDER_NOT_FOUND;
	}

	@Override
	protected BusinessErrorCode getBusinessExceptionForbidden() {
		return BusinessErrorCode.WORK_GROUP_FOLDER_FORBIDDEN;
	}

	@Override
	public WorkGroupNode create(Account actor, User owner, WorkGroup workGroup, WorkGroupNode workGroupNode, WorkGroupNode nodeParent, Boolean strict, Boolean dryRun)
			throws BusinessException {
		List<WorkGroupNode> node = repository.findByWorkGroupAndParentAndName(
				workGroup.getLsUuid(),
				nodeParent.getUuid(),
				workGroupNode.getName());
		if (node != null && !node.isEmpty()) {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_FOLDER_ALREADY_EXISTS,
					"Can not create a new folder, it already exists.");
		}
		try {
			workGroupNode.setId(null);
			workGroupNode.setParent(nodeParent.getUuid());
			workGroupNode.setWorkGroup(workGroup.getLsUuid());
			workGroupNode.setPathFromParent(nodeParent);
			workGroupNode.setUuid(UUID.randomUUID().toString());
			workGroupNode.setCreationDate(new Date());
			workGroupNode.setModificationDate(new Date());
			workGroupNode.setLastAuthor(new AccountMto(owner, true));
			if (dryRun) {
				return workGroupNode;
			}
			workGroupNode = repository.insert(workGroupNode);
			workGroupNodeBusinessService.updateRelatedWorkGroupNodeResources(workGroupNode, workGroupNode.getModificationDate());
		} catch (org.springframework.dao.DuplicateKeyException e) {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_FOLDER_ALREADY_EXISTS,
					"Can not create a new folder, it already exists.");
		}
		WorkGroupNodeAuditLogEntry log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.CREATE,
				AuditLogEntryType.WORKGROUP_FOLDER, workGroupNode, workGroup);
		addMembersToLog(workGroup.getLsUuid(), log);
		logEntryService.insert(log);
		return workGroupNode;
	}

	@Override
	public WorkGroupNode delete(Account actor, Account owner, WorkGroup workGroup, WorkGroupNode workGroupNode) throws BusinessException {
		repository.delete(workGroupNode);
		workGroupNodeBusinessService.updateRelatedWorkGroupNodeResources(workGroupNode, timeService.dateNow());
		return workGroupNode;
	}
}
