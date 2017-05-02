/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2016. Contribute to
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
			logInfo(batchRunContext, total, position, "processing domain : " + resource.toString());

			// creation of domain statistic for the past day using account statistic of the past day
			domainDailyStatBusinessService.create(resource, getYesterdayBegin(), getYesterdayEnd());

			//updating user quota with account quotas only updated this morning. I suppose this batch is run every morning. 
			ContainerQuota userContainerQuota = containerQuotaBusinessService.find(resource, ContainerQuotaType.USER);
			userContainerQuota = containerQuotaBusinessService.updateByBatch(userContainerQuota);

			//updating workgroup quota with account quotas only updated this morning. I suppose this batch is run every morning.
			ContainerQuota threadContainerQuota = containerQuotaBusinessService.find(resource, ContainerQuotaType.WORK_GROUP);
			threadContainerQuota = containerQuotaBusinessService.updateByBatch(threadContainerQuota);

			DomainQuota domainQuota = domainQuotaBusinessService.find(resource);
			domainQuota = domainQuotaBusinessService.updateByBatch(domainQuota);

		} catch (BusinessException businessException) {
			logError(total, position, "Error while trying to update domainQuota", batchRunContext);
			logger.info("Error occured while updating a domain quota for domain", businessException);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to update a domainQuota");
			exception.setBusinessException(businessException);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		DomainBatchResultContext domainContext = (DomainBatchResultContext) context;
		AbstractDomain domain = domainContext.getResource();
		logInfo(batchRunContext, total, position, "DailyDomainStatistics, ContainerQuota and DomainQuota of the domain : "
				+ domain.getUuid() + " have been successfully created");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		DomainBatchResultContext context = (DomainBatchResultContext) exception.getContext();
		AbstractDomain domain = context.getResource();
		logError(total, position,
				"creating DailyDomainStatistic, ContainerQuota and DomainQuota have failed for the domain : "
						+ domain.getUuid(), batchRunContext);
		logger.error("Error occured while creating DailyDomainStatistics, ContainerQuota and DomainQuota for a domain "
				+ domain.getUuid() + ". BatchBusinessException ", exception);
	}
}
