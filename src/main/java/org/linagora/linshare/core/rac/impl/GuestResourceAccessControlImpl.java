/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
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

import org.linagora.linshare.core.business.service.ModeratorBusinessService;
import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Moderator;
import org.linagora.linshare.core.rac.GuestResourceAccessControl;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;

public class GuestResourceAccessControlImpl extends
		AbstractResourceAccessControlImpl<Account, Account, Guest> implements
		GuestResourceAccessControl {

	private final AbstractDomainService abstractDomainService;

	private final ModeratorBusinessService moderatorBusinessService;

	public GuestResourceAccessControlImpl(
			final FunctionalityReadOnlyService functionalityService,
			final AbstractDomainService abstractDomainService,
			final ModeratorBusinessService moderatorBusinessService) {
		super(functionalityService);
		this.abstractDomainService = abstractDomainService;
		this.moderatorBusinessService = moderatorBusinessService;
	}

	@Override
	protected Account getOwner(Guest entry, Object... opt) {
		return null;
	}

	@Override
	protected String getOwnerRepresentation(Account owner) {
		return owner.getAccountRepresentation();
	}

	@Override
	protected String getEntryRepresentation(Guest entry) {
		return entry.getAccountRepresentation();
	}

	@Override
	protected boolean hasReadPermission(Account authUser, Account actor,
			Guest entry, Object... opt) {
		if (authUser.hasDelegationRole())
			return hasPermission(authUser, TechnicalAccountPermissionType.GUESTS_GET);
		if (actor.isInternal() || actor.isGuest()) {
			return true;
		}
		return false;
	}

	@Override
	protected boolean hasListPermission(Account authUser, Account actor,
			Guest entry, Object... opt) {
		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser,
					TechnicalAccountPermissionType.GUESTS_LIST);
		} 
		if (actor.isInternal()) {
			/* Is it usefull to check if the current authUser is an internal ?
			 * Only internals have the right to create guests.
			*/
			if (authUser.equals(actor)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasDeletePermission(Account authUser, Account actor,
			Guest entry, Object... opt) {
		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser,
					TechnicalAccountPermissionType.GUESTS_DELETE);
		} else if (actor.isInternal()) {
			if (entry.getDomain().isManagedBy(actor)) {
				return true;
			}
			if (!guestFunctionalityStatus(actor.getDomain())) {
				return false;
			}
			Optional<Moderator> moderator = moderatorBusinessService.findByGuestAndAccount(actor, entry);
			if (moderator.isPresent() && ModeratorRole.ADMIN.equals(moderator.get().getRole())) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasCreatePermission(Account authUser, Account actor,
			Guest entry, Object... opt) {
		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser,
					TechnicalAccountPermissionType.GUESTS_CREATE);
		} else if (authUser.isInternal()) {
			// Only internal users can create guests.
			if (guestFunctionalityStatus(actor.getDomain())) {
				// We can not create guests if guest functionality is not enable.
				if (hasGuestDomain(actor.getDomainId())) {
					// We can not create guest if there is no guest domain.
					// TODO : Add a check if user still can guest.
					return true;
				} else {
					logger.error("Missing guest domain to create a guest.");
				}
			} else {
				logger.warn("Guests functionality is not enabled.");
			}
		}
		return false;
	}

	@Override
	protected boolean hasUpdatePermission(Account authUser, Account actor,
			Guest entry, Object... opt) {
		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser,
					TechnicalAccountPermissionType.GUESTS_UPDATE);
		} else if (actor.isInternal()) {
			// if actor is an administrator of this guest, we let him through,
			// no matter if the functionality is enabled or not.
			if (entry.getDomain().isManagedBy(actor)) {
				return true;
			}
			// the user is a regular user, so he can not use this endpoint if func is disabled.
			if (!guestFunctionalityStatus(actor.getDomain())) {
				return false;
			}
			Optional<Moderator> moderator = moderatorBusinessService.findByGuestAndAccount(actor, entry);
			if (moderator.isPresent() &&
					(moderator.get().isAdminModerator() || moderator.get().isSimpleModerator())
				) {
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
			logger.warn("guest functionality is disable.");
		}
		return status;
	}

	private boolean hasGuestDomain(String topDomainId) {
		boolean status = abstractDomainService.findGuestDomain(topDomainId) != null;
		if (!status) {
			logger.warn("guest domain do not exist.");
		}
		return status;
	}

	@Override
	protected String getTargetedAccountRepresentation(Account targetedAccount) {
		return targetedAccount.getAccountRepresentation();
	}

}
