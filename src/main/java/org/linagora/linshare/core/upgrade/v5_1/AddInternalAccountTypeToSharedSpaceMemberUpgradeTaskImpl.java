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
package org.linagora.linshare.core.upgrade.v5_1;

import java.util.Arrays;
import java.util.List;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.batches.utils.FakeContext;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.client.result.UpdateResult;

public class AddInternalAccountTypeToSharedSpaceMemberUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	private final MongoTemplate mongoTemplate;

	public AddInternalAccountTypeToSharedSpaceMemberUpgradeTaskImpl(
			AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			MongoTemplate mongoTemplate) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_5_1_ADD_INTERNAL_ACCOUNT_TYPE_TO_SHARED_SPACE_MEMBER;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		return Arrays.asList("fakeUuid");
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		Query query = new Query();
		query.addCriteria(Criteria.where("account.accountType").exists(false));
		Update update = new Update();
		update.set("account.accountType", AccountType.INTERNAL);
		UpdateResult updateMulti = mongoTemplate.updateMulti(query, update, SharedSpaceMember.class);
		console.logInfo(batchRunContext, total, position, "updateMulti: " + updateMulti);
		BatchResultContext<FakeContext> res = new BatchResultContext<>(new FakeContext(identifier));
		res.setProcessed(true);
		return res;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<FakeContext> res = (BatchResultContext<FakeContext>) context;
		if (res.getProcessed()) {
			logInfo(batchRunContext, total, position, "Internal accountType was added succefully to sharedSpaceMember.");
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occured while adding Internal accountType to sharedSpaceMember", exception);
	}
}
