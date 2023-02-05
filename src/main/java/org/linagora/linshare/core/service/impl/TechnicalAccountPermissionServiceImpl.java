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

import org.linagora.linshare.core.business.service.TechnicalAccountPermissionBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.TechnicalAccountPermission;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.TechnicalAccountPermissionService;

public class TechnicalAccountPermissionServiceImpl implements
		TechnicalAccountPermissionService {

	private final TechnicalAccountPermissionBusinessService technicalAccountPermissionBusinessService;

	public TechnicalAccountPermissionServiceImpl(
			final TechnicalAccountPermissionBusinessService technicalAccountPermissionBusinessService) {
		super();
		this.technicalAccountPermissionBusinessService = technicalAccountPermissionBusinessService;
	}

	@Override
	public TechnicalAccountPermission create(Account actor,
			TechnicalAccountPermission permission) throws BusinessException {
		return technicalAccountPermissionBusinessService.create(permission);
	}

	@Override
	public void delete(Account actor, TechnicalAccountPermission permission)
			throws BusinessException {
		technicalAccountPermissionBusinessService.delete(permission);
	}

	@Override
	public TechnicalAccountPermission find(Account actor, String uuid)
			throws BusinessException {
		return technicalAccountPermissionBusinessService.find(uuid);
	}

	@Override
	public TechnicalAccountPermission update(Account actor,
			TechnicalAccountPermission permissionDto) throws BusinessException {
		TechnicalAccountPermission permission = technicalAccountPermissionBusinessService
				.find(permissionDto.getUuid());
		permission.getAccountPermissions().clear();
		permission.getAccountPermissions().addAll(permissionDto.getAccountPermissions());
		return technicalAccountPermissionBusinessService.update(permission);
	}
}
