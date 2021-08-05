package org.linagora.linshare.core.service.impl;

import org.linagora.linshare.core.business.service.ContainerQuotaBusinessService;
import org.linagora.linshare.core.business.service.DomainQuotaBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.exception.BusinessException;

public class DomainServiceCommonImp extends GenericAdminServiceImpl {

	protected final DomainQuotaBusinessService domainQuotaBusinessService;
	protected final ContainerQuotaBusinessService containerQuotaBusinessService;

	public DomainServiceCommonImp(
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