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
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.rac.UploadRequestResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;

public class UploadRequestResourceAccessControlImpl
		extends AbstractUploadRequestResourceAccessControlImpl<Account, UploadRequest>
		implements UploadRequestResourceAccessControl {

	public UploadRequestResourceAccessControlImpl(FunctionalityReadOnlyService functionalityService) {
		super(functionalityService);
	}

	@Override
	protected boolean hasReadPermission(Account authUser, Account actor, UploadRequest entry, Object... opt) {
		return defaultUploadRequestPermissionCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.UPLOAD_REQUEST_GET);
	}

	@Override
	protected boolean hasListPermission(Account authUser, Account actor, UploadRequest entry, Object... opt) {
		if (authUser.hasUploadRequestRole()) {
			return true;
		}
		return defaultPermissionCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.UPLOAD_REQUEST_LIST, false);
	}

	@Override
	protected boolean hasDeletePermission(Account authUser, Account actor, UploadRequest entry, Object... opt) {
		return defaultPermissionCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.UPLOAD_REQUEST_DELETE);
	}

	@Override
	protected boolean hasCreatePermission(Account authUser, Account actor, UploadRequest entry, Object... opt) {
		return defaultPermissionCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.UPLOAD_REQUEST_CREATE, false);
	}

	@Override
	protected boolean hasUpdatePermission(Account authUser, Account actor, UploadRequest entry, Object... opt) {
		return defaultUploadRequestPermissionCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.UPLOAD_REQUEST_UPDATE);
	}

	@Override
	protected String getTargetedAccountRepresentation(Account targetedAccount) {
		return targetedAccount.getAccountRepresentation();
	}

	@Override
	protected String getEntryRepresentation(UploadRequest entry) {
		return entry.getUuid();
	}

	@Override
	protected Account getOwner(UploadRequest entry, Object... opt) {
		return entry.getUploadRequestGroup().getOwner();
	}
}