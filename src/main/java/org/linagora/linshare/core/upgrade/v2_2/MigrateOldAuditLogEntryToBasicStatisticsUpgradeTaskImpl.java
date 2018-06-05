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

package org.linagora.linshare.core.upgrade.v2_2;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.BasicStatisticType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.BasicStatisticService;
import org.linagora.linshare.mongo.entities.BasicStatistic;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntry;
import org.linagora.linshare.mongo.repository.AuditAdminMongoRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import com.google.common.collect.Lists;

public class MigrateOldAuditLogEntryToBasicStatisticsUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	protected AbstractDomainRepository abstractDomainRepository;

	protected AuditAdminMongoRepository auditUserMongoRepository;

	protected BasicStatisticService basicStatisticService;

	protected DomainBusinessService domainBusinessService;

	public MigrateOldAuditLogEntryToBasicStatisticsUpgradeTaskImpl(
			AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			AbstractDomainRepository domainRepository,
			AuditAdminMongoRepository auditUserMongoRepository, 
			BasicStatisticService basicStatisticService,
			DomainBusinessService domainBusinessService) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.abstractDomainRepository = domainRepository;
		this.auditUserMongoRepository = auditUserMongoRepository;
		this.basicStatisticService = basicStatisticService;
		this.domainBusinessService = domainBusinessService;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_2_2_GENERATE_BASIC_STATISTICS_FROM_AUDIT_LOG_ENTRIES;
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
		Date endDate = basicStatisticService.getFirstStatisticCreationDate();
		console.logDebug(batchRunContext, total, position, "Processing domain : " + abstractDomain.toString());
		List<AuditLogEntry> auditLogEntryList = auditUserMongoRepository.findAllBeforeDateByDomainUuid(identifier,
				endDate);
		 String  parentDomainUuid = null;
		AbstractDomain parentDomain = abstractDomain.getParentDomain();
		if (parentDomain != null) {
			parentDomainUuid = parentDomain.getUuid();
		}
		List<BasicStatistic> basicStatisticsList = Lists.newArrayList();
		for (AuditLogEntry auditLogEntry : auditLogEntryList) {
			basicStatisticsList
					.add(new BasicStatistic(1L, parentDomainUuid, auditLogEntry, BasicStatisticType.ONESHOT));
		}
		basicStatisticService.insert(basicStatisticsList);
		Date beginDate = getBeginDate(endDate);
		while (basicStatisticService.countBeforeDate(endDate) != 0L) {
			List<BasicStatistic> dailyBasicStatisticList = Lists.newArrayList();
			for (AuditLogEntryType resourceType : AuditLogEntryType.values()) {
				for (LogAction action : LogAction.values()) {
					Long value = basicStatisticService.countBasicStatistic(identifier, action, beginDate, endDate,
							resourceType, BasicStatisticType.ONESHOT);
					if (value != 0L) {
						dailyBasicStatisticList.add(new BasicStatistic(value, identifier, parentDomainUuid, action,
								endDate, resourceType, BasicStatisticType.DAILY));
					}
				}
			}
			if (!dailyBasicStatisticList.isEmpty()) {
				basicStatisticService.insert(dailyBasicStatisticList);
			}
			endDate = decrementEndDate(endDate);
			beginDate = decrementBeginDate(beginDate);
		}
		res.setProcessed(true);
		return res;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<AbstractDomain> res = (BatchResultContext<AbstractDomain>) context;
		AbstractDomain resource = res.getResource();
		if (res.getProcessed()) {
			logInfo(batchRunContext, total, position, resource + " has been created.");
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
		console.logError(batchRunContext, total, position, "The upgrade task : " + resource + " failed.",
				batchRunContext);
		logger.error("Error occured while creating basicStatistic: " + resource + ". BatchBusinessException",
				exception);
	}

	protected Date decrementBeginDate(Date beginDate) {
		Calendar beginCalendar = Calendar.getInstance();
		beginCalendar.setTime(beginDate);
		beginCalendar.add(GregorianCalendar.DATE, -1);
		beginCalendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		beginCalendar.set(GregorianCalendar.MINUTE, 0);
		beginCalendar.set(GregorianCalendar.SECOND, 0);
		beginCalendar.set(GregorianCalendar.MILLISECOND, 0);
		return beginCalendar.getTime();
	}

	protected Date decrementEndDate(Date endDate) {
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(endDate);
		endCalendar.add(GregorianCalendar.DATE, -1);
		endCalendar.set(GregorianCalendar.HOUR_OF_DAY, 23);
		endCalendar.set(GregorianCalendar.MINUTE, 59);
		endCalendar.set(GregorianCalendar.SECOND, 58);
		endCalendar.set(GregorianCalendar.MILLISECOND, 999);
		return endCalendar.getTime();
	}

	protected Date getBeginDate(Date endDate) {
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(endDate);
		endCalendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		endCalendar.set(GregorianCalendar.MINUTE, 0);
		endCalendar.set(GregorianCalendar.SECOND, 0);
		endCalendar.set(GregorianCalendar.MILLISECOND, 0);
		return endCalendar.getTime();
	}
}
