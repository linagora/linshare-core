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

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WelcomeMessages;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.WelcomeMessagesFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.WelcomeMessagesDto;
import org.linagora.linshare.core.facade.webservice.common.dto.DomainLightDto;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.WelcomeMessagesService;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class WelcomeMessagesFacadeImpl extends AdminGenericFacadeImpl implements
		WelcomeMessagesFacade {

	private final WelcomeMessagesService service;

	private final AbstractDomainService domainService;

	public WelcomeMessagesFacadeImpl(final AccountService accountService,
			final WelcomeMessagesService wlcmService,
			final AbstractDomainService abstractDomainService) {
		super(accountService);
		this.service = wlcmService;
		this.domainService = abstractDomainService;
	}

	@Override
	public Set<WelcomeMessagesDto> findAll(String domainId, boolean parent)
			throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Set<WelcomeMessagesDto> wlcmDtoList = Sets.newHashSet();
		List<WelcomeMessages> entities = service.findAll(authUser, domainId, parent);
		for (WelcomeMessages entity : entities) {
			wlcmDtoList.add(new WelcomeMessagesDto(entity, true));
		}
		return wlcmDtoList;
	}

	@Override
	public WelcomeMessagesDto find(String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(uuid, "Welcome message uuid must be set.");
		WelcomeMessages welcomeMessage = service.find(authUser, uuid);
		WelcomeMessagesDto ret = new WelcomeMessagesDto(welcomeMessage, true);
		loadRelativeDomains(authUser, ret);
		return ret;
	}

	@Override
	public WelcomeMessagesDto create(WelcomeMessagesDto wlcmDto)
			throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notNull(wlcmDto, "Welcome message must be set.");
		Validate.notEmpty(wlcmDto.getUuid(),
				"Welcome message uuid must be set.");
		Validate.notNull(wlcmDto.getMyDomain(),
				"Welcome message domain must be set.");
		String domainId = wlcmDto.getMyDomain().getIdentifier();
		Validate.notEmpty(domainId,
				"Welcome message domain identifier must be set.");

		WelcomeMessages wlcm = wlcmDto.toObject();
		WelcomeMessages wlcmMessage = service.create(authUser, wlcm, domainId);
		WelcomeMessagesDto ret = new WelcomeMessagesDto(wlcmMessage, true);
		loadRelativeDomains(authUser, ret);
		return ret;
	}

	@Override
	public WelcomeMessagesDto update(WelcomeMessagesDto wlcmDto, String uuid) throws BusinessException {
		Validate.notNull(wlcmDto, "Welcome message object must be set.");
		if (!Strings.isNullOrEmpty(uuid)) {
			wlcmDto.setUuid(uuid);
		}
		Validate.notEmpty(wlcmDto.getUuid(), "Welcome message uuid must be set.");
		User authUser = checkAuthentication(Role.ADMIN);
		if (wlcmDto.getDomains() == null) {
			Set<DomainLightDto> domains = Sets.newHashSet();
			wlcmDto.setDomains(domains);
		}
		WelcomeMessages wlcm = wlcmDto.toObject();
		List<String> domainUuids = Lists.newArrayList();
		for (DomainLightDto d : wlcmDto.getDomains()) {
			domainUuids.add(d.getIdentifier());
		}
		WelcomeMessages wlcmMessage = service.update(authUser, wlcm, domainUuids);
		WelcomeMessagesDto ret = new WelcomeMessagesDto(wlcmMessage, true);
		loadRelativeDomains(authUser, ret);
		return ret;
	}

	@Override
	public WelcomeMessagesDto delete(String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(uuid, "Welcome message uuid must be set.");
		WelcomeMessages welcomeMessage = service.delete(authUser, uuid);
		return new WelcomeMessagesDto(welcomeMessage, true);
	}

	@Override
	public WelcomeMessagesDto delete(WelcomeMessagesDto wlcmDto)
			throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notNull(wlcmDto, "Welcome message must be set.");
		Validate.notEmpty(wlcmDto.getUuid(),
				"Welcome message uuid must be set.");
		WelcomeMessages welcomeMessage = service.delete(authUser,
				wlcmDto.getUuid());
		return new WelcomeMessagesDto(welcomeMessage, true);
	}

	private void loadRelativeDomains(User authUser,
			WelcomeMessagesDto welcomeMessage) {
		for (AbstractDomain domain : domainService.loadRelativeDomains(authUser,
				welcomeMessage.getUuid())) {
			welcomeMessage.addDomain(domain);
		}
	}
}
