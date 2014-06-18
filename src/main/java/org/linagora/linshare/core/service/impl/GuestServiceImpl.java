package org.linagora.linshare.core.service.impl;

import java.util.List;

import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.business.service.GuestBusinessService;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.GuestDomain;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;

public class GuestServiceImpl implements GuestService {

	private static final Logger logger = LoggerFactory
			.getLogger(GuestServiceImpl.class);

	private final GuestBusinessService guestBusinessService;

	private final AbstractDomainService abstractDomainService;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final UserService userService;

	private final DomainPermissionBusinessService domainPermissionBusinessService;

	public GuestServiceImpl(
			final GuestBusinessService guestBusinessService,
			final AbstractDomainService abstractDomainService,
			final FunctionalityReadOnlyService functionalityReadOnlyService,
			final UserService userService,
			final DomainPermissionBusinessService domainPermissionBusinessService) {
		this.guestBusinessService = guestBusinessService;
		this.abstractDomainService = abstractDomainService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.userService = userService;
		this.domainPermissionBusinessService = domainPermissionBusinessService;
	}

	@Override
	public Guest findByLsUuid(User actor, String lsUuid)
			throws BusinessException {
		Assert.notNull(actor);
		Guest guest = guestBusinessService.findByLsUuid(lsUuid);

		if (guest != null
				&& !domainPermissionBusinessService.isAdminForThisUser(actor,
						guest))
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Actor "
					+ actor + " cannot see this guest : " + guest.getLsUuid());
		return guest;
	}

	@Override
	public boolean exist(String lsUuid) throws BusinessException {
		return guestBusinessService.findByLsUuid(lsUuid) != null;
	}

	@Override
	public Guest create(User actor, Guest guest, String ownerLsUuid)
			throws BusinessException {
		Assert.notNull(actor);
		Assert.notNull(guest);
		Assert.notNull(ownerLsUuid);
		User owner = retreiveOwner(ownerLsUuid);
		if (exist(guest.getLsUuid())) {
			throw new BusinessException(BusinessErrorCode.GUEST_ALREADY_EXISTS,
					"Guest already exist");
		}
		if (!exist(ownerLsUuid)) {
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND,
					"Owner not  found");
		}
		if (!canCreateGuest(owner)) {
			throw new BusinessException(
					BusinessErrorCode.USER_CANNOT_CREATE_GUEST,
					"Owner cannot create guest");
		}
		if (!hasGuestDomain(owner.getDomainId())) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_DO_NOT_EXISTS,
					"Guest domain was not found");
		}
		GuestDomain guestDomain = abstractDomainService.getGuestDomain(owner
				.getDomainId());
		if (!guestBusinessService.isValid(guestDomain.getIdentifier(),
				guest.getMail())) {
			throw new BusinessException(BusinessErrorCode.GUEST_ALREADY_EXISTS,
					"Tuple mail/domain already exist");

		}
		return guestBusinessService.create(guest, owner, guestDomain);
	}

	@Override
	public Guest update(User actor, Guest guest, String ownerLsUuid)
			throws BusinessException {
		Assert.notNull(actor);
		Assert.notNull(guest);
		if (!exist(guest.getLsUuid())) {
			throw new BusinessException(BusinessErrorCode.GUEST_ALREADY_EXISTS,
					"Guest does not exist");
		}
		if (domainPermissionBusinessService.isAdminForThisUser(actor, guest)) {
			throw new BusinessException(
					BusinessErrorCode.USER_CANNOT_CREATE_GUEST,
					"Actor cannot update guest");
		}
		// update directly if update does not concern owner
		if (ownerLsUuid == null) {
			return guestBusinessService.update(guest, guest.getOwner(),
					guest.getDomain());
		}
		User owner = retreiveOwner(ownerLsUuid);
		GuestDomain guestDomain = abstractDomainService.getGuestDomain(owner
				.getDomainId());
		return guestBusinessService.update(guest, owner, guestDomain);
	}

	@Override
	public void delete(User actor, String lsUuid) throws BusinessException {
		Assert.notNull(actor);
		Assert.notNull(lsUuid);
		Guest guest = guestBusinessService.findByLsUuid(lsUuid);
		if (guest == null) {
			throw new BusinessException(BusinessErrorCode.GUEST_ALREADY_EXISTS,
					"Guest does not exist");
		}
		if (!domainPermissionBusinessService.isAdminForThisUser(actor, guest)) {
			throw new BusinessException(
					BusinessErrorCode.USER_CANNOT_DELETE_GUEST,
					"Actor cannot update guest");
		}

		guestBusinessService.delete(guest);
	}

	@Override
	public void cleanExpiredGuests(SystemAccount systemAccount) {
		List<Guest> guests = guestBusinessService.findOutdatedGuests();
		logger.info(guests.size() + " guest(s) have been found to be removed");
		for (User guest : guests) {
			try {
				userService.deleteUser(systemAccount, guest.getLsUuid());
				logger.info("Removed expired user : "
						+ guest.getAccountReprentation());
			} catch (BusinessException ex) {
				logger.warn("Unable to remove expired user : "
						+ guest.getAccountReprentation() + "\n" + ex.toString());
			}
		}
	}

	@Override
	public void removeContactRestriction(User actor, String lsUuid)
			throws BusinessException {
		Assert.notNull(actor);
		Assert.notNull(lsUuid);

		Guest guest = guestBusinessService.findByLsUuid(lsUuid);
		guestBusinessService.purgeRestriction(guest);
		guest.setRestricted(false);
		guestBusinessService.update(guest, guest.getOwner(), guest.getDomain());
	}

	@Override
	public void addRestrictedContact(User actor, String lsUuid,
			String contactLsUuid) throws BusinessException {
		Assert.notNull(actor);
		Assert.notNull(lsUuid);

	}

	@Override
	public void resetContactRestrictions(User actor, String lsUuid,
			List<String> mailContacts) throws BusinessException {
		Assert.notNull(actor);
		Assert.notNull(lsUuid);

		removeContactRestriction(actor, lsUuid);
		enableContactRestriction(actor, lsUuid);
		for (String contactLsUuid : mailContacts) {
			addRestrictedContact(actor, lsUuid, contactLsUuid);
		}
	}

	@Override
	public List<AllowedContact> getRestrictedContacts(User actor, String lsUuid)
			throws BusinessException {
		Assert.notNull(actor);
		Assert.notNull(lsUuid);
		Guest guest = guestBusinessService.findByLsUuid(lsUuid);
		if (guest.isRestricted()) {
			return Lists.newArrayList();
		}
		return guestBusinessService.getRestrictedContacts(guest);
	}

	/**
	 * HELPERS
	 */

	private boolean hasGuestDomain(String topDomainId) {
		return abstractDomainService.getGuestDomain(topDomainId) != null;
	}

	private boolean canCreateGuest(User user) {
		if (user.getAccountType() == AccountType.GUEST) {
			return false;
		}
		return guestFunctionalityStatus(user.getDomain());
	}

	private boolean guestFunctionalityStatus(AbstractDomain domain) {
		return functionalityReadOnlyService.getGuestFunctionality(domain)
				.getActivationPolicy().getStatus();
	}

	private User retreiveOwner(String ownerLsUuid) throws BusinessException {
		User owner = userService.findByLsUuid(ownerLsUuid);
		if (owner == null) {
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND,
					"Owner was not found");
		}
		return owner;
	}

	private void enableContactRestriction(User actor, String lsUuid)
			throws BusinessException {
		Guest guest = guestBusinessService.findByLsUuid(lsUuid);
		guest.setRestricted(true);
		update(actor, guest, guest.getLsUuid());
	}
}