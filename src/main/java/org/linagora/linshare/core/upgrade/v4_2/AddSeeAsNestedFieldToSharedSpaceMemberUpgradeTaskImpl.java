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

import java.util.Arrays;
import java.util.List;

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
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.repository.SharedSpaceMemberMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpaceNodeMongoRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.client.result.UpdateResult;

public class AddSeeAsNestedFieldToSharedSpaceMemberUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	private MongoTemplate mongoTemplate;

	public AddSeeAsNestedFieldToSharedSpaceMemberUpgradeTaskImpl(AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			SharedSpaceMemberMongoRepository memberMongoRepository, SharedSpaceNodeMongoRepository nodeMongoRepository,
			MongoTemplate mongoTemplate) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_4_2_ADD_SEE_AS_NESTED_FIELD_TO_SHARED_SPACE_MEMBERS;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		// we change all existing entities in one process no need to fetch uuids
		return Arrays.asList("fakeUuid");
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		Update update = new Update();
		update.set("seeAsNested", false);
		// update only entities that didn't have the field seeAsNested (upgrade task
		// could be executed several times)
		Query query = Query.query(new Criteria().orOperator(Criteria.where("seeAsNested").exists(false), new Criteria()
				.andOperator(Criteria.where("seeAsNested").exists(true), Criteria.where("seeAsNested").is(false))));
		logger.debug(query.toString());
		UpdateResult updatedMembers = mongoTemplate.updateMulti(query, update, SharedSpaceMember.class);
		logger.debug("Total of updated SharedSpaceMember entities {}", updatedMembers.getModifiedCount());
		BatchResultContext<FakeContext> res = new BatchResultContext<>(new FakeContext(identifier));
		res.setProcessed(true);
		return res;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<FakeContext> res = (BatchResultContext<FakeContext>) context;
		if (res.getProcessed()) {
			logInfo(batchRunContext, total, position, "seeAsNested field was added to all SharedSpaceMember entities.");
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occured while processing all SharedSpaceMember entities update . BatchBusinessException",
				exception);
	}

}
