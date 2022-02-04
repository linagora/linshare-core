/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */

package org.linagora.linshare.core.batches.impl;

import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.AnonymousUrlBatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.AnonymousUrlService;

public class DeleteExpiredAnonymousUrlBatchImpl extends GenericBatchImpl {

	private final AnonymousUrlService service;

	public DeleteExpiredAnonymousUrlBatchImpl(
			final AccountRepository<Account> accountRepository,
			final AnonymousUrlService anonymousUrlService) {
		super(accountRepository);
		this.service = anonymousUrlService;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info(getClass().toString() + " job starting ...");
		SystemAccount actor = getSystemAccount();
		List<String> allExpiredUrl = service
				.findAllExpiredEntries(actor, actor);
		logger.info(allExpiredUrl.size()
				+ " anonymous url(s) have been found to be deleted");
		return allExpiredUrl;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		SystemAccount actor = getSystemAccount();
		AnonymousUrl resource = service.find(actor, actor, identifier);
		ResultContext context = new AnonymousUrlBatchResultContext(resource);
		try {
			console.logInfo(batchRunContext, total,
					position, "processing anonymous url : " + resource.getReprentation());
			service.delete(actor, actor, identifier);
			logger.info("Expired anonymous url was deleted : "
					+ resource.getReprentation());
		} catch (BusinessException businessException) {
			BatchBusinessException exception = new BatchBusinessException(
					context,
					"Error while trying to delete expired anonymous url.");
			exception.setBusinessException(businessException);
			console.logError(batchRunContext, total, position, "Error while trying to delete expired anonymous url",
					exception);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		AnonymousUrlBatchResultContext auContext = (AnonymousUrlBatchResultContext) context;
		AnonymousUrl url = auContext.getResource();
		console.logInfo(batchRunContext, total, position, "The anonymous url " + url.getReprentation()
				+ " has been successfully deleted.");
	}

	@Override
	public void notifyError(BatchBusinessException exception,
			String identifier, long total, long position, BatchRunContext batchRunContext) {
		AnonymousUrlBatchResultContext auContext = (AnonymousUrlBatchResultContext) exception
				.getContext();
		AnonymousUrl url = auContext.getResource();
		console.logError(batchRunContext, total, position,
				"cleaning anonymous url has failed : " + url.getReprentation());
	}

	@Override
	public void terminate(BatchRunContext batchRunContext, List<String> all, long errors,
			long unhandled_errors, long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " anonymous url(s) have been deleted.");
		if (errors > 0) {
			logger.error(errors + " anonymous url(s) failed to be deleted.");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors
					+ " anonymous url(s) failed to be deleted (unhandled error).");
		}
		logger.info(getClass().toString() + " job terminated.");
	}
}
