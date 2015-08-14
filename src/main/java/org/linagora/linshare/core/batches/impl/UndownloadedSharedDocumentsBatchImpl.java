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

import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.domain.entities.ShareLogEntry;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.Context;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.MailBuildingService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.ShareEntryGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UndownloadedSharedDocumentsBatchImpl extends GenericBatchImpl {

	private final ShareEntryGroupService service;

	private final MailBuildingService mailService;

	private final NotifierService notifierService;

	private final LogEntryService logService;

	public UndownloadedSharedDocumentsBatchImpl(
			final ShareEntryGroupService service,
			final MailBuildingService mailBuildingService,
			final NotifierService notifierService,
			final LogEntryService logService,
			AccountRepository<Account> accountRepository) {
		super(accountRepository);
		this.service = service;
		this.mailService = mailBuildingService;
		this.notifierService = notifierService;
		this.logService = logService;
	}

	@Override
	public List<String> getAll() {
		logger.info("UndownloadedSharedDocumentsBatchImpl job starting ...");
		List<String> allUuids = service.findAllAboutToBeNotified(
				getSystemAccount());
		logger.info(allUuids.size()
				+ " shareEntryGroup with undownloaded documents");
		return allUuids;
	}

	@Override
	public Context execute(String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		SystemAccount actor = getSystemAccount();
		ShareEntryGroup shareEntryGroup = service.findByUuid(actor, identifier);

		Context context = new BatchResultContext<ShareEntryGroup>(shareEntryGroup);
		MailContainerWithRecipient mail = null;
		try {
			// No more info ?
			logInfo(total, position, "processing shareEntryGroup : "
					+ shareEntryGroup.getUuid());
			logger.info("needNotification : " + shareEntryGroup.needNotification());
			if (shareEntryGroup.needNotification()) {
				// log action and notification
				mail = mailService.buildNoDocumentHasBeenDownloadedAcknowledgment(shareEntryGroup);
				shareEntryGroup.setNotified(true);
				service.update(actor, shareEntryGroup);
				logActions(shareEntryGroup, LogAction.SHARE_WITH_USD_NOT_DOWNLOADED);
			} else {
				// only log action
				logActions(shareEntryGroup, LogAction.SHARE_WITH_USD_DOWNLOADED);
				// Nothing to do ? set notified to true ? or set expiration to null ? 
				// How to exclude them from finders ?
			}
		} catch (BusinessException businessException) {
			logError(total, position,
					"Error while trying to send a notification for undownloaded shared documents");
			logger.info("Error occured while sending notification ",
					businessException);
			BatchBusinessException exception = new BatchBusinessException(
					context,
					"Error while trying to send a notification for undownloaded shared documents");
			exception.setBusinessException(businessException);
			throw exception;
		}
		// Once every thing is ok, transaction is about to be committed, we can send the notification.
		notifierService.sendNotification(mail);
		return context;
	}

	private void logActions(ShareEntryGroup shareEntryGroup, LogAction logAction) {
		for (ShareEntry share : shareEntryGroup.getShareEntries()){
			ShareLogEntry logEntry = new ShareLogEntry(shareEntryGroup.getOwner(), share, logAction, "");
			logService.create(logEntry);
		}
		for (AnonymousShareEntry anonymousShare : shareEntryGroup.getAnonymousShareEntries()){
			ShareLogEntry logEntry = new ShareLogEntry(shareEntryGroup.getOwner(), anonymousShare, logAction, "");
			logService.create(logEntry);
		}
	}

	@Override
	public void notify(Context context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<ShareEntryGroup> shareEntryGroupContext = (BatchResultContext<ShareEntryGroup>) context;

		logInfo(total, position,
				"The notification for the shareEntryGroup "
						+ shareEntryGroupContext.getResource().getUuid()
						+ " has been successfully sent ");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier,
			long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<ShareEntryGroup> context = (BatchResultContext<ShareEntryGroup>) exception
				.getContext();
		logError(total, position,
				"Sending undownload shared documents notification has failed : "
						+ context.getResource().getUuid());
		logger.error(
				"Error occured while Sending undownload shared documents notification "
						+ context.getResource().getUuid()
						+ ". BatchBusinessException ",
				exception);
	}

	@Override
	public void terminate(List<String> all, long errors, long unhandled_errors,
			long total) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " notification have been sent.");
		if (errors > 0) {
			logger.error(errors + " notifications has not been sent.");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors
					+ " notification failed to be sent (unhandled error).");
		}
		logger.info("UndownloadedSharedDocumentsBatchImpl job terminated.");
	}
}
