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

import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface SharedSpaceNodeMongoRepository extends MongoRepository<SharedSpaceNode, String> {

	SharedSpaceNode findByUuid(String uuid);

	List<SharedSpaceNode> findByNameAndParentUuid(String name, String parentUuid);

	List<SharedSpaceNode> findByParentUuidAndNodeType(String parentUuid, NodeType nodeType);

	@Query("{name: {'$regex':?0,'option':'i'}}")
	List<SharedSpaceNode> findByName(String name);

	@Query(value = "{ 'domainUuid' : ?0, nodeType: 'WORK_SPACE' }", count = true)
	Long countWorkspaces(String domainUuid);

	@Query(value = "{ 'domainUuid' : ?0 , 'parentUuid' : ?1, nodeType: 'WORK_GROUP' }", count = true)
	Long countNestedWorkgroups(String domainUuid, String parentUuid);

	@Query("{ 'author.uuid' : ?0 }")
	List<SharedSpaceNode> findByAuthorUuid(final String authorUuid);
}
