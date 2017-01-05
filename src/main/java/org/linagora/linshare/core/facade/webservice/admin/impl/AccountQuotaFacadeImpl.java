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
package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.AccountQuotaFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.AccountQuotaDto;
import org.linagora.linshare.core.service.AccountQuotaService;
import org.linagora.linshare.core.service.AccountService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class AccountQuotaFacadeImpl extends AdminGenericFacadeImpl implements AccountQuotaFacade {

	private final AccountQuotaService service;

	public AccountQuotaFacadeImpl(
			final AccountService accountService,
			final AccountQuotaService service) {
		super(accountService);
		this.service = service;
	}

	@Override
	public AccountQuotaDto find(String uuid) throws BusinessException {
		Validate.notNull(uuid, "Account quota uuid must be set.");
		User actor = checkAuthentication(Role.ADMIN);
		AccountQuota quota = service.find(actor, uuid);
		return new AccountQuotaDto(quota);
	}

	@Override
	public List<AccountQuotaDto> findAll(String domainUuid, ContainerQuotaType type) throws BusinessException {
		// TODO FMA Quota manage type and domains filters.
		User actor = checkAuthentication(Role.ADMIN);
		List<AccountQuota> all = service.findAll(actor);
		return ImmutableList.copyOf(Lists.transform(all, AccountQuotaDto.toDto()));
	}

	@Override
	public AccountQuotaDto update(AccountQuotaDto dto, String uuid) throws BusinessException {
		Validate.notNull(dto, "AccountQuotaDto must be set.");
		if (!Strings.isNullOrEmpty(uuid)) {
			dto.setUuid(uuid);
		}
		Validate.notEmpty(dto.getUuid(), "Quota uuid must be set.");
		User actor = checkAuthentication(Role.ADMIN);
		AccountQuota aq = service.update(actor, dto.toObject());
		return new AccountQuotaDto(aq);
	}

}
