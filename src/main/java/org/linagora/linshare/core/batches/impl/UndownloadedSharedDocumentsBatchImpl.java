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

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.ShareWarnUndownloadedFilesharesEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.ShareEntryGroupService;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.entities.logs.ShareEntryAuditLogEntry;

import com.google.common.collect.Lists;

public class UndownloadedSharedDocumentsBatchImpl extends GenericBatchImpl {

	private final ShareEntryGroupService service;

	private final MailBuildingService mailService;

	private final NotifierService notifierService;

	private final LogEntryService logEntryService;

	public UndownloadedSharedDocumentsBatchImpl(
			final ShareEntryGroupService service,
			final MailBuildingService mailBuildingService,
			final NotifierService notifierService,
			final LogEntryService logEntryService,
			AccountRepository<Account> accountRepository) {
		super(accountRepository);
		this.service = service;
		this.mailService = mailBuildingService;
		this.notifierService = notifierService;
		this.logEntryService = logEntryService;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info("UndownloadedSharedDocumentsBatchImpl job starting ...");
		List<String> allUuids = service.findAllAboutToBeNotified(getSystemAccount(), getSystemAccount());
		logger.info(allUuids.size() + " shareEntryGroup with undownloaded documents");
		return allUuids;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		SystemAccount actor = getSystemAccount();
		ShareEntryGroup shareEntryGroup = service.find(actor, actor, identifier);

		ResultContext context = new BatchResultContext<ShareEntryGroup>(shareEntryGroup);
		MailContainerWithRecipient mail = null;
		List<AuditLogEntryUser> logs = null;
		try {
			// No more info ?
			console.logInfo(batchRunContext, total, position, "processing shareEntryGroup : " + shareEntryGroup.getUuid());
			logger.info("needNotification : " + shareEntryGroup.needNotification());
			shareEntryGroup.setProcessed(true);
			if (shareEntryGroup.needNotification()) {
				// log action and notification
				EmailContext emailContext = new ShareWarnUndownloadedFilesharesEmailContext(shareEntryGroup);
				mail = mailService.build(emailContext);
				shareEntryGroup.setNotified(true);
				logs = getLogActions(actor, shareEntryGroup);
			} else {
				// only log action
				logs = getLogActions(actor, shareEntryGroup);
				// Nothing to do ? set notified to true ? or set expiration to
				// null ?
				// How to exclude them from finders ?
			}
			service.update(actor, actor, shareEntryGroup);
		} catch (BusinessException businessException) {
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to send a notification for undownloaded shared documents");
			exception.setBusinessException(businessException);
			console.logError(batchRunContext, total, position,
					"Error while trying to send a notification for undownloaded shared documents", exception);
			throw exception;
		}
		// Once every thing is ok, transaction is about to be committed, we can
		// send the notification.
		notifierService.sendNotification(mail);
		logEntryService.insert(logs);
		return context;
	}

	private List<AuditLogEntryUser> getLogActions(SystemAccount actor, ShareEntryGroup shareEntryGroup) {
		List<AuditLogEntryUser> logs = Lists.newArrayList();
		Account owner = shareEntryGroup.getOwner();
		for (ShareEntry share : shareEntryGroup.getShareEntries()) {
			ShareEntryAuditLogEntry log = new ShareEntryAuditLogEntry(actor, owner, LogAction.UPDATE, share, AuditLogEntryType.SHARE_ENTRY);
			logs.add(log);
		}
		for (AnonymousShareEntry anonymousShare : shareEntryGroup.getAnonymousShareEntries()) {
			ShareEntryAuditLogEntry log = new ShareEntryAuditLogEntry(actor, owner, LogAction.UPDATE, anonymousShare, AuditLogEntryType.SHARE_ENTRY);
			logs.add(log);
		}
		return logs;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<ShareEntryGroup> shareEntryGroupContext = (BatchResultContext<ShareEntryGroup>) context;

		console.logInfo(batchRunContext, total, position, "The notification for the shareEntryGroup "
				+ shareEntryGroupContext.getResource().getUuid() + " has been successfully sent ");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<ShareEntryGroup> context = (BatchResultContext<ShareEntryGroup>) exception.getContext();
		console.logError(batchRunContext, total, position,
				"Sending undownload shared documents notification has failed : " + context.getResource().getUuid());
	}

	@Override
	public void terminate(BatchRunContext batchRunContext, List<String> all, long errors, long unhandled_errors, long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " share entry group have been processed.");
		if (errors > 0) {
			logger.error(errors + " share entry groups has not been processed.");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors + " share entry group failed to be processed (unhandled error).");
		}
		logger.info("UndownloadedSharedDocumentsBatchImpl job terminated.");
	}
}
