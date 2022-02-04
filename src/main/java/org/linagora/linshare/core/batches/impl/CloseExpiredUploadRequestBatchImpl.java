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
		notifierService.sendNotification(notifications);
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
