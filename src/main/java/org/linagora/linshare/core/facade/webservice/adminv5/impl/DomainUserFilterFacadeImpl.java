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
package org.linagora.linshare.core.facade.webservice.adminv5.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.constants.UserFilterType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UserLdapPattern;
import org.linagora.linshare.core.facade.webservice.admin.impl.AdminGenericFacadeImpl;
import org.linagora.linshare.core.facade.webservice.adminv5.DomainUserFilterFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.AbstractUserFilterDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.LDAPUserFilterDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.UserProviderService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class DomainUserFilterFacadeImpl extends AdminGenericFacadeImpl implements DomainUserFilterFacade {

	private final Map<UserFilterType, UserProviderService> userProviderServices;

	public DomainUserFilterFacadeImpl(
			AccountService accountService,
			Map<UserFilterType, UserProviderService> userProviderServices) {
		super(accountService);
		this.userProviderServices = userProviderServices;
	}

	private UserProviderService getService(UserFilterType type) {
		Validate.notNull(type, " UserFilterType must be set");
		UserProviderService userProviderService = userProviderServices.get(type);
		Validate.notNull(userProviderService, "Can not find a service that handle your UserFilterType: " + type);
		return userProviderService;
	}

	@Override
	public List<AbstractUserFilterDto> findAll(boolean model) {
		checkAuthentication(Role.SUPERADMIN);
		UserProviderService userProviderService = getService(UserFilterType.LDAP);
		List<UserLdapPattern> domainUserfilters = Lists.newArrayList();
		if (model) {
			domainUserfilters = userProviderService.findAllSystemDomainPattern();
		} else {
			domainUserfilters = userProviderService.findAllUserDomainPattern();
		}
		return ImmutableList.copyOf(Lists.transform(domainUserfilters, LDAPUserFilterDto.toDto()));
	}

	@Override
	public AbstractUserFilterDto find(String uuid) {
		checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(uuid, "UserFilter uuid must be set.");
		UserProviderService userProviderService = getService(UserFilterType.LDAP);
		UserLdapPattern domainUserFilter = userProviderService.findDomainPattern(uuid);
		return new LDAPUserFilterDto(domainUserFilter);
	}

	@Override
	public AbstractUserFilterDto create(LDAPUserFilterDto ldapUserFilterDto) {
		Account authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(ldapUserFilterDto, "Ldap user filter to create must be set");
		UserProviderService userProviderService = userProviderServices.get(UserFilterType.LDAP);
		return new LDAPUserFilterDto(userProviderService.createDomainPattern(authUser, ldapUserFilterDto.toLdapUserFilterObject()));
	}

	@Override
	public AbstractUserFilterDto update(String uuid, LDAPUserFilterDto ldapUserFilterDto) {
		Account authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(ldapUserFilterDto, "Ldap user filter to update must be set");
		if (!Strings.isNullOrEmpty(uuid)) {
			ldapUserFilterDto.setUuid(uuid);
		}
		Validate.notEmpty(ldapUserFilterDto.getUuid(), "Ldap user filter's uuid must be set");
		UserProviderService userProviderService = userProviderServices.get(UserFilterType.LDAP);
		UserLdapPattern domainUserFilter = userProviderService.findDomainPattern(ldapUserFilterDto.getUuid());
		domainUserFilter = userProviderService.updateDomainPattern(authUser,
				ldapUserFilterDto.toLdapUserFilterObject());
		return new LDAPUserFilterDto(domainUserFilter);
	}

	@Override
	public AbstractUserFilterDto delete(String uuid, LDAPUserFilterDto ldapUserFilterDto) {
		Account authUser = checkAuthentication(Role.SUPERADMIN);
		if (Strings.isNullOrEmpty(uuid)) {
			Validate.notNull(ldapUserFilterDto, "Ldap user filter to delete must be set");
			Validate.notEmpty(ldapUserFilterDto.getUuid(), "Ldap user filter's uuid must be set");
			uuid = ldapUserFilterDto.getUuid();
		}
		Validate.notEmpty(uuid, "Ldap user filter's uuid must be set");
		UserProviderService userProviderService = userProviderServices.get(UserFilterType.LDAP);
		return new LDAPUserFilterDto(userProviderService.deletePattern(authUser, uuid));
	}

	@Override
	public List<DomainDto> findAllDomainsByUserFilter(String uuid) {
		Account authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(uuid, "UserFilter uuid must be set.");
		UserProviderService userProviderService = userProviderServices.get(UserFilterType.LDAP);
		UserLdapPattern domainUserFilter = userProviderService.findDomainPattern(uuid);
		List<AbstractDomain> domains = userProviderService.findAllDomainsByUserFilter(authUser, domainUserFilter);
		return ImmutableList.copyOf(Lists.transform(domains, DomainDto.toDto()));
	}
}
