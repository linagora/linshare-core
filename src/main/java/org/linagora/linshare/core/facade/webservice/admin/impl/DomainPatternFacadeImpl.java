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

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserLdapPattern;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.DomainPatternFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.DomainPatternDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.UserProviderService;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class DomainPatternFacadeImpl extends AdminGenericFacadeImpl implements DomainPatternFacade {

	private final UserProviderService userProviderService;

	protected Function<UserLdapPattern, DomainPatternDto> convertUserPattern = new Function<UserLdapPattern, DomainPatternDto>() {
		@Override
		public DomainPatternDto apply(UserLdapPattern userPattern) {
			return new DomainPatternDto(userPattern);
		}
	};

	public DomainPatternFacadeImpl(final AccountService accountService, final UserProviderService userProviderService) {
		super(accountService);
		this.userProviderService = userProviderService;
	}

	@Override
	public List<DomainPatternDto> findAll() throws BusinessException {
		checkAuthentication(Role.SUPERADMIN);
		List<UserLdapPattern> domainPatterns = userProviderService.findAllUserDomainPattern();
		return ImmutableList.copyOf(Lists.transform(domainPatterns, convertUserPattern));
	}

	@Override
	public DomainPatternDto find(String uuid) throws BusinessException {
		checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(uuid, "domain pattern uuid must be set.");
		return new DomainPatternDto(userProviderService.findDomainPattern(uuid));
	}

	@Override
	public List<DomainPatternDto> findAllModels() throws BusinessException {
		checkAuthentication(Role.SUPERADMIN);
		List<UserLdapPattern> domainPatterns = userProviderService.findAllDomainPattern();
		return ImmutableList.copyOf(Lists.transform(domainPatterns, convertUserPattern));
	}

	@Override
	public DomainPatternDto update(DomainPatternDto domainPatternDto) throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(domainPatternDto.getUuid(), "domain pattern uuid must be set.");
		return new DomainPatternDto(
				userProviderService.updateDomainPattern(authUser, new UserLdapPattern(domainPatternDto)));
	}

	@Override
	public DomainPatternDto create(DomainPatternDto domainPatternDto) throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(domainPatternDto.getLabel(), "domain pattern label must be set.");
		return new DomainPatternDto(
				userProviderService.createDomainPattern(authUser, new UserLdapPattern(domainPatternDto)));
	}

	@Override
	public DomainPatternDto delete(DomainPatternDto domainPatternDto) throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(domainPatternDto.getUuid(), "domain pattern uuid must be set.");
		UserLdapPattern pattern = userProviderService.deletePattern(authUser, domainPatternDto.getUuid());
		return new DomainPatternDto(pattern);
	}

}
