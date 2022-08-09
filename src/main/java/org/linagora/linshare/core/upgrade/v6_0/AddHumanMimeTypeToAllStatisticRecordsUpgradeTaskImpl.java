/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2022. Contribute to
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
package org.linagora.linshare.core.upgrade.v6_0;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
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
import org.linagora.linshare.mongo.entities.MimeTypeStatistic;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.linagora.linshare.webservice.utils.StatisticServiceUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.google.common.collect.Lists;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;

public class AddHumanMimeTypeToAllStatisticRecordsUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	private final MongoTemplate mongoTemplate;

	public AddHumanMimeTypeToAllStatisticRecordsUpgradeTaskImpl(
			AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			MongoTemplate mongoTemplate) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_6_0_ADD_HUMAN_MIME_TYPE_TO_EXISTING_STAT_RECORDS;
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
		updateAllMimeTypeStatistics(batchRunContext, total, position);
		updateHumanMimeTypeStatistics(batchRunContext);
		updateAllWorkGroupNodes(batchRunContext, total, position);
		updateHumanWorkGroupNodes(batchRunContext);
		res.setProcessed(true);
		return res;
	}

	private void updateHumanMimeTypeStatistics(BatchRunContext batchRunContext) {
		MongoCollection<Document> collection = mongoTemplate.getCollection(mongoTemplate.getCollectionName(MimeTypeStatistic.class));
		DistinctIterable<String> distincts = collection.distinct("mimeType", String.class);
		ArrayList<String> mimeTypes = Lists.newArrayList(distincts);
		int mimeTypeSize = mimeTypes.size();
		logger.debug("mimeType:size:" + mimeTypeSize);
		long position = 0;
		for (String mimeType : distincts) {
			logger.debug("mimeType:" + mimeType);
			String humanMimeType = StatisticServiceUtils.getHumanMimeType(mimeType);
			if (!humanMimeType.equals("others")) {
				Query query = new Query();
				query.addCriteria(Criteria.where("mimeType").is(mimeType));
				long localTotal = mongoTemplate.count(query, MimeTypeStatistic.class);
				console.logInfo(batchRunContext, mimeTypeSize, position, "Nb elements for mimeType '" + mimeType + "' in MimeTypeStatistic found: " + localTotal);
				Update update = new Update();
				update.set("humanMimeType", humanMimeType);
				UpdateResult updateMulti = mongoTemplate.updateMulti(query, update, MimeTypeStatistic.class);
				console.logInfo(batchRunContext, mimeTypeSize, position, "updateMulti: humanMimeType: " + updateMulti);
				position++;
			}
		}
	}

	private void updateAllMimeTypeStatistics(BatchRunContext batchRunContext, long total, long position) {
		Query query = new Query();
		query.addCriteria(Criteria.where("humanMimeType").exists(false));
		long localTotal = mongoTemplate.count(query, MimeTypeStatistic.class);
		console.logInfo(batchRunContext, total, position, "Missing humanMimeType in MimeTypeStatistic found: " + localTotal);
		Update update = new Update();
		update.set("humanMimeType", "others");
		update.set("totalSize", 0);
		UpdateResult updateMulti = mongoTemplate.updateMulti(query, update, MimeTypeStatistic.class);
		console.logInfo(batchRunContext, total, position, "updateMulti: " + updateMulti);
	}

	private void updateAllWorkGroupNodes(BatchRunContext batchRunContext, long total, long position) {
		Query query = new Query();
		query.addCriteria(Criteria.where("humanMimeType").exists(false));
		long localTotal = mongoTemplate.count(query, WorkGroupNode.class);
		console.logInfo(batchRunContext, total, position, "Missing humanMimeType in WorkGroupNode found: " + localTotal);
		Update update = new Update();
		update.set("humanMimeType", "others");
		UpdateResult updateMulti = mongoTemplate.updateMulti(query, update, WorkGroupNode.class);
		console.logInfo(batchRunContext, total, position, "updateMulti: " + updateMulti);
	}

	private void updateHumanWorkGroupNodes(BatchRunContext batchRunContext) {
		MongoCollection<Document> collection = mongoTemplate.getCollection(mongoTemplate.getCollectionName(WorkGroupNode.class));
		DistinctIterable<String> distincts = collection.distinct("mimeType", String.class);
		ArrayList<String> mimeTypes = Lists.newArrayList(distincts);
		int mimeTypeSize = mimeTypes.size();
		logger.debug("mimeType:size:" + mimeTypeSize);
		long position = 0;
		for (String mimeType : distincts) {
			logger.debug("mimeType:" + mimeType);
			String humanMimeType = StatisticServiceUtils.getHumanMimeType(mimeType);
			if (!humanMimeType.equals("others")) {
				Query query = new Query();
				query.addCriteria(Criteria.where("mimeType").is(mimeType));
				long localTotal = mongoTemplate.count(query, MimeTypeStatistic.class);
				console.logInfo(batchRunContext, mimeTypeSize, position, "Nb elements for mimeType '" + mimeType + "' in WorkGroupNode found: " + localTotal);
				Update update = new Update();
				update.set("humanMimeType", humanMimeType);
				UpdateResult updateMulti = mongoTemplate.updateMulti(query, update, WorkGroupNode.class);
				console.logInfo(batchRunContext, mimeTypeSize, position, "updateMulti: humanMimeType: " + updateMulti);
				position++;
			}
		}
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
