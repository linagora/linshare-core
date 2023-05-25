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
package org.linagora.linshare.core.facade.webservice.delegation.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.PatchDto;
import org.linagora.linshare.core.facade.webservice.user.SharedSpaceNodeFacade;
import org.linagora.linshare.core.facade.webservice.user.impl.GenericFacadeImpl;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;

import com.google.common.base.Strings;

public class SharedSpaceNodeFacadeImpl extends DelegationGenericFacadeImpl implements SharedSpaceNodeFacade {

	private final SharedSpaceNodeService nodeService;
	
	private final AuditLogEntryService auditLogEntryService;

	public SharedSpaceNodeFacadeImpl(AccountService accountService,
                                     SharedSpaceNodeService nodeService,
                                     AuditLogEntryService auditLogEntryService,
									 UserService userService) {
		super(accountService, userService);
		this.nodeService = nodeService;
		this.auditLogEntryService = auditLogEntryService;
	}

	@Override
	public SharedSpaceNode find(String actorUuid, String uuid, boolean withRole, boolean lastUpdater) throws BusinessException {
		Validate.notEmpty(uuid, "Missing required shared space node uuid.");
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		return nodeService.find(authUser, actor, uuid, withRole, lastUpdater);
	}

	@Override
	public SharedSpaceNode create(String actorUuid, SharedSpaceNode node) throws BusinessException {
		Validate.notNull(node, "Missing required input shared space node.");
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		SharedSpaceNode toCreate = new SharedSpaceNode(actor.getDomainId(), node.getName(), node.getParentUuid(), node.getNodeType(),
				node.getVersioningParameters(), node.getDescription(), new SharedSpaceAccount(actor));
		return nodeService.create(authUser, actor, toCreate);
	}

	@Override
	public SharedSpaceNode delete(String actorUuid, SharedSpaceNode node, String uuid) throws BusinessException {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		if (!Strings.isNullOrEmpty(uuid)) {
			node = nodeService.find(authUser, actor, uuid);
		} else {
			Validate.notNull(node, "node must be set");
			Validate.notEmpty(node.getUuid(), "node uuid must be set.");
		}
		return nodeService.delete(authUser, actor, node);
	}

	@Override
	public SharedSpaceNode updatePartial(String actorUuid, PatchDto patchNode, String uuid) throws BusinessException {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		Validate.notNull(patchNode, "Missind required input patch node.");
		Validate.notNull(patchNode.getName(), "The name of the attribute to update must be set .");
		Validate.notNull(patchNode.getValue(),"The value of the attribute to update must be set.");
		patchNode.setUuid(uuid);
		return nodeService.updatePartial(authUser, actor, patchNode);
	}
	
	@Override
	public SharedSpaceNode update(String actorUuid, SharedSpaceNode node, String uuid) throws BusinessException {
		Validate.notNull(node, "Missind required input shared space node.");
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		if (!Strings.isNullOrEmpty(uuid)) {
			node.setUuid(uuid);
		} else {
			Validate.notEmpty(node.getUuid(), "node uuid must be set.");
		}
		return nodeService.update(authUser, actor, node);
	}

	@Override
	public List<SharedSpaceMember> members(String actorUuid, String uuid, String accountUuid) throws BusinessException {
		Validate.notEmpty(uuid, "Missing required shared space node");
		Account authUser = checkAuthentication();
		return nodeService.findAllMembers(authUser, authUser, uuid, accountUuid);
	}

	@Override
	public List<SharedSpaceNodeNested> findAllMyNodes(String actorUuid, boolean withRole, String parent) {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		if (!Strings.isNullOrEmpty(parent)) {
			SharedSpaceNode node = nodeService.find(authUser, actor, parent);
			if (!NodeType.WORK_SPACE.equals(node.getNodeType())) {
				throw new BusinessException(BusinessErrorCode.SHARED_SPACE_NODE_FORBIDDEN,
						"The requested shared space is not supported");
			}
		} else {
			parent = null;
		}
		return nodeService.findAllByAccount(authUser, actor, withRole, parent);
	}

	@Override
	public List<SharedSpaceNode> findAll() {
		Account authUser = checkAuthentication();
		return nodeService.findAllRootWorkgroups(authUser, authUser);
	}

	@Override
	public Set<AuditLogEntryUser> findAllSharedSpaceAudits(String sharedSpaceUuid, List<LogAction> actions,
			List<AuditLogEntryType> types, String beginDate, String endDate, String resourceUuid) {
		Account authUser = checkAuthentication();
		Validate.notEmpty(sharedSpaceUuid, "shared space uuid required");
		User actor = (User) getActor(authUser, null);
		SharedSpaceNode sharedSpace = nodeService.find(authUser, actor, sharedSpaceUuid);
		return auditLogEntryService.findAllSharedSpaceAudits(authUser, actor, sharedSpace.getUuid(), resourceUuid, actions, types, beginDate, endDate);
	}
}
