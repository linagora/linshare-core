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
			if (moderator.isPresent() && moderator.get().isAdminModerator()) {
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
			if (moderator.isPresent() && moderator.get().isAdminModerator()) {
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
