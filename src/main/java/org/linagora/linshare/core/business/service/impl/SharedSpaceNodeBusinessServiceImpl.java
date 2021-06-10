/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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
package org.linagora.linshare.core.business.service.impl;

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.business.service.SharedSpaceNodeBusinessService;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;
import org.linagora.linshare.mongo.repository.SharedSpaceNodeMongoRepository;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class SharedSpaceNodeBusinessServiceImpl implements SharedSpaceNodeBusinessService {

	protected SharedSpaceNodeMongoRepository sharedSpaceNodeMongoRepository;
	
	protected MongoTemplate mongoTemplate;

	public SharedSpaceNodeBusinessServiceImpl(SharedSpaceNodeMongoRepository sharedSpaceNodeMongoRepository,
			MongoTemplate mongoTemplate) {
		this.sharedSpaceNodeMongoRepository = sharedSpaceNodeMongoRepository;
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public SharedSpaceNode find(String uuid) throws BusinessException {
		return sharedSpaceNodeMongoRepository.findByUuid(uuid);
	}

	@Override
	public void loadLastUpdaterAuditTrace(SharedSpaceNode node) throws BusinessException {
		Query query = new Query();
		Criteria criteria = new Criteria();
		criteria.orOperator(Criteria.where("resourceUuid").is(node.getUuid()),
				Criteria.where("relatedResources").in(node.getUuid()));
		query.addCriteria(criteria);
		query.with(Sort.by(Direction.DESC, "creationDate")).limit(1);
		AuditLogEntryUser lastAuditEntry = mongoTemplate.findOne(query, AuditLogEntryUser.class);
		node.setLastAuditEntry(lastAuditEntry);
	}

	@Override
	public SharedSpaceNode create(SharedSpaceNode node) throws BusinessException {
		if (node.getDescription() == null) {
			node.setDescription("");
		}
		return sharedSpaceNodeMongoRepository.insert(node);
	}

	@Override
	public List<SharedSpaceNode> findByNameAndParentUuid(String name, String parentUuid) throws BusinessException {
		return sharedSpaceNodeMongoRepository.findByNameAndParentUuid(name, parentUuid);
	}

	@Override
	public List<SharedSpaceNode> findByParentUuidAndType(String parentUuid) throws BusinessException {
		return sharedSpaceNodeMongoRepository.findByParentUuidAndNodeType(parentUuid, NodeType.WORK_GROUP);
	}

	@Override
	public void delete(SharedSpaceNode node) throws BusinessException {
		sharedSpaceNodeMongoRepository.delete(node);
	}

	@Override
	public SharedSpaceNode update(SharedSpaceNode foundNodeToUpdate, SharedSpaceNode nodeToUpdate)
			throws BusinessException {
		foundNodeToUpdate.setName(nodeToUpdate.getName());
		if (nodeToUpdate.getDescription() != null) {
			foundNodeToUpdate.setDescription(nodeToUpdate.getDescription());
		}
		if (nodeToUpdate.getVersioningParameters() != null) {
			foundNodeToUpdate.setVersioningParameters(nodeToUpdate.getVersioningParameters());
		}
		foundNodeToUpdate.setModificationDate(new Date());
		return sharedSpaceNodeMongoRepository.save(foundNodeToUpdate);
	}

	public List<SharedSpaceNode> findAll() throws BusinessException {
		return sharedSpaceNodeMongoRepository.findAll();
	}

	@Override
	public List <SharedSpaceNode> searchByName(String name) throws BusinessException {
		return sharedSpaceNodeMongoRepository.findByName(name);
	}

	@Override
	public PageContainer<SharedSpaceNodeNested> findAll(PageContainer<SharedSpaceNodeNested> container, Sort sort) {
		Aggregation aggregation = Aggregation.newAggregation(
		Aggregation.project(
				Fields.from(
						Fields.field("uuid"),
						Fields.field("name"),
						Fields.field("parentUuid"),
						Fields.field("creationDate"),
						Fields.field("modificationDate"),
						Fields.field("nodeType")
						)
				),
		Aggregation.skip(Long.valueOf(container.getPageNumber() * container.getPageSize())),
		Aggregation.limit(Long.valueOf(container.getPageSize())),
		Aggregation.sort(sort));
		List<SharedSpaceNodeNested> nodes = mongoTemplate.aggregate(aggregation, "shared_space_nodes", SharedSpaceNodeNested.class)
				.getMappedResults();
		return new PageContainer<SharedSpaceNodeNested>(container.getPageNumber(), container.getPageSize(),
				sharedSpaceNodeMongoRepository.count(), nodes);
	}
}
