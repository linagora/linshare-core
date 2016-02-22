/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
	protected boolean hasReadPermission(Account actor, Account owner,
			ShareEntry entry, Object... opt) {
		if (actor.hasDelegationRole()) {
			return hasPermission(actor,
					TechnicalAccountPermissionType.SHARE_ENTRIES_GET);
		} else if (actor.isInternal() || actor.isGuest()) {
			/*
			 * The owner has the right to read his own shareEntry, and the
			 * recipient has the right to read his received shareEntry because
			 * it is same shared entity.
			 */
			if (owner != null && actor.equals(owner)) {
				return true;
			}
			Account recipient = getRecipient(entry);
			if (recipient != null && actor.equals(recipient)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasListPermission(Account actor, Account owner,
			ShareEntry entry, Object... opt) {
		return defaultPermissionCheck(actor, owner, entry,
				TechnicalAccountPermissionType.SHARE_ENTRIES_LIST);
	}

	@Override
	protected boolean hasDeletePermission(Account actor, Account owner,
			ShareEntry entry, Object... opt) {
		if (actor.hasDelegationRole()) {
			return hasPermission(actor,
					TechnicalAccountPermissionType.SHARE_ENTRIES_DELETE);
		} else if (actor.isInternal() || actor.isGuest()) {
			/*
			 * The owner has the right to delete his own shareEntry, and the
			 * recipient has the right to delete his received shareEntry because
			 * it is same shared entity.
			 */
			if (owner != null && actor.equals(owner)) {
				return true;
			}
			Account recipient = getRecipient(entry);
			if (recipient != null && actor.equals(recipient)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasCreatePermission(Account actor, Account owner,
			ShareEntry entry, Object... opt) {
		return defaultPermissionCheck(actor, owner, entry,
				TechnicalAccountPermissionType.SHARE_ENTRIES_CREATE);
	}

	@Override
	protected boolean hasUpdatePermission(Account actor, Account owner,
			ShareEntry entry, Object... opt) {
		if (actor.hasDelegationRole()) {
			return hasPermission(actor,
					TechnicalAccountPermissionType.SHARE_ENTRIES_UPDATE);
		} else if (actor.isInternal() || actor.isGuest()) {
			/*
			 * The owner has the right to update his own shareEntry, and the
			 * recipient has the right to update his received shareEntry because
			 * it is same shared entity.
			 */
			if (owner != null && actor.equals(owner)) {
				return true;
			}
			Account recipient = getRecipient(entry);
			if (recipient != null && actor.equals(recipient)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasDownloadPermission(Account actor, Account owner,
			ShareEntry entry, Object... opt) {
		if (actor.hasDelegationRole()) {
			return hasPermission(actor,
					TechnicalAccountPermissionType.SHARE_ENTRIES_DOWNLOAD);
		} else if (actor.isInternal() || actor.isGuest()) {
			/*
			 * Only the recipient has the right to download his received
			 * shareEntry. It makes no sense for the owner.
			 */
			Account recipient = getRecipient(entry);
			if (recipient != null && actor.equals(recipient)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasDownloadTumbnailPermission(Account actor,
			Account owner, ShareEntry entry, Object... opt) {
		if (actor.hasDelegationRole()) {
			return hasPermission(
					actor,
					TechnicalAccountPermissionType.SHARE_ENTRIES_DOWNLOAD_THUMBNAIL);
		} else if (actor.isInternal() || actor.isGuest()) {
			/*
			 * Only the recipient has the right to download the thumb nail of
			 * his received shareEntry. It makes no sense for the owner.
			 */
			Account recipient = getRecipient(entry);
			if (recipient != null && actor.equals(recipient)) {
				return true;
			}
		}
		return false;
	}
}
