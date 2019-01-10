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
import java.io.InputStream;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.core.domain.constants.OperationHistoryTypeEnum;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.OperationHistory;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
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
import org.linagora.linshare.core.service.WorkGroupDocumentService;
import org.linagora.linshare.core.utils.UniqueName;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.logs.WorkGroupNodeAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.CopyMto;
import org.linagora.linshare.mongo.repository.DocumentGarbageCollectorMongoRepository;
import org.linagora.linshare.mongo.repository.WorkGroupNodeMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;

public class WorkGroupDocumentServiceImpl extends WorkGroupNodeAbstractServiceImpl
		implements WorkGroupDocumentService {

	private final DocumentEntryBusinessService documentEntryBusinessService;

	protected final LogEntryService logEntryService;

	protected final FunctionalityReadOnlyService functionalityReadOnlyService;

	protected final MimeTypeService mimeTypeService;

	protected final VirusScannerService virusScannerService;

	protected final MimeTypeMagicNumberDao mimeTypeIdentifier;

	protected final OperationHistoryBusinessService operationHistoryBusinessService;

	protected final QuotaService quotaService;

	protected final WorkGroupNodeMongoRepository repository;

	protected final DocumentGarbageCollectorMongoRepository documentGarbageCollectorRepository;

	public WorkGroupDocumentServiceImpl(DocumentEntryBusinessService documentEntryBusinessService,
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
			SharedSpaceMemberBusinessService sharedSpaceMemberBusinessService) {
		super(workGroupNodeMongoRepository, mongoTemplate, antiSamyService, threadMemberRepository, logEntryService, sharedSpaceMemberBusinessService);
		this.documentEntryBusinessService = documentEntryBusinessService;
		this.logEntryService = logEntryService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.mimeTypeService = mimeTypeService;
		this.virusScannerService = virusScannerService;
		this.mimeTypeIdentifier = mimeTypeIdentifier;
		this.operationHistoryBusinessService = operationHistoryBusinessService;
		this.quotaService = quotaService;
		this.repository = workGroupNodeMongoRepository;
		this.documentGarbageCollectorRepository = documentGarbageCollectorRepository;
	}

	@Override
	public WorkGroupNode create(Account actor, Account owner, WorkGroup workgroup, Long size, String mimeType, String fileName,
			WorkGroupNode nodeParent) throws BusinessException {
		Validate.notNull(nodeParent);
		WorkGroupDocument document= null;
		AbstractDomain domain = owner.getDomain();
		checkSpace(workgroup, size);
		// check if the file MimeType is allowed
		Functionality mimeFunctionality = functionalityReadOnlyService.getMimeTypeFunctionality(domain);
		if (mimeFunctionality.getActivationPolicy().getStatus()) {
			mimeTypeService.checkFileMimeType(owner, fileName, mimeType);
		}
		document = new WorkGroupDocument(actor, fileName, size, mimeType, workgroup, nodeParent);
		document = repository.insert(document);
		WorkGroupNodeAuditLogEntry log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.CREATE,
				AuditLogEntryType.WORKGROUP_DOCUMENT, document, workgroup);
		addMembersToLog(workgroup, log);
		logEntryService.insert(log);
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
			WorkGroupNode nodeParent, boolean ciphered, Long size, String fromNodeUuid, CopyMto copiedFrom) throws BusinessException {
		Validate.notEmpty(documentUuid, "documentUuid is required.");
		Validate.notEmpty(fileName, "fileName is required.");
		Validate.notNull(nodeParent);
		checkSpace(toWorkGroup, size);
		WorkGroupDocument node = documentEntryBusinessService.copy(owner, toWorkGroup, nodeParent, documentUuid, fileName,
				ciphered);
		WorkGroupNodeAuditLogEntry log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.CREATE,
				AuditLogEntryType.WORKGROUP_DOCUMENT, node, toWorkGroup);
		addMembersToLog(toWorkGroup, log);
		log.setCause(LogActionCause.COPY);
		log.setFromResourceUuid(fromNodeUuid);
		log.setCopiedFrom(copiedFrom);
		addMembersToLog(toWorkGroup, log);
		logEntryService.insert(log);
		addToQuota(toWorkGroup, size);
		return node;
	}

	@Override
	public WorkGroupNode delete(Account actor, Account owner, WorkGroup workGroup, WorkGroupNode workGroupNode)
			throws BusinessException {
		WorkGroupNodeAuditLogEntry log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.DELETE,
				AuditLogEntryType.WORKGROUP_DOCUMENT, workGroupNode, workGroup);
		addMembersToLog(workGroup, log);
		logEntryService.insert(log);
		repository.delete(workGroupNode);
		return workGroupNode;
	}

	@Override
	public InputStream getDocumentStream(Account actor, Account owner, WorkGroup workGroup, WorkGroupDocument node)
			throws BusinessException {
		WorkGroupNodeAuditLogEntry log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.DOWNLOAD,
				AuditLogEntryType.WORKGROUP_DOCUMENT, node, workGroup);
		addMembersToLog(workGroup, log);
		logEntryService.insert(log);
		return documentEntryBusinessService.getDocumentStream(node);
	}

	@Override
	public void markAsCopied(Account actor, Account owner, WorkGroup workGroup, WorkGroupNode node, CopyMto copiedTo)
			throws BusinessException {
		WorkGroupNodeAuditLogEntry log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.DOWNLOAD,
				AuditLogEntryType.WORKGROUP_DOCUMENT, node, workGroup);
		log.setCause(LogActionCause.COPY);
		addMembersToLog(workGroup, log);
		log.setCopiedTo(copiedTo);
		logEntryService.insert(log);
	}

	@Override
	public InputStream getThumbnailStream(Account actor, Account owner, WorkGroup workGroup, WorkGroupDocument node, ThumbnailType thumbnailType) throws BusinessException {
		return documentEntryBusinessService.getThreadEntryThumbnailStream(node, thumbnailType);
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
		OperationHistory oh = new OperationHistory(workGroup, workGroup.getDomain(), size, OperationHistoryTypeEnum.DELETE,
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
