/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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
