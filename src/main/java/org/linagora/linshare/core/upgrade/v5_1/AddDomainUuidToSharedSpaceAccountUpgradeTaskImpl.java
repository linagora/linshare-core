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

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.repository.SharedSpaceMemberMongoRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class AddDomainUuidToSharedSpaceAccountUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	private final MongoTemplate mongoTemplate;

	private final SharedSpaceMemberMongoRepository sharedSpaceMemberMongoRepository;

	public AddDomainUuidToSharedSpaceAccountUpgradeTaskImpl(
			AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			MongoTemplate mongoTemplate,
			SharedSpaceMemberMongoRepository sharedSpaceMemberMongoRepository) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.mongoTemplate = mongoTemplate;
		this.sharedSpaceMemberMongoRepository = sharedSpaceMemberMongoRepository;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_5_1_ADD_DOMAIN_UUID_TO_SHARED_SPACE_ACCOUNT_IN_MEMBERS;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		Query query = Query.query(Criteria.where
				("account").exists(true).and
				("account.domainUuid").exists(false));
		List<String> audits = mongoTemplate.findDistinct(query, "uuid", SharedSpaceMember.class, String.class);
		return audits;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		SharedSpaceMember member = sharedSpaceMemberMongoRepository.findByUuid(identifier);
		BatchResultContext<SharedSpaceMember> res = new BatchResultContext<>(member);
		res.setProcessed(false);
		if (member == null) {
			return res;
		}
		Account account = accountRepository.findActivateAndDestroyedByLsUuid(member.getAccount().getUuid());
		if (Objects.nonNull(account)) {
			Query query = new Query();
			query.addCriteria(Criteria.where("uuid").is(identifier));
			Update update = new Update();
			update.set("account.domainUuid", account.getDomainId());
			mongoTemplate.findAndModify(query, update, SharedSpaceMember.class);
			res.setProcessed(true);
		} else {
			logger.error("Can not find actor with uuid:%s",
					member.getAccount().getUuid());
		}
		return res;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<SharedSpaceMember> res = (BatchResultContext<SharedSpaceMember>) context;
		SharedSpaceMember resource = res.getResource();
		if (resource != null) {
			logInfo(batchRunContext, total, position, "Domain uuid was successfully added to SharedSpaceMember collection: " + resource.toString());
		} else {
			logInfo(batchRunContext, total, position, "SharedSpaceMember update was skipped, can not find related resource : " + res.toString());
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<SharedSpaceMember> res = (BatchResultContext<SharedSpaceMember>) exception.getContext();
		SharedSpaceMember resource = res.getResource();
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occurred while adding domain uuid to SharedSpaceMember: "
				+ resource +
				". BatchBusinessException", exception);
	}
}
