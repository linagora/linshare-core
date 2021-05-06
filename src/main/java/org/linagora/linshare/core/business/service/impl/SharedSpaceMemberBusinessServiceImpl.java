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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberWorkgroup;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.LightSharedSpaceRole;
import org.linagora.linshare.mongo.repository.SharedSpaceMemberMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpaceNodeMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpaceRoleMongoRepository;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class SharedSpaceMemberBusinessServiceImpl implements SharedSpaceMemberBusinessService {

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
		return repository.insert(member);
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
	}

	@Override
	public SharedSpaceMember update(SharedSpaceMember foundMemberToUpdate, SharedSpaceMember memberToUpdate) {
		Validate.notNull(memberToUpdate.getRole(), "The role must be set.");
		foundMemberToUpdate.setRole(new LightSharedSpaceRole(checkRole(memberToUpdate.getRole().getUuid())));
		foundMemberToUpdate.setModificationDate(new Date());
		return checkSharedSpaceMemberTypeOnUpdate(foundMemberToUpdate);
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

	
	/**
	 * @param accountUuid String uuid of shared space account
	 * @param withRole Boolean if true return the role of  member in the node
	 * @return {@link SharedSpaceNodeNested} {@link List} All nodes of the given member except Nested SharedSpaces on Drives 
	 */
	@Override
	public List<SharedSpaceNodeNested> findAllNestedNodeByAccountUuid(String accountUuid, boolean withRole,
			String parent) {
		// TODO Improve performance of retrieving the sharedSpaces(may be use of agregation)
		if (withRole) {
			List<SharedSpaceMember> members = repository.findByAccountUuidAndNodeParentUuid(accountUuid, parent);
			return members.stream().map(member -> new SharedSpaceNodeNested(member)).collect(Collectors.toList());
		} else {
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.match(Criteria.where("account.uuid").is(accountUuid)),
					Aggregation.match(Criteria.where("node.parentUuid").is(parent)),
					Aggregation.project("node.uuid",
							"node.name",
							"node.nodeType",
							"node.creationDate",
							"node.modificationDate"));
			return mongoTemplate.aggregate(aggregation, "shared_space_members", SharedSpaceNodeNested.class)
					.getMappedResults();
		}
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
	public List<SharedSpaceMember> findAllMembersByParent(String parentUuid) {
		return repository.findByParentUuidAndNested(parentUuid, true);
	}

	@Override
	public List<SharedSpaceNodeNested> findAllNodesByParent(String parentUuid) {
		List<SharedSpaceMember> members = findAllMembersByParent(parentUuid);
		return Lists.transform(members, convertToSharedSpaceNode);
	}

	@Override
	public List<SharedSpaceNodeNested> findAllByParentAndAccount(String accountUuid, String parentUuid) {
		List<SharedSpaceMember> members = findAllMembersByParentAndAccount(accountUuid, parentUuid);
		return Lists.transform(members, convertToSharedSpaceNode);
	}

	@Override
	public List<SharedSpaceMember> findAllMembersWithNoConflictedRoles(String accountUuid, String parentUuid,
			String roleUuid) {
		return repository.findAllMembersWithNoConflictedRoles(accountUuid, parentUuid, true, roleUuid);
	}

	@Override
	public List<SharedSpaceMember> findAllMembersByParentAndAccountAndPristine(String accountUuid, String parentUuid,
			Boolean pristine) {
		return repository.findAllMembersByParentAndAccountAndPristine(accountUuid, parentUuid, pristine);
	}

	@Override
	public PageContainer<SharedSpaceNodeNested> findAllByAccount(String accountUuid,
			PageContainer<SharedSpaceNodeNested> container) {
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("account.uuid").is(accountUuid)),
				Aggregation.project("node.uuid",
						"node.name",
						"node.nodeType",
						"node.creationDate",
						"node.modificationDate"),
				Aggregation.skip(Long.valueOf(container.getPageNumber() * container.getPageSize())),
				Aggregation.limit(Long.valueOf(container.getPageSize())));
		List<SharedSpaceNodeNested> sharedSpaces = mongoTemplate.aggregate(aggregation, "shared_space_members", SharedSpaceNodeNested.class)
				.getMappedResults();
		return new PageContainer<SharedSpaceNodeNested>(container.getPageNumber(), container.getPageSize(), getCount(accountUuid), sharedSpaces);
	}

	private Long getCount(String accountUuid) {
		Query countQuery = new Query(Criteria.where("account.uuid").is(accountUuid));
		return mongoTemplate.count(countQuery, SharedSpaceMember.class);
	}

	@Override
	public PageContainer<SharedSpaceNodeNested> findAll(PageContainer<SharedSpaceNodeNested> container) {
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.project("node.uuid",
						"node.name",
						"node.nodeType",
						"node.creationDate",
						"node.modificationDate"),
				Aggregation.skip(Long.valueOf(container.getPageNumber() * container.getPageSize())),
				Aggregation.limit(Long.valueOf(container.getPageSize())));
		List<SharedSpaceNodeNested> sharedSpaces = mongoTemplate.aggregate(aggregation, "shared_space_members", SharedSpaceNodeNested.class)
				.getMappedResults();
		return new PageContainer<SharedSpaceNodeNested>(container.getPageNumber(), container.getPageSize(), nodeRepository.count(), sharedSpaces);
	}
}
