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

package org.linagora.linshare.core.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.ContainerQuotaBusinessService;
import org.linagora.linshare.core.business.service.GuestBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
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
import org.linagora.linshare.core.domain.entities.Moderator;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ModeratorRole;
import org.linagora.linshare.core.notifications.context.GuestAccountNewCreationEmailContext;
import org.linagora.linshare.core.notifications.context.GuestAccountResetPasswordEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.rac.GuestResourceAccessControl;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.ModeratorService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.ResetGuestPassword;
import org.linagora.linshare.mongo.entities.logs.GuestAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.UserAuditLogEntry;
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

	protected final ModeratorService moderatorService;

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
			final AccountQuotaBusinessService accountQuotaBusinessService,
			final SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
			final ModeratorService moderatorService) {
		super(rac, sanitizerInputHtmlBusinessService);
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
		this.moderatorService = moderatorService;
	}

	@Override
	public Guest find(Account authUser, Account actor, String lsUuid)
			throws BusinessException {
		preChecks(authUser, actor);
		Guest guest = guestBusinessService.findByLsUuid(lsUuid);
		if (guest == null) {
			logger.error("Current actor " + actor.getAccountRepresentation()
					+ " is looking for a misssing guest : " + lsUuid);
			String message = "Can not find guest with uuid : " + lsUuid;
			throw new BusinessException(BusinessErrorCode.GUEST_NOT_FOUND,
					message);
		}
		checkReadPermission(authUser, actor, Guest.class,
				BusinessErrorCode.GUEST_FORBIDDEN, guest);
		return guest;
	}

	@Override
	public List<AllowedContact> load(Account authUser, User guest)
			throws BusinessException {
		preChecks(authUser, guest);
		Guest guest2 = guestBusinessService.findByLsUuid(guest.getLsUuid());
		checkReadPermission(authUser, authUser, Guest.class,
				BusinessErrorCode.GUEST_FORBIDDEN, guest2);
		return guestBusinessService.loadAllowedContacts(guest);
	}

	@Override
	public Guest find(Account authUser, Account actor, String domainUuid, String mail)
			throws BusinessException {
		AbstractDomain domain = null;
		if (Strings.isNullOrEmpty(domainUuid)) {
			domain= actor.getDomain();
		} else {
			domain = abstractDomainService.findGuestDomain(domainUuid);

		}
		return guestBusinessService.find(domain, mail);
	}

	@Override
	public List<Guest> findAll(Account authUser, Account actor, Boolean mine)
			throws BusinessException {
		preChecks(authUser, actor);
		checkListPermission(authUser, actor, Guest.class,
				BusinessErrorCode.GUEST_FORBIDDEN, null);
		List<AbstractDomain> authorizedDomains = abstractDomainService.getAllAuthorizedDomains(actor.getDomain());
		List<Guest> list = null;
		if (mine == null) {
			list = guestBusinessService.findAll(authorizedDomains);
		} else if (mine) {
			list = guestBusinessService.findAllMyGuests(actor);
		} else {
			list = guestBusinessService.findAllOthersGuests(authorizedDomains, actor);
		}
		return list;
	}

	@Override
	public boolean exist(String lsUuid) throws BusinessException {
		return guestBusinessService.findByLsUuid(lsUuid) != null;
	}

	@Override
	public Guest create(Account authUser, Account actor, Guest guest,
			List<String> restrictedMails) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notNull(guest);
		Validate.notEmpty(guest.getMail(), "Guest mail must be set.");
		checkCreatePermission(authUser, actor, Guest.class,
				BusinessErrorCode.USER_CANNOT_CREATE_GUEST, null);
		if (!hasGuestDomain(actor.getDomainId())) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_DO_NOT_EXIST,
					"Guest domain was not found");
		}
		Date expiryDate = calculateGuestExpiryDate(actor, guest.getExpirationDate());
		guest.setExpirationDate(expiryDate);
		AbstractDomain guestDomain = abstractDomainService.findGuestDomain(actor
				.getDomainId());
		User user = null;
		try {
			user = userService.findOrCreateUser(guest.getMail(), actor.getDomainId());
		} catch (BusinessException e) {
			logger.error("User is null");
		}
		if (user != null) {
			throw new BusinessException(BusinessErrorCode.GUEST_ALREADY_EXISTS,
					"Can not create an internal user as guest");
		}
		if (!guestBusinessService.exist(guestDomain.getUuid(),
				guest.getMail())) {
			throw new BusinessException(BusinessErrorCode.GUEST_ALREADY_EXISTS,
					"Pair mail/domain already exist");
		}
		guest.setRole(Role.SIMPLE);
		guest.setFirstName(sanitize(guest.getFirstName()));
		guest.setLastName(sanitize(guest.getLastName()));
		List<User> restrictedContacts = null;
		if (guest.isRestricted()) {
			restrictedContacts = transformToUsers(authUser, restrictedMails);
			if (restrictedContacts == null || restrictedContacts.isEmpty()) {
				throw new BusinessException(
						BusinessErrorCode.GUEST_INVALID_INPUT,
						"Can not create a restricted guest without restricted contacts (internal or guest users only).");
			}
		}
		Guest create = guestBusinessService.create(actor, guest,
				guestDomain, restrictedContacts);
		createQuotaGuest(guest);
		ResetGuestPassword resetGuestPassword = new ResetGuestPassword(create);
		resetGuestPassword.setKind(ResetTokenKind.NEW_PASSWORD);
		resetGuestPasswordMongoRepository.insert(resetGuestPassword);
		GuestAccountNewCreationEmailContext mailContext = new GuestAccountNewCreationEmailContext((User)actor, create, resetGuestPassword.getUuid());
		MailContainerWithRecipient mail = mailBuildingService.build(mailContext);
		notifierService.sendNotification(mail);
		moderatorService.create(authUser, actor, new Moderator(org.linagora.linshare.core.domain.constants.ModeratorRole.ADMIN, actor, create));
		GuestAuditLogEntry log = new GuestAuditLogEntry(authUser, actor, LogAction.CREATE, AuditLogEntryType.GUEST, guest);
		logEntryService.insert(log);
		return create;
	}

	@Override
	public Guest update(Account authUser, User actor, Guest guest,
			List<String> restrictedMails) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notNull(guest, "Guest object is required");
		Validate.notEmpty(guest.getLsUuid(), "Guest uuid is required");
		// In case if guestDto was an existing modified entity.
		guestBusinessService.evict(guest);
		Guest entity = find(authUser, actor, guest.getLsUuid());
		GuestAuditLogEntry log = new GuestAuditLogEntry(authUser, actor, LogAction.UPDATE, AuditLogEntryType.GUEST,
				entity);
		checkUpdatePermission(authUser, actor, Guest.class,
				BusinessErrorCode.CANNOT_UPDATE_USER, entity);
		AbstractDomain guestDomain = abstractDomainService.findGuestDomain(actor
				.getDomainId());
		if (guestDomain == null) {
			throw new BusinessException(
					BusinessErrorCode.USER_CANNOT_CREATE_GUEST,
					"New owner doesn't have guest domain");
		}
		List<User> restrictedContacts = transformToUsers(authUser, restrictedMails);
		Date newExpirationDate = guest.getExpirationDate(); 
		if (newExpirationDate != null && !newExpirationDate.before(new Date())) {
			if (!userService.isAdminForThisUser(authUser, actor)) {
				newExpirationDate = calculateGuestExpiryDate(actor, newExpirationDate);
				checkDateValidity(actor, entity.getExpirationDate(), newExpirationDate,
						functionalityReadOnlyService.getGuestsExpirationDateProlongation(actor.getDomain())
								.getActivationPolicy().getStatus(),
						entity);
			}
			guest.setExpirationDate(newExpirationDate);
		} else {
			guest.setExpirationDate(entity.getExpirationDate());
		}
		guest.setFirstName(sanitize(guest.getFirstName()));
		guest.setLastName(sanitize(guest.getLastName()));
		Guest result = guestBusinessService.update(actor, entity, guest, guestDomain,
				restrictedContacts);
		log.setResourceUpdated(new UserMto(result));
		logEntryService.insert(log);
		return result;
	}

	private void checkDateValidity(User owner, Date oldExpiryDate,
			Date dateToCheck, Boolean prolongation, Guest guest) {
		if (oldExpiryDate != null) {
			Date date;
			if (prolongation) {
				date = dateToCheck;
			} else {
				TimeUnitValueFunctionality func = functionalityReadOnlyService
						.getGuestsExpiration(owner.getDomain());
				Calendar expiryDate = Calendar.getInstance();
				expiryDate.setTime(guest.getCreationDate());
				expiryDate.add(Calendar.MONTH, func.getMaxValue());
				date = expiryDate.getTime();
			}
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
					User user = userService.findOrCreateUserWithDomainPolicies(mail, actor.getDomainId());
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
	public Guest delete(Account authUser, User actor, String lsUuid)
			throws BusinessException {
		preChecks(authUser, actor);
		Validate.notEmpty(lsUuid);
		Guest original = find(authUser, actor, lsUuid);
		checkDeletePermission(authUser, actor, Guest.class,
				BusinessErrorCode.CANNOT_DELETE_USER, original);
		List<Moderator> moderators = moderatorService.findAllByGuest(authUser, actor, original.getLsUuid(), null, null);
		if (!moderators.isEmpty()) {
			moderatorService.deleteAllModerators(authUser, actor, moderators, original);
		}
		userService.deleteUser(authUser, original.getLsUuid());
		GuestAuditLogEntry log = new GuestAuditLogEntry(authUser, actor, LogAction.DELETE, AuditLogEntryType.GUEST,
				original);
		logEntryService.insert(log);
		return original;
	}

	@Override
	public void deleteUser(SystemAccount systemAccount, String uuid) {
		Guest original = guestBusinessService.findByLsUuid(uuid);
		userService.deleteUser(systemAccount, original.getLsUuid());
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
		List<ResetGuestPassword> tokensNotUsed = resetGuestPasswordMongoRepository.findByGuestNotUsed(guest.getLsUuid(), new Date());
		if (!tokensNotUsed.isEmpty()) {
			tokensNotUsed.forEach(t -> {
				t.setAlreadyUsed(true);
				resetGuestPasswordMongoRepository.save(t);
			});
		}
		ResetGuestPassword resetGuestPassword = resetGuestPasswordMongoRepository.insert(new ResetGuestPassword(guest));
		resetGuestPassword.setKind(ResetTokenKind.RESET_PASSWORD);
		UserAuditLogEntry userAuditLogEntry = new UserAuditLogEntry(guest, guest, LogAction.CREATE,
				AuditLogEntryType.RESET_PASSWORD, guest);
		logEntryService.insert(userAuditLogEntry);
		GuestAccountResetPasswordEmailContext context = new GuestAccountResetPasswordEmailContext(guest,
				resetGuestPassword.getUuid());
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
			throw new BusinessException(BusinessErrorCode.GUEST_NOT_FOUND, "Guest does not exist");
		}
		// TODO: find if there is already a valid token for this guest, and
		// reuse it if not expired.
		List<ResetGuestPassword> tokensNotUsed = resetGuestPasswordMongoRepository.findByGuestNotUsed(guest.getLsUuid(), new Date());
		if (!tokensNotUsed.isEmpty()) {
			tokensNotUsed.forEach(t -> {
				t.setAlreadyUsed(true);
				resetGuestPasswordMongoRepository.save(t);
			});
		}
		ResetGuestPassword resetGuestPassword = new ResetGuestPassword(guest);
		resetGuestPassword.setKind(ResetTokenKind.RESET_PASSWORD);
		resetGuestPassword = resetGuestPasswordMongoRepository.insert(resetGuestPassword);
		UserAuditLogEntry userAuditLogEntry = new UserAuditLogEntry(guest, guest, LogAction.CREATE,
				AuditLogEntryType.RESET_PASSWORD, guest);
		logEntryService.insert(userAuditLogEntry);
		GuestAccountResetPasswordEmailContext context = new GuestAccountResetPasswordEmailContext(guest,
				resetGuestPassword.getUuid());
		MailContainerWithRecipient mail = mailBuildingService.build(context);
		notifierService.sendNotification(mail);
	}

	@Override
	public List<Guest> search(Account authUser, Account actor, String firstName, String lastName, String mail, boolean all)
			throws BusinessException {
		preChecks(authUser, actor);
		if (actor.isGuest()) {
			throw new BusinessException(BusinessErrorCode.GUEST_FORBIDDEN, "Guests are not allowed to use this method.");
		}
		// TODO : check if one of the 3 parameters is not null/empty.
		List<AbstractDomain> authorizedDomains = abstractDomainService.getAllAuthorizedDomains(actor.getDomain());
		List<Guest> list = null;
		if (all) {
			list = guestBusinessService.search(authorizedDomains,  firstName, lastName, mail, null);
		} else {
			list = guestBusinessService.search(authorizedDomains,  firstName, lastName, mail, actor);
		}
		return list;
	}

	@Override
	public List<Guest> search(Account authUser, Account actor, String pattern, Boolean mine) throws BusinessException {
		preChecks(authUser, actor);
		if (actor.isGuest()) {
			throw new BusinessException(BusinessErrorCode.GUEST_FORBIDDEN, "Guests are not allowed to use this method.");
		}
		String message = "You must fill a pattern to search ! At least three characters.";
		Validate.notEmpty(pattern, message);
		if (pattern.length() < 3) {
			logger.error(message);
			throw new BusinessException(BusinessErrorCode.GUEST_INVALID_SEARCH_INPUT, message);
		}
		List<AbstractDomain> authorizedDomains = abstractDomainService.getAllAuthorizedDomains(actor.getDomain());
		List<Guest> list = null;
		if (mine == null) {
			list = guestBusinessService.search(authorizedDomains, pattern);
		} else if (mine) {
			list = guestBusinessService.searchMyGuests(authorizedDomains, pattern, actor);
		} else {
			list = guestBusinessService.searchExceptGuests(authorizedDomains, pattern, actor);
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

	private Date calculateGuestExpiryDate(Account authUser, Date currentGuestExpirationDate) {
			TimeUnitValueFunctionality func = functionalityReadOnlyService
					.getGuestsExpiration(authUser.getDomain());
			return functionalityReadOnlyService.getDateValue(func, currentGuestExpirationDate, BusinessErrorCode.GUEST_EXPIRY_DATE_INVALID);
	}

	@Override
	public Date getGuestExpirationDate(Account authUser,
			Date currentGuestExpirationDate) throws BusinessException {
		return calculateGuestExpiryDate(authUser, currentGuestExpirationDate);
	}

	@Override
	public SystemAccount getGuestSystemAccount() {
		return guestBusinessService.getGuestSystemAccount();
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
		userQuota.setDomainShared(containerQuota.getDomainQuota().getDomainShared());
		userQuota.setDomainSharedOverride(false);
		accountQuotaBusinessService.create(userQuota);
	}

	@Override
	public List<Guest> findAll(User authUser, User actor, String pattern, ModeratorRole moderatorRole) {
		preChecks(authUser, actor);
		checkListPermission(authUser, actor, Guest.class, BusinessErrorCode.GUEST_FORBIDDEN, null);
		List<Guest> guests = Lists.newArrayList();
		List<AbstractDomain> authorizedDomains = abstractDomainService.getAllAuthorizedDomains(actor.getDomain());
		if (Strings.isNullOrEmpty(pattern)) {
			guests = guestBusinessService.findAll(actor, authorizedDomains, moderatorRole);
		} else {
			guests = guestBusinessService.search(actor, authorizedDomains, pattern, moderatorRole);
		}
		return guests;
	}
}
