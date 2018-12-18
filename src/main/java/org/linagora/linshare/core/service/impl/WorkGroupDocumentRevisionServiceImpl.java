/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.business.service.DocumentEntryRevisionBusinessService;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.ThreadMemberRepository;
import org.linagora.linshare.core.service.AntiSamyService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.MimeTypeService;
import org.linagora.linshare.core.service.QuotaService;
import org.linagora.linshare.core.service.VirusScannerService;
import org.linagora.linshare.core.service.WorkGroupDocumentRevisionService;
import org.linagora.linshare.mongo.entities.DocumentGarbageCollecteur;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupDocumentRevision;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.WorkGroupVersioning;
import org.linagora.linshare.mongo.entities.logs.WorkGroupNodeAuditLogEntry;
import org.linagora.linshare.mongo.repository.DocumentGarbageCollectorMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpaceNodeMongoRepository;
import org.linagora.linshare.mongo.repository.WorkGroupNodeMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.google.common.collect.Lists;

public class WorkGroupDocumentRevisionServiceImpl extends WorkGroupDocumentServiceImpl
		implements WorkGroupDocumentRevisionService {

	protected final DocumentEntryRevisionBusinessService documentEntryRevisionBusinessService;

	protected final SharedSpaceNodeMongoRepository sharedSpaceNodeMongoRepository;

	public WorkGroupDocumentRevisionServiceImpl(DocumentEntryBusinessService documentEntryBusinessService,
			LogEntryService logEntryService,
			FunctionalityReadOnlyService functionalityReadOnlyService,
			MimeTypeService mimeTypeService,
			VirusScannerService virusScannerService,
			MimeTypeMagicNumberDao mimeTypeIdentifier,
			AntiSamyService antiSamyService,
			WorkGroupNodeMongoRepository workGroupNodeMongoRepository,
			DocumentGarbageCollectorMongoRepository documentGarbageCollectorRepository,
			ThreadMemberRepository threadMemberRepository,
			MongoTemplate mongoTemplate,
			OperationHistoryBusinessService operationHistoryBusinessService,
			QuotaService quotaService,
			SharedSpaceMemberBusinessService sharedSpaceMemberBusinessService,
			DocumentEntryRevisionBusinessService documentEntryRevisionBusinessService,
			SharedSpaceNodeMongoRepository sharedSpaceNodeMongoRepository) {
		super(documentEntryBusinessService, logEntryService, functionalityReadOnlyService, mimeTypeService,
				virusScannerService, mimeTypeIdentifier, antiSamyService, workGroupNodeMongoRepository,
				documentGarbageCollectorRepository, threadMemberRepository, mongoTemplate, operationHistoryBusinessService,
				quotaService, sharedSpaceMemberBusinessService);
		this.documentEntryRevisionBusinessService = documentEntryRevisionBusinessService;
		this.sharedSpaceNodeMongoRepository = sharedSpaceNodeMongoRepository;
	}

	@Override
	public WorkGroupDocumentRevision create(Account actor, Account owner, WorkGroup workGroup, File tempFile,
			String fileName, WorkGroupNode parentNode) throws BusinessException {
		Validate.notNull(parentNode);
		Validate.notNull(tempFile);
		if (!parentNode.getNodeType().equals(WorkGroupNodeType.DOCUMENT)) {
			List<WorkGroupNode> nodes = repository.findByWorkGroupAndParentAndName(workGroup.getLsUuid(), parentNode.getUuid(), fileName);
			if (!nodes.isEmpty()) {
				parentNode = nodes.get(0);
			}
			if(parentNode == null) {
				throw new BusinessException(BusinessErrorCode.DOCUMENT_ENTRY_NOT_FOUND, "The parent node has not been found.");
			}
		}
		Long size = tempFile.length();
		WorkGroupDocumentRevision documentRevision = null;
		try {
			String mimeType = mimeTypeIdentifier.getMimeType(tempFile);
			AbstractDomain domain = owner.getDomain();
			checkSpace(workGroup, size);
			String timeStampingUrl = getTimeStampingUrl(domain);
			Boolean checkIfIsCiphered = checkFileProperties(owner, fileName, mimeType, tempFile, size, domain);
			documentRevision = documentEntryRevisionBusinessService.createWorkGroupDocumentRevision(owner, workGroup,
					tempFile, size, fileName, checkIfIsCiphered, timeStampingUrl, mimeType, parentNode);
			WorkGroupNodeAuditLogEntry log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.CREATE,
					AuditLogEntryType.WORKGROUP_DOCUMENT_REVISION, parentNode, workGroup);
			logEntryService.insert(log);
			addToQuota(workGroup, size);
		} finally {
			try {
				logger.debug("deleting temp file : " + tempFile.getName());
				tempFile.delete(); // remove the temporary file
			} catch (Exception e) {
				logger.error("can not delete temp file : " + e.getMessage());
			}
		}
		return documentRevision;
	}

	@Override
	public WorkGroupDocument updateDocument(Account actor, Account owner, WorkGroup workGroup, WorkGroupDocumentRevision documentRevision) throws BusinessException {
		Validate.notNull(documentRevision);
		Validate.notEmpty(documentRevision.getUuid(), "Missing uuid");
		Validate.notEmpty(documentRevision.getName(), "Missing name");
		WorkGroupDocument parentDocument = (WorkGroupDocument) repository.findByWorkGroupAndUuid(workGroup.getLsUuid(), documentRevision.getParent());
		if (parentDocument == null) {
			throw new BusinessException(BusinessErrorCode.DOCUMENT_ENTRY_NOT_FOUND, "The parent document has not been found for this revision.");
		} 
		parentDocument.setModificationDate(new Date());
		parentDocument.setMetaData(documentRevision.getMetaData());
		parentDocument.setLastAuthor(documentRevision.getLastAuthor());
		parentDocument.setSize(documentRevision.getSize());
		Long revisionsNumber = repository.countByWorkGroupAndParentAndNodeType(workGroup.getLsUuid(),
				parentDocument.getUuid(), WorkGroupNodeType.DOCUMENT_REVISION);
		if (revisionsNumber < 2) {
			parentDocument.setHasRevision(false);
		} else {
			parentDocument.setHasRevision(true);
		}
		parentDocument = repository.save(parentDocument);
		WorkGroupNodeAuditLogEntry log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.UPDATE,
				AuditLogEntryType.WORKGROUP_DOCUMENT, parentDocument, workGroup);
		logEntryService.insert(log);
		return parentDocument;
	}

	@Override
	public WorkGroupDocument restore(Account actor, Account owner, WorkGroup workGroup, String revisionUuid) throws BusinessException {
		WorkGroupNode workGroupNode = repository.findByUuid(revisionUuid);
		if(workGroupNode == null) {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_DOCUMENT_REVISION_NOT_FOUND, "The node has not been found.");
		}
		if(!workGroupNode.getNodeType().equals(WorkGroupNodeType.DOCUMENT_REVISION)) {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_OPERATION_UNSUPPORTED, "The node type is not a revision.");
		}
		WorkGroupDocumentRevision newRevision = this.copy(actor, owner, workGroup, (WorkGroupDocumentRevision) workGroupNode); 
		return updateDocument(actor, owner, workGroup, newRevision);
	}

	private WorkGroupDocumentRevision copy(Account actor, Account owner, WorkGroup workGroup, WorkGroupDocumentRevision documentRevision) {
		documentRevision = (WorkGroupDocumentRevision) repository.findByUuid(documentRevision.getUuid());
		documentRevision.setId(null);
		documentRevision.setUuid(UUID.randomUUID().toString());
		documentRevision.setModificationDate(new Date());
		documentRevision.setCreationDate(new Date());
		return repository.insert(documentRevision);
	}

	@Override
	public List<WorkGroupNode> findAll(Account actor, WorkGroup workGroup, String parentUuid) {
		Validate.notNull(actor);
		Validate.notNull(workGroup);
		Validate.notNull(parentUuid);
		Functionality versioningFunctionality = functionalityReadOnlyService.getWorkGroupFileVersioning(actor.getDomain());
		boolean isVersioningFunctionalityEnabled = versioningFunctionality.getActivationPolicy().getStatus();
		List<WorkGroupNode> nodes = Lists.newArrayList();
		if (isVersioningFunctionalityEnabled && isVersioningEnabledInWorkGroup(workGroup)) {
			nodes = repository.findByWorkGroupAndParentAndNodeType(workGroup.getLsUuid(), parentUuid, WorkGroupNodeType.DOCUMENT_REVISION);
			nodes = nodes.stream()
					.sorted(Comparator.comparing(WorkGroupNode::getCreationDate, Comparator.reverseOrder()))
					.collect(Collectors.toList());
			if(!nodes.isEmpty()) {
				// The most recent revision is not considered as a regular revision, so we don't return it.
				nodes.remove(0);
			}
		}
		return nodes;
	}

	private boolean isVersioningEnabledInWorkGroup(WorkGroup workGroup) {
		SharedSpaceNode ssn = sharedSpaceNodeMongoRepository.findByUuid(workGroup.getLsUuid());
		WorkGroupVersioning workGroupVersioning = ssn.getWorkGroupVersioning();
		if (workGroupVersioning == null) {
			return false;
		}
		return workGroupVersioning.isEnabled();
	}

	@Override
	public List<WorkGroupNode> deleteAll(Account actor, Account owner, WorkGroup workGroup, WorkGroupNode parentNode)
			throws BusinessException {
		List<WorkGroupNode> revisions = repository.findByWorkGroupAndParentAndNodeType(workGroup.getLsUuid(),
				parentNode.getUuid(), WorkGroupNodeType.DOCUMENT_REVISION);
		for (WorkGroupNode rev : revisions) {
			deleteRevision(actor, owner, workGroup, (WorkGroupDocumentRevision) rev);
		}
		return revisions;
	}

	@Override
	public WorkGroupNode delete(Account actor, Account owner, WorkGroup workGroup, WorkGroupNode workGroupNode) throws BusinessException{
		List<WorkGroupNode> revisions = findAll(actor, workGroup, workGroupNode.getParent());
		WorkGroupNode revisionToDelete = repository.findByWorkGroupAndUuid(workGroup.getLsUuid(), workGroupNode.getUuid());
		if(revisions.isEmpty()) {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_DOCUMENT_REVISION_NOT_FOUND, "The revision has not been found");
		}
		if(!revisions.contains(revisionToDelete)) {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_DOCUMENT_REVISION_DELETE_FORBIDDEN, "You can't delete the most recent revision, try to delete the whole document");
		}
		return deleteRevision(actor, owner, workGroup, (WorkGroupDocumentRevision) workGroupNode);
	}

	private WorkGroupDocumentRevision deleteRevision(Account actor, Account owner, WorkGroup workGroup, WorkGroupDocumentRevision revision) throws BusinessException {
		Validate.notNull(workGroup, "workGroup must not be null");
		Validate.notNull(revision, "revision must not be null");
		repository.delete(revision);
		delFromQuota(workGroup, revision.getSize());
		WorkGroupNodeAuditLogEntry log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.DELETE, AuditLogEntryType.WORKGROUP_DOCUMENT_REVISION, revision, workGroup);
		addMembersToLog(workGroup, log);
		logEntryService.insert(log);
		if (isGarbageCollectorReady(revision.getDocumentUuid())) {
			documentGarbageCollectorRepository.insert(new DocumentGarbageCollecteur(revision.getDocumentUuid()));
		}
		return revision;
	}

	private boolean isGarbageCollectorReady(String documentUuid) {
		List<WorkGroupDocument> nodes = repository.findByDocumentUuid(documentUuid);
		if(nodes.isEmpty()) {
			return true;
		}
		return false;
	}
}