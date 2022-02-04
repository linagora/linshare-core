/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2017-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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

package org.linagora.linshare.core.upgrade.v4_2;

import java.util.List;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
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
import org.linagora.linshare.core.repository.AccountQuotaRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.DomainQuotaRepository;
import org.linagora.linshare.core.repository.hibernate.ContainerQuotaRepositoryImpl;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;

public class ComputeCurrentValueForDomainsUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	protected AbstractDomainRepository abstractDomainRepository;

	protected AccountQuotaRepository accountQuotaRepository;

	protected ContainerQuotaRepositoryImpl containerQuotaRepository;

	protected DomainQuotaRepository domainQuotaRepository;

	public ComputeCurrentValueForDomainsUpgradeTaskImpl(AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			AbstractDomainRepository domainRepository,
			AccountQuotaRepository accountQuotaRepository,
			ContainerQuotaRepositoryImpl containerQuotaRepository,
			DomainQuotaRepository domainQuotaRepository) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.abstractDomainRepository = domainRepository;
		this.accountQuotaRepository = accountQuotaRepository;
		this.containerQuotaRepository = containerQuotaRepository;
		this.domainQuotaRepository = domainQuotaRepository;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_4_2_COMPUTE_CURRENT_VALUE_FOR_DOMAINS;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		List<String> domainIdentifiers = abstractDomainRepository.findAllDomainIdentifiers();
		return domainIdentifiers;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		AbstractDomain abstractDomain = abstractDomainRepository.findById(identifier);
		BatchResultContext<AbstractDomain> res = new BatchResultContext<AbstractDomain>(abstractDomain);
		console.logDebug(batchRunContext, total, position, "Processing domain : " + abstractDomain.toString());
		ContainerQuota containerQuotaWorkGroup = containerQuotaRepository.find(abstractDomain, ContainerQuotaType.WORK_GROUP);
		long currentQuotaWorkGroup = accountQuotaRepository.sumOfCurrentValue(containerQuotaWorkGroup);
		containerQuotaWorkGroup.setCurrentValue(currentQuotaWorkGroup);
		containerQuotaRepository.update(containerQuotaWorkGroup);
		ContainerQuota containerQuotaUser = containerQuotaRepository.find(abstractDomain, ContainerQuotaType.USER);
		long currentDomainQuota = containerQuotaUser.getCurrentValue() + currentQuotaWorkGroup;
		DomainQuota domainQuota = domainQuotaRepository.find(abstractDomain);
		domainQuota.setCurrentValue(currentDomainQuota);
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
