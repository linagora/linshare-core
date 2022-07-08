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
package org.linagora.linshare.core.facade.webservice.adminv5.impl;

import java.util.Optional;

import org.jsoup.helper.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.constants.StatisticType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Statistic;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.domain.entities.fields.StorageConsumptionStatisticField;
import org.linagora.linshare.core.facade.webservice.admin.impl.AdminGenericFacadeImpl;
import org.linagora.linshare.core.facade.webservice.adminv5.StorageConsumptionStatisticFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.StorageConsumptionStatisticDto;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.StatisticService;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.linagora.linshare.webservice.utils.PageContainerAdaptor;

public class StorageConsumptionStatisticFacadeImpl extends AdminGenericFacadeImpl implements
		StorageConsumptionStatisticFacade {

	private final StatisticService statisticService;
	private final AbstractDomainService abstractDomainService;
	private static PageContainerAdaptor<Statistic, StorageConsumptionStatisticDto> pageContainerAdaptor = new PageContainerAdaptor<>();

	public StorageConsumptionStatisticFacadeImpl(AccountService accountService,
			StatisticService statisticService,
			AbstractDomainService abstractDomainService) {
		super(accountService);
		this.statisticService = statisticService;
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	public PageContainer<StorageConsumptionStatisticDto> findAll(
			String domainUuid, Optional<String> accountUuid,
			SortOrder sortOrder, StorageConsumptionStatisticField sortField,
			StatisticType statisticType,
			Optional<String> beginDate, Optional<String> endDate,
			Integer pageNumber, Integer pageSize) {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(domainUuid, "Missing domain uuid in the path.");
		AbstractDomain domain = abstractDomainService.findById(domainUuid);
		PageContainer<Statistic> container = new PageContainer<>(pageNumber, pageSize);
		container = statisticService.findAll(authUser, domain, accountUuid, sortOrder, sortField, statisticType, beginDate, endDate, container);
		PageContainer<StorageConsumptionStatisticDto> dto = pageContainerAdaptor.convert(container, StorageConsumptionStatisticDto.toDto());
		return dto;
	}
}
