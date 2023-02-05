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

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.JwtLongTimeFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.JwtLongTimeService;
import org.linagora.linshare.mongo.entities.PermanentToken;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class JwtLongTimeFacadeImpl extends UserGenericFacadeImp implements JwtLongTimeFacade {

	JwtLongTimeService jwtLongTimeService;

	AuditLogEntryService auditLogEntryService;

	public JwtLongTimeFacadeImpl(AccountService accountService,
			JwtLongTimeService jwtLongTimeService,
			AuditLogEntryService auditLogEntryService) {
		super(accountService);
		this.jwtLongTimeService = jwtLongTimeService;
		this.auditLogEntryService = auditLogEntryService;
	}

	@Override
	public PermanentToken find(String uuid) {
		Validate.notEmpty(uuid, "token uuid must be set");
		User authUser = checkAuthentication();
		return jwtLongTimeService.find(authUser, authUser, uuid);
	}

	@Override
	public PermanentToken create(PermanentToken permanentToken) throws BusinessException {
		Validate.notEmpty(permanentToken.getLabel(), "Missing Label");
		User authUser = checkAuthentication();
		return jwtLongTimeService.create(authUser, authUser, permanentToken);
	}

	@Override
	public List<PermanentToken> findAll() throws BusinessException {
		User authUser = checkAuthentication();
		return jwtLongTimeService.findAll(authUser, authUser);
	}

	@Override
	public PermanentToken delete(PermanentToken jwtLongTime, String uuid) throws BusinessException {
		User authUser = checkAuthentication();
		if (!Strings.isNullOrEmpty(uuid)) {
			jwtLongTime = jwtLongTimeService.find(authUser, authUser, uuid);
		} else {
			Validate.notNull(jwtLongTime, "jwtLongTime object must be set");
			Validate.notEmpty(jwtLongTime.getUuid(), "jwtLongTime uuid must be set");
			jwtLongTime = jwtLongTimeService.find(authUser, authUser, jwtLongTime.getUuid());
		}
		return jwtLongTimeService.delete(authUser, authUser, jwtLongTime);
	}

	@Override
	public PermanentToken update(PermanentToken permanentToken, String uuid) {
		Validate.notNull(permanentToken, "permanentToken object must be set");
		if (!Strings.isNullOrEmpty(uuid)) {
			permanentToken.setUuid(uuid);
		}
		Validate.notEmpty(permanentToken.getUuid(), "permanentToken uuid must be set");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, null);
		return jwtLongTimeService.update(authUser, actor, permanentToken.getUuid(), permanentToken);
	}

	@Override
	public Set<AuditLogEntryUser> findAllAudit(List<LogAction> actions) throws BusinessException {
		User authUser = checkAuthentication();
		if(actions.isEmpty()) {
			actions.add(LogAction.CREATE);
			actions.add(LogAction.DELETE);
		}
		List<AuditLogEntryType> type = Lists.newArrayList();
		type.add(AuditLogEntryType.JWT_PERMANENT_TOKEN);
		return auditLogEntryService.findAllForUsers(authUser, authUser, actions, type, true, null, null);
	}
}
