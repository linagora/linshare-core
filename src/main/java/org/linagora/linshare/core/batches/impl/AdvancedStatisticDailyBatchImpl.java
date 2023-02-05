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
import org.linagora.linshare.core.utils.DocumentUtils;
import org.linagora.linshare.mongo.entities.MimeTypeStatistic;
import org.linagora.linshare.mongo.repository.AdvancedStatisticMongoRepository;
import org.linagora.linshare.utils.DocumentCount;

import com.google.common.collect.Maps;

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
		Map<String, MimeTypeStatistic> mimeTypeStatistics = Maps.newHashMap();
		String parentDomainUuid = null;
		AbstractDomain parentDomain = domain.getParentDomain();
		if (parentDomain != null) {
			parentDomainUuid = parentDomain.getUuid();
		}
		console.logInfo(batchRunContext, total, position, "processing domain : " + domain.toString());
		// Find total occurence of each mimeType on documentEntry
		List<DocumentCount> documents = documentEntryRepository.countAndGroupByMimeType(domain, getBeginDate(), getEndDate());
		addMimeTypes(domain, parentDomainUuid, documents, mimeTypeStatistics);

		// Find total occurrences of each mimeType on workGroupDocument
		List<String> workgroupsByDomains = threadRepository.findByDomainUuid(identifier);
		documents = workGroupNodeBusinessService.findTotalOccurenceOfMimeTypeByDomain(
				workgroupsByDomains, getBeginDate().getTime(), getEndDate().getTime());
		addMimeTypes(domain, parentDomainUuid, documents, mimeTypeStatistics);

		// Find total occurrences of each mimeType on uploadRequestEntry
		documents = uploadRequestEntryRepository.findByDomainsBetweenTwoDates(domain, getBeginDate(), getEndDate());
		addMimeTypes(domain, parentDomainUuid, documents, mimeTypeStatistics);

		if (!mimeTypeStatistics.isEmpty()) {
			advancedStatisticMongoRepository.insert(mimeTypeStatistics.values());
		}
		context.setProcessed(true);
		return context;
	}

	private void addMimeTypes(AbstractDomain domain, String parentDomainUuid,
			List<DocumentCount> documents, Map<String, MimeTypeStatistic> mimeTypeStatistics) {
		for (DocumentCount document : documents) {
			if (mimeTypeStatistics.containsKey(document.getMimeType())) {
				MimeTypeStatistic mimeTypeStatistic = mimeTypeStatistics.get(document.getMimeType());
				mimeTypeStatistic.addValue(document.getTotal());
				mimeTypeStatistic.addTotalSize(document.getTotalSize());
			}
			else {
				String humanMimeType = DocumentUtils.getHumanMimeType(document.getMimeType());
				logger.debug("mimeType: " + document.getMimeType());
				logger.debug("humanMimeType: " + humanMimeType);
				MimeTypeStatistic mimeTypeStatistic = new MimeTypeStatistic(
						document.getTotal(),
						document.getTotalSize(),
						parentDomainUuid,
						domain.getUuid(),
						document.getMimeType(),
						humanMimeType);
				mimeTypeStatistics.put(document.getMimeType(), mimeTypeStatistic);
			}
		}
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
