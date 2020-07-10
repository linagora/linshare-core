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
package org.linagora.linshare.core.upgrade.v2_2;

import java.util.List;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.logs.SharedSpaceNodeAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.ThreadAuditLogEntry;
import org.linagora.linshare.mongo.repository.AuditUserMongoRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class MigrateWorkGroupAuditToSharedSpaceNodeUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	protected MongoTemplate mongoTemplate;

	protected AuditUserMongoRepository auditUserMongoRepository;

	public MigrateWorkGroupAuditToSharedSpaceNodeUpgradeTaskImpl(AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			MongoTemplate mongoTemplate,
			AuditUserMongoRepository auditUserMongoRepository) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.mongoTemplate = mongoTemplate;
		this.auditUserMongoRepository = auditUserMongoRepository;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_2_2_MIGRATE_WORKGROUP_AUDIT_TO_SHARED_SPACE_AUDIT;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		List<String> workGroupsAudits = findAllWorkGroupsAudit();
		return workGroupsAudits;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("uuid").is(identifier)));
		ThreadAuditLogEntry threadAudit = mongoTemplate
				.aggregate(aggregation, "audit_log_entries", ThreadAuditLogEntry.class).getUniqueMappedResult();
		if (threadAudit == null) {
			return null;
		}
		BatchResultContext<ThreadAuditLogEntry> res = new BatchResultContext<ThreadAuditLogEntry>(threadAudit);
		console.logDebug(batchRunContext, total, position, "Processing audit : " + threadAudit.toString());
		res.setProcessed(false);
		if (AuditLogEntryType.WORKGROUP.equals(threadAudit.getType())) {
			SharedSpaceNode resource = new SharedSpaceNode(threadAudit.getResource().getName(),
					threadAudit.getResource().getUuid(), NodeType.WORK_GROUP, threadAudit.getCreationDate(),
					threadAudit.getCreationDate());
			SharedSpaceNodeAuditLogEntry ssnAuditLogEntry = new SharedSpaceNodeAuditLogEntry();
			if (LogAction.UPDATE.equals(threadAudit.getAction())) {
				SharedSpaceNode resourceUpdated = new SharedSpaceNode(threadAudit.getResourceUpdated().getName(),
						threadAudit.getResourceUpdated().getUuid(), NodeType.WORK_GROUP, threadAudit.getCreationDate(),
						null);
				ssnAuditLogEntry.setResourceUpdated(resourceUpdated);
			}
			ssnAuditLogEntry.setResource(resource);
			ssnAuditLogEntry.setResourceUuid(threadAudit.getResourceUuid());
			ssnAuditLogEntry.setActor(threadAudit.getActor());
			ssnAuditLogEntry.setAuthUser(threadAudit.getAuthUser());
			ssnAuditLogEntry.setCreationDate(threadAudit.getCreationDate());
			ssnAuditLogEntry.setAction(threadAudit.getAction());
			ssnAuditLogEntry.setType(threadAudit.getType());
			auditUserMongoRepository.insert(ssnAuditLogEntry);
			auditUserMongoRepository.delete(threadAudit);
			res.setProcessed(true);
		}
		return res;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<ThreadAuditLogEntry> res = (BatchResultContext<ThreadAuditLogEntry>) context;
		ThreadAuditLogEntry resource = res.getResource();
		logInfo(batchRunContext, total, position, resource + " has been updated.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<ThreadAuditLogEntry> res = (BatchResultContext<ThreadAuditLogEntry>) exception.getContext();
		ThreadAuditLogEntry resource = res.getResource();
		console.logError(batchRunContext, total, position, "The upgrade task : " + resource + " failed",
				batchRunContext);
		logger.error("Error occured while migrating the threadAudit : " + resource, exception);
	}

	private List<String> findAllWorkGroupsAudit() {
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("type").is("WORKGROUP").and("_class")
						.is("org.linagora.linshare.mongo.entities.logs.ThreadAuditLogEntry")),
				Aggregation.project("uuid"));
		List<ThreadAuditLogEntry> results = mongoTemplate
				.aggregate(aggregation, "audit_log_entries", ThreadAuditLogEntry.class).getMappedResults();
		return Lists.transform(results, new Function<ThreadAuditLogEntry, String>() {
			@Override
			public String apply(ThreadAuditLogEntry threadAuditLogEntry) {
				return threadAuditLogEntry.getUuid();
			}
		});
	}
}
