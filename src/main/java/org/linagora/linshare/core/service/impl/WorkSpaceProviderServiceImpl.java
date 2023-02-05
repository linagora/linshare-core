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

import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.entities.WorkSpaceProvider;
import org.linagora.linshare.core.domain.entities.LdapWorkSpaceProvider;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.WorkSpaceProviderRepository;
import org.linagora.linshare.core.repository.LdapWorkSpaceProviderRepository;
import org.linagora.linshare.core.service.WorkSpaceProviderService;

public class WorkSpaceProviderServiceImpl extends GenericAdminServiceImpl implements WorkSpaceProviderService {

	private WorkSpaceProviderRepository workSpaceProviderRepository;

	private LdapWorkSpaceProviderRepository ldapWorkSpaceProviderRepository;

	protected WorkSpaceProviderServiceImpl(
			LdapWorkSpaceProviderRepository ldapWorkSpaceProviderRepository,
			WorkSpaceProviderRepository workSpaceProviderRepository,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super(sanitizerInputHtmlBusinessService);
		this.ldapWorkSpaceProviderRepository = ldapWorkSpaceProviderRepository;
		this.workSpaceProviderRepository = workSpaceProviderRepository;
	}

	@Override
	public LdapWorkSpaceProvider find(String uuid) throws BusinessException {
		LdapWorkSpaceProvider provider = ldapWorkSpaceProviderRepository.findByUuid(uuid);
		if (provider == null) {
			throw new BusinessException(
					BusinessErrorCode.WORKSPACE_PROVIDER_NOT_FOUND,
					"WorkSpace provider identifier no found.");
		}
		return provider;
	}

	@Override
	public LdapWorkSpaceProvider create(LdapWorkSpaceProvider ldapWorkSpaceProvider) throws BusinessException {
		return ldapWorkSpaceProviderRepository.create(ldapWorkSpaceProvider);
	}

	@Override
	public boolean exists(String uuid) {
		LdapWorkSpaceProvider provider = ldapWorkSpaceProviderRepository.findByUuid(uuid);
		return provider != null;
	}

	@Override
	public LdapWorkSpaceProvider update(LdapWorkSpaceProvider ldapWorkSpaceProvider) throws BusinessException {
		return ldapWorkSpaceProviderRepository.update(ldapWorkSpaceProvider);
	}

	@Override
	public void delete(WorkSpaceProvider workSpaceProvider) throws BusinessException {
		workSpaceProviderRepository.delete(workSpaceProvider);
	}

}
