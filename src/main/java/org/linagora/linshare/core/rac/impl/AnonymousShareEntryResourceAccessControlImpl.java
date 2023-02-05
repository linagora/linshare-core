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
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.rac.AnonymousShareEntryResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;

public class AnonymousShareEntryResourceAccessControlImpl extends
		EntryResourceAccessControlImpl<Contact, AnonymousShareEntry> implements
		AnonymousShareEntryResourceAccessControl {

	public AnonymousShareEntryResourceAccessControlImpl(
			final FunctionalityReadOnlyService functionalityService) {
		super(functionalityService);
	}

	@Override
	protected String getEntryRepresentation(AnonymousShareEntry entry) {
		StringBuilder sb = new StringBuilder(entry.getEntryType().toString());
		sb.append(" (");
		sb.append(entry.getUuid());
		sb.append(") ");
		return sb.toString();
	}

	@Override
	protected Account getOwner(AnonymousShareEntry entry, Object... opt) {
		Account owner = entry.getEntryOwner();
		return owner;
	}

	@Override
	protected String getOwnerRepresentation(Account owner) {
		return owner.getAccountRepresentation();
	}

	@Override
	protected boolean hasReadPermission(Account authUser, Account actor,
			AnonymousShareEntry entry, Object... opt) {
		if (authUser.hasAnonymousShareSystemAccountRole()) {
			return true;
		}
		return defaultPermissionCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.ANONYMOUS_SHARE_ENTRIES_GET);
	}

	@Override
	protected boolean hasListPermission(Account authUser, Account actor,
			AnonymousShareEntry entry, Object... opt) {
		return defaultPermissionCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.ANONYMOUS_SHARE_ENTRIES_LIST, false);
	}

	@Override
	protected boolean hasDeletePermission(Account authUser, Account actor,
			AnonymousShareEntry entry, Object... opt) {
		return defaultPermissionCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.ANONYMOUS_SHARE_ENTRIES_DELETE);
	}

	@Override
	protected boolean hasCreatePermission(Account authUser, Account actor,
			AnonymousShareEntry entry, Object... opt) {
		if (defaultPermissionCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.ANONYMOUS_SHARE_ENTRIES_CREATE, false)) {
			Functionality anonymousUrl = functionalityService.getAnonymousUrl(actor.getDomain());
			if (anonymousUrl.getActivationPolicy().getStatus()) {
				return true;
			} else
				logger.debug("Functionality: ANONYMOUS URL not enable.");
		}
		return false;
	}

	@Override
	protected boolean hasUpdatePermission(Account authUser, Account actor,
			AnonymousShareEntry entry, Object... opt) {
		return defaultPermissionCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.ANONYMOUS_SHARE_ENTRIES_UPDATE);
	}

	@Override
	protected boolean hasDownloadPermission(Account authUser, Account actor,
			AnonymousShareEntry entry, Object... opt) {
		if (authUser.hasAnonymousShareSystemAccountRole()) {
			return true;
		}
		/*
		 * The actor has not the right to download his own anonymousShareEntry,
		 * neither account with delegation role.
		 */
		return false;
	}

	@Override
	protected boolean hasDownloadTumbnailPermission(Account authUser,
			Account actor, AnonymousShareEntry entry, Object... opt) {
		if (authUser.hasAnonymousShareSystemAccountRole()) {
			return true;
		}
		/*
		 * The actor has not the right to download his own anonymousShareEntry,
		 * neither account with delegation role.
		 */
		return false;
	}
}
