/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2017-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.business.service.WorkGroupNodeBusinessService;
import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.ThreadMemberRepository;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.TimeService;
import org.linagora.linshare.core.service.WorkGroupNodeAbstractService;
import org.linagora.linshare.core.utils.UniqueName;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupDocumentRevision;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.repository.WorkGroupNodeMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.google.common.base.Strings;


public abstract class WorkGroupNodeAbstractServiceImpl implements WorkGroupNodeAbstractService {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected final WorkGroupNodeMongoRepository repository;

	protected final ThreadMemberRepository threadMemberRepository;

	protected final MongoTemplate mongoTemplate;

	protected final LogEntryService logEntryService;

	protected final SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService;

	protected final SharedSpaceMemberBusinessService sharedSpaceMemberBusinessService;

	protected final MimeTypeMagicNumberDao mimeTypeIdentifier;

	protected final TimeService timeService;

	protected final WorkGroupNodeBusinessService workGroupNodeBusinessService;

	public WorkGroupNodeAbstractServiceImpl(
			WorkGroupNodeMongoRepository repository,
			MongoTemplate mongoTemplate,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
			ThreadMemberRepository threadMemberRepository,
			LogEntryService logEntryService,
			SharedSpaceMemberBusinessService sharedSpaceMemberBusinessService,
			MimeTypeMagicNumberDao mimeTypeMagicNumberDao,
			TimeService timeService,
			WorkGroupNodeBusinessService workGroupNodeBusinessService) {
		super();
		this.repository = repository;
		this.mongoTemplate = mongoTemplate;
		this.logEntryService = logEntryService;
		this.sanitizerInputHtmlBusinessService = sanitizerInputHtmlBusinessService;
		this.threadMemberRepository = threadMemberRepository;
		this.sharedSpaceMemberBusinessService = sharedSpaceMemberBusinessService;
		this.mimeTypeIdentifier = mimeTypeMagicNumberDao;
		this.timeService = timeService;
		this.workGroupNodeBusinessService = workGroupNodeBusinessService;
	}

	protected abstract BusinessErrorCode getBusinessExceptionAlreadyExists();

	protected abstract BusinessErrorCode getBusinessExceptionNotFound();

	protected abstract BusinessErrorCode getBusinessExceptionForbidden();

	@Override
	public WorkGroupNode find(Account actor, User owner, WorkGroup workGroup, String workGroupNodeUuid)
			throws BusinessException {
		WorkGroupNode folder = repository.findByWorkGroupAndUuid(workGroup.getLsUuid(), workGroupNodeUuid);
		if (folder == null) {
			logger.error("Node not found " + workGroupNodeUuid);
			throw new BusinessException(getBusinessExceptionNotFound(),
					"Node not found : " + workGroupNodeUuid);
		}
		return folder;
	}

	@Override
	public void checkUniqueName(WorkGroup workGroup, WorkGroupNode nodeParent, String name) {
		if (!isUniqueName(workGroup, nodeParent, name)) {
			throw new BusinessException(getBusinessExceptionAlreadyExists(),
					"Can not create a new node, it already exists.");
		}
	}

	@Override
	public boolean isUniqueName(WorkGroup workGroup, WorkGroupNode nodeParent, String name) {
		List<WorkGroupNode> nodes = repository.findByWorkGroupAndParentAndName(workGroup.getLsUuid(),
				nodeParent.getUuid(), name);
		if (nodes.isEmpty()) {
			return true;
		}
		return false;
	}

	protected UniqueName getQueryForNodesWithSameName(String name) {
		UniqueName un = new UniqueName(name);
		String pattern = " \\(([0-9]+)\\)$";
		un.setSearchPattern("^" + UniqueName.getEscapeName(name) + pattern);
		un.setExtractPattern(pattern);
		return un;
	}

	@Override
	public String getNewName(Account actor, User owner, WorkGroup workGroup, WorkGroupNode nodeParent,
			String currentName) {
		if (!isUniqueName(workGroup, nodeParent, currentName)) {
			UniqueName res = getQueryForNodesWithSameName(currentName);
			Query query = res.validate().buildQuery();
			query.addCriteria(Criteria.where("workGroup").is(workGroup.getLsUuid()));
			query.addCriteria(Criteria.where("parent").is(nodeParent.getUuid()));
			List<WorkGroupNode> nodes = mongoTemplate.find(query, WorkGroupNode.class);
			res.setFoundNodes(nodes);
			currentName = res.computeNewName();
			logger.info("new computed name {} ", currentName);
		}
		return currentName;
	}

	protected boolean isDocument(WorkGroupNode node) {
		return node.getNodeType().equals(WorkGroupNodeType.DOCUMENT);
	}

	protected boolean isFolder(WorkGroupNode node) {
		return node.getNodeType().equals(WorkGroupNodeType.FOLDER);
	}

	protected boolean isRevison(WorkGroupNode node) {
		return node.getNodeType().equals(WorkGroupNodeType.DOCUMENT_REVISION);
	}

	@Override
	public void addMembersToLog(String workGroupUuid, AuditLogEntryUser log) {
		List<String> members = sharedSpaceMemberBusinessService.findMembersUuidBySharedSpaceNodeUuid(workGroupUuid);
		log.addRelatedAccounts(members);
	}

	@Override
	public String computeFileName(WorkGroupDocument document, WorkGroupDocumentRevision revision, boolean isDocument) {
		try {
			MimeTypes types = MimeTypes.getDefaultMimeTypes();
			MimeType revisionType;
			revisionType = types.forName(revision.getMimeType());
			String preferedRevisionExtension = revisionType.getExtension();
			String documentExtension = FilenameUtils.getExtension(document.getName());
			String revisionExtension = FilenameUtils.getExtension(revision.getName());
			String documentName = document.getName();
			if (!Strings.isNullOrEmpty(documentExtension)) {
				int i = documentName.lastIndexOf(".");
				documentName = documentName.substring(0, i);
			}
			if (isDocument) {
				if (document.getMimeType().equals(revision.getMimeType())) {
					return document.getName();
				} else {
					if (mimeTypeIdentifier.isKnownExtension(revisionExtension)) {
						return documentName.concat(revisionExtension);
					} else {
						return documentName.concat(preferedRevisionExtension);
					}
				}
			} else {
				String extension = mimeTypeIdentifier.isKnownExtension(revisionExtension) ? revisionExtension
						: preferedRevisionExtension;
				SimpleDateFormat formatter = new SimpleDateFormat("YYYYMMdd-HHmmss");
				documentName = documentName + "-r" + formatter.format(revision.getCreationDate()).concat(extension);
				return documentName;
			}
		} catch (MimeTypeException e) {
			logger.debug("Error when trying to get the extension of file", e.getMessage());
			return document.getName();
		}
	}
}
