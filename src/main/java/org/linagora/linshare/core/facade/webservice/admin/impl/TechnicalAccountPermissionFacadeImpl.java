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
package org.linagora.linshare.core.facade.webservice.admin.impl;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.TechnicalAccountPermission;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.TechnicalAccountPermissionFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.TechnicalAccountPermissionDto;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.TechnicalAccountPermissionService;

public class TechnicalAccountPermissionFacadeImpl extends AdminGenericFacadeImpl
		implements TechnicalAccountPermissionFacade {

	private final TechnicalAccountPermissionService technicalAccountPermissionService;
	
	private final AbstractDomainService domainService;

	public TechnicalAccountPermissionFacadeImpl(final AccountService accountService,
			final TechnicalAccountPermissionService technicalAccountPermissionService,
			final AbstractDomainService domainService) {
		super(accountService);
		this.technicalAccountPermissionService = technicalAccountPermissionService;
		this.domainService = domainService;
	}

	@Override
	public TechnicalAccountPermissionDto find(String uuid) throws BusinessException {
		User authUser = checkAuth();
		Validate.notEmpty(uuid, "uuid must be set.");
		TechnicalAccountPermission permission = technicalAccountPermissionService.find(authUser, uuid);
		technicalAccountPermissionService.delete(authUser, permission);
		return new TechnicalAccountPermissionDto(permission);
	}

	@Override
	public TechnicalAccountPermissionDto update(TechnicalAccountPermissionDto dto)
			throws BusinessException {
		User authUser = checkAuth();
		Validate.notEmpty(dto.getUuid(), "uuid must be set.");
		TechnicalAccountPermission tap = new TechnicalAccountPermission(dto);
		for (String domain: dto.getDomains()) {
			if (domain != null)
				tap.addDomain(domainService.findById(domain));
		}
		TechnicalAccountPermission permission = technicalAccountPermissionService.update(authUser, tap);
		return new TechnicalAccountPermissionDto(permission);
	}

	/**
	 * Helpers
	 */
	
	private User checkAuth() throws BusinessException {
		return checkAuthentication(Role.SUPERADMIN);
	}
}
