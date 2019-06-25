/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017-2018 LINAGORA
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.WorkGroupNodeBusinessService;
import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.objects.CopyResource;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.impl.WorkGroupNodeResourceAccessControlImpl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.WorkGroupDocumentRevisionService;
import org.linagora.linshare.core.service.WorkGroupDocumentService;
import org.linagora.linshare.core.service.WorkGroupFolderService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.core.utils.FileAndMetaData;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupDocumentRevision;
import org.linagora.linshare.mongo.entities.WorkGroupFolder;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.light.AuditLightEntity;
import org.linagora.linshare.mongo.entities.logs.WorkGroupNodeAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.entities.mto.CopyMto;
import org.linagora.linshare.mongo.entities.mto.NodeMetadataMto;
import org.linagora.linshare.mongo.entities.mto.WorkGroupLightNode;
import org.linagora.linshare.mongo.repository.WorkGroupNodeMongoRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class WorkGroupNodeServiceImpl extends GenericWorkGroupNodeServiceImpl implements WorkGroupNodeService {


	protected final WorkGroupNodeMongoRepository repository;

	protected final WorkGroupDocumentService workGroupDocumentService;

	protected final WorkGroupFolderService workGroupFolderService;

	protected final LogEntryService logEntryService;

	protected final SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService;

	protected final MongoTemplate mongoTemplate;

	protected final FunctionalityReadOnlyService functionalityReadOnlyService;

	protected final WorkGroupDocumentRevisionService revisionService;

	protected final MimeTypeMagicNumberDao mimeTypeIdentifier;

	private static final String MODIFICATION_DATE = "modificationDate";

	protected final WorkGroupNodeBusinessService workGroupNodeBusinessService;

	public WorkGroupNodeServiceImpl(WorkGroupNodeMongoRepository repository,
			LogEntryService logEntryService,
			WorkGroupDocumentService workGroupDocumentService,
			WorkGroupFolderService workGroupFolderService,
			WorkGroupNodeResourceAccessControlImpl rac,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
			MongoTemplate mongoTemplate,
			FunctionalityReadOnlyService functionalityReadOnlyService,
			WorkGroupDocumentRevisionService revisionService,
			MimeTypeMagicNumberDao mimeTypeIdentifier,
			WorkGroupNodeBusinessService workGroupNodeBusinessService) {
		super(rac);
		this.repository = repository;
		this.workGroupDocumentService = workGroupDocumentService;
		this.workGroupFolderService = workGroupFolderService;
		this.logEntryService = logEntryService;
		this.sanitizerInputHtmlBusinessService = sanitizerInputHtmlBusinessService;
		this.mongoTemplate = mongoTemplate;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.revisionService = revisionService;
		this.mimeTypeIdentifier = mimeTypeIdentifier;
		this.workGroupNodeBusinessService = workGroupNodeBusinessService;
	}

	@Override
	public List<WorkGroupNode> findAll(Account actor, User owner, WorkGroup workGroup) throws BusinessException {
		return findAll(actor, owner, workGroup, null, true, Lists.newArrayList(WorkGroupNodeType.DOCUMENT));
	}

	@Override
	public List<WorkGroupNode> findAll(Account actor, Account owner, WorkGroup workGroup, String parentUuid, Boolean flat, List<WorkGroupNodeType> nodeTypes)
			throws BusinessException {
		preChecks(actor, owner);
		Validate.notNull(workGroup, "Missing workGroup");
		checkListPermission(actor, owner, WorkGroupNode.class, BusinessErrorCode.WORK_GROUP_DOCUMENT_FORBIDDEN, null, workGroup);
		// code compliant with older line of codes.
		if (flat == null) {
			flat = false;
		}
		// code compliant with older line of codes.
		if (nodeTypes == null) {
			nodeTypes = Lists.newArrayList();
		}
		Sort defaultSort = new Sort(Sort.Direction.DESC, MODIFICATION_DATE);
		if (flat) {
			if (nodeTypes.isEmpty()) {
				nodeTypes.add(WorkGroupNodeType.DOCUMENT);
				nodeTypes.add(WorkGroupNodeType.FOLDER);
			}
			return repository.findByWorkGroupAndNodeTypes(workGroup.getLsUuid(), nodeTypes,
					defaultSort);
		}
		// if parentUUid is null, it means we want to get the content of the root folder, so we need its uuid.
		if (parentUuid == null) {
			// looking for root the folder
			List<WorkGroupNode> rootList = repository.findByWorkGroupAndParent(workGroup.getLsUuid(), workGroup.getLsUuid());
			if (rootList.isEmpty()) {
				// if there is no root folder, there is no content at all, no need to go further
				return rootList;
			} else {
				// otherwise we retrieve the root folder uuid
				WorkGroupNode root = rootList.get(0);
				parentUuid = root.getUuid();
			}
		}
		if (nodeTypes.isEmpty()) {
			return repository.findByWorkGroupAndParent(workGroup.getLsUuid(), parentUuid);
		}
		return repository.findByWorkGroupAndParentAndNodeTypes(workGroup.getLsUuid(), parentUuid, nodeTypes, defaultSort);
	}

	@Override
	public WorkGroupNode find(Account actor, Account owner, WorkGroup workGroup, String workGroupNodeUuid, boolean withTree)
			throws BusinessException {
		preChecks(actor, owner);
		WorkGroupNode node = null;
		if (workGroup.getLsUuid().equals(workGroupNodeUuid)) {
			// It means we want the root folder.
			node = getRootFolder(owner, workGroup);
		} else {
			node = findNode(workGroup, workGroupNodeUuid);
		}
		checkReadPermission(actor, owner, WorkGroupNode.class, BusinessErrorCode.WORK_GROUP_DOCUMENT_FORBIDDEN, node, workGroup);
		if (withTree) {
			node.setTreePath(getTreePath(workGroup, node));
		}
		return node;
	}

	@Override
	public WorkGroupNode findForDownloadOrCopyRight(Account actor, User owner, WorkGroup workGroup,
			String workGroupNodeUuid) throws BusinessException {
		preChecks(actor, owner);
		WorkGroupNode node = find(actor, owner, workGroup, workGroupNodeUuid, false);
		checkDownloadPermission(actor, owner, WorkGroupNode.class, BusinessErrorCode.WORK_GROUP_DOCUMENT_FORBIDDEN,
				node, workGroup);
		if (!isDocument(node) && !isRevision(node)) {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_OPERATION_UNSUPPORTED,
					"Can not download this kind of node.");
		}
		return node;
	}

	protected List<WorkGroupLightNode> getTreePath(WorkGroup workGroup, WorkGroupNode node) {
		List<WorkGroupLightNode> tree = Lists.newArrayList();
		String path = node.getPath();
		if (path == null) {
			return tree;
		}
		String[] split = path.split(",");
		for (int i = 0; i < split.length; i++) {
			String uuid = split[i];
			if (!uuid.isEmpty()) {
				tree.add(new WorkGroupLightNode(findNode(workGroup, uuid)));
			}
		}
		return tree;
	}

	protected WorkGroupNode findNode(WorkGroup workGroup, String workGroupNodeUuid) {
		WorkGroupNode node = repository.findByWorkGroupAndUuid(workGroup.getLsUuid(), workGroupNodeUuid);
		if (node == null) {
			logger.error("Node not found " + workGroupNodeUuid);
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_NODE_NOT_FOUND,
					"Node not found : " + workGroupNodeUuid);
		}
		return node;
	}

	@Override
	public String findWorkGroupUuid(Account actor, User owner, String workGroupNodeUuid)
			throws BusinessException {
		preChecks(actor, owner);
		WorkGroupNode node = repository.findByUuid(workGroupNodeUuid);
		if (node == null) {
			logger.error("Node  not found " + workGroupNodeUuid);
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_NODE_NOT_FOUND,
					"Node not found : " + workGroupNodeUuid);
		}
		return node.getWorkGroup();
	}

	@Override
	public WorkGroupNode create(Account actor, User owner, WorkGroup workGroup, WorkGroupNode workGroupNode, Boolean strict, Boolean dryRun)
			throws BusinessException {
		preChecks(actor, owner);
		Validate.notNull(workGroupNode.getNodeType(), "You must fill noteType field.");
		checkCreatePermission(actor, owner, WorkGroupNode.class, BusinessErrorCode.WORK_GROUP_DOCUMENT_FORBIDDEN, null, workGroup);
		if (!workGroupNode.getNodeType().equals(WorkGroupNodeType.FOLDER)) {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_OPERATION_UNSUPPORTED, "Can not create this kind of node with this method.");
		}
		WorkGroupNode nodeParent = getParentNode(actor, owner, workGroup, workGroupNode.getParent());
		String fileName = sanitizerInputHtmlBusinessService.sanitizeFileName(workGroupNode.getName());
		if (strict) {
			workGroupFolderService.checkUniqueName(workGroup, nodeParent, fileName);
		} else {
			fileName = workGroupFolderService.getNewName(actor, owner, workGroup, nodeParent, fileName);
		}
		workGroupNode.setName(fileName);
		return workGroupFolderService.create(actor, owner, workGroup, workGroupNode, nodeParent, strict, dryRun);
	}

	@Override
	public WorkGroupNode create(Account actor, User owner, WorkGroup workGroup, File tempFile, String fileName,
			String parentNodeUuid, Boolean strict) throws BusinessException {
		preChecks(actor, owner);
		checkCreatePermission(actor, owner, WorkGroupNode.class, BusinessErrorCode.WORK_GROUP_DOCUMENT_FORBIDDEN, null,
				workGroup);
		fileName = sanitizerInputHtmlBusinessService.sanitizeFileName(fileName);
		if (parentNodeUuid != null && parentNodeUuid.isEmpty()) {
			parentNodeUuid = null;
		}
		WorkGroupNode parentNode = getParentNode(actor, owner, workGroup, parentNodeUuid);
		if (WorkGroupNodeType.DOCUMENT_REVISION.equals(parentNode.getNodeType())) {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_DOCUMENT_FORBIDDEN,
					"Cannot have a Revision as parent node");
		}
		boolean isRevisionFunctionality = revisionService.checkVersioningFunctionality(actor.getDomain(), workGroup);
		boolean isRevisionOnly = false;
		if (WorkGroupNodeType.DOCUMENT.equals(parentNode.getNodeType())) {
			isRevisionOnly = isRevisionFunctionality;
			if (!isRevisionOnly) {
				throw new BusinessException(BusinessErrorCode.WORK_GROUP_DOCUMENT_FORBIDDEN,
						"Cannot have a document as parent when revision functionality is disabled");
			}
		}
		if (!workGroupDocumentService.isUniqueName(workGroup, parentNode, fileName)) {
			isRevisionOnly = isRevisionFunctionality;
			if (!isRevisionOnly) {
				if (strict) {
					workGroupDocumentService.checkUniqueName(workGroup, parentNode, fileName);
				}
				fileName = workGroupDocumentService.getNewName(actor, owner, workGroup, parentNode, fileName);
			}
		}
		Long size = tempFile.length();
		String mimeType = mimeTypeIdentifier.getMimeType(tempFile);
		if (isRevisionOnly) {
			WorkGroupDocumentRevision documentRevision = revisionService.create(actor, owner, workGroup, tempFile,
					fileName, parentNode);
			revisionService.updateDocument(actor, (Account) owner, workGroup, documentRevision);
			return documentRevision;
		} else {
			WorkGroupNode document = workGroupDocumentService.createWithoutLogStorage(actor, owner, workGroup, size, mimeType, fileName,
					parentNode);
			WorkGroupDocumentRevision revision = revisionService.create(actor, owner, workGroup, tempFile, fileName, document);
			WorkGroupNodeAuditLogEntry log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.CREATE,
					AuditLogEntryType.WORKGROUP_DOCUMENT, document, workGroup);
			workGroupDocumentService.addMembersToLog(workGroup.getLsUuid(), log);
			log.addRelatedResources(revision.getUuid());
			logEntryService.insert(log);
			return document;
		}
	}

	@Override
	public WorkGroupNode update(Account actor, Account owner, WorkGroup workGroup, WorkGroupNode dto)
			throws BusinessException {
		preChecks(actor, owner);
		Validate.notNull(dto);
		Validate.notEmpty(dto.getUuid(), "Missing uuid");
		Validate.notEmpty(dto.getName(), "Missing name");
		WorkGroupNode node = find(actor, owner, workGroup, dto.getUuid(), false);
		if (isRevision(node)) {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_OPERATION_UNSUPPORTED, "Can not update a revision.");
		}
		checkUpdatePermission(actor, owner, WorkGroupNode.class, BusinessErrorCode.WORK_GROUP_DOCUMENT_FORBIDDEN, node, workGroup);
		WorkGroupNodeAuditLogEntry log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.UPDATE, getAuditLogEntryType(node.getNodeType()), node, workGroup);
		// we add the full path of the current node to the audit trace.
		log.getResource().setTreePath(getTreePath(workGroup, node));
		String originalName = node.getName();
		String originalPath = node.getPath();

		node.setName(dto.getName());
		node.setDescription(dto.getDescription());
		node.setModificationDate(new Date());
		node.setMetaData(dto.getMetaData());
		node.setLastAuthor(new AccountMto(owner, true));
		// Just in case.
		node.setTreePath(null);

		if (node.getNodeType().equals(WorkGroupNodeType.ROOT_FOLDER)) {
			return repository.save(node);
		}
		boolean updatePath = false;
		WorkGroupNode nodeParent = null;
		// Check if we have to move the current node
		if (dto.getParent() != null) {
			String newParentUuid = dto.getParent();
			nodeParent = find(actor, owner, workGroup, newParentUuid, false);
			if (!node.getParent().equals(newParentUuid)) {
				// We need to move the node
				if (!isFolder(nodeParent)) {
					throw new BusinessException(BusinessErrorCode.WORK_GROUP_OPERATION_UNSUPPORTED, "Can not move the current node to this kind of node.");
				}
				// Check if name is unique in the parent node (target)
				checkUniqueName(workGroup, nodeParent, dto.getName(), node.getNodeType());
				node.setParent(nodeParent.getUuid());
				node.setPathFromParent(nodeParent);
				updatePath = true;
			} else {
				// Check if name is unique in the parent node (current)
				if (!node.getName().equals(originalName)) {
					checkUniqueName(workGroup, nodeParent, dto.getName(), node.getNodeType());
				}
			}
		} else {
			nodeParent = find(actor, owner, workGroup, node.getParent(), false);
			if (!node.getName().equals(originalName)) {
				// Check if name is unique in the parent node (current or target)
				checkUniqueName(workGroup, nodeParent, node.getName(), node.getNodeType());
			}
		}
		node = repository.save(node);
		// updating the new treePath.
		log.initResourceUpdated(node);
		log.getResourceUpdated().setTreePath(getTreePath(workGroup, node));
		workGroupDocumentService.addMembersToLog(workGroup.getLsUuid(), log);
		logEntryService.insert(log);
		if (updatePath) {
			Query query = new Query();
			query.addCriteria(Criteria.where("workGroup").is(workGroup.getLsUuid()));
			String newFullPath = node.getPath() + node.getUuid() + ",";
			String pattern = "^" + originalPath + node.getUuid() + ",";
			query.addCriteria(Criteria.where("path").regex(pattern));
			List<WorkGroupNode> allSubNodes = mongoTemplate.find(query, WorkGroupNode.class);
			for (WorkGroupNode n : allSubNodes) {
				if (logger.isTraceEnabled())
					logger.trace("Node : " + n.getName() + " : " + n.getPath());
				n.setPath(n.getPath().replaceAll(pattern, newFullPath));
				if (logger.isTraceEnabled())
					logger.trace("Node : " + n.getName() + " : " + n.getPath());
			}
			repository.saveAll(allSubNodes);
		}
		return node;
	}

	protected void checkUniqueName(WorkGroup workGroup, WorkGroupNode nodeParent, String name, WorkGroupNodeType nodeType) {
		if (WorkGroupNodeType.DOCUMENT.equals(nodeType)) {
				workGroupDocumentService.checkUniqueName(workGroup, nodeParent, name);
		}
		workGroupFolderService.checkUniqueName(workGroup, nodeParent, name);
	}

	protected AuditLogEntryType getAuditLogEntryType(WorkGroupNodeType type) {
		if (type.equals(WorkGroupNodeType.FOLDER) || type.equals(WorkGroupNodeType.ROOT_FOLDER)) {
			return AuditLogEntryType.WORKGROUP_FOLDER;
		} else if (type.equals(WorkGroupNodeType.DOCUMENT)) {
			return AuditLogEntryType.WORKGROUP_DOCUMENT;
		} else {
			throw new BusinessException(BusinessErrorCode.UNKNOWN, "invalid type");
		}
	}

	@Override
	public WorkGroupNode delete(Account actor, Account owner, WorkGroup workGroup, String workGroupNodeUuid)
			throws BusinessException {
		preChecks(actor, owner);
		Validate.notEmpty(workGroupNodeUuid, "missing workGroupNodeUuid");
		WorkGroupNode node = find(actor, owner, workGroup, workGroupNodeUuid, false);
		checkDeletePermission(actor, owner, WorkGroupNode.class, BusinessErrorCode.WORK_GROUP_DOCUMENT_FORBIDDEN,
				node, workGroup);
		AuditLogEntryType auditType = AuditLogEntryType.getWorkgroupAuditType(node.getNodeType());
		WorkGroupNodeAuditLogEntry log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.DELETE, auditType, node,
				workGroup);
		workGroupDocumentService.addMembersToLog(workGroup.getLsUuid(), log);
		deleteNode(actor, owner, workGroup, node, log);
		logEntryService.insert(log);
		return node;
	}

	private void deleteNode(Account actor, Account owner, WorkGroup workGroup, WorkGroupNode workGroupNode,
			WorkGroupNodeAuditLogEntry log) {
		if (isFolder(workGroupNode)) {
			List<WorkGroupNode> findAll = findAll(actor, owner, workGroup, workGroupNode.getUuid(), false, Lists.newArrayList());
			for (WorkGroupNode node : findAll) {
				deleteNode(actor, owner, workGroup, node, log);
			}
			if (!findAll.isEmpty()) {
				log.addAuditLightEntities(transformToAuditLightEntity(findAll));
				log.addRelatedResources(getListUuid(findAll));
			}
			workGroupFolderService.delete(actor, owner, workGroup, workGroupNode);
		} else if (isDocument(workGroupNode)) {
//			Delete all revisions into the document.
			List<WorkGroupNode> revisions = repository.findByWorkGroupAndParentAndNodeType(workGroup.getLsUuid(),
					workGroupNode.getUuid(), WorkGroupNodeType.DOCUMENT_REVISION);
			log.addRelatedResources(getListUuid(revisions));
			log.addAuditLightEntities(transformToAuditLightEntity(revisions));
			revisionService.deleteAll(actor, owner, workGroup, revisions);
//			Delete the document.
			workGroupDocumentService.delete(actor, owner, workGroup, workGroupNode);
		} else if (isRevision(workGroupNode)) {
			log.addRelatedResources(workGroupNode.getParent());
			revisionService.delete(actor, owner, workGroup, workGroupNode);
		} else {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_OPERATION_UNSUPPORTED,
					"Can not delete this type of node, type not supported.");
		}
	}

	protected List<String> getListUuid(List<WorkGroupNode> findAll) {
		return findAll.stream().map(WorkGroupNode::getUuid).collect(Collectors.toList());
	}

	protected List<AuditLightEntity> transformToAuditLightEntity(List<WorkGroupNode> nodes) {
		return nodes.stream().map(node -> new AuditLightEntity(node.getUuid(), node.getName(), node.getNodeType()))
				.collect(Collectors.toList());
	}

	@Override
	public FileAndMetaData thumbnail(Account actor, User owner, WorkGroup workGroup, String workGroupNodeUuid,
			ThumbnailType kind) throws BusinessException {
		preChecks(actor, owner);
		WorkGroupNode node = find(actor, owner, workGroup, workGroupNodeUuid, false);
		checkThumbNailDownloadPermission(actor, owner, WorkGroupNode.class,
				BusinessErrorCode.WORK_GROUP_DOCUMENT_FORBIDDEN, node, workGroup);
		if (isDocument(node)) {
			node = revisionService.findMostRecent(workGroup, node.getUuid());
			InputStream stream = workGroupDocumentService.getThumbnailStream(actor, owner, workGroup,
					(WorkGroupDocument) node, kind);
			return new FileAndMetaData((WorkGroupDocument) node, stream);
		} else if (isRevision(node)) {
			InputStream stream = workGroupDocumentService.getThumbnailStream(actor, owner, workGroup,
					(WorkGroupDocument) node, kind);
			return new FileAndMetaData((WorkGroupDocument) node, stream);
		} else {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_OPERATION_UNSUPPORTED,
					"Can not get thumbnail for this kind of node.");
		}
	}

	@Override
	public FileAndMetaData download(Account actor, User owner, WorkGroup workGroup, String workGroupNodeUuid)
			throws BusinessException {
		preChecks(actor, owner);
		WorkGroupNode node = find(actor, owner, workGroup, workGroupNodeUuid, false);
		checkDownloadPermission(actor, owner, WorkGroupNode.class, BusinessErrorCode.WORK_GROUP_DOCUMENT_FORBIDDEN,
				node, workGroup);
		if (isDocument(node)) {
			WorkGroupDocumentRevision revision = (WorkGroupDocumentRevision) revisionService.findMostRecent(workGroup,
					node.getUuid());
			return workGroupDocumentService.download(actor, owner, workGroup, (WorkGroupDocument) node, revision);
		} else if (isRevision(node)) {
			WorkGroupNode documentParent = workGroupDocumentService.find(actor, owner, workGroup, node.getParent());
			return revisionService.download(actor, owner, workGroup, (WorkGroupDocument) documentParent,
					(WorkGroupDocumentRevision) node);
		} else if (isFolder(node)) {
			String pattern = "^" + node.getPath() + node.getUuid();
			if (!workGroupNodeBusinessService.downloadIsAllowed(workGroup, pattern)) {
				throw new BusinessException(BusinessErrorCode.WORK_GROUP_NODE_DOWNLOAD_FORBIDDEN,
						"Can not download this folder, The Folder size is too large. ");
			}
			Map<String, WorkGroupNode> nodes = workGroupNodeBusinessService.findAllSubNodes(workGroup, pattern);
			List<WorkGroupNode> documentNodes = workGroupNodeBusinessService.findAllSubDocuments(workGroup, pattern);
			WorkGroupNodeAuditLogEntry log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.DOWNLOAD,
					AuditLogEntryType.WORKGROUP_FOLDER, node, workGroup);
			FileAndMetaData dataFile = workGroupNodeBusinessService.downloadFolder(actor, owner, workGroup, node, nodes,
					documentNodes, log);
			workGroupDocumentService.addMembersToLog(workGroup.getLsUuid(), log);
			logEntryService.insert(log);
			return dataFile;
		} else {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_OPERATION_UNSUPPORTED,
					"Can not download this kind of node.");
		}
	}

	@Override
	public WorkGroupNode copy(Account actor, User owner, WorkGroup toWorkGroup, String toNodeUuid, CopyResource cr)
			throws BusinessException {
		preChecks(actor, owner);
		Validate.notEmpty(cr.getDocumentUuid(), "Missing documentUuid");
		Validate.notEmpty(cr.getName(), "Missing fileName");
		checkCreatePermission(actor, owner, WorkGroupNode.class, BusinessErrorCode.WORK_GROUP_DOCUMENT_FORBIDDEN, null, toWorkGroup);
		String fileName = sanitizerInputHtmlBusinessService.sanitizeFileName(cr.getName());
		if (toNodeUuid != null && toNodeUuid.isEmpty()) {
			toNodeUuid = null;
		}
		WorkGroupNode nodeParent = getParentNode(actor, owner, toWorkGroup, toNodeUuid);
		fileName = workGroupDocumentService.getNewName(actor, owner, toWorkGroup, nodeParent, fileName);
		WorkGroupNode newWGDocument = workGroupDocumentService.createWithoutLogStorage(actor, owner, toWorkGroup, cr.getSize(),
				cr.getMimeType(), fileName, nodeParent);
		AuditLogEntryType auditType = AuditLogEntryType.getWorkgroupAuditType(newWGDocument.getNodeType());
		WorkGroupNodeAuditLogEntry log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.CREATE,
				auditType, newWGDocument, toWorkGroup);
		log.setCause(LogActionCause.COPY);
		workGroupDocumentService.copy(actor, owner, toWorkGroup, cr.getDocumentUuid(), fileName, newWGDocument,
				cr.getCiphered(), cr.getSize(), cr.getResourceUuid(), cr.getCopyFrom(), log);
		return newWGDocument;
	}

	@Override
	public void markAsCopied(Account actor, Account owner, WorkGroup workGroup, WorkGroupNode wgNode, CopyMto copiedTo)
			throws BusinessException {
		if ((isDocument(wgNode) || (isRevision(wgNode)))) {
			workGroupDocumentService.markAsCopied(actor, owner, workGroup, wgNode, copiedTo);
		} else {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_OPERATION_UNSUPPORTED,
					"Can not copy this kind of node.");
		}
	}

	@Override
	public WorkGroupNode copy(Account actor, User owner, WorkGroup fromWorkGroup, String fromNodeUuid,
			WorkGroup toWorkGroup, String toNodeUuid) throws BusinessException {
		// step 1 : check the source
		WorkGroupNode fromNode = find(actor, owner, fromWorkGroup, fromNodeUuid, false);
		checkDownloadPermission(actor, owner, WorkGroupNode.class, BusinessErrorCode.WORK_GROUP_DOCUMENT_FORBIDDEN, fromNode, fromWorkGroup);
		// step 2 : check the destination
		WorkGroupNode toNode = null;
		if (Strings.isNullOrEmpty(toNodeUuid)) {
			// in the root folder
			toNode = getRootFolder(owner, toWorkGroup);
		} else {
			toNode = find(actor, owner, toWorkGroup, toNodeUuid, false);
			if (isRevision(toNode)) {
				throw new BusinessException(BusinessErrorCode.WORK_GROUP_OPERATION_UNSUPPORTED,
						"Can not copy to this kind of node.");
			}
		}
		checkCreatePermission(actor, owner, WorkGroupNode.class, BusinessErrorCode.WORK_GROUP_DOCUMENT_FORBIDDEN, toNode, toWorkGroup);
		if (isDocument(fromNode)) {
			WorkGroupDocumentRevision mostRecent = (WorkGroupDocumentRevision) revisionService.findMostRecent(fromWorkGroup,
					fromNode.getUuid());
			WorkGroupDocument doc = (WorkGroupDocument) fromNode;
			CopyMto copyFrom = new CopyMto(doc, fromWorkGroup);
			String fileName = workGroupDocumentService.computeFileName(doc, mostRecent, true);
			if (isFolder(toNode)) {
				// members of the "recipient workgroup" do not need to know the name of the source workgroup.
				fileName = workGroupDocumentService.getNewName(actor, owner, toWorkGroup, toNode, fileName);
				WorkGroupDocument newDocument = (WorkGroupDocument) workGroupDocumentService.createWithoutLogStorage(actor, owner,
						toWorkGroup, doc.getSize(), doc.getMimeType(), fileName, toNode);
				WorkGroupNodeAuditLogEntry log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.CREATE,
						AuditLogEntryType.WORKGROUP_DOCUMENT, newDocument, toWorkGroup);
				log.setCause(LogActionCause.COPY);
				// copy the most recent revision into the new document.
				workGroupDocumentService.copy(actor, owner, toWorkGroup, mostRecent.getDocumentUuid(), fileName,
						newDocument, doc.getCiphered(), doc.getSize(), doc.getUuid(), copyFrom, log);
				CopyMto copiedTo = new CopyMto(newDocument, toWorkGroup);
				workGroupDocumentService.markAsCopied(actor, owner, fromWorkGroup, fromNode, copiedTo);
				return newDocument;
			} else if (isDocument(toNode)) {
				// Check versioning functionality
				if (!revisionService.checkVersioningFunctionality(actor.getDomain(), toWorkGroup)) {
					throw new BusinessException(BusinessErrorCode.WORK_GROUP_OPERATION_UNSUPPORTED, "Can not copy to this kind of node.");
				}
				// copy the most recent revision into the document.
				WorkGroupDocumentRevision revision = (WorkGroupDocumentRevision) workGroupDocumentService.copy(actor,
						owner, fromWorkGroup, mostRecent.getDocumentUuid(), fileName, toNode, doc.getCiphered(),
						doc.getSize(), doc.getUuid(), copyFrom, null);
				toNode = revisionService.updateDocument(actor, owner, toWorkGroup, revision);
				CopyMto copiedTo = new CopyMto(revision, toWorkGroup);
				workGroupDocumentService.markAsCopied(actor, owner, fromWorkGroup, fromNode, copiedTo);
				return toNode;
			}
		} else if (isFolder(fromNode)) {
			if (isFolder(toNode)) {
				// TODO:FMA:workgroups manage folder and nested folders.
			}
		} else if (isRevision(fromNode)) {
			WorkGroupDocumentRevision revision = (WorkGroupDocumentRevision) fromNode;
			WorkGroupDocument parent = (WorkGroupDocument) workGroupDocumentService.find(actor, owner, fromWorkGroup,
					revision.getParent());
			String fileName = workGroupDocumentService.computeFileName(parent, revision, false);
			CopyMto copyFrom = new CopyMto(revision, fromWorkGroup);
			if (isFolder(toNode)) {
				// Create a new document from this revision
				fileName = workGroupDocumentService.getNewName(actor, owner, toWorkGroup, toNode, fileName);
				WorkGroupDocument newDocument = (WorkGroupDocument) workGroupDocumentService.createWithoutLogStorage(actor, owner,
						toWorkGroup, revision.getSize(), revision.getMimeType(), fileName, toNode);
				WorkGroupNodeAuditLogEntry log = new WorkGroupNodeAuditLogEntry(actor, owner, LogAction.CREATE,
						AuditLogEntryType.WORKGROUP_DOCUMENT, newDocument, toWorkGroup);
				log.setCause(LogActionCause.COPY);
				// copy the revision into the new document.
				workGroupDocumentService.copy(actor, owner, toWorkGroup, revision.getDocumentUuid(), fileName,
						newDocument, revision.getCiphered(), revision.getSize(), revision.getUuid(), copyFrom, log);
				CopyMto copiedTo = new CopyMto(newDocument, toWorkGroup);
				workGroupDocumentService.markAsCopied(actor, owner, fromWorkGroup, fromNode, copiedTo);
				return newDocument;
			} else 	if (isDocument(toNode)) {
				// Check versioning functionality
				if (!revisionService.checkVersioningFunctionality(actor.getDomain(), toWorkGroup)) {
					throw new BusinessException(BusinessErrorCode.WORK_GROUP_OPERATION_UNSUPPORTED, "Can not copy to this kind of node.");
				}
				// Copy the revision into the document.
				WorkGroupDocumentRevision newRevision = (WorkGroupDocumentRevision) workGroupDocumentService.copy(actor,
						owner, fromWorkGroup, revision.getDocumentUuid(), fileName, toNode,
						revision.getCiphered(), revision.getSize(), revision.getUuid(), copyFrom, null);
				toNode = revisionService.updateDocument(actor, owner, toWorkGroup, newRevision);
				CopyMto copiedTo = new CopyMto(newRevision, toWorkGroup);
				workGroupDocumentService.markAsCopied(actor, owner, fromWorkGroup, fromNode, copiedTo);
				return toNode;
			}
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_OPERATION_UNSUPPORTED, "Can not copy this kind of node.");
		}
		throw new BusinessException(BusinessErrorCode.WORK_GROUP_OPERATION_UNSUPPORTED, "Can not copy this kind of node.");
	}

	@Override
	public WorkGroupNode getRootFolder(Account actor, Account owner, WorkGroup workGroup) {
		checkListPermission(actor, owner, WorkGroupNode.class, BusinessErrorCode.WORK_GROUP_DOCUMENT_FORBIDDEN, null, workGroup);
		return getRootFolder(owner, workGroup);
	}

	@Override
	public NodeMetadataMto findMetadata(User authUser, User actor, WorkGroup workGroup, WorkGroupNode node,
			boolean storage) {
		preChecks(authUser, actor);
		checkListPermission(authUser, actor, WorkGroupNode.class, BusinessErrorCode.WORK_GROUP_DOCUMENT_FORBIDDEN, null,
				workGroup);
		if (isRevision(node)) {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_OPERATION_UNSUPPORTED,
					"Can not retrieve the details for this kind of node.");
		}
		String pattern = Strings.isNullOrEmpty(node.getPath()) ? "^," + node.getUuid()
				: "^" + node.getPath() + node.getUuid();
		NodeMetadataMto metaData = new NodeMetadataMto(node.getUuid(), node.getNodeType());
		if (storage) {
			metaData.setStorageSize(workGroupNodeBusinessService.computeNodeSize(workGroup, pattern,
					WorkGroupNodeType.DOCUMENT_REVISION));
		}
		metaData.setSize(getSize(workGroup, pattern, node));
		metaData.setCount(workGroupNodeBusinessService.computeNodeCount(workGroup, pattern, node));
		return metaData;
	}

	private Long getSize(WorkGroup workGroup, String pattern, WorkGroupNode node) {
		if (isDocument(node)) {
			return ((WorkGroupDocument) node).getSize();
		}
		return workGroupNodeBusinessService.computeNodeSize(workGroup, pattern, WorkGroupNodeType.DOCUMENT);
	}

	protected WorkGroupNode getRootFolder(Account owner, WorkGroup workGroup) {
		WorkGroupNode wgnParent = null;
		String workGroupUuid = workGroup.getLsUuid();
		List<WorkGroupNode> results = repository.findByWorkGroupAndParent(workGroupUuid, workGroupUuid);
		wgnParent = DataAccessUtils.singleResult(results);
		if (wgnParent == null) {
			// creation of the root folder.
			wgnParent = new WorkGroupFolder(new AccountMto(owner), workGroup.getName(), workGroupUuid, workGroupUuid);
			wgnParent.setNodeType(WorkGroupNodeType.ROOT_FOLDER);
			wgnParent.setUuid(UUID.randomUUID().toString());
			wgnParent.setCreationDate(new Date());
			wgnParent.setModificationDate(new Date());
			wgnParent = repository.insert(wgnParent);
		}
		return wgnParent;
	}

	protected WorkGroupNode getParentNode(Account actor, Account owner, WorkGroup workGroup, String parentNodeUuid) {
		WorkGroupNode nodeParent = null;
		if (parentNodeUuid == null) {
			nodeParent = getRootFolder(owner, workGroup);
		} else {
			if (parentNodeUuid.equals(workGroup.getLsUuid())) {
				nodeParent = getRootFolder(owner, workGroup);
			} else {
				nodeParent = find(actor, owner, workGroup, parentNodeUuid, false);
			}
		}
		return nodeParent;
	}

	protected boolean isDocument(WorkGroupNode node) {
		return node.getNodeType().equals(WorkGroupNodeType.DOCUMENT);
	}

	protected boolean isFolder(WorkGroupNode node) {
		return node.getNodeType().equals(WorkGroupNodeType.FOLDER) || node.getNodeType().equals(WorkGroupNodeType.ROOT_FOLDER);
	}

	protected boolean isRevision(WorkGroupNode node) {
		return node.getNodeType().equals(WorkGroupNodeType.DOCUMENT_REVISION);
	}

	@Override
	public WorkGroupNode findByWorkGroupNodeUuid(String uuid) {
		return repository.findByUuid(uuid);
	}

}
