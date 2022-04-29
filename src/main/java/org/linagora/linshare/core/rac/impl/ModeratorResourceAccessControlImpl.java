/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */

package org.linagora.linshare.core.rac.impl;

import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Moderator;
import org.linagora.linshare.core.rac.ModeratorResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;

public class ModeratorResourceAccessControlImpl extends AbstractResourceAccessControlImpl<Account, Account, Moderator>
		implements ModeratorResourceAccessControl {

	private DomainPermissionBusinessService domainPermissionService;

	public ModeratorResourceAccessControlImpl(
			FunctionalityReadOnlyService functionalityService,
			DomainPermissionBusinessService domainPermissionService) {
		super(functionalityService);
		this.domainPermissionService = domainPermissionService;
	}

	@Override
	protected boolean hasReadPermission(Account authUser, Account actor, Moderator entry, Object... opt) {
		return true;
	}

	@Override
	protected boolean hasListPermission(Account authUser, Account actor, Moderator entry, Object... opt) {
		return true;
	}

	@Override
	protected boolean hasDeletePermission(Account authUser, Account actor, Moderator entry, Object... opt) {
		return true;
	}

	@Override
	protected boolean hasCreatePermission(Account authUser, Account actor, Moderator entry, Object... opt) {
		return true;
	}

	private boolean checkAdminFor(Account authUser, Account actor, Moderator entry) {
		if(actor.hasAdminRole()) {
			return domainPermissionService.isAdminForThisUser(actor, entry.getGuest());
		}
		if(authUser.equals(actor)) {
			return true;
		}
		if (actor.getLsUuid().equals(entry.getAccount().getLsUuid())) {
			return true;
		}
		return false;
	}

	@Override
	protected boolean hasUpdatePermission(Account authUser, Account actor, Moderator entry, Object... opt) {
		return true;
	}

	@Override
	protected String getTargetedAccountRepresentation(Account actor) {
		return actor.getAccountRepresentation();
	}

	@Override
	protected Account getOwner(Moderator entry, Object... opt) {
		if (entry != null) {
			return entry.getAccount();
		}
		return null;
	}

	@Override
	protected String getEntryRepresentation(Moderator entry) {
		return entry.getUuid();
	}
}
