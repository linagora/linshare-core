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

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.mongo.entities.logs.ShareEntryAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.google.common.collect.Lists;

public class UpdateSystmeAccountToAnonymousOnAuditTraces extends GenericUpgradeTaskImpl {

	private MongoTemplate mongoTemplate;

	public UpdateSystmeAccountToAnonymousOnAuditTraces(
			AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			MongoTemplate mongoTemplate) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_4_2_UPDATE_SYSTEM_TO_ANONYMOUS_ACCOUNT_ON_AUDIT_TRACES;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		return Lists.newArrayList("Fake returned string list");
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		Account anonymousAccount = accountRepository.getAnonymousShareSystemAccount();
		BatchResultContext<Account> res = new BatchResultContext<>(anonymousAccount);
		res.setProcessed(false);
		Validate.notNull(anonymousAccount, "This upgrade task can not be executed if the system anonymous share account not found.");
		Query query = new Query();
		query.addCriteria(new Criteria().andOperator(
				Criteria.where("type").is(AuditLogEntryType.ANONYMOUS_SHARE_ENTRY.toString()),
				Criteria.where("action").is(LogAction.DOWNLOAD.toString()),
				Criteria.where("authUser.uuid").ne(anonymousAccount.getLsUuid())));
		logger.info("{} ShareEntryAuditLogEntries have been found.", mongoTemplate.count(query,
				ShareEntryAuditLogEntry.class));
		Update update = new Update().set("authUser", new AccountMto(anonymousAccount));
		mongoTemplate.updateMulti(query, update, ShareEntryAuditLogEntry.class);
		res.setProcessed(true);
		return res;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<Account> res = (BatchResultContext<Account>) context;
		Account resource = res.getResource();
		logInfo(batchRunContext, total, position, resource + " has been updated.");
		if (res.getProcessed()) {
			logInfo(batchRunContext, total, position,
					"ShareEntryAuditLogEntries have been updated with the new anonymous acccount: " + resource.toString());
		} else {
			logInfo(batchRunContext, total, position,
					"ShareEntryAuditLogEntries have been skipped : " + resource.toString());
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<Account> res = (BatchResultContext<Account>) exception.getContext();
		Account resource = res.getResource();
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occured while processing shareEntryAuditLogEntries update : {} . BatchBusinessException",
				resource, exception);
	}
}
