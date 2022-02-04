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
package org.linagora.linshare.mongo.repository;

import java.util.List;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface SharedSpaceMemberMongoRepository extends MongoRepository<SharedSpaceMember, String> {

	SharedSpaceMember findByUuid(String uuid) throws BusinessException;

	@Query("{ 'account.uuid' : ?0, 'node.uuid' : ?1 }")
	SharedSpaceMember findByAccountAndNode(String accountUuid, String nodeUuid);

	List<SharedSpaceMember> findByNodeUuid(String shareSpaceNodeUuid);

	@Query("{'account.name': {$regex : ?0, '$option':'i'}}")
	List<SharedSpaceMember> findByMemberName(String memberName);

	List<SharedSpaceMember> findByAccountUuidAndNested(String accountUuid, boolean nested);

	@Query("{ 'account.uuid' : ?0, 'role.uuid' : ?1 }")
	List<SharedSpaceMember> findByAccountAndRole(String accountUuid, String roleUuid);

	@Query("{ 'node.uuid' : ?0, 'uuid' : ?1 }")
	SharedSpaceMember findByNodeUuidAndUuid(String nodeUuid, String uuid);

	/**
	 * It allows to get all members of a WorkSpace only because it uses parentUuid 
	 * and the method is called only from the WorkSpace service
	 * @param parentUuid
	 * @return
	 */
	@Query("{ 'node.parentUuid' : ?0}")
	List<SharedSpaceMember> findAllByNodeParentUuid(String parentUuid);

	@Query("{ 'account.uuid' : ?0, 'node.parentUuid' : ?1,  'nested' : ?2 }")
	List<SharedSpaceMember> findByAccountUuidAndParentUuidAndNested(String accountUuid, String parentUuid, boolean nested);

	@Query("{ 'account.uuid' : ?0, 'node.parentUuid' : ?1, 'nested' : ?2, 'role.uuid' : { $ne: ?3 } }")
	List<SharedSpaceMember> findAllMembersWithConflictRoles(String accountUuid, String parentUuid, boolean nested, String roleUuid);

	@Query("{ 'account.uuid' : ?0, 'node.parentUuid' : ?1, 'nested' : ?2, 'role.uuid' : ?3 }")
	List<SharedSpaceMember> findAllMembersWithNoConflictedRoles(String accountUuid, String parentUuid, boolean nested, String roleUuid);

	/**
	 * Method use to get all SharedSpaces (workgroup) membership in a SharedSpace (WorkSpace) in order to update
	 * all pristine membership.
	 *
	 * @param accountUuid : the uuid of the account that belong to a SharedSpace
	 * @param parentUuid : the uuid of the parent SharedSpace (aka WorkSpace)
	 * @param pristine: Define if the membership was updated in this SharedSpace (WG) or if the membership is the same as in the WorkSpace.
	 * @return
	 */
	@Query("{ 'accountUuid' : ?0, 'node.parentUuid' : ?1, 'pristine' : ?2 }")
	List<SharedSpaceMember> findAllMembersByParentAndAccountAndPristine(String accountUuid, String parentUuid,
			boolean pristine);
}
