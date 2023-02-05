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
package org.linagora.linshare.core.upgrade.v4_0;

import java.util.List;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.light.LightSharedSpaceRole;
import org.linagora.linshare.mongo.repository.SharedSpaceMemberMongoRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.google.common.collect.Lists;
import com.mongodb.client.DistinctIterable;

/**
 * Upgrade SharedSpaceMember structure by replace GenericLightEntity by
 * LightSharedSpaceRole that contains a new property [type: NodeType] introduced
 * on LinShare v4.0
 */
public class UpgradeSharedSpaceMemberStructureWithTypedSharedSpaceRoles extends GenericUpgradeTaskImpl {

	protected MongoTemplate mongoTemplate;

	protected SharedSpaceMemberMongoRepository memberMongoRepository;

	public UpgradeSharedSpaceMemberStructureWithTypedSharedSpaceRoles(AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository, MongoTemplate mongoTemplate,
			SharedSpaceMemberMongoRepository memberMongoRepository) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.mongoTemplate = mongoTemplate;
		this.memberMongoRepository = memberMongoRepository;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_4_0_UPDATE_SHARED_SPACE_MEMBER_STRUCTURE_WITH_TYPED_ROLES;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		DistinctIterable<String> results = mongoTemplate.getCollection("shared_space_members").distinct("uuid",
				String.class);
		return Lists.newArrayList(results);
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		SharedSpaceMember member = memberMongoRepository.findByUuid(identifier);
		BatchResultContext<SharedSpaceMember> batchResultContext = new BatchResultContext<>(member);
		batchResultContext.setProcessed(false);
		LightSharedSpaceRole newRole = new LightSharedSpaceRole(member.getRole().getUuid(), member.getRole().getName(),
				NodeType.WORK_GROUP);
		logger.info(" ROLE new structure {}", newRole.toString());
		member.setRole(newRole);
		memberMongoRepository.save(member);
		batchResultContext.setProcessed(true);
		return batchResultContext;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<SharedSpaceMember> batchResultContext = (BatchResultContext<SharedSpaceMember>) context;
		SharedSpaceMember resource = batchResultContext.getResource();
		logInfo(batchRunContext, total, position, "{} has been updated.", resource);
		if (batchResultContext.getProcessed()) {
			logInfo(batchRunContext, total, position, "SharedSpaceMember has been successfully updated : {}", resource.toString());
		} else {
			logInfo(batchRunContext, total, position, "The update of SharedSpaceMember has been skipped : {}", resource.toString());
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<SharedSpaceMember> res = (BatchResultContext<SharedSpaceMember>) exception.getContext();
		SharedSpaceMember resource = res.getResource();
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occured while processing SharedSpaceMember update : {} . BatchBusinessException", resource,
				exception);
	}

}
