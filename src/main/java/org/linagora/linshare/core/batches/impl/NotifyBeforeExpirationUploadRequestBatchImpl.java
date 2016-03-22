/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
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

import org.linagora.linshare.core.batches.NotifyBeforeExpirationUploadRequestBatch;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.Context;
import org.linagora.linshare.core.job.quartz.UploadRequestBatchResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.UploadRequestRepository;
import org.linagora.linshare.core.service.MailBuildingService;
import org.linagora.linshare.core.service.NotifierService;

import com.google.common.collect.Lists;

public class NotifyBeforeExpirationUploadRequestBatchImpl extends GenericBatchImpl
		implements NotifyBeforeExpirationUploadRequestBatch {

	private final UploadRequestRepository uploadRequestRepository;
	private final MailBuildingService mailBuildingService;
	private final NotifierService notifierService;

	public NotifyBeforeExpirationUploadRequestBatchImpl(
			AccountRepository<Account> accountRepository,
			final UploadRequestRepository uploadRequestRepository,
			final MailBuildingService mailBuildingService,
			final NotifierService notifierService) {
		super(accountRepository);
		this.uploadRequestRepository = uploadRequestRepository;
		this.mailBuildingService = mailBuildingService;
		this.notifierService = notifierService;
	}

	@Override
	public List<String> getAll() {
		logger.info(getClass().toString() + " job starting ...");
		List<String> entries = uploadRequestRepository.findAllRequestsToBeNotified();
		logger.info(entries.size() + " Upload Request(s) have been found to be enabled");
		return entries;
	}

	@Override
	public Context execute(String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		List<MailContainerWithRecipient> notifications = Lists.newArrayList();
		UploadRequest r = uploadRequestRepository.findByUuid(identifier);
		Context context = new UploadRequestBatchResultContext(r);
		for (UploadRequestUrl u : r.getUploadRequestURLs()) {
			notifications.add(mailBuildingService.buildUploadRequestBeforeExpiryWarnRecipient((User) r.getOwner(), u));
		}
		notifications.add(mailBuildingService.buildUploadRequestBeforeExpiryWarnOwner((User) r.getOwner(), r));
		notifierService.sendNotification(notifications);
		return context;
	}

	@Override
	public void notify(Context context, long total, long position) {
		UploadRequestBatchResultContext uploadRequestContext = (UploadRequestBatchResultContext) context;
		UploadRequest r = uploadRequestContext.getResource();
		logInfo(total, position, "The Upload Request " + r.getUuid() + " has been successfully enabled.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position) {
		UploadRequestBatchResultContext uploadRequestContext = (UploadRequestBatchResultContext) exception.getContext();
		UploadRequest r = uploadRequestContext.getResource();
		logError(total, position, "Enabling upload request has failed : " + r.getUuid());
		logger.error("Error occured while enabling upload request " + r.getUuid() + ". BatchBusinessException ",
				exception);
	}

	@Override
	public void terminate(List<String> all, long errors, long unhandled_errors, long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " upload request(s) have been enabled.");
		if (errors > 0) {
			logger.error(errors + " upload request(s) failed to be enabled.");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors + " upload request(s) failed to be enabled(unhandled error).");
		}
		logger.info("EnableUploadRequestBatchImpl job terminated.");
	}
}
