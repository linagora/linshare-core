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
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.repository.SharedSpaceMemberMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpaceNodeMongoRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class UpdateSeeAsNestedFieldInNestedSharedSpaceMemberUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	private MongoTemplate mongoTemplate;

	private SharedSpaceMemberMongoRepository memberMongoRepository;

	private SharedSpaceNodeMongoRepository nodeMongoRepository;

	public UpdateSeeAsNestedFieldInNestedSharedSpaceMemberUpgradeTaskImpl(AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			SharedSpaceMemberMongoRepository memberMongoRepository,
			SharedSpaceNodeMongoRepository nodeMongoRepository,
			MongoTemplate mongoTemplate) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.mongoTemplate = mongoTemplate;
		this.memberMongoRepository = memberMongoRepository;
		this.nodeMongoRepository = nodeMongoRepository;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_4_2_UPDATE_SEE_AS_NESTED_FIELD_IN_NESTED_SHARED_SPACE_MEMBERS;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		return mongoTemplate.findDistinct(
				Query.query(
						Criteria.where("nested").is(true).and("type").is("WORK_GROUP").and("seeAsNested").is(false)),
				"uuid", SharedSpaceMember.class, String.class);
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		SharedSpaceMember member = memberMongoRepository.findByUuid(identifier);
		BatchResultContext<SharedSpaceMember> res = new BatchResultContext<SharedSpaceMember>(member);
		res.setProcessed(false);
		SharedSpaceNode drive = nodeMongoRepository.findByUuid(member.getNode().getParentUuid());
		if (drive != null) {
			SharedSpaceMember findByAccountAndNode = memberMongoRepository
					.findByAccountAndNode(member.getAccount().getUuid(), drive.getUuid());
			if (findByAccountAndNode != null) {
				// Only members that belongs to both drive and its nested workgroup are seen as
				// nested
				member.setSeeAsNested(true);
				memberMongoRepository.save(member);
				res.setProcessed(true);
			}
		}
		return res;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<SharedSpaceMember> res = (BatchResultContext<SharedSpaceMember>) context;
		SharedSpaceMember resource = res.getResource();
		logInfo(batchRunContext, total, position, resource + " has been updated.");
		if (res.getProcessed()) {
			logInfo(batchRunContext, total, position,
					"seeAsNested field was set to true for SharedSpaceMember entity (this member belongs to both drive and its nested workgroup): " + resource.toString());
		} else {
			logInfo(batchRunContext, total, position,
					"SharedSpaceMember has been skipped : " + resource.toString());
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<SharedSpaceMember> res = (BatchResultContext<SharedSpaceMember>) exception.getContext();
		SharedSpaceMember resource = res.getResource();
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occured while processing SharedSpaceMember update : {} . BatchBusinessException",
				resource, exception);
	}

}
