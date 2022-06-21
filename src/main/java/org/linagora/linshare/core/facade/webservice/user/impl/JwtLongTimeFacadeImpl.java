/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
