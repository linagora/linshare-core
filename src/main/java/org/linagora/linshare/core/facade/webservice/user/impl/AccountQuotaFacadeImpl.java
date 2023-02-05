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
package org.linagora.linshare.core.facade.webservice.user.impl;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.AccountQuotaFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.AccountQuotaDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.QuotaService;

public class AccountQuotaFacadeImpl extends GenericFacadeImpl implements AccountQuotaFacade {

	protected final QuotaService quotaService;

	public AccountQuotaFacadeImpl(
			final AccountService accountService,
			final QuotaService service) {
		super(accountService);
		this.quotaService = service;
	}

	@Override
	public AccountQuotaDto find(String actorUuid, String uuid) throws BusinessException {
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		Validate.notEmpty(uuid, "Missing account uuid");
		AccountQuota aq = quotaService.find(authUser, actor, uuid);
		AccountQuotaDto dto = null;
		if (aq.getDomainShared()) {
			Long usedSpace = quotaService.getRealTimeUsedSpace(authUser, actor, uuid);
			DomainQuota domainQuota = aq.getContainerQuota().getDomainQuota();
			dto = new AccountQuotaDto(domainQuota.getQuota(), usedSpace, aq.getMaxFileSize(), aq.getMaintenance());
			Long domainUsedSpace = quotaService.getRealTimeUsedSpace(authUser, actor, domainQuota);
			dto.setDomainUsedSpace(domainUsedSpace);
		} else {
			if (aq.getShared()) {
				Long usedSpace = quotaService.getRealTimeUsedSpace(authUser, actor, aq.getContainerQuota());
				dto = new AccountQuotaDto(aq.getContainerQuota().getQuota(), usedSpace, aq.getMaxFileSize(), aq.getMaintenance());
			} else {
				Long usedSpace = quotaService.getRealTimeUsedSpace(authUser, actor, uuid);
				dto = new AccountQuotaDto(aq.getQuota(), usedSpace, aq.getMaxFileSize(), aq.getMaintenance());
			}
		}
		return dto;
	}

	@Override
	public boolean maintenanceModeIsEnabled() throws BusinessException {
		User authUser = checkAuthentication();
		AccountQuota aq = quotaService.findByRelatedAccount(authUser);
		return aq.getMaintenance();
	}

}
