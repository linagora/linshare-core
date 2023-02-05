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

import java.util.Arrays;
import java.util.List;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.batches.utils.FakeContext;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.ldap.Role;
import org.linagora.linshare.mongo.entities.logs.SharedSpaceMemberAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.SharedSpaceNodeAuditLogEntry;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class RenameDriveAuditToWorkSpaceUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	private final MongoTemplate mongoTemplate;

	public RenameDriveAuditToWorkSpaceUpgradeTaskImpl(
			AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			MongoTemplate mongoTemplate) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_5_0_AUDIT_RENAME_DRIVE_TO_WORK_SPACE;

	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		return Arrays.asList("fakeUuid");
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		renameDrive();
		renameDriveRole();
		renameDriveMember();
		BatchResultContext<FakeContext> res = new BatchResultContext<>(new FakeContext(identifier));
		res.setProcessed(true);
		return res;
	}

	private void renameDrive() {
		Query query = Query.query(Criteria.where("type").is(AuditLogEntryType.DRIVE));
		Update update = new Update();
		update.set("type", AuditLogEntryType.WORK_SPACE);
		update.set("resource.nodeType", NodeType.WORK_SPACE);
		mongoTemplate.updateMulti(query, update, SharedSpaceNodeAuditLogEntry.class);
	}

	/**
	 * For the old Roles we set them as string but the new ones
	 * (WORK_SPACE_READER/WRITER/ADMIN)it could be better to set an enum for that
	 */
	private void renameDriveRole() {
		Query query = new Query();
		query.addCriteria(new Criteria().andOperator(
				Criteria.where("type").is(AuditLogEntryType.DRIVE_MEMBER),
				Criteria.where("resource.role.name").is(Role.WORK_SPACE_ADMIN)));
		Update update = new Update();
		update.set("resource.role.name", "WORK_SPACE_ADMIN");
		update.set("resource.role.type", NodeType.WORK_SPACE);
		mongoTemplate.updateMulti(query, update, SharedSpaceMemberAuditLogEntry.class);

		Query query2 = new Query();
		query2.addCriteria(new Criteria().andOperator(
				Criteria.where("type").is(AuditLogEntryType.DRIVE_MEMBER),
				Criteria.where("resource.role.name").is(Role.WORK_SPACE_READER)));
		Update update2 = new Update();
		update2.set("resource.role.name", "WORK_SPACE_READER");
		update.set("resource.role.type", NodeType.WORK_SPACE);
		mongoTemplate.updateMulti(query2, update2, SharedSpaceMemberAuditLogEntry.class);

		Query query3 = new Query();
		query3.addCriteria(new Criteria().andOperator(
				Criteria.where("type").is(AuditLogEntryType.DRIVE_MEMBER),
				Criteria.where("resource.role.name").is(Role.WORK_SPACE_WRITER)));
		Update update3 = new Update();
		update3.set("resource.role.name", "WORK_SPACE_WRITER");
		update.set("resource.role.type", NodeType.WORK_SPACE);
		mongoTemplate.updateMulti(query3, update3, SharedSpaceMemberAuditLogEntry.class);
	}

	private void renameDriveMember() {
		Query query = Query.query(Criteria.where("type").is(AuditLogEntryType.DRIVE_MEMBER));
		Update update = new Update();
		update.set("type", AuditLogEntryType.WORK_SPACE_MEMBER);
		update.set("resource.node.nodeType", NodeType.WORK_SPACE);
		update.set("resource.type", NodeType.WORK_SPACE);
		mongoTemplate.updateMulti(query, update, SharedSpaceMemberAuditLogEntry.class);
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<FakeContext> res = (BatchResultContext<FakeContext>) context;
		if (res.getProcessed()) {
			logInfo(batchRunContext, total, position, "DRIVE audit was renamed succefully to workSpace.");
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occured while renaming DRIVE audits to workSpace", exception);
	}

}
