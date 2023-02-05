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
import org.linagora.linshare.core.domain.constants.GroupFilterType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.facade.webservice.admin.impl.AdminGenericFacadeImpl;
import org.linagora.linshare.core.facade.webservice.adminv5.DomainGroupFilterFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.AbstractGroupFilterDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.LDAPWorkGroupFilterDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.GroupLdapPatternService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class DomainGroupFilterFacadeImpl extends AdminGenericFacadeImpl implements DomainGroupFilterFacade {

	private final Map<GroupFilterType, GroupLdapPatternService> groupLdapFilterServices;

	public DomainGroupFilterFacadeImpl(
			AccountService accountService,
			Map<GroupFilterType, GroupLdapPatternService> groupLdapFilterServices) {
		super(accountService);
		this.groupLdapFilterServices = groupLdapFilterServices;
	}

	private GroupLdapPatternService getService(GroupFilterType type) {
		Validate.notNull(type, " GroupFilterType must be set");
		GroupLdapPatternService groupLdapFilterService = groupLdapFilterServices.get(type);
		Validate.notNull(groupLdapFilterService, "Can not find a service that handle your GroupFilterType: " + type);
		return groupLdapFilterService;
	}

	@Override
	public List<AbstractGroupFilterDto> findAll(boolean model) {
		checkAuthentication(Role.SUPERADMIN);
		GroupLdapPatternService groupLdapFilterService = getService(GroupFilterType.LDAP);
		List<GroupLdapPattern> domainGroupfilters = Lists.newArrayList();
		if (model) {
			domainGroupfilters = groupLdapFilterService.findAllSystemGroupLdapPatterns();
		} else {
			domainGroupfilters = groupLdapFilterService.findAllPublicGroupPatterns();
		}
		return ImmutableList.copyOf(Lists.transform(domainGroupfilters, LDAPWorkGroupFilterDto.toDto()));
	}

	@Override
	public AbstractGroupFilterDto find(String uuid) {
		checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(uuid, "GroupFilter uuid must be set.");
		GroupLdapPatternService groupLdapFilterService = getService(GroupFilterType.LDAP);
		GroupLdapPattern domainGroupfilter = groupLdapFilterService.find(uuid);
		return new LDAPWorkGroupFilterDto(domainGroupfilter);
	}

	@Override
	public AbstractGroupFilterDto create(LDAPWorkGroupFilterDto ldapWorkGroupFilterDto) {
		Account authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(ldapWorkGroupFilterDto, "GroupFilter to create must be set");
		GroupLdapPatternService groupLdapFilterService = groupLdapFilterServices.get(GroupFilterType.LDAP);
		GroupLdapPattern domainGroupFilter = groupLdapFilterService.create(authUser,
				ldapWorkGroupFilterDto.toLdapGroupFilterObject());
		return new LDAPWorkGroupFilterDto(domainGroupFilter);
	}

	@Override
	public AbstractGroupFilterDto update(String uuid, LDAPWorkGroupFilterDto ldapWorkGroupFilterDto) {
		Account authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(ldapWorkGroupFilterDto, "GroupFilter to update must be set");
		if (!Strings.isNullOrEmpty(uuid)) {
			ldapWorkGroupFilterDto.setUuid(uuid);
		}
		Validate.notEmpty(ldapWorkGroupFilterDto.getUuid(), "Ldap user filter's uuid must be set");
		GroupLdapPatternService groupLdapFilterService = groupLdapFilterServices.get(GroupFilterType.LDAP);
		GroupLdapPattern domainGroupFilter = groupLdapFilterService.find(ldapWorkGroupFilterDto.getUuid());
		domainGroupFilter = groupLdapFilterService.update(authUser, ldapWorkGroupFilterDto.toLdapGroupFilterObject());
		return new LDAPWorkGroupFilterDto(domainGroupFilter);
	}

	@Override
	public AbstractGroupFilterDto delete(String uuid, LDAPWorkGroupFilterDto ldapWorkGroupFilterDto) {
		Account authUser = checkAuthentication(Role.SUPERADMIN);
		if (Strings.isNullOrEmpty(uuid)) {
			Validate.notNull(ldapWorkGroupFilterDto, "GroupFilter to delete must be set");
			Validate.notEmpty(ldapWorkGroupFilterDto.getUuid(), "Ldap user filter's uuid must be set");
			uuid = ldapWorkGroupFilterDto.getUuid();
		}
		Validate.notEmpty(uuid, "Ldap user filter's uuid must be set");
		GroupLdapPatternService groupLdapFilterService = groupLdapFilterServices.get(GroupFilterType.LDAP);
		GroupLdapPattern domainGroupFilterToDelete = groupLdapFilterService.find(uuid);
		return new LDAPWorkGroupFilterDto(groupLdapFilterService.delete(authUser, domainGroupFilterToDelete));
	}

	@Override
	public List<DomainDto> findAllDomainsByGroupFilter(String uuid) {
		Account authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(uuid, "GroupFilter uuid must be set.");
		GroupLdapPatternService groupLdapFilterService = groupLdapFilterServices.get(GroupFilterType.LDAP);
		GroupLdapPattern domainGroupFilter = groupLdapFilterService.find(uuid);
		List<AbstractDomain> domains = groupLdapFilterService.findAllDomainsByGroupFilter(authUser, domainGroupFilter);
		return ImmutableList.copyOf(Lists.transform(domains, DomainDto.toDto()));
	}
}
