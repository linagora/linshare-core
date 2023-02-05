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
package org.linagora.linshare.mongo.repository;

import java.util.List;

import org.linagora.linshare.core.domain.constants.SharedSpaceActionType;
import org.linagora.linshare.core.domain.constants.SharedSpaceResourceType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.mongo.entities.SharedSpacePermission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface SharedSpacePermissionMongoRepository extends MongoRepository<SharedSpacePermission, String> {
	
	SharedSpacePermission findByUuid(String uuid) throws BusinessException;

	Long removeByUuid(String uuid) throws BusinessException;

	@Query("{'roles.name': ?0}")
	List<SharedSpacePermission> findBySharedSpaceRole(String roleName) throws BusinessException;

	@Query("{'roles.uuid': ?0}")
	List<SharedSpacePermission> findBySharedSpaceRoleUuid(String roleUuid) throws BusinessException;

	List<SharedSpacePermission> findAll() throws BusinessException;

	@Query("{'roles.uuid': ?0, 'action': ?1, 'resource': ?2}")
	List<SharedSpacePermission> findByRoleAndActionAndResource(String roleUuid, SharedSpaceActionType action, SharedSpaceResourceType resourceType);

}
