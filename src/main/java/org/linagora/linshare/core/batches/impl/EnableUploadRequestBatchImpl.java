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

import org.linagora.linshare.core.batches.EnableUploadRequestBatch;
import org.linagora.linshare.core.business.service.UploadRequestGroupBusinessService;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.job.quartz.UploadRequestBatchResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.UploadRequestService;

public class EnableUploadRequestBatchImpl extends GenericBatchImpl implements EnableUploadRequestBatch {

	private final UploadRequestService uploadRequestService;

	private final UploadRequestGroupBusinessService uploadRequestGroupBusinessService;

	public EnableUploadRequestBatchImpl(
			AccountRepository<Account> accountRepository,
			final UploadRequestService uploadRequestService,
			final UploadRequestGroupBusinessService uploadRequestGroupBusinessService) {
		super(accountRepository);
		this.uploadRequestService = uploadRequestService;
		this.uploadRequestGroupBusinessService = uploadRequestGroupBusinessService;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		SystemAccount account = getSystemAccount();
		logger.info(getClass().toString() + " job starting ...");
		List<String> entries = uploadRequestService.findUnabledRequests(account);
		logger.info(entries.size()
				+ " Upload Request(s) have been found to be enabled");
		return entries;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		SystemAccount account = getSystemAccount();
		UploadRequest uploadRequest = uploadRequestService.find(account, account, identifier);
		ResultContext context = new UploadRequestBatchResultContext(uploadRequest);
		console.logInfo(batchRunContext, total, position, "processing upload request : ", uploadRequest.getUuid());
		uploadRequest = uploadRequestService.updateStatus(account, uploadRequest.getUploadRequestGroup().getOwner(),
				identifier, UploadRequestStatus.ENABLED, false);
		UploadRequestGroup group = uploadRequest.getUploadRequestGroup();
		if (group.getStatus().equals(UploadRequestStatus.ENABLED)) {
			logger.debug("The URG {} is already enabled", group);
		} else {
			uploadRequestGroupBusinessService.updateStatus(group, UploadRequestStatus.ENABLED);
		}
		context.setProcessed(true);
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		UploadRequestBatchResultContext uploadRequestContext = (UploadRequestBatchResultContext) context;
		UploadRequest r = uploadRequestContext.getResource();
		console.logInfo(batchRunContext, total, position, "The Upload Request "
				+ r.getUuid()
				+ " has been successfully enabled.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		UploadRequestBatchResultContext uploadRequestContext = (UploadRequestBatchResultContext) exception.getContext();
		UploadRequest r = uploadRequestContext.getResource();
		console.logError(
				batchRunContext,
				total,
				position,
				"Enabling upload request has failed : "
						+ r.getUuid());
	}

	@Override
	public void terminate(BatchRunContext batchRunContext, List<String> all, long errors, long unhandled_errors, long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success
				+ " upload request(s) have been enabled.");
		if (errors > 0) {
			logger.error(errors
					+ " upload request(s) failed to be enabled.");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors
					+ " upload request(s) failed to be enabled(unhandled error).");
		}
		logger.info("EnableUploadRequestBatchImpl job terminated.");
	}
}
