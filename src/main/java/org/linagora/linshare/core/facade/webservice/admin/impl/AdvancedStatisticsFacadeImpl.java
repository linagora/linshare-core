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

import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.AdvancedStatisticsFacade;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AdvancedStatisticService;
import org.linagora.linshare.mongo.entities.MimeTypeStatistic;

public class AdvancedStatisticsFacadeImpl extends AdminGenericFacadeImpl implements AdvancedStatisticsFacade {

	protected AdvancedStatisticService statisticService;

	protected AbstractDomainService abstractDomainService;

	protected DomainPermissionBusinessService permissionService;

	public AdvancedStatisticsFacadeImpl(
			AccountService accountService,
			AdvancedStatisticService statisticService,
			DomainPermissionBusinessService permissionService,
			AbstractDomainService abstractDomainService) {
		super(accountService);
		this.statisticService = statisticService;
		this.abstractDomainService = abstractDomainService;
		this.permissionService = permissionService;
	}

	@Override
	public Set<MimeTypeStatistic> findBetweenTwoDates(String domainUuid, String beginDate,
			String endDate, String mimeType) {
		Validate.notEmpty(mimeType);
		User authUser = checkAuthentication(Role.ADMIN);
		if (domainUuid.isEmpty()) {
			domainUuid = authUser.getDomainId();
		}
		AbstractDomain domain = abstractDomainService.findById(domainUuid);
		if (!permissionService.isAdminForThisDomain(authUser, domain)) {
			throw new BusinessException(BusinessErrorCode.STATISTIC_READ_DOMAIN_ERROR,
					"You are not allowed to use this domain");
		}
		return statisticService.findBetweenTwoDates(authUser, domainUuid, beginDate, endDate, mimeType);
	}

}
