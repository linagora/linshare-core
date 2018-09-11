/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
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

import java.util.List;

import org.jsoup.helper.Validate;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceNodeBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupDto;
import org.linagora.linshare.core.rac.SharedSpaceNodeResourceAccessControl;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.core.service.SharedSpaceRoleService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.logs.SharedSpaceNodeAuditLogEntry;

public class SharedSpaceNodeServiceImpl extends GenericServiceImpl<Account, SharedSpaceNode>
		implements SharedSpaceNodeService {

	private final SharedSpaceNodeBusinessService businessService;

	private final SharedSpaceMemberBusinessService memberBusinessService;

	private final SharedSpaceMemberService memberService;

	private final SharedSpaceRoleService ssRoleService;

	private final LogEntryService logEntryService;

	private final ThreadService threadService;

	public SharedSpaceNodeServiceImpl(SharedSpaceNodeBusinessService businessService,
			SharedSpaceNodeResourceAccessControl rac,
			SharedSpaceMemberBusinessService memberBusinessService,
			SharedSpaceMemberService memberService,
			SharedSpaceRoleService ssRoleService,
			LogEntryService logEntryService,
			ThreadService threadService) {
		super(rac);
		this.businessService = businessService;
		this.memberService = memberService;
		this.ssRoleService = ssRoleService;
		this.memberBusinessService = memberBusinessService;
		this.logEntryService = logEntryService;
		this.threadService = threadService;
	}

	@Override
	public SharedSpaceNode find(Account authUser, Account actor, String uuid) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notEmpty(uuid, "Missing required shared space node uuid.");
		SharedSpaceNode found = businessService.find(uuid);
		if (found == null) {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_NOT_FOUND,
					"The shared space node with uuid: " + uuid + " is not found");
		}
		checkReadPermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.WORK_GROUP_FORBIDDEN, found);
		return found;
	}

	@Override
	public SharedSpaceNode create(Account authUser, Account actor, SharedSpaceNode node) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notNull(node, "Missing required input shared space node.");
		Validate.notNull(node.getNodeType(), "you must set the node type");
		checkCreatePermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.WORK_GROUP_FORBIDDEN, null);
		//Hack to create thread into shared space node
		SharedSpaceNode created = simpleCreate(authUser, actor, node);
		SharedSpaceRole role = ssRoleService.getAdmin(authUser, actor);
		memberService.createWithoutCheckPermission(authUser, actor, created, role,
				new SharedSpaceAccount((User) actor));
		return created;
	}

	protected SharedSpaceNode simpleCreate(Account authUser, Account actor, SharedSpaceNode node) throws BusinessException {
		//Hack to create thread into shared space node
		WorkGroup workGroup = threadService.create(authUser, actor, node.getName());
		node.setUuid(workGroup.getLsUuid());
		SharedSpaceNode created = businessService.create(node);
		createLog(authUser, actor, LogAction.CREATE, created);
		return created;
	}

	/**
	 * Only use to compability with threadFacade
	 *
	 */
	@Deprecated
	public WorkGroupDto createWorkGroupDto(Account authUser, Account actor, SharedSpaceNode node) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notNull(node, "Missing required input shared space node.");
		Validate.notNull(node.getNodeType(), "you must set the node type");
		checkCreatePermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.WORK_GROUP_FORBIDDEN, null);
		//Hack to create thread into shared space node
		SharedSpaceNode created = simpleCreate(authUser, actor, node);
		SharedSpaceRole role = ssRoleService.getAdmin(authUser, actor);
		memberService.createWithoutCheckPermission(authUser, actor, created, role, new SharedSpaceAccount((User) actor));
		WorkGroup workGroup = threadService.find(authUser, actor, created.getUuid());
		return new WorkGroupDto(workGroup, created);
	}

	protected SharedSpaceNodeAuditLogEntry createLog(Account authUser, Account actor, LogAction action,
			SharedSpaceNode resource) {
		SharedSpaceNodeAuditLogEntry log = new SharedSpaceNodeAuditLogEntry(authUser, actor, action,
				AuditLogEntryType.WORKGROUP, resource);
		logEntryService.insert(log);
		return log;
	}

	protected SharedSpaceNodeAuditLogEntry updateLog(Account authUser, Account actor, SharedSpaceNode resource,
			SharedSpaceNode resourceUpdated) {
		SharedSpaceNodeAuditLogEntry log = new SharedSpaceNodeAuditLogEntry(authUser, actor, LogAction.UPDATE,
				AuditLogEntryType.WORKGROUP, resource);
		log.setResourceUpdated(resourceUpdated);
		logEntryService.insert(log);
		return log;
	}

	@Override
	public SharedSpaceNode delete(Account authUser, Account actor, SharedSpaceNode node) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notNull(node, "missing required node to delete.");
		Validate.notEmpty(node.getUuid(), "missing required node uuid to delete");
		SharedSpaceNode foundedNodeTodel = find(authUser, actor, node.getUuid());
		checkDeletePermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.WORK_GROUP_FORBIDDEN,
				foundedNodeTodel);
		simpleDelete(authUser, actor, foundedNodeTodel);
		return foundedNodeTodel;
	}

	/**
	 * Only use to compability with threadFacade
	 *
	 */
	@Override
	public WorkGroupDto deleteWorkgroupDto(Account authUser, Account actor, SharedSpaceNode node) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notNull(node, "missing required node to delete.");
		Validate.notEmpty(node.getUuid(), "missing required node uuid to delete");
		SharedSpaceNode foundedNodeTodel = find(authUser, actor, node.getUuid());
		checkDeletePermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.WORK_GROUP_FORBIDDEN,
				foundedNodeTodel);
		WorkGroup workGroup = simpleDelete(authUser, actor, foundedNodeTodel);
		return new WorkGroupDto(workGroup, foundedNodeTodel);
	}

	private WorkGroup simpleDelete(Account authUser, Account actor, SharedSpaceNode foundedNodeTodel) throws BusinessException {
		businessService.delete(foundedNodeTodel);
		WorkGroup workGroup = threadService.find(authUser, authUser, foundedNodeTodel.getUuid());
		threadService.deleteThread(authUser, authUser, workGroup);
		SharedSpaceNodeAuditLogEntry log = new SharedSpaceNodeAuditLogEntry(authUser, actor, LogAction.DELETE,
				AuditLogEntryType.WORKGROUP, foundedNodeTodel);
		logEntryService.insert(log);
		memberService.deleteAllMembers(authUser, actor, foundedNodeTodel.getUuid());
		return workGroup;
	}

	@Override
	public SharedSpaceNode update(Account authUser, Account actor, SharedSpaceNode nodeToUpdate)
			throws BusinessException {
		Validate.notNull(nodeToUpdate, "nodeToUpdate must be set.");
		Validate.notEmpty(nodeToUpdate.getUuid(), "shared space node uuid to update must be set.");
		SharedSpaceNode node = find(authUser, actor, nodeToUpdate.getUuid());
		checkUpdatePermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.WORK_GROUP_FORBIDDEN,
				nodeToUpdate);
		SharedSpaceNode updated = businessService.update(node, nodeToUpdate);
		memberBusinessService.updateNestedNode(updated);
		SharedSpaceNodeAuditLogEntry log = new SharedSpaceNodeAuditLogEntry(authUser, actor, LogAction.UPDATE,
				AuditLogEntryType.WORKGROUP, node);
		log.setResourceUpdated(updated);
		logEntryService.insert(log);
		return updated;
	}

	@Override
	public List<SharedSpaceNode> findAll(Account authUser, Account actor) {
		preChecks(authUser, actor);
		checkListPermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.WORK_GROUP_FORBIDDEN, null);
		return businessService.findAll();
	}

	@Override
	public List<SharedSpaceNodeNested> findAllByAccount(Account authUser, Account actor) {
		preChecks(authUser, actor);
		checkListPermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.WORK_GROUP_FORBIDDEN, null);
		return memberService.findAllByAccount(authUser, actor, actor.getLsUuid());
	}

	@Override
	public List<SharedSpaceMember> findAllMembers(Account authUser, Account actor, String sharedSpaceNodeUuid) {
		return memberService.findAll(authUser, actor, sharedSpaceNodeUuid);
	}

	@Override
	public List<SharedSpaceNode> searchByName(Account authUser, Account actor, String name) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notEmpty(name, "Missing required shared space node name.");
		List<SharedSpaceNode> founds = businessService.searchByName(name);
		checkListPermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.WORK_GROUP_FORBIDDEN, null);
		return founds;
	}
}
