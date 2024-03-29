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
package org.linagora.linshare.core.service.fragment.impl;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceNodeBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.AbstractResourceAccessControl;
import org.linagora.linshare.core.repository.ThreadRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.SharedSpaceRoleService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.core.service.fragment.SharedSpaceFragmentService;
import org.linagora.linshare.core.service.impl.GenericServiceImpl;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.logs.SharedSpaceNodeAuditLogEntry;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;

public abstract class AbstractSharedSpaceFragmentServiceImpl extends GenericServiceImpl<Account, SharedSpaceNode>
		implements SharedSpaceFragmentService {

	protected final SharedSpaceNodeBusinessService businessService;

	protected final SharedSpaceMemberBusinessService memberBusinessService;

	protected final SharedSpaceMemberService memberService;

	protected final SharedSpaceRoleService ssRoleService;

	protected final LogEntryService logEntryService;

	protected final ThreadService threadService;

	protected final ThreadRepository threadRepository;
	
	protected final FunctionalityReadOnlyService functionalityService;

	protected final AccountQuotaBusinessService accountQuotaBusinessService;
	
	protected final WorkGroupNodeService workGroupNodeService;

	protected final SharedSpaceMemberBusinessService memberWorkSpaceService;

	public AbstractSharedSpaceFragmentServiceImpl(
			AbstractResourceAccessControl<Account, Account, SharedSpaceNode> rac,
			SharedSpaceNodeBusinessService businessService,
			SharedSpaceMemberBusinessService memberBusinessService,
			SharedSpaceMemberService memberService,
			SharedSpaceRoleService ssRoleService,
			LogEntryService logEntryService,
			ThreadService threadService,
			ThreadRepository threadRepository,
			FunctionalityReadOnlyService functionalityService,
			AccountQuotaBusinessService accountQuotaBusinessService,
			WorkGroupNodeService workGroupNodeService,
			SharedSpaceMemberBusinessService memberWorkSpaceService,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super(rac, sanitizerInputHtmlBusinessService);
		this.businessService = businessService;
		this.memberService = memberService;
		this.ssRoleService = ssRoleService;
		this.memberBusinessService = memberBusinessService;
		this.logEntryService = logEntryService;
		this.threadService = threadService;
		this.functionalityService = functionalityService;
		this.accountQuotaBusinessService = accountQuotaBusinessService;
		this.threadRepository = threadRepository;
		this.workGroupNodeService = workGroupNodeService;
		this.memberWorkSpaceService = memberWorkSpaceService;
	}

	@Override
	public SharedSpaceNode create(Account authUser, Account actor, SharedSpaceNode node) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notNull(node, "Missing required input shared space node.");
		Validate.notNull(node.getNodeType(), "you must set the node type");
		SharedSpaceNode created = businessService.create(node);
		saveLog(authUser, actor, LogAction.CREATE, created);
		return created;
	}

	protected SharedSpaceNodeAuditLogEntry saveLog(Account authUser, Account actor, LogAction action,
			SharedSpaceNode resource) {
		SharedSpaceNodeAuditLogEntry log = new SharedSpaceNodeAuditLogEntry(authUser, actor, action,
				fromNodeType(resource.getNodeType().toString()), resource);
		logEntryService.insert(log);
		return log;
	}

	@Override
	public SharedSpaceNode update(Account authUser, Account actor, SharedSpaceNode nodeToUpdate)
			throws BusinessException {
		Validate.notNull(nodeToUpdate, "nodeToUpdate must be set.");
		Validate.notEmpty(nodeToUpdate.getUuid(), "shared space node uuid to update must be set.");
		SharedSpaceNode node = find(authUser, actor, nodeToUpdate.getUuid());
		SharedSpaceNode nodeLog = new SharedSpaceNode(node);
		SharedSpaceNodeAuditLogEntry log = new SharedSpaceNodeAuditLogEntry(authUser, actor, LogAction.UPDATE,
				fromNodeType(nodeToUpdate.getNodeType().toString()), nodeLog);
		checkUpdatePermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.SHARED_SPACE_NODE_FORBIDDEN,
				nodeToUpdate);
		nodeToUpdate.setName(sanitize(nodeToUpdate.getName()));
		nodeToUpdate.setDescription(sanitize(nodeToUpdate.getDescription()));
		SharedSpaceNode updated = businessService.update(node, nodeToUpdate);
		memberBusinessService.updateNestedNode(updated);
		memberBusinessService.addMembersToRelatedAccountsAndRelatedDomains(updated.getUuid(), log);
		log.setResourceUpdated(updated);
		logEntryService.insert(log);
		return updated;
	}

	@Override
	public List<SharedSpaceNodeNested> findAllWorkgroupsInNode(Account authUser, Account actor, SharedSpaceNode parent) {
		preChecks(authUser, actor);
		Validate.notNull(parent, "The parent must be set.");
		if (!NodeType.WORK_SPACE.equals(parent.getNodeType())) {
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_NODE_FORBIDDEN,
					String.format("You can not list workgroups in this node with uuid {}", parent.getUuid()));
		}
		checkListPermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.SHARED_SPACE_NODE_FORBIDDEN, null,
				parent);
		return memberService.findAllWorkGroupsInNode(authUser, actor, parent.getUuid(), actor.getLsUuid());
	}

	protected SharedSpaceNode find(Account authUser, Account actor, String uuid) {
		preChecks(authUser, actor);
		Validate.notEmpty(uuid, "Missing required shared space node uuid.");
		SharedSpaceNode found = businessService.find(uuid);
		if (found == null) {
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_NODE_NOT_FOUND,
					"The shared space node with uuid: " + uuid + " is not found");
		}
		checkReadPermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.SHARED_SPACE_NODE_FORBIDDEN, found);
		return found;
	}

	private static AuditLogEntryType fromNodeType(String nodeType) {
		return AuditLogEntryType.fromString(nodeType);
	}
}
