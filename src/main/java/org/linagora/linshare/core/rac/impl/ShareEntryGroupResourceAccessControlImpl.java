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
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.rac.ShareEntryGroupResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;

public class ShareEntryGroupResourceAccessControlImpl extends AbstractResourceAccessControlImpl<Account, Account, ShareEntryGroup> implements ShareEntryGroupResourceAccessControl{

	public ShareEntryGroupResourceAccessControlImpl(FunctionalityReadOnlyService functionalityService) {
		super(functionalityService);
	}

	@Override
	protected boolean hasReadPermission(Account actor, Account account, ShareEntryGroup entry, Object... opt) {
		return defaultPermissionCheck(actor, account, entry, TechnicalAccountPermissionType.SHARE_ENTRY_GROUPS_GET);
	}

	@Override
	protected boolean hasListPermission(Account actor, Account account, ShareEntryGroup entry, Object... opt) {
		return defaultPermissionCheck(actor, account, entry, TechnicalAccountPermissionType.SHARE_ENTRY_GROUPS_LIST, false);
	}

	@Override
	protected boolean hasDeletePermission(Account actor, Account account, ShareEntryGroup entry, Object... opt) {
		return defaultPermissionCheck(actor, account, entry, TechnicalAccountPermissionType.SHARE_ENTRY_GROUPS_DELETE);
	}

	/**
	 * This methods returns true because the creation rights are check when you are trying to crate shares.
	 * So if you can create a share entry, you can also create a share entry group.
	 */
	@Override
	protected boolean hasCreatePermission(Account actor, Account account, ShareEntryGroup entry, Object... opt) {
		return true;
	}

	@Override
	protected boolean hasUpdatePermission(Account actor, Account account, ShareEntryGroup entry, Object... opt) {
		return defaultPermissionCheck(actor, account, entry, TechnicalAccountPermissionType.SHARE_ENTRY_GROUPS_UPDATE);
	}

	@Override
	protected String getTargetedAccountRepresentation(Account targetedAccount) {
		return targetedAccount.getAccountRepresentation();
	}

	@Override
	protected String getEntryRepresentation(ShareEntryGroup entry) {
		return entry.getUuid();
	}

	@Override
	protected Account getOwner(ShareEntryGroup entry, Object... opt) {
		return entry.getOwner();
	}
}
