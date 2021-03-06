/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.ContainerQuotaFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.ContainerQuotaDto;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ContainerQuotaService;
import org.linagora.linshare.core.service.QuotaService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class ContainerQuotaFacadeImpl extends AdminGenericFacadeImpl implements ContainerQuotaFacade {

	private final ContainerQuotaService service;
	
	private final AbstractDomainService abstractDomainService;

	private final QuotaService quotaService;

	public ContainerQuotaFacadeImpl(
			final AccountService accountService,
			final ContainerQuotaService containerQuotaService,
			final AbstractDomainService abstractDomainService,
			final QuotaService quotaService) {
		super(accountService);
		this.service = containerQuotaService;
		this.abstractDomainService = abstractDomainService;
		this.quotaService = quotaService;
	}

	@Override
	public ContainerQuotaDto find(String uuid, boolean realTime) throws BusinessException {
		Validate.notEmpty(uuid, "uuid must be set.");
		User authUser = checkAuthentication(Role.ADMIN);
		ContainerQuota quota = service.find(authUser, uuid);
		ContainerQuotaDto dto = new ContainerQuotaDto(quota);
		if (realTime) {
			Long usedSpace = quotaService.getRealTimeUsedSpace(authUser, authUser, quota);
			dto.setUsedSpace(usedSpace);
		}
		return dto;
	}

	@Override
	public List<ContainerQuotaDto> findAll(String domainUuid, ContainerQuotaType type) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		// TODO FMA Quota manage type and domains filters.
		List<ContainerQuota> containers = null;
		if (domainUuid != null) {
			AbstractDomain domain = abstractDomainService.findById(domainUuid);
			containers = service.findAll(authUser, domain);
		} else {
			containers = service.findAll(authUser);
		}
		return ImmutableList.copyOf(Lists.transform(containers, ContainerQuotaDto.toDto()));
	}

	@Override
	public ContainerQuotaDto update(ContainerQuotaDto dto, String uuid) throws BusinessException {
		Validate.notNull(dto, "ContainerQuotaDto must be set.");
		Validate.notNull(dto.getQuota(), "Quota must be set.");
		if (!Strings.isNullOrEmpty(uuid)) {
			dto.setUuid(uuid);
		}
		Validate.notEmpty(dto.getUuid(), "Quota uuid must be set.");
		User authUser = checkAuthentication(Role.ADMIN);
		ContainerQuota cq = service.update(authUser, dto.toObject());
		return new ContainerQuotaDto(cq);
	}
}
