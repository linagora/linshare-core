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
package org.linagora.linshare.core.facade.webservice.user.impl;

import java.security.SecureRandom;
import java.util.Date;

import org.apache.commons.lang3.Validate;
import org.jboss.aerogear.security.otp.api.Base32;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.BooleanValueFunctionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.SecondFactorAuthenticationFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.SecondFactorDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.mongo.entities.logs.UserAuditLogEntry;

public class SecondFactorAuthenticationFacadeImpl extends UserGenericFacadeImp implements SecondFactorAuthenticationFacade {

	protected final FunctionalityReadOnlyService functionalityReadOnlyService;
	
	protected final LogEntryService logEntryService;

	public SecondFactorAuthenticationFacadeImpl(
			final AccountService accountService,
			final FunctionalityReadOnlyService functionalityReadOnlyService,
			final LogEntryService logEntryService
			) {
		super(accountService);
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.logEntryService = logEntryService;
	}

	@Override
	public SecondFactorDto find(String uuid) {
		User authUser = checkAuthentication();
		SecondFactorDto dto = to2FADto(authUser);
		return dto;
	}

	@Override
	public SecondFactorDto create(SecondFactorDto sfd) {
		User authUser = checkAuthentication();
		BooleanValueFunctionality twofaFunc = functionalityReadOnlyService
				.getSecondFactorAuthenticationFunctionality(authUser.getDomain());
		if (!twofaFunc.getActivationPolicy().getStatus()) {
			String message = "You can not create a 2FA shared key for your account. Functionality is not enabled.";
			throw new BusinessException(BusinessErrorCode.AUTHENTICATION_SECOND_FACTOR_NOT_ENABLED, message);
		}
		if (authUser.isUsing2FA()) {
			String message = "You can not create a 2FA shared key for your account. It already exists.";
			throw new BusinessException(BusinessErrorCode.AUTHENTICATION_SECOND_FACTOR_ALREADY_EXISTS, message);
		}
		SecureRandom random = new SecureRandom();
		byte[] secret = random.generateSeed(20);
		authUser.setSecondFACreationDate(new Date());
		authUser.setSecondFASecret(Base32.encode(secret));
		Account updated = accountService.update(authUser);
		UserAuditLogEntry userAuditLogEntry = new UserAuditLogEntry(authUser, updated, LogAction.UPDATE,
				AuditLogEntryType.USER, (User) updated);
		userAuditLogEntry.setCause(LogActionCause.SECOND_FACTOR_SHARED_KEY_CREATE);
		logEntryService.insert(userAuditLogEntry);
		SecondFactorDto dto = to2FADto(updated);
		dto.setSharedKey(updated.getSecondFASecret());
		return dto;
	}

	@Override
	public SecondFactorDto delete(String uuid, SecondFactorDto sfd) {
		Validate.notEmpty(uuid, "Missing account uuid as path param.");
		User authUser = checkAuthentication();
		authUser.setSecondFACreationDate(null);
		authUser.setSecondFASecret(null);
		Account updated = accountService.update(authUser);
		UserAuditLogEntry userAuditLogEntry = new UserAuditLogEntry(authUser, updated, LogAction.UPDATE,
				AuditLogEntryType.USER, (User) updated);
		userAuditLogEntry.setCause(LogActionCause.SECOND_FACTOR_SHARED_KEY_DELETE);
		logEntryService.insert(userAuditLogEntry);
		SecondFactorDto dto = to2FADto(updated);
		return dto;
	}

	private SecondFactorDto to2FADto(Account authUser) {
		SecondFactorDto dto = new SecondFactorDto(authUser.getLsUuid(), authUser.getSecondFACreationDate(), authUser.isUsing2FA());
		BooleanValueFunctionality twofaFunc = functionalityReadOnlyService.getSecondFactorAuthenticationFunctionality(authUser.getDomain());
		if (twofaFunc.getActivationPolicy().getStatus()) {
			dto.setRequired(twofaFunc.getValue());
			dto.setCanDeleteIt(twofaFunc.getDelegationPolicy().getStatus());
		}
		return dto;
	}
}
