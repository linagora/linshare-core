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
package org.linagora.linshare.core.business.service;

import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.dto.WorkgroupMemberAutoCompleteResultDto;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.springframework.data.domain.Sort;

public interface SharedSpaceMemberBusinessService {

	SharedSpaceMember find(String uuid) throws BusinessException;

	List<SharedSpaceMember> findAll() throws BusinessException;

	SharedSpaceMember create(SharedSpaceMember sharedSpacemember) throws BusinessException;

	SharedSpaceMember findByAccountAndNode(String accountUuid, String nodeUuid) throws BusinessException;

	void delete(SharedSpaceMember memberToDelete) throws BusinessException;

	SharedSpaceMember update(SharedSpaceMember foundMemberToUpdate, SharedSpaceMember member)
			throws BusinessException;

	List<SharedSpaceMember> findBySharedSpaceNodeUuid(String shareSpaceNodeUuid) throws BusinessException;

	void deleteAll(List<SharedSpaceMember> foundMembersToDelete) throws BusinessException;

	List<String> findMembersUuidBySharedSpaceNodeUuid(String shareSpaceNodeUuid) throws BusinessException;

	List<SharedSpaceMember> findByMemberName(String name) throws BusinessException;

	void updateNestedNode(SharedSpaceNode node) throws BusinessException;

	List<SharedSpaceMember> findAllUserMemberships(String userUuid);

	List<SharedSpaceMember> findAllByAccountAndRole(String accountUuid, String roleUuid);

	SharedSpaceMember findByNodeAndUuid(String nodeUuid, String uuid);

	/**
	 * This method is used by Admins only
	 * @param nodeTypes is the type of sharedSpace to filter with (Drive/WORK_GROUP)
	 * @param roleNames is the list of sharedSpaceRoles to filter with
	 * @param name is the sharedSpaces' name to filter by
	 * @param container contains the pageNumber, pageSize
	 * @param Account The account to filter by
	 * @return {@link SharedSpaceNodeNested} pageContainer of SharedSpaceNodeNested
	 */
	PageContainer<SharedSpaceNodeNested> findAllSharedSpaces(Account account, Set<NodeType> nodeTypes, Set<String> roleNames, String name, PageContainer<SharedSpaceNodeNested> container, Sort sort);


	List<SharedSpaceMember> findAllMembersByParentAndAccount(String accountUuid, String parentUuid);
	List<SharedSpaceMember> findAllMembersByParentAndAccountAndPristine(String accountUuid, String parentUuid, Boolean pristine);
	List<SharedSpaceNodeNested> findAllByParentAndAccount(String accountUuid, String parentUuid);

	/**
	 * It is a projection onto SharedSpaceMembers to get a list of SharedSpaces which belong to
	 * the following account with its owner role (Optionally).
	 * @param accountUuid String uuid of shared space account
	 * @param withRole Boolean if true return the role of  member in the node
	 * @param parent : by default all SharedSpaces without parent are returned. (Nullable)
	 * @return {@link SharedSpaceNodeNested} {@link List} list of Sharedspaces.
	 */
	List<SharedSpaceNodeNested> findAllSharedSpacesByAccountAndParent(String accountUuid, boolean withRole, String parent);

	/**
	 * It is a projection onto SharedSpaceMembers to get a list of SharedSpaces which belong to
	 * the following account with its owner role (Optionally).
	 * Workgroups not considered as nested will be also returned.
	 * @param accountUuid String uuid of shared space account
	 * @param withRole Boolean if true return the role of  member in the node
	 * @param parent : by default all SharedSpaces without parent are returned. (Nullable)
	 * @param types the list of node types you want to get (NullAble.)
	 * @return {@link SharedSpaceNodeNested} {@link List} list of Sharedspaces.
	 */
	List<SharedSpaceNodeNested> findAllSharedSpacesByAccountAndParentForUsers(String accountUuid, boolean withRole, String parent, Set<NodeType> types);

	/**
	 * This method must only be used by root account. there is no filter on the account member.
	 * @param parentUuid
	 * @return
	 */
	List<SharedSpaceNodeNested> findAllNodesByParent(String parentUuid);

	/**
	 * Autocomplete existing members by last name, first name or email
	 * @param nodeUuid
	 * @param pattern
	 * @return
	 * @throws BusinessException
	 */
	List<WorkgroupMemberAutoCompleteResultDto> autocompleteOnActiveMembers(String nodeUuid, String pattern) throws BusinessException;

	List<WorkgroupMemberAutoCompleteResultDto> autocompleteOnAssetAuthor(String nodeUuid, String pattern);
}
