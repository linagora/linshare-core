/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.entities.WorkgroupMember;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.ThreadMemberRepository;
import org.linagora.linshare.core.repository.ThreadRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;
import org.linagora.linshare.mongo.repository.SharedSpaceMemberMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpaceNodeMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpaceRoleMongoRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;

public class MigrateThreadToMongoUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	protected ThreadMemberRepository threadMemberRepository;

	protected ThreadRepository threadRepository;

	protected UserRepository<User> userRepository;

	protected SharedSpaceNodeMongoRepository nodeMongoRepository;

	protected SharedSpaceRoleMongoRepository roleMongoRepository;

	protected SharedSpaceMemberMongoRepository memberMongoRepository;

	public MigrateThreadToMongoUpgradeTaskImpl(AccountRepository<Account> accountRepository,
			UserRepository<User> userRepository, UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			ThreadMemberRepository threadMemberRepository, ThreadRepository threadRepository,
			SharedSpaceNodeMongoRepository nodeMongoRepository, SharedSpaceRoleMongoRepository roleMongoRepository,
			SharedSpaceMemberMongoRepository memberMongoRepository) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.userRepository = userRepository;
		this.threadMemberRepository = threadMemberRepository;
		this.threadRepository = threadRepository;
		this.nodeMongoRepository = nodeMongoRepository;
		this.roleMongoRepository = roleMongoRepository;
		this.memberMongoRepository = memberMongoRepository;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_2_2_MIGRATE_THREAD_AND_THREAD_MEMBERS_TO_MONGO_DATABASE;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		return threadRepository.findAllThreadUuid();
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		WorkGroup thread = threadRepository.findByLsUuid(identifier);
		SharedSpaceNode node = createSharedSpaceNode(thread);
		List<WorkgroupMember> threadMembers = threadMemberRepository.findAllThreadMembers(thread);
		for (WorkgroupMember threadMember : threadMembers) {
			createSharedSpaceMember(threadMember, node);
		}
		console.logDebug(batchRunContext, total, position, "Processing " + thread.toString());
		BatchResultContext<WorkGroup> res = new BatchResultContext<WorkGroup>(thread);
		res.setProcessed(true);
		return res;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<WorkGroup> res = (BatchResultContext<WorkGroup>) context;
		WorkGroup resource = res.getResource();
		logInfo(batchRunContext, total, position, resource + " has been updated.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<WorkGroup> res = (BatchResultContext<WorkGroup>) exception.getContext();
		WorkGroup resource = res.getResource();
		console.logError(batchRunContext, total, position, "The upgrade task : " + resource + " failed",
				batchRunContext);
		logger.error("Error occured while migrating the threadMember : " + resource, exception);
	}

	private void createSharedSpaceMember(WorkgroupMember threadMemberOld, SharedSpaceNode node) {
		SharedSpaceNodeNested nodde = new SharedSpaceNodeNested(node);
		SharedSpaceAccount account = new SharedSpaceAccount(threadMemberOld.getUser());
		SharedSpaceRole currentRole = new SharedSpaceRole();
		if (threadMemberOld.getAdmin()) {
			currentRole = roleMongoRepository.findByName("ADMIN");
		} else if (threadMemberOld.getCanUpload()) {
			currentRole = roleMongoRepository.findByName("CONTRIBUTOR");
		} else {
			currentRole = roleMongoRepository.findByName("READER");
		}
		GenericLightEntity role = new GenericLightEntity(currentRole);
		SharedSpaceMember ssMember = new SharedSpaceMember(nodde, role, account);
		ssMember.setCreationDate(threadMemberOld.getCreationDate());
		ssMember.setModificationDate(threadMemberOld.getModificationDate());
		memberMongoRepository.insert(ssMember);
	}

	private SharedSpaceNode createSharedSpaceNode(WorkGroup threadOld) {
		SharedSpaceNode node = new SharedSpaceNode(threadOld.getName(), threadOld.getLsUuid(), NodeType.WORK_GROUP,
				threadOld.getCreationDate(), threadOld.getModificationDate());
		return nodeMongoRepository.insert(node);
	}
}
