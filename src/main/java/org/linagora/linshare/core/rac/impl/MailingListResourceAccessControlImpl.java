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
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.MailingList;
import org.linagora.linshare.core.rac.MailingListResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;

public class MailingListResourceAccessControlImpl extends
		AbstractResourceAccessControlImpl<Account, Account, MailingList> implements MailingListResourceAccessControl {

	public MailingListResourceAccessControlImpl(FunctionalityReadOnlyService functionalityService) {
		super(functionalityService);
	}

	@Override
	protected boolean hasReadPermission(Account authUser, Account actor, MailingList entry, Object... opt) {
		return defaultPermissionCheckListReadRight(authUser, actor, entry, TechnicalAccountPermissionType.LISTS_GET);
	}

	@Override
	protected boolean hasListPermission(Account authUser, Account actor, MailingList entry, Object... opt) {
		return defaultPermissionCheckListReadRight(authUser, actor, entry, TechnicalAccountPermissionType.LISTS_LIST);
	}

	@Override
	protected boolean hasDeletePermission(Account authUser, Account actor, MailingList entry, Object... opt) {
		return defaultPermissionCheckCreateUpdateDeleteRight(authUser, actor, entry, TechnicalAccountPermissionType.LISTS_DELETE);
	}

	@Override
	protected boolean hasCreatePermission(Account authUser, Account actor, MailingList entry, Object... opt) {
		return defaultPermissionCheckCreateUpdateDeleteRight(authUser, actor, entry, TechnicalAccountPermissionType.LISTS_CREATE);
	}

	@Override
	protected boolean hasUpdatePermission(Account authUser, Account actor, MailingList entry, Object... opt) {
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
	protected String getEntryRepresentation(MailingList entry) {
		return entry.getUuid();
	}

	@Override
	protected Account getOwner(MailingList entry, Object... opt) {
		return entry.getOwner();
	}

	@Override
	protected boolean defaultPermissionCheck(Account authUser, Account actor, MailingList entry,
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

	protected boolean defaultPermissionCheckCreateUpdateDeleteRight(Account authUser, Account actor, MailingList entry,
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

	protected boolean defaultPermissionCheckListReadRight(Account authUser, Account actor, MailingList entry,
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
