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

import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface WorkGroupNodeMongoRepository extends MongoRepository<WorkGroupNode, String> , WorkGroupNodeMongoRepositoryCustom {

	List<WorkGroupNode> findByWorkGroup(String workGroupUuid);

	WorkGroupNode findByUuid(String uuid);

	WorkGroupNode findByWorkGroupAndUuid(String workGroupUuid, String uuid);

	WorkGroupNode findByWorkGroupAndUuidAndNodeType(String workGroupUuid, String uuid, WorkGroupNodeType type);

	List<WorkGroupNode> findByWorkGroupAndParent(String workGroupUuid, String parentUuid);

	List<WorkGroupNode> findByWorkGroupAndParentAndNodeType(String workGroupUuid, String parentUuid, WorkGroupNodeType type);

	List<WorkGroupNode> findByWorkGroupAndParentAndNodeType(String workGroupUuid, String parentUuid, WorkGroupNodeType type, Sort sort);

	@Query("{ 'workGroup' : ?0, 'parent' : ?1, 'nodeType' : {'$in' : ?2 } }")
	List<WorkGroupNode> findByWorkGroupAndParentAndNodeTypes(String workGroupUuid, String parentUuid, List<WorkGroupNodeType> types, Sort sort);

	@Query("{ 'workGroup' : ?0, 'nodeType' : {'$in' : ?1 } }")
	List<WorkGroupNode> findByWorkGroupAndNodeTypes(String workGroupUuid, List<WorkGroupNodeType> types, Sort sort);

	List<WorkGroupNode> findByWorkGroupAndNodeType(String workGroupUuid, WorkGroupNodeType type);

	List<WorkGroupNode> findByWorkGroupAndParentAndName(String workGroupUuid, String parentUuid, String name);

	Long countByWorkGroupAndParentAndNodeType(String workGroupUuid, String parentUuid, WorkGroupNodeType type);

	WorkGroupNode deleteByWorkGroupAndParent(WorkGroup workGroup, WorkGroupNode workGroupNode);

}
