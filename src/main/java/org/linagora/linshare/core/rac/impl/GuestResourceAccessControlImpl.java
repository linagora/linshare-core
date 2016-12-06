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
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.rac.GuestResourceAccessControl;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;

public class GuestResourceAccessControlImpl extends
		AbstractResourceAccessControlImpl<Account, Account, Guest> implements
		GuestResourceAccessControl {

	private final AbstractDomainService abstractDomainService;

	public GuestResourceAccessControlImpl(
			final FunctionalityReadOnlyService functionalityService,
			final AbstractDomainService abstractDomainService) {
		super(functionalityService);
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	protected Account getOwner(Guest entry, Object... opt) {
		if (entry != null) {
			return entry.getOwner();
		}
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
	protected boolean hasReadPermission(Account actor, Account owner,
			Guest entry, Object... opt) {
		if (actor.hasDelegationRole())
			return hasPermission(actor, TechnicalAccountPermissionType.GUESTS_GET);
		if (actor.isInternal() || actor.isGuest()) {
			return true;
		}
		return false;
	}

	@Override
	protected boolean hasListPermission(Account actor, Account owner,
			Guest entry, Object... opt) {
		if (actor.hasDelegationRole()) {
			return hasPermission(actor,
					TechnicalAccountPermissionType.GUESTS_LIST);
		} else if (actor.isInternal()) {
			/* Is it usefull to check if the current actor is an internal ?
			 * Only internals have the right to create guests.
			*/
			if (actor.equals(owner)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasDeletePermission(Account actor, Account owner,
			Guest entry, Object... opt) {
		if (actor.hasDelegationRole()) {
			return hasPermission(actor,
					TechnicalAccountPermissionType.GUESTS_DELETE);
		} else if (actor.isInternal()) {
			if (entry.getOwner().equals(owner)) {
				return true;
			}
			if (!entry.getDomain().isManagedBy(actor)) {
				return false;
			}
		}
		return false;
	}

	@Override
	protected boolean hasCreatePermission(Account actor, Account owner,
			Guest entry, Object... opt) {
		if (actor.hasDelegationRole()) {
			return hasPermission(actor,
					TechnicalAccountPermissionType.GUESTS_CREATE);
		} else if (actor.isInternal()) {
			// Only internal users can create guests.
			if (guestFunctionalityStatus(owner.getDomain())) {
				// We can not create guests if guest functionality is not enable.
				if (hasGuestDomain(owner.getDomainId())) {
					// We can not create guest if there is no guest domain.
					// TODO : Add a check if user still can guest.
					return true;
				} else {
					logger.error("Missing guest domain to create a guest.");
				}
			}
		}
		return false;
	}

	@Override
	protected boolean hasUpdatePermission(Account actor, Account owner,
			Guest entry, Object... opt) {
		if (actor.hasDelegationRole()) {
			return hasPermission(actor,
					TechnicalAccountPermissionType.GUESTS_UPDATE);
		} else if (actor.isInternal()) {
			if (entry.getOwner().equals(owner)) {
				return true;
			}
			if (!entry.getDomain().isManagedBy(actor)) {
				return false;
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
