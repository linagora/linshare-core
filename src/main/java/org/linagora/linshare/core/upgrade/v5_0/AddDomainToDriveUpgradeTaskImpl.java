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
package org.linagora.linshare.core.upgrade.v5_0;

import java.util.List;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.ThreadRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.repository.SharedSpaceNodeMongoRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class AddDomainToDriveUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	private final MongoTemplate mongoTemplate;

	private final SharedSpaceNodeMongoRepository nodeMongoRepository;

	private final UserRepository<User> userRepository;

	public AddDomainToDriveUpgradeTaskImpl(
			AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			MongoTemplate mongoTemplate,
			SharedSpaceNodeMongoRepository nodeMongoRepository,
			ThreadRepository threadRepository,
			UserRepository<User> userRepository) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.mongoTemplate = mongoTemplate;
		this.nodeMongoRepository = nodeMongoRepository;
		this.userRepository = userRepository;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_5_0_ADD_DOMAIN_TO_DRIVE;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		Query query = Query.query(Criteria.where
				("nodeType").is(NodeType.WORK_SPACE).and
				("domainUuid").exists(false));
		List<String> nodes = mongoTemplate.findDistinct(query, "uuid", SharedSpaceNode.class, String.class);
		return nodes;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		SharedSpaceNode sharedSpace = nodeMongoRepository.findByUuid(identifier);
		BatchResultContext<SharedSpaceNode> res = new BatchResultContext<>(sharedSpace);
		res.setProcessed(false);
		if (sharedSpace == null) {
			return res;
		}
		User user = userRepository.findActivateAndDestroyedByLsUuid(sharedSpace.getAuthor().getUuid());
		sharedSpace.setDomainUuid(user.getDomainId());
		nodeMongoRepository.save(sharedSpace);
		updateDriveMembers(sharedSpace);
		updateNestedWorkgroups(sharedSpace);
		updateNestedWorkgroupsMembers(sharedSpace);
		res.setProcessed(true);
		return res;

	}

	private void updateNestedWorkgroupsMembers(SharedSpaceNode sharedSpace) {
		// Update all members of nested workgroups of the drive
		Query query3 = new Query();
		query3.addCriteria(Criteria.where("node.parentUuid").is(sharedSpace.getUuid()));
		Update update3 = new Update();
		update3.set("node.domainUuid", sharedSpace.getDomainUuid());
		mongoTemplate.updateMulti(query3, update3, SharedSpaceMember.class);
	}

	private void updateNestedWorkgroups(SharedSpaceNode sharedSpace) {
		// Update all nested workgroups
		Query query2 = new Query();
		query2.addCriteria(Criteria.where
				("parentUuid").is(sharedSpace.getUuid()).and
				("nodeType").is(NodeType.WORK_GROUP));
		Update update2 = new Update();
		update2.set("domainUuid", sharedSpace.getDomainUuid());
		mongoTemplate.updateMulti(query2, update2, SharedSpaceNode.class);
	}

	private void updateDriveMembers(SharedSpaceNode sharedSpace) {
		// Update all Drive memebers
		Query query = new Query();
		query.addCriteria(Criteria.where("node.uuid").is(sharedSpace.getUuid()));
		Update update = new Update();
		update.set("node.domainUuid", sharedSpace.getDomainUuid());
		mongoTemplate.updateMulti(query, update, SharedSpaceMember.class);
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<SharedSpaceNode> res = (BatchResultContext<SharedSpaceNode>) context;
		SharedSpaceNode resource = res.getResource();
		logInfo(batchRunContext, total, position, resource + " has been updated.");
		if (res.getProcessed()) {
			logInfo(batchRunContext, total, position, "Drive has been updated: " + resource.toString());
		} else {
			logInfo(batchRunContext, total, position, "Drive has been skipped : " + resource.toString());
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<SharedSpaceNode> res = (BatchResultContext<SharedSpaceNode>) exception.getContext();
		SharedSpaceNode resource = res.getResource();
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occured while processing Drive update : {} . BatchBusinessException", resource,
				exception);
	}
}
