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
package org.linagora.linshare.core.business.service.impl;

import java.util.List;

import org.linagora.linshare.core.business.service.SharedSpacePermissionBusinessService;
import org.linagora.linshare.core.domain.constants.SharedSpaceActionType;
import org.linagora.linshare.core.domain.constants.SharedSpaceResourceType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.mongo.entities.SharedSpacePermission;
import org.linagora.linshare.mongo.repository.SharedSpacePermissionMongoRepository;

public class SharedSpacePermissionBusinessServiceImpl implements SharedSpacePermissionBusinessService {

	private final SharedSpacePermissionMongoRepository sharedSpacePermissionMongoRepository;

	public SharedSpacePermissionBusinessServiceImpl(
			SharedSpacePermissionMongoRepository sharedSpacePermissionMongorepository) {
		this.sharedSpacePermissionMongoRepository = sharedSpacePermissionMongorepository;
	}

	@Override
	public SharedSpacePermission findByUuid(String uuid) throws BusinessException {
		return sharedSpacePermissionMongoRepository.findByUuid(uuid);
	}

	@Override
	public List<SharedSpacePermission> findByRole(String roleName) throws BusinessException {
		return sharedSpacePermissionMongoRepository.findBySharedSpaceRole(roleName);
	}

	@Override
	public List<SharedSpacePermission> findByRoleUuid(String roleUuid) throws BusinessException {
		return sharedSpacePermissionMongoRepository.findBySharedSpaceRoleUuid(roleUuid);
	}

	@Override
	public List<SharedSpacePermission> findAll() throws BusinessException {
		return sharedSpacePermissionMongoRepository.findAll();
	}

	@Override
	public Boolean hasPermission(String roleUuid, SharedSpaceActionType action, SharedSpaceResourceType resourceType) {
		return !sharedSpacePermissionMongoRepository.findByRoleAndActionAndResource(roleUuid, action, resourceType)
				.isEmpty();
	}

}
