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

package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.WelcomeMessages;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.WelcomeMessagesFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.WelcomeMessagesDto;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.WelcomeMessagesService;

import com.google.common.collect.Sets;

public class WelcomeMessagesFacadeImpl extends AdminGenericFacadeImpl implements
		WelcomeMessagesFacade {

	private final WelcomeMessagesService welcomeMessagesService;

	private final AbstractDomainService abstractDomainService;

	public WelcomeMessagesFacadeImpl(final AccountService accountService,
			final WelcomeMessagesService wlcmService,
			final AbstractDomainService abstractDomainService) {
		super(accountService);
		this.welcomeMessagesService = wlcmService;
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	public Set<WelcomeMessagesDto> findAll(String domainId)
			throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		Set<WelcomeMessagesDto> wlcmDtoList = Sets.newHashSet();
		List<WelcomeMessages> entities = welcomeMessagesService.findAll(actor, domainId);
		for (WelcomeMessages entity : entities) {
			wlcmDtoList.add(new WelcomeMessagesDto(entity));
		}
		return wlcmDtoList;
	}

	@Override
	public WelcomeMessagesDto find(String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Welcome message uuid must be set.");
		User actor = checkAuthentication(Role.ADMIN);
		return new WelcomeMessagesDto(welcomeMessagesService.find(actor, uuid));
	}

	@Override
	public WelcomeMessagesDto create(WelcomeMessagesDto wlcmDto)
			throws BusinessException {
		Validate.notNull(wlcmDto, "Welcome message must be set.");
		User actor = checkAuthentication(Role.ADMIN);

		AbstractDomain domain = abstractDomainService.findById(wlcmDto
				.getMyDomain().getIdentifier());

		WelcomeMessages wlcm = wlcmDto.toObject();
		WelcomeMessages wlcmMessage = welcomeMessagesService.create(actor,
				domain, wlcm.getUuid());
		return new WelcomeMessagesDto(wlcmMessage);
	}

	@Override
	public WelcomeMessagesDto update(WelcomeMessagesDto wlcmDto)
			throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);

		AbstractDomain domain = abstractDomainService.findById(wlcmDto
				.getMyDomain().getIdentifier());

		WelcomeMessages wlcm = wlcmDto.toObject();
		WelcomeMessages wlcmMessage = welcomeMessagesService.update(actor,
				domain, wlcm);
		return new WelcomeMessagesDto(wlcmMessage);
	}

	@Override
	public WelcomeMessagesDto delete(String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Welcome message uuid must be set.");
		User actor = checkAuthentication(Role.ADMIN);
		return new WelcomeMessagesDto(welcomeMessagesService.delete(actor, uuid));
	}

	@Override
	public WelcomeMessagesDto delete(WelcomeMessagesDto wlcmDto)
			throws BusinessException {
		Validate.notNull(wlcmDto,"Welcome message must be set.");
		Validate.notEmpty(wlcmDto.getUuid(), "Welcome message uuid must be set.");
		User actor = checkAuthentication(Role.ADMIN);
		return new WelcomeMessagesDto(welcomeMessagesService.delete(actor,
				wlcmDto.getUuid()));
	}
}
