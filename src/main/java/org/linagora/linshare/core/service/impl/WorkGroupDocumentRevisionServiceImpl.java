/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
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

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.business.service.DocumentEntryRevisionBusinessService;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.business.service.WorkGroupNodeBusinessService;
import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.ThreadMemberRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.MimeTypeService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.QuotaService;
import org.linagora.linshare.core.service.TimeService;
import org.linagora.linshare.core.service.VirusScannerService;
import org.linagora.linshare.core.service.WorkGroupDocumentRevisionService;
import org.linagora.linshare.core.utils.FileAndMetaData;
import org.linagora.linshare.mongo.entities.DocumentGarbageCollecteur;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.VersioningParameters;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupDocumentRevision;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.logs.WorkGroupNodeAuditLogEntry;
import org.linagora.linshare.mongo.repository.DocumentGarbageCollectorMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpaceNodeMongoRepository;
import org.linagora.linshare.mongo.repository.WorkGroupNodeMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.google.common.io.ByteSource;

public class WorkGroupDocumentRevisionServiceImpl extends WorkGroupDocumentServiceImpl
		implements WorkGroupDocumentRevisionService {

	protected final DocumentEntryRevisionBusinessService documentEntryRevisionBusinessService;

	protected final SharedSpaceNodeMongoRepository sharedSpaceNodeMongoRepository;

	protected final DocumentRepository documentRepository;

	public WorkGroupDocumentRevisionServiceImpl(DocumentEntryBusinessService documentEntryBusinessService,
			LogEntryService logEntryService,
			FunctionalityReadOnlyService functionalityReadOnlyService,
			MimeTypeService mimeTypeService,
			VirusScannerService virusScannerService,
			MimeTypeMagicNumberDao mimeTypeIdentifier,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
			WorkGroupNodeMongoRepository workGroupNodeMongoRepository,
			DocumentGarbageCollectorMongoRepository documentGarbageCollectorRepository,
			ThreadMemberRepository threadMemberRepository,
			MongoTemplate mongoTemplate,
			OperationHistoryBusinessService operationHistoryBusinessService,
			QuotaService quotaService,
			SharedSpaceMemberBusinessService sharedSpaceMemberBusinessService,
			DocumentEntryRevisionBusinessService documentEntryRevisionBusinessService,
			SharedSpaceNodeMongoRepository sharedSpaceNodeMongoRepository,
			DocumentRepository documentRepository,
			TimeService timeService,
			WorkGroupNodeBusinessService workGroupNodeBusinessService,
			MailBuildingService mailBuildingService,
			NotifierService notifierService) {
		super(documentEntryBusinessService, logEntryService, functionalityReadOnlyService, mimeTypeService,
				virusScannerService, mimeTypeIdentifier, sanitizerInputHtmlBusinessService, workGroupNodeMongoRepository,
				documentGarbageCollectorRepository, threadMemberRepository, mongoTemplate, operationHistoryBusinessService,
				quotaService, sharedSpaceMemberBusinessService,timeService, workGroupNodeBusinessService, mailBuildingService, notifierService);
		this.documentEntryRevisionBusinessService = documentEntryRevisionBusinessService;
		this.sharedSpaceNodeMongoRepository = sharedSpaceNodeMongoRepository;
		this.documentRepository = documentRepository;
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
			if (hasRevision(workGroup.getLsUuid(), parentNode.getUuid())) {
				WorkGroupNodeAuditLogEntry log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.CREATE,
						AuditLogEntryType.WORKGROUP_DOCUMENT_REVISION, documentRevision, workGroup);
				addMembersToLog(workGroup.getLsUuid(), log);
				log.addRelatedResources(parentNode.getUuid());
				logEntryService.insert(log);
			} else {
				WorkGroupDocument parentDocument = (WorkGroupDocument) repository
						.findByWorkGroupAndUuid(workGroup.getLsUuid(), documentRevision.getParent());
				parentDocument.setHasThumbnail(documentRevision.getHasThumbnail());
				parentDocument.setSha256sum(documentRevision.getSha256sum());
				repository.save(parentDocument);
			}
			addToQuota(workGroup, size);
		} finally {
			try {
				logger.debug("deleting temp file : " + tempFile.getName());
				tempFile.delete(); // remove the temporary file
			} catch (Exception e) {
				logger.error("can not delete temp file : " + e.getMessage());
			}
		}
		workGroupNodeBusinessService.updateRelatedWorkGroupNodeResources(documentRevision, documentRevision.getModificationDate());
		return documentRevision;
	}

	@Override
	public WorkGroupDocument updateDocument(Account actor, Account owner, WorkGroup workGroup,
			WorkGroupDocumentRevision documentRevision) throws BusinessException {
		Validate.notNull(documentRevision);
		Validate.notEmpty(documentRevision.getUuid(), "Missing uuid");
		Validate.notEmpty(documentRevision.getName(), "Missing name");
		WorkGroupDocument parentDocument = (WorkGroupDocument) repository.findByWorkGroupAndUuid(workGroup.getLsUuid(),
				documentRevision.getParent());
		if (parentDocument == null) {
			throw new BusinessException(BusinessErrorCode.DOCUMENT_ENTRY_NOT_FOUND,
					"The parent document has not been found for this revision.");
		}
		parentDocument.setModificationDate(new Date());
		parentDocument.setMetaData(documentRevision.getMetaData());
		parentDocument.setLastAuthor(documentRevision.getLastAuthor());
		parentDocument.setSize(documentRevision.getSize());
		parentDocument.setHasThumbnail(documentRevision.getHasThumbnail());
		boolean hasRevision = hasRevision(workGroup.getLsUuid(), parentDocument.getUuid());
		parentDocument.setHasRevision(hasRevision);
		parentDocument.setSha256sum(documentRevision.getSha256sum());
		parentDocument.setMimeType(documentRevision.getMimeType());
		parentDocument = repository.save(parentDocument);
		return parentDocument;
	}

	@Override
	public WorkGroupNode findMostRecent(WorkGroup workGroup, String parentUuid) throws BusinessException {
		Validate.notNull(workGroup);
		Validate.notNull(parentUuid);
		return documentEntryRevisionBusinessService.findMostRecent(workGroup, parentUuid);
	}

	@Override
	public boolean checkVersioningFunctionality(AbstractDomain domain, WorkGroup workGroup) {
		SharedSpaceNode ssn = sharedSpaceNodeMongoRepository.findByUuid(workGroup.getLsUuid());
		VersioningParameters parameters = ssn.getVersioningParameters();
		Functionality versioningFunctionality = functionalityReadOnlyService.getWorkGroupFileVersioning(domain);
		if (versioningFunctionality.getActivationPolicy().getStatus()) {
			if (parameters != null) {
				return parameters.getEnable();
			}
			return true;
		}
		return false;
	}

	@Override
	public FileAndMetaData download(Account actor, User owner, WorkGroup workGroup, WorkGroupDocument node,
			WorkGroupDocumentRevision revision) {
		String fileName = computeFileName(node, revision, false);
		revision.setName(fileName);
		ByteSource byteSource = getDocumentStream(actor, owner, workGroup, revision,
				WorkGroupNodeType.DOCUMENT_REVISION);
		return new FileAndMetaData(byteSource, revision.getSize(), fileName, revision.getMimeType());
	}

	@Override
	public List<WorkGroupNode> deleteAll(Account actor, Account owner, WorkGroup workGroup,
			List<WorkGroupNode> revisions) throws BusinessException {
		for (WorkGroupNode rev : revisions) {
			deleteRevision(actor, owner, workGroup, (WorkGroupDocumentRevision) rev);
		}
		// Since all revisions have the same parent and same workGroup we can use just one revision to update related resources
		WorkGroupNode firstRevision = revisions.stream()
				.findFirst()
				.orElseThrow(() -> new BusinessException(BusinessErrorCode.WORK_GROUP_DOCUMENT_REVISION_NOT_FOUND, "The first revision has not been found."));
		workGroupNodeBusinessService.updateRelatedWorkGroupNodeResources(firstRevision, timeService.dateNow());
		return revisions;
	}

	@Override
	public WorkGroupNode delete(Account actor, Account owner, WorkGroup workGroup, WorkGroupNode workGroupNode)
			throws BusinessException {
		Validate.notNull(workGroupNode, "revision must not be null");
		WorkGroupNode revisionToDelete = repository.findByWorkGroupAndUuidAndNodeType(workGroup.getLsUuid(),
				workGroupNode.getUuid(), WorkGroupNodeType.DOCUMENT_REVISION);
		if (revisionToDelete == null) {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_DOCUMENT_REVISION_NOT_FOUND,
					"The revision has not been found");
		}
		if (!hasRevision(workGroup.getLsUuid(), workGroupNode.getParent())) {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_DOCUMENT_REVISION_DELETE_FORBIDDEN,
					"You can't delete the last revision, try to delete the whole document");
		}
		revisionToDelete = deleteRevision(actor, owner, workGroup, (WorkGroupDocumentRevision) revisionToDelete);
		WorkGroupDocumentRevision mostRecentRevision = (WorkGroupDocumentRevision) findMostRecent(workGroup,
				revisionToDelete.getParent());
		updateDocument(actor, owner, workGroup, mostRecentRevision);
		workGroupNodeBusinessService.updateRelatedWorkGroupNodeResources(revisionToDelete, timeService.dateNow());
		return revisionToDelete;
	}

	private WorkGroupDocumentRevision deleteRevision(Account actor, Account owner, WorkGroup workGroup, WorkGroupDocumentRevision revision) throws BusinessException {
		repository.delete(revision);
		delFromQuota(workGroup, revision.getSize());
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

	private boolean hasRevision(String workgroupUuid, String parentDocumentUuid) {
		return repository.countByWorkGroupAndParentAndNodeType(workgroupUuid, parentDocumentUuid,
				WorkGroupNodeType.DOCUMENT_REVISION) > 1;
	}

}
