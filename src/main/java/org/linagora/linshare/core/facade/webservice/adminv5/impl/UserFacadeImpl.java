/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.facade.webservice.adminv5.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.BooleanValueFunctionality;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.domain.entities.fields.UserFields;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.UtilGuestAuthor;
import org.linagora.linshare.core.facade.webservice.admin.impl.AdminGenericFacadeImpl;
import org.linagora.linshare.core.facade.webservice.adminv5.UserFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.RestrictedContactDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.UserDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.UserDtoQuotaDto;
import org.linagora.linshare.core.facade.webservice.common.dto.GuestDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ModeratorRoleEnum;
import org.linagora.linshare.core.facade.webservice.common.dto.PasswordDto;
import org.linagora.linshare.core.facade.webservice.user.dto.SecondFactorDto;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountQuotaService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.QuotaService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.core.service.UserService2;
import org.linagora.linshare.mongo.entities.logs.UserAuditLogEntry;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.linagora.linshare.webservice.utils.PageContainerAdaptor;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class UserFacadeImpl extends AdminGenericFacadeImpl implements UserFacade {

	private final UserService2 userService2;

	private final UserService userService;

	private final GuestService guestService;

	private final QuotaService quotaService;

	private final AbstractDomainService abstractDomainService;

	private final PageContainerAdaptor<User, UserDto> pageContainerAdaptor = new PageContainerAdaptor<>();

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final AccountQuotaService accountQuotaService;

	private final DomainPermissionBusinessService domainPermissionBusinessService;

	private final LogEntryService logEntryService;


	private final UtilGuestAuthor utilGuestAuthor;

	public UserFacadeImpl(
			AccountService accountService,
			UserService2 userService2,
			AbstractDomainService abstractDomainService,
			GuestService guestService,
			QuotaService quotaService,
			UserService userService,
			FunctionalityReadOnlyService functionalityReadOnlyService,
			AccountQuotaService accountQuotaService, 
			DomainPermissionBusinessService domainPermissionBusinessService,
			MongoTemplate mongoTemplate,
			LogEntryService logEntryService) {
		super(accountService);
		this.userService2 = userService2;
		this.abstractDomainService = abstractDomainService;
		this.guestService = guestService;
		this.quotaService = quotaService;
		this.userService = userService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.accountQuotaService = accountQuotaService;
		this.domainPermissionBusinessService = domainPermissionBusinessService;
		this.logEntryService = logEntryService;
		this.utilGuestAuthor = new UtilGuestAuthor(mongoTemplate);
	}

	@Override
	public PageContainer<UserDto> findAll(String actorUuid, List<String> domainsUuids, SortOrder sortOrder,
			UserFields sortField, String mail, String firstName, String lastName, Boolean restricted,
			Boolean canCreateGuest, Boolean canUpload, String role, String type, String moderatorRole, Optional<Integer> greaterThan, Optional<Integer> lowerThan,
		    Integer pageNumber, Integer pageSize) {
		User authUser = checkAuthentication(Role.ADMIN);
		User actor = getActor(authUser, actorUuid);
		PageContainer<User> container = new PageContainer<>(pageNumber, pageSize);
		container = userService2.findAll(authUser, actor, domainsUuids, sortOrder, sortField, mail, firstName, lastName,
				restricted, canCreateGuest, canUpload, role, type, moderatorRole, greaterThan, lowerThan, container);
		PageContainer<UserDto> dto = pageContainerAdaptor.convert(container, UserDto.toDto());
		return dto;
	}

	@Override
	public UserDto find(String actorUuid, String uuid) {
		User authUser = checkAuthentication(Role.ADMIN);
		User actor = getActor(authUser, actorUuid);
		Validate.notEmpty(uuid, "User uuid must be set.");
		User user = userService2.find(authUser, actor, uuid);
		UserDto userDto = toDtoV5(user);
		AccountQuota quota = quotaService.findByRelatedAccount(user);
		userDto.setQuotaUuid(quota.getUuid());
		return userDto;
	}

	@Override
	public UserDto update(String actorUuid, UserDto userDto, String uuid) throws BusinessException {
		Account authUser = checkAuthentication(Role.ADMIN);
		Account actor = getActor(authUser, actorUuid);
		if (!Strings.isNullOrEmpty(uuid)) {
			userDto.setUuid(uuid);
		}
		Validate.notEmpty(userDto.getUuid(), "user uuid must be set");
		Validate.notNull(userDto.isLocked(), "isLocked parameter should be set");
		Validate.notNull(userDto.getDomain(), "domain parameter should be set");
		User entity = userService2.find(authUser, actor, userDto.getUuid());
		User userToUpdate = userDto.toUserObject(entity.isGuest());
		if (!userDto.isLocked() && entity.isLocked()) {
			userService2.unlock(authUser, actor, entity);
		}
		entity = userService2.update(authUser, actor, userToUpdate, userDto.getDomain().getUuid());
		return toDtoV5(entity);
	}

	@Override
	public UserDto delete(String actorUuid, UserDto userDto, String uuid) throws BusinessException {
		Account authUser = checkAuthentication(Role.ADMIN);
		User actor = getActor(authUser, actorUuid);
		if (Strings.isNullOrEmpty(uuid)) {
			Validate.notNull(userDto, "missing user to delete");
			Validate.notEmpty(userDto.getUuid(), "Missing user's uuid");
			uuid = userDto.getUuid();
		}
		Validate.notEmpty(uuid, "user uuid must be set");
		User userToDelete = userService2.find(authUser, actor, uuid);
		if (userToDelete.isGuest()) {
			userToDelete = guestService.delete(authUser, actor, userToDelete.getLsUuid());
		} else {
			userToDelete = userService2.delete(authUser, actor, userToDelete.getLsUuid());
		}
		return toDtoV5(userToDelete);
	}

	@Override
	public List<RestrictedContactDto> findAllRestrictedContacts(String actorUuid, String userUuid, String mail,
			String firstName, String lastName) {
		Account authUser = checkAuthentication(Role.ADMIN);
		Account actor = getActor(authUser, actorUuid);
		Validate.notNull(userUuid);
		User user = userService2.find(authUser, actor, userUuid);
		List<AllowedContact> allowedContacts = userService2.findAllRestrictedContacts(authUser, actor, user, mail,
				firstName, lastName);
		return ImmutableList.copyOf(Lists.transform(allowedContacts, RestrictedContactDto.toDto()));
	}

	@Override
	public RestrictedContactDto findRestrictedContact(String actorUuid, String ownerUuid,
			String restrictedContactUuid) {
		Account authUser = checkAuthentication(Role.ADMIN);
		Account actor = getActor(authUser, actorUuid);
		User owner = userService2.find(authUser, actor, ownerUuid);
		return new RestrictedContactDto(
				userService2.findRestrictedContact(authUser, actor, owner, restrictedContactUuid));
	}

	@Override
	public RestrictedContactDto createRestrictedContact(String actorUuid, String ownerUuid,
			RestrictedContactDto restrictedContactDto) {
		Account authUser = checkAuthentication(Role.ADMIN);
		Account actor = getActor(authUser, actorUuid);
		Validate.notEmpty(ownerUuid, "The owner's uuid of the restricted contact must be set");
		User owner = userService2.find(authUser, actor, ownerUuid);
		User user = null;
		if (!Strings.isNullOrEmpty(restrictedContactDto.getUuid())) {
			user = userService2.find(authUser, actor, restrictedContactDto.getUuid());
		} else {
			Validate.notEmpty(restrictedContactDto.getMail(), "RestrictedContact's mail must be set");
			Validate.notNull(restrictedContactDto.getDomain(), "The restrictedContact's domain must be set");
			AbstractDomain domain = abstractDomainService.findById(restrictedContactDto.getDomain().getUuid());
			user = userService.findOrCreateUser(restrictedContactDto.getMail(), domain.getUuid());
		}
		AllowedContact restrictedContactToCreate = new AllowedContact(owner, user);
		return new RestrictedContactDto(
				userService2.createRestrictedContact(authUser, actor, restrictedContactToCreate));
	}

	@Override
	public RestrictedContactDto deleteRestrictedContact(String actorUuid, String ownerUuid, RestrictedContactDto restrictedContactDto,
			String restrictedContactUuid) {
		Account authUser = checkAuthentication(Role.ADMIN);
		Account actor = getActor(authUser, actorUuid);
		Validate.notNull(ownerUuid, "The owner's uuid of the restricted contact must be set");
		User owner = userService2.find(authUser, actor, ownerUuid);
		if (Strings.isNullOrEmpty(restrictedContactUuid)) {
			Validate.notNull(restrictedContactDto, "Restricted contact must be set");
			Validate.notEmpty(restrictedContactDto.getUuid(), "Restricted contact uuid must be set");
			restrictedContactUuid = restrictedContactDto.getUuid();
		}
		return new RestrictedContactDto(
				userService2.deleteRestrictedContact(authUser, actor, owner, restrictedContactUuid));
	}

	@Override
	public UserDto isAuthorized() throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		UserDto dto = toDtoV5(authUser);
		BooleanValueFunctionality twofaFunc = functionalityReadOnlyService
				.getSecondFactorAuthenticationFunctionality(authUser.getDomain());
		if (twofaFunc.getActivationPolicy().getStatus()) {
			dto.setSecondFAUuid(authUser.getLsUuid());
			dto.setSecondFAEnabled(authUser.isUsing2FA());
		}
		return dto;
	}

	@Override
	public void changePassword(PasswordDto password) throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		User actor = getActor(authUser, null);
		userService.changePassword(authUser, actor, password.getOldPwd(), password.getNewPwd());
	}

	@Override
	public UserDtoQuotaDto findUserQuota(String actorUuid, String accountUuid, String quotaUuid) {
		Account authUser = checkAuthentication(Role.ADMIN);
		Account actor = getActor(authUser, actorUuid);
		Validate.notEmpty(quotaUuid, "quotaUuid must be set.");
		Validate.notEmpty(accountUuid, "accountUuid must be set.");
		User user = userService2.find(authUser, actor, accountUuid);
		checkAdminPermission(authUser, user);
		AccountQuota quota = accountQuotaService.find(authUser, actor, user.getLsUuid(), quotaUuid);
		Long realTimeUsedSpace = quotaService.getRealTimeUsedSpace(authUser, authUser, quotaUuid);
		UserDtoQuotaDto quotaDto = new UserDtoQuotaDto(quota, realTimeUsedSpace);
		return quotaDto;
	}

	@Override
	public UserDtoQuotaDto updateUserQuota(String actorUuid, String userUuid, String quotaUuid, UserDtoQuotaDto dto) {
		Account authUser = checkAuthentication(Role.ADMIN);
		Account actor = getActor(authUser, actorUuid);
		Validate.notEmpty(userUuid, "userUuid must be set.");
		Validate.notNull(dto, "quota dto must be not null.");
		Validate.notNull(dto.getQuota(), "quota must be set.");
		if (!Strings.isNullOrEmpty(quotaUuid)) {
			dto.setUuid(quotaUuid);
		} else {
			Validate.notEmpty(dto.getUuid(), "Dto's uuid must be set.");
		}
		User user = userService2.find(authUser, actor, userUuid);
		checkAdminPermission(authUser, user);
		AccountQuota aq = accountQuotaService.update(authUser, actor, userUuid, dto.toObject());
		return new UserDtoQuotaDto(aq, quotaService.getRealTimeUsedSpace(authUser, authUser, dto.getUuid()));
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
		// TODO: should be done in the service
		UserAuditLogEntry userAuditLogEntry = new UserAuditLogEntry(authUser, user, LogAction.UPDATE,
				AuditLogEntryType.USER, (User) user);
		userAuditLogEntry.setCause(LogActionCause.SECOND_FACTOR_SHARED_KEY_DELETE);
		logEntryService.insert(userAuditLogEntry);
		return new SecondFactorDto(user.getLsUuid(), user.getSecondFACreationDate(), user.isUsing2FA());
	}

	// TODO: Could be moved to AdminGenericFacadeImpl
	private void checkSecondFactorUuid(String userUuid, String secondFactorUuid) {
		// For now, second factor uuid must equal user uuid.
		if (!userUuid.equals(secondFactorUuid)) {
			String message = "Second factor key uuid must be the same as user uuid.";
			logger.error(message);
			throw new BusinessException(message);
		}
	}
	// TODO: Could be moved to AdminGenericFacadeImpl 
	private void checkAdminPermission(Account actor, Account user) {
		if (!domainPermissionBusinessService.isAdminForThisDomain(actor, user.getDomain())) {
			logger.error("Not allowed to perform this action, You are not an admin for domain {}",
					user.getDomainId());
			throw new BusinessException(BusinessErrorCode.AUTHENTICATION_SECOND_FACTOR_FORBIDEN,
					"Not allowed to perform this action");
		}
	}

	@Override
	public List<GuestDto> findAllUserGuests(String actorUuid, String uuid, ModeratorRoleEnum role, String pattern) {
		Account authUser = checkAuthentication(Role.ADMIN);
		User actor = getActor(authUser, actorUuid);
		Validate.notEmpty(uuid, "User's uuid should be set.");
		User userToFilterBy = userService2.find(authUser, actor, uuid);
		Optional<User> moderator = Optional.ofNullable(userToFilterBy);
		Optional<ModeratorRole> moderatorRole = Optional.empty();
		if (role != null) {
			if(!role.equals(ModeratorRoleEnum.ALL)) {
				// Here we at least always filter by the "owner", so ModeratorRoleEnum.ALL does not have any meaning.
				moderatorRole = Optional.of(ModeratorRoleEnum.toModeratorRole(role));
			}
		}
		List<Guest> guests = guestService.findAll(authUser, actor, moderator, Optional.ofNullable(pattern), moderatorRole);
		// FIXME:FMA addModeratorRoletoGuestDto
		return guests
				.stream()
				.map(GuestDto.toDto())
				.collect(Collectors.toUnmodifiableList());
	}

	private UserDto toDtoV5(User user) {
		UserDto userDto;
		if (user.isGuest()) {
			userDto = UserDto.toDtoV5(user, utilGuestAuthor.getAuthor(user.getLsUuid()));
		} else {
			userDto = UserDto.toDtoV5(user, null);
		}
		return userDto;
	}

	@Override
	public UserDto create(UserDto userDto) throws BusinessException {
		Account authUser = checkAuthentication(Role.ADMIN);
		if (userDto.getDomain() == null || StringUtils.isBlank(userDto.getDomain().getUuid())
				|| StringUtils.isBlank(userDto.getMail())
				|| userDto.getRole() == null) {
			throw new BusinessException("Domain, mail and role are mandatory to create an user.");
		}
		AbstractDomain targetDomain = abstractDomainService.findById(userDto.getDomain().getUuid());
		if (!authUser.hasSuperAdminRole() && !authUser.getDomain().equals(targetDomain)) {
			throw new BusinessException("As admin you can only create on your own domain : " + authUser.getDomain().getLabel());
		}

		User userToUpdate = userDto.toUserObject(userDto.isGuest());
		userToUpdate.setDomain(targetDomain);
		return toDtoV5(userService.saveOrUpdateUser(userToUpdate, Optional.of(authUser)));
	}
}
