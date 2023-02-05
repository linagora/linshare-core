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

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.client.result.UpdateResult;

public class AddGuestAccountTypeToSharedSpaceMemberUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	private final MongoTemplate mongoTemplate;

	private final GuestRepository guestRepository;

	public AddGuestAccountTypeToSharedSpaceMemberUpgradeTaskImpl(
			AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			MongoTemplate mongoTemplate,
			GuestRepository guestRepository) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.mongoTemplate = mongoTemplate;
		this.guestRepository = guestRepository;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_5_1_ADD_GUEST_ACCOUNT_TYPE_TO_SHARED_SPACE_MEMBER;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		List<String> uuids = guestRepository.findAllGuestsUuids();
		return uuids;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		Account account = accountRepository.findActivateAndDestroyedByLsUuid(identifier);
		BatchResultContext<Account> res = new BatchResultContext<Account>(account);
		if (account == null) {
			res.setIdentifier(identifier);
			return res;
		}
		Query query = new Query();
		query.addCriteria(Criteria.where("account.uuid").is(identifier));
		Update update = new Update();
		update.set("account.accountType", AccountType.GUEST);
		UpdateResult updateMulti = mongoTemplate.updateMulti(query, update, SharedSpaceMember.class);
		console.logInfo(batchRunContext, total, position, "updateMulti: " + updateMulti);
		res.setProcessed(true);
		return res;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<Account> res = (BatchResultContext<Account>) context;
		Account resource = res.getResource();
		if (resource != null) {
			logInfo(batchRunContext, total, position, "Guest accountType was added successfully to sharedSpaceMember: " + resource.toString());
		} else {
			logInfo(batchRunContext, total, position, "SharedSpacemember update was skipped, can not find related account : " + res.toString());
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<Account> res = (BatchResultContext<Account>) exception.getContext();
		Account resource = res.getResource();
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occurred while adding guest accountType to sharedSpaceMembers : "
				+ resource +
				". BatchBusinessException", exception);
	}
}
