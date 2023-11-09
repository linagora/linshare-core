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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WelcomeMessages;
import org.linagora.linshare.core.domain.entities.WelcomeMessagesEntry;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.impl.AdminGenericFacadeImpl;
import org.linagora.linshare.core.facade.webservice.adminv5.WelcomeMessageFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.WelcomeMessageAssignDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.WelcomeMessageDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DomainService;
import org.linagora.linshare.core.service.WelcomeMessagesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class WelcomeMessageFacadeImpl extends AdminGenericFacadeImpl implements WelcomeMessageFacade {

	private static final Logger LOGGER = LoggerFactory.getLogger(WelcomeMessageFacadeImpl.class);

	private final DomainService domainService;

	private final WelcomeMessagesService welcomeMessagesService;

	private final DomainPermissionBusinessService domainPermissionBusinessService;

	private final DomainBusinessService domainBusinessService;

	public WelcomeMessageFacadeImpl(AccountService accountService,
				DomainService domainService,
				WelcomeMessagesService welcomeMessagesService,
				DomainPermissionBusinessService domainPermissionBusinessService,
				DomainBusinessService domainBusinessService) {
		super(accountService);
		this.domainService = domainService;
		this.welcomeMessagesService = welcomeMessagesService;
		this.domainPermissionBusinessService = domainPermissionBusinessService;
		this.domainBusinessService = domainBusinessService;
	}

	@Override
	public List<WelcomeMessageDto> findAll(String domainUuid) {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(domainUuid, "Domain uuid uuid must be set.");
		AbstractDomain domain = domainService.find(authUser, domainUuid);
		return welcomeMessagesService.findAll(authUser, domainUuid, true)
				.stream()
				.map(welcomeMessage -> WelcomeMessageDto.from(welcomeMessage, domain, isReadOnly(authUser, welcomeMessage)))
				.collect(Collectors.toUnmodifiableList());
	}

	private boolean isReadOnly(User authUser, WelcomeMessages welcomeMessages) {
		if (authUser.getRole().equals(Role.SUPERADMIN)) {
			return false;
		}
		if (domainPermissionBusinessService.isAdminForThisDomain(authUser, welcomeMessages.getDomain())) {
			return false;
		}
		return true;
	}

	@Override
	public WelcomeMessageDto find(String domainUuid, String welcomeMessageUuid) {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(domainUuid, "Domain uuid uuid must be set.");
		Validate.notEmpty(welcomeMessageUuid, "Welcome message uuid must be set.");
		WelcomeMessages welcomeMessage = welcomeMessagesService.find(authUser, welcomeMessageUuid);
		AbstractDomain domain = welcomeMessage.getDomain();
		return WelcomeMessageDto.from(welcomeMessage, domain, isReadOnly(authUser, welcomeMessage));
	}

	@Override
	public WelcomeMessageDto create(String domainUuid, WelcomeMessageDto welcomeMessageDto) {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(domainUuid, "Domain uuid uuid must be set.");
		Validate.notNull(welcomeMessageDto, "Welcome message must be set.");
		AbstractDomain domain = domainService.find(authUser, domainUuid);
		WelcomeMessages welcomeMessages = toEntity(domain, welcomeMessageDto);
		return WelcomeMessageDto.from(
				createWelcomeMessages(domainUuid, welcomeMessageDto, authUser, welcomeMessages),
				domain,
				isReadOnly(authUser, welcomeMessages));
	}

	private WelcomeMessages createWelcomeMessages(String domainUuid, WelcomeMessageDto welcomeMessageDto, User authUser, WelcomeMessages welcomeMessages) {
		if (!Strings.isNullOrEmpty(welcomeMessageDto.getUuid())) {
			return welcomeMessagesService.createByCopy(authUser, welcomeMessages, domainUuid);
		}
		return welcomeMessagesService.create(authUser, welcomeMessages);
	}

	private WelcomeMessages toEntity(AbstractDomain domain, WelcomeMessageDto welcomeMessageDto) {
		WelcomeMessages welcomeMessages = new WelcomeMessages(welcomeMessageDto.getName(),
				welcomeMessageDto.getDescription(),
				domain,
				entries(welcomeMessageDto.getEntries()));
		welcomeMessages.setUuid(welcomeMessageDto.getUuid());
		return welcomeMessages;
	}

	private Map<SupportedLanguage, WelcomeMessagesEntry> entries(Map<SupportedLanguage, String> entries) {
		Map<SupportedLanguage, WelcomeMessagesEntry> map = new HashMap<>();
		if (entries != null) {
			for (Map.Entry<SupportedLanguage, String> entry : entries.entrySet()) {
				map.put(entry.getKey(), new WelcomeMessagesEntry(entry.getKey(), entry.getValue()));
			}
		}
		return map;
	}

	@Override
	public WelcomeMessageDto update(String domainUuid, String welcomeMessageUuid, WelcomeMessageDto welcomeMessageDto) {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(domainUuid, "Domain uuid uuid must be set.");
		Validate.notNull(welcomeMessageDto, "Welcome message object must be set.");
		if (!Strings.isNullOrEmpty(welcomeMessageUuid)) {
			welcomeMessageDto.setUuid(welcomeMessageUuid);
		}
		Validate.notEmpty(welcomeMessageDto.getUuid(), "Welcome message uuid must be set.");
		AbstractDomain domain = domainService.find(authUser, domainUuid);
		WelcomeMessages welcomeMessage = welcomeMessagesService.find(authUser, welcomeMessageUuid);
		if (isReadOnly(authUser, welcomeMessage)) {
			LOGGER.info("The welcome message %s is belonging to domain %s (not %s)",
					welcomeMessageUuid,
					domain.getUuid(),
					domainUuid);
			throw new BusinessException(
					BusinessErrorCode.WELCOME_MESSAGES_NOT_FOUND,
					"Welcome message with uuid :" + welcomeMessageUuid + " not found.");
		}
		List<String> domainUuids = null;
		WelcomeMessages welcomeMessages = toEntity(domain, welcomeMessageDto);
		return WelcomeMessageDto.from(
				welcomeMessagesService.update(authUser, welcomeMessages, domainUuids),
				domain,
				isReadOnly(authUser, welcomeMessages));
	}

	@Override
	public WelcomeMessageDto delete(String domainUuid, String welcomeMessageUuid, WelcomeMessageDto welcomeMessageDto) {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(domainUuid, "Domain uuid uuid must be set.");
		Validate.notNull(welcomeMessageDto, "Welcome message object must be set.");
		if (!Strings.isNullOrEmpty(welcomeMessageUuid)) {
			welcomeMessageDto.setUuid(welcomeMessageUuid);
		}
		Validate.notEmpty(welcomeMessageDto.getUuid(), "Welcome message uuid must be set.");
		if (isAssigned(welcomeMessageDto.getUuid())) {
			throw new BusinessException(
				BusinessErrorCode.WELCOME_MESSAGES_ASSIGNED,
				"Welcome message with uuid :" + welcomeMessageDto.getUuid() + " is assigned to a domain.");
		}
		AbstractDomain domain = domainService.find(authUser, domainUuid);
		WelcomeMessages welcomeMessage = welcomeMessagesService.find(authUser, welcomeMessageDto.getUuid());
		if (isReadOnly(authUser, welcomeMessage)) {
			LOGGER.info("The welcome message %s is belonging to domain %s (not %s)",
					welcomeMessageUuid,
					domain.getUuid(),
					domainUuid);
			throw new BusinessException(
					BusinessErrorCode.WELCOME_MESSAGES_NOT_FOUND,
					"Welcome message with uuid :" + welcomeMessageDto.getUuid() + " not found.");
		}
		return WelcomeMessageDto.from(
				welcomeMessagesService.delete(authUser, welcomeMessageDto.getUuid()),
				domain,
				isReadOnly(authUser, welcomeMessage));
	}

	private boolean isAssigned(String uuid) {
		return associatedDomains(uuid)
			.stream()
			.findAny()
			.isPresent();
	}

	@Override
	public WelcomeMessageDto assign(String domainUuid, String welcomeMessageUuid, WelcomeMessageAssignDto assignDto) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(domainUuid, "Domain uuid uuid must be set.");
		Validate.notNull(assignDto, "Welcome message object must be set.");
		Validate.notEmpty(welcomeMessageUuid, "Welcome message uuid must be set.");

		AbstractDomain domain = domainService.find(authUser, domainUuid);
		WelcomeMessages welcomeMessage = welcomeMessagesService.find(authUser, welcomeMessageUuid);
		if (!domain.isAncestry(welcomeMessage.getDomain().getUuid())) {
			LOGGER.info("The welcome message %s is not belonging to the domain %s or its parent",
					welcomeMessageUuid,
					domainUuid);
			throw new BusinessException(
					BusinessErrorCode.WELCOME_MESSAGES_NOT_FOUND,
					"Welcome message with uuid :" + welcomeMessageUuid + " not found.");
		}
		List<String> domainUuids = new ArrayList<>();
		if (assignDto.isAssign()) {
			domainUuids.add(domain.getUuid());
		}
		return WelcomeMessageDto.from(
				welcomeMessagesService.update(authUser,
						welcomeMessagesService.find(authUser, welcomeMessageUuid),
						domainUuids),
				domain,
				isReadOnly(authUser, welcomeMessage));
	}

	@Override
	public List<DomainDto> associatedDomains(String welcomeMessageUuid) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(welcomeMessageUuid, "Welcome message uuid must be set.");
		WelcomeMessages welcomeMessage = welcomeMessagesService.find(authUser, welcomeMessageUuid);
		return domainBusinessService.loadRelativeDomains(welcomeMessage)
			.stream()
			.map(DomainDto::getLight)
			.collect(Collectors.toUnmodifiableList());
	}
}
