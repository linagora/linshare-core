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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.impl.AdminGenericFacadeImpl;
import org.linagora.linshare.core.facade.webservice.adminv5.DomainQuotaFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainQuotaDto;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DomainQuotaService;
import org.linagora.linshare.core.service.QuotaService;

import com.google.common.base.Strings;

public class DomainQuotaFacadeImpl extends AdminGenericFacadeImpl implements DomainQuotaFacade {

	private final DomainQuotaService service;
	private final QuotaService quotaService;
	private final AbstractDomainService abstractDomainService;

	public DomainQuotaFacadeImpl(
			final AccountService accountService,
			final DomainQuotaService domainQuotaService,
			final QuotaService quotaService,
			final AbstractDomainService abstractDomainService) {
		super(accountService);
		this.service = domainQuotaService;
		this.quotaService = quotaService;
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	public DomainQuotaDto find(String uuid, boolean realTime) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notNull(uuid, "Domain quota uuid must be set.");
		DomainQuota quota = service.find(authUser, uuid);
		DomainQuotaDto dto = DomainQuotaDto.from(quota);
		dto = checkRealTime(realTime, authUser, quota);
		return dto;
	}

	private DomainQuotaDto checkRealTime(boolean realTime, User authUser, DomainQuota quota) {
		Optional<Long> usedSpace = Optional.empty();
		Optional<Long> currentValueForSubdomains = Optional.empty();
		if (realTime) {
			usedSpace = Optional.of(quotaService.getRealTimeUsedSpace(authUser, authUser, quota));
			if (quota.getDomain().isRootDomain()) {
				currentValueForSubdomains = Optional.of(quotaService.getTodayUsedSpace(authUser, authUser));
			} else if (quota.getDomain().isTopDomain()) {
				Set<AbstractDomain> subdomain = quota.getDomain().getSubdomain();
				for (AbstractDomain domain : subdomain) {
					DomainQuota domainQuota = quotaService.find(domain);
					currentValueForSubdomains = Optional.of(currentValueForSubdomains.orElse(0L)
							+ quotaService.getRealTimeUsedSpace(authUser, authUser, domainQuota));
				}
			}
		}
		return DomainQuotaDto.from(quota, usedSpace, currentValueForSubdomains);
	}

	@Override
	public List<DomainQuotaDto> findAll(String domainUuid) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		AbstractDomain domain = abstractDomainService.findById(domainUuid);
		List<DomainQuota> domainQuotas = service.findAllByDomain(authUser, domain);
		return domainQuotas
				.stream()
				.map(DomainQuotaDto::from)
				.collect(Collectors.toUnmodifiableList());
	}

	private AbstractDomain checkDomainBelonging(User authUser, String domainUuid, String domainQuotaUuid) {
		AbstractDomain domain = abstractDomainService.findById(domainUuid);
		DomainQuota domainContainerQuota = service.find(authUser, domainQuotaUuid);
		if (!domain.getUuid().equals(domainContainerQuota.getDomain().getUuid())) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_QUOTA_NOT_FOUND,
					"The requested quota does not belong to the entered domain, please check the entered information.");
		}
		return domain;
	}

	@Override
	public DomainQuotaDto update(String domainUuid, DomainQuotaDto dto, String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notNull(dto, "DomainQuotaDto must be set.");
		if (Strings.isNullOrEmpty(uuid)) {
			uuid = dto.getUuid();
		}
		Validate.notEmpty(uuid, "Domain quota uuid must be set.");
		checkDomainBelonging(authUser, domainUuid, uuid);
		DomainQuota domainQuota = dto.toObject(Optional.of(uuid));
		domainQuota = service.update(authUser, domainQuota);
		return DomainQuotaDto.from(domainQuota);
	}

}
