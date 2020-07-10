/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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
package org.linagora.linshare.core.batches.impl;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.linagora.linshare.core.business.service.BatchHistoryBusinessService;
import org.linagora.linshare.core.business.service.WorkGroupNodeBusinessService;
import org.linagora.linshare.core.domain.constants.BatchType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.DomainBatchResultContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.repository.ThreadRepository;
import org.linagora.linshare.core.repository.UploadRequestEntryRepository;
import org.linagora.linshare.mongo.entities.MimeTypeStatistic;
import org.linagora.linshare.mongo.repository.AdvancedStatisticMongoRepository;

import com.google.common.collect.Lists;

public class AdvancedStatisticDailyBatchImpl extends GenericBatchWithHistoryImpl {

	private final AbstractDomainRepository abstractDomainRepository;

	private final ThreadRepository threadRepository;

	private final DocumentEntryRepository documentEntryRepository;

	private final AdvancedStatisticMongoRepository advancedStatisticMongoRepository;

	private final UploadRequestEntryRepository uploadRequestEntryRepository;

	private final WorkGroupNodeBusinessService workGroupNodeBusinessService;

	public AdvancedStatisticDailyBatchImpl(AccountRepository<Account> accountRepository,
			BatchHistoryBusinessService batchHistoryBusinessService,
			AbstractDomainRepository abstractDomainRepository,
			ThreadRepository threadRepository,
			DocumentEntryRepository documentEntryRepository,
			AdvancedStatisticMongoRepository advancedStatisticMongoRepository,
			UploadRequestEntryRepository uploadRequestEntryRepository,
			WorkGroupNodeBusinessService workGroupNodeBusinessService) {
		super(accountRepository, batchHistoryBusinessService);
		this.abstractDomainRepository = abstractDomainRepository;
		this.threadRepository = threadRepository;
		this.documentEntryRepository = documentEntryRepository;
		this.advancedStatisticMongoRepository = advancedStatisticMongoRepository;
		this.uploadRequestEntryRepository = uploadRequestEntryRepository;
		this.workGroupNodeBusinessService = workGroupNodeBusinessService;
	}

	@Override
	public BatchType getBatchType() {
		return BatchType.DAILY_ADVANCED_STATISTIC_BATCH;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info(getClass().toString() + " job starting ...");
		List<String> domainUuids = abstractDomainRepository.findAllDomainIdentifiers();
		logger.info(domainUuids.size() + " domain uuid have been found");
		return domainUuids;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		AbstractDomain domain = abstractDomainRepository.findById(identifier);
		ResultContext context = new DomainBatchResultContext(domain);
		context.setProcessed(false);
		String parentDomainUuid = null;
		AbstractDomain parentDomain = domain.getParentDomain();
		if (parentDomain != null) {
			parentDomainUuid = parentDomain.getUuid();
		}
		console.logInfo(batchRunContext, total, position, "processing domain : " + domain.toString());
		// Find total occurence of each mimeType on documentEntry
		Map<String, Long> documentEntryMap = documentEntryRepository.countAndGroupByMimeType(domain, getBeginDate(),
				getEndDate());
		// Find total occurence of each mimeType on workGroupDocument
		List<String> workgroupsByDomains = threadRepository.findByDomainUuid(identifier);
		Map<String, Long> workGroupDocumentMap = workGroupNodeBusinessService.findTotalOccurenceOfMimeTypeByDomain(
				workgroupsByDomains, getBeginDate().getTime(), getEndDate().getTime());
		// Find total occurence of each mimeType on uploadRequestEntry
		Map<String, Long> uploadRequestEntryMap = uploadRequestEntryRepository.findByDomainsBetweenTwoDates(domain, getBeginDate(), getEndDate());
		@SuppressWarnings("unchecked")
		Map<String, Long> totalMerged = mergeMaps(workGroupDocumentMap, documentEntryMap, uploadRequestEntryMap);
		List<MimeTypeStatistic> mimeTypeStatistics = Lists.newArrayList();
		for (Map.Entry<String, Long> entry : totalMerged.entrySet()) {
			MimeTypeStatistic mimeTypeStatistic = new MimeTypeStatistic(entry.getValue(), parentDomainUuid,
					domain.getUuid(), entry.getKey());
			mimeTypeStatistics.add(mimeTypeStatistic);
		}
		if (!mimeTypeStatistics.isEmpty()) {
			advancedStatisticMongoRepository.insert(mimeTypeStatistics);
		}
		context.setProcessed(true);
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		DomainBatchResultContext domainContext = (DomainBatchResultContext) context;
		AbstractDomain domain = domainContext.getResource();
		console.logInfo(batchRunContext, total, position,
				"DailyDomainAdvancedStatistics : " + domain.getUuid() + " have been successfully created");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		DomainBatchResultContext context = (DomainBatchResultContext) exception.getContext();
		AbstractDomain domain = context.getResource();
		console.logError(batchRunContext, total, position, "creating DailyDomainAdvancedStatistic : " + domain.getUuid());
	}

	protected Calendar getBeginDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(GregorianCalendar.DATE, -1);
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		calendar.set(GregorianCalendar.MINUTE, 0);
		calendar.set(GregorianCalendar.SECOND, 0);
		calendar.set(GregorianCalendar.MILLISECOND, 0);
		return calendar;
	}

	protected Calendar getEndDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.add(Calendar.SECOND, 1);
		return calendar;
	}

	@SuppressWarnings("unchecked")
	protected Map<String, Long> mergeMaps(Map<String, Long>...documentEntryMap) {
		return Stream.of(documentEntryMap).map(Map::entrySet)
				.flatMap(Collection::stream)
				.collect(
						Collectors
						.toMap(Map.Entry::getKey,
								Map.Entry::getValue,
								Long::sum));
	}

}
