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
