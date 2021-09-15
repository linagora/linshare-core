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
import org.linagora.linshare.core.domain.constants.DriveFilterType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.LdapDriveFilter;
import org.linagora.linshare.core.facade.webservice.admin.impl.AdminGenericFacadeImpl;
import org.linagora.linshare.core.facade.webservice.adminv5.DomainDriveFilterFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.AbstractDriveFilterDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.LDAPDriveFilterDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.LdapDriveFilterService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class DomainDriveFilterFacadeImpl extends AdminGenericFacadeImpl implements DomainDriveFilterFacade {

	private final Map<DriveFilterType, LdapDriveFilterService> driveLdapFilterServices;

	public DomainDriveFilterFacadeImpl(
			AccountService accountService,
			Map<DriveFilterType, LdapDriveFilterService> driveLdapFilterServices) {
		super(accountService);
		this.driveLdapFilterServices = driveLdapFilterServices;
	}

	private LdapDriveFilterService getService(DriveFilterType type) {
		Validate.notNull(type, " DriveFilterType must be set");
		LdapDriveFilterService driveLdapFilterService = driveLdapFilterServices.get(type);
		Validate.notNull(driveLdapFilterService, "Can not find a service that handle your DriveFilterType: " + type);
		return driveLdapFilterService;
	}

	@Override
	public List<AbstractDriveFilterDto> findAll(boolean model) {
		checkAuthentication(Role.SUPERADMIN);
		LdapDriveFilterService driveLdapFilterService = getService(DriveFilterType.LDAP);
		List<LdapDriveFilter > domainGroupfilters = Lists.newArrayList();
		if (model) {
			domainGroupfilters = driveLdapFilterService.findAllSystemDriveLdapPatterns();
		} else {
			domainGroupfilters = driveLdapFilterService.findAllPublicDrivePatterns();
		}
		return ImmutableList.copyOf(Lists.transform(domainGroupfilters, LDAPDriveFilterDto.toDto()));
	}

	@Override
	public AbstractDriveFilterDto find(String uuid) {
		checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(uuid, "DriveFilter uuid must be set.");
		LdapDriveFilterService driveLdapFilterService = getService(DriveFilterType.LDAP);
		LdapDriveFilter domainGroupfilter = driveLdapFilterService.find(uuid);
		return new LDAPDriveFilterDto(domainGroupfilter);
	}

	@Override
	public AbstractDriveFilterDto create(AbstractDriveFilterDto dto) {
		Account authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(dto, "DriveFilter to create must be set");
		LDAPDriveFilterDto driveFilterDto = (LDAPDriveFilterDto) dto;
		LdapDriveFilterService driveLdapFilterService = driveLdapFilterServices.get(DriveFilterType.LDAP);
		LdapDriveFilter domainGroupFilter = driveLdapFilterService.create(authUser,
				driveFilterDto.toLdapDriveFilterObject());
		return new LDAPDriveFilterDto(domainGroupFilter);
	}

	@Override
	public AbstractDriveFilterDto update(String uuid, AbstractDriveFilterDto dto) {
		Account authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(dto, "DriveFilter to update must be set");
		if (!Strings.isNullOrEmpty(uuid)) {
			dto.setUuid(uuid);
		}
		Validate.notEmpty(dto.getUuid(), "Ldap user filter's uuid must be set");
		LDAPDriveFilterDto driveFilterDto = (LDAPDriveFilterDto) dto;
		LdapDriveFilterService driveLdapFilterService = driveLdapFilterServices.get(DriveFilterType.LDAP);
		LdapDriveFilter domainGroupFilter = driveLdapFilterService.find(dto.getUuid());
		domainGroupFilter = driveLdapFilterService.update(authUser, driveFilterDto.toLdapDriveFilterObject());
		return new LDAPDriveFilterDto(domainGroupFilter);
	}

	@Override
	public AbstractDriveFilterDto delete(String uuid, AbstractDriveFilterDto dto) {
		Account authUser = checkAuthentication(Role.SUPERADMIN);
		if (Strings.isNullOrEmpty(uuid)) {
			Validate.notNull(dto, "DriveFilter to delete must be set");
			Validate.notEmpty(dto.getUuid(), "Ldap user filter's uuid must be set");
			uuid = dto.getUuid();
		}
		Validate.notEmpty(uuid, "Ldap drive filter's uuid must be set");
		LdapDriveFilterService driveLdapFilterService = driveLdapFilterServices.get(DriveFilterType.LDAP);
		LdapDriveFilter domainDriveFilterToDelete = driveLdapFilterService.find(uuid);
		return new LDAPDriveFilterDto(driveLdapFilterService.delete(authUser, domainDriveFilterToDelete));
	}

	@Override
	public List<DomainDto> findAllDomainsByDriveFilter(String uuid) {
		Account authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(uuid, "DriveFilter uuid must be set.");
		LdapDriveFilterService driveLdapFilterService = driveLdapFilterServices.get(DriveFilterType.LDAP);
		LdapDriveFilter domainGroupFilter = driveLdapFilterService.find(uuid);
		List<AbstractDomain> domains = driveLdapFilterService.findAllDomainsByDriveFilter(authUser, domainGroupFilter);
		return ImmutableList.copyOf(Lists.transform(domains, DomainDto.toDto()));
	}
}
