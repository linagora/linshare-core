/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2018. Contribute to
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
				if (((User) actor).getCanUpload()) {
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
		if (isEnabled(authUser)) {
			if (authUser.hasUploadRequestRole()) {
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
