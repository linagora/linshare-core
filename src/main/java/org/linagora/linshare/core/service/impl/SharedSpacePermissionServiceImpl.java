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
package org.linagora.linshare.core.service.impl;

import java.util.List;
import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.SharedSpacePermissionBusinessService;
import org.linagora.linshare.core.domain.constants.SharedSpaceActionType;
import org.linagora.linshare.core.domain.constants.SharedSpaceResourceType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.SharedSpacePermissionService;
import org.linagora.linshare.mongo.entities.SharedSpacePermission;

public class SharedSpacePermissionServiceImpl implements SharedSpacePermissionService {

	private final SharedSpacePermissionBusinessService sharedSpacePermissionBusinessService;

	public SharedSpacePermissionServiceImpl(SharedSpacePermissionBusinessService sharedSpacePermissionBusinessService) {
		this.sharedSpacePermissionBusinessService = sharedSpacePermissionBusinessService;
	}

	@Override
	public SharedSpacePermission findByUuid(Account authUser, Account actor, String uuid) throws BusinessException {
		Validate.notNull(authUser, "Missing authUser account");
		Validate.notNull(actor, "Missing actor account ");
		Validate.notEmpty(actor.getLsUuid(), "Missing authUser uuid");
		Validate.notEmpty(uuid, "Missing required shared space permission uuid.");
		SharedSpacePermission foundPermission = sharedSpacePermissionBusinessService.findByUuid(uuid);
		if (foundPermission == null) {
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_PERMISSION_NOT_FOUND,
					"The permission with uuid " + uuid + " was not found");
		}
		if (!(authUser.isUser())) {
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_PERMISSION_FORBIDDEN, "you are not authorized");
		}
		return foundPermission;
	}

	@Override
	public List<SharedSpacePermission> findByRole(Account authUser, Account actor, String roleName)
			throws BusinessException {
		Validate.notNull(authUser, "Missing authUser account");
		Validate.notNull(actor, "Missing actor");
		Validate.notEmpty(actor.getLsUuid(), "Missing actor uuid");
		List<SharedSpacePermission> foundPermission = sharedSpacePermissionBusinessService.findByRole(roleName);
		if (foundPermission == null) {
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_PERMISSION_NOT_FOUND,
					"The permission with related roles " + roleName + " was not found");
		}
		if (!(authUser.isUser())) {
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_PERMISSION_FORBIDDEN, "you are not authorized");
		}
		return foundPermission;
	}

	@Override
	public List<SharedSpacePermission> findAll(Account authUser, Account actor) throws BusinessException {
		if (!(authUser.isUser())) {
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_PERMISSION_FORBIDDEN, "you are not authorized");
		}
		return sharedSpacePermissionBusinessService.findAll();
	}

	public Boolean hasPermission(Account authUser, Account actor, String roleUuid, SharedSpaceActionType action,
			SharedSpaceResourceType resourceType) {
		Validate.notNull(authUser, "Missing authUser account");
		Validate.notNull(actor, "Missing actor");
		Validate.notEmpty(actor.getLsUuid(), "Missing actor uuid");
		return sharedSpacePermissionBusinessService.hasPermission(roleUuid, action, resourceType);
	}

}
