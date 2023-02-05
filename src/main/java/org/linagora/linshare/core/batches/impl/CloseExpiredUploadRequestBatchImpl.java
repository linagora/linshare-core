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

import org.linagora.linshare.core.batches.CloseExpiredUploadRequestBatch;
import org.linagora.linshare.core.batches.utils.OperationKind;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.job.quartz.UploadRequestBatchResultContext;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.UploadRequestWarnExpiryEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.UploadRequestService;

import com.google.common.collect.Lists;

public class CloseExpiredUploadRequestBatchImpl extends GenericBatchImpl implements CloseExpiredUploadRequestBatch {

	private final UploadRequestService uploadRequestService;

	private final MailBuildingService mailBuildingService;

	private final NotifierService notifierService;

	public CloseExpiredUploadRequestBatchImpl(
			AccountRepository<Account> accountRepository,
			final UploadRequestService uploadRequestService,
			final MailBuildingService mailBuildingService,
			final NotifierService notifierService) {
		super(accountRepository);
		this.uploadRequestService = uploadRequestService;
		this.mailBuildingService = mailBuildingService;
		this.notifierService = notifierService;
		this.operationKind = OperationKind.CLOSED;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		SystemAccount account = getSystemAccount();
		logger.info(getClass().toString() + " job starting ...");
		List<String> entries = uploadRequestService.findOutdatedRequests(account);
		logger.info(entries.size()
				+ " Upload Request(s) have been found to be closed");
		return entries;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		List<MailContainerWithRecipient> notifications = Lists.newArrayList();
		SystemAccount account = getSystemAccount();
		UploadRequest uploadRequest = uploadRequestService.find(account, null, identifier);
		ResultContext context = new UploadRequestBatchResultContext(uploadRequest);
		console.logInfo(batchRunContext, total, position, "processing upload request : ", uploadRequest.getUuid());
		uploadRequest.updateStatus(UploadRequestStatus.CLOSED);
		uploadRequest = uploadRequestService.updateRequest(account, uploadRequest.getUploadRequestGroup().getOwner(), uploadRequest);
		for (UploadRequestUrl u : uploadRequest.getUploadRequestURLs()) {
			if (uploadRequest.getEnableNotification()) {
				EmailContext ctx = new UploadRequestWarnExpiryEmailContext((User) uploadRequest.getUploadRequestGroup().getOwner(), uploadRequest, u, false);
				notifications.add(mailBuildingService.build(ctx));
			}
		}
		EmailContext ctx = new UploadRequestWarnExpiryEmailContext((User) uploadRequest.getUploadRequestGroup().getOwner(), uploadRequest, null, true);
		notifications.add(mailBuildingService.build(ctx));
		notifierService.sendNotification(notifications, true);
		context.setProcessed(true);
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		UploadRequestBatchResultContext uploadRequestContext = (UploadRequestBatchResultContext) context;
		UploadRequest r = uploadRequestContext.getResource();
		console.logInfo(batchRunContext, total, position,
				"The Upload Request " + r.getUuid() + " has been successfully closed.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		UploadRequestBatchResultContext uploadRequestContext = (UploadRequestBatchResultContext) exception.getContext();
		UploadRequest r = uploadRequestContext.getResource();
		console.logError(batchRunContext, total, position, "Closing upload request has failed : " + r.getUuid());
	}
}
