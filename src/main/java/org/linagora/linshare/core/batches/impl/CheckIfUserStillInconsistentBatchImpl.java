/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

package org.linagora.linshare.core.batches.impl;

import java.util.List;

import org.linagora.linshare.core.batches.CheckIfUserStillInconsistentBatch;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.CheckIfUserStillInconsistentBatchResultContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.InconsistentUserService;
import org.linagora.linshare.core.service.UserService;

public class CheckIfUserStillInconsistentBatchImpl extends GenericBatchImpl implements CheckIfUserStillInconsistentBatch {

	private final InconsistentUserService internaleService;

	private final UserService userService;

	private final AbstractDomainService abstractDomainService;

	public CheckIfUserStillInconsistentBatchImpl(AccountRepository<Account> accountRepository,
			final InconsistentUserService service,
			final UserService userService,
			final AbstractDomainService abstractDomainService) {
		super(accountRepository);
		this.internaleService = service;
		this.userService = userService;
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		SystemAccount account = getSystemAccount();
		logger.info(getClass().toString() + " job starting ...");
		List<String> entries = internaleService.findAllIconsistentsUuid(account);
		logger.info(entries.size()
				+ " Inconsistent users have been found to be processed.");
		return entries;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		SystemAccount account = getSystemAccount();
		User user  = userService.findByLsUuid(identifier);
		ResultContext c = new CheckIfUserStillInconsistentBatchResultContext(user);
		logInfo(batchRunContext,
				total,
				position, "processing internal : "
						+ user.getAccountRepresentation());
		c.setProcessed(false);
		if (abstractDomainService.isUserExist(user.getDomain(), user.getMail())) {
			user.setInconsistent(false);
			userService.updateUser(account, user, user.getDomainId());
			c.setProcessed(true);
		}
		return c;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		CheckIfUserStillInconsistentBatchResultContext c = (CheckIfUserStillInconsistentBatchResultContext) context;
		User u = c.getResource();
		if (c.getProcessed()) {
			logInfo(batchRunContext, total, position, "The inconsistent user " + u.getLsUuid() + " has been successfully checked.");
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		CheckIfUserStillInconsistentBatchResultContext c = (CheckIfUserStillInconsistentBatchResultContext) exception.getContext();
		User u = c.getResource();
		logError(total, position, "Flaging user has failed " + u.getLsUuid() + ". BatchBusinessException ", batchRunContext, exception);
	}

	@Override
	public void terminate(BatchRunContext batchRunContext, List<String> all, long errors, long unhandled_errors, long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " user(s) have been checked.");
		logger.info(processed + " user(s) have been unflagged.");
		if (errors > 0) {
			logger.error(errors + " user(s) failed to be unflagged.");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors + " user(s) failed to be unflagged (unhandled error)");
		}
		logger.info(getClass().toString() + " job terminated.");
	}
}
