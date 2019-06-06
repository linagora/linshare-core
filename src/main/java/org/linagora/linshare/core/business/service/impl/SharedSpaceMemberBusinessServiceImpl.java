/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.business.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;
import org.linagora.linshare.mongo.repository.SharedSpaceMemberMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpaceNodeMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpaceRoleMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;

public class SharedSpaceMemberBusinessServiceImpl implements SharedSpaceMemberBusinessService {

	protected final SharedSpaceMemberMongoRepository repository;

	protected final SharedSpaceRoleMongoRepository roleRepository;

	protected final SharedSpaceNodeMongoRepository nodeRepository;

	protected final UserRepository<User> userRepository;
	
	protected final MongoTemplate mongoTemplate;

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
		member.setRole(new GenericLightEntity(checkRole(member.getRole().getUuid())));
		member.setNode(new SharedSpaceNodeNested(checkNode(member.getNode().getUuid())));
		member.setAccount(new SharedSpaceAccount(checkUser(member.getAccount().getUuid())));
		return repository.insert(member);
	}

	private SharedSpaceRole checkRole(String roleUuid) {
		SharedSpaceRole role = roleRepository.findByUuid(roleUuid);
		if (role == null) {
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_ROLE_NOT_FOUND,
					"The required role does not exist.");
		}
		return role;
	}

	private SharedSpaceNode checkNode(String nodeUuid) {
		SharedSpaceNode node = nodeRepository.findByUuid(nodeUuid);
		if (node == null) {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_NOT_FOUND, "The required role does not exist.");
		}
		return node;
	}

	private User checkUser(String userUuid) {
		User user = userRepository.findByLsUuid(userUuid);
		if (user == null) {
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND, "The required role does not exist.");
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
		Validate.notNull(memberToUpdate.getRole(),"The role must be set.");
		foundMemberToUpdate.setRole(new GenericLightEntity(checkRole(memberToUpdate.getRole().getUuid())));
		foundMemberToUpdate.setModificationDate(new Date());
		return repository.save(foundMemberToUpdate);
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
	public List<SharedSpaceNodeNested> findAllNestedNodeByAccountUuid(String accountUuid, boolean withRole) {
		if (withRole) {
			List<SharedSpaceMember> members = repository.findByAccountUuid(accountUuid);
			return members.stream().map(member -> new SharedSpaceNodeNested(member)).collect(Collectors.toList());
		}
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("account.uuid").is(accountUuid)),
				Aggregation.project("node.uuid",
						"node.name",
						"node.nodeType",
						"node.creationDate",
						"node.modificationDate"));
		return mongoTemplate.aggregate(aggregation, "shared_space_members", SharedSpaceNodeNested.class)
				.getMappedResults();
	}

	@Override
	public void updateNestedNode(SharedSpaceNode node) throws BusinessException {
		List<SharedSpaceMember> members = repository.findByNodeUuid(node.getUuid());
		for (SharedSpaceMember member : members) {
			member.getNode().setName(node.getName());
			member.getNode().setModificationDate(node.getModificationDate());
		}
		repository.saveAll(members);
	}

	@Override
	public List<SharedSpaceMember> findAllUserMemberships(String userUuid) {
		return repository.findByAccountUuid(userUuid);
	}

	@Override
	public List<SharedSpaceMember> findAllByAccountAndRole(String accountUuid, String roleUuid) {
		return repository.findByAccountAndRole(accountUuid, roleUuid);
	}

	@Override
	public SharedSpaceMember findByNodeAndUuid(String nodeUuid, String uuid) {
		return repository.findByNodeUuidAndUuid(nodeUuid, uuid);
	}

}
