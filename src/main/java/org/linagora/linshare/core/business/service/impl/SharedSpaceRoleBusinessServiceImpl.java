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
import java.util.Set;

import org.bson.Document;
import org.linagora.linshare.core.business.service.SharedSpaceRoleBusinessService;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.repository.SharedSpaceRoleMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;

public class SharedSpaceRoleBusinessServiceImpl implements SharedSpaceRoleBusinessService {

	private final SharedSpaceRoleMongoRepository sharedSpaceRoleMongoRepository;

	private final MongoTemplate mongoTemplate;

	public SharedSpaceRoleBusinessServiceImpl(
			SharedSpaceRoleMongoRepository sharedSpaceRoleMongoRepository,
			MongoTemplate mongoTemplate) {
		super();
		this.sharedSpaceRoleMongoRepository = sharedSpaceRoleMongoRepository;
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public SharedSpaceRole find(String uuid) throws BusinessException {
		return sharedSpaceRoleMongoRepository.findByUuid(uuid);
	}

	@Override
	public SharedSpaceRole findByName(String name) throws BusinessException {
		return sharedSpaceRoleMongoRepository.findByName(name);
	}

	@Override
	public List<SharedSpaceRole> findAll() throws BusinessException {
		return sharedSpaceRoleMongoRepository.findAll();
	}

	@Override
	public List<SharedSpaceRole> findRolesByNodeType(NodeType type) {
		return sharedSpaceRoleMongoRepository.findByType(type);
	}

	@Override
	public SharedSpaceRole findByNameAndNodeType(String name, NodeType nodeType) throws BusinessException {
		return sharedSpaceRoleMongoRepository.findByNameAndType(name, nodeType);
	}

	@Override
	public Set<String> findAllSharedSpaceRoleNames(Account authUser, Account actor) {
		MongoCollection<Document> sharedSpaceRoles = mongoTemplate.getCollection("shared_space_roles");
		return Set.copyOf(Lists.newArrayList(sharedSpaceRoles.distinct("name", String.class)));
	}
}
