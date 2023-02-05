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
package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.AccountQuotaFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.AccountQuotaDto;
import org.linagora.linshare.core.service.AccountQuotaService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.QuotaService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class AccountQuotaFacadeImpl extends AdminGenericFacadeImpl implements AccountQuotaFacade {

	private final AccountQuotaService service;

	private final QuotaService quotaService;

	public AccountQuotaFacadeImpl(
			final AccountService accountService,
			final AccountQuotaService service,
			final QuotaService quotaService) {
		super(accountService);
		this.service = service;
		this.quotaService = quotaService;
	}

	@Override
	public AccountQuotaDto find(String uuid, boolean realTime) throws BusinessException {
		Validate.notNull(uuid, "Account quota uuid must be set.");
		User authUser = checkAuthentication(Role.ADMIN);
		AccountQuota quota = service.find(authUser, uuid);
		AccountQuotaDto dto = new AccountQuotaDto(quota);
		if (realTime) {
			Long usedSpace = quotaService.getRealTimeUsedSpace(authUser, authUser, uuid);
			dto.setUsedSpace(usedSpace);
		}
		return dto;
	}

	@Override
	public List<AccountQuotaDto> findAll(String domainUuid, ContainerQuotaType type) throws BusinessException {
		// TODO FMA Quota manage type and domains filters.
		User authUser = checkAuthentication(Role.ADMIN);
		List<AccountQuota> all = service.findAll(authUser);
		return ImmutableList.copyOf(Lists.transform(all, AccountQuotaDto.toDto()));
	}

	@Override
	public AccountQuotaDto update(AccountQuotaDto dto, String uuid) throws BusinessException {
		Validate.notNull(dto, "AccountQuotaDto must be set.");
		if (!Strings.isNullOrEmpty(uuid)) {
			dto.setUuid(uuid);
		}
		Validate.notEmpty(dto.getUuid(), "Quota uuid must be set.");
		User authUser = checkAuthentication(Role.ADMIN);
		AccountQuota aq = service.update(authUser, dto.toObject());
		return new AccountQuotaDto(aq);
	}

}
