/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2021 LINAGORA
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
