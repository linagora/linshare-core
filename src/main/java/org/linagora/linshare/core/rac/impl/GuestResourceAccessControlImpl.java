package org.linagora.linshare.core.rac.impl;

import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.rac.GuestResourceAccessControl;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;

public class GuestResourceAccessControlImpl extends
		AbstractResourceAccessControlImpl<Account, Account, Guest> implements
		GuestResourceAccessControl {

	private final FunctionalityReadOnlyService functionalityService;

	private final AbstractDomainService abstractDomainService;

	public GuestResourceAccessControlImpl(
			FunctionalityReadOnlyService functionalityService,
			AbstractDomainService abstractDomainService) {
		super();
		this.functionalityService = functionalityService;
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	protected Account getOwner(Guest entry) {
		return entry.getOwner();
	}

	@Override
	protected String getOwnerRepresentation(Account owner) {
		return owner.getAccountReprentation();
	}

	@Override
	protected String getEntryRepresentation(Guest entry) {
		return entry.getAccountReprentation();
	}

	@Override
	protected boolean hasReadPermission(Account actor, Account owner,
			Guest entry) {
		return defaultPermissionCheck(actor, owner, entry,
				TechnicalAccountPermissionType.GUESTS_GET);
	}

	@Override
	protected boolean hasListPermission(Account actor, Account owner,
			Guest entry) {
		if (actor.hasDelegationRole()) {
			return hasPermission(actor,
					TechnicalAccountPermissionType.GUESTS_LIST);
		} else if (actor.isInternal()) {
			if (entry.getOwner().equals(owner)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasDeletePermission(Account actor, Account owner,
			Guest entry) {
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
			Guest entry) {
		if (actor.hasDelegationRole()) {
			return hasPermission(actor,
					TechnicalAccountPermissionType.GUESTS_CREATE);
		} else if (actor.isInternal()) {
			if (guestFunctionalityStatus(owner.getDomain())) {
				if (hasGuestDomain(owner.getDomainId())) {
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
			Guest entry) {
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
		return functionalityService.getGuestFunctionality(domain)
				.getActivationPolicy().getStatus();
	}

	private boolean hasGuestDomain(String topDomainId) {
		return abstractDomainService.getGuestDomain(topDomainId) != null;
	}

}
