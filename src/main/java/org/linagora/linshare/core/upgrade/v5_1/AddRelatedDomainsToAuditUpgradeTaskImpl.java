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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.batches.utils.FakeContext;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryAdmin;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.entities.logs.ModeratorAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.ShareEntryAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.SharedSpaceMemberAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.SharedSpaceNodeAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.UserAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.EntryMto;
import org.linagora.linshare.mongo.entities.mto.ShareEntryMto;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.CloseableIterator;

import com.google.common.collect.Lists;

public class AddRelatedDomainsToAuditUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	private final MongoTemplate mongoTemplate;

	public AddRelatedDomainsToAuditUpgradeTaskImpl(
			AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			MongoTemplate mongoTemplate) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_5_1_ADD_RELATED_DOMAIN_FIELD_TO_AUDIT;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		return Arrays.asList("fakeUuid");
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		BatchResultContext<FakeContext> res = new BatchResultContext<>(new FakeContext(identifier));
		res.setProcessed(false);

		updateAdminTraces(batchRunContext, total, position);

		// Peculiar use case : AuditLogEntryType.USER
		peculiarUseCaseUsers(batchRunContext, total, position);

		// Peculiar use case : AuditLogEntryType.GUEST_MODERATOR
		peculiarUseCaseModerators(batchRunContext, total, position);

		// Peculiar use case : AuditLogEntryType.SHARE_ENTRY
		peculiarUseCaseShareEntries(batchRunContext, total, position);

		// Peculiar use case : AuditLogEntryType WORKGROUP and WORKSPACE
		peculiarUseCaseSharedSpaceNodeTraces(batchRunContext, total, position);

		// Peculiar use case : AuditLogEntryType WORKGROUP_MEMBER and WORK_SPACE_MEMBER
		peculiarUseCaseSharedSpaceMemberTraces(batchRunContext, total, position);

		updateUserTraces(batchRunContext, total, position);
		res.setProcessed(true);
		return res;
	}

	private void peculiarUseCaseUsers(BatchRunContext batchRunContext, long total, long position) {
		Query query = Query.query(
				Criteria.where
				("type").is(AuditLogEntryType.USER.toString()).and
				("relatedDomains").exists(false));
		long localTotal = mongoTemplate.count(query, AuditLogEntryUser.class);
		console.logInfo(batchRunContext, total, position, "localTotal peculiarUseCaseUsers found: " + localTotal);
		CloseableIterator<AuditLogEntryUser> stream = mongoTemplate.stream(query, AuditLogEntryUser.class);
		AtomicInteger localPosition = new AtomicInteger(0);
		stream.forEachRemaining(entry -> {
			updateRelatedDomainsForUserResourceTraces(batchRunContext, localPosition, localTotal, entry);
			}
		);
	}

	private void peculiarUseCaseModerators(BatchRunContext batchRunContext, long total, long position) {
		Query query = Query.query(
				Criteria.where
				("type").is(AuditLogEntryType.GUEST_MODERATOR.toString()).and
				("relatedDomains").exists(false));
		long localTotal = mongoTemplate.count(query, ModeratorAuditLogEntry.class);
		console.logInfo(batchRunContext, total, position, "localTotal peculiarUseCaseModerators found: " + localTotal);
		CloseableIterator<ModeratorAuditLogEntry> stream = mongoTemplate.stream(query, ModeratorAuditLogEntry.class);
		AtomicInteger localPosition = new AtomicInteger(0);
		stream.forEachRemaining(entry -> {
			updateRelatedDomainsForModeratorResourceTraces(batchRunContext, localPosition, localTotal, entry);
			}
		);
	}

	private void peculiarUseCaseShareEntries(BatchRunContext batchRunContext, long total, long position) {
		Query query = Query.query(
				Criteria.where
				("type").is(AuditLogEntryType.SHARE_ENTRY.toString()).and
				("relatedDomains").exists(false));
		long localTotal = mongoTemplate.count(query, ShareEntryAuditLogEntry.class);
		console.logInfo(batchRunContext, total, position, "localTotal peculiarUseCaseShareEntries found: " + localTotal);
		CloseableIterator<ShareEntryAuditLogEntry> stream = mongoTemplate.stream(query, ShareEntryAuditLogEntry.class);
		AtomicInteger localPosition = new AtomicInteger(0);
		stream.forEachRemaining(entry -> {
			updateRelatedDomainsForShareEntryResourceTraces(batchRunContext, localPosition, localTotal, entry);
			}
		);
	}

	private void updateAdminTraces(BatchRunContext batchRunContext, long total, long position) {
		Query query = Query.query(
				Criteria.where
				("type").in(Lists.newArrayList(
						AuditLogEntryType.DOMAIN.toString(),
						AuditLogEntryType.DOMAIN_PATTERN.toString(),
						AuditLogEntryType.FUNCTIONALITY.toString(),
						AuditLogEntryType.GROUP_FILTER.toString(),
						AuditLogEntryType.MAIL_ATTACHMENT.toString(),
						AuditLogEntryType.PUBLIC_KEY.toString(),
						AuditLogEntryType.WORKSPACE_FILTER.toString()
					)).and
				("relatedDomains").exists(false));
		long localTotal = mongoTemplate.count(query, AuditLogEntryAdmin.class);
		console.logInfo(batchRunContext, total, position, "localTotal updateAdminTraces found: " + localTotal);
		CloseableIterator<AuditLogEntryAdmin> stream = mongoTemplate.stream(query, AuditLogEntryAdmin.class);
		AtomicInteger localPosition = new AtomicInteger(0);
		stream.forEachRemaining(entry -> {
			updateRelatedDomainsForAdminTraces(batchRunContext, localPosition, localTotal, entry);
			}
		);
	}

	private void updateUserTraces(BatchRunContext batchRunContext, long total, long position) {
		Query query = Query.query(
				Criteria.where
				("type").in(Lists.newArrayList(
//						AuditLogEntryType.AUTHENTICATION.toString(),	// ?
						AuditLogEntryType.DOCUMENT_ENTRY.toString(),
						AuditLogEntryType.GUEST.toString(),
						AuditLogEntryType.JWT_PERMANENT_TOKEN.toString(),
						AuditLogEntryType.CONTACTS_LISTS.toString(),
						AuditLogEntryType.CONTACTS_LISTS_CONTACTS.toString(),
//						AuditLogEntryType.GUEST_MODERATOR.toString(), // Handled by a specific case.
						AuditLogEntryType.SAFE_DETAIL.toString(),
//						AuditLogEntryType.SHARE_ENTRY.toString(), // Handled by a specific case.
						AuditLogEntryType.ANONYMOUS_SHARE_ENTRY.toString(),
						AuditLogEntryType.UPLOAD_REQUEST.toString(),
						AuditLogEntryType.UPLOAD_REQUEST_ENTRY.toString(),
						AuditLogEntryType.UPLOAD_REQUEST_GROUP.toString(),
						AuditLogEntryType.UPLOAD_REQUEST_URL.toString(),
//						AuditLogEntryType.USER.toString(), // Handled by a specific case.
						AuditLogEntryType.USER_PREFERENCE.toString(),
//						AuditLogEntryType.WORK_GROUP.toString(), // Handled by a specific case.
//						AuditLogEntryType.WORKGROUP_MEMBER.toString(), // Handled by a specific case.
//						AuditLogEntryType.WORK_SPACE.toString(), // Handled by a specific case.
//						AuditLogEntryType.WORK_SPACE_MEMBER.toString(), // Handled by a specific case.
//						AuditLogEntryType.WORKGROUP_DOCUMENT.toString(), // Handled by a specific case.
//						AuditLogEntryType.WORKGROUP_DOCUMENT_REVISION.toString(), // Handled by a specific case.
//						AuditLogEntryType.WORKGROUP_FOLDER.toString(), // Handled by a specific case.
						AuditLogEntryType.RESET_PASSWORD.toString()
					)).and
				("relatedDomains").exists(false));
		long localTotal = mongoTemplate.count(query, AuditLogEntryUser.class);
		console.logInfo(batchRunContext, total, position, "localTotal updateUserTraces found: " + localTotal);
		CloseableIterator<AuditLogEntryUser> stream = mongoTemplate.stream(query, AuditLogEntryUser.class);
		AtomicInteger localPosition = new AtomicInteger(0);
		stream.forEachRemaining(entry -> {
				updateRelatedDomainsForUserTraces(batchRunContext, localPosition, localTotal, entry);
			}
		);
	}

	private void peculiarUseCaseSharedSpaceNodeTraces(BatchRunContext batchRunContext, long total, long position) {
		Query query = Query.query(
				Criteria.where
				("type").in(Lists.newArrayList(
						AuditLogEntryType.WORK_GROUP.toString(),
						AuditLogEntryType.WORKGROUP.toString(),
						AuditLogEntryType.WORK_SPACE.toString()
					)).and
				("relatedDomains").exists(false));
		long localTotal = mongoTemplate.count(query, AuditLogEntryUser.class);
		console.logInfo(batchRunContext, total, position, "localTotal peculiarUseCaseSharedSpacesTraces found: " + localTotal);
		CloseableIterator<AuditLogEntryUser> stream = mongoTemplate.stream(query, AuditLogEntryUser.class);
		AtomicInteger localPosition = new AtomicInteger(0);
		stream.forEachRemaining(entry -> {
			updateRelatedDomainsForShareSpaceNodeTraces(batchRunContext, localPosition, localTotal, entry);
			}
		);
	}

	private void peculiarUseCaseSharedSpaceMemberTraces(BatchRunContext batchRunContext, long total, long position) {
		Query query = Query.query(
				Criteria.where
				("type").in(Lists.newArrayList(
						AuditLogEntryType.WORKGROUP_MEMBER.toString(),
						AuditLogEntryType.WORK_SPACE_MEMBER.toString()
					)).and
				("relatedDomains").exists(false));
		long localTotal = mongoTemplate.count(query, AuditLogEntryUser.class);
		console.logInfo(batchRunContext, total, position, "localTotal peculiarUseCaseSharedSpacesTraces found: " + localTotal);
		CloseableIterator<AuditLogEntryUser> stream = mongoTemplate.stream(query, AuditLogEntryUser.class);
		AtomicInteger localPosition = new AtomicInteger(0);
		stream.forEachRemaining(entry -> {
			updateRelatedDomainsForShareSpaceMemberTraces(batchRunContext, localPosition, localTotal, entry);
			}
		);
	}

	private void updateRelatedDomainsForAdminTraces(BatchRunContext batchRunContext, AtomicInteger position, long total, AuditLogEntryAdmin entry) {
		if ((position.incrementAndGet() % 100) == 0) {
			console.logInfo(batchRunContext, total, position.get(), "Processing updateAdminTraces ... " + entry.getType() + " : " + entry.getUuid());
		}
		entry.addRelatedDomains(
				entry.getAuthUser().getDomain().getUuid(),
				entry.getTargetDomainUuid()
		);
		mongoTemplate.save(entry);
	}

	private void updateRelatedDomainsForUserResourceTraces(BatchRunContext batchRunContext, AtomicInteger position, long total, AuditLogEntryUser entry) {
		if ((position.incrementAndGet() % 100) == 0) {
			console.logInfo(batchRunContext, total, position.get(), "Processing peculiarUseCaseUsers ... " + entry.getType() + " : " + entry.getUuid());
		}
		entry.addRelatedDomains(
				entry.getAuthUser().getDomain().getUuid(),
				entry.getActor().getDomain().getUuid(),
				((UserAuditLogEntry)entry).getResource().getDomain().getUuid()
		);
		mongoTemplate.save(entry);
	}

	private void updateRelatedDomainsForModeratorResourceTraces(BatchRunContext batchRunContext, AtomicInteger position, long total, AuditLogEntryUser entry) {
		if ((position.incrementAndGet() % 100) == 0) {
			console.logInfo(batchRunContext, total, position.get(), "Processing peculiarUseCaseModerators ... " + entry.getType() + " : " + entry.getUuid());
		}
		entry.addRelatedDomains(
				entry.getAuthUser().getDomain().getUuid(),
				entry.getActor().getDomain().getUuid(),
				((ModeratorAuditLogEntry)entry).getResource().getAccount().getDomain().getUuid(),
				((ModeratorAuditLogEntry)entry).getResource().getGuest().getDomain().getUuid()
				);
		mongoTemplate.save(entry);
	}

	private void updateRelatedDomainsForShareEntryResourceTraces(BatchRunContext batchRunContext, AtomicInteger position, long total, AuditLogEntryUser entry) {
		if ((position.incrementAndGet() % 100) == 0) {
			console.logInfo(batchRunContext, total, position.get(), "Processing peculiarUseCaseShareEntries ... " + entry.getType() + " : " + entry.getUuid());
		}
		EntryMto resource = ((ShareEntryAuditLogEntry)entry).getResource();
		ShareEntryMto shareEntry = (ShareEntryMto)resource;
		entry.addRelatedDomains(
				entry.getAuthUser().getDomain().getUuid(),
				entry.getActor().getDomain().getUuid(),
				shareEntry.getSender().getDomain().getUuid(),
				shareEntry.getRecipient().getDomain().getUuid()
		);
		mongoTemplate.save(entry);
	}

	private void updateRelatedDomainsForUserTraces(BatchRunContext batchRunContext, AtomicInteger position, long total, AuditLogEntryUser entry) {
		if ((position.incrementAndGet() % 100) == 0) {
			console.logInfo(batchRunContext, total, position.get(), "Processing updateUserTraces ... " + entry.getType() + " : " + entry.getUuid());
		}
		entry.addRelatedDomains(
				entry.getAuthUser().getDomain().getUuid(),
				entry.getActor().getDomain().getUuid()
		);
		mongoTemplate.save(entry);
	}

	private void updateRelatedDomainsForShareSpaceNodeTraces(BatchRunContext batchRunContext, AtomicInteger position, long total, AuditLogEntryUser entry) {
		if ((position.incrementAndGet() % 100) == 0) {
			console.logInfo(batchRunContext, total, position.get(), "Processing updateRelatedDomainsForShareSpaceNodeTraces ... " + entry.getType() + " : " + entry.getUuid());
		}
		SharedSpaceNodeAuditLogEntry node = (SharedSpaceNodeAuditLogEntry) entry;
		List<String> relatedDomains = Lists.newArrayList();
		relatedDomains.add(node.getAuthUser().getDomain().getUuid());
		relatedDomains.add(node.getActor().getDomain().getUuid());

		List<String> relatedAccounts = Lists.newArrayList();
		Account author = accountRepository.findActivateAndDestroyedByLsUuid(node.getResource().getAuthor().getUuid());
		if (Objects.nonNull(author)) {
			// add missing information
			node.getResource().getAuthor().setDomainUuid(author.getDomainId());
			relatedDomains.add(author.getDomainId());
			// just in case we need it later.
			relatedAccounts.add(author.getLsUuid());
		}
		if (node.getRelatedAccounts() == null || node.getRelatedAccounts().isEmpty()) {
			// relatedAccounts field was never initialize :'(
			// we need to provide a workaround because we can fix it.
			// there is no way to know which users were member of this wg at the time of the creation of the audit trace.

			// fixing related accounts
			relatedAccounts.add(node.getAuthUser().getUuid());
			relatedAccounts.add(node.getActor().getUuid());
			node.getRelatedAccounts().addAll(relatedAccounts);
		}

		// fixing related domains
		for (String accountUuid : node.getRelatedAccounts()) {
			Account account = accountRepository.findActivateAndDestroyedByLsUuid(accountUuid);
			if (Objects.nonNull(account)) {
				relatedDomains.add(account.getDomainId());
			}
		}
		node.getRelatedDomains().addAll(relatedDomains);
		mongoTemplate.save(entry);
	}

	private void updateRelatedDomainsForShareSpaceMemberTraces(BatchRunContext batchRunContext, AtomicInteger position, long total, AuditLogEntryUser entry) {
		if ((position.incrementAndGet() % 100) == 0) {
			console.logInfo(batchRunContext, total, position.get(), "Processing updateRelatedDomainsForShareSpaceMemberTraces ... " + entry.getType() + " : " + entry.getUuid());
		}
		SharedSpaceMemberAuditLogEntry node = (SharedSpaceMemberAuditLogEntry) entry;
		List<String> relatedDomains = Lists.newArrayList();
		relatedDomains.add(node.getAuthUser().getDomain().getUuid());
		relatedDomains.add(node.getActor().getDomain().getUuid());

		List<String> relatedAccounts = Lists.newArrayList();
		Account member = accountRepository.findActivateAndDestroyedByLsUuid(node.getResource().getAccount().getUuid());
		if (Objects.nonNull(member)) {
			// add missing information
			node.getResource().getAccount().setDomainUuid(member.getDomainId());
			relatedDomains.add(member.getDomainId());
			// just in case we need it later.
			relatedAccounts.add(member.getLsUuid());
		}
		if (node.getRelatedAccounts() == null || node.getRelatedAccounts().isEmpty()) {
			// relatedAccounts field was never initialize :'(
			// we need to provide a workaround because we can fix it.
			// there is no way to know which users were member of this wg at the time of the creation of the audit trace.

			// fixing related accounts
			relatedAccounts.add(node.getAuthUser().getUuid());
			relatedAccounts.add(node.getActor().getUuid());
			node.getRelatedAccounts().addAll(relatedAccounts);
		}

		// fixing related domains
		for (String accountUuid : node.getRelatedAccounts()) {
			Account account = accountRepository.findActivateAndDestroyedByLsUuid(accountUuid);
			if (Objects.nonNull(account)) {
				relatedDomains.add(account.getDomainId());
			}
		}
		node.getRelatedDomains().addAll(relatedDomains);
		mongoTemplate.save(entry);
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<FakeContext> res = (BatchResultContext<FakeContext>) context;
		if (res.getProcessed()) {
			logInfo(batchRunContext, total, position, "All audit traces were updated with related domain information.");
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occured while adding related domain information to all audit traces", exception);
	}
}
