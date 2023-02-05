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
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.rac.ShareEntryResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;

public class ShareEntryResourceAccessControlImpl extends
		EntryResourceAccessControlImpl<Account, ShareEntry> implements
		ShareEntryResourceAccessControl {

	public ShareEntryResourceAccessControlImpl(
			final FunctionalityReadOnlyService functionalityService) {
		super(functionalityService);
	}

	@Override
	protected String getEntryRepresentation(ShareEntry entry) {
		StringBuilder sb = new StringBuilder(entry.getEntryType().toString());
		sb.append(" (");
		sb.append(entry.getUuid());
		sb.append(") ");
		return sb.toString();
	}

	@Override
	protected Account getOwner(ShareEntry entry, Object... opt) {
		Account owner = entry.getEntryOwner();
		return owner;
	}

	@Override
	protected String getOwnerRepresentation(Account owner) {
		return owner.getAccountRepresentation();
	}

	@Override
	protected Account getRecipient(ShareEntry entry) {
		if (entry != null) {
			return entry.getRecipient();
		}
		return null;
	}

	@Override
	protected String getRecipientRepresentation(ShareEntry entry) {
		User recipient = entry.getRecipient();
		return recipient.getAccountRepresentation();
	}

	@Override
	protected boolean hasReadPermission(Account authUser, Account actor,
			ShareEntry entry, Object... opt) {
		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser,
					TechnicalAccountPermissionType.SHARE_ENTRIES_GET);
		} else if (authUser.isInternal() || authUser.isGuest()) {
			/*
			 * The actor has the right to read his own shareEntry, and the
			 * recipient has the right to read his received shareEntry because
			 * it is same shared entity.
			 */
			if (actor != null && authUser.equals(actor)) {
				return true;
			}
			Account recipient = getRecipient(entry);
			if (recipient != null && authUser.equals(recipient)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasListPermission(Account authUser, Account actor,
			ShareEntry entry, Object... opt) {
		return defaultPermissionCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.SHARE_ENTRIES_LIST, false);
	}

	@Override
	protected boolean hasDeletePermission(Account authUser, Account actor,
			ShareEntry entry, Object... opt) {
		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser,
					TechnicalAccountPermissionType.SHARE_ENTRIES_DELETE);
		} else if (authUser.isInternal() || authUser.isGuest()) {
			/*
			 * The actor has the right to delete his own shareEntry, and the
			 * recipient has the right to delete his received shareEntry because
			 * it is same shared entity.
			 */
			if (actor != null && authUser.equals(actor)) {
				return true;
			}
			Account recipient = getRecipient(entry);
			if (recipient != null && authUser.equals(recipient)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasCreatePermission(Account authUser, Account actor,
			ShareEntry entry, Object... opt) {
		return defaultPermissionCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.SHARE_ENTRIES_CREATE, false);
	}

	@Override
	protected boolean hasUpdatePermission(Account authUser, Account actor,
			ShareEntry entry, Object... opt) {
		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser,
					TechnicalAccountPermissionType.SHARE_ENTRIES_UPDATE);
		} else if (authUser.isInternal() || authUser.isGuest()) {
			/*
			 * The actor has the right to update his own shareEntry, and the
			 * recipient has the right to update his received shareEntry because
			 * it is same shared entity.
			 */
			if (actor != null && authUser.equals(actor)) {
				return true;
			}
			Account recipient = getRecipient(entry);
			if (recipient != null && authUser.equals(recipient)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasDownloadPermission(Account authUser, Account actor,
			ShareEntry entry, Object... opt) {
		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser,
					TechnicalAccountPermissionType.SHARE_ENTRIES_DOWNLOAD);
		} else if (authUser.isInternal() || authUser.isGuest()) {
			/*
			 * Only the recipient has the right to download his received
			 * shareEntry. It makes no sense for the actor.
			 */
			Account recipient = getRecipient(entry);
			if (recipient != null && authUser.equals(recipient)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasDownloadTumbnailPermission(Account authUser,
			Account actor, ShareEntry entry, Object... opt) {
		if (authUser.hasDelegationRole()) {
			return hasPermission(
					authUser,
					TechnicalAccountPermissionType.SHARE_ENTRIES_DOWNLOAD_THUMBNAIL);
		} else if (authUser.isInternal() || authUser.isGuest()) {
			/*
			 * Only the recipient has the right to download the thumb nail of
			 * his received shareEntry. It makes no sense for the actor.
			 */
			Account recipient = getRecipient(entry);
			if (recipient != null && authUser.equals(recipient)) {
				return true;
			}
		}
		return false;
	}
}
