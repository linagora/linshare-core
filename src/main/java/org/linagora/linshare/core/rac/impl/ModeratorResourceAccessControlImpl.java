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

import java.util.Optional;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.ModeratorBusinessService;
import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Moderator;
import org.linagora.linshare.core.rac.ModeratorResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;

public class ModeratorResourceAccessControlImpl extends AbstractResourceAccessControlImpl<Account, Account, Moderator>
		implements ModeratorResourceAccessControl {

	private ModeratorBusinessService moderatorBusinessService;

	public ModeratorResourceAccessControlImpl(
			FunctionalityReadOnlyService functionalityService,
			ModeratorBusinessService moderatorBusinessService) {
		super(functionalityService);
		this.moderatorBusinessService = moderatorBusinessService;
	}

	@Override
	protected boolean hasReadPermission(Account authUser, Account actor, Moderator entry, Object... opt) {
		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser, TechnicalAccountPermissionType.GUESTS_MODERATOR_GET);
		} else if (actor.isInternal()) {
			if (entry.getGuest().getDomain().isManagedBy(actor)) {
				return true;
			}
			Optional<Moderator> moderator = moderatorBusinessService.findByGuestAndAccount(actor, entry.getGuest());
			if (moderator.isPresent()) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasListPermission(Account authUser, Account actor, Moderator entry, Object... opt) {
		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser, TechnicalAccountPermissionType.GUESTS_MODERATOR_LIST);
		} else if (actor.isInternal()) {
			Validate.notNull(opt[0], "Guest should be set.");
			Guest guest = (Guest) opt[0];
			if (guest.getDomain().isManagedBy(actor)) {
				return true;
			}
			Optional<Moderator> moderator = moderatorBusinessService.findByGuestAndAccount(actor, guest);
			if (moderator.isPresent()) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasDeletePermission(Account authUser, Account actor, Moderator entry, Object... opt) {
		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser,
					TechnicalAccountPermissionType.GUESTS_MODERATOR_DELETE);
		} else if (actor.isInternal()) {
			if (entry.getGuest().getDomain().isManagedBy(actor)) {
				return true;
			}
			if (!guestFunctionalityStatus(actor.getDomain())) {
				return false;
			}
			Optional<Moderator> moderator = moderatorBusinessService.findByGuestAndAccount(actor, entry.getGuest());
			if (moderator.isPresent() && ModeratorRole.ADMIN.equals(moderator.get().getRole())) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasCreatePermission(Account authUser, Account actor, Moderator entry, Object... opt) {
		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser, TechnicalAccountPermissionType.GUESTS_MODERATOR_CREATE);
		} else if (actor.isInternal()) {
			if (entry.getGuest().getDomain().isManagedBy(actor)) {
				return true;
			}
			if (!guestFunctionalityStatus(actor.getDomain())) {
				return false;
			}
			Optional<Moderator> moderator = moderatorBusinessService.findByGuestAndAccount(actor, entry.getGuest());
			if (moderator.isPresent() && ModeratorRole.ADMIN.equals(moderator.get().getRole())) {
				return true;
			}
			// The guest creator is automatically its first moderator with ADMIN role, we
			// will return true if `onGuestCreation` status is true
			Boolean onGuestCreation = (Boolean) opt[0];
			if (onGuestCreation) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasUpdatePermission(Account authUser, Account actor, Moderator entry, Object... opt) {
		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser, TechnicalAccountPermissionType.GUESTS_MODERATOR_UPDATE);
		} else if (actor.isInternal()) {
			if (entry.getGuest().getDomain().isManagedBy(actor)) {
				return true;
			}
			if (!guestFunctionalityStatus(actor.getDomain())) {
				return false;
			}
			Optional<Moderator> moderator = moderatorBusinessService.findByGuestAndAccount(actor, entry.getGuest());
			if (moderator.isPresent() && ModeratorRole.ADMIN.equals(moderator.get().getRole())) {
				return true;
			}
		}
		return false;
	}

	private boolean guestFunctionalityStatus(AbstractDomain domain) {
		Functionality guestFunctionality = functionalityService.getGuests(domain);
		boolean status = guestFunctionality
				.getActivationPolicy().getStatus();
		if (!status) {
			logger.warn("guest functionality is disabled.");
		}
		return status;
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
