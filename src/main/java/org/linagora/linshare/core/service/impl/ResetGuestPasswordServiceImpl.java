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
package org.linagora.linshare.core.service.impl;

import java.util.Date;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.ResetGuestPasswordService;
import org.linagora.linshare.mongo.entities.ResetGuestPassword;
import org.linagora.linshare.mongo.repository.ResetGuestPasswordMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResetGuestPasswordServiceImpl implements ResetGuestPasswordService {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected ResetGuestPasswordMongoRepository repository;

	protected GuestService guestService;

	public ResetGuestPasswordServiceImpl(ResetGuestPasswordMongoRepository repository, GuestService guestService) {
		super();
		this.repository = repository;
		this.guestService = guestService;
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
			throw new BusinessException(BusinessErrorCode.RESET_GUEST_PASSWORD_EXPIRED_TOKEN,
					"The reset token is expired.");
		}
		if (resetGuestPassword.getAlreadyUsed()) {
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
		guestService.resetPassword(guest, dto.getPassword());
		reset = repository.save(reset);
		logger.info("Reset password");
		return reset;
	}

}
