/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2020-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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
package org.linagora.linshare.core.upgrade.v4_0;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.domain.constants.ResetTokenKind;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.GuestAccountResetPasswordFor4_0_EmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.mongo.entities.ResetGuestPassword;
import org.linagora.linshare.mongo.repository.ResetGuestPasswordMongoRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;

public class NotifyAllGuestsToResetPasswordsWithOldEncodingUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	private final GuestRepository guestRepository;

	private final NotifierService notifierService;

	private final MailBuildingService mailBuildingService;

	private final ResetGuestPasswordMongoRepository resetGuestPasswordMongoRepository;

	private final int urlExpirationDays;

	public NotifyAllGuestsToResetPasswordsWithOldEncodingUpgradeTaskImpl(AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository, GuestRepository guestRepository,
			NotifierService notifierService, MailBuildingService mailBuildingService,
			ResetGuestPasswordMongoRepository resetGuestPasswordMongoRepository,
			int urlExpirationDays) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.guestRepository = guestRepository;
		this.notifierService = notifierService;
		this.mailBuildingService = mailBuildingService;
		this.resetGuestPasswordMongoRepository = resetGuestPasswordMongoRepository;
		this.urlExpirationDays = urlExpirationDays;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_4_0_PASSWORD_ENCODING_STRATEGY_CHANGES_FOR_GUESTS;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info("{} job is starting ...", getClass().toString());
		List<String> uuids = guestRepository.findAllWithDeprecatedPasswordEncoding();
		logger.info("{} guest(s) has been found.", uuids.size());
		return uuids;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		Guest guest = guestRepository.findByLsUuid(identifier);
		BatchResultContext<Guest> batchResultContext = new BatchResultContext<>(guest);
		if (guest == null) {
			batchResultContext.setProcessed(false);
			return batchResultContext;
		}
		ResetGuestPassword resetGuestPassword = new ResetGuestPassword(guest);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, urlExpirationDays);
		resetGuestPassword.setExpirationDate(calendar.getTime());
		resetGuestPassword.setKind(ResetTokenKind.RESET_PASSWORD);
		resetGuestPasswordMongoRepository.insert(resetGuestPassword);
		EmailContext mailContext = new GuestAccountResetPasswordFor4_0_EmailContext(guest, resetGuestPassword.getUuid(),
				resetGuestPassword.getExpirationDate());
		MailContainerWithRecipient mail = mailBuildingService.build(mailContext);
		notifierService.sendNotification(mail);
		batchResultContext.setProcessed(true);
		return batchResultContext;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<Guest> batchResultContext = (BatchResultContext<Guest>) context;
		Guest resource = batchResultContext.getResource();
		if (batchResultContext.getProcessed()) {
			logInfo(batchRunContext, total, position,
					"Guest has been successfully notified to update their passwords : " + resource.toString());
		} else {
			logInfo(batchRunContext, total, position,
					"No Guest has been notified");
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<Guest> res = (BatchResultContext<Guest>) exception.getContext();
		Guest resource = res.getResource();
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occured while processing Guest notify : {} . BatchBusinessException", resource,
				exception);
	}
}
