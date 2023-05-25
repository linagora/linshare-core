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
package org.linagora.linshare.core.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceRoleBusinessService;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.SharedSpaceRoleResourceAccessControl;
import org.linagora.linshare.core.service.SharedSpaceRoleService;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;

public class SharedSpaceRoleServiceImpl extends GenericServiceImpl<Account, SharedSpaceRole>
		implements SharedSpaceRoleService {

	private final SharedSpaceRoleBusinessService sharedSpaceRoleBusinessService;

	public SharedSpaceRoleServiceImpl(SharedSpaceRoleBusinessService sharedSpaceRoleBusinessService,
			SharedSpaceRoleResourceAccessControl rac,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super(rac, sanitizerInputHtmlBusinessService);
		this.sharedSpaceRoleBusinessService = sharedSpaceRoleBusinessService;
	}

	@Override
	public SharedSpaceRole find(Account authUser, Account actor, String uuid) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notEmpty(uuid, "Missing required shared space role uuid.");
		SharedSpaceRole found = sharedSpaceRoleBusinessService.find(uuid);
		if (found == null) {
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_ROLE_NOT_FOUND,
					"The role with uuid " + uuid + " was not found");
		}
		checkReadPermission(authUser, actor, SharedSpaceRole.class, BusinessErrorCode.SHARED_SPACE_ROLE_FORBIDDEN,
				found);
		return found;
	}

	@Override
	public SharedSpaceRole findByName(Account authUser, Account actor, String name) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notEmpty(name, "Missing required shared space role name.");
		SharedSpaceRole found = sharedSpaceRoleBusinessService.findByName(name);
		if (found == null) {
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_ROLE_NOT_FOUND,
					"The role with name " + name + " was not found");
		}
		checkReadPermission(authUser, actor, SharedSpaceRole.class, BusinessErrorCode.SHARED_SPACE_ROLE_FORBIDDEN,
				found);
		return found;
	}

	@Override
	public List<SharedSpaceRole> findAll(Account authUser, Account actor) {
		preChecks(authUser, actor);
		checkListPermission(authUser, actor, SharedSpaceRole.class, BusinessErrorCode.SHARED_SPACE_ROLE_FORBIDDEN,
				null);
		List<SharedSpaceRole> foundRoles = sharedSpaceRoleBusinessService.findAll();
		return foundRoles;
	}

	public SharedSpaceRole getAdmin(Account authUser, Account actor) {
		preChecks(authUser, actor);
		SharedSpaceRole roleAdmin = findByName(authUser, actor, "ADMIN");
		return roleAdmin;
	}

	public SharedSpaceRole getWorkSpaceAdmin(Account authUser, Account actor) {
		preChecks(authUser, actor);
		return findByName(authUser, actor, "WORK_SPACE_ADMIN");
	}

	@Override
	public List<SharedSpaceRole> findRolesByNodeType(Account authUser, Account actor, NodeType type) {
		preChecks(authUser, actor);
		Validate.notNull(type, "Missing required shared space role.");
		checkListPermission(authUser, actor, SharedSpaceRole.class, BusinessErrorCode.SHARED_SPACE_ROLE_FORBIDDEN,
				null);
		return sharedSpaceRoleBusinessService.findRolesByNodeType(type);
	}

	@Override
	public SharedSpaceRole findByNameAndNodeType(Account authUser, Account actor, String name, NodeType nodeType) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notEmpty(name, "Missing required shared space role name.");
		Validate.notNull(nodeType, "Missing required nodeType.");
		SharedSpaceRole found = sharedSpaceRoleBusinessService.findByNameAndNodeType(name, nodeType);
		if (found == null) {
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_ROLE_NOT_FOUND,
					"The role with name " + name + " was not found");
		}
		checkReadPermission(authUser, actor, SharedSpaceRole.class, BusinessErrorCode.SHARED_SPACE_ROLE_FORBIDDEN,
				found);
		return found;
	}

	@Override
	public Boolean exist(Account authUser, Account actor, String sharedSpaceRoleName) {
		preChecks(authUser, actor);
		Validate.notEmpty(sharedSpaceRoleName, "Missing required shared space role name.");
		SharedSpaceRole found = sharedSpaceRoleBusinessService.findByName(sharedSpaceRoleName);
		if (Objects.isNull(found)) {
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_ROLE_NOT_FOUND,
					"The role: " + sharedSpaceRoleName + " was not found");
		}
		checkReadPermission(authUser, actor, SharedSpaceRole.class, BusinessErrorCode.SHARED_SPACE_ROLE_FORBIDDEN,
				found);
		return true;
	}

	@Override
	public Set<String> findAllSharedSpaceRoleNames(Account authUser, Account actor) {
		preChecks(authUser, actor);
		checkListPermission(actor, authUser, SharedSpaceRole.class, BusinessErrorCode.SHARED_SPACE_ROLE_FORBIDDEN,
				null);
		Set<String> roles = sharedSpaceRoleBusinessService.findAllSharedSpaceRoleNames(authUser, actor);
		return roles;
	}
}
