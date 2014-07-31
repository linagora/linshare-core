/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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

package org.linagora.linshare.core.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.business.service.GuestBusinessService;
import org.linagora.linshare.core.business.service.impl.GuestBusinessServiceImpl.GuestWithMetadata;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.GuestDomain;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.MailBuildingService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class GuestServiceImpl implements GuestService {

	private static final Logger logger = LoggerFactory
			.getLogger(GuestServiceImpl.class);

	private final GuestBusinessService guestBusinessService;

	private final AbstractDomainService abstractDomainService;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final UserService userService;

	private final DomainPermissionBusinessService domainPermissionBusinessService;

	private final NotifierService notifierService;

	private final MailBuildingService mailBuildingService;

	public GuestServiceImpl(
			final GuestBusinessService guestBusinessService,
			final AbstractDomainService abstractDomainService,
			final FunctionalityReadOnlyService functionalityReadOnlyService,
			final UserService userService,
			final DomainPermissionBusinessService domainPermissionBusinessService,
			final NotifierService notifierService,
			final MailBuildingService mailBuildingService) {
		this.guestBusinessService = guestBusinessService;
		this.abstractDomainService = abstractDomainService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.userService = userService;
		this.domainPermissionBusinessService = domainPermissionBusinessService;
		this.notifierService = notifierService;
		this.mailBuildingService = mailBuildingService;
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
		User owner = retrieveOwner(ownerLsUuid);
		Date expiryDate = calculateUserExpiryDate(owner);
		if (userService.findByLsUuid(ownerLsUuid) == null) {
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND,
					"Owner not found");
		}
		if (!canCreateGuest(owner)) {
			throw new BusinessException(
					BusinessErrorCode.USER_CANNOT_CREATE_GUEST,
					"Owner cannot create guest");
		}
		if (!hasGuestDomain(owner.getDomainId())) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_DO_NOT_EXIST,
					"Guest domain was not found");
		}
		GuestDomain guestDomain = abstractDomainService.getGuestDomain(owner
				.getDomainId());
		if (!guestBusinessService.exist(guestDomain.getIdentifier(),
				guest.getMail())) {
			throw new BusinessException(BusinessErrorCode.GUEST_ALREADY_EXISTS,
					"Pair mail/domain already exist");
		}
		GuestWithMetadata create = guestBusinessService.create(guest, owner,
				guestDomain, expiryDate);
		MailContainerWithRecipient mail = mailBuildingService.buildNewGuest(
				owner, create.getGuest(), create.getPassword());
		notifierService.sendNotification(mail);
		return create.getGuest();
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
		if (!domainPermissionBusinessService.isAdminForThisUser(actor, guest)) {
			throw new BusinessException(
					BusinessErrorCode.USER_CANNOT_CREATE_GUEST,
					"Actor cannot update guest");
		}
		// update directly if update does not concern owner
		if (ownerLsUuid == null) {
			return guestBusinessService.update(guest, guest.getOwner(),
					guest.getDomain());
		}
		User owner = retrieveOwner(ownerLsUuid);
		GuestDomain guestDomain = abstractDomainService.getGuestDomain(owner
				.getDomainId());
		if (guestDomain == null) {
			throw new BusinessException(
					BusinessErrorCode.USER_CANNOT_CREATE_GUEST,
					"New owner doesn't have guest domain");
		}
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
					"Actor cannot delete guest");
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
	public void resetPassword(String lsUuid) throws BusinessException {
		Assert.notNull(lsUuid);
		Guest guest = retrieveGuest(lsUuid);
		GuestWithMetadata update = guestBusinessService.resetPassword(guest);
		MailContainerWithRecipient mail = mailBuildingService
				.buildResetPassword(update.getGuest(), update.getPassword());
		notifierService.sendNotification(mail);
	}

	/**
	 * HELPERS
	 */

	private Guest retrieveGuest(String lsUuid) throws BusinessException {
		Guest guest = guestBusinessService.findByLsUuid(lsUuid);
		if (guest == null) {
			throw new BusinessException(BusinessErrorCode.GUEST_NOT_FOUND,
					"Guest does not exist");
		}
		return guest;
	}

	private User retrieveOwner(String ownerLsUuid) throws BusinessException {
		User owner = userService.findByLsUuid(ownerLsUuid);
		if (owner == null) {
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND,
					"Owner was not found");
		}
		return owner;
	}

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

	private Date calculateUserExpiryDate(Account owner) {
		Calendar expiryDate = Calendar.getInstance();
		TimeUnitValueFunctionality func = functionalityReadOnlyService
				.getGuestAccountExpiryTimeFunctionality(owner.getDomain());
		expiryDate.add(func.toCalendarUnitValue(), func.getValue());
		return expiryDate.getTime();
	}
}