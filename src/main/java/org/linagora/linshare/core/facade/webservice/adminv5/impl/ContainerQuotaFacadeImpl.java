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
package org.linagora.linshare.core.facade.webservice.adminv5.impl;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.Quota;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.impl.AdminGenericFacadeImpl;
import org.linagora.linshare.core.facade.webservice.adminv5.ContainerQuotaFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.ContainerQuotaDto;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ContainerQuotaService;
import org.linagora.linshare.core.service.DomainQuotaService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class ContainerQuotaFacadeImpl extends AdminGenericFacadeImpl implements ContainerQuotaFacade {

	private final ContainerQuotaService service;
	
	private final AbstractDomainService abstractDomainService;

	private final DomainQuotaService domainQuotaService;

	public ContainerQuotaFacadeImpl(
			final AccountService accountService,
			final ContainerQuotaService containerQuotaService,
			final AbstractDomainService abstractDomainService,
			final DomainQuotaService domainQuotaService) {
		super(accountService);
		this.service = containerQuotaService;
		this.abstractDomainService = abstractDomainService;
		this.domainQuotaService = domainQuotaService;
	}

	@Override
	public List<ContainerQuotaDto> findAll(String domainUuid, String quotaUuid, ContainerQuotaType type)
			throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(domainUuid, "domainUuid must be set.");
		Validate.notEmpty(quotaUuid, "quotaUuid must be set.");
		AbstractDomain domain = abstractDomainService.findById(domainUuid);
		Quota quota = domainQuotaService.find(authUser, quotaUuid);
		if (!isDomainBelonging(domain, quota)) {
			throw new BusinessException(BusinessErrorCode.CONTAINER_QUOTA_NOT_FOUND,
					"The requested quota does not belong to the entered domain, please check the entered information.");
		} else {
			List<ContainerQuota> containers = null;
			if (domainUuid != null) {
				containers = service.findAll(authUser, domain);
			} else {
				containers = service.findAll(authUser);
			}
			return ImmutableList.copyOf(Lists.transform(containers, ContainerQuotaDto.toDto()));
		}
	}

	@Override
	public ContainerQuotaDto find(String domainUuid, String quotaUuid, String uuid, boolean realTime)
			throws BusinessException {
		throw new BusinessException(BusinessErrorCode.NOT_IMPLEMENTED_YET, "Not implemented yet.");
	}

	private Boolean isDomainBelonging(AbstractDomain domain, Quota quota) {
		return domain.getUuid().equals(quota.getDomain().getUuid()) ? true : false;
	}

	@Override
	public ContainerQuotaDto update(String domainUuid, String quotaUuid, ContainerQuotaDto dto, String uuid)
			throws BusinessException {
		throw new BusinessException(BusinessErrorCode.NOT_IMPLEMENTED_YET, "Not implemented yet.");
	}
}
