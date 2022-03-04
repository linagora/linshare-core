/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.fields.SharedSpaceMemberField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.dto.WorkgroupMemberAutoCompleteResultDto;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberWorkgroup;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.light.LightSharedSpaceRole;
import org.linagora.linshare.mongo.projections.dto.AggregateNestedNodeUuidResult;
import org.linagora.linshare.mongo.projections.dto.AggregateNodeCountResult;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;
import org.linagora.linshare.mongo.repository.SharedSpaceMemberMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpaceNodeMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpaceRoleMongoRepository;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.client.result.UpdateResult;

public class SharedSpaceMemberBusinessServiceImpl implements SharedSpaceMemberBusinessService {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected final SharedSpaceMemberMongoRepository repository;

	protected final SharedSpaceRoleMongoRepository roleRepository;

	protected final SharedSpaceNodeMongoRepository nodeRepository;

	protected final UserRepository<User> userRepository;
	
	protected final MongoTemplate mongoTemplate;

	protected final Function<SharedSpaceMember, SharedSpaceNodeNested> convertToSharedSpaceNode = new Function<SharedSpaceMember, SharedSpaceNodeNested>() {
		@Override
		public SharedSpaceNodeNested apply(SharedSpaceMember member) {
			return member.getNode();
		}
	};

	public SharedSpaceMemberBusinessServiceImpl(SharedSpaceMemberMongoRepository sharedSpaceMemberMongoRepository,
			SharedSpaceRoleMongoRepository roleRepository,
			SharedSpaceNodeMongoRepository nodeRepository,
			UserRepository<User> userRepository,
			MongoTemplate mongoTemplate) {
		super();
		this.repository = sharedSpaceMemberMongoRepository;
		this.roleRepository = roleRepository;
		this.nodeRepository = nodeRepository;
		this.userRepository = userRepository;
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public SharedSpaceMember find(String uuid) throws BusinessException {
		return repository.findByUuid(uuid);
	}

	@Override
	public SharedSpaceMember create(SharedSpaceMember member) throws BusinessException {
		member.setRole(new LightSharedSpaceRole(checkRole(member.getRole().getUuid())));
		member.setNode(new SharedSpaceNodeNested(checkNode(member.getNode().getUuid())));
		member.setAccount(new SharedSpaceAccount(checkUser(member.getAccount().getUuid())));
		member = repository.insert(member);
		updateRelatedSharedSpaceResources(member, member.getModificationDate());
		return member;
	}

	private void updateRelatedSharedSpaceResources(SharedSpaceMember member, Date modificationDate) {
		// Updating SSnode
		UpdateResult updateMulti = mongoTemplate.updateMulti(
				new Query().addCriteria(new Criteria("uuid").is(member.getNode().getUuid())),
				new Update().set("modificationDate", modificationDate),
				SharedSpaceNode.class);
		logger.trace("SharedSpaceNode: {}", updateMulti.toString());
		// Updating nested SSnode in SSmember collection
		updateMulti = mongoTemplate.updateMulti(
				new Query().addCriteria(new Criteria("node.uuid").is(member.getNode().getUuid())),
				new Update().set("node.modificationDate", modificationDate),
				SharedSpaceMember.class);
		logger.trace("SharedSpaceMembers: {}", updateMulti.toString());

		// Testing if this SSnode have a parent (A workSpace)
		String parentUuid = member.getNode().getParentUuid();
		if (parentUuid != null) {
			updateMulti = mongoTemplate.updateMulti(
					new Query().addCriteria(new Criteria("uuid").is(parentUuid)),
					new Update().set("modificationDate", modificationDate),
					SharedSpaceNode.class);
			logger.trace("Parent SharedSpaceNode: {}", updateMulti.toString());
			// Updating nested SSnode in SSmember collection
			updateMulti = mongoTemplate.updateMulti(
					new Query().addCriteria(new Criteria("node.uuid").is(parentUuid).and("type").is(NodeType.WORK_SPACE)),
					new Update().set("node.modificationDate", modificationDate),
					SharedSpaceMember.class);
			logger.trace("Parent SharedSpaceMembers: {}", updateMulti.toString());
		}
	}

	protected SharedSpaceRole checkRole(String roleUuid) {
		SharedSpaceRole role = roleRepository.findByUuid(roleUuid);
		if (role == null) {
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_ROLE_NOT_FOUND,
					"The required role does not exist.");
		}
		return role;
	}

	protected SharedSpaceNode checkNode(String nodeUuid) {
		SharedSpaceNode node = nodeRepository.findByUuid(nodeUuid);
		if (node == null) {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_NOT_FOUND, "The required node does not exist.");
		}
		return node;
	}

	protected User checkUser(String userUuid) {
		User user = userRepository.findByLsUuid(userUuid);
		if (user == null) {
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND, "The required user does not exist.");
		}
		return user;
	}

	@Override
	public List<SharedSpaceMember> findAll() throws BusinessException {
		return repository.findAll();
	}

	@Override
	public SharedSpaceMember findByAccountAndNode(String accountUuid, String nodeUuid) {
		return repository.findByAccountAndNode(accountUuid, nodeUuid);
	}

	@Override
	public void delete(SharedSpaceMember memberToDelete) {
		repository.delete(memberToDelete);
		updateRelatedSharedSpaceResources(memberToDelete, new Date());
	}

	@Override
	public SharedSpaceMember update(SharedSpaceMember foundMemberToUpdate, SharedSpaceMember memberToUpdate) {
		Validate.notNull(memberToUpdate.getRole(), "The role must be set.");
		foundMemberToUpdate.setRole(new LightSharedSpaceRole(checkRole(memberToUpdate.getRole().getUuid())));
		foundMemberToUpdate.setModificationDate(new Date());
		SharedSpaceMember member = checkSharedSpaceMemberTypeOnUpdate(foundMemberToUpdate);
		updateRelatedSharedSpaceResources(member, member.getModificationDate());
		return member;
	}

	private SharedSpaceMember checkSharedSpaceMemberTypeOnUpdate(SharedSpaceMember member) {
		if (NodeType.WORK_GROUP.equals(member.getNode().getNodeType())) {
			SharedSpaceMemberWorkgroup memberWg = (SharedSpaceMemberWorkgroup) member;
			memberWg.setPristine(false);
			return repository.save(memberWg);
		}
		return repository.save(member);
	}

	@Override
	public List<SharedSpaceMember> findBySharedSpaceNodeUuid(String shareSpaceNodeUuid) {
		return repository.findByNodeUuid(shareSpaceNodeUuid);
	}

	@Override
	public List<String> findMembersUuidBySharedSpaceNodeUuid(String shareSpaceNodeUuid) {
		List<SharedSpaceMember> members = repository.findByNodeUuid(shareSpaceNodeUuid);
		Stream<SharedSpaceAccount> accounts = members.stream().map(SharedSpaceMember::getAccount);
		return accounts.map(SharedSpaceAccount::getUuid).collect(Collectors.toList());
	}

	@Override
	public void deleteAll(List<SharedSpaceMember> foundMembersToDelete) {
		repository.deleteAll(foundMembersToDelete);
	}

	@Override
	public List<SharedSpaceMember> findByMemberName(String name) throws BusinessException {
		return repository.findByMemberName(name);
	}

	@Override
	public List<SharedSpaceNodeNested> findAllSharedSpacesByAccountAndParent(String accountUuid, boolean withRole, String parent) {
		ProjectionOperation projections = null;
		MatchOperation match = null;
		if (withRole) {
			projections = Aggregation.project(
				Fields.from(
						Fields.field("uuid", "node.uuid"),
						Fields.field("name", "node.name"),
						Fields.field("parentUuid", "node.parentUuid"),
						Fields.field("creationDate", "node.creationDate"),
						Fields.field("modificationDate", "node.modificationDate"),
						Fields.field("nodeType", "node.nodeType"),
						Fields.field("role.uuid", "role.uuid"),
						Fields.field("role.name", "role.name")
						)
				);
		} else {
			projections = Aggregation.project(
					Fields.from(
							Fields.field("uuid", "node.uuid"),
							Fields.field("name", "node.name"),
							Fields.field("parentUuid", "node.parentUuid"),
							Fields.field("creationDate", "node.creationDate"),
							Fields.field("modificationDate", "node.modificationDate"),
							Fields.field("nodeType", "node.nodeType")
							)
					);
		}
		if (Objects.isNull(parent)) {
			match = Aggregation.match(Criteria.where("account.uuid").is(accountUuid).and("nested").is(false));
		} else {
			match = Aggregation.match(Criteria.where("account.uuid").is(accountUuid).and("nested").is(true).and("node.parentUuid").is(parent));
		}
		Aggregation aggregation = Aggregation.newAggregation(SharedSpaceMember.class,
				match,
				Aggregation.sort(Direction.DESC, "node.modificationDate"),
				projections
				);
		AggregationResults<SharedSpaceNodeNested> aggregate = mongoTemplate.aggregate(
				aggregation, "shared_space_members", SharedSpaceNodeNested.class);
		return aggregate.getMappedResults();
	}

	@Override
	public List<SharedSpaceNodeNested> findAllSharedSpacesByAccountAndParentForUsers(String accountUuid, boolean withRole,
			String parent, Set<NodeType> types) {
		ProjectionOperation projections = null;
		MatchOperation match = null;
		if (types == null) {
			types = Sets.newHashSet();
			types.add(NodeType.WORK_SPACE);
			types.add(NodeType.WORK_GROUP);
		}
		if (withRole) {
			projections = Aggregation.project(
				Fields.from(
						Fields.field("uuid", "node.uuid"),
						Fields.field("name", "node.name"),
						Fields.field("parentUuid", "node.parentUuid"),
						Fields.field("creationDate", "node.creationDate"),
						Fields.field("modificationDate", "node.modificationDate"),
						Fields.field("nodeType", "node.nodeType"),
						Fields.field("role.uuid", "role.uuid"),
						Fields.field("role.name", "role.name")
						)
				);
		} else {
			projections = Aggregation.project(
					Fields.from(
							Fields.field("uuid", "node.uuid"),
							Fields.field("name", "node.name"),
							Fields.field("parentUuid", "node.parentUuid"),
							Fields.field("creationDate", "node.creationDate"),
							Fields.field("modificationDate", "node.modificationDate"),
							Fields.field("nodeType", "node.nodeType")
							)
					);
		}
		if (Objects.isNull(parent)) {
			match = Aggregation.match(Criteria.where("account.uuid").is(accountUuid).and("seeAsNested").is(false));
		} else {
			match = Aggregation.match(Criteria.where("account.uuid").is(accountUuid).and("node.parentUuid").is(parent));
		}
		Aggregation aggregation = Aggregation.newAggregation(SharedSpaceMember.class,
				match,
				Aggregation.match(Criteria.where("node.nodeType").in(types)),
				Aggregation.sort(Direction.DESC, "node.modificationDate"),
				projections
				);
		AggregationResults<SharedSpaceNodeNested> aggregate = mongoTemplate.aggregate(
				aggregation, "shared_space_members", SharedSpaceNodeNested.class);
		return aggregate.getMappedResults();
	}

	@Override
	public void updateNestedNode(SharedSpaceNode node) throws BusinessException {
		SharedSpaceNodeNested nested = new SharedSpaceNodeNested(node);
		Query query = Query.query(Criteria.where("node.uuid").is(node.getUuid()));
		Update update = new Update().set("node", nested);
		mongoTemplate.updateMulti(query, update, SharedSpaceMember.class);
	}

	@Override
	public List<SharedSpaceMember> findAllUserMemberships(String userUuid) {
		return repository.findByAccountUuidAndNested(userUuid, false);
	}

	@Override
	public List<SharedSpaceMember> findAllByAccountAndRole(String accountUuid, String roleUuid) {
		return repository.findByAccountAndRole(accountUuid, roleUuid);
	}

	@Override
	public SharedSpaceMember findByNodeAndUuid(String nodeUuid, String uuid) {
		return repository.findByNodeUuidAndUuid(nodeUuid, uuid);
	}

	@Override
	public List<SharedSpaceMember> findAllMembersByParentAndAccount(String accountUuid, String parentUuid) {
		return repository.findByAccountUuidAndParentUuidAndNested(accountUuid, parentUuid, true);
	}

	@Override
	public List<SharedSpaceNodeNested> findAllNodesByParent(String parentUuid) {
		// TODO: why don't use projection ?
		List<SharedSpaceMember> members = repository.findAllByNodeParentUuid(parentUuid);
		return Lists.transform(members, convertToSharedSpaceNode);
	}

	@Override
	public List<SharedSpaceNodeNested> findAllByParentAndAccount(String accountUuid, String parentUuid) {
		// TODO: why don't use projection ?
		List<SharedSpaceMember> members = findAllMembersByParentAndAccount(accountUuid, parentUuid);
		return Lists.transform(members, convertToSharedSpaceNode);
	}

	@Override
	public List<SharedSpaceMember> findAllMembersByParentAndAccountAndPristine(String accountUuid, String parentUuid,
			Boolean pristine) {
		return repository.findAllMembersByParentAndAccountAndPristine(accountUuid, parentUuid, pristine);
	}

	@Override
	public PageContainer<SharedSpaceNodeNested> findAllSharedSpaces(Account account,
			List<String> domains, Set<NodeType> nodeTypes, Set<String> roleNames, String name, PageContainer<SharedSpaceNodeNested> container, Sort sort) {
		Validate.notNull(container, "Container can not be null.");
		Validate.notNull(container.getPageNumber(), "PageNumber can not be null.");
		Validate.notNull(container.getPageSize(), "PageSize can not be null.");
		if (Objects.isNull(nodeTypes) || nodeTypes.isEmpty()) {
			nodeTypes.addAll(EnumSet.allOf(NodeType.class));
		}
		// common parameters for both 'count' and 'get' queries.
		List<AggregationOperation> commonOperations = Lists.newArrayList();
		commonOperations.add(Aggregation.match(Criteria.where("node.nodeType").in(nodeTypes)));
		commonOperations.add(Aggregation.match(Criteria.where("role.name").in(roleNames)));
		if (Objects.nonNull(account)) {
			commonOperations.add(Aggregation.match(Criteria.where("account.uuid").is(account.getLsUuid())));
		}
		if (!CollectionUtils.isEmpty(domains)) {
			commonOperations.add(Aggregation.match(Criteria.where("node.domainUuid").in(domains)));
		}
		if (!Strings.isNullOrEmpty(name)) {
			commonOperations.add(Aggregation.match(Criteria.where("node.name").regex("(?i).*" + name + ".*")));
		}
		commonOperations.add(Aggregation.group("node"));
		ProjectionOperation projections = Aggregation.project(
				Fields.from(
						Fields.field("uuid", "node.uuid"),
						Fields.field("name", "node.name"),
						Fields.field("parentUuid", "node.parentUuid"),
						Fields.field("creationDate", "node.creationDate"),
						Fields.field("modificationDate", "node.modificationDate"),
						Fields.field("nodeType", "node.nodeType")
						)
				);
		commonOperations.add(projections);

		// first query to get the count of matched elements
		List<AggregationOperation> aggregationOperations = Lists.newArrayList(commonOperations);
		aggregationOperations.add(Aggregation.count().as("count"));
		Aggregation aggregation2 = Aggregation.newAggregation(SharedSpaceMember.class, aggregationOperations);
		List<AggregateNodeCountResult> results = mongoTemplate.aggregate(aggregation2, SharedSpaceMember.class, AggregateNodeCountResult.class).getMappedResults();
		Long count = 0L;
		if (results.size() > 0 && Objects.nonNull(results.get(0) != null)) {
			count = results.get(0).getCount();
		}

		// second query to get matched elements
		aggregationOperations = Lists.newArrayList(commonOperations);
		aggregationOperations.add(Aggregation.sort(sort));
		aggregationOperations.add(Aggregation.skip(Long.valueOf(container.getPageNumber() * container.getPageSize())));
		aggregationOperations.add(Aggregation.limit(Long.valueOf(container.getPageSize())));
		Aggregation aggregation = Aggregation.newAggregation(SharedSpaceMember.class, aggregationOperations);
		container.validateTotalPagesCount(count);
		List<SharedSpaceNodeNested> sharedSpaces = mongoTemplate.aggregate(aggregation, SharedSpaceMember.class, SharedSpaceNodeNested.class)
				.getMappedResults();
		return container.loadData(sharedSpaces);
	}

	@Override
	public List<WorkgroupMemberAutoCompleteResultDto> autocompleteOnActiveMembers(String nodeUuid, String pattern)
			throws BusinessException {
		ProjectionOperation projections = Aggregation.project(
			Fields.from(
				Fields.field("identifier", "account.uuid"),
				Fields.field("accountUuid", "account.uuid"),
				Fields.field("firstName", "account.firstName"),
				Fields.field("lastName", "account.lastName"),
				Fields.field("mail", "account.mail"),
				Fields.field("display", "account.name"),
				Fields.field("sharedSpaceUuid", "node.uuid"),
				Fields.field("sharedSpaceMemberUuid", "uuid")
				)
			);
		Aggregation aggregation = Aggregation.newAggregation(SharedSpaceMember.class,
			Aggregation.match(Criteria.where("node.uuid").is(nodeUuid)),
			Aggregation.match(
				new Criteria().orOperator(
					Criteria.where("account.name").regex("(?i).*" + pattern + ".*"),
					Criteria.where(	"account.mail").regex("(?i).*" + pattern + ".*")
				)
			),
			Aggregation.sort(Direction.DESC, "modificationDate"),
			projections
			);
		AggregationResults<WorkgroupMemberAutoCompleteResultDto> aggregate = mongoTemplate.aggregate(
				aggregation, SharedSpaceMember.class, WorkgroupMemberAutoCompleteResultDto.class);
		return aggregate.getMappedResults();
	}

	@Override
	public List<WorkgroupMemberAutoCompleteResultDto> autocompleteOnAssetAuthor(String nodeUuid, String pattern) {
		 ProjectionOperation projections = Aggregation.project(
				Fields.from(
						Fields.field("identifier", "_id"),
						Fields.field("accountUuid", "_id"),
						Fields.field("firstName", "lastAuthor.firstName"),
						Fields.field("lastName", "lastAuthor.lastName"),
						Fields.field("mail", "lastAuthor.mail"),
						Fields.field("display", "lastAuthor.name"),
						Fields.field("sharedSpaceUuid", "workGroup")
						)
				);
		Aggregation aggregation = Aggregation.newAggregation(WorkGroupNode.class,
			Aggregation.match(Criteria.where("workGroup").is(nodeUuid)),
				Aggregation.match(
						new Criteria().orOperator(
								Criteria.where("lastAuthor.name").regex("(?i).*" + pattern + ".*"),
								Criteria.where("lastAuthor.mail").regex("(?i).*" + pattern + ".*")
								)
						),
			Aggregation.group("lastAuthor.uuid")
				.last("lastAuthor").as("lastAuthor")
				.last("workGroup").as("workGroup"),
			projections
			);
		AggregationResults<WorkgroupMemberAutoCompleteResultDto> aggregate = mongoTemplate.aggregate(
				aggregation, WorkGroupNode.class, WorkgroupMemberAutoCompleteResultDto.class);
		return aggregate.getMappedResults();
	}

	@Override
	public List<SharedSpaceMember> findLastFiveUpdatedNestedWorkgroups(String parentUuid, String accountUuid) {
		Query query = new Query();
		query.addCriteria(Criteria.where("account.uuid").is(accountUuid));
		query.addCriteria(Criteria.where("node.parentUuid").is(parentUuid));
		query.with(Sort.by(Sort.Direction.DESC, "node.modificationDate"));
		query.limit(5);
		return mongoTemplate.find(query, SharedSpaceMember.class);
	}

	@Override
	public PageContainer<SharedSpaceNodeNested> findSharedSpacesByMembersNumber(Integer greaterThan,
			Integer lessThan, Set<String> roles, Sort sort, PageContainer<SharedSpaceNodeNested> container) {
		List<AggregationOperation> commonOperations = Lists.newArrayList();
		if (!CollectionUtils.isEmpty(roles)) {
			commonOperations.add(Aggregation.match(Criteria.where("role.name").in(roles)));
		}
		commonOperations.add(Aggregation.group("node").count().as("total"));
		if (Objects.nonNull(greaterThan)) {
			commonOperations.add(Aggregation.match(Criteria.where("total").gt(greaterThan)));
		}
		if (Objects.nonNull(lessThan)) {
			commonOperations.add(Aggregation.match(Criteria.where("total").lt(lessThan)));
		}
		ProjectionOperation projections = Aggregation.project(
				Fields.from(
						Fields.field("uuid", "node.uuid"),
						Fields.field("name", "node.name"),
						Fields.field("parentUuid", "node.parentUuid"),
						Fields.field("creationDate", "node.creationDate"),
						Fields.field("modificationDate", "node.modificationDate"),
						Fields.field("nodeType", "node.nodeType")
						)
				);
		commonOperations.add(projections);
		// first query to get the count of matched elements
		List<AggregationOperation> aggregationOperations = Lists.newArrayList(commonOperations);
		aggregationOperations.add(Aggregation.count().as("count"));
		Aggregation aggregation2 = Aggregation.newAggregation(SharedSpaceMember.class, aggregationOperations);
		List<AggregateNodeCountResult> results = mongoTemplate.aggregate(aggregation2, SharedSpaceMember.class, AggregateNodeCountResult.class).getMappedResults();
		Long count = 0L;
		if (results.size() > 0 && Objects.nonNull(results.get(0) != null)) {
			count = results.get(0).getCount();
		}
		// second query to get matched elements
		aggregationOperations = Lists.newArrayList(commonOperations);
		aggregationOperations.add(Aggregation.sort(sort));
		aggregationOperations.add(Aggregation.skip(Long.valueOf(container.getPageNumber() * container.getPageSize())));
		aggregationOperations.add(Aggregation.limit(Long.valueOf(container.getPageSize())));
		Aggregation aggregation = Aggregation.newAggregation(SharedSpaceMember.class, aggregationOperations);
		List<SharedSpaceNodeNested> sharedSpaces = mongoTemplate.aggregate(aggregation, SharedSpaceMember.class, SharedSpaceNodeNested.class).getMappedResults();
		container.validateTotalPagesCount(count);
		return container.loadData(sharedSpaces);
	}

	@Override
	public PageContainer<SharedSpaceNodeNested> findOrphanSharedSpaces(Sort sort, PageContainer<SharedSpaceNodeNested> container) {
		Set<String> sharedSpacesUuids = getNestedNodeUuids();
		List<AggregationOperation> nodeOperations = getNodeAggregations(sharedSpacesUuids);
		Aggregation nodeAggregation = Aggregation.newAggregation(SharedSpaceNode.class, nodeOperations);
		List<SharedSpaceNodeNested> results = mongoTemplate.aggregate(nodeAggregation, SharedSpaceNode.class, SharedSpaceNodeNested.class).getMappedResults();
		Long count = countOrphanNodes(nodeOperations);
		// second query to get matched elements
		nodeOperations = Lists.newArrayList(nodeOperations);
		nodeOperations.add(Aggregation.sort(sort));
		nodeOperations.add(Aggregation.skip(Long.valueOf(container.getPageNumber() * container.getPageSize())));
		nodeOperations.add(Aggregation.limit(Long.valueOf(container.getPageSize())));
		container.validateTotalPagesCount(count);
		return container.loadData(results);
	}

	/**
	 * This method will count the orphan sharedSpaceNodes
	 * @return {@link Long}
	 */
	private Long countOrphanNodes(List<AggregationOperation> nodeOperations) {
		List<AggregationOperation> aggregationOperations = Lists.newArrayList(nodeOperations);
		aggregationOperations.add(Aggregation.count().as("count"));
		Aggregation countAggregation = Aggregation.newAggregation(SharedSpaceNode.class, aggregationOperations);
		List<AggregateNodeCountResult> countResults = mongoTemplate.aggregate(countAggregation, SharedSpaceNode.class, AggregateNodeCountResult.class).getMappedResults();
		Long count = 0L;
		if (countResults.size() > 0 && Objects.nonNull(countResults.get(0) != null)) {
			count = countResults.get(0).getCount();
		}
		return count;
	}

	
	/**
	 * This method will return all SharedSpaceNodeNested uuids
	 * @return {@link List} list of string.
	 */
	private Set<String> getNestedNodeUuids() {
		Aggregation memberAggregation = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("node.parentUuid").exists(false)),
				Aggregation.group("node"),
				Aggregation.project("node.uuid")
			);
		List<AggregateNestedNodeUuidResult> sharedSpaces = mongoTemplate.aggregate(memberAggregation, SharedSpaceMember.class, AggregateNestedNodeUuidResult.class).getMappedResults();
		Set<String> sharedSpacesUuids = sharedSpaces.stream().map(node -> node.getUuid()).collect(Collectors.toSet());
		return sharedSpacesUuids;
	}

	/**
	 * This method will return aggregation operation of root sharedSpaceNodes which does not exist in SharedSpaceNodeNestedUuids
	 * @return {@link List} list of AggregationOperation.
	 */
	private List<AggregationOperation> getNodeAggregations(Set<String> sharedSpacesUuids) {
		List<AggregationOperation> nodeOperations = Lists.newArrayList();
		nodeOperations.add(Aggregation.match(Criteria.where("parentUuid").exists(false)));
		nodeOperations.add(Aggregation.match(Criteria.where("uuid").nin(sharedSpacesUuids)));
		ProjectionOperation projections = Aggregation.project(
				Fields.from(
						Fields.field("uuid"),
						Fields.field("name"),
						Fields.field("nodeType"),
						Fields.field("domainUuid"),
						Fields.field("creationDate"),
						Fields.field("modificationDate")
						)
				);
		nodeOperations.add(projections);
		return nodeOperations;
	}

	@Override
	public PageContainer<SharedSpaceMember> findAllMembersWithPagination(String sharedSpaceNodeUuid, String accountUuid,
			Set<String> roles, String email, SortOrder sortOrder, SharedSpaceMemberField sortField,
			PageContainer<SharedSpaceMember> container) {
		Query query = new Query();
		query.addCriteria(Criteria.where("node.uuid").is(sharedSpaceNodeUuid));
		if (!Strings.isNullOrEmpty(accountUuid)) {
			query.addCriteria(Criteria.where("account.uuid").is(accountUuid));
		}
		if (!Strings.isNullOrEmpty(email)) {
			query.addCriteria(Criteria.where("account.mail").is(email));
		}
		if (!CollectionUtils.isEmpty(roles)) {
			query.addCriteria(Criteria.where("role.name").in(roles));
		}
		long count = mongoTemplate.count(query, SharedSpaceMember.class);
		logger.debug("Total of elements returned by the query without pagination: {}", count);
		if (count == 0) {
			return new PageContainer<SharedSpaceMember>();
		}
		Pageable paging = PageRequest.of(container.getPageNumber(), container.getPageSize());
		query.with(paging);
		query.with(Sort.by(SortOrder.getSortDir(sortOrder), sortField.toString()));
		container.validateTotalPagesCount(count);
		List<SharedSpaceMember> members = mongoTemplate.find(query, SharedSpaceMember.class);
		return container.loadData(members);
	}
}
