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

package org.linagora.linshare.core.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.GuestBusinessService;
import org.linagora.linshare.core.business.service.impl.GuestBusinessServiceImpl.GuestWithMetadata;
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
import org.linagora.linshare.core.rac.GuestResourceAccessControl;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.MailBuildingService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuestServiceImpl extends GenericServiceImpl<Account, Guest>
		implements GuestService {

	private static final Logger logger = LoggerFactory
			.getLogger(GuestServiceImpl.class);

	private final GuestBusinessService guestBusinessService;

	private final AbstractDomainService abstractDomainService;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final UserService userService;

	private final NotifierService notifierService;

	private final MailBuildingService mailBuildingService;

	public GuestServiceImpl(final GuestBusinessService guestBusinessService,
			final AbstractDomainService abstractDomainService,
			final FunctionalityReadOnlyService functionalityReadOnlyService,
			final UserService userService,
			final NotifierService notifierService,
			final MailBuildingService mailBuildingService,
			final GuestResourceAccessControl rac) {
		super(rac);
		this.guestBusinessService = guestBusinessService;
		this.abstractDomainService = abstractDomainService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.userService = userService;
		this.notifierService = notifierService;
		this.mailBuildingService = mailBuildingService;
	}

	@Override
	public Guest find(Account actor, Account owner, String lsUuid)
			throws BusinessException {
		preChecks(actor, owner);
		Guest guest = guestBusinessService.findByLsUuid(lsUuid);
		if (guest == null) {
			logger.error("Current actor " + owner.getAccountReprentation()
					+ " is looking for a misssing guest : " + lsUuid);
			String message = "Can not find guest with uuid : " + lsUuid;
			throw new BusinessException(BusinessErrorCode.GUEST_NOT_FOUND,
					message);
		}
		checkReadPermission(actor, owner, Guest.class,
				BusinessErrorCode.GUEST_FORBIDDEN, null);
		return guest;
	}

	@Override
	public Guest find(Account actor, Account owner, String domainId, String mail)
			throws BusinessException {
		if (domainId == null) {
			domainId = owner.getDomainId();
		}
		// Ugly. getGuestDomain should check if input domain exists. if not, an
		// exception should be throw
		AbstractDomain domain = abstractDomainService.findById(domainId);
		domain = abstractDomainService.getGuestDomain(domain.getIdentifier());
		return guestBusinessService.find(domain, mail);
	}

	@Override
	public List<Guest> findAllMyGuests(Account actor, Account owner)
			throws BusinessException {
		preChecks(actor, owner);
		checkListPermission(actor, owner, Guest.class,
				BusinessErrorCode.GUEST_FORBIDDEN, null);
		return guestBusinessService.findAllMyGuests(owner);
	}

	@Override
	public boolean exist(String lsUuid) throws BusinessException {
		return guestBusinessService.findByLsUuid(lsUuid) != null;
	}

	@Override
	public Guest create(Account actor, Account owner, Guest guest)
			throws BusinessException {
		preChecks(actor, owner);
		Validate.notNull(guest);
		checkCreatePermission(actor, owner, Guest.class,
				BusinessErrorCode.USER_CANNOT_CREATE_GUEST, null);
		if (!hasGuestDomain(owner.getDomainId())) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_DO_NOT_EXIST,
					"Guest domain was not found");
		}
		Date expiryDate = calculateUserExpiryDate(owner);
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

	void updateValidation(Guest guest) {
		Validate.notNull(guest, "Guest object is required");
		Validate.notEmpty(guest.getLsUuid(), "Guest uuid is required");
		guest.setOwner(null);
		guest.setDomain(null);
	}

	@Override
	public Guest update(Account actor, User owner, Guest guest)
			throws BusinessException {
		preChecks(actor, owner);
		updateValidation(guest);
		Guest original = find(actor, owner, guest.getLsUuid());
		checkUpdatePermission(actor, owner, Guest.class,
				BusinessErrorCode.CANNOT_UPDATE_USER, original);
		GuestDomain guestDomain = abstractDomainService.getGuestDomain(owner
				.getDomainId());
		if (guestDomain == null) {
			throw new BusinessException(
					BusinessErrorCode.USER_CANNOT_CREATE_GUEST,
					"New owner doesn't have guest domain");
		}
		return guestBusinessService.update(owner, guest, guestDomain);
	}

	@Override
	public void delete(Account actor, User owner, String lsUuid)
			throws BusinessException {
		preChecks(actor, owner);
		Validate.notEmpty(lsUuid);
		Guest original = find(actor, owner, lsUuid);
		checkDeletePermission(actor, owner, Guest.class,
				BusinessErrorCode.CANNOT_DELETE_USER, original);
		guestBusinessService.delete(original);
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
		Validate.notEmpty(lsUuid);
		Guest guest = retrieveGuest(lsUuid);
		// TODO : create a log entry for this action
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

	private boolean hasGuestDomain(String topDomainId) {
		return abstractDomainService.getGuestDomain(topDomainId) != null;
	}

	private Date calculateUserExpiryDate(Account owner) {
		Calendar expiryDate = Calendar.getInstance();
		TimeUnitValueFunctionality func = functionalityReadOnlyService
				.getGuestAccountExpiryTimeFunctionality(owner.getDomain());
		expiryDate.add(func.toCalendarValue(), func.getValue());
		return expiryDate.getTime();
	}
}
