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

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.business.service.SharedSpaceNodeBusinessService;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.repository.SharedSpaceNodeMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.Nonnull;

public class SharedSpaceNodeBusinessServiceImpl implements SharedSpaceNodeBusinessService {

	private final Logger logger = LoggerFactory.getLogger(SharedSpaceNodeBusinessServiceImpl.class);

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
	public List<SharedSpaceNode> searchByName(String name) throws BusinessException {
		return sharedSpaceNodeMongoRepository.findByName(name);
	}

	@Override
	public List<SharedSpaceNode> findAllRootWorkgroups() {
		Query query = new Query();
		query.addCriteria(Criteria.where("nodeType").is(NodeType.WORK_GROUP));
		query.addCriteria(Criteria.where("parentUuid").is(null));
		return mongoTemplate.find(query, SharedSpaceNode.class);
	}

	@Override
	public void transferWorkspaceFromGuestToInternal(@Nonnull final User guest, @Nonnull final User author) {
		logger.info("start sharing space node from guest to internal user");
		final List<SharedSpaceNode> sharedSpaceNodes = this.sharedSpaceNodeMongoRepository.findByAuthorUuid(guest.getLsUuid());
		if (sharedSpaceNodes != null && !sharedSpaceNodes.isEmpty()) {
			for (final SharedSpaceNode sharedSpaceNode : sharedSpaceNodes) {
				try {
					final SharedSpaceAccount newAuthor = new SharedSpaceAccount(author);
					sharedSpaceNode.setAuthor(newAuthor);
					sharedSpaceNode.setDomainUuid(author.getDomain().getUuid());
					this.sharedSpaceNodeMongoRepository.save(sharedSpaceNode);
					logger.debug("the space node is shared successfully");
				} catch (final BusinessException | IllegalArgumentException e) {
					logger.error("An error occurred while updating share space node", e);
					throw e;
				}
			}
		} else {
			logger.debug("the list of shared space node is empty or null");
		}
	}
}
