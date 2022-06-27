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
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class AddFirstLastNameToActorAuditsUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	private final MongoTemplate mongoTemplate;

	public AddFirstLastNameToActorAuditsUpgradeTaskImpl(
			AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			MongoTemplate mongoTemplate) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_5_1_ADD_FIRST_NAME_AND_LAST_NAME_TO_AUDIT_ACTOR_FIELD;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		Aggregation aggregation = Aggregation.newAggregation(
			Aggregation.match(Criteria.where
				("actor").exists(true).and
				("actor.uuid").exists(true).and
				("actor.firstName").exists(false).and
				("actor.lastName").exists(false)),
			Aggregation.group("actor"),
			Aggregation.project(Fields.from(Fields.field("actor.uuid"))));
		return mongoTemplate.aggregate(aggregation, "audit_log_entries", UUID.class)
			.getMappedResults()
			.stream()
			.map(UUID::getUuid)
			.collect(Collectors.toUnmodifiableList());
	}

	private static class UUID {
		private final String uuid;

		@SuppressWarnings("unused")
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
		BatchResultContext batchResultContext = new BatchResultContext<>(identifier);
		try {
			String firstName;
			String lastName;
			Account account = accountRepository.findActivateAndDestroyedByLsUuid(identifier);
			if (Objects.nonNull(account)) {
				if (account instanceof User) {
					User actor = (User) account;
					firstName = actor.getFirstName();
					lastName = actor.getLastName();
				} else if (account instanceof SystemAccount) {
					firstName = "System";
					lastName = "System";
				} else {
					logger.warn("Not an user: %s", identifier);
					firstName = "John";
					lastName = "Doe";
				}
			} else {
				logger.warn("Can not find actor with uuid:%s, fake first and last name will be used", identifier);
				firstName = "John";
				lastName = "Doe";
			}
			Query query = new Query();
			query.addCriteria(Criteria.where("actor.uuid").is(identifier));
			Update update = new Update();
			update.set("actor.firstName", firstName);
			update.set("actor.lastName", lastName);
			mongoTemplate.updateMulti(query, update, AuditLogEntryUser.class);
			batchResultContext.setProcessed(true);
			return batchResultContext;
		} catch (Exception e) {
			logger.error("An error occurred", e);
			throw new BatchBusinessException(batchResultContext, "fail");
		}
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<String> res = (BatchResultContext<String>) context;
		String resource = res.getResource();
		if (resource != null) {
			logInfo(batchRunContext, total, position, "First and last name were added successfully to actor on audit traces: " + resource);
		} else {
			logInfo(batchRunContext, total, position, "AuditLogEntryUser update were skipped, can not find related resource : " + res);
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<String> res = (BatchResultContext<String>) exception.getContext();
		String resource = res.getResource();
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occurred while adding first and last name to actor on audit traces : "
				+ resource +
				". BatchBusinessException", exception);
	}
}
