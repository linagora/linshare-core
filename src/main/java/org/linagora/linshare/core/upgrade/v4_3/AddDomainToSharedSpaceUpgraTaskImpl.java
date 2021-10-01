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
package org.linagora.linshare.core.upgrade.v4_3;

import java.util.List;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.repository.SharedSpaceMemberMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpaceNodeMongoRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class AddDomainToSharedSpaceUpgraTaskImpl extends GenericUpgradeTaskImpl {

	private MongoTemplate mongoTemplate;

	private SharedSpaceNodeMongoRepository nodeMongoRepository;

	private UserRepository<User> userRepository;

	private SharedSpaceMemberMongoRepository memberRepository;

	public AddDomainToSharedSpaceUpgraTaskImpl(
			AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			MongoTemplate mongoTemplate,
			SharedSpaceNodeMongoRepository nodeMongoRepository,
			UserRepository<User> userRepository,
			SharedSpaceMemberMongoRepository memberRepository) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.mongoTemplate = mongoTemplate;
		this.nodeMongoRepository = nodeMongoRepository;
		this.userRepository = userRepository;
		this.memberRepository = memberRepository;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_4_3_ADD_DOMAIN_TO_SHARED_SPACE;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		// What about wg and drives synchronized for LDAP
		Query query = Query.query(Criteria.where
				("domainUuid").exists(false).and
				("author").exists(true));
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
		// we do not retrieve the fake user `unknown-user@linshare.org` because it does not have a domain
		if (!sharedSpace.getAuthor().getMail().equals("unknown-user@linshare.org")) {
			User user = userRepository.findByLsUuid(sharedSpace.getAuthor().getUuid());
			sharedSpace.setDomainUuid(user.getDomainId());
			nodeMongoRepository.save(sharedSpace);
			// We need to update all nested nodes on sharedSpaceMembers list
			List<SharedSpaceMember> members = memberRepository.findByNodeUuid(sharedSpace.getUuid());
			for (SharedSpaceMember sharedSpaceMember : members) {
				sharedSpaceMember.getNode().setDomainUuid(sharedSpace.getDomainUuid());
				memberRepository.save(sharedSpaceMember);
			}
			res.setProcessed(true);
		} else {
			// Which domain should we assign to the retrieved SharedSpace with a fake author
		}
		return res;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<SharedSpaceNode> res = (BatchResultContext<SharedSpaceNode>) context;
		SharedSpaceNode resource = res.getResource();
		logInfo(batchRunContext, total, position, resource + " has been updated.");
		if (res.getProcessed()) {
			logInfo(batchRunContext, total, position, "SharedSpaceNode has been updated: " + resource.toString());
		} else {
			logInfo(batchRunContext, total, position, "SharedSpaceNode has been skipped : " + resource.toString());
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<SharedSpaceNode> res = (BatchResultContext<SharedSpaceNode>) exception.getContext();
		SharedSpaceNode resource = res.getResource();
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occured while processing SharedSpaceNode update : {} . BatchBusinessException", resource,
				exception);
	}
}
