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
 * and free version of LinShare™, powered by Linagora © 2009–2016. Contribute to
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
			logInfo(batchRunContext, total, position, "processing shareEntryGroup : " + shareEntryGroup.getUuid());
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
			logError(total, position, "Error while trying to send a notification for undownloaded shared documents", batchRunContext);
			logger.error("Error occured while sending notification ", businessException);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to send a notification for undownloaded shared documents");
			exception.setBusinessException(businessException);
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

		logInfo(batchRunContext, total, position, "The notification for the shareEntryGroup "
				+ shareEntryGroupContext.getResource().getUuid() + " has been successfully sent ");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<ShareEntryGroup> context = (BatchResultContext<ShareEntryGroup>) exception.getContext();
		logError(total, position,
				"Sending undownload shared documents notification has failed : " + context.getResource().getUuid(), batchRunContext);
		logger.error("Error occured while Sending undownload shared documents notification "
				+ context.getResource().getUuid() + ". BatchBusinessException ", exception);
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
