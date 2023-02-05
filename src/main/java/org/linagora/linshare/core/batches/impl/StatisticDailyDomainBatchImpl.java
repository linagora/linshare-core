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
package org.linagora.linshare.core.batches.impl;

import java.util.List;

import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.BatchHistoryBusinessService;
import org.linagora.linshare.core.business.service.ContainerQuotaBusinessService;
import org.linagora.linshare.core.business.service.DomainDailyStatBusinessService;
import org.linagora.linshare.core.business.service.DomainQuotaBusinessService;
import org.linagora.linshare.core.domain.constants.BatchType;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.DomainBatchResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.AbstractDomainService;

public class StatisticDailyDomainBatchImpl extends GenericBatchWithHistoryImpl {

	private final AccountQuotaBusinessService accountQuotaBusinessService;

	private final AbstractDomainService abstractDomainService;

	private final ContainerQuotaBusinessService containerQuotaBusinessService;

	private final DomainQuotaBusinessService domainQuotaBusinessService;

	private final DomainDailyStatBusinessService domainDailyStatBusinessService;

	public StatisticDailyDomainBatchImpl(
			final AccountRepository<Account> accountRepository,
			final AccountQuotaBusinessService accountQuotaBusinessService,
			final AbstractDomainService abstractDomainService,
			final ContainerQuotaBusinessService containerQuotaBusinessService,
			final DomainQuotaBusinessService domainQuotaBusinessService,
			final DomainDailyStatBusinessService domainDailyStatBusinessService,
			final BatchHistoryBusinessService batchHistoryBusinessService) {
		super(accountRepository, batchHistoryBusinessService);
		this.accountQuotaBusinessService = accountQuotaBusinessService;
		this.abstractDomainService = abstractDomainService;
		this.containerQuotaBusinessService = containerQuotaBusinessService;
		this.domainQuotaBusinessService = domainQuotaBusinessService;
		this.domainDailyStatBusinessService = domainDailyStatBusinessService;
	}

	@Override
	public BatchType getBatchType() {
		return BatchType.DAILY_DOMAIN_BATCH;
	}
	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		List<String> domains = accountQuotaBusinessService.findDomainUuidByBatchModificationDate(getYesterdayEnd());
		logger.info(domains.size() + " domain(s) have been found in accountQuota table and modified by batch today");
		return domains;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		AbstractDomain resource = abstractDomainService.findById(identifier);
		ResultContext context = new DomainBatchResultContext(resource);
		try {
			console.logInfo(batchRunContext, total, position, "processing domain : " + resource.toString());

			//updating user quota with account quotas only updated this morning. I suppose this batch is run every morning. 
			ContainerQuota userContainerQuota = containerQuotaBusinessService.find(resource, ContainerQuotaType.USER);
			userContainerQuota = containerQuotaBusinessService.sumOfCurrentValue(userContainerQuota);

			//updating workgroup quota with account quotas only updated this morning. I suppose this batch is run every morning.
			ContainerQuota threadContainerQuota = containerQuotaBusinessService.find(resource, ContainerQuotaType.WORK_GROUP);
			threadContainerQuota = containerQuotaBusinessService.sumOfCurrentValue(threadContainerQuota);

			DomainQuota domainQuota = domainQuotaBusinessService.find(resource);
			domainQuota = domainQuotaBusinessService.sumOfCurrentValue(domainQuota);

			// creation of domain statistic for the past day using account statistic of the past day
			domainDailyStatBusinessService.create(resource, domainQuota.getCurrentValue(), getYesterdayBegin(), getYesterdayEnd());

		} catch (BusinessException businessException) {
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to update a domainQuota");
			exception.setBusinessException(businessException);
			console.logError(batchRunContext, total, position, "Error while trying to update domainQuota", exception);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		DomainBatchResultContext domainContext = (DomainBatchResultContext) context;
		AbstractDomain domain = domainContext.getResource();
		console.logInfo(batchRunContext, total, position, "DailyDomainStatistics, ContainerQuota and DomainQuota of the domain : "
				+ domain.getUuid() + " have been successfully created");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		DomainBatchResultContext context = (DomainBatchResultContext) exception.getContext();
		AbstractDomain domain = context.getResource();
		console.logError(batchRunContext, total, position,
				"creating DailyDomainStatistic, ContainerQuota and DomainQuota have failed for the domain : "
						+ domain.getUuid());
		}
}
