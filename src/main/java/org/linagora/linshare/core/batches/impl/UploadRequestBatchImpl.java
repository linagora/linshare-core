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

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.linagora.linshare.core.batches.UploadRequestBatch;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.UploadRequestWarnBeforeExpiryEmailContext;
import org.linagora.linshare.core.notifications.context.UploadRequestWarnExpiryEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.UploadRequestRepository;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.UploadRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class UploadRequestBatchImpl implements UploadRequestBatch {

	private static final Logger logger = LoggerFactory.getLogger(UploadRequestBatchImpl.class);

	private final UploadRequestRepository uploadRequestRepository;
	private final UploadRequestService uploadRequestService;
	private final MailBuildingService mailBuildingService;
	private final NotifierService notifierService;
	private final AccountRepository<Account> accountRepository;

	public UploadRequestBatchImpl(
			final UploadRequestRepository uploadRequestRepository,
			final MailBuildingService mailBuildingService,
			final NotifierService notifierService,
			final UploadRequestService uploadRequestService,
			final AccountRepository<Account> accountRepository) {
		this.uploadRequestRepository = uploadRequestRepository;
		this.mailBuildingService = mailBuildingService;
		this.notifierService = notifierService;
		this.uploadRequestService = uploadRequestService;
		this.accountRepository = accountRepository;
	}

	@Override
	public void updateStatus() {
		SystemAccount systemAccount = accountRepository.getBatchSystemAccount();
		logger.info("Update upload request status");
		List<MailContainerWithRecipient> notifications = Lists.newArrayList();
		for (UploadRequest r : uploadRequestRepository.findByStatus(UploadRequestStatus.STATUS_CREATED)) {
			if (r.getActivationDate().before(new Date())) {
				try {
					r.updateStatus(UploadRequestStatus.STATUS_ENABLED);
					r = uploadRequestService.updateRequest(systemAccount, systemAccount, r);
					for (UploadRequestUrl u: r.getUploadRequestURLs()) {
						notifications.add(mailBuildingService.buildActivateUploadRequest((User) r.getOwner(), u));
					}
				} catch (BusinessException e) {
					logger.error("Fail to update upload request status of the request : " + r.getUuid());
				}
			}
		}
		for (UploadRequest r : uploadRequestRepository.findByStatus(UploadRequestStatus.STATUS_ENABLED)) {
			if (DateUtils.isSameDay(r.getNotificationDate(), new Date()) && !DateUtils.isSameDay(r.getNotificationDate(), r.getExpiryDate())) {
				logger.debug("date de notification == today..." + r.getExpiryDate() + " == " + new Date());
				try {
					for (UploadRequestUrl u : r.getUploadRequestURLs()) {
						EmailContext ctx = new UploadRequestWarnBeforeExpiryEmailContext((User)r.getOwner(), r, u, false);
						notifications.add(mailBuildingService.build(ctx));
					}
					EmailContext ctx = new UploadRequestWarnBeforeExpiryEmailContext((User)r.getOwner(), r, null, true);
					notifications.add(mailBuildingService.build(ctx));
				} catch (BusinessException e) {
					logger.error("Fail to update upload request status of the request : " + r.getUuid());
				}
			}
			if (r.getExpiryDate().before(new Date())) {
				try {
					r.updateStatus(UploadRequestStatus.STATUS_CLOSED);
					r = uploadRequestService.updateRequest(systemAccount, systemAccount, r);
					for (UploadRequestUrl u : r.getUploadRequestURLs()) {
						EmailContext ctx = new UploadRequestWarnExpiryEmailContext((User)r.getOwner(), r, u, false);
						notifications.add(mailBuildingService.build(ctx));
					}
					EmailContext ctx = new UploadRequestWarnExpiryEmailContext((User)r.getOwner(), r, null, true);
					notifications.add(mailBuildingService.build(ctx));
				} catch (BusinessException e) {
					logger.error("Fail to update upload request status of the request : " + r.getUuid());
				}
			}
		}
		try {
			notifierService.sendNotification(notifications);
		} catch (BusinessException e) {
			logger.error("Unable to send upload request status notifications");
		}
	}
}
