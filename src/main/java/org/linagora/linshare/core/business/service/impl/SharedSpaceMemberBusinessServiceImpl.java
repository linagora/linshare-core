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

import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;
import org.linagora.linshare.mongo.repository.SharedSpaceMemberMongoRepository;

public class SharedSpaceMemberBusinessServiceImpl implements SharedSpaceMemberBusinessService {
	private final SharedSpaceMemberMongoRepository sharedSpaceMemberMongoRepository;

	public SharedSpaceMemberBusinessServiceImpl(SharedSpaceMemberMongoRepository sharedSpaceMemberMongoRepository) {
		super();
		this.sharedSpaceMemberMongoRepository = sharedSpaceMemberMongoRepository;
	}

	@Override
	public SharedSpaceMember find(String uuid) throws BusinessException {
		return sharedSpaceMemberMongoRepository.findByUuid(uuid);
	}

	@Override
	public SharedSpaceMember create(SharedSpaceMember member) throws BusinessException {
		return sharedSpaceMemberMongoRepository.insert(member);
	}

	@Override
	public List<SharedSpaceMember> findAll() throws BusinessException {
		return sharedSpaceMemberMongoRepository.findAll();
	}

	@Override
	public SharedSpaceMember findByMemberAndSharedSpaceNode(String accountUuid, String nodeUuid) {
		return sharedSpaceMemberMongoRepository.findByAccountAndNode(accountUuid, nodeUuid);
	}

	@Override
	public void delete(SharedSpaceMember memberToDelete) {
		sharedSpaceMemberMongoRepository.delete(memberToDelete);
	}

	@Override
	public SharedSpaceMember update(SharedSpaceMember foundMemberToUpdate, SharedSpaceMember memberToUpdate) {
		foundMemberToUpdate.setAccount(memberToUpdate.getAccount());
		foundMemberToUpdate.setNode(memberToUpdate.getNode());
		foundMemberToUpdate.setRole(memberToUpdate.getRole());
		foundMemberToUpdate.setModificationDate(new Date());
		return sharedSpaceMemberMongoRepository.save(foundMemberToUpdate);
	}

	@Override
	public SharedSpaceMember updateRole(SharedSpaceMember foundMemberToUpdate, GenericLightEntity newRole) {
		foundMemberToUpdate.setRole(newRole);
		foundMemberToUpdate.setModificationDate(new Date());
		return sharedSpaceMemberMongoRepository.save(foundMemberToUpdate);
	}

	@Override
	public List<SharedSpaceMember> findBySharedSpaceNodeUuid(String shareSpaceNodeUuid) {
		return sharedSpaceMemberMongoRepository.findByShareSpaceNodeUuid(shareSpaceNodeUuid);
	}

	@Override
	public List<String> findMembersUuidBySharedSpaceNodeUuid(String shareSpaceNodeUuid) {
		List<SharedSpaceMember> members = sharedSpaceMemberMongoRepository.findByShareSpaceNodeUuid(shareSpaceNodeUuid);
		Stream<GenericLightEntity> accounts = members.stream().map(SharedSpaceMember::getAccount);
		return accounts.map(GenericLightEntity::getUuid).collect(Collectors.toList());
	}

	@Override
	public void deleteAll(List<SharedSpaceMember> foundMembersToDelete) {
		sharedSpaceMemberMongoRepository.delete(foundMembersToDelete);
	}

	@Override
	public List<SharedSpaceMember> findByMemberName(String name) throws BusinessException {
		return sharedSpaceMemberMongoRepository.findByMemberName(name);
	}

}
