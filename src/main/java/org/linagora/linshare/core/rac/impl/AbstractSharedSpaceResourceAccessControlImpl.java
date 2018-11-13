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
 * and free version of LinShare™, powered by Linagora © 2009-2018. Contribute to
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
package org.linagora.linshare.core.rac.impl;

import org.linagora.linshare.core.domain.constants.SharedSpaceActionType;
import org.linagora.linshare.core.domain.constants.SharedSpaceResourceType;
import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.rac.AbstractSharedSpaceResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.repository.SharedSpaceMemberMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpacePermissionMongoRepository;

public abstract class AbstractSharedSpaceResourceAccessControlImpl<R, E> extends AbstractResourceAccessControlImpl<Account, R, E> implements AbstractSharedSpaceResourceAccessControl<Account, R, E>{

	protected SharedSpaceMemberMongoRepository sharedSpaceMemberMongoRepository;

	protected SharedSpacePermissionMongoRepository sharedSpacePermissionMongoRepository;

	public AbstractSharedSpaceResourceAccessControlImpl(FunctionalityReadOnlyService functionalityService,
			SharedSpaceMemberMongoRepository sharedSpaceMemberMongoRepository,
			SharedSpacePermissionMongoRepository sharedSpacePermissionMongoRepository) {
		super(functionalityService);
		this.sharedSpaceMemberMongoRepository = sharedSpaceMemberMongoRepository;
		this.sharedSpacePermissionMongoRepository = sharedSpacePermissionMongoRepository;
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

	protected boolean defaultSharedSpacePermissionCheck(Account authUser, Account actor, String sharedSpaceNodeUuid,
			TechnicalAccountPermissionType permission, SharedSpaceActionType action, SharedSpaceResourceType resourceType) {
		if (authUser.hasSuperAdminRole()) {
			return true;
		}
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
