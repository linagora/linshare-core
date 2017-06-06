/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2017. Contribute to
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
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.impl.WorkGroupNodeResourceAccessControlImpl;
import org.linagora.linshare.core.service.AntiSamyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.WorkGroupDocumentService;
import org.linagora.linshare.core.service.WorkGroupFolderService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.core.utils.FileAndMetaData;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.WorkGroupFolder;
import org.linagora.linshare.mongo.entities.logs.WorkGroupNodeAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.entities.mto.WorkGroupLightNode;
import org.linagora.linshare.mongo.repository.WorkGroupNodeMongoRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.google.common.collect.Lists;

public class WorkGroupNodeServiceImpl extends GenericWorkGroupNodeServiceImpl implements WorkGroupNodeService {


	protected final WorkGroupNodeMongoRepository repository;

	protected final WorkGroupDocumentService workGroupDocumentService;

	protected final WorkGroupFolderService workGroupFolderService;

	protected final LogEntryService logEntryService;

	protected final AntiSamyService antiSamyService;

	protected final MongoTemplate mongoTemplate;

	public WorkGroupNodeServiceImpl(WorkGroupNodeMongoRepository repository, LogEntryService logEntryService,
			WorkGroupDocumentService workGroupDocumentService,
			WorkGroupFolderService workGroupFolderService,
			WorkGroupNodeResourceAccessControlImpl rac,
			AntiSamyService antiSamyService,
			MongoTemplate mongoTemplate) {
		super(rac);
		this.repository = repository;
		this.workGroupDocumentService = workGroupDocumentService;
		this.workGroupFolderService = workGroupFolderService;
		this.logEntryService = logEntryService;
		this.antiSamyService = antiSamyService;
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public List<WorkGroupNode> findAll(Account actor, User owner, Thread workGroup) throws BusinessException {
		return findAll(actor, owner, workGroup, null, true, null);
	}

	@Override
	public List<WorkGroupNode> findAll(Account actor, User owner, Thread workGroup, String parentUuid, Boolean flatDocumentMode, WorkGroupNodeType nodeType)
			throws BusinessException {
		preChecks(actor, owner);
		Validate.notNull(workGroup, "Missing workGroup");
		checkListPermission(actor, owner, WorkGroupNode.class, BusinessErrorCode.WORK_GROUP_DOCUMENT_FORBIDDEN, null, workGroup);
		if (flatDocumentMode == null)
			flatDocumentMode = false;
		if (flatDocumentMode) {
			return repository.findByWorkGroupAndNodeType(workGroup.getLsUuid(), WorkGroupNodeType.DOCUMENT);
		}
		if (parentUuid == null) {
			List<WorkGroupNode> rootList = repository.findByWorkGroupAndParent(workGroup.getLsUuid(), workGroup.getLsUuid());
			if (rootList.isEmpty()) {
				return rootList;
			}
			WorkGroupNode root = rootList.get(0);
			parentUuid = root.getUuid();
		}
		Validate.notEmpty(parentUuid, "Missing workGroup parentUuid");
		if (nodeType != null) {
			return repository.findByWorkGroupAndParentAndNodeType(workGroup.getLsUuid(), parentUuid, nodeType);
		}
		return repository.findByWorkGroupAndParent(workGroup.getLsUuid(), parentUuid);
	}

	@Override
	public WorkGroupNode find(Account actor, User owner, Thread workGroup, String workGroupNodeUuid, boolean withTree)
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

	protected List<WorkGroupLightNode> getTreePath(Thread workGroup, WorkGroupNode node) {
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

	protected WorkGroupNode findNode(Thread workGroup, String workGroupNodeUuid) {
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
	public WorkGroupNode create(Account actor, User owner, Thread workGroup, WorkGroupNode workGroupNode, Boolean strict, Boolean dryRun)
			throws BusinessException {
		preChecks(actor, owner);
		Validate.notNull(workGroupNode.getNodeType(), "You must fill noteType field.");
		checkCreatePermission(actor, owner, WorkGroupNode.class, BusinessErrorCode.WORK_GROUP_DOCUMENT_FORBIDDEN, null, workGroup);
		if (!workGroupNode.getNodeType().equals(WorkGroupNodeType.FOLDER)) {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_OPERATION_UNSUPPORTED, "Can not create this kind of node with this method.");
		}
		WorkGroupNode nodeParent = getParentNode(actor, owner, workGroup, workGroupNode.getParent());
		String fileName = sanitizeFileName(workGroupNode.getName());
		if (strict) {
			workGroupFolderService.checkUniqueName(workGroup, nodeParent, fileName);
		} else {
			fileName = workGroupFolderService.getNewName(actor, owner, workGroup, nodeParent, fileName);
		}
		workGroupNode.setName(fileName);
		return workGroupFolderService.create(actor, owner, workGroup, workGroupNode, nodeParent, strict, dryRun);
	}

	@Override
	public WorkGroupNode create(Account actor, User owner, Thread workGroup, File tempFile, String fileName,
			String parentNodeUuid, Boolean strict) throws BusinessException {
		preChecks(actor, owner);
		checkCreatePermission(actor, owner, WorkGroupNode.class, BusinessErrorCode.WORK_GROUP_DOCUMENT_FORBIDDEN, null, workGroup);
		fileName = sanitizeFileName(fileName);
		if (parentNodeUuid != null) {
			if (parentNodeUuid.isEmpty()) {
				parentNodeUuid = null;
			}
		}
		WorkGroupNode nodeParent = getParentNode(actor, owner, workGroup, parentNodeUuid);
		if (strict) {
			workGroupDocumentService.checkUniqueName(workGroup, nodeParent, fileName);
		} else {
			fileName = workGroupDocumentService.getNewName(actor, owner, workGroup, nodeParent, fileName);
		}
		WorkGroupNode dto = workGroupDocumentService.create(actor, owner, workGroup, tempFile, fileName, nodeParent);
		return dto;
	}

	@Override
	public WorkGroupNode update(Account actor, User owner, Thread workGroup, WorkGroupNode dto)
			throws BusinessException {
		preChecks(actor, owner);
		Validate.notNull(dto);
		Validate.notEmpty(dto.getUuid(), "Missing uuid");
		Validate.notEmpty(dto.getName(), "Missing name");
		WorkGroupNode node = find(actor, owner, workGroup, dto.getUuid(), false);
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
				checkUniqueName(workGroup, nodeParent, dto.getName(), node.getNodeType());
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
			repository.save(allSubNodes);
		}
		return node;
	}

	protected void checkUniqueName(Thread workGroup, WorkGroupNode nodeParent, String name, WorkGroupNodeType nodeType) {
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
	public WorkGroupNode delete(Account actor, User owner, Thread workGroup, String workGroupNodeUuid)
			throws BusinessException {
		preChecks(actor, owner);
		Validate.notEmpty(workGroupNodeUuid, "missing workGroupNodeUuid");
		WorkGroupNode node = find(actor, owner, workGroup, workGroupNodeUuid, false);
		checkDeletePermission(actor, owner, WorkGroupNode.class, BusinessErrorCode.WORK_GROUP_DOCUMENT_FORBIDDEN,
				node, workGroup);
		deleteNode(actor, owner, workGroup, node);
		return node;
	}

	private void deleteNode(Account actor, User owner, Thread workGroup, WorkGroupNode workGroupNode) {
		if (isFolder(workGroupNode)) {
			List<WorkGroupNode> findAll = findAll(actor, owner, workGroup, workGroupNode.getUuid(), false, null);
			for (WorkGroupNode node : findAll) {
				deleteNode(actor, owner, workGroup, node);
			}
			workGroupFolderService.delete(actor, owner, workGroup, workGroupNode);
		} else if (isDocument(workGroupNode)) {
			workGroupDocumentService.delete(actor, owner, workGroup, workGroupNode);
		} else {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_OPERATION_UNSUPPORTED, "Can not delete this type of node, type not supported.");
		}
	}

	@Override
	public FileAndMetaData thumbnail(Account actor, User owner, Thread workGroup, String workGroupNodeUuid) throws BusinessException {
		preChecks(actor, owner);
		WorkGroupNode node = find(actor, owner, workGroup, workGroupNodeUuid, false);
		checkThumbNailDownloadPermission(actor, owner, WorkGroupNode.class, BusinessErrorCode.WORK_GROUP_DOCUMENT_FORBIDDEN, node, workGroup);
		if (isDocument(node)) {
			InputStream stream = workGroupDocumentService.getThumbnailStream(actor, owner, workGroup, (WorkGroupDocument) node);
			return new FileAndMetaData((WorkGroupDocument)node, stream);
		} else {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_OPERATION_UNSUPPORTED, "Can not get thumbnail for this kind of node.");
		}
	}

	@Override
	public FileAndMetaData download(Account actor, User owner, Thread workGroup, String workGroupNodeUuid)
			throws BusinessException {
		preChecks(actor, owner);
		WorkGroupNode node = find(actor, owner, workGroup, workGroupNodeUuid, false);
		checkDownloadPermission(actor, owner, WorkGroupNode.class, BusinessErrorCode.WORK_GROUP_DOCUMENT_FORBIDDEN, node, workGroup);
		if (isDocument(node)) {
			InputStream stream = workGroupDocumentService.getDocumentStream(actor, owner, workGroup, (WorkGroupDocument) node);
			return new FileAndMetaData((WorkGroupDocument)node, stream);
		} else {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_OPERATION_UNSUPPORTED, "Can not download this kind of node.");
		}
	}

	@Override
	public WorkGroupNode copy(Account actor, User owner, Thread workGroup, DocumentEntry documentEntry,
			WorkGroupNode nodeParent) throws BusinessException {
		return workGroupDocumentService.copy(actor, owner, workGroup, documentEntry, nodeParent);
	}

	@Override
	public WorkGroupNode copy(Account actor, User owner, Thread workGroup, String workGroupNodeUuid, String destinationNodeUuid)
			throws BusinessException {
		WorkGroupNode node = find(actor, owner, workGroup, workGroupNodeUuid, false);
		checkCreatePermission(actor, owner, WorkGroupNode.class, BusinessErrorCode.WORK_GROUP_DOCUMENT_FORBIDDEN, null, workGroup);
		if (destinationNodeUuid != null) {
			if (destinationNodeUuid.isEmpty()) {
				destinationNodeUuid = node.getParent();
			}
		} else {
			destinationNodeUuid = node.getParent();
		}
		WorkGroupNode nodeParent = find(actor, owner, workGroup, destinationNodeUuid, false);
		if (isDocument(node)) {
			if (isFolder(nodeParent)) {
				String fileName = workGroupDocumentService.getNewName(actor, owner, workGroup, nodeParent, node.getName());
				return workGroupDocumentService.copy(actor, owner, workGroup, (WorkGroupDocument) node, nodeParent, fileName);
			} else if (isDocument(nodeParent)) {
				// TODO new feature : create a new revision for this file.
				throw new BusinessException(BusinessErrorCode.WORK_GROUP_OPERATION_UNSUPPORTED, "Can not copy this kind of node.");
			}
		} else if (isFolder(node)) {
			if (isFolder(nodeParent)) {
				// TODO:FMA:workgroups manage folder and nested folders.
			}
		} else if (isRevison(node)) {
			// TODO manage revisions.
			if (isFolder(nodeParent)) {
				// TODO create a new document from this revision
			} else 	if (isDocument(nodeParent)) {
				// TODO restore current document with this revision
			}
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_OPERATION_UNSUPPORTED, "Can not copy this kind of node.");
		}
		throw new BusinessException(BusinessErrorCode.WORK_GROUP_OPERATION_UNSUPPORTED, "Can not copy this kind of node.");
	}

	@Override
	public WorkGroupNode getRootFolder(Account actor, User owner, Thread workGroup) {
		checkListPermission(actor, owner, WorkGroupNode.class, BusinessErrorCode.WORK_GROUP_DOCUMENT_FORBIDDEN, null, workGroup);
		return getRootFolder(owner, workGroup);
	}

	protected WorkGroupNode getRootFolder(User owner, Thread workGroup) {
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

	protected WorkGroupNode getParentNode(Account actor, User owner, Thread workGroup, String parentNodeUuid) {
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

	protected String sanitizeFileName(String fileName) throws BusinessException {
		fileName = fileName.replace("\\", "_");
		fileName = fileName.replace(":", "_");
		fileName = fileName.replace("?", "_");
		fileName = fileName.replace("^", "_");
		fileName = fileName.replace(",", "_");
		fileName = fileName.replace("<", "_");
		fileName = fileName.replace(">", "_");
		fileName = fileName.replace("*", "_");
		fileName = fileName.replace("/", "_");
		fileName = fileName.replace("\"", "_");
		fileName = fileName.replace("|", "_");
		fileName = antiSamyService.clean(fileName);
		if (fileName.isEmpty()) {
			throw new BusinessException(BusinessErrorCode.INVALID_FILENAME, "fileName is empty after the xss filter");
		}
		return fileName;
	}

	protected boolean isDocument(WorkGroupNode node) {
		return node.getNodeType().equals(WorkGroupNodeType.DOCUMENT);
	}

	protected boolean isFolder(WorkGroupNode node) {
		return node.getNodeType().equals(WorkGroupNodeType.FOLDER) || node.getNodeType().equals(WorkGroupNodeType.ROOT_FOLDER);
	}

	protected boolean isRevison(WorkGroupNode node) {
		return node.getNodeType().equals(WorkGroupNodeType.DOCUMENT_REVISION);
	}
}
