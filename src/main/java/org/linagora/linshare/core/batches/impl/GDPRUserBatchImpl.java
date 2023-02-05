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
package org.linagora.linshare.core.batches.impl;

import java.util.List;

import org.linagora.linshare.core.batches.impl.gdpr.AnonymizeAuditLogEntry;
import org.linagora.linshare.core.batches.impl.gdpr.AnonymizeMailingListLogEntry;
import org.linagora.linshare.core.batches.impl.gdpr.AnonymizeModeratorLogEntry;
import org.linagora.linshare.core.batches.impl.gdpr.AnonymizeSharedSpace;
import org.linagora.linshare.core.batches.impl.gdpr.AnonymizeThreadAuditLogEntry;
import org.linagora.linshare.core.batches.impl.gdpr.AnonymizeWorkGroup;
import org.linagora.linshare.core.batches.impl.gdpr.GDPRConstants;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.AccountBatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.TimeService;
import org.springframework.data.mongodb.core.MongoTemplate;

public class GDPRUserBatchImpl extends GenericBatchImpl {

	private final TimeService timeService;
	private final UserRepository userRepository;
	private final MongoTemplate mongoTemplate;
	private final boolean gdprActivated;

	public GDPRUserBatchImpl(
			AccountRepository<Account> accountRepository,
			TimeService timeService,
			UserRepository userRepository,
			MongoTemplate mongoTemplate,
			boolean gdprActivated) {
		super(accountRepository);
		this.timeService = timeService;
		this.userRepository = userRepository;
		this.mongoTemplate = mongoTemplate;
		this.gdprActivated = gdprActivated;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		return userRepository.findAllNonAnonymizedPurgedAccounts(timeService.previousYear());
	}

	@Override
	public boolean needToRun() {
		return gdprActivated;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position) throws BatchBusinessException, BusinessException {
		User user = (User) userRepository.findActivateAndDestroyedByLsUuid(identifier);
		ResultContext context = new AccountBatchResultContext(user);

		new AnonymizeAuditLogEntry(mongoTemplate).process(identifier);
		new AnonymizeMailingListLogEntry(mongoTemplate).process(identifier);
		new AnonymizeModeratorLogEntry(mongoTemplate).process(identifier);
		new AnonymizeSharedSpace(mongoTemplate).process(identifier);
		new AnonymizeThreadAuditLogEntry(mongoTemplate).process(identifier);
		new AnonymizeWorkGroup(mongoTemplate).process(identifier);

		user.setMail(GDPRConstants.MAIL_ANONYMIZATION);
		user.setFirstName(GDPRConstants.FIRST_NAME_ANONYMIZATION);
		user.setLastName(GDPRConstants.LAST_NAME_ANONYMIZATION);
		userRepository.update(user);

		context.setProcessed(true);
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		AccountBatchResultContext accountContext = (AccountBatchResultContext) context;
		Account account = accountContext.getResource();
		console.logInfo(batchRunContext, total, position, "The User " + account.getLsUuid() + " has been anonymized processed.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		AccountBatchResultContext accountContext = (AccountBatchResultContext) exception.getContext();
		Account account = accountContext.getResource();
		console.logError(batchRunContext, total, position, "Anonymization for user has failed : " + account.getLsUuid());
	}
}
