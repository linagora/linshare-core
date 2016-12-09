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

import java.util.Date;
import java.util.GregorianCalendar;
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
import org.linagora.linshare.core.job.quartz.Context;
import org.linagora.linshare.core.job.quartz.DomainBatchResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.AbstractDomainService;

public class StatisticDailyDomainBatchImpl extends GenericBatchImpl {

	private final AccountQuotaBusinessService accountQuotaBusinessService;

	private final AbstractDomainService abstractDomainService;

	private final ContainerQuotaBusinessService containerQuotaBusinessService;

	private final DomainQuotaBusinessService domainQuotaBusinessService;

	private final DomainDailyStatBusinessService domainDailyStatBusinessService;

	private final BatchHistoryBusinessService batchHistoryBusinessService;

	public StatisticDailyDomainBatchImpl(
			final AccountRepository<Account> accountRepository,
			final AccountQuotaBusinessService accountQuotaBusinessService,
			final AbstractDomainService abstractDomainService,
			final ContainerQuotaBusinessService containerQuotaBusinessService,
			final DomainQuotaBusinessService domainQuotaBusinessService,
			final DomainDailyStatBusinessService domainDailyStatBusinessService,
			final BatchHistoryBusinessService batchHistoryBusinessService) {
		super(accountRepository);
		this.accountQuotaBusinessService = accountQuotaBusinessService;
		this.abstractDomainService = abstractDomainService;
		this.containerQuotaBusinessService = containerQuotaBusinessService;
		this.domainQuotaBusinessService = domainQuotaBusinessService;
		this.domainDailyStatBusinessService = domainDailyStatBusinessService;
		this.batchHistoryBusinessService = batchHistoryBusinessService;
	}

	@Override
	public List<String> getAll() {
		logger.info("DailyDomainBatchImpl job starting ...");
		// TODO is it between yesterday and now ? or yseterday 00h00 and today 00h00 ?
		List<String> domains = accountQuotaBusinessService.findDomainByBatchModificationDate(yesterday(), new Date());
		logger.info(domains.size() + " domain(s) have been found in accountQuota table and modified by batch today");
		return domains;
	}

	@Override
	public Context execute(String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		Date today = today();
		AbstractDomain resource = abstractDomainService.findById(identifier);
		Context context = new DomainBatchResultContext(resource);
		try {
			logInfo(total, position, "processing domain : " + resource.toString());
			domainDailyStatBusinessService.create(resource, today);
		} catch (BusinessException businessException) {
			logError(total, position, "Error while trying to create DomainDailyStat");
			logger.info("Error occured while creating daily statistics for domain", businessException);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to create a DomainDailyStat");
			exception.setBusinessException(businessException);
			throw exception;
		}
		try {
			ContainerQuota userContainerQuota = containerQuotaBusinessService.find(resource, ContainerQuotaType.USER);
			userContainerQuota = containerQuotaBusinessService.updateByBatch(userContainerQuota, today);
		} catch (BusinessException businessException) {
			logError(total, position, "Error while trying to update userContainerQuota");
			logger.info("Error occured while updating an user container quota for domain", businessException);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to update a userEnsebleQuota");
			exception.setBusinessException(businessException);
			throw exception;
		}
		try {
			ContainerQuota threadContainerQuota = containerQuotaBusinessService.find(resource, ContainerQuotaType.WORK_GROUP);
			threadContainerQuota = containerQuotaBusinessService.updateByBatch(threadContainerQuota, today);
		} catch (BusinessException businessException) {
			logError(total, position, "Error while trying to update threadContainerQuota");
			logger.info("Error occured while updating a thread container quota for domain", businessException);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to update a threadEnsebleQuota");
			exception.setBusinessException(businessException);
			throw exception;
		}
		try {
			DomainQuota domainQuota = domainQuotaBusinessService.find(resource);
			domainQuota = domainQuotaBusinessService.updateByBatch(domainQuota, today);
			// Workaround ? or not ?
			if (!resource.isRootDomain()) {
				long diffValue = domainQuota.getCurrentValue() - domainQuota.getLastValue();
				DomainQuota rootQuota = domainQuotaBusinessService.find(abstractDomainService.getUniqueRootDomain());
				rootQuota.setBatchModificationDate(new Date());
				rootQuota.setLastValue(rootQuota.getCurrentValue());
				rootQuota.setCurrentValue(rootQuota.getCurrentValue() + diffValue);
				domainQuotaBusinessService.update(rootQuota);
			}
		} catch (BusinessException businessException) {
			logError(total, position, "Error while trying to update domainQuota");
			logger.info("Error occured while updating a domain quota for domain", businessException);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to update a domainQuota");
			exception.setBusinessException(businessException);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(Context context, long total, long position) {
		DomainBatchResultContext domainContext = (DomainBatchResultContext) context;
		AbstractDomain domain = domainContext.getResource();
		logInfo(total, position, "DailyDomainStatistics, ContainerQuota and DomainQuota of the domain : "
				+ domain.getUuid() + " have been successfully created");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position) {
		DomainBatchResultContext context = (DomainBatchResultContext) exception.getContext();
		AbstractDomain domain = context.getResource();
		logError(total, position,
				"creating DailyDomainStatistic, ContainerQuota and DomainQuota have failed for the domain : "
						+ domain.getUuid());
		logger.error("Error occured while creating DailyDomainStatistics, ContainerQuota and DomainQuota for a domain "
				+ domain.getUuid() + ". BatchBusinessException ", exception);
	}

	@Override
	public void terminate(List<String> all, long errors, long unhandled_errors, long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " DailyDomainStatistic, ContainerQuota and DomainQuota for domain(s) have bean created.");
		if (errors > 0) {
			logger.info(errors
					+ "  DailyDomainStatistic, ContainerQuota and DomainQuota for domain(s) failed to be created");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors
					+ "  DailyDomainStatistic, ContainerQuota and DomainQuota for domain(s) failed to be created (unhandled error.)");
		}
		logger.info("DailyDomainBatchImpl job terminated");
	}

	private Date today() {
		GregorianCalendar dateCalender = new GregorianCalendar();
		dateCalender.set(GregorianCalendar.HOUR_OF_DAY, 0);
		dateCalender.set(GregorianCalendar.MINUTE, 0);
		dateCalender.set(GregorianCalendar.SECOND, 0);
		return dateCalender.getTime();
	}

	private Date yesterday() {
		GregorianCalendar dateCalender = new GregorianCalendar();
		dateCalender.add(GregorianCalendar.DATE, -1);
		dateCalender.set(GregorianCalendar.HOUR_OF_DAY, 23);
		dateCalender.set(GregorianCalendar.MINUTE, 59);
		dateCalender.set(GregorianCalendar.SECOND, 59);
		return dateCalender.getTime();
	}

	@Override
	public boolean needToRun() {
		return !batchHistoryBusinessService.exist(today(), null, BatchType.DAILY_DOMAIN_BATCH);
	}
}
