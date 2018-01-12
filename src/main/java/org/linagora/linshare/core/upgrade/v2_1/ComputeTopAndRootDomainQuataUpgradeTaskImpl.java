/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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

package org.linagora.linshare.core.upgrade.v2_1;

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

public class ComputeTopAndRootDomainQuataUpgradeTaskImpl extends GenericUpgradeTaskImpl{

	protected AbstractDomainRepository abstractDomainRepository;

	protected DomainQuotaRepository domainQuotaRepository;

	public ComputeTopAndRootDomainQuataUpgradeTaskImpl(AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			AbstractDomainRepository abstractDomainRepository,
			DomainQuotaRepository domainQuotaRepository) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.abstractDomainRepository = abstractDomainRepository;
		this.domainQuotaRepository = domainQuotaRepository;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_2_1_COMPUTE_TOP_AND_ROOT_DOMAIN_QUOTA;
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
