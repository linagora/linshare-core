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
package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.List;

import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.constants.StatisticType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Statistic;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.StatisticFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.StatisticDto;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.StatisticService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class StatisticFacadeImpl extends AdminGenericFacadeImpl implements
		StatisticFacade {

	private final StatisticService statisticService;
	private final AbstractDomainService abstractDomainService;
	private final AccountService accountService;
	private final DomainPermissionBusinessService permissionService;

	public StatisticFacadeImpl(AccountService accountService,
			DomainPermissionBusinessService permissionService,
			StatisticService statisticService,
			AbstractDomainService abstractDomainService) {
		super(accountService);
		this.statisticService = statisticService;
		this.abstractDomainService = abstractDomainService;
		this.accountService = accountService;
		this.permissionService = permissionService;
	}

	@Override
	public List<StatisticDto> findBetweenTwoDates(String accountUuid,
			String domainUuid, String beginDate, String endDate,
			StatisticType statisticType) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		AbstractDomain domain = null;
		// TODO FIXME Quota & Statistics
		Account actor = null;
		if (domainUuid != null) {
			domain = abstractDomainService.findById(domainUuid);
			if (!permissionService.isAdminforThisDomain(authUser, domain)) {
				throw new BusinessException(
						BusinessErrorCode.STATISTIC_READ_DOMAIN_ERROR,
						"You are not allowed to use this domain");
			}
		}
		if (accountUuid != null) {
			actor = accountService.findByLsUuid(accountUuid);
			if (!permissionService.isAdminForThisUser(authUser, (User) actor)) {
				throw new BusinessException(
						BusinessErrorCode.STATISTIC_READ_ACTOR_ERROR,
						"You are not allowed to read these accounts statistics");
			}
		}
		List<Statistic> listStat = statisticService.findBetweenTwoDates(authUser, actor,
				domain, beginDate, endDate, statisticType);
		return ImmutableList.copyOf(Lists.transform(listStat,
				StatisticDto.toDto()));
	}
}
