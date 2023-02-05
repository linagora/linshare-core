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

import org.linagora.linshare.core.batches.NotifyBeforeExpirationUploadRequestBatch;
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
import org.linagora.linshare.core.notifications.context.UploadRequestWarnBeforeExpiryEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.UploadRequestService;

import com.google.common.collect.Lists;

public class NotifyBeforeExpirationUploadRequestBatchImpl extends GenericBatchImpl
		implements NotifyBeforeExpirationUploadRequestBatch {

	private final MailBuildingService mailBuildingService;

	private final NotifierService notifierService;

	private final UploadRequestService service;

	public NotifyBeforeExpirationUploadRequestBatchImpl(
			AccountRepository<Account> accountRepository,
			final MailBuildingService mailBuildingService,
			final NotifierService notifierService,
			final UploadRequestService service) {
		super(accountRepository);
		this.mailBuildingService = mailBuildingService;
		this.notifierService = notifierService;
		this.service  = service;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		SystemAccount account = getSystemAccount();
		logger.info(getClass().toString() + " job starting ...");
		List<String> entries = service.findAllRequestsToBeNotified(account);
		logger.info(entries.size() + " Upload Request(s) have been found, notification before expiration will be sent to them.");
		return entries;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		List<MailContainerWithRecipient> notifications = Lists.newArrayList();
		UploadRequest uploadRequest = service.find(getSystemAccount(), null, identifier);
		ResultContext context = new UploadRequestBatchResultContext(uploadRequest);
		if (!uploadRequest.isNotified()) {
			for (UploadRequestUrl urUrl : uploadRequest.getUploadRequestURLs()) {
				if (uploadRequest.getEnableNotification()) {
					EmailContext ctx = new UploadRequestWarnBeforeExpiryEmailContext(
							(User) uploadRequest.getUploadRequestGroup().getOwner(), uploadRequest, urUrl, false);
					notifications.add(mailBuildingService.build(ctx));
				}
			}
			EmailContext ctx = new UploadRequestWarnBeforeExpiryEmailContext((User)uploadRequest.getUploadRequestGroup().getOwner(), uploadRequest, null, true);
			notifications.add(mailBuildingService.build(ctx));
			uploadRequest.setNotified(true);
			service.updateRequest(getSystemAccount(), uploadRequest.getUploadRequestGroup().getOwner(), uploadRequest);
			notifierService.sendNotification(notifications);
		}
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		UploadRequestBatchResultContext uploadRequestContext = (UploadRequestBatchResultContext) context;
		UploadRequest r = uploadRequestContext.getResource();
		console.logInfo(batchRunContext, total, position, "The Upload Request " + r.getUuid() + " has been successfully processed.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		UploadRequestBatchResultContext uploadRequestContext = (UploadRequestBatchResultContext) exception.getContext();
		UploadRequest r = uploadRequestContext.getResource();
		console.logError(batchRunContext, total, position, "Sending notifications for upload request has failed : " + r.getUuid());
	}

	@Override
	public void terminate(BatchRunContext batchRunContext, List<String> all, long errors, long unhandled_errors, long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " upload request(s) have been processed.");
		if (errors > 0) {
			logger.error(errors + " notification(s) failed to sent.");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors + " notification(s) failed to sent(unhandled error).");
		}
		logger.info(getClass().toString() + " job terminated.");
	}
}
