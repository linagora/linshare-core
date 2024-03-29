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
package org.linagora.linshare.core.upgrade.v4_2;

import java.util.List;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.ThreadRepository;
import org.linagora.linshare.core.service.WorkGroupDocumentRevisionService;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupDocumentRevision;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.repository.AuditUserMongoRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.linagora.linshare.mongo.repository.WorkGroupNodeMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class AddMissingPropertiesToWorkGroupNodesUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	private MongoTemplate mongoTemplate;

	private ThreadRepository threadRepository;

	private WorkGroupNodeMongoRepository workGroupNodeMongoRepository;
	
	private WorkGroupDocumentRevisionService revisionService;

	public AddMissingPropertiesToWorkGroupNodesUpgradeTaskImpl(AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			AuditUserMongoRepository auditUserMongoRepository,
			MongoTemplate mongoTemplate,
			ThreadRepository threadRepository,
			WorkGroupNodeMongoRepository workGroupNodeMongoRepository,
			WorkGroupDocumentRevisionService revisionService) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.mongoTemplate = mongoTemplate;
		this.threadRepository = threadRepository;
		this.workGroupNodeMongoRepository = workGroupNodeMongoRepository;
		this.revisionService = revisionService;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_4_2_ADD_MISSING_PROPERTIES_TO_WORK_GROUP_NODE;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		Query query = Query.query(Criteria.where("nodeType").is("DOCUMENT").and("hasThumbnail").exists(false).and("sha256sum").exists(false));
		return mongoTemplate.findDistinct(query, "uuid", WorkGroupDocument.class, String.class);
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		
		WorkGroupDocument copiedDocument = (WorkGroupDocument) workGroupNodeMongoRepository
				.findByUuid(identifier);
		BatchResultContext<WorkGroupNode> res = new BatchResultContext<>(copiedDocument);
		res.setProcessed(false);
		if (copiedDocument == null) {
			return res;
		}
		WorkGroup toWg = threadRepository.findByLsUuid(copiedDocument.getWorkGroup());
		if (toWg == null) {
			toWg = threadRepository.findDeleted(copiedDocument.getWorkGroup());
		}
		if (toWg == null) {
			logger.debug("Can not find the Thread/Workgroup, maybe it was removed. uuid=%s", copiedDocument.getWorkGroup());
			return res;
		}
		WorkGroupDocumentRevision mostRecent = (WorkGroupDocumentRevision) revisionService.findMostRecent(toWg,
				copiedDocument.getUuid());
		if (mostRecent == null) {
			res.setProcessed(false);
			logError(total, position, "WorkGroupDocumentRevision are missing for WorkGroupDocument: {}", batchRunContext, copiedDocument.getUuid());
		} else {
			copiedDocument.setSha256sum(mostRecent.getSha256sum());
			copiedDocument.setHasThumbnail(mostRecent.getHasThumbnail());
			workGroupNodeMongoRepository.save(copiedDocument);
			res.setProcessed(true);
		}
		return res;

	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<WorkGroupNode> res = (BatchResultContext<WorkGroupNode>) context;
		WorkGroupNode resource = res.getResource();
		logInfo(batchRunContext, total, position, resource + " has been updated.");
		if (res.getProcessed()) {
			logInfo(batchRunContext, total, position, "WorkGroupDocument has been updated: " + resource.toString());
		} else {
			logInfo(batchRunContext, total, position, "WorkGroupDocument has been skipped : " + resource.toString());
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<WorkGroupNode> res = (BatchResultContext<WorkGroupNode>) exception.getContext();
		WorkGroupNode resource = res.getResource();
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occured while processing WorkGroupDocument update : {} . BatchBusinessException", resource,
				exception);
	}

}
