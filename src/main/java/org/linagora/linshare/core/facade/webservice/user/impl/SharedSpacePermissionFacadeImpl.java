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
import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.SharedSpacePermissionFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.SharedSpacePermissionService;
import org.linagora.linshare.mongo.entities.SharedSpacePermission;

public class SharedSpacePermissionFacadeImpl extends GenericFacadeImpl implements SharedSpacePermissionFacade {

	private final SharedSpacePermissionService sharedSpacePermissionService;

	public SharedSpacePermissionFacadeImpl(AccountService accountService,
			SharedSpacePermissionService sharedSpacePermissionService) {
		super(accountService);
		this.sharedSpacePermissionService = sharedSpacePermissionService;
	}

	@Override
	public SharedSpacePermission find(String actorUuid, String uuid) throws BusinessException {
		Validate.notEmpty("Missing required shared space permission uuid", uuid);
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		return sharedSpacePermissionService.findByUuid(authUser, actor, uuid);
	}

	@Override
	public List<SharedSpacePermission> findByRole(String actorUuid, String roleName) throws BusinessException {
		Validate.notNull(roleName, "Missing require role for this permission");
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		return sharedSpacePermissionService.findByRole(authUser, actor, roleName);
	}

	@Override
	public List<SharedSpacePermission> findAll(String actorUuid) throws BusinessException {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		return sharedSpacePermissionService.findAll(authUser, actor);
	}

}
