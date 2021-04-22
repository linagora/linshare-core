/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2021. Contribute to
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
package org.linagora.linshare.core.upgrade.v4_2;

import java.util.List;

import org.bson.Document;
import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Quota;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.repository.SharedSpaceNodeMongoRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.google.common.collect.Lists;
import com.mongodb.DBObject;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;

public class ComputeAllWorkgroupsQuotaImpl extends GenericUpgradeTaskImpl {

	protected SharedSpaceNodeMongoRepository repository;

	protected AccountQuotaBusinessService accountQuotaBusinessService;

	protected ThreadService threadService;

	protected OperationHistoryBusinessService operationHistoryBusinessService;

	protected MongoTemplate mongoTemplate;


	public ComputeAllWorkgroupsQuotaImpl(AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository, SharedSpaceNodeMongoRepository repository,
			AccountQuotaBusinessService accountQuotaBusinessService, ThreadService threadService,
			OperationHistoryBusinessService operationHistoryBusinessService, MongoTemplate mongoTemplate) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.repository = repository;
		this.accountQuotaBusinessService = accountQuotaBusinessService;
		this.threadService = threadService;
		this.operationHistoryBusinessService = operationHistoryBusinessService;
		this.mongoTemplate = mongoTemplate;

	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_4_2_COMPUTE_ALL_WORKGROUPS_QUOTA;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info("{} job is starting ...", getClass().toString());
		Query query = new Query();
		query.addCriteria(Criteria.where("nodeType").is("WORK_GROUP"));
		MongoCollection<Document> sharedSpaceNodes = mongoTemplate.getCollection("shared_space_nodes");
		DistinctIterable<String> results = sharedSpaceNodes.distinct("uuid", query.getQueryObject(), String.class);
		List<String> allUuids = Lists.newArrayList(results);
		logger.info("{} workgroups have been found.", allUuids.size());
		return allUuids;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		SharedSpaceNode sharedSpace = repository.findByUuid(identifier);
		WorkGroup workGroup = threadService.findByLsUuidUnprotected(identifier);
		BatchResultContext<SharedSpaceNode> res = new BatchResultContext<>(sharedSpace);
		res.setProcessed(false);
		if (workGroup == null || sharedSpace == null) {
			res.setIdentifier(identifier);
			return res;
		}
		//We compute the total size of documents inside the workgroups and substract the size of today operations
		//Today operations will be computed by the daily batch at midnight
		Long computedAllDocumentsSize = computeSizeAllDocumentsFromWorkGroup(identifier);
		Long computedAllTodayOperations = operationHistoryBusinessService.sumOperationValue(workGroup, workGroup.getDomain(), null, null, ContainerQuotaType.WORK_GROUP);
		Quota workgroupQuota = accountQuotaBusinessService.find(workGroup);
		workgroupQuota.setCurrentValue(computedAllDocumentsSize - computedAllTodayOperations);
		repository.save(sharedSpace);
		res.setProcessed(true);
		return res;

	}

	private Long computeSizeAllDocumentsFromWorkGroup(String workgroupUuid) {
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(new Criteria("workGroup").is(workgroupUuid).andOperator(
						new Criteria("nodeType").is(WorkGroupNodeType.DOCUMENT_REVISION))),
				Aggregation.group("workGroup").sum("size").as("totalSize"),
				Aggregation.project("totalSize").andExclude("_id"));
		AggregationResults<DBObject> resultDocument = mongoTemplate.aggregate(aggregation, WorkGroupDocument.class,
				DBObject.class);
		if ((null == resultDocument.getUniqueMappedResult()) || !Long.class.equals(resultDocument.getUniqueMappedResult().get("totalSize").getClass())) {
			return (long) 0;
		}
		Long result = (long) resultDocument.getUniqueMappedResult().get("totalSize");
		return result;
	}


	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		BatchResultContext<SharedSpaceNode> res = (BatchResultContext<SharedSpaceNode>) context;
		SharedSpaceNode resource = res.getResource();
		logInfo(batchRunContext, total, position, resource + " has been updated.");
		if (res.getProcessed()) {
			logInfo(batchRunContext, total, position,
					"SharedSpaceNode quota has been updated : " + resource.toString());
		} else {
			logInfo(batchRunContext, total, position,
					"SharedSpaceNode quota has been skipped : " + resource.toString());
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		BatchResultContext<SharedSpaceNode> res = (BatchResultContext<SharedSpaceNode>) exception.getContext();
		SharedSpaceNode resource = res.getResource();
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occured while processing SharedSpaceNode quota update : {} . BatchBusinessException",
				resource, exception);
	}


}
