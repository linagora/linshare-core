/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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
package org.linagora.linshare.core.service.impl;

import java.util.List;

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
		checkListPermission(actor, authUser, SharedSpaceRole.class, BusinessErrorCode.SHARED_SPACE_ROLE_FORBIDDEN,
				null);
		List<SharedSpaceRole> foundRoles = sharedSpaceRoleBusinessService.findAll();
		return foundRoles;
	}

	public SharedSpaceRole getAdmin(Account authUser, Account actor) {
		preChecks(authUser, actor);
		SharedSpaceRole roleAdmin = findByName(authUser, actor, "ADMIN");
		return roleAdmin;
	}

	public SharedSpaceRole getDriveAdmin(Account authUser, Account actor) {
		preChecks(authUser, actor);
		SharedSpaceRole roleAdmin = findByName(authUser, actor, "DRIVE_ADMIN");
		return roleAdmin;
	}

	@Override
	public List<SharedSpaceRole> findRolesByNodeType(Account authUser, Account actor, NodeType type) {
		preChecks(authUser, actor);
		Validate.notNull(type, "Missing required shared space role.");
		checkListPermission(actor, authUser, SharedSpaceRole.class, BusinessErrorCode.SHARED_SPACE_ROLE_FORBIDDEN,
				null);
		return sharedSpaceRoleBusinessService.findRolesByNodeType(type);
	}
}
