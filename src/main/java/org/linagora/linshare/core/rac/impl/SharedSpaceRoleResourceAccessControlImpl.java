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

import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.rac.SharedSpaceRoleResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;

public class SharedSpaceRoleResourceAccessControlImpl 
	extends AbstractResourceAccessControlImpl<Account, Account, SharedSpaceRole> 
	implements SharedSpaceRoleResourceAccessControl{

	public SharedSpaceRoleResourceAccessControlImpl(FunctionalityReadOnlyService functionalityService) {
		super(functionalityService);
	}

	@Override
	protected boolean hasReadPermission(Account authUser, Account account, SharedSpaceRole entry, Object... opt) {
		if (authUser.hasAdminRole() || authUser.hasSuperAdminRole()) {
			return true;
		} else {
			return defaultPermissionCheck(authUser, authUser, entry,
					TechnicalAccountPermissionType.SHARED_SPACE_ROLE_GET, false);
		}
	}

	@Override
	protected boolean hasListPermission(Account authUser, Account account, SharedSpaceRole entry, Object... opt) {
		if (authUser.hasAdminRole() || authUser.hasSuperAdminRole()) {
			return true;
		} else {
			return defaultPermissionCheck(authUser, account, entry,
					TechnicalAccountPermissionType.SHARED_SPACE_ROLE_LIST, false);
		}
	}

	@Override
	protected boolean hasDeletePermission(Account authUser, Account account, SharedSpaceRole entry, Object... opt) {
		return false;
	}

	@Override
	protected boolean hasCreatePermission(Account authUser, Account account, SharedSpaceRole entry, Object... opt) {
		return false;
	}

	@Override
	protected boolean hasUpdatePermission(Account authUser, Account account, SharedSpaceRole entry, Object... opt) {
		return false;
	}

	@Override
	protected String getTargetedAccountRepresentation(Account actor) {
		return null;
	}

	@Override
	protected Account getOwner(SharedSpaceRole entry, Object... opt) {
		return null;
	}

	@Override
	protected String getEntryRepresentation(SharedSpaceRole entry) {
		return null;
	}

}

