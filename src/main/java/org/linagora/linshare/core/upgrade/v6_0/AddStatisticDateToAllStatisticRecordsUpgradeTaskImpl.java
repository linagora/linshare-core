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
package org.linagora.linshare.core.upgrade.v6_0;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.batches.utils.FakeContext;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.mongo.entities.BasicStatistic;
import org.linagora.linshare.mongo.entities.MimeTypeStatistic;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.CloseableIterator;

public class AddStatisticDateToAllStatisticRecordsUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	private final MongoTemplate mongoTemplate;

	public AddStatisticDateToAllStatisticRecordsUpgradeTaskImpl(
			AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			MongoTemplate mongoTemplate) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_6_0_ADD_STATISTIC_DATE_TO_EXISTING_STAT_RECORDS;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		return Arrays.asList("fakeUuid");
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		BatchResultContext<FakeContext> res = new BatchResultContext<>(new FakeContext(identifier));
		res.setProcessed(false);

		updateMimeTypeStatistics(batchRunContext, total, position);
		updateBasicStatistic(batchRunContext, total, position);

		res.setProcessed(true);
		return res;
	}

	private void updateMimeTypeStatistics(BatchRunContext batchRunContext, long total, long position) {
		Query query = Query.query(Criteria.where("statisticDate").exists(false));
		long localTotal = mongoTemplate.count(query, MimeTypeStatistic.class);
		console.logInfo(batchRunContext, total, position, "Missing statisticDate in MimeTypeStatistic found: " + localTotal);
		CloseableIterator<MimeTypeStatistic> stream = mongoTemplate.stream(query, MimeTypeStatistic.class);
		AtomicInteger localPosition = new AtomicInteger(0);
		stream.forEachRemaining(entry -> {
			addStatisticDateField(batchRunContext, localPosition, localTotal, entry);
			}
		);
	}

	private void addStatisticDateField(BatchRunContext batchRunContext, AtomicInteger position, long total, MimeTypeStatistic entry) {
		if ((position.incrementAndGet() % 100) == 0) {
			console.logInfo(batchRunContext, total, position.get(), "addStatisticDateField ... " + entry.getType() + " : " + entry.getUuid());
		}
		entry.setStatisticDate(
			LocalDate.ofInstant(
				entry.getCreationDate().toInstant(),
				ZoneId.systemDefault()
			).toString()
		);
		mongoTemplate.save(entry);
	}

	private void updateBasicStatistic(BatchRunContext batchRunContext, long total, long position) {
		Query query = Query.query(Criteria.where("statisticDate").exists(false));
		long localTotal = mongoTemplate.count(query, BasicStatistic.class);
		console.logInfo(batchRunContext, total, position, "Missing statisticDate in MimeTypeStatistic found: " + localTotal);
		CloseableIterator<BasicStatistic> stream = mongoTemplate.stream(query, BasicStatistic.class);
		AtomicInteger localPosition = new AtomicInteger(0);
		stream.forEachRemaining(entry -> {
			addStatisticDateField(batchRunContext, localPosition, localTotal, entry);
			}
		);
	}

	private void addStatisticDateField(BatchRunContext batchRunContext, AtomicInteger position, long total, BasicStatistic entry) {
		if ((position.incrementAndGet() % 100) == 0) {
			console.logInfo(batchRunContext, total, position.get(), "addStatisticDateField ... " + entry.getType() + " : " + entry.getUuid());
		}
		entry.setStatisticDate(
				LocalDate.ofInstant(
						entry.getCreationDate().toInstant(),
						ZoneId.systemDefault()
						).toString()
				);
		mongoTemplate.save(entry);
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<FakeContext> res = (BatchResultContext<FakeContext>) context;
		if (res.getProcessed()) {
			logInfo(batchRunContext, total, position, "All Statistics were updated with statisticDate information.");
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occured while adding statisticDate information to all Statistics", exception);
	}
}
