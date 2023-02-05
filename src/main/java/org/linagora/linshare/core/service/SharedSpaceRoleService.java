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
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;

public interface SharedSpaceRoleService {

	SharedSpaceRole find(Account authUser, Account actor, String uuid) throws BusinessException;

	SharedSpaceRole findByName(Account authUser, Account actor, String name) throws BusinessException;

	List<SharedSpaceRole> findAll(Account authUser, Account actor) throws BusinessException;

	SharedSpaceRole getAdmin(Account authUser, Account actor) throws BusinessException;

	SharedSpaceRole getWorkSpaceAdmin(Account authUser, Account actor) throws BusinessException;

	/**
	 * This method will retrieve a list of roles filtered by {@link NodeType}
	 * @param authUser: The authenticated account
	 * @param actor: The account on which the processing will be applied
	 * @param type
	 * @return List of {@link SharedSpaceRole}
	 */
	List<SharedSpaceRole> findRolesByNodeType(Account authUser, Account actor, NodeType type);

	SharedSpaceRole findByNameAndNodeType(Account authUser, Account actor, String name, NodeType nodeType)
			throws BusinessException;

	Boolean exist(Account authUser, Account actor, String sharedSpaceRoleName);

	Set<String> findAllSharedSpaceRoleNames(Account authUser, Account actor);
}

