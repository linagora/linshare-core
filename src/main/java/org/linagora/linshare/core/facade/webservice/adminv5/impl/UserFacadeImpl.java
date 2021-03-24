/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2021. Contribute to
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
package org.linagora.linshare.core.facade.webservice.adminv5.impl;

import java.util.List;

import org.apache.commons.lang3.Validate;
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
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.impl.AdminGenericFacadeImpl;
import org.linagora.linshare.core.facade.webservice.adminv5.UserFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.RestrictedContactDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDto;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.QuotaService;
import org.linagora.linshare.core.service.UserService2;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.linagora.linshare.webservice.utils.PageContainerAdaptor;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class UserFacadeImpl extends AdminGenericFacadeImpl implements UserFacade {
	
	private final UserService2 userService2;

	private final GuestService guestService;

	private final QuotaService quotaService;

	private final AbstractDomainService abstractDomainService;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final PageContainerAdaptor<User, UserDto> pageContainerAdaptor = new PageContainerAdaptor<>();

	public UserFacadeImpl(
			AccountService accountService,
			UserService2 userService2,
			AbstractDomainService abstractDomainService,
			GuestService guestService,
			QuotaService quotaService,
			FunctionalityReadOnlyService functionalityReadOnlyService) {
		super(accountService);
		this.userService2 = userService2;
		this.abstractDomainService = abstractDomainService;
		this.guestService = guestService;
		this.quotaService = quotaService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
	}

	@Override
	public PageContainer<UserDto> findAll(String actorUuid, String domainUuid, SortOrder sortOrder,
			UserFields sortField, String mail, String firstName, String lastName, Boolean restricted,
			Boolean canCreateGuest, Boolean canUpload, String role, String type, Integer pageNumber, Integer pageSize) {
		User authUser = checkAuthentication(Role.ADMIN);
		User actor = getActor(authUser, actorUuid);
		PageContainer<User> container = new PageContainer<>(pageNumber, pageSize);
		AbstractDomain domain = null;
		if (!Strings.isNullOrEmpty(domainUuid)) {
			domain = abstractDomainService.findById(domainUuid);
		}
		container = userService2.findAll(authUser, actor, domain, sortOrder, sortField, mail, firstName, lastName,
				restricted, canCreateGuest, canUpload, role, type, container);
		PageContainer<UserDto> dto = pageContainerAdaptor.convert(container, UserDto.toDto());
		return dto;
	}

	@Override
	public UserDto find(String actorUuid, String uuid) {
		User authUser = checkAuthentication(Role.ADMIN);
		User actor = getActor(authUser, actorUuid);
		Validate.notEmpty(uuid, "User uuid must be set.");
		User user = userService2.find(authUser, actor, uuid);
		UserDto userDto = null;
		if (user.isGuest() && user.isRestricted()) {
			Guest guest = guestService.find(authUser, actor, uuid);
			userDto = UserDto.getFull(guest);
		} else {
			userDto = UserDto.getFull(user);
		}
		AccountQuota quota = quotaService.findByRelatedAccount(user);
		userDto.setQuotaUuid(quota.getUuid());
		userDto.setLocked(user.isLocked());
		setSecondFAAttributes(userDto, user);
		return userDto;
	}

	private void setSecondFAAttributes(UserDto userDto, User user) {
		BooleanValueFunctionality twofaFunc = functionalityReadOnlyService
				.getSecondFactorAuthenticationFunctionality(user.getDomain());
		if (twofaFunc.getActivationPolicy().getStatus()) {
			userDto.setSecondFAUuid(user.getLsUuid());
			userDto.setSecondFAEnabled(user.isUsing2FA());
			userDto.setSecondFARequired(twofaFunc.getValue());
		} else {
			userDto.setSecondFAEnabled(false);
			userDto.setSecondFARequired(false);
		}
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
		User entity = userService2.find(authUser, actor, userDto.getUuid());
		User userToUpdate = userDto.toUserObject(entity.isGuest());
		if (!userDto.isLocked() && entity.isLocked()) {
			entity = userService2.unlock(authUser, actor, entity);
		}
		User update = checkAccountTypeAndUpdate(authUser, actor, userDto, entity, userToUpdate);
		UserDto updatedDto = UserDto.getFull(update);
		updatedDto.setLocked(update.isLocked());
		return updatedDto;
	}

	private User checkAccountTypeAndUpdate(Account authUser, Account actor, UserDto userDto, User entity,
			User userToUpdate) {
		User update = null;
		if (entity.isGuest()) {
			List<String> ac = null;
			if (userDto.isRestricted()) {
				ac = Lists.newArrayList();
				for (UserDto contactDto : userDto.getRestrictedContacts()) {
					ac.add(contactDto.getMail());
				}
			}
			update = guestService.update(authUser, (User) entity.getOwner(), (Guest) userToUpdate, ac);
		} else {
			update = userService2.update(authUser, actor, userToUpdate, userDto.getDomain());
		}
		return update;
	}

	@Override
	public UserDto delete(String actorUuid, UserDto userDto, String uuid) throws BusinessException {
		Account authUser = checkAuthentication(Role.ADMIN);
		Account actor = getActor(authUser, actorUuid);
		if (!Strings.isNullOrEmpty(uuid)) {
			userDto.setUuid(uuid);
		}
		Validate.notEmpty(userDto.getUuid(), "user uuid must be set");
		User user = userService2.delete(authUser, actor, userDto.getUuid());
		return UserDto.getFull(user);
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
}
