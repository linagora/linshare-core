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
package org.linagora.linshare.core.upgrade.v4_2;

import java.util.List;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.DomainQuotaRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;

import com.google.common.collect.Lists;

public class ComputeTopAndRootDomainQuotaUpgradeTaskImpl extends GenericUpgradeTaskImpl{

	protected AbstractDomainRepository abstractDomainRepository;

	protected DomainQuotaRepository domainQuotaRepository;


	protected ComputeTopAndRootDomainQuotaUpgradeTaskImpl(AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			AbstractDomainRepository abstractDomainRepository, DomainQuotaRepository domainQuotaRepository) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.abstractDomainRepository = abstractDomainRepository;
		this.domainQuotaRepository = domainQuotaRepository;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_4_2_COMPUTE_TOP_AND_ROOT_DOMAIN_QUOTA;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		List<AbstractDomain> topdomains = abstractDomainRepository.findAllTopDomain();
		List<String> res = Lists.newArrayList();
		for (AbstractDomain abstractDomain : topdomains) {
			res.add(abstractDomain.getUuid());
		}
		res.add(abstractDomainRepository.getUniqueRootDomain().getUuid());
		return res;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		AbstractDomain abstractDomain = abstractDomainRepository.findById(identifier);
		BatchResultContext<AbstractDomain> res = new BatchResultContext<AbstractDomain>(abstractDomain);
		DomainQuota domainQuota = domainQuotaRepository.find(abstractDomain);
		long valueForSubdomains = domainQuotaRepository.sumOfCurrentValueForSubdomains(abstractDomain);
		domainQuota.setCurrentValueForSubdomains(valueForSubdomains);
		domainQuotaRepository.update(domainQuota);
		res.setProcessed(true);
		return res;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<AbstractDomain> res = (BatchResultContext<AbstractDomain>) context;
		AbstractDomain resource = res.getResource();
		if (res.getProcessed()) {
			logInfo(batchRunContext, total, position, resource + " has been updated.");
		} else {
			logInfo(batchRunContext, total, position, resource + " has been skipped.");
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<AbstractDomain> res = (BatchResultContext<AbstractDomain>) exception.getContext();
		AbstractDomain resource = res.getResource();
		console.logError(batchRunContext, total, position, "The upgrade task : " + resource + " failed.", batchRunContext);
		logger.error("Error occured while updating the DomainQuota : "
				+ resource +
				". BatchBusinessException", exception);
	}
}
