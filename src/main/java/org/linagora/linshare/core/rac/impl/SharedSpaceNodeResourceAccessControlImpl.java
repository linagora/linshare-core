/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.rac.impl;

import java.util.Objects;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.constants.SharedSpaceActionType;
import org.linagora.linshare.core.domain.constants.SharedSpaceResourceType;
import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
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
			SharedSpaceNodeMongoRepository sharedSpaceNodeMongoRepository) {
		super(functionalityService, sharedSpaceMemberMongoRepository, sharedSpacePermissionMongoRepository, sharedSpaceNodeMongoRepository);
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
			creation = functionalityService.getDriveCreationRight(actor.getDomain());
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
		// Check the user can create workgroups inside this drive
		if (parent != null && NodeType.WORK_SPACE.equals(parent.getNodeType())) {
			boolean canCreateWorkGroupsInside = defaultSharedSpacePermissionCheck(authUser, actor, parent,
					TechnicalAccountPermissionType.SHARED_SPACE_NODE_CREATE, SharedSpaceActionType.CREATE);
			if (!canCreateWorkGroupsInside) {
				logger.error("You cannot create workgroups inside this node");
				return false;
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
