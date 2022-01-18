/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.facade.webservice.adminv5.impl;

import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.WorkSpaceProviderType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.WorkSpaceProvider;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.LdapDriveFilter;
import org.linagora.linshare.core.domain.entities.LdapWorkSpaceProvider;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.impl.AdminGenericFacadeImpl;
import org.linagora.linshare.core.facade.webservice.adminv5.DriveProviderFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.AbstractDriveProviderDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.LDAPDriveProviderDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DomainService;
import org.linagora.linshare.core.service.WorkSpaceProviderService;
import org.linagora.linshare.core.service.LdapWorkSpaceFilterService;
import org.linagora.linshare.core.service.impl.LdapConnectionServiceImpl;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

public class DriveProviderFacadeImpl extends AdminGenericFacadeImpl implements DriveProviderFacade {

	private DomainService domainService;

	private WorkSpaceProviderService driveProviderService;

	private LdapConnectionServiceImpl ldapConnectionService;

	private LdapWorkSpaceFilterService driveLdapFilterService;

	public DriveProviderFacadeImpl(
			AccountService accountService,
			DomainService domainService,
			WorkSpaceProviderService driveProviderService,
			LdapConnectionServiceImpl ldapConnectionService,
			LdapWorkSpaceFilterService driveLdapFilterService) {
		super(accountService);
		this.domainService = domainService;
		this.driveProviderService = driveProviderService;
		this.ldapConnectionService = ldapConnectionService;
		this.driveLdapFilterService = driveLdapFilterService;
	}

	@Override
	public Set<AbstractDriveProviderDto> findAll(String domainUuid) {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		AbstractDomain domain = domainService.find(authUser, domainUuid);
		Set<AbstractDriveProviderDto> res = Sets.newHashSet();
		WorkSpaceProvider driveProvider = domain.getDriveProvider();
		if (driveProvider != null) {
			if (WorkSpaceProviderType.LDAP_PROVIDER.equals(driveProvider.getType())) {
				res.add(new LDAPDriveProviderDto((LdapWorkSpaceProvider) driveProvider));
			} else {
				throw new BusinessException(BusinessErrorCode.DRIVE_PROVIDER_UNSUPPORTED_TYPE,
						"DriveProvider not supported yet");
			}
		}
		return res;
	}

	@Override
	public AbstractDriveProviderDto find(String domainUuid, String uuid) {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		AbstractDomain domain = domainService.find(authUser, domainUuid);
		WorkSpaceProvider driveProvider = driveProviderService.find(uuid);
		if (isDomainBelonging(domain, driveProvider)) {
			return new LDAPDriveProviderDto((LdapWorkSpaceProvider) driveProvider);
		} else {
			throw new BusinessException(BusinessErrorCode.DRIVE_PROVIDER_NOT_FOUND,
					"The requested drive provider does not belong to the entered domain, please check the entered information.");
		}
	}

	private Boolean isDomainBelonging(AbstractDomain domain, WorkSpaceProvider driveProvider) {
		return domain.getDriveProvider().getUuid().equals(driveProvider.getUuid()) ? true : false;
	}

	@Override
	public AbstractDriveProviderDto create(String domainUuid, AbstractDriveProviderDto dto) {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(dto, "drive provider must be set.");
		Validate.notNull(dto.getType(), "drive provider must be set.");
		AbstractDomain domain = domainService.find(authUser, domainUuid);
		if (domain.isRootDomain() || domain.isGuestDomain()) {
			throw new BusinessException(BusinessErrorCode.DRIVE_PROVIDER_FORBIDDEN, "You can not manage a DriveProvider for this kind of domain.");
		}
		if (domain.getDriveProvider() != null) {
			throw new BusinessException(BusinessErrorCode.DRIVE_PROVIDER_ALREADY_EXIST, "DriveProvider already exists. Can't create more than one");
		}
		if (dto.getType().equals(WorkSpaceProviderType.LDAP_PROVIDER)) {
			return createLdapDriveProvider(authUser, (LDAPDriveProviderDto) dto, domain);
		} else {
			throw new BusinessException(BusinessErrorCode.DRIVE_PROVIDER_NOT_FOUND, "DriveProvider not found");
		}
	}

	private AbstractDriveProviderDto createLdapDriveProvider(User authUser, LDAPDriveProviderDto dto, AbstractDomain domain) {
		Validate.notNull(dto.getDriveFilter(), "driveFilter payload is mandatory for drive provider creation");
		String driveFilterUuid = dto.getDriveFilter().getUuid();
		Validate.notEmpty(driveFilterUuid, "driveFilter uuid is mandatory for drive provider creation");
		// ldap server
		Validate.notNull(dto.getLdapServer(), "LDAP Connection payload is mandatory for drive provider creation");
		String ldapConnectionUuid = dto.getLdapServer().getUuid();
		Validate.notEmpty(ldapConnectionUuid, "LDAP connection uuid is mandatory for drive provider creation");
		// baseDn
		String baseDn = dto.getBaseDn();
		Validate.notEmpty(baseDn, "baseDn is mandatory for drive provider creation");
		LdapDriveFilter driveLdapPattern = driveLdapFilterService.find(driveFilterUuid);
		LdapConnection connection = ldapConnectionService.find(ldapConnectionUuid);
		LdapWorkSpaceProvider driveProvider = (LdapWorkSpaceProvider) driveProviderService
				.create(new LdapWorkSpaceProvider(
						domain,
						driveLdapPattern,
						baseDn,
						connection,
						dto.getSearchInOtherDomains()));
		domain.setDriveProvider(driveProvider);
		return new LDAPDriveProviderDto(driveProvider);
	}

	@Override
	public AbstractDriveProviderDto update(String domainUuid, String uuid, AbstractDriveProviderDto dto) {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(dto, "drive provider must be set.");
		Validate.notNull(dto.getType(), "drive provider must be set.");
		AbstractDomain domain = domainService.find(authUser, domainUuid);
		if (Strings.isNullOrEmpty(uuid)) {
			Validate.notNull(dto, "drive provider must be set.");
			uuid = dto.getUuid();
			Validate.notEmpty(uuid, "Missing drive provider uuid in the payload.");
		}
		WorkSpaceProvider driveProvider = driveProviderService.find(uuid);
		if (isDomainBelonging(domain, driveProvider)) {
			if (dto.getType().equals(WorkSpaceProviderType.LDAP_PROVIDER)) {
				return updateLdapDriveProvider((LDAPDriveProviderDto) dto, domain, driveProvider);
			} else {
				throw new BusinessException(BusinessErrorCode.DRIVE_PROVIDER_NOT_FOUND, "DriveProvider not found");
			}
		} else {
			throw new BusinessException(BusinessErrorCode.DRIVE_PROVIDER_NOT_FOUND,
					"The requested drive provider does not belong to the entered domain, please check the entered information.");
		}
	}

	private LDAPDriveProviderDto updateLdapDriveProvider(LDAPDriveProviderDto driveProviderDto, AbstractDomain domain,
			WorkSpaceProvider driveProvider) {
		// drive filter
		Validate.notNull(driveProviderDto.getDriveFilter(), "driveFilter payload is mandatory for drive provider update");
		String driveFilterUuid = driveProviderDto.getDriveFilter().getUuid();
		Validate.notEmpty(driveFilterUuid, "driveFilter uuid is mandatory for drive provider update");
		// ldap connection
		Validate.notNull(driveProviderDto.getLdapServer(), "LDAP Connection payload is mandatory for drive provider update");
		String ldapConnectionUuid = driveProviderDto.getLdapServer().getUuid();
		Validate.notEmpty(ldapConnectionUuid, "LDAP connection uuid is mandatory for drive provider update");
		// baseDn
		String baseDn = driveProviderDto.getBaseDn();
		Validate.notEmpty(baseDn, "baseDn is mandatory for drive provider update");
		LdapWorkSpaceProvider provider = (LdapWorkSpaceProvider) driveProvider;
		provider.setBaseDn(baseDn);
		LdapDriveFilter driveLdapFilter = driveLdapFilterService.find(driveFilterUuid);
		LdapConnection connection = ldapConnectionService.find(ldapConnectionUuid);
		provider.setLdapConnection(connection);
		provider.setDriveFilter(driveLdapFilter);
		return new LDAPDriveProviderDto((LdapWorkSpaceProvider) driveProviderService.update(provider));
	}

	@Override
	public AbstractDriveProviderDto delete(String domainUuid, String uuid, AbstractDriveProviderDto dto) {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		AbstractDomain domain = domainService.find(authUser, domainUuid);
		if (domain.isRootDomain() || domain.isGuestDomain()) {
			throw new BusinessException(BusinessErrorCode.DRIVE_PROVIDER_FORBIDDEN, "You can not manage DriveProvider for this kind of domain.");
		}
		if (Strings.isNullOrEmpty(uuid)) {
			Validate.notNull(dto, "drive provider must be set.");
			uuid = dto.getUuid();
			Validate.notEmpty(uuid, "Missing drive provider uuid in the payload.");
		}
		WorkSpaceProvider driveProvider = driveProviderService.find(uuid);
		if (isDomainBelonging(domain, driveProvider)) {
			driveProviderService.delete(driveProvider);
			domain.setDriveProvider(null);
			return new LDAPDriveProviderDto((LdapWorkSpaceProvider) driveProvider);
		} else {
			throw new BusinessException(BusinessErrorCode.DRIVE_PROVIDER_NOT_FOUND,
					"The requested drive provider does not belong to the entered domain, please check the entered information.");
		}
	}
}
