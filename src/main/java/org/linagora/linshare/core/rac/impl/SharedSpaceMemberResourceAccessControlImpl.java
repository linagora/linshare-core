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

import org.linagora.linshare.core.domain.constants.SharedSpaceActionType;
import org.linagora.linshare.core.domain.constants.SharedSpaceResourceType;
import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.rac.SharedSpaceMemberResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberDrive;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.repository.SharedSpaceMemberMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpaceNodeMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpacePermissionMongoRepository;

public class SharedSpaceMemberResourceAccessControlImpl
		extends AbstractSharedSpaceResourceAccessControlImpl<Account, SharedSpaceMember>
		implements SharedSpaceMemberResourceAccessControl {

	public SharedSpaceMemberResourceAccessControlImpl(FunctionalityReadOnlyService functionalityService,
			SharedSpaceMemberMongoRepository sharedSpaceMemberMongoRepository,
			SharedSpacePermissionMongoRepository sharedSpacePermissionMongoRepository,
			SharedSpaceNodeMongoRepository sharedSpaceNodeMongoRepository) {
		super(functionalityService, sharedSpaceMemberMongoRepository, sharedSpacePermissionMongoRepository, sharedSpaceNodeMongoRepository);
	}
	
	@Override
	protected boolean hasReadPermission(Account authUser, Account actor, SharedSpaceMember entry, Object... opt) {
		return defaultSharedSpacePermissionAndFunctionalityCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.SHARED_SPACE_PERMISSION_GET, SharedSpaceActionType.READ, getSharedSpaceResourceType());
	}

	@Override
	protected boolean hasListPermission(Account authUser, Account actor, SharedSpaceMember entry, Object... opt) {
		String nodeUuid = (String) opt[0];
		SharedSpaceNode node = sharedSpaceNodeMongoRepository.findByUuid(nodeUuid);
		if (!isFunctionalityEnabled(actor, node)) {
			return false;
		}
		return defaultSharedSpacePermissionCheck(authUser, actor, nodeUuid,
				TechnicalAccountPermissionType.SHARED_SPACE_PERMISSION_LIST, SharedSpaceActionType.READ);
	}

	@Override
	protected boolean hasDeletePermission(Account authUser, Account actor, SharedSpaceMember entry, Object... opt) {
		String nodeUuid = entry.getNode().getUuid();
		String parentUuid = null;
		SharedSpaceMemberDrive workSpaceMember = null;
		if (opt != null && opt.length > 0 && opt[0] != null) {
			parentUuid = (String) opt[0];
		}
		SharedSpaceNode node = sharedSpaceNodeMongoRepository.findByUuid(nodeUuid);
		if (!isFunctionalityEnabled(actor, node)) {
			return false;
		}
		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser, TechnicalAccountPermissionType.SHARED_SPACE_PERMISSION_DELETE);
		}
		if (authUser.isInternal() || authUser.isGuest()) {
			// could be either workSpace or Workgroup member
			SharedSpaceMember nodeMember = sharedSpaceMemberMongoRepository.findByAccountAndNode(actor.getLsUuid(),
					nodeUuid);
			if (nodeMember == null) {
				return false;
			} else if (parentUuid == null) {
				// root workgroup: ssmember is membership of a stand-alone workgroup
				return hasPermission(nodeMember.getRole().getUuid(), SharedSpaceActionType.DELETE,
						SharedSpaceResourceType.MEMBER);
			} else {
				workSpaceMember = (SharedSpaceMemberDrive) sharedSpaceMemberMongoRepository
						.findByAccountAndNode(actor.getLsUuid(), parentUuid);
				if (workSpaceMember == null) {
					// ssmember is membership of a nested workgroup only and not member of the workSpace
					return hasPermission(nodeMember.getRole().getUuid(), SharedSpaceActionType.DELETE,
							SharedSpaceResourceType.MEMBER);
				} else {
					// ssmember is membership of a workSpace and its nested workgroup
					return hasPermission(nodeMember.getRole().getUuid(), SharedSpaceActionType.DELETE,
							SharedSpaceResourceType.MEMBER)
							|| hasPermission(workSpaceMember.getRole().getUuid(), SharedSpaceActionType.DELETE,
									SharedSpaceResourceType.MEMBER);
				}
			}
		} else {
			// TODO: Check administrator permissions (User with role Role.ADMIN)
		}
		return false;
	}

	@Override
	protected boolean hasCreatePermission(Account authUser, Account actor, SharedSpaceMember entry, Object... opt) {
		SharedSpaceNode node = (SharedSpaceNode) opt[0];
		if (!isFunctionalityEnabled(actor, node)) {
			return false;
		}
		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser, TechnicalAccountPermissionType.SHARED_SPACE_PERMISSION_CREATE);
		}
		if (authUser.isInternal() || authUser.isGuest()) {
			if (actor != null && authUser.equals(actor)) {
				SharedSpaceMember foundMember = sharedSpaceMemberMongoRepository.findByAccountAndNode(actor.getLsUuid(),
						node.getUuid());
				if (foundMember == null) {
					if (sharedSpaceMemberMongoRepository.findByNodeUuid(node.getUuid()).isEmpty()) {
						return true;
					}
					return false;
				}
				return hasPermission(foundMember.getRole().getUuid(), SharedSpaceActionType.CREATE, getSharedSpaceResourceType());
			}
		}
		return false;

	}

	@Override
	protected boolean hasUpdatePermission(Account authUser, Account actor, SharedSpaceMember entry, Object... opt) {
		return defaultSharedSpacePermissionAndFunctionalityCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.SHARED_SPACE_PERMISSION_UPDATE, SharedSpaceActionType.UPDATE, getSharedSpaceResourceType());
	}

	@Override
	protected String getTargetedAccountRepresentation(Account targatedAccount) {
		return targatedAccount.getAccountRepresentation();
	}

	@Override
	protected Account getOwner(SharedSpaceMember entry, Object... opt) {
		return null;
	}

	@Override
	protected String getEntryRepresentation(SharedSpaceMember entry) {
		return entry.toString();
	}

	@Override
	protected SharedSpaceResourceType getSharedSpaceResourceType() {
		return SharedSpaceResourceType.MEMBER;
	}

	@Override
	protected String getSharedSpaceNodeUuid(SharedSpaceMember entry) {
		return entry.getNode().getUuid();
	}
}
