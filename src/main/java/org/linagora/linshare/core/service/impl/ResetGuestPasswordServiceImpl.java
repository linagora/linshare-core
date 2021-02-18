/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2021 LINAGORA
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
package org.linagora.linshare.core.service.impl;

import java.util.Date;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.PasswordService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.GuestWarnGuestAboutHisPasswordResetEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.ResetGuestPasswordService;
import org.linagora.linshare.mongo.entities.ResetGuestPassword;
import org.linagora.linshare.mongo.entities.logs.UserAuditLogEntry;
import org.linagora.linshare.mongo.repository.ResetGuestPasswordMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResetGuestPasswordServiceImpl implements ResetGuestPasswordService {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected ResetGuestPasswordMongoRepository repository;

	protected GuestService guestService;
	
	protected LogEntryService logEntryService;

	protected final NotifierService notifierService;

	protected final MailBuildingService mailBuildingService;

	protected final PasswordService passwordService;

	public ResetGuestPasswordServiceImpl(ResetGuestPasswordMongoRepository repository,
			GuestService guestService,
			LogEntryService logEntryService,
			NotifierService notifierService,
			MailBuildingService mailBuildingService,
			PasswordService passwordService) {
		super();
		this.repository = repository;
		this.guestService = guestService;
		this.logEntryService = logEntryService;
		this.notifierService = notifierService;
		this.mailBuildingService = mailBuildingService;
		this.passwordService = passwordService;
	}

	@Override
	public SystemAccount getGuestSystemAccount() {
		return guestService.getGuestSystemAccount();
	}

	@Override
	public ResetGuestPassword find(Account actor, Account owner, String uuid) throws BusinessException {
		Validate.notEmpty(uuid);
		ResetGuestPassword resetGuestPassword = repository.findByUuid(uuid);
		if (resetGuestPassword == null) {
			logger.error("Reset token requested not found : " + uuid);
			throw new BusinessException(BusinessErrorCode.RESET_GUEST_PASSWORD_NOT_FOUND,
					"The reset token was not found.");
		}
		logger.info("Reset token found : " + resetGuestPassword);
		Date now = new Date();
		if (resetGuestPassword.getExpirationDate().before(now)) {
			User guest = guestService.find(actor, owner, resetGuestPassword.getGuestUuid());
			UserAuditLogEntry userAuditLogEntry = new UserAuditLogEntry(guest, guest, LogAction.FAILURE,
					AuditLogEntryType.RESET_PASSWORD, guest);
			logEntryService.insert(userAuditLogEntry);
			throw new BusinessException(BusinessErrorCode.RESET_GUEST_PASSWORD_EXPIRED_TOKEN,
					"The reset token is expired.");
		}
		if (resetGuestPassword.getAlreadyUsed()) {
			User guest = guestService.find(actor, owner, resetGuestPassword.getGuestUuid());
			UserAuditLogEntry userAuditLogEntry = new UserAuditLogEntry(guest, guest, LogAction.FAILURE,
					AuditLogEntryType.RESET_PASSWORD, guest);
			logEntryService.insert(userAuditLogEntry);
			throw new BusinessException(BusinessErrorCode.RESET_GUEST_PASSWORD_ALREADY_USED_TOKEN,
					"The reset token was already used.");
		}
		String guestUuid = resetGuestPassword.getGuestUuid();
		logger.debug("Password reset requested for guest. Looking for guest uuid : " + guestUuid);
		// Just to check if guest still exists.
		Guest guest = guestService.find(actor, owner, guestUuid);
		logger.info("Password reset request for guest : " + guest.getAccountRepresentation());
		return resetGuestPassword;
	}

	@Override
	public ResetGuestPassword update(Account actor, Account owner, ResetGuestPassword dto) throws BusinessException {
		Validate.notNull(dto);
		Validate.notEmpty(dto.getUuid(), "Missing uuid");
		Validate.notEmpty(dto.getPassword(), "Missing password");
		ResetGuestPassword reset = find(actor, owner, dto.getUuid());
		reset.setAlreadyUsed(true);
		Guest guest = guestService.find(actor, owner, reset.getGuestUuid());
		passwordService.validateAndStorePassword(guest, dto.getPassword());
		reset = repository.save(reset);
		UserAuditLogEntry userAuditLogEntry = new UserAuditLogEntry(guest, guest, LogAction.SUCCESS,
				AuditLogEntryType.RESET_PASSWORD, guest);
		logEntryService.insert(userAuditLogEntry);
		logger.info("Reset password");
		GuestWarnGuestAboutHisPasswordResetEmailContext context = new GuestWarnGuestAboutHisPasswordResetEmailContext(guest);
		MailContainerWithRecipient mail = mailBuildingService.build(context);
		notifierService.sendNotification(mail);
		return reset;
	}
}
