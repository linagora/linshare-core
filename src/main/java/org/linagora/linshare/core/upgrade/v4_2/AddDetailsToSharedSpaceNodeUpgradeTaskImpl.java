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

import java.util.List;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.logs.SharedSpaceNodeAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.repository.AuditUserMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpaceNodeMongoRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class AddDetailsToSharedSpaceNodeUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	protected MongoTemplate mongoTemplate;
	
	protected SharedSpaceNodeMongoRepository sharedSpaceNodeMongoRepository;
	
	protected AuditUserMongoRepository auditUserMongoRepository;

	public AddDetailsToSharedSpaceNodeUpgradeTaskImpl(AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			AuditUserMongoRepository auditUserMongoRepository,
			SharedSpaceNodeMongoRepository sharedSpaceNodeMongoRepository,
			MongoTemplate mongoTemplate) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.mongoTemplate = mongoTemplate;
		this.sharedSpaceNodeMongoRepository = sharedSpaceNodeMongoRepository;
		this.auditUserMongoRepository = auditUserMongoRepository;

	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_4_2_ADD_DETAILS_TO_SHARED_SPACE_NODES;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info("{} job is starting ...", getClass().toString());
		List<String> uuids = mongoTemplate.query(SharedSpaceNode.class).distinct("uuid").as(String.class).all();
		logger.info("{} shared spaces have been found.", uuids.size());
		return uuids;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		SharedSpaceNode sharedSpace = sharedSpaceNodeMongoRepository.findByUuid(identifier);
		BatchResultContext<SharedSpaceNode> res = new BatchResultContext<>(sharedSpace);
		res.setProcessed(false);
		if (sharedSpace == null) {
			return res;
		}
		Query query = new Query();
		query.addCriteria(new Criteria().andOperator(Criteria.where("resourceUuid").is(identifier), Criteria.where("action").is(LogAction.CREATE)));
		query.with(Sort.by(Direction.ASC, "resource.creationDate")).limit(1);
		SharedSpaceNodeAuditLogEntry audit = mongoTemplate.findOne(query,
				SharedSpaceNodeAuditLogEntry.class);
		AccountMto accountMto;
		if (audit == null) {
			String msg = String.format("Author is missing for sharedSpace %1$s (%2$s). Using John DOE unknown-user@linshare.org instead.", sharedSpace.getName(), sharedSpace.getUuid());
			console.logWarn(batchRunContext, total, position, msg);
			accountMto = new AccountMto();
			accountMto.setFirstName("John");
			accountMto.setLastName("DOE");
			accountMto.setName("John DOE");
			accountMto.setMail("unknown-user@linshare.org");
			accountMto.setUuid("176718dc-d37b-4d3e-8218-ca77652056f2");
		} else {
			accountMto = audit.getActor();
		}
		SharedSpaceAccount author = new SharedSpaceAccount();
		author.setUuid(accountMto.getUuid());
		author.setMail(accountMto.getMail());
		author.setName(accountMto.getName());
		sharedSpace.setAuthor(author);
		sharedSpace.setDescription("");
		sharedSpaceNodeMongoRepository.save(sharedSpace);
		Query query2 = new Query();
		query2.addCriteria(Criteria.where("resource.uuid").is(identifier));
		Update update = new Update();
		update.set("resource.author", sharedSpace.getAuthor());
		update.set("resource.description", "");
		update.set("resourceUpdated.author", sharedSpace.getAuthor());
		update.set("resourceUpdated.description", "");
		mongoTemplate.updateMulti(query2, update, SharedSpaceNodeAuditLogEntry.class);
		res.setProcessed(true);
		return res;

	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<SharedSpaceNode> res = (BatchResultContext<SharedSpaceNode>) context;
		SharedSpaceNode resource = res.getResource();
		logInfo(batchRunContext, total, position, resource + " has been updated.");
		if (res.getProcessed()) {
			logInfo(batchRunContext, total, position,
					"SharedSpaceNode has been updated: " + resource.toString());
		} else {
			logInfo(batchRunContext, total, position,
					"SharedSpaceNode has been skipped : " + resource.toString());
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<SharedSpaceNode> res = (BatchResultContext<SharedSpaceNode>) exception.getContext();
		SharedSpaceNode resource = res.getResource();
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occured while processing SharedSpaceNode update : {} . BatchBusinessException",
				resource, exception);
	}

}
