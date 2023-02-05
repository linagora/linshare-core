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
package org.linagora.linshare.core.upgrade.v5_1;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryAdmin;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.entities.logs.ModeratorAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.ShareEntryAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.SharedSpaceMemberAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.SharedSpaceNodeAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.ThreadAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.UserAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.WorkGroupNodeAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.AnonymousShareEntryMto;
import org.linagora.linshare.mongo.entities.mto.DomainMto;
import org.linagora.linshare.mongo.entities.mto.EntryMto;
import org.linagora.linshare.mongo.entities.mto.ShareEntryMto;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.CloseableIterator;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@SuppressWarnings("deprecation")
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

		// Peculiar use case : AuditLogEntryType WORKGROUP_DOCUMENT WORKGROUP_DOCUMENT_REVISION and WORKGROUP_FOLDER
		peculiarUseCaseWorkGroupNodeTraces(batchRunContext, total, position);

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
//						AuditLogEntryType.AUTHENTICATION.toString(), // Handled by a specific case.
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
		console.logInfo(batchRunContext, total, position, "localTotal peculiarUseCaseSharedSpaceNodeTraces found: " + localTotal);
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
		console.logInfo(batchRunContext, total, position, "localTotal peculiarUseCaseSharedSpaceMemberTraces found: " + localTotal);
		CloseableIterator<AuditLogEntryUser> stream = mongoTemplate.stream(query, AuditLogEntryUser.class);
		AtomicInteger localPosition = new AtomicInteger(0);
		stream.forEachRemaining(entry -> {
			updateRelatedDomainsForShareSpaceMemberTraces(batchRunContext, localPosition, localTotal, entry);
			}
		);
	}

	private void peculiarUseCaseWorkGroupNodeTraces(BatchRunContext batchRunContext, long total, long position) {
		Query query = Query.query(
				Criteria.where
				("type").in(Lists.newArrayList(
						AuditLogEntryType.WORKGROUP_DOCUMENT.toString(),
						AuditLogEntryType.WORKGROUP_DOCUMENT_REVISION.toString(),
						AuditLogEntryType.WORKGROUP_FOLDER.toString()
					)).and
				("relatedDomains").exists(false));
		long localTotal = mongoTemplate.count(query, AuditLogEntryUser.class);
		console.logInfo(batchRunContext, total, position, "localTotal peculiarUseCaseWorkGroupNodeTraces found: " + localTotal);
		CloseableIterator<AuditLogEntryUser> stream = mongoTemplate.stream(query, AuditLogEntryUser.class);
		AtomicInteger localPosition = new AtomicInteger(0);
		stream.forEachRemaining(entry -> {
			updateRelatedDomainsForWorkGroupNodeTraces(batchRunContext, localPosition, localTotal, entry);
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
		if (resource instanceof ShareEntryMto) {
			ShareEntryMto shareEntry = (ShareEntryMto)resource;
			entry.addRelatedDomains(
					entry.getAuthUser().getDomain().getUuid(),
					entry.getActor().getDomain().getUuid(),
					shareEntry.getSender().getDomain().getUuid(),
					shareEntry.getRecipient().getDomain().getUuid()
			);
		} else {
			AnonymousShareEntryMto shareEntry = (AnonymousShareEntryMto)resource;
			entry.addRelatedDomains(
					entry.getAuthUser().getDomain().getUuid(),
					entry.getActor().getDomain().getUuid(),
					shareEntry.getSender().getDomain().getUuid()
			);
		}
		mongoTemplate.save(entry);

	}

	private void updateRelatedDomainsForUserTraces(BatchRunContext batchRunContext, AtomicInteger position, long total, AuditLogEntryUser entry) {
		if ((position.incrementAndGet() % 100) == 0) {
			console.logInfo(batchRunContext, total, position.get(), "Processing updateUserTraces ... " + entry.getType() + " : " + entry.getUuid());
		}
		entry.addRelatedDomains(entry.getAuthUser().getDomain().getUuid());
		DomainMto actorDomain = entry.getActor().getDomain();
		if (actorDomain != null) {
			entry.addRelatedDomains(actorDomain.getUuid());
		}
		mongoTemplate.save(entry);
	}

	private void updateRelatedDomainsForShareSpaceNodeTraces(BatchRunContext batchRunContext, AtomicInteger position, long total, AuditLogEntryUser entry) {
		if ((position.incrementAndGet() % 100) == 0) {
			console.logInfo(batchRunContext, total, position.get(), "Processing updateRelatedDomainsForShareSpaceNodeTraces ... " + entry.getType() + " : " + entry.getUuid());
		}
		Set<String> relatedDomains = Sets.newHashSet();
		relatedDomains.add(entry.getAuthUser().getDomain().getUuid());
		relatedDomains.add(entry.getActor().getDomain().getUuid());

		Set<String> relatedAccounts = Sets.newHashSet();
		Account author = null;
		SharedSpaceAccount authorTrace = null;
		if (!(entry instanceof ThreadAuditLogEntry)) {
			// we ignore explicitly this old class, otherwise we want to make LinShare crash if it is not the right type.
			SharedSpaceNodeAuditLogEntry node = (SharedSpaceNodeAuditLogEntry) entry;
			authorTrace = node.getResource().getAuthor();
		}
		if (authorTrace != null) {
			author = accountRepository.findActivateAndDestroyedByLsUuid(authorTrace.getUuid());
		}
		if (Objects.nonNull(author)) {
			// add missing information
			authorTrace.setDomainUuid(author.getDomainId());
			relatedDomains.add(author.getDomainId());
			// just in case we need it later.
			relatedAccounts.add(author.getLsUuid());
		}
		if (entry.getRelatedAccounts() == null || entry.getRelatedAccounts().isEmpty()) {
			// relatedAccounts field was never initialize :'(
			// we need to provide a workaround because we can fix it.
			// there is no way to know which users were member of this wg at the time of the creation of the audit trace.

			// fixing related accounts
			relatedAccounts.add(entry.getAuthUser().getUuid());
			relatedAccounts.add(entry.getActor().getUuid());
			entry.addRelatedAccounts(relatedAccounts);
		}

		// fixing related domains
		for (String accountUuid : new HashSet<String>(entry.getRelatedAccounts())) {
			Account account = accountRepository.findActivateAndDestroyedByLsUuid(accountUuid);
			if (Objects.nonNull(account)) {
				relatedDomains.add(account.getDomainId());
			}
		}
		entry.addRelatedDomains(relatedDomains);
		mongoTemplate.save(entry);
	}

	private void updateRelatedDomainsForShareSpaceMemberTraces(BatchRunContext batchRunContext, AtomicInteger position, long total, AuditLogEntryUser entry) {
		if ((position.incrementAndGet() % 100) == 0) {
			console.logInfo(batchRunContext, total, position.get(), "Processing updateRelatedDomainsForShareSpaceMemberTraces ... " + entry.getType() + " : " + entry.getUuid());
		}
		SharedSpaceMemberAuditLogEntry node = (SharedSpaceMemberAuditLogEntry) entry;
		Set<String> relatedDomains = Sets.newHashSet();
		relatedDomains.add(node.getAuthUser().getDomain().getUuid());
		relatedDomains.add(node.getActor().getDomain().getUuid());

		Set<String> relatedAccounts = Sets.newHashSet();
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
			node.addRelatedAccounts(relatedAccounts);
		}

		// fixing related domains
		for (String accountUuid : new HashSet<String>(node.getRelatedAccounts())) {
			Account account = accountRepository.findActivateAndDestroyedByLsUuid(accountUuid);
			if (Objects.nonNull(account)) {
				relatedDomains.add(account.getDomainId());
			}
		}
		node.addRelatedDomains(relatedDomains);
		mongoTemplate.save(entry);
	}

	private void updateRelatedDomainsForWorkGroupNodeTraces(BatchRunContext batchRunContext, AtomicInteger position, long total, AuditLogEntryUser entry) {
		if ((position.incrementAndGet() % 100) == 0) {
			console.logInfo(batchRunContext, total, position.get(), "Processing updateRelatedDomainsForShareSpaceMemberTraces ... " + entry.getType() + " : " + entry.getUuid());
		}
		WorkGroupNodeAuditLogEntry node = (WorkGroupNodeAuditLogEntry) entry;
		Set<String> relatedDomains = Sets.newHashSet();
		relatedDomains.add(node.getAuthUser().getDomain().getUuid());
		relatedDomains.add(node.getActor().getDomain().getUuid());

		// fixing related domains
		for (String accountUuid : new HashSet<String>(node.getRelatedAccounts())) {
			Account account = accountRepository.findActivateAndDestroyedByLsUuid(accountUuid);
			if (Objects.nonNull(account)) {
				relatedDomains.add(account.getDomainId());
			}
		}
		node.addRelatedDomains(relatedDomains);
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
