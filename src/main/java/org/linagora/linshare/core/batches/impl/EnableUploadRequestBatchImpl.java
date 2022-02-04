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
