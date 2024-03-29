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

import org.bson.Document;
import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.MailAttachment;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.MailAttachmentRepository;
import org.linagora.linshare.mongo.entities.logs.MailAttachmentAuditLogEntry;
import org.linagora.linshare.mongo.repository.AuditAdminMongoRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.google.common.collect.Lists;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;

public class UpdateTargetDomainUuidMailAttachmentAuditUpgradeTaskImpl extends GenericUpgradeTaskImpl{

	protected MongoTemplate mongoTemplate;

	protected AuditAdminMongoRepository auditAdminMongoRepository;

	protected MailAttachmentRepository attachmentRepository;

	public UpdateTargetDomainUuidMailAttachmentAuditUpgradeTaskImpl(
			AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			MongoTemplate mongoTemplate,
			AuditAdminMongoRepository auditUserMongoRepository,
			MailAttachmentRepository attachmentRepository) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.mongoTemplate = mongoTemplate;
		this.auditAdminMongoRepository = auditUserMongoRepository;
		this.attachmentRepository = attachmentRepository;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_4_0_UPDATE_TARGET_DOMAIN_UUID_MAIL_ATTACHMENT_AUDIT;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		Query query = new Query();
		query.addCriteria(Criteria.where("type").is("MAIL_ATTACHMENT"));
		MongoCollection<Document> mailAttachmentAudits = mongoTemplate.getCollection("audit_log_entries");
		DistinctIterable<String> results = mailAttachmentAudits.distinct("uuid", query.getQueryObject(), String.class);
		return Lists.newArrayList(results);
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		MailAttachmentAuditLogEntry attachmentAuditLogEntry = auditAdminMongoRepository.findByUuid(identifier);
		BatchResultContext<MailAttachmentAuditLogEntry> res = new BatchResultContext<>(attachmentAuditLogEntry);
		res.setProcessed(false);
		MailAttachment attachment = attachmentRepository.findByUuid(attachmentAuditLogEntry.getResourceUuid());
		if (attachment == null) {
			return null;
		}
		attachmentAuditLogEntry.setTargetDomainUuid(attachment.getMailConfig().getDomain().getUuid());
		auditAdminMongoRepository.save(attachmentAuditLogEntry);
		res.setProcessed(true);
		return res;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<MailAttachmentAuditLogEntry> res = (BatchResultContext<MailAttachmentAuditLogEntry>) context;
		MailAttachmentAuditLogEntry resource = res.getResource();
		logInfo(batchRunContext, total, position, resource + " has been updated.");
		if (res.getProcessed()) {
			logInfo(batchRunContext, total, position,
					"MailAttachmentAuditLogEntry has been updated : " + resource.toString());
		} else {
			logInfo(batchRunContext, total, position,
					"MailAttachmentAuditLogEntry has been skipped : " + resource.toString());
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<MailAttachmentAuditLogEntry> res = (BatchResultContext<MailAttachmentAuditLogEntry>) exception
				.getContext();
		MailAttachmentAuditLogEntry resource = res.getResource();
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occured while processing MailAttachmentAuditLogEntry update : {} . BatchBusinessException",
				resource, exception);
	}

}
