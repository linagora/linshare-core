/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2022. Contribute to
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
import org.linagora.linshare.core.domain.constants.WorkSpaceFilterType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.LdapWorkSpaceFilter;
import org.linagora.linshare.core.facade.webservice.admin.impl.AdminGenericFacadeImpl;
import org.linagora.linshare.core.facade.webservice.adminv5.DomainWorkSpaceFilterFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.AbstractWorkSpaceFilterDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.LDAPWorkSpaceFilterDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.LdapWorkSpaceFilterService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class DomainWorkSpaceFilterFacadeImpl extends AdminGenericFacadeImpl implements DomainWorkSpaceFilterFacade {

	private final Map<WorkSpaceFilterType, LdapWorkSpaceFilterService> workSpaceLdapFilterServices;

	public DomainWorkSpaceFilterFacadeImpl(
			AccountService accountService,
			Map<WorkSpaceFilterType, LdapWorkSpaceFilterService> workSpaceLdapFilterServices) {
		super(accountService);
		this.workSpaceLdapFilterServices = workSpaceLdapFilterServices;
	}

	private LdapWorkSpaceFilterService getService(WorkSpaceFilterType type) {
		Validate.notNull(type, " WorkSpaceFilterType must be set");
		LdapWorkSpaceFilterService workSpaceLdapFilterService = workSpaceLdapFilterServices.get(type);
		Validate.notNull(workSpaceLdapFilterService, "Can not find a service that handle your WorkSpaceFilterType: " + type);
		return workSpaceLdapFilterService;
	}

	@Override
	public List<AbstractWorkSpaceFilterDto> findAll(boolean model) {
		checkAuthentication(Role.SUPERADMIN);
		LdapWorkSpaceFilterService workSpaceLdapFilterService = getService(WorkSpaceFilterType.LDAP);
		List<LdapWorkSpaceFilter > domainGroupfilters = Lists.newArrayList();
		if (model) {
			domainGroupfilters = workSpaceLdapFilterService.findAllSystemWorkSpaceLdapFilters();
		} else {
			domainGroupfilters = workSpaceLdapFilterService.findAllPublicWorkSpaceFilters();
		}
		return ImmutableList.copyOf(Lists.transform(domainGroupfilters, LDAPWorkSpaceFilterDto.toDto()));
	}

	@Override
	public AbstractWorkSpaceFilterDto find(String uuid) {
		checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(uuid, "WorkSpaceFilter uuid must be set.");
		LdapWorkSpaceFilterService workSpaceLdapFilterService = getService(WorkSpaceFilterType.LDAP);
		LdapWorkSpaceFilter domainGroupfilter = workSpaceLdapFilterService.find(uuid);
		return new LDAPWorkSpaceFilterDto(domainGroupfilter);
	}

	@Override
	public AbstractWorkSpaceFilterDto create(AbstractWorkSpaceFilterDto dto) {
		Account authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(dto, "WorkSpaceFilter to create must be set");
		LDAPWorkSpaceFilterDto workSpaceFilterDto = (LDAPWorkSpaceFilterDto) dto;
		LdapWorkSpaceFilterService workSpaceLdapFilterService = workSpaceLdapFilterServices.get(WorkSpaceFilterType.LDAP);
		LdapWorkSpaceFilter domainGroupFilter = workSpaceLdapFilterService.create(authUser,
				workSpaceFilterDto.toLdapWorkSpaceFilterObject());
		return new LDAPWorkSpaceFilterDto(domainGroupFilter);
	}

	@Override
	public AbstractWorkSpaceFilterDto update(String uuid, AbstractWorkSpaceFilterDto dto) {
		Account authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(dto, "WorkSpaceFilter to update must be set");
		if (!Strings.isNullOrEmpty(uuid)) {
			dto.setUuid(uuid);
		}
		Validate.notEmpty(dto.getUuid(), "Ldap user filter's uuid must be set");
		LDAPWorkSpaceFilterDto workSpaceFilterDto = (LDAPWorkSpaceFilterDto) dto;
		LdapWorkSpaceFilterService workSpaceLdapFilterService = workSpaceLdapFilterServices.get(WorkSpaceFilterType.LDAP);
		LdapWorkSpaceFilter domainGroupFilter = workSpaceLdapFilterService.find(dto.getUuid());
		domainGroupFilter = workSpaceLdapFilterService.update(authUser, workSpaceFilterDto.toLdapWorkSpaceFilterObject());
		return new LDAPWorkSpaceFilterDto(domainGroupFilter);
	}

	@Override
	public AbstractWorkSpaceFilterDto delete(String uuid, AbstractWorkSpaceFilterDto dto) {
		Account authUser = checkAuthentication(Role.SUPERADMIN);
		if (Strings.isNullOrEmpty(uuid)) {
			Validate.notNull(dto, "WorkSpaceFilter to delete must be set");
			Validate.notEmpty(dto.getUuid(), "Ldap user filter's uuid must be set");
			uuid = dto.getUuid();
		}
		Validate.notEmpty(uuid, "Ldap WorkSpace filter's uuid must be set");
		LdapWorkSpaceFilterService workSpaceLdapFilterService = workSpaceLdapFilterServices.get(WorkSpaceFilterType.LDAP);
		LdapWorkSpaceFilter domainWorkSpaceFilterToDelete = workSpaceLdapFilterService.find(uuid);
		return new LDAPWorkSpaceFilterDto(workSpaceLdapFilterService.delete(authUser, domainWorkSpaceFilterToDelete));
	}

	@Override
	public List<DomainDto> findAllDomainsByWorkSpaceFilter(String uuid) {
		Account authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(uuid, "WorkSpaceFilter uuid must be set.");
		LdapWorkSpaceFilterService workSpaceLdapFilterService = workSpaceLdapFilterServices.get(WorkSpaceFilterType.LDAP);
		LdapWorkSpaceFilter domainGroupFilter = workSpaceLdapFilterService.find(uuid);
		List<AbstractDomain> domains = workSpaceLdapFilterService.findAllDomainsByWorkSpaceFilter(authUser, domainGroupFilter);
		return ImmutableList.copyOf(Lists.transform(domains, DomainDto.toDto()));
	}
}
