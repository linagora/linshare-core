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

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.delegation.impl.DelegationGenericFacadeImpl;
import org.linagora.linshare.core.facade.webservice.user.AuditLogEntryUserFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;

public class AuditLogEntryUserFacadeImpl extends DelegationGenericFacadeImpl implements AuditLogEntryUserFacade {

	protected final AuditLogEntryService service;

	public AuditLogEntryUserFacadeImpl(AccountService accountService, final AuditLogEntryService service, UserService userService) {
		super(accountService, userService);
		this.service = service;
	}

	@Override
	public Set<AuditLogEntryUser> findAll(String actorUuid, List<LogAction> action, List<AuditLogEntryType> type, boolean forceAll,
			String beginDate, String endDate) throws BusinessException {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		return service.findAllForUsers(authUser, actor, action, type, forceAll, beginDate, endDate);
	}
}
