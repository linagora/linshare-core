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
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.ContainerQuotaBusinessService;
import org.linagora.linshare.core.business.service.GuestBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.core.domain.constants.ResetTokenKind;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.GuestAccountNewCreationEmailContext;
import org.linagora.linshare.core.notifications.context.GuestAccountResetPasswordEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.rac.GuestResourceAccessControl;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.ResetGuestPassword;
import org.linagora.linshare.mongo.entities.logs.GuestAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.UserMto;
import org.linagora.linshare.mongo.repository.ResetGuestPasswordMongoRepository;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class GuestServiceImpl extends GenericServiceImpl<Account, Guest>
		implements GuestService {

	private final GuestBusinessService guestBusinessService;

	private final AbstractDomainService abstractDomainService;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final UserService userService;

	private final NotifierService notifierService;

	private final MailBuildingService mailBuildingService;

	private final LogEntryService logEntryService;

	private final ContainerQuotaBusinessService containerQuotaBusinessService;

	private final AccountQuotaBusinessService accountQuotaBusinessService;

	protected final ResetGuestPasswordMongoRepository resetGuestPasswordMongoRepository;

	public GuestServiceImpl(final GuestBusinessService guestBusinessService,
			final AbstractDomainService abstractDomainService,
			final FunctionalityReadOnlyService functionalityReadOnlyService,
			final UserService userService,
			final NotifierService notifierService,
			final MailBuildingService mailBuildingService,
			final LogEntryService logEntryService,
			final GuestResourceAccessControl rac,
			final ContainerQuotaBusinessService containerQuotaBusinessService,
			final ResetGuestPasswordMongoRepository resetGuestPasswordMongoRepository,
			final AccountQuotaBusinessService accountQuotaBusinessService) {
		super(rac);
		this.guestBusinessService = guestBusinessService;
		this.abstractDomainService = abstractDomainService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.userService = userService;
		this.notifierService = notifierService;
		this.mailBuildingService = mailBuildingService;
		this.logEntryService = logEntryService;
		this.containerQuotaBusinessService = containerQuotaBusinessService;
		this.accountQuotaBusinessService = accountQuotaBusinessService;
		this.resetGuestPasswordMongoRepository = resetGuestPasswordMongoRepository;
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
	public Guest find(Account actor, Account owner, String domainUuid, String mail)
			throws BusinessException {
		AbstractDomain domain = null;
		if (Strings.isNullOrEmpty(domainUuid)) {
			domain= owner.getDomain();
		} else {
			domain = abstractDomainService.findGuestDomain(domainUuid);

		}
		return guestBusinessService.find(domain, mail);
	}

	@Override
	public List<Guest> findAll(Account actor, Account owner, Boolean mine)
			throws BusinessException {
		preChecks(actor, owner);
		checkListPermission(actor, owner, Guest.class,
				BusinessErrorCode.GUEST_FORBIDDEN, null);
		List<AbstractDomain> authorizedDomains = abstractDomainService.getAllAuthorizedDomains(owner.getDomain());
		List<Guest> list = null;
		if (mine == null) {
			list = guestBusinessService.findAll(authorizedDomains);
		} else if (mine) {
			list = guestBusinessService.findAllMyGuests(owner);
		} else {
			list = guestBusinessService.findAllOthersGuests(authorizedDomains, owner);
		}
		return list;
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
//			AKO : not empty already check if the object is null.
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
		AbstractDomain guestDomain = abstractDomainService.findGuestDomain(owner
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
		Guest create = guestBusinessService.create(owner, guest,
				guestDomain, restrictedContacts);
		createQuotaGuest(guest);
		ResetGuestPassword resetGuestPassword = new ResetGuestPassword(create);
		resetGuestPassword.setKind(ResetTokenKind.NEW_PASSWORD);
		resetGuestPasswordMongoRepository.insert(resetGuestPassword);
		GuestAccountNewCreationEmailContext mailContext = new GuestAccountNewCreationEmailContext((User)owner, create, resetGuestPassword.getUuid());
		MailContainerWithRecipient mail = mailBuildingService.build(mailContext);
		notifierService.sendNotification(mail);
		GuestAuditLogEntry log = new GuestAuditLogEntry(actor, owner, LogAction.CREATE, AuditLogEntryType.GUEST, guest);
		logEntryService.insert(log);
		return create;
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
		AbstractDomain guestDomain = abstractDomainService.findGuestDomain(owner
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
									owner.getDomain()).getActivationPolicy()
							.getStatus(), entity);
		} else {
			guest.setExpirationDate(entity.getExpirationDate());
		}
		Guest result = guestBusinessService.update(owner, entity, guest, guestDomain,
				restrictedContacts);
		log.setResourceUpdated(new UserMto(result));
		logEntryService.insert(log);
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
		guestBusinessService.delete(original);
		GuestAuditLogEntry log = new GuestAuditLogEntry(actor, owner, LogAction.DELETE, AuditLogEntryType.GUEST,
				original);
		logEntryService.insert(log);
		return original;
	}

	@Override
	public void deleteUser(SystemAccount systemAccount, String uuid) {
		Guest original = guestBusinessService.findByLsUuid(uuid);
		userService.deleteUser(systemAccount, uuid);
		GuestAuditLogEntry log = new GuestAuditLogEntry(systemAccount, systemAccount, LogAction.DELETE, AuditLogEntryType.GUEST,
				original);
		log.setCause(LogActionCause.EXPIRATION);
		logEntryService.insert(log);
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
	public void triggerResetPassword(String lsUuid) throws BusinessException {
		Validate.notEmpty(lsUuid);
		// TODO : create a log entry for this action
		Guest guest = retrieveGuest(lsUuid);
		ResetGuestPassword resetGuestPassword = resetGuestPasswordMongoRepository.insert(new ResetGuestPassword(guest));
		resetGuestPassword.setKind(ResetTokenKind.RESET_PASSWORD);
		GuestAccountResetPasswordEmailContext context = new GuestAccountResetPasswordEmailContext(guest, resetGuestPassword.getUuid());
		MailContainerWithRecipient mail = mailBuildingService.build(context);
		notifierService.sendNotification(mail);
	}

	@Override
	public void triggerResetPassword(SystemAccount actor, String email, String domainUuid) throws BusinessException {
		Validate.notEmpty(email);
		Guest guest = null;
		if (Strings.isNullOrEmpty(domainUuid)) {
			guest = guestBusinessService.findByMail(email);
		} else {
			AbstractDomain domain = abstractDomainService.findById(domainUuid);
			guest = guestBusinessService.find(domain, email);
		}
		if (guest == null) {
			throw new BusinessException(BusinessErrorCode.GUEST_NOT_FOUND,
					"Guest does not exist");
		}
		// TODO: find if there is already a valid token for this guest, and reuse it if not expired.
		ResetGuestPassword resetGuestPassword = new ResetGuestPassword(guest);
		resetGuestPassword.setKind(ResetTokenKind.RESET_PASSWORD);
		resetGuestPassword = resetGuestPasswordMongoRepository.insert(resetGuestPassword);
		GuestAccountResetPasswordEmailContext context = new GuestAccountResetPasswordEmailContext(guest, resetGuestPassword.getUuid());
		MailContainerWithRecipient mail = mailBuildingService.build(context);
		notifierService.sendNotification(mail);
	}

	@Override
	public List<Guest> search(Account actor, Account owner, String firstName, String lastName, String mail, boolean all)
			throws BusinessException {
		preChecks(actor, owner);
		if (owner.isGuest()) {
			throw new BusinessException(BusinessErrorCode.GUEST_FORBIDDEN, "Guests are not allowed to use this method.");
		}
		// TODO : check if one of the 3 parameters is not null/empty.
		List<AbstractDomain> authorizedDomains = abstractDomainService.getAllAuthorizedDomains(owner.getDomain());
		List<Guest> list = null;
		if (all) {
			list = guestBusinessService.search(authorizedDomains,  firstName, lastName, mail, null);
		} else {
			list = guestBusinessService.search(authorizedDomains,  firstName, lastName, mail, owner);
		}
		return list;
	}

	@Override
	public List<Guest> search(Account actor, Account owner, String pattern, Boolean mine) throws BusinessException {
		preChecks(actor, owner);
		if (owner.isGuest()) {
			throw new BusinessException(BusinessErrorCode.GUEST_FORBIDDEN, "Guests are not allowed to use this method.");
		}
		String message = "You must fill a pattern to search ! At least three characters.";
		Validate.notEmpty(pattern, message);
		if (pattern.length() < 3) {
			logger.error(message);
			throw new BusinessException(BusinessErrorCode.GUEST_INVALID_SEARCH_INPUT, message);
		}
		List<AbstractDomain> authorizedDomains = abstractDomainService.getAllAuthorizedDomains(owner.getDomain());
		List<Guest> list = null;
		if (mine == null) {
			list = guestBusinessService.search(authorizedDomains, pattern);
		} else if (mine) {
			list = guestBusinessService.searchMyGuests(authorizedDomains, pattern, owner);
		} else {
			list = guestBusinessService.searchExceptGuests(authorizedDomains, pattern, owner);
		}
		return list;
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
		return abstractDomainService.findGuestDomain(topDomainId) != null;
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

	@Override
	public SystemAccount getGuestSystemAccount() {
		return guestBusinessService.getGuestSystemAccount();
	}

	@Override
	public Guest resetPassword(Guest guest, String password) throws BusinessException {
		// TODO : Check password complexity.
		return guestBusinessService.resetPassword(guest, password);
	}

	private void createQuotaGuest(Guest guest) throws BusinessException {
		Validate.notNull(guest);
		Validate.notNull(guest.getDomain());
		ContainerQuota containerQuota = containerQuotaBusinessService.find(guest.getDomain(), ContainerQuotaType.USER);
		if (containerQuota == null) {
			throw new BusinessException(BusinessErrorCode.CONTAINER_QUOTA_NOT_FOUND, "No container quota found for the domain : " + guest.getDomainId());
		}
		AccountQuota userQuota = new AccountQuota(
				guest.getDomain(),
				guest.getDomain().getParentDomain(),
				guest, containerQuota);
		accountQuotaBusinessService.create(userQuota);
	}
}
