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
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserLogEntry;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.GuestResourceAccessControl;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.MailBuildingService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.GuestAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.repository.AuditUserMongoRepository;

import com.google.common.collect.Lists;

public class GuestServiceImpl extends GenericServiceImpl<Account, Guest>
		implements GuestService {

	private final GuestBusinessService guestBusinessService;

	private final AbstractDomainService abstractDomainService;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final UserService userService;

	private final NotifierService notifierService;

	private final MailBuildingService mailBuildingService;

	private final LogEntryService LogEntryService;

	private final AuditUserMongoRepository auditMongoRepository;

	public GuestServiceImpl(final GuestBusinessService guestBusinessService,
			final AbstractDomainService abstractDomainService,
			final FunctionalityReadOnlyService functionalityReadOnlyService,
			final UserService userService,
			final NotifierService notifierService,
			final MailBuildingService mailBuildingService,
			final LogEntryService logEntryService,
			final AuditUserMongoRepository auditMongoRepository,
			final GuestResourceAccessControl rac) {
		super(rac);
		this.guestBusinessService = guestBusinessService;
		this.abstractDomainService = abstractDomainService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.userService = userService;
		this.notifierService = notifierService;
		this.mailBuildingService = mailBuildingService;
		this.LogEntryService = logEntryService;
		this.auditMongoRepository = auditMongoRepository;
	}

	@Override
	public Guest find(Account actor, Account owner, String lsUuid)
			throws BusinessException {
		preChecks(actor, owner);
		Guest guest = guestBusinessService.findByLsUuid(lsUuid);
		if (guest == null) {
			logger.error("Current actor " + owner.getAccountRepresentation()
					+ " is looking for a misssing guest : " + lsUuid);
			String message = "Can not find guest with uuid : " + lsUuid;
			throw new BusinessException(BusinessErrorCode.GUEST_NOT_FOUND,
					message);
		}
		checkReadPermission(actor, owner, Guest.class,
				BusinessErrorCode.GUEST_FORBIDDEN, guest);
		GuestAuditLogEntry log = new GuestAuditLogEntry(actor, owner, LogAction.GET, AuditLogEntryType.GUEST, guest);
		auditMongoRepository.insert(log);
		return guest;
	}

	@Override
	public List<AllowedContact> load(Account actor, User guest)
			throws BusinessException {
		preChecks(actor, guest);
		Guest guest2 = guestBusinessService.findByLsUuid(guest.getLsUuid());
		checkReadPermission(actor, actor, Guest.class,
				BusinessErrorCode.GUEST_FORBIDDEN, guest2);
		return guestBusinessService.loadAllowedContacts(guest);
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
		domain = abstractDomainService.getGuestDomain(domain.getUuid());
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
	public Guest create(Account actor, Account owner, Guest guest,
			List<String> restrictedMails) throws BusinessException {
		preChecks(actor, owner);
		Validate.notNull(guest);
		if (guest.isRestricted()) {
			Validate.notNull(restrictedMails,
					"A restricted guest must have a restricted list of contacts (mails)");
			Validate.notEmpty(restrictedMails,
					"A restricted guest must have a restricted list of contacts (mails)");
		}
		Validate.notEmpty(guest.getMail(), "Guest mail must be set.");
		checkCreatePermission(actor, owner, Guest.class,
				BusinessErrorCode.USER_CANNOT_CREATE_GUEST, null);
		if (!hasGuestDomain(owner.getDomainId())) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_DO_NOT_EXIST,
					"Guest domain was not found");
		}
		Date expiryDate = guest.getExpirationDate();
		if (expiryDate != null) {
			checkDateValidity((User) owner, null, expiryDate, null, guest);
		} else {
			expiryDate = calculateUserExpiryDate(owner, null);
			guest.setExpirationDate(expiryDate);
		}
		AbstractDomain guestDomain = abstractDomainService.getGuestDomain(owner
				.getDomainId());
		if (!guestBusinessService.exist(guestDomain.getUuid(),
				guest.getMail())) {
			throw new BusinessException(BusinessErrorCode.GUEST_ALREADY_EXISTS,
					"Pair mail/domain already exist");
		}
		guest.setRole(Role.SIMPLE);
		List<User> restrictedContacts = null;
		if (guest.isRestricted()) {
			restrictedContacts = transformToUsers(actor, restrictedMails);
			if (restrictedContacts == null || restrictedContacts.isEmpty()) {
				throw new BusinessException(
						BusinessErrorCode.GUEST_INVALID_INPUT,
						"Can not create a restricted guest without restricted contacts (internal or guest users only).");
			}
		}
		GuestWithMetadata create = guestBusinessService.create(owner, guest,
				guestDomain, restrictedContacts);
		MailContainerWithRecipient mail = mailBuildingService.buildNewGuest(
				owner, create.getGuest(), create.getPassword());
		notifierService.sendNotification(mail);
		UserLogEntry userLogEntry = new UserLogEntry(actor, LogAction.USER_CREATE, "Creating a guest", create.getGuest());
		LogEntryService.create(userLogEntry);
		GuestAuditLogEntry log = new GuestAuditLogEntry(actor, owner, LogAction.CREATE, AuditLogEntryType.GUEST, guest);
		auditMongoRepository.insert(log);
		return create.getGuest();
	}

	@Override
	public Guest update(Account actor, User owner, Guest guest,
			List<String> restrictedMails) throws BusinessException {
		preChecks(actor, owner);
		Validate.notNull(guest, "Guest object is required");
		Validate.notEmpty(guest.getLsUuid(), "Guest uuid is required");
		// In case if guestDto was an existing modified entity.
		guestBusinessService.evict(guest);
		Guest entity = find(actor, owner, guest.getLsUuid());
		GuestAuditLogEntry log = new GuestAuditLogEntry(actor, owner, LogAction.UPDATE, AuditLogEntryType.GUEST,
				entity);
		checkUpdatePermission(actor, owner, Guest.class,
				BusinessErrorCode.CANNOT_UPDATE_USER, entity);
		AbstractDomain guestDomain = abstractDomainService.getGuestDomain(owner
				.getDomainId());
		if (guestDomain == null) {
			throw new BusinessException(
					BusinessErrorCode.USER_CANNOT_CREATE_GUEST,
					"New owner doesn't have guest domain");
		}
		List<User> restrictedContacts = transformToUsers(actor, restrictedMails);
		Date newExpirationDate = guest.getExpirationDate();
		if (newExpirationDate != null) {
			checkDateValidity(
					owner,
					entity.getExpirationDate(),
					newExpirationDate,
					functionalityReadOnlyService
							.getGuestsExpirationDateProlongation(
									owner.getDomain()).getDelegationPolicy()
							.getStatus(), entity);
		} else {
			guest.setExpirationDate(entity.getExpirationDate());
		}
		Guest result = guestBusinessService.update(owner, entity, guest, guestDomain,
				restrictedContacts);
		UserLogEntry userLogEntry = new UserLogEntry(actor, LogAction.USER_UPDATE, "Updating a guest", entity);
		LogEntryService.create(userLogEntry);
		log.setResourceUpdated(new AccountMto(result));
		auditMongoRepository.insert(log);
		return result;
	}

	private void checkDateValidity(User owner, Date oldExpiryDate,
			Date dateToCheck, Boolean prolongation, Guest guest) {

		if (oldExpiryDate == null) {
			if (dateToCheck.before(new Date())
					|| dateToCheck.after(calculateUserExpiryDate(owner, null))) {
				throw new BusinessException(
						BusinessErrorCode.GUEST_EXPIRY_DATE_INVALID,
						"Guest expiry date invalid.");
			}
		} else {
			Date date;
			if (prolongation) {
				date = calculateUserExpiryDate(owner, null);
			} else
				date = calculateUserExpiryDate(owner, guest.getCreationDate());
			if (dateToCheck.before(new Date()) || dateToCheck.after(date)) {
				throw new BusinessException(
						BusinessErrorCode.GUEST_EXPIRY_DATE_INVALID,
						"Guest expiry date invalid.");
			}
		}
	}

	private List<User> transformToUsers(Account actor,
			List<String> restrictedMails) {
		if (restrictedMails != null) {
			List<User> restrictedContacts = Lists.newArrayList();
			for (String mail : restrictedMails) {
				try {
					User user = userService.findOrCreateUser(mail,
							actor.getDomainId());
					restrictedContacts.add(user);
				} catch (BusinessException ex) {
					logger.error("You can not restricted a guest to a simple email address. It must be an User.");
				}
			}
			return restrictedContacts;
		}
		return null;
	}

	@Override
	public Guest delete(Account actor, User owner, String lsUuid)
			throws BusinessException {
		preChecks(actor, owner);
		Validate.notEmpty(lsUuid);
		Guest original = find(actor, owner, lsUuid);
		checkDeletePermission(actor, owner, Guest.class,
				BusinessErrorCode.CANNOT_DELETE_USER, original);
		UserLogEntry userLogEntry = new UserLogEntry(actor, LogAction.USER_DELETE, "Deleting a guest", original);
		LogEntryService.create(userLogEntry);
		guestBusinessService.delete(original);
		GuestAuditLogEntry log = new GuestAuditLogEntry(actor, owner, LogAction.USER_CREATE, AuditLogEntryType.GUEST, original);
		auditMongoRepository.insert(log);
		return original;
	}

	@Override
	public void deleteUser(SystemAccount systemAccount, String uuid) {
		userService.deleteUser(systemAccount, uuid);
	}

	@Override
	public List<String> findOudatedGuests(SystemAccount systemAccount)
			throws BusinessException {
		Validate.notNull(systemAccount);
		return guestBusinessService.findOutdatedGuestIdentifiers();
	}

	@Override
	public Guest findOudatedGuest(SystemAccount systemAccount, String uuid)
			throws BusinessException {
		Validate.notNull(systemAccount);
		Validate.notEmpty(uuid);
		return guestBusinessService.findByLsUuid(uuid);
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

	private Date calculateUserExpiryDate(Account owner, Date guestCreationDate) {
		Calendar expiryDate = Calendar.getInstance();
		if (guestCreationDate == null) {
			TimeUnitValueFunctionality func = functionalityReadOnlyService
					.getGuestsExpiration(owner.getDomain());
			expiryDate.add(func.toCalendarValue(), func.getValue());
		} else {
			expiryDate.setTime(guestCreationDate);
			expiryDate.add(Calendar.MONTH, 3);
		}
		return expiryDate.getTime();
	}

	@Override
	public Date getGuestExpirationDate(Account actor,
			Date currentGuestExpirationDate) throws BusinessException {
		return calculateUserExpiryDate(actor, currentGuestExpirationDate);
	}
}
