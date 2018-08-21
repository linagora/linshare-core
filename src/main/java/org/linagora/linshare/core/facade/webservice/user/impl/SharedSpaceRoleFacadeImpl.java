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
package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.List;

import org.apache.commons.lang.Validate;
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
	public List<SharedSpaceRole> findAll(String actorUuid) throws BusinessException {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		List<SharedSpaceRole> list = roleService.findAll(authUser, actor);
		return Lists.transform(list, convert);
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
