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
import java.util.Objects;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.ContainerQuotaBusinessService;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.business.service.WorkGroupNodeBusinessService;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.constants.OperationHistoryTypeEnum;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.OperationHistory;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.ThreadRepository;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.repository.SharedSpaceNodeMongoRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class AddDomainToWorkGroupUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	private final MongoTemplate mongoTemplate;

	private final SharedSpaceNodeMongoRepository nodeMongoRepository;

	private final ThreadRepository threadRepository;

	private final OperationHistoryBusinessService operationHistoryBusinessService;

	private final WorkGroupNodeBusinessService workGroupNodeBusinessService;

	private final ContainerQuotaBusinessService containerQuotaBusinessService;

	private final AccountQuotaBusinessService accountQuotaBusinessService;

	public AddDomainToWorkGroupUpgradeTaskImpl(
			AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			MongoTemplate mongoTemplate,
			SharedSpaceNodeMongoRepository nodeMongoRepository,
			ThreadRepository threadRepository,
			OperationHistoryBusinessService operationHistoryBusinessService,
			WorkGroupNodeBusinessService workGroupNodeBusinessService,
			ContainerQuotaBusinessService containerQuotaBusinessService,
			AccountQuotaBusinessService accountQuotaBusinessService) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.mongoTemplate = mongoTemplate;
		this.nodeMongoRepository = nodeMongoRepository;
		this.threadRepository = threadRepository;
		this.operationHistoryBusinessService = operationHistoryBusinessService;
		this.workGroupNodeBusinessService = workGroupNodeBusinessService;
		this.containerQuotaBusinessService = containerQuotaBusinessService;
		this.accountQuotaBusinessService = accountQuotaBusinessService;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_5_0_ADD_DOMAIN_TO_WORK_GROUP;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		Query query = Query.query(Criteria.where
				("nodeType").is(NodeType.WORK_GROUP).and
				("domainUuid").exists(false).and
				("parentUuid").exists(false));
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
		WorkGroup workGroup = threadRepository.findByLsUuid(sharedSpace.getUuid());
		logger.debug("Workgroup: %s", workGroup);
		if (Objects.isNull(workGroup)) {
			logger.debug("Workgroup not found with uuid: %s, so it will be created with the related quota", sharedSpace.getUuid());
			Account author = accountRepository.findActivateAndDestroyedByLsUuid(sharedSpace.getAuthor().getUuid());
			workGroup = new WorkGroup(author.getDomain(), author, sharedSpace.getName());
			threadRepository.create(workGroup);
			workGroup.setLsUuid(sharedSpace.getUuid());
			workGroup.setCreationDate(sharedSpace.getCreationDate());
			workGroup.setModificationDate(sharedSpace.getModificationDate());
			createThreadQuota(sharedSpace, workGroup);
			threadRepository.update(workGroup);
			Long nodeSize = workGroupNodeBusinessService.computeAllWorkgroupNodesSize(sharedSpace.getUuid());
			OperationHistory oh = new OperationHistory(workGroup, workGroup.getDomain(), nodeSize,
					OperationHistoryTypeEnum.CREATE, ContainerQuotaType.WORK_GROUP);
			operationHistoryBusinessService.create(oh);
			logger.debug("Workgroup with uuid: %s is created", sharedSpace.getUuid());
		}
		sharedSpace.setDomainUuid(workGroup.getDomainId());
		nodeMongoRepository.save(sharedSpace);
		updateSharedSpaceNested(sharedSpace);
		res.setProcessed(true);
		return res;
	}

	private void createThreadQuota(SharedSpaceNode sharedSpace, WorkGroup workGroup) {
		ContainerQuota containerQuota = containerQuotaBusinessService.find(workGroup.getDomain(), ContainerQuotaType.WORK_GROUP);
		AccountQuota threadQuota = new AccountQuota(
				workGroup.getDomain(),
				workGroup.getDomain().getParentDomain(),
				workGroup, containerQuota);
		threadQuota.setDomainShared(containerQuota.getDomainQuota().getDomainShared());
		threadQuota.setDomainSharedOverride(containerQuota.getDomainQuota().getDomainSharedOverride());
		threadQuota = accountQuotaBusinessService.create(threadQuota);
		threadQuota.setUuid(sharedSpace.getQuotaUuid());
	}

	private void updateSharedSpaceNested(SharedSpaceNode sharedSpace) {
		Query query = new Query();
		query.addCriteria(Criteria.where
				("node.uuid").is(sharedSpace.getUuid()).and
				("node.domainUuid").exists(false));
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
			logInfo(batchRunContext, total, position, "Workgroup has been updated: " + resource.toString());
		} else {
			logInfo(batchRunContext, total, position, "Workgroup has been skipped : " + resource.toString());
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<SharedSpaceNode> res = (BatchResultContext<SharedSpaceNode>) exception.getContext();
		SharedSpaceNode resource = res.getResource();
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occured while processing Workgroup update : {} . BatchBusinessException", resource,
				exception);
	}
}
