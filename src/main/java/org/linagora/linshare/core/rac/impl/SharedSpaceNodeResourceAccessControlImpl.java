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
package org.linagora.linshare.core.rac.impl;

import java.util.Objects;
import java.util.Set;

import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.constants.SharedSpaceActionType;
import org.linagora.linshare.core.domain.constants.SharedSpaceResourceType;
import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.IntegerValueFunctionality;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.SharedSpaceNodeResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.repository.SharedSpaceMemberMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpaceNodeMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpacePermissionMongoRepository;

public class SharedSpaceNodeResourceAccessControlImpl
		extends AbstractSharedSpaceResourceAccessControlImpl<Account, SharedSpaceNode>
		implements SharedSpaceNodeResourceAccessControl {

	public SharedSpaceNodeResourceAccessControlImpl(FunctionalityReadOnlyService functionalityService,
													SharedSpaceMemberMongoRepository sharedSpaceMemberMongoRepository,
													SharedSpacePermissionMongoRepository sharedSpacePermissionMongoRepository,
													SharedSpaceNodeMongoRepository sharedSpaceNodeMongoRepository,
													DomainPermissionBusinessService domainPermissionBusinessService) {
		super(functionalityService,
				sharedSpaceMemberMongoRepository,
				sharedSpacePermissionMongoRepository,
				sharedSpaceNodeMongoRepository,
				domainPermissionBusinessService);
	}

	@Override
	protected SharedSpaceResourceType getSharedSpaceResourceType() {
		return SharedSpaceResourceType.WORK_GROUP;
	}
	
	@Override
	protected boolean hasReadPermission(Account authUser, Account actor, SharedSpaceNode entry, Object... opt) {
		return defaultSharedSpacePermissionAndFunctionalityCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.SHARED_SPACE_NODE_GET, SharedSpaceActionType.READ,
				getSharedSpaceResourceType(entry));
	}

	@Override
	protected boolean hasListPermission(Account authUser, Account actor, SharedSpaceNode entry, Object... opt) {

		boolean isSharedSpaceFuncEnabled = functionalityService.getSharedSpaceFunctionality(actor.getDomain()).getActivationPolicy()
				.getStatus();
		if (!isSharedSpaceFuncEnabled) {
			return false;
		}
		if (opt.length > 0 && opt[0] != null) {
				SharedSpaceNode parent = (SharedSpaceNode) opt[0];
				boolean canListWorkGroupsInside = defaultSharedSpacePermissionAndFunctionalityCheck(authUser, actor, parent,
						TechnicalAccountPermissionType.SHARED_SPACE_NODE_GET, SharedSpaceActionType.READ,
						getSharedSpaceResourceType(parent));
				if (!canListWorkGroupsInside) {
					logger.error(String.format("You cannot list workgroups inside the node {}", parent.getUuid()));
					return false;
				}
		}
		if (opt.length > 1 && opt[1] != null) {
			@SuppressWarnings("unchecked")
			Set<NodeType> types = (Set<NodeType>) opt[1];
			if (!isSharedSpaceFuncEnabled) {
				types.remove(NodeType.WORK_SPACE);
				types.remove(NodeType.WORK_GROUP);
			}
		}
		return defaultPermissionCheck(authUser, actor, entry, TechnicalAccountPermissionType.SHARED_SPACE_NODE_LIST,
				false);
	}

	@Override
	protected boolean hasDeletePermission(Account authUser, Account actor, SharedSpaceNode entry, Object... opt) {
		return defaultSharedSpacePermissionAndFunctionalityCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.SHARED_SPACE_NODE_DELETE, SharedSpaceActionType.DELETE, getSharedSpaceResourceType(entry));
	}

	@Override
	protected boolean hasCreatePermission(Account authUser, Account actor, SharedSpaceNode entry, Object... opt) {
		if (!isFunctionalityEnabled(actor, entry)) {
			return false;
		}
		SharedSpaceNode parent = null;
		if (opt.length > 0 && opt[0] != null) {
			parent = (SharedSpaceNode) opt[0];
		}
		Functionality creation = null;
		if (NodeType.WORK_GROUP.equals(entry.getNodeType()) && (Objects.isNull(entry.getParentUuid()))) {
			creation = functionalityService.getWorkGroupCreationRight(actor.getDomain());
		} else if (NodeType.WORK_SPACE.equals(entry.getNodeType())
				|| (NodeType.WORK_GROUP.equals(entry.getNodeType()))) {
			creation = functionalityService.getWorkSpaceCreationRight(actor.getDomain());
		} else {
			String message = "Unsupported NodeType exception.";
			logger.error(message);
			return false;
		}
		if (!creation.getActivationPolicy().getStatus()) {
			String message = "You can not create shared space, you are not authorized.";
			logger.error(message);
			logger.error("The current domain does not allow you to create a shared space node.");
			return false;
		}
		// Check the user can create workgroups inside this workSpace
		if (parent != null && NodeType.WORK_SPACE.equals(parent.getNodeType())) {
			boolean canCreateWorkGroupsInside = defaultSharedSpacePermissionCheck(authUser, actor, parent,
					TechnicalAccountPermissionType.SHARED_SPACE_NODE_CREATE, SharedSpaceActionType.CREATE);
			if (!canCreateWorkGroupsInside) {
				logger.error("You cannot create workgroups inside this node");
				return false;
			}
		}
		if (NodeType.WORK_GROUP.equals(entry.getNodeType()) && Objects.nonNull(entry.getParentUuid()) && parent == null) {
			IntegerValueFunctionality functionality = functionalityService.getWorkGroupLimitFunctionality(actor.getDomain());
			if (functionality.getActivationPolicy().getStatus()) {
				Integer limit = functionality.getMaxValue();
				Long count = sharedSpaceNodeMongoRepository.countNestedWorkgroups(actor.getDomainId(), entry.getParentUuid());
				logger.debug("Nested WORK_GROUP count: " + count);
				logger.debug("Nested WORK_GROUP limit: " + limit);
				if (count >= limit) {
					throw new BusinessException(
							BusinessErrorCode.WORK_GROUP_LIMIT_REACHED,
							"You have reached the limit of allowed nested workgroups in your domain: " + count + "/" + limit);
				}
			}
		} else if (NodeType.WORK_SPACE.equals(entry.getNodeType())) {
			IntegerValueFunctionality functionality = functionalityService.getWorkSpaceLimitFunctionality(actor.getDomain());
			if (functionality.getActivationPolicy().getStatus()) {
				Integer limit = functionality.getMaxValue();
				Long count = sharedSpaceNodeMongoRepository.countWorkspaces(actor.getDomainId());
				logger.debug("WORK_SPACE count: " + count);
				logger.debug("WORK_SPACE limit: " + limit);
				if (count >= limit) {
					throw new BusinessException(
							BusinessErrorCode.WORKSPACE_LIMIT_REACHED,
							"You have reached the limit of allowed workspaces in your domain: " + count + "/" + limit);
				}
			}
		}
		return defaultPermissionCheck(authUser, actor, entry, TechnicalAccountPermissionType.SHARED_SPACE_NODE_CREATE,
				false);
	}

	@Override
	protected boolean hasUpdatePermission(Account authUser, Account actor, SharedSpaceNode entry, Object... opt) {
		return defaultSharedSpacePermissionAndFunctionalityCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.SHARED_SPACE_NODE_UPDATE, SharedSpaceActionType.UPDATE, getSharedSpaceResourceType(entry));
	}

	@Override
	protected String getSharedSpaceNodeUuid(SharedSpaceNode entry) {
		return entry.getUuid();
	}

	@Override
	protected Account getOwner(SharedSpaceNode entry, Object... opt) {
		return null;
	}

	protected SharedSpaceResourceType getSharedSpaceResourceType(SharedSpaceNode entry) {
		return SharedSpaceResourceType.fromNodeType(entry.getNodeType().toString());
	}

}
