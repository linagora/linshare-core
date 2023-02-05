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
package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.SharedSpaceRoleFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.SharedSpacePermissionService;
import org.linagora.linshare.core.service.SharedSpaceRoleService;
import org.linagora.linshare.mongo.entities.SharedSpacePermission;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class SharedSpaceRoleFacadeImpl extends GenericFacadeImpl implements SharedSpaceRoleFacade {

	private final SharedSpaceRoleService roleService;

	private final SharedSpacePermissionService permissionService;

	protected Function<SharedSpaceRole, SharedSpaceRole> convert = new Function<SharedSpaceRole, SharedSpaceRole>() {
		@Override
		public SharedSpaceRole apply(SharedSpaceRole arg0) {
			SharedSpaceRole role = new SharedSpaceRole(arg0);
			role.setAuthor(null);
			role.setDomain(null);
			role.setCreationDate(null);
			role.setModificationDate(null);
			return role;
		}
	};

	protected Function<SharedSpacePermission, SharedSpacePermission> convertPermissions = new Function<SharedSpacePermission, SharedSpacePermission>() {
		@Override
		public SharedSpacePermission apply(SharedSpacePermission arg0) {
			SharedSpacePermission perm = new SharedSpacePermission();
			perm.setUuid(arg0.getUuid());
			perm.setAction(arg0.getAction());
			perm.setResource(arg0.getResource());
			return perm;
		}
	};

	public SharedSpaceRoleFacadeImpl(
			AccountService accountService,
			SharedSpaceRoleService roleService,
			SharedSpacePermissionService permissionService) {
		super(accountService);
		this.roleService = roleService;
		this.permissionService = permissionService;
	}

	@Override
	public SharedSpaceRole find(String actorUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Missing required shared space role uuid.");
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		return convert.apply(roleService.find(authUser, actor, uuid));
	}

	@Override
	public SharedSpaceRole findByName(String actorUuid, String name) throws BusinessException {
		Validate.notEmpty(name, "Missing required shared space role name.");
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		return convert.apply(roleService.findByName(authUser, actor, name));
	}

	@Override
	public List<SharedSpaceRole> findAll(String actorUuid, NodeType nodeType) throws BusinessException {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		List<SharedSpaceRole> roles = Lists.newArrayList();
		if (Objects.isNull(nodeType)) {
			roles = roleService.findAll(authUser, actor);
		} else {
			roles = roleService.findRolesByNodeType(authUser, actor, nodeType);
		}
		return Lists.transform(roles, convert);
	}

	@Override
	public List<SharedSpacePermission> findAll(String actorUuid, String roleUuid) throws BusinessException {
		Validate.notEmpty(roleUuid, "Missing required shared space role uuid.");
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		SharedSpaceRole role = roleService.find(authUser, actor, roleUuid);
		List<SharedSpacePermission> list = permissionService.findByRole(authUser, actor, role.getName());
		return Lists.transform(list, convertPermissions);
	}

}
