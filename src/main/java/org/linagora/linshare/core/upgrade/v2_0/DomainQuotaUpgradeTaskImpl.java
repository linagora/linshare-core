/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2017. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.upgrade.v2_0;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.business.service.ContainerQuotaBusinessService;
import org.linagora.linshare.core.business.service.DomainQuotaBusinessService;
import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;

public abstract class DomainQuotaUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	protected AbstractDomainRepository repository;

	protected DomainQuotaBusinessService domainQuotaBusinessService;

	protected ContainerQuotaBusinessService containerQuotaBusinessService;

	public DomainQuotaUpgradeTaskImpl(
			AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			DomainQuotaBusinessService domainQuotaBusinessService,
			ContainerQuotaBusinessService containerQuotaBusinessService,
			AbstractDomainRepository repository) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.repository = repository;
		this.domainQuotaBusinessService = domainQuotaBusinessService;
		this.containerQuotaBusinessService = containerQuotaBusinessService;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		AbstractDomain domain = repository.findById(identifier);
		BatchResultContext<AbstractDomain> res = new BatchResultContext<AbstractDomain>(domain);
		DomainQuota quota = domainQuotaBusinessService.find(domain);
		if (quota != null) {
			res.setProcessed(false);
			logger.debug("Quota already defined. domain skipped :" + domain.toString());
			return res;
		}
		AbstractDomain parentDomain = domain.getParentDomain();
		boolean isSubdomain = domain.getDomainType().equals(DomainType.SUBDOMAIN);
		// Quota for the new domain
		DomainQuota parentDomainQuota = domainQuotaBusinessService.find(parentDomain);
		if (parentDomainQuota == null) {
			logError(total, position, "Can not init quota for the current domain : " + domain.toString() + " failed.", batchRunContext);
			logError(total, position, "Missing parent domain quota : " + parentDomain.toString() + " failed.", batchRunContext);
			throw new BatchBusinessException(res, "Can not init quota for the current domain");
		}
		DomainQuota domainQuota = new DomainQuota(parentDomainQuota, domain);
		if (isSubdomain) {
			domainQuota.setDefaultQuota(null);
			domainQuota.setDefaultQuotaOverride(null);
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
		res.setProcessed(true);
		return res;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<AbstractDomain> res = (BatchResultContext<AbstractDomain>) context;
		AbstractDomain resource = res.getResource();
		logInfo(batchRunContext, total, position, "The domain quota of " + resource.toString() + " has been successfully created.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<AbstractDomain> res = (BatchResultContext<AbstractDomain>) exception.getContext();
		AbstractDomain resource = res.getResource();
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occured while processing domain : "
				+ resource.toString() +
				". BatchBusinessException", exception);
	}
}
