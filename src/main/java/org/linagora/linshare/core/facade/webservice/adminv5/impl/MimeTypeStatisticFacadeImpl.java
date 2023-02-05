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

import java.util.Optional;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.AdvancedStatisticType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.fields.MimeTypeStatisticField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.facade.webservice.admin.impl.AdminGenericFacadeImpl;
import org.linagora.linshare.core.facade.webservice.adminv5.MimeTypeStatisticFacade;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AdvancedStatisticService;
import org.linagora.linshare.mongo.entities.MimeTypeStatistic;
import org.linagora.linshare.webservice.utils.PageContainer;

public class MimeTypeStatisticFacadeImpl extends AdminGenericFacadeImpl implements
		MimeTypeStatisticFacade {

	private final AdvancedStatisticService statisticService;
	private final AbstractDomainService abstractDomainService;

	public MimeTypeStatisticFacadeImpl(AccountService accountService,
			AdvancedStatisticService statisticService,
			AbstractDomainService abstractDomainService) {
		super(accountService);
		this.statisticService = statisticService;
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	public PageContainer<MimeTypeStatistic> findAll(
			String domainUuid, boolean includeNestedDomains,
			SortOrder sortOrder,
			MimeTypeStatisticField sortField, AdvancedStatisticType statisticType,
			Optional<String> mimeType,
			boolean sum, Optional<String> beginDate,
			Optional<String> endDate, Integer pageNumber, Integer pageSize) {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(domainUuid, "Missing domain uuid in the path.");
		AbstractDomain domain = abstractDomainService.findById(domainUuid);
		PageContainer<MimeTypeStatistic> container = new PageContainer<>(pageNumber, pageSize);
		container = statisticService.findAll(authUser, domain, includeNestedDomains, Optional.empty(), sortOrder, sortField, statisticType, mimeType, sum, beginDate, endDate, container);
		return container;
	}
}
