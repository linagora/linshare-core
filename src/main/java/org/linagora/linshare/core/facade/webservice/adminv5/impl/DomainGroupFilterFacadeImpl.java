/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2021. Contribute to
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
