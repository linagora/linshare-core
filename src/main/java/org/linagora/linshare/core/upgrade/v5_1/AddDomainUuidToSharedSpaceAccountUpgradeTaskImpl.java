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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
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
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.client.result.UpdateResult;

public class AddDomainUuidToSharedSpaceAccountUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	private final MongoTemplate mongoTemplate;

	public AddDomainUuidToSharedSpaceAccountUpgradeTaskImpl(
			AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			MongoTemplate mongoTemplate) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_5_1_ADD_DOMAIN_UUID_TO_SHARED_SPACE_ACCOUNT_IN_MEMBERS;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		Aggregation aggregation = Aggregation.newAggregation(
			Aggregation.match(Criteria.where
				("account").exists(true).and
				("account.domainUuid").exists(false)),
			Aggregation.group("account"),
			Aggregation.project(Fields.from(Fields.field("account.uuid"))));
		return mongoTemplate.aggregate(aggregation, SharedSpaceMember.class, UUID.class)
			.getMappedResults()
			.stream()
			.map(UUID::getUuid)
			.collect(Collectors.toUnmodifiableList());
	}

	private static class UUID {
		private final String uuid;

		@SuppressWarnings("unused")
		public UUID(String uuid) {
			this.uuid = uuid;
		}

		public String getUuid() {
			return uuid;
		}
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		Account account = accountRepository.findActivateAndDestroyedByLsUuid(identifier);
		BatchResultContext<Account> batchResultContext = new BatchResultContext<>(account);
		batchResultContext.setProcessed(false);
		batchResultContext.setIdentifier(identifier);
		if (Objects.nonNull(account)) {
			Query query = new Query();
			query.addCriteria(Criteria.where("account.uuid").is(identifier));
			Update update = new Update();
			update.set("account.domainUuid", account.getDomainId());
			UpdateResult updateMulti = mongoTemplate.updateMulti(query, update, SharedSpaceMember.class);
			console.logInfo(batchRunContext, total, position, "updateMulti: " + updateMulti);
			batchResultContext.setProcessed(true);
		} else {
			if (identifier.equals("176718dc-d37b-4d3e-8218-ca77652056f2")) {
				logger.debug("Account not found be cause it is a fake user (unknown-user@linshare.org): " + identifier);
			} else {
				logger.error("Can not find actor with uuid: " + identifier);
			}
		}
		return batchResultContext;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<Account> batchResultContext = (BatchResultContext<Account>) context;
		Account resource = batchResultContext.getResource();
		if (resource != null) {
			logInfo(batchRunContext, total, position, "Domain uuid was successfully added to SharedSpaceMember account collection: " + resource);
		} else {
			logInfo(batchRunContext, total, position, "SharedSpaceMember account update was skipped, can not find related resource : " + batchResultContext);
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<Account> batchResultContext = (BatchResultContext<Account>) exception.getContext();
		String resource = batchResultContext.getIdentifier();
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occurred while adding domain uuid to SharedSpaceMember account: "
				+ resource +
				". BatchBusinessException", exception);
	}
}
