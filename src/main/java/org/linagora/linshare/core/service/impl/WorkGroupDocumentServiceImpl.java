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
package org.linagora.linshare.core.service.impl;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.business.service.WorkGroupNodeBusinessService;
import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.core.domain.constants.OperationHistoryTypeEnum;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.OperationHistory;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.WorkGroupWarnNewWorkgroupDocumentContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.repository.ThreadMemberRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.MimeTypeService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.QuotaService;
import org.linagora.linshare.core.service.TimeService;
import org.linagora.linshare.core.service.VirusScannerService;
import org.linagora.linshare.core.service.WorkGroupDocumentService;
import org.linagora.linshare.core.utils.FileAndMetaData;
import org.linagora.linshare.core.utils.UniqueName;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupDocumentRevision;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.logs.WorkGroupNodeAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.CopyMto;
import org.linagora.linshare.mongo.repository.DocumentGarbageCollectorMongoRepository;
import org.linagora.linshare.mongo.repository.WorkGroupNodeMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;

public class WorkGroupDocumentServiceImpl extends WorkGroupNodeAbstractServiceImpl
		implements WorkGroupDocumentService {

	private final DocumentEntryBusinessService documentEntryBusinessService;

	protected final LogEntryService logEntryService;

	protected final FunctionalityReadOnlyService functionalityReadOnlyService;

	protected final MimeTypeService mimeTypeService;

	protected final VirusScannerService virusScannerService;

	protected final OperationHistoryBusinessService operationHistoryBusinessService;

	protected final QuotaService quotaService;

	protected final WorkGroupNodeMongoRepository repository;

	protected final DocumentGarbageCollectorMongoRepository documentGarbageCollectorRepository;

	protected final MailBuildingService mailBuildingService;

	protected final NotifierService notifierService;

	public WorkGroupDocumentServiceImpl(DocumentEntryBusinessService documentEntryBusinessService,
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
			TimeService timeService,
			WorkGroupNodeBusinessService workGroupNodeBusinessService,
			MailBuildingService mailBuildingService,
			NotifierService notifierService) {
		super(workGroupNodeMongoRepository, mongoTemplate, sanitizerInputHtmlBusinessService, threadMemberRepository, logEntryService, sharedSpaceMemberBusinessService, mimeTypeIdentifier, timeService, workGroupNodeBusinessService);
		this.documentEntryBusinessService = documentEntryBusinessService;
		this.logEntryService = logEntryService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.mimeTypeService = mimeTypeService;
		this.virusScannerService = virusScannerService;
		this.operationHistoryBusinessService = operationHistoryBusinessService;
		this.quotaService = quotaService;
		this.repository = workGroupNodeMongoRepository;
		this.documentGarbageCollectorRepository = documentGarbageCollectorRepository;
		this.mailBuildingService = mailBuildingService;
		this.notifierService = notifierService;
	}

	@Override
	public WorkGroupNode create(Account actor, Account owner, WorkGroup workgroup, Long size, String mimeType, String fileName,
			WorkGroupNode nodeParent) throws BusinessException {
		WorkGroupDocument document = createWithoutLogStorage(actor, owner, workgroup, size, mimeType, fileName,
				nodeParent);
		WorkGroupNodeAuditLogEntry log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.CREATE,
				AuditLogEntryType.WORKGROUP_DOCUMENT, document, workgroup);
		sharedSpaceMemberBusinessService.addMembersToRelatedAccountsAndRelatedDomains(workgroup.getLsUuid(), log);
		logEntryService.insert(log);
		return document;
	}

	@Override
	public WorkGroupDocument createWithoutLogStorage(Account actor, Account owner, WorkGroup workGroup, Long size,
			String mimeType, String fileName, WorkGroupNode nodeParent) throws BusinessException {
		Validate.notNull(nodeParent);
		AbstractDomain domain = owner.getDomain();
		checkSpace(workGroup, size);
		// check if the file MimeType is allowed
		Functionality mimeFunctionality = functionalityReadOnlyService.getMimeTypeFunctionality(domain);
		if (mimeFunctionality.getActivationPolicy().getStatus()) {
			mimeTypeService.checkFileMimeType(owner, fileName, mimeType);
		}
		WorkGroupDocument document = new WorkGroupDocument(actor, fileName, size, mimeType, workGroup, nodeParent);
		document.setPathFromParent(nodeParent);
		document = repository.insert(document);
		workGroupNodeBusinessService.updateRelatedWorkGroupNodeResources(document, document.getModificationDate());
		List<SharedSpaceMember> members = sharedSpaceMemberBusinessService.findBySharedSpaceNodeUuid(workGroup.getLsUuid());
		List<MailContainerWithRecipient> mailContainers = Lists.newArrayList();
		for (SharedSpaceMember sharedSpaceMember : members) {
			if (!sharedSpaceMember.getAccount().getUuid().equals(actor.getLsUuid())) {
				WorkGroupNode parent = repository
						.findByWorkGroupAndUuid(sharedSpaceMember.getNode().getUuid(), document.getParent());
				WorkGroupWarnNewWorkgroupDocumentContext context = new WorkGroupWarnNewWorkgroupDocumentContext(
						workGroup.getDomain(), false, owner, sharedSpaceMember, parent, document);
				MailContainerWithRecipient mail = mailBuildingService.build(context);
				mailContainers.add(mail);
			}
		}
		notifierService.sendNotification(mailContainers, false);
		return document;
	}

	protected Boolean checkFileProperties(Account owner, String fileName, String mimeType, File tempFile, Long size,
			AbstractDomain domain) {
		virusScannerService.checkVirus(fileName, owner, tempFile, size);
		Functionality enciphermentFunctionality = functionalityReadOnlyService.getEnciphermentFunctionality(domain);
		Boolean checkIfIsCiphered = enciphermentFunctionality.getActivationPolicy().getStatus();
		return checkIfIsCiphered;
	}

	protected String getTimeStampingUrl(AbstractDomain domain) {
		StringValueFunctionality timeStampingFunctionality = functionalityReadOnlyService.getTimeStampingFunctionality(domain);
		if (timeStampingFunctionality.getActivationPolicy().getStatus()) {
			return timeStampingFunctionality.getValue();
		}
		return null;
	}

	@Override
	public WorkGroupNode copy(Account actor, Account owner, WorkGroup toWorkGroup, String documentUuid, String fileName,
			WorkGroupNode nodeParent, boolean ciphered, Long size, String fromNodeUuid, CopyMto copiedFrom, WorkGroupNodeAuditLogEntry log, boolean moveDocument) throws BusinessException {
		Validate.notEmpty(documentUuid, "documentUuid is required.");
		Validate.notEmpty(fileName, "fileName is required.");
		Validate.notNull(nodeParent);
		checkSpace(toWorkGroup, size);
		WorkGroupDocument node = documentEntryBusinessService.copy(owner, toWorkGroup, nodeParent, documentUuid, fileName,
				ciphered);
//		LOG can be null in case of copy into a document.
		if (log == null) {
			log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.CREATE,
					AuditLogEntryType.WORKGROUP_DOCUMENT_REVISION, node, toWorkGroup);
			if (copiedFrom.getNodeType().equals(WorkGroupNodeType.DOCUMENT_REVISION)) {
				log.setCause(LogActionCause.RESTORE);
			} else if (moveDocument){
				log.setCause(LogActionCause.MOVE);
			} else {
				log.setCause(LogActionCause.COPY);
			}
			log.addRelatedResources(node.getParent());
		} else {
			log.addRelatedResources(node.getUuid());
		}
		log.setFromResourceUuid(fromNodeUuid);
		log.setCopiedFrom(copiedFrom);
		sharedSpaceMemberBusinessService.addMembersToRelatedAccountsAndRelatedDomains(toWorkGroup.getLsUuid(), log);
		logEntryService.insert(log);
		addToQuota(toWorkGroup, size);
		return node;
	}
	
	@Override
	public WorkGroupNode copy(Account actor, Account owner, WorkGroup toWorkGroup, String documentUuid, String fileName,
			WorkGroupNode nodeParent, boolean ciphered, Long size, String fromNodeUuid, CopyMto copiedFrom,
			WorkGroupNodeAuditLogEntry log) throws BusinessException {
		return copy(actor, owner, toWorkGroup, documentUuid, fileName, nodeParent, ciphered, size, fromNodeUuid, copiedFrom, log, false);
	}

	@Override
	public WorkGroupNode delete(Account actor, Account owner, WorkGroup workGroup, WorkGroupNode workGroupNode)
			throws BusinessException {
		repository.delete(workGroupNode);
		workGroupNodeBusinessService.updateRelatedWorkGroupNodeResources(workGroupNode, timeService.dateNow());
		return workGroupNode;
	}

	public ByteSource getDocumentStream(Account actor, Account owner, WorkGroup workGroup,
			WorkGroupDocumentRevision revision, WorkGroupNodeType nodeType) throws BusinessException {
		WorkGroupNodeAuditLogEntry log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.DOWNLOAD, AuditLogEntryType.WORKGROUP_DOCUMENT,
				revision, workGroup);
		sharedSpaceMemberBusinessService.addMembersToRelatedAccountsAndRelatedDomains(workGroup.getLsUuid(), log);
		log.addRelatedResources(revision.getParent());
		logEntryService.insert(log);
		return documentEntryBusinessService.getByteSource(revision);
	}

	@Override
	public FileAndMetaData download(Account actor, User owner, WorkGroup workGroup, WorkGroupDocument node,
			WorkGroupDocumentRevision revision) {
		String documentName = computeFileName(node, revision, true);
		revision.setName(documentName);
		ByteSource byteSource = getDocumentStream(actor, owner, workGroup, revision,
				WorkGroupNodeType.DOCUMENT);
		return new FileAndMetaData(byteSource, revision.getSize(), documentName, revision.getMimeType());
	}

	@Override
	public void markAsCopied(Account actor, Account owner, WorkGroup workGroup, WorkGroupNode node, CopyMto copiedTo)
			throws BusinessException {
		WorkGroupNodeAuditLogEntry log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.DOWNLOAD, AuditLogEntryType.WORKGROUP_DOCUMENT,
				node, workGroup);
		log.setCause(LogActionCause.COPY);
		sharedSpaceMemberBusinessService.addMembersToRelatedAccountsAndRelatedDomains(workGroup.getLsUuid(), log);
		log.setCopiedTo(copiedTo);
		logEntryService.insert(log);
	}

	@Override
	public ByteSource getThumbnailByteSource(Account actor, Account owner, WorkGroup workGroup, WorkGroupDocument node, ThumbnailType thumbnailType) throws BusinessException {
		return documentEntryBusinessService.getThumbnailByteSource(node, thumbnailType);
	}

	protected void checkSpace(WorkGroup workGroup, long size) throws BusinessException {
		quotaService.checkIfUserCanAddFile(workGroup, size, ContainerQuotaType.WORK_GROUP);
	}

	protected void addToQuota(WorkGroup workGroup, Long size) {
		OperationHistory oh = new OperationHistory(workGroup, workGroup.getDomain(), size, OperationHistoryTypeEnum.CREATE,
				ContainerQuotaType.WORK_GROUP);
		operationHistoryBusinessService.create(oh);
	}

	protected void delFromQuota(WorkGroup workGroup, Long size) {
		OperationHistory oh = new OperationHistory(workGroup, workGroup.getDomain(), - size, OperationHistoryTypeEnum.DELETE,
				ContainerQuotaType.WORK_GROUP);
		operationHistoryBusinessService.create(oh);
	}

	@Override
	protected UniqueName getQueryForNodesWithSameName(String name) {
		String[] split = name.split("\\.");
		if (split.length == 1) {
			return super.getQueryForNodesWithSameName(name);
		} else {
			String extension = split[split.length - 1];
			if (mimeTypeIdentifier.isKnownExtension("." + extension)) {
				String baseName = name.substring(0, name.length() - extension.length() -1);
				UniqueName un = new UniqueName(name);
				un.setBaseName(baseName);
				un.setSuffix("." + extension);
				String pattern = " \\(([0-9]+)\\)\\." + extension + "$";
				un.setSearchPattern("^" + UniqueName.getEscapeName(baseName) + pattern);
				un.setExtractPattern(pattern);
				return un;
			} else {
				return super.getQueryForNodesWithSameName(name);
			}
		}
	}

	@Override
	protected BusinessErrorCode getBusinessExceptionAlreadyExists() {
		return BusinessErrorCode.WORK_GROUP_DOCUMENT_ALREADY_EXISTS;
	}

	@Override
	protected BusinessErrorCode getBusinessExceptionNotFound() {
		return BusinessErrorCode.WORK_GROUP_DOCUMENT_NOT_FOUND;
	}

	@Override
	protected BusinessErrorCode getBusinessExceptionForbidden() {
		return BusinessErrorCode.WORK_GROUP_DOCUMENT_FORBIDDEN;
	}

}
