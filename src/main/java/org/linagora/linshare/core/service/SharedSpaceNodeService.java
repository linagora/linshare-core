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
package org.linagora.linshare.core.service;

import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.fields.SharedSpaceField;
import org.linagora.linshare.core.domain.entities.fields.SharedSpaceMemberField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.PatchDto;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupDto;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;
import org.linagora.linshare.webservice.utils.PageContainer;

public interface SharedSpaceNodeService {

	SharedSpaceNode find(Account authUser, Account actor, String uuid) throws BusinessException;
	
	SharedSpaceNode find(Account authUser, Account actor, String uuid, boolean withRole, boolean lastUpdater) throws BusinessException;

	SharedSpaceNode create(Account authUser, Account actor, SharedSpaceNode node) throws BusinessException;

	SharedSpaceNode update(Account authUser, Account actor, SharedSpaceNode node) throws BusinessException;
	
	SharedSpaceNode updatePartial(Account authUser, Account actor, PatchDto node) throws BusinessException;

	SharedSpaceNode delete(Account authUser, Account actor, SharedSpaceNode node) throws BusinessException;

	List<SharedSpaceNode> findAll(Account authUser, Account actor);

	/** Search some SharedSpaceNode by their name
	 **/
	List<SharedSpaceNode> searchByName(Account authUser, Account actor, String name) throws BusinessException;

	List<SharedSpaceMember> findAllMembers(Account authUser, Account actor, String sharedSpaceNodeUuid, String accountUuid);

	@Deprecated
	/**
	 * Only use to compability with threadFacade and WorkgroupFacadeImpl
	 *
	 */
	List<SharedSpaceNodeNested> findAllByAccount(Account authUser, Account actor);

	@Deprecated
	/**
	 * Only use to compability with threadFacade
	 *
	 */
	WorkGroupDto createWorkGroupDto(Account authUser, Account actor, SharedSpaceNode node) throws BusinessException;

	@Deprecated
	/**
	 * Only use to compability with threadFacade
	 *
	 */
	WorkGroupDto deleteWorkgroupDto(Account authUser, Account actor, SharedSpaceNode node) throws BusinessException;

	/**
	 * Admins API only
	 * @param authUser
	 * @param actor
	 * @param account
	 * @param domains
	 * @param sortOrder
	 * @param nodeTypes
	 * @param sharedSpaceRoles
	 * @param sortField
	 * @param name
	 * @param greaterThanOrEqualTo
	 * @param lessThanOrEqualTo	
	 * @param container
	 * @return
	 */
	PageContainer<SharedSpaceNodeNested> findAll(Account authUser, Account actor, Account account, List<String> domains,
			SortOrder sortOrder, Set<NodeType> nodeTypes, Set<String> sharedSpaceRoles, SharedSpaceField sortField, String name, Integer greaterThanOrEqualTo, Integer lessThanOrEqualTo, PageContainer<SharedSpaceNodeNested> container);

	/**
	 * user API only
	 * @param authUser
	 * @param actor
	 * @param withRole
	 * @param parent
	 * @return
	 */
	List<SharedSpaceNodeNested> findAllByAccount(Account authUser, Account actor, boolean withRole, String parent);

	@Deprecated
	/**
	 * Used for old Admins API only
	 * @param authUser
	 * @param actor
	 * @return
	 */
	List<SharedSpaceNode> findAllRootWorkgroups(Account authUser, Account actor);

	PageContainer<SharedSpaceMember> findAllMembersWithPagination(Account authUser, Account actor, String sharedSpaceNodeUuid, String accountUuid, Set<String> roles, String email, String firstName, String lastName, String pattern, String type, SortOrder sortOrder, SharedSpaceMemberField sortField, PageContainer<SharedSpaceMember> container);

}
