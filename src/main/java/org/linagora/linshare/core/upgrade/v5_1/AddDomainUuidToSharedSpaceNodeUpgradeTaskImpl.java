/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2022. Contribute to
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
package org.linagora.linshare.core.upgrade.v5_1;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class AddDomainUuidToSharedSpaceNodeUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	private final MongoTemplate mongoTemplate;

	public AddDomainUuidToSharedSpaceNodeUpgradeTaskImpl(
			AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			MongoTemplate mongoTemplate) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_5_1_ADD_DOMAIN_UUID_TO_SHARED_SPACE_ACCOUNT_IN_NODES;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		Aggregation aggregation = Aggregation.newAggregation(
			Aggregation.match(Criteria.where
				("author").exists(true).and
				("author.domainUuid").exists(false)),
			Aggregation.group("author"),
			Aggregation.project(Fields.from(Fields.field("author.uuid"))));
		return mongoTemplate.aggregate(aggregation, SharedSpaceNode.class, UUID.class)
			.getMappedResults()
			.stream()
			.map(UUID::getUuid)
			.collect(Collectors.toUnmodifiableList());
	}

	private static class UUID {
		private final String uuid;

		public UUID(String uuid) {
			this.uuid = uuid;
		}

		public String getUuid() {
			return uuid;
		}
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		Account account = accountRepository.findActivateAndDestroyedByLsUuid(identifier);
		BatchResultContext<Account> batchResultContext = new BatchResultContext<>(account);
		batchResultContext.setProcessed(false);
		batchResultContext.setIdentifier(identifier);
		if (Objects.nonNull(account)) {
			Query query = new Query();
			query.addCriteria(Criteria.where("author.uuid").is(identifier));
			Update update = new Update();
			update.set("author.domainUuid", account.getDomainId());
			mongoTemplate.updateMulti(query, update, SharedSpaceNode.class);
			batchResultContext.setProcessed(true);
		} else {
			logger.error("Can not find author with uuid:" + identifier);
			batchResultContext.setProcessed(false);
		}
		return batchResultContext;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<String> batchResultContext = (BatchResultContext<String>) context;
		String resource = batchResultContext.getResource();
		if (resource != null) {
			logInfo(batchRunContext, total, position, "Domain uuid was successfully added to SharedSpaceNode author: " + resource);
		} else {
			logInfo(batchRunContext, total, position, "SharedSpaceNode update was skipped, can not find related resource : " + batchResultContext);
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<Account> batchResultContext = (BatchResultContext<Account>) exception.getContext();
		String resource = batchResultContext.getIdentifier();
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occurred while adding domain uuid to SharedSpaceNode author: "
				+ resource +
				". BatchBusinessException", exception);
	}
}
