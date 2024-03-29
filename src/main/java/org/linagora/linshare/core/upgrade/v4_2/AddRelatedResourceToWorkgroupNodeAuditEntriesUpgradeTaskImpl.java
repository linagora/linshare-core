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

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.mongo.entities.logs.WorkGroupNodeAuditLogEntry;
import org.linagora.linshare.mongo.repository.AuditUserMongoRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class AddRelatedResourceToWorkgroupNodeAuditEntriesUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	private MongoTemplate mongoTemplate;
	
	private AuditUserMongoRepository auditUserMongoRepository;
	
	public AddRelatedResourceToWorkgroupNodeAuditEntriesUpgradeTaskImpl(AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			MongoTemplate mongoTemplate,
			AuditUserMongoRepository auditUserMongoRepository) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.mongoTemplate = mongoTemplate;
		this.auditUserMongoRepository = auditUserMongoRepository;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_4_2_ADD_WORKGROUP_UUID_AS_RELATED_RESOURCE_IN_WORKGROUP_NODE_AUDIT_TRACES;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		Query query = Query.query(Criteria.where("type").in(Arrays.asList(AuditLogEntryType.WORKGROUP_FOLDER,
				AuditLogEntryType.WORKGROUP_DOCUMENT, AuditLogEntryType.WORKGROUP_DOCUMENT_REVISION)));
		return mongoTemplate.findDistinct(query, "uuid", WorkGroupNodeAuditLogEntry.class, String.class);
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		WorkGroupNodeAuditLogEntry audit = mongoTemplate.findOne(Query.query(Criteria.where("uuid").is(identifier)),
				WorkGroupNodeAuditLogEntry.class);
		BatchResultContext<WorkGroupNodeAuditLogEntry> res = new BatchResultContext<>(audit);
		res.setProcessed(false);
		if (audit != null) {
			Set<String> relatedResources = audit.getRelatedResources();
			if (relatedResources != null && relatedResources.contains(audit.getWorkGroup().getUuid())) {
				//avoid processing if workgroup uuid already exists in the relatedResource list 
				return res;
			}
			audit.addRelatedResources(audit.getWorkGroup().getUuid());
			auditUserMongoRepository.save(audit);
			res.setProcessed(true);
		}
		return res;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<WorkGroupNodeAuditLogEntry> res = (BatchResultContext<WorkGroupNodeAuditLogEntry>) context;
		WorkGroupNodeAuditLogEntry resource = res.getResource();
		logInfo(batchRunContext, total, position, resource + " has been updated.");
		if (res.getProcessed()) {
			logInfo(batchRunContext, total, position,
					"WorkGroupNodeAuditLogEntry has been updated: " + resource.toString());
		} else {
			logInfo(batchRunContext, total, position,
					"WorkGroupNodeAuditLogEntry has been skipped : " + resource.toString());
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<WorkGroupNodeAuditLogEntry> res = (BatchResultContext<WorkGroupNodeAuditLogEntry>) exception.getContext();
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occured while processing WorkGroupNodeAuditLogEntry update : {} . BatchBusinessException",
				res.getResource()
				, exception);
	}

}
