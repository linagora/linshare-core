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
import org.linagora.linshare.core.domain.entities.GroupProvider;
import org.linagora.linshare.core.domain.entities.LdapGroupProvider;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.GroupProviderRepository;
import org.linagora.linshare.core.repository.LdapGroupProviderRepository;
import org.linagora.linshare.core.service.GroupProviderService;

public class GroupProviderServiceImpl extends GenericAdminServiceImpl implements GroupProviderService {

	private GroupProviderRepository groupProviderRepository;

	private LdapGroupProviderRepository ldapGroupProviderRepository;

	public GroupProviderServiceImpl(GroupProviderRepository groupProviderRepository,
			LdapGroupProviderRepository ldapGroupProviderRepository,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super(sanitizerInputHtmlBusinessService);
		this.groupProviderRepository = groupProviderRepository;
		this.ldapGroupProviderRepository = ldapGroupProviderRepository;
	}

	@Override
	public LdapGroupProvider find(String uuid) throws BusinessException {
		LdapGroupProvider provider = ldapGroupProviderRepository.findByUuid(uuid);
		if (provider == null) {
			throw new BusinessException(
					BusinessErrorCode.GROUP_PROVIDER_NOT_FOUND,
					"Group provider identifier no found.");
		}
		return provider;
	}

	@Override
	public LdapGroupProvider create(LdapGroupProvider ldapGroupProvider) throws BusinessException {
			return ldapGroupProviderRepository.create(ldapGroupProvider);
	}

	@Override
	public LdapGroupProvider update(LdapGroupProvider groupProvider) throws BusinessException {
		return ldapGroupProviderRepository.update(groupProvider);
	}

	@Override
	public void delete(GroupProvider groupProvider) throws BusinessException {
		groupProviderRepository.delete(groupProvider);
	}

	@Override
	public boolean exists(String uuid) {
		LdapGroupProvider provider = ldapGroupProviderRepository.findByUuid(uuid);
		return provider != null;
	}
}
