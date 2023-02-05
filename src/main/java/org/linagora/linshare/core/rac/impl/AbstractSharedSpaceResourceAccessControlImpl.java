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
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.rac.AbstractSharedSpaceResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.repository.SharedSpaceMemberMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpaceNodeMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpacePermissionMongoRepository;

public abstract class AbstractSharedSpaceResourceAccessControlImpl<R, E> extends AbstractResourceAccessControlImpl<Account, R, E> implements AbstractSharedSpaceResourceAccessControl<Account, R, E>{

	protected SharedSpaceMemberMongoRepository sharedSpaceMemberMongoRepository;

	protected SharedSpacePermissionMongoRepository sharedSpacePermissionMongoRepository;
	
	protected SharedSpaceNodeMongoRepository sharedSpaceNodeMongoRepository;

	public AbstractSharedSpaceResourceAccessControlImpl(FunctionalityReadOnlyService functionalityService,
			SharedSpaceMemberMongoRepository sharedSpaceMemberMongoRepository,
			SharedSpacePermissionMongoRepository sharedSpacePermissionMongoRepository,
			SharedSpaceNodeMongoRepository sharedSpaceNodeMongoRepository) {
		super(functionalityService);
		this.sharedSpaceMemberMongoRepository = sharedSpaceMemberMongoRepository;
		this.sharedSpacePermissionMongoRepository = sharedSpacePermissionMongoRepository;
		this.sharedSpaceNodeMongoRepository = sharedSpaceNodeMongoRepository;
	}

	protected abstract SharedSpaceResourceType getSharedSpaceResourceType();

	protected Account getOwner(SharedSpaceNode entry, Object... opt) {
		return null;
	}

	protected abstract String getSharedSpaceNodeUuid(E entry);

	@Override
	protected String getTargetedAccountRepresentation(Account actor) {
		return actor.getAccountRepresentation();
	}

	@Override
	protected String getEntryRepresentation(E entry) {
		return entry.toString();
	}

	protected boolean defaultSharedSpacePermissionCheck(Account authUser, Account actor, E entry,
			TechnicalAccountPermissionType permission, SharedSpaceActionType action) {
		String sharedSpaceNodeUuid = getSharedSpaceNodeUuid(entry);
		return defaultSharedSpacePermissionCheck(authUser, actor, sharedSpaceNodeUuid, permission, action);
	}

	protected boolean defaultSharedSpacePermissionCheck(Account authUser, Account actor, String sharedSpaceNodeUuid,
			TechnicalAccountPermissionType permission, SharedSpaceActionType action) {
		return defaultSharedSpacePermissionCheck(authUser, actor, sharedSpaceNodeUuid, permission, action, getSharedSpaceResourceType());
	}
	
	protected boolean defaultSharedSpacePermissionAndFunctionalityCheck(Account authUser, Account actor, E entry,
			TechnicalAccountPermissionType permission, SharedSpaceActionType action, SharedSpaceResourceType resourceType) {
		String nodeUuid = getSharedSpaceNodeUuid(entry);
		SharedSpaceNode node = sharedSpaceNodeMongoRepository.findByUuid(nodeUuid);
		if (!isFunctionalityEnabled(actor, node)) {
			return false;
		}
		return defaultSharedSpacePermissionCheck(authUser, actor, nodeUuid, permission, action, resourceType);
	}
	
	protected boolean isFunctionalityEnabled(Account actor, SharedSpaceNode node) {
		Functionality sharedSpaceFunctionality = functionalityService.getSharedSpaceFunctionality(actor.getDomain());
		if (!sharedSpaceFunctionality.getActivationPolicy().getStatus()) {
			logger.error("{} is disabled on your domain, you are not authorized to do any operation.",
					sharedSpaceFunctionality.getIdentifier());
			return false;
		}
		return true;
	}

	protected boolean defaultSharedSpacePermissionCheck(Account authUser, Account actor, String sharedSpaceNodeUuid,
			TechnicalAccountPermissionType permission, SharedSpaceActionType action, SharedSpaceResourceType resourceType) {
		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser, permission);
		}
		if (authUser.isInternal() || authUser.isGuest()) {
			if (actor != null && authUser.equals(actor)) {
				SharedSpaceMember foundMember = sharedSpaceMemberMongoRepository.findByAccountAndNode(actor.getLsUuid(),
						sharedSpaceNodeUuid);
				if (foundMember == null) {
					return false;
				}
				return hasPermission(foundMember.getRole().getUuid(), action, resourceType);
			}
		}
		return false;
	}

	protected Boolean hasPermission(String roleUuid, SharedSpaceActionType action, SharedSpaceResourceType resourceType) {
		return !sharedSpacePermissionMongoRepository.findByRoleAndActionAndResource(roleUuid, action, resourceType)
				.isEmpty();
	}

}
