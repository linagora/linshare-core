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
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.rac.MailingListResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;

public class MailingListResourceAccessControlImpl extends
		AbstractResourceAccessControlImpl<Account, Account, ContactList> implements MailingListResourceAccessControl {

	public MailingListResourceAccessControlImpl(FunctionalityReadOnlyService functionalityService) {
		super(functionalityService);
	}

	@Override
	protected boolean hasReadPermission(Account authUser, Account actor, ContactList entry, Object... opt) {
		return defaultPermissionCheckListReadRight(authUser, actor, entry, TechnicalAccountPermissionType.LISTS_GET);
	}

	@Override
	protected boolean hasListPermission(Account authUser, Account actor, ContactList entry, Object... opt) {
		return defaultPermissionCheckListReadRight(authUser, actor, entry, TechnicalAccountPermissionType.LISTS_LIST);
	}

	@Override
	protected boolean hasDeletePermission(Account authUser, Account actor, ContactList entry, Object... opt) {
		return defaultPermissionCheckCreateUpdateDeleteRight(authUser, actor, entry, TechnicalAccountPermissionType.LISTS_DELETE);
	}

	@Override
	protected boolean hasCreatePermission(Account authUser, Account actor, ContactList entry, Object... opt) {
		return defaultPermissionCheckCreateUpdateDeleteRight(authUser, actor, entry, TechnicalAccountPermissionType.LISTS_CREATE);
	}

	@Override
	protected boolean hasUpdatePermission(Account authUser, Account actor, ContactList entry, Object... opt) {
		return defaultPermissionCheckCreateUpdateDeleteRight(authUser, actor, entry, TechnicalAccountPermissionType.LISTS_UPDATE);
	}

	private boolean isEnable(Account actor) {
		Functionality func = functionalityService.getContactsListFunctionality(actor.getDomain());
		return func.getActivationPolicy().getStatus();
	}

	private boolean isEnableRW(Account actor) {
		Functionality func = functionalityService.getContactsListCreationFunctionality(actor.getDomain());
		return func.getActivationPolicy().getStatus();
	}

	@Override
	protected String getTargetedAccountRepresentation(Account targetedAccount) {
		return targetedAccount.getAccountRepresentation();
	}

	@Override
	protected String getEntryRepresentation(ContactList entry) {
		return entry.getUuid();
	}

	@Override
	protected Account getOwner(ContactList entry, Object... opt) {
		return entry.getOwner();
	}

	@Override
	protected boolean defaultPermissionCheck(Account authUser, Account actor, ContactList entry,
		TechnicalAccountPermissionType permission) {
		if (authUser.hasDelegationRole())
			return hasPermission(authUser, permission);
		if (authUser.isInternal() || authUser.isGuest()) {
			if (actor != null && authUser.equals(actor)) {
				if (entry != null) {
					return actor.equals(entry.getOwner());
				}
				return true;
			}
		}
		return false;
	}

	protected boolean defaultPermissionCheckCreateUpdateDeleteRight(Account authUser, Account actor, ContactList entry,
			TechnicalAccountPermissionType permission) {
		if (authUser.hasDelegationRole())
			return hasPermission(authUser, permission);
		if (!isEnable(actor)) {
			return false;
		}
		if (!isEnableRW(actor)) {
			return false;
		}
		if (authUser.isInternal() || authUser.isGuest()) {
			if (actor != null && authUser.equals(actor)) {
				if (entry != null) {
					return actor.equals(entry.getOwner());
				} else {
					return true;
				}
			}
		}
		return false;
	}

	protected boolean defaultPermissionCheckListReadRight(Account authUser, Account actor, ContactList entry,
			TechnicalAccountPermissionType permission) {
		if (authUser.hasDelegationRole())
			return hasPermission(authUser, permission);
		if (!isEnable(actor)) {
			return false;
		}
		if (authUser.isInternal() || authUser.isGuest()) {
			if (actor != null && authUser.equals(actor)) {
				if (entry != null) {
					if (actor.equals(entry.getOwner()) || entry.isPublic()) {
						return true;
					}
				} else {
					return true;
				}
			}
		}
		return false;
	}

}
