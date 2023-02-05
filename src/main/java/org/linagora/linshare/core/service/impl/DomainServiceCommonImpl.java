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
package org.linagora.linshare.core.service.impl;

import org.linagora.linshare.core.business.service.ContainerQuotaBusinessService;
import org.linagora.linshare.core.business.service.DomainQuotaBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.exception.BusinessException;

public class DomainServiceCommonImpl extends GenericAdminServiceImpl {

	protected final DomainQuotaBusinessService domainQuotaBusinessService;
	protected final ContainerQuotaBusinessService containerQuotaBusinessService;

	public DomainServiceCommonImpl(
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
			DomainQuotaBusinessService domainQuotaBusinessService,
			ContainerQuotaBusinessService containerQuotaBusinessService) {
		super(sanitizerInputHtmlBusinessService);
		this.domainQuotaBusinessService = domainQuotaBusinessService;
		this.containerQuotaBusinessService = containerQuotaBusinessService;
	}

	protected void createDomainQuotaAndContainerQuota(AbstractDomain domain) throws BusinessException {
		AbstractDomain parentDomain = domain.getParentDomain();
		boolean isSubdomain = false;
		if (domain.getDomainType().equals(DomainType.SUBDOMAIN) || domain.isGuestDomain()) {
			isSubdomain = true;
		}
		// Quota for the new domain
		DomainQuota parentDomainQuota = domainQuotaBusinessService.find(parentDomain);
		DomainQuota domainQuota = new DomainQuota(parentDomainQuota, domain);
		if (isSubdomain) {
			domainQuota.setDefaultQuota(null);
			domainQuota.setDefaultQuotaOverride(null);
			domainQuota.setDefaultDomainShared(null);
			domainQuota.setDefaultDomainSharedOverride(null);
		}
		domainQuotaBusinessService.create(domainQuota);
		// Quota containers for the new domain.
		for (ContainerQuota parentContainerQuota : containerQuotaBusinessService.findAll(parentDomain)) {
			ContainerQuota cq = new ContainerQuota(domain, parentDomain, domainQuota, parentContainerQuota);
			if (isSubdomain) {
				cq.setDefaultQuota(null);
				cq.setDefaultQuotaOverride(null);
			}
			containerQuotaBusinessService.create(cq);
		}
	}

}