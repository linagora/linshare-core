/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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
package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.BooleanValueFunctionality;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.UserFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.InconsistentSearchDto;
import org.linagora.linshare.core.facade.webservice.common.dto.PasswordDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UserSearchDto;
import org.linagora.linshare.core.facade.webservice.user.dto.SecondFactorDto;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.InconsistentUserService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.QuotaService;
import org.linagora.linshare.core.service.UserProviderService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.core.service.UserService2;
import org.linagora.linshare.mongo.entities.logs.UserAuditLogEntry;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.linagora.linshare.webservice.utils.PageContainerAdaptor;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class UserFacadeImpl extends AdminGenericFacadeImpl implements
		UserFacade {

	final private static int AUTO_COMPLETE_LIMIT = 20;

	private final UserService userService;

	private final UserService2 userService2;

	private final GuestService guestService;

	protected final QuotaService quotaService;

	private final InconsistentUserService inconsistentUserService;

	private final AbstractDomainService abstractDomainService;

	private final DomainPermissionBusinessService domainPermissionBusinessService;

	private final LogEntryService logEntryService;

	protected final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final PageContainerAdaptor<User, UserDto> pageConverterAdaptor = new PageContainerAdaptor<>();

	public UserFacadeImpl(final AccountService accountService,
			final UserService userService,
			final InconsistentUserService inconsistentUserService,
			final GuestService guestService,
			final QuotaService quotaService,
			final AbstractDomainService abstractDomainService,
			final UserProviderService userProviderService,
			final DomainPermissionBusinessService domainPermissionBusinessService,
			final FunctionalityReadOnlyService functionalityReadOnlyService,
			final LogEntryService logEntryService,
			final UserService2 userService2) {
		super(accountService);
		this.userService = userService;
		this.inconsistentUserService = inconsistentUserService;
		this.guestService = guestService;
		this.quotaService = quotaService;
		this.abstractDomainService = abstractDomainService;
		this.domainPermissionBusinessService = domainPermissionBusinessService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.logEntryService = logEntryService;
		this.userService2 = userService2;
	}

	@Override
	public List<UserDto> search(UserSearchDto userSearchDto)
			throws BusinessException {
		checkAuthentication(Role.ADMIN);
		return searchUsers(userSearchDto.getFirstName(),
				userSearchDto.getLastName(), userSearchDto.getMail(), null);
	}

	@Override
	public Set<UserDto> searchInternals(String pattern)
			throws BusinessException {
		checkAuthentication(Role.ADMIN);
		return searchUsers(pattern, AccountType.INTERNAL);
	}

	@Override
	public Set<UserDto> searchGuests(String pattern) throws BusinessException {
		checkAuthentication(Role.ADMIN);
		return searchUsers(pattern, AccountType.GUEST);
	}

	/**
	 * Search users using firstname, lastname and mail as search criteria. Each
	 * param can be null. If all parameters are null, return all.
	 * 
	 * @param firstName
	 * @param lastName
	 * @param mail
	 * @param type
	 * @return
	 * @throws BusinessException
	 */
	private List<UserDto> searchUsers(String firstName, String lastName,
			String mail, AccountType type) throws BusinessException {
		User currentUser = super.checkAuthentication(Role.ADMIN);

		List<UserDto> usersDto = Lists.newArrayList();
		Set<User> users = new HashSet<User>();
		users.addAll(userService.searchUser(mail, firstName, lastName, type,
				currentUser));
		for (User user : users) {
			UserDto userDto = UserDto.getFull(user);

			if (user.isGuest() && user.isRestricted()) {
				Guest guest = guestService.find(currentUser, currentUser,
						user.getLsUuid());
				Set<AllowedContact> contacts = guest.getRestrictedContacts();
				for (AllowedContact contact : contacts) {
					userDto.getRestrictedContacts().add(
							UserDto.getSimple(contact.getContact()));
				}
			}
			usersDto.add(userDto);
		}
		return usersDto;
	}

	private Set<UserDto> searchUsers(String pattern, AccountType type)
			throws BusinessException {
		Set<UserDto> usersDto = new HashSet<UserDto>();
		usersDto.addAll(searchUsers(pattern, null, null, type));
		usersDto.addAll(searchUsers(null, pattern, null, type));
		usersDto.addAll(searchUsers(null, null, pattern, type));
		return usersDto;
	}

	@Override
	public UserDto update(UserDto userDto, Integer version) throws BusinessException {
		Validate.notNull(userDto, "user must be set.");
		Validate.notEmpty(userDto.getUuid(), "uuid must be set.");
		Validate.notNull(userDto.getLocale(), "locale must be set.");
		User authUser = checkAuthentication(Role.ADMIN);
		User entity = userService.findByLsUuid(userDto.getUuid());
		if (entity == null) {
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND, "Can not find user");
		}
		User userToUpdate = userDto.toUserObject(entity.isGuest());
		if (version >= 4) {
			Validate.notNull(userDto.isLocked(), "isLocked parameter should be set");
			if (!userDto.isLocked() && entity.isLocked()) {
				entity = userService.unlockUser(authUser, entity);
			}
		}
		User update;
		if (entity.isGuest()) {
			List<String> ac = null;
			if (userDto.isRestricted()) {
				ac = Lists.newArrayList();
				for (UserDto contactDto : userDto.getRestrictedContacts()) {
					ac.add(contactDto.getMail());
				}
			}
			update = guestService.update(authUser, (User) entity.getOwner(),
					(Guest) userToUpdate, ac);
		} else {
			update = userService.updateUser(authUser, userToUpdate,
					userDto.getDomain());
		}
		UserDto updatedDto = UserDto.getFull(update);
		if (version >= 4) {
			updatedDto.setLocked(update.isLocked());
		}
		return updatedDto;
	}

	@Override
	public UserDto delete(UserDto userDto) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		String uuid = userDto.getUuid();
		Validate.notEmpty(uuid, "user unique identifier must be set.");
		User user = userService.deleteUser(authUser, uuid);
		return UserDto.getFull(user);
	}
	
	@Override
	public SecondFactorDto delete2FA(String userUuid, String secondFactorUuid, SecondFactorDto dto) throws BusinessException {
		Account authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(userUuid, "user uuid must be set");
		if (Strings.isNullOrEmpty(secondFactorUuid)) {
			Validate.notNull(dto, "missing SecondFactorDto");
			Validate.notEmpty(dto.getUuid(), "Missing second factor key uuid");
			secondFactorUuid = dto.getUuid();
		}
		checkSecondFactorUuid(userUuid, secondFactorUuid);
		Account user = userService.findByLsUuid(userUuid);
		checkAdminPermission(authUser, user);
		user.setSecondFACreationDate(null);
		user.setSecondFASecret(null);
		user = accountService.update(user);
		UserAuditLogEntry userAuditLogEntry = new UserAuditLogEntry(authUser, user, LogAction.UPDATE,
				AuditLogEntryType.USER, (User) user);
		userAuditLogEntry.setCause(LogActionCause.SECOND_FACTOR_SHARED_KEY_DELETE);
		logEntryService.insert(userAuditLogEntry);
		return new SecondFactorDto(user.getLsUuid(), user.getSecondFACreationDate(), user.isUsing2FA());
	}

	private void checkSecondFactorUuid(String userUuid, String secondFactorUuid) {
		// For now, second factor uuid must equal user uuid.
		if (!userUuid.equals(secondFactorUuid)) {
			String message = "Second factor key uuid must be the same as user uuid.";
			logger.error(message);
			throw new BusinessException(message);
		}
	}

	private void checkAdminPermission(Account actor, Account user) {
		if (!domainPermissionBusinessService.isAdminforThisDomain(actor, user.getDomain())) {
			logger.error("Not allowed to perform this action, You are not an admin for domain {}",
					user.getDomainId());
			throw new BusinessException(BusinessErrorCode.AUTHENTICATION_SECOND_FACTOR_FORBIDEN,
					"Not allowed to perform this action");
		}
	}

	@Override
	public SecondFactorDto find2FA(String userUuid, String secondFactorUuid) throws BusinessException {
		Account authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(userUuid, "user uuid must be set");
		Validate.notEmpty(secondFactorUuid, "Second Factor uuid must be set");
		checkSecondFactorUuid(userUuid, secondFactorUuid);
		Account user = userService.findByLsUuid(userUuid);
		checkAdminPermission(authUser, user);
		return new SecondFactorDto(user.getLsUuid(), user.getSecondFACreationDate(), user.isUsing2FA());
	}

	@Override
	public Set<UserDto> findAllInconsistent() throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Set<UserDto> ret = Sets.newHashSet();

		for (User user : inconsistentUserService.findAllInconsistent(authUser)) {
			ret.add(UserDto.getFull(user));
		}
		return ret;
	}

	@Override
	public void updateInconsistent(UserDto userDto) throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		update(userDto, 1);
		inconsistentUserService.updateDomain(authUser, userDto.getUuid(), userDto.getDomain());
	}

	@Override
	public List<InconsistentSearchDto> checkInconsistentUserStatus(UserSearchDto searchDto) {
		Validate.notNull(searchDto, "searchDto must not be null");
		String mail = searchDto.getMail();
		Validate.notEmpty(mail, "User mail must not be empty");
		checkAuthentication(Role.SUPERADMIN);
		List<InconsistentSearchDto> res = Lists.newArrayList();
		for (AbstractDomain domain : abstractDomainService.getAllDomains()) {
			User user = userService.findUserInDB(domain.getUuid(), mail);
			if (user != null) {
				if (!(user.isGuest() || user.isInternal())) {
					// we exclude technical users and root
					continue;
				}
				InconsistentSearchDto dto = new InconsistentSearchDto(domain, mail);
				dto.setDatabase(true);
				dto.setGuest(user.isGuest());
				dto.setUuid(user.getLsUuid());
				if (user.isInternal()) {
					dto.setLdap(abstractDomainService.isUserExist(domain, mail));
					if (dto.isLdap()) {
						if (user.isInconsistent()) {
							user.setInconsistent(false);
							accountService.update(user);
						}
					} else {
						if (!user.isInconsistent()) {
							user.setInconsistent(true);
							accountService.update(user);
						}
					}
				}
				res.add(dto);
			} else {
				if (abstractDomainService.isUserExist(domain, mail)) {
					InconsistentSearchDto dto = new InconsistentSearchDto(domain, mail);
					dto.setLdap(true);
					res.add(dto);
				}
			}
		}
		return res;
	}

	@Override
	public List<String> autocompleteInconsistent(UserSearchDto dto)
			throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Set<String> res = Sets.newHashSet();
		List<User> internals = abstractDomainService
				.autoCompleteUserWithoutDomainPolicies(authUser, dto.getMail());
		for (User user : internals) {
			res.add(user.getMail());
		}
		res.addAll(accountService.findAllKnownEmails(authUser, dto.getMail()));
		int range = (res.size() < AUTO_COMPLETE_LIMIT ? res.size() : AUTO_COMPLETE_LIMIT);
		return Lists.newArrayList(res).subList(0, range);
	}

	@Override
	public void changePassword(PasswordDto password) throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		User actor = getActor(authUser, null);
		userService.changePassword(authUser, actor,
				password.getOldPwd(), password.getNewPwd());
	}

	@Override
	public UserDto findUser(String uuid, Integer version) throws BusinessException {
		User currentUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(uuid, "User uuid must be set.");
		UserDto userDto = null;
		User user = userService.findByLsUuid(uuid);
		if (user.isGuest() && user.isRestricted()) {
			Guest guest = guestService.find(currentUser, currentUser,
					uuid);
			userDto =  UserDto.getFull(guest);
		} else {
			userDto = UserDto.getFull(user);
		}
		// get the quota for the current user.
		AccountQuota quota = quotaService.findByRelatedAccount(user);
		userDto.setQuotaUuid(quota.getUuid());
		if (version >= 4) {
			userDto.setLocked(user.isLocked());
			BooleanValueFunctionality twofaFunc = functionalityReadOnlyService.getSecondFactorAuthenticationFunctionality(user.getDomain());
			if (twofaFunc.getActivationPolicy().getStatus()) {
				userDto.setSecondFAUuid(user.getLsUuid());
				userDto.setSecondFAEnabled(user.isUsing2FA());
				userDto.setSecondFARequired(twofaFunc.getValue());
			} else {
				userDto.setSecondFAEnabled(false);
				userDto.setSecondFARequired(false);
			}
		}
		return userDto;
	}

	@Override
	public boolean exist(String uuid) throws BusinessException {
		checkAuthentication(Role.ADMIN);
		Validate.notEmpty(uuid, "User uuid must be set.");
		return userService.exist(uuid);
	}

	@Override
	public UserDto create(UserDto userDto) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notNull(userDto, "User dto must be set.");
		String uuid = userDto.getUuid();
		if (uuid != null) {
			if (userService.exist(uuid)) {
				return UserDto.getFull(userService.findByLsUuid(uuid));
			}
		}
		String mail = userDto.getMail();
		String domain = userDto.getDomain();
		Validate.notEmpty(mail, "User mail must be set.");
		Validate.notEmpty(domain, "User domain identifier must be set.");
		User user = userService.findOrCreateUserWithDomainPolicies(domain, mail, authUser.getDomainId());
		return UserDto.getFull(user);
	}

	@Override
	public boolean updateEmail(String currentEmail, String newEmail) {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		logger.info("Start email migration...");
		logger.info("Step 1: Find and update user's email ...");
		boolean hasBeenUpdated = userService.updateUserEmail(authUser, currentEmail, newEmail);

		if(hasBeenUpdated) {
			logger.info("Step 2: start updateMailingListEmail ...");
			userService.updateMailingListEmail(authUser, currentEmail, newEmail);
			logger.info("Step 3: start updateRecipientFavourite ...");
			userService.updateRecipientFavourite(authUser, currentEmail, newEmail);
		}
		logger.info("End of email migration...");
		return hasBeenUpdated;
	}

	@Override
	public UserDto isAuthorized(Role role, Integer version) throws BusinessException {
		User authUser = checkAuthentication(role);
		UserDto dto = UserDto.getFull(authUser);
		if (version >= 4) {
			BooleanValueFunctionality twofaFunc = functionalityReadOnlyService.getSecondFactorAuthenticationFunctionality(authUser.getDomain());
			if (twofaFunc.getActivationPolicy().getStatus()) {
				dto.setSecondFAUuid(authUser.getLsUuid());
				dto.setSecondFAEnabled(authUser.isUsing2FA());
				dto.setSecondFARequired(twofaFunc.getValue());
			} else {
				dto.setSecondFAUuid(null);
				dto.setSecondFAEnabled(false);
				dto.setSecondFARequired(false);
			}
		}
		return dto;
	}

	@Override
	public PageContainer<UserDto> findAll(String actorUuid, String domainUuid, String creationDate,
			String modificationDate, String mail, String firstName, String lastName, Boolean restricted,
			Boolean canCreateGuest, Boolean canUpload, String role, String type, Integer pageNumber, Integer pageSize) {
		User authUser = checkAuthentication(Role.ADMIN);
		User actor = getActor(authUser, actorUuid);
		PageContainer<User> container = new PageContainer<>(pageNumber, pageSize);
		AbstractDomain domain = null;
		if (!Strings.isNullOrEmpty(domainUuid)) {
			domain = abstractDomainService.findById(domainUuid);
		}
		container = userService2.findAll(authUser, actor, domain, creationDate, modificationDate, mail, firstName,
				lastName, restricted, canCreateGuest, canUpload, role, type, container);
		PageContainer<UserDto> dto = pageConverterAdaptor.convert(container, UserDto.toDto());
		return dto;
	}
}
