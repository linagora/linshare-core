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
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.rac.UploadRequestEntryRessourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;

public class UploadRequestEntryRessourceAccessControlImpl extends EntryResourceAccessControlImpl<Account, UploadRequestEntry>
		implements UploadRequestEntryRessourceAccessControl {

	public UploadRequestEntryRessourceAccessControlImpl(
			FunctionalityReadOnlyService functionalityService) {
		super(functionalityService);
	}

	@Override
	protected String getEntryRepresentation(UploadRequestEntry entry) {
		StringBuilder sb = new StringBuilder(entry.getEntryType().toString());
		sb.append(" (");
		sb.append(entry.getUuid());
		sb.append(") ");
		return sb.toString();
	}

	@Override
	protected Account getOwner(UploadRequestEntry entry, Object... opt) {
		Account owner = entry.getEntryOwner();
		return owner;
	}

	@Override
	protected String getOwnerRepresentation(Account owner) {
		return owner.getAccountRepresentation();
	}

	@Override
	protected boolean hasReadPermission(Account authUser, Account actor, UploadRequestEntry entry, Object... opt) {
		return defaultPermissionCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.UPLOAD_REQUEST_ENTRIES_GET);
	}

	@Override
	protected boolean hasListPermission(Account authUser, Account actor, UploadRequestEntry entry, Object... opt) {
		if (authUser.hasUploadRequestRole()) {
			return true;
		}
		return defaultPermissionCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.UPLOAD_REQUEST_ENTRIES_LIST, false);
	}

	@Override
	protected boolean hasDeletePermission(Account authUser, Account actor, UploadRequestEntry entry, Object... opt) {
		return defaultPermissionCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.UPLOAD_REQUEST_ENTRIES_DELETE);
	}

	@Override
	protected boolean hasCreatePermission(Account authUser, Account actor, UploadRequestEntry entry, Object... opt) {
		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser, TechnicalAccountPermissionType.UPLOAD_REQUEST_ENTRIES_CREATE);
		} else if (authUser.isInternal() || authUser.isGuest()) {
			if (authUser.equals(actor)) {
				if (((User) actor).isCanUpload()) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected boolean hasUpdatePermission(Account authUser, Account actor,
			UploadRequestEntry entry, Object... opt) {
		return defaultPermissionCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.UPLOAD_REQUEST_ENTRIES_UPDATE);
	}

	@Override
	protected boolean hasDownloadPermission(Account authUser, Account actor,
			UploadRequestEntry entry, Object... opt) {
		return defaultPermissionCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.UPLOAD_REQUEST_ENTRIES_DOWNLOAD);
	}

	@Override
	protected boolean hasDownloadTumbnailPermission(Account authUser,
			Account actor, UploadRequestEntry entry, Object... opt) {
		return defaultPermissionCheck(
				authUser,
				actor,
				entry,
				TechnicalAccountPermissionType.UPLOAD_REQUEST_ENTRIES_DOWNLOAD_THUMBNAIL);
	}

	/**
	 * Checking if the functionality Upload Request is enabled before checking the
	 * default permissions
	 * 
	 * @param authUser
	 * @param actor
	 * @param entry
	 * @param permission
	 * @return
	 */
	protected boolean defaultPermissionCheck(Account authUser, Account actor, UploadRequestEntry entry,
			TechnicalAccountPermissionType permission) {
		logger.trace("Current authUser " + authUser.getAccountRepresentation());
		logger.trace("Current actor " + actor.getAccountRepresentation());
		if (isEnabled(actor)) {
			if (authUser.hasUploadRequestRole()) {
				logger.trace("authUser has UploadRequest Role");
				return true;
			}
			return defaultPermissionCheck(authUser, actor, entry, permission, true);
		}
		return false;
	}

	private boolean isEnabled(Account authUser) {
		Functionality func = functionalityService.getUploadRequestFunctionality(authUser.getDomain());
		return func.getActivationPolicy().getStatus();
	}

}
