/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
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
package org.linagora.linshare.core.upgrade.v2_2;

import java.util.List;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupLightDto;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;
import org.linagora.linshare.mongo.entities.logs.SharedSpaceMemberAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.ThreadMemberAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.entities.mto.ThreadMemberMto;
import org.linagora.linshare.mongo.repository.AuditUserMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpaceRoleMongoRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class MigrateWorkGroupMemberAuditToSharedSpaceMemberAuditUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	protected MongoTemplate mongoTemplate;

	protected AuditUserMongoRepository auditUserMongoRepository;

	protected SharedSpaceRoleMongoRepository sharedSpaceRoleMongoRepository;

	public MigrateWorkGroupMemberAuditToSharedSpaceMemberAuditUpgradeTaskImpl(AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			MongoTemplate mongoTemplate,
			AuditUserMongoRepository auditUserMongoRepository,
			SharedSpaceRoleMongoRepository sharedSpaceRoleMongoRepository) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.mongoTemplate = mongoTemplate;
		this.auditUserMongoRepository = auditUserMongoRepository;
		this.sharedSpaceRoleMongoRepository = sharedSpaceRoleMongoRepository;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_2_2_MIGRATE_WORKGROUP_MEMBER_AUDIT_TO_SHARED_SPACE_MEMBER_AUDIT;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		List<String> workGroupsAudits = findAllWorkGroupsAudit();
		return workGroupsAudits;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		Query query = new Query();
		query.addCriteria(Criteria.where("uuid").is(identifier));
		ThreadMemberAuditLogEntry threadMemberAudit = mongoTemplate.findOne(query, ThreadMemberAuditLogEntry.class);
		if (threadMemberAudit == null) {
			return null;
		}
		BatchResultContext<ThreadMemberAuditLogEntry> res = new BatchResultContext<ThreadMemberAuditLogEntry>(threadMemberAudit);
		console.logDebug(batchRunContext, total, position, "Processing audit : " + threadMemberAudit.toString());
		res.setProcessed(false);
		if (AuditLogEntryType.WORKGROUP_MEMBER.equals(threadMemberAudit.getType())) {
			SharedSpaceMember member = new SharedSpaceMember(getNode(threadMemberAudit.getWorkGroup()),
					getRole(threadMemberAudit.getResource()), getAccount(threadMemberAudit.getResource().getUser()));
			member.setUser(member.getAccount());
			SharedSpaceMemberAuditLogEntry ssmAuditLogEntry = new SharedSpaceMemberAuditLogEntry();
			ssmAuditLogEntry.setCreationDate(threadMemberAudit.getCreationDate());
			ssmAuditLogEntry.setResource(member);
			ssmAuditLogEntry.setResourceUuid(threadMemberAudit.getResourceUuid());
			ssmAuditLogEntry.setAction(threadMemberAudit.getAction());
			ssmAuditLogEntry.setAuthUser(threadMemberAudit.getAuthUser());
			ssmAuditLogEntry.setActor(threadMemberAudit.getActor());
			ssmAuditLogEntry.setType(AuditLogEntryType.WORKGROUP_MEMBER);
			ssmAuditLogEntry.setWorkGroup(threadMemberAudit.getWorkGroup());
			if (LogAction.UPDATE.equals(threadMemberAudit.getAction())) {
				SharedSpaceMember memberUpdated = new SharedSpaceMember(getNode(threadMemberAudit.getWorkGroup()),
						getRole(threadMemberAudit.getResourceUpdated()),
						getAccount(threadMemberAudit.getResourceUpdated().getUser()));
				ssmAuditLogEntry.setResourceUpdated(memberUpdated);
			}
			auditUserMongoRepository.insert(ssmAuditLogEntry);
			auditUserMongoRepository.delete(threadMemberAudit);
			res.setProcessed(true);
		}
		return res;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<ThreadMemberAuditLogEntry> res = (BatchResultContext<ThreadMemberAuditLogEntry>) context;
		ThreadMemberAuditLogEntry resource = res.getResource();
		logInfo(batchRunContext, total, position, resource + " has been updated.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<ThreadMemberAuditLogEntry> res = (BatchResultContext<ThreadMemberAuditLogEntry>) exception
				.getContext();
		ThreadMemberAuditLogEntry resource = res.getResource();
		console.logError(batchRunContext, total, position, "The upgrade task : " + resource + " failed",
				batchRunContext);
		logger.error("Error occured while migrating the threadMember : " + resource, exception);
	}

	private List<String> findAllWorkGroupsAudit() {
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("type").is("WORKGROUP_MEMBER").and("_class")
						.is("org.linagora.linshare.mongo.entities.logs.ThreadMemberAuditLogEntry")),
				Aggregation.project("uuid"));
		List<ThreadMemberAuditLogEntry> results = mongoTemplate
				.aggregate(aggregation, "audit_log_entries", ThreadMemberAuditLogEntry.class).getMappedResults();
		return Lists.transform(results, new Function<ThreadMemberAuditLogEntry, String>() {
			@Override
			public String apply(ThreadMemberAuditLogEntry threadMemberAuditLogEntry) {
				return threadMemberAuditLogEntry.getUuid();
			}
		});
	}

	private GenericLightEntity getRole(ThreadMemberMto resource) {
		SharedSpaceRole role = new SharedSpaceRole();
		if (resource.isAdmin()) {
			role = sharedSpaceRoleMongoRepository.findByName("ADMIN");
		} else if (resource.isCanUpload()) {
			role = sharedSpaceRoleMongoRepository.findByName("CONTRIBUTOR");
		} else {
			role = sharedSpaceRoleMongoRepository.findByName("READER");
		}
		return new GenericLightEntity(role);
	}

	private SharedSpaceAccount getAccount(AccountMto user) {
		return new SharedSpaceAccount(user.getUuid(), user.getName(), user.getFirstName(), user.getLastName(),
				user.getMail());
	}

	private SharedSpaceNodeNested getNode(WorkGroupLightDto groupLightDto) {
		return new SharedSpaceNodeNested(groupLightDto.getUuid(), groupLightDto.getName(), null, NodeType.WORK_GROUP,
				groupLightDto.getCreationDate(), null);
	}

}
