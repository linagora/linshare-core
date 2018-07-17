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
package org.linagora.linshare.core.rac.impl;

import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.rac.SharedSpaceMemberResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;

public class SharedSpaceMemberResourceAccessControlImpl
		extends AbstractResourceAccessControlImpl<Account, Account, SharedSpaceMember>
		implements SharedSpaceMemberResourceAccessControl {

	private final SharedSpaceMemberBusinessService sharedSpaceMemberBusinessService;

	public SharedSpaceMemberResourceAccessControlImpl(FunctionalityReadOnlyService functionalityService,
			SharedSpaceMemberBusinessService sharedSpaceMemberBusinessService) {
		super(functionalityService);
		this.sharedSpaceMemberBusinessService = sharedSpaceMemberBusinessService;
	}

	@Override
	protected boolean hasReadPermission(Account authUser, Account actor, SharedSpaceMember entry, Object... opt) {
		return defaultPermissionCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.SHARED_SPACE_PERMISSION_READ, false);
	}

	@Override
	protected boolean hasListPermission(Account authUser, Account actor, SharedSpaceMember entry, Object... opt) {
		return defaultPermissionCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.SHARED_SPACE_PERMISSION_LIST, false);
	}

	@Override
	protected boolean hasDeletePermission(Account authUser, Account actor, SharedSpaceMember entry, Object... opt) {
		// TODO Auto-generated method stub
		return defaultPermissionCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.SHARED_SPACE_PERMISSION_DELETE, false);
	}

	@Override
	protected boolean hasCreatePermission(Account authUser, Account actor, SharedSpaceMember entry, Object... opt) {
		if (!checkAccountNotInNode(entry.getAccount().getUuid(), entry.getNode().getUuid())) {
			logger.error(
					String.format("The account with the UUID : %s is already a member of the node with the uuid : %s",
							entry.getAccount().getUuid(), entry.getNode().getUuid()));
			return false;
		}
		return defaultPermissionCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.SHARED_SPACE_PERMISSION_CREATE, false);
	}

	@Override
	protected boolean hasUpdatePermission(Account authUser, Account actor, SharedSpaceMember entry, Object... opt) {
		// TODO Auto-generated method stub
		return defaultPermissionCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.SHARED_SPACE_PERMISSION_UPDATE, false);
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

	private boolean checkAccountNotInNode(String possibleMemberUuid, String nodeUuid) {
		return sharedSpaceMemberBusinessService.findByMemberAndSharedSpaceNode(possibleMemberUuid, nodeUuid) == null;
	}
}
