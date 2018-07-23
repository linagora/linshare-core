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
package org.linagora.linshare.core.service.impl;

import java.util.List;
import org.jsoup.helper.Validate;
import org.linagora.linshare.core.business.service.SharedSpacePermissionBusinessService;
import org.linagora.linshare.core.domain.constants.SharedSpaceActionType;
import org.linagora.linshare.core.domain.constants.SharedSpaceResourceType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.SharedSpacePermissionService;
import org.linagora.linshare.mongo.entities.SharedSpacePermission;

public class SharedSpacePermissionServiceImpl implements SharedSpacePermissionService {

	private final SharedSpacePermissionBusinessService sharedSpacePermissionBusinessService;

	public SharedSpacePermissionServiceImpl(SharedSpacePermissionBusinessService sharedSpacePermissionBusinessService) {
		this.sharedSpacePermissionBusinessService = sharedSpacePermissionBusinessService;
	}

	@Override
	public SharedSpacePermission findByUuid(Account authUser, Account actor, String uuid) throws BusinessException {
		Validate.notNull(authUser, "Missing authUser account");
		Validate.notNull(actor, "Missing actor account ");
		Validate.notEmpty(actor.getLsUuid(), "Missing authUser uuid");
		Validate.notEmpty(uuid, "Missing required shared space permission uuid.");
		SharedSpacePermission foundPermission = sharedSpacePermissionBusinessService.findByUuid(uuid);
		if (foundPermission == null) {
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_PERMISSION_NOT_FOUND,
					"The permission with uuid " + uuid + " was not found");
		}
		if (!(authUser.isUser())) {
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_PERMISSION_FORBIDDEN, "you are not authorized");
		}
		return foundPermission;
	}

	@Override
	public List<SharedSpacePermission> findByRole(Account authUser, Account actor, String roleName)
			throws BusinessException {
		Validate.notNull(authUser, "Missing authUser account");
		Validate.notNull(actor, "Missing actor");
		Validate.notEmpty(actor.getLsUuid(), "Missing actor uuid");
		List<SharedSpacePermission> foundPermission = sharedSpacePermissionBusinessService.findByRole(roleName);
		if (foundPermission == null) {
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_PERMISSION_NOT_FOUND,
					"The permission with related roles " + roleName + " was not found");
		}
		if (!(authUser.isUser())) {
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_PERMISSION_FORBIDDEN, "you are not authorized");
		}
		return foundPermission;
	}

	@Override
	public List<SharedSpacePermission> findAll(Account authUser, Account actor) throws BusinessException {
		if (!(authUser.isUser())) {
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_PERMISSION_FORBIDDEN, "you are not authorized");
		}
		return sharedSpacePermissionBusinessService.findAll();
	}

	public Boolean hasPermission(Account authUser, Account actor, String roleUuid, SharedSpaceActionType action,
			SharedSpaceResourceType resourceType) {
		Validate.notNull(authUser, "Missing authUser account");
		Validate.notNull(actor, "Missing actor");
		Validate.notEmpty(actor.getLsUuid(), "Missing actor uuid");
		return sharedSpacePermissionBusinessService.hasPermission(roleUuid, action, resourceType);
	}

}
