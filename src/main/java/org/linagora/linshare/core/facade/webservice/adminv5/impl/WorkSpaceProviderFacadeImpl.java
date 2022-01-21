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
import org.linagora.linshare.core.domain.entities.LdapWorkSpaceFilter;
import org.linagora.linshare.core.domain.entities.LdapWorkSpaceProvider;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.impl.AdminGenericFacadeImpl;
import org.linagora.linshare.core.facade.webservice.adminv5.WorkSpaceProviderFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.AbstractWorkSpaceProviderDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.LDAPWorkSpaceProviderDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DomainService;
import org.linagora.linshare.core.service.WorkSpaceProviderService;
import org.linagora.linshare.core.service.LdapWorkSpaceFilterService;
import org.linagora.linshare.core.service.impl.LdapConnectionServiceImpl;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

public class WorkSpaceProviderFacadeImpl extends AdminGenericFacadeImpl implements WorkSpaceProviderFacade {

	private DomainService domainService;

	private WorkSpaceProviderService workSpaceProviderService;

	private LdapConnectionServiceImpl ldapConnectionService;

	private LdapWorkSpaceFilterService workSpaceLdapFilterService;

	public WorkSpaceProviderFacadeImpl(
			AccountService accountService,
			DomainService domainService,
			WorkSpaceProviderService workSpaceProviderService,
			LdapConnectionServiceImpl ldapConnectionService,
			LdapWorkSpaceFilterService workSpaceLdapFilterService) {
		super(accountService);
		this.domainService = domainService;
		this.workSpaceProviderService = workSpaceProviderService;
		this.ldapConnectionService = ldapConnectionService;
		this.workSpaceLdapFilterService = workSpaceLdapFilterService;
	}

	@Override
	public Set<AbstractWorkSpaceProviderDto> findAll(String domainUuid) {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		AbstractDomain domain = domainService.find(authUser, domainUuid);
		Set<AbstractWorkSpaceProviderDto> res = Sets.newHashSet();
		WorkSpaceProvider workSpaceProvider = domain.getWorkSpaceProvider();
		if (workSpaceProvider != null) {
			if (WorkSpaceProviderType.LDAP_PROVIDER.equals(workSpaceProvider.getType())) {
				res.add(new LDAPWorkSpaceProviderDto((LdapWorkSpaceProvider) workSpaceProvider));
			} else {
				throw new BusinessException(BusinessErrorCode.WORKSPACE_PROVIDER_UNSUPPORTED_TYPE,
						"WorkSpaceProvider not supported yet");
			}
		}
		return res;
	}

	@Override
	public AbstractWorkSpaceProviderDto find(String domainUuid, String uuid) {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		AbstractDomain domain = domainService.find(authUser, domainUuid);
		WorkSpaceProvider workSpaceProvider = workSpaceProviderService.find(uuid);
		if (isDomainBelonging(domain, workSpaceProvider)) {
			return new LDAPWorkSpaceProviderDto((LdapWorkSpaceProvider) workSpaceProvider);
		} else {
			throw new BusinessException(BusinessErrorCode.WORKSPACE_PROVIDER_NOT_FOUND,
					"The requested WorkSpace provider does not belong to the entered domain, please check the entered information.");
		}
	}

	private Boolean isDomainBelonging(AbstractDomain domain, WorkSpaceProvider workSpaceProvider) {
		return domain.getWorkSpaceProvider().getUuid().equals(workSpaceProvider.getUuid()) ? true : false;
	}

	@Override
	public AbstractWorkSpaceProviderDto create(String domainUuid, AbstractWorkSpaceProviderDto dto) {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(dto, "WorkSpace provider must be set.");
		Validate.notNull(dto.getType(), "WorkSpace provider must be set.");
		AbstractDomain domain = domainService.find(authUser, domainUuid);
		if (domain.isRootDomain() || domain.isGuestDomain()) {
			throw new BusinessException(BusinessErrorCode.WORKSPACE_PROVIDER_FORBIDDEN, "You can not manage a WorkSpaceProvider for this kind of domain.");
		}
		if (domain.getWorkSpaceProvider() != null) {
			throw new BusinessException(BusinessErrorCode.WORKSPACE_PROVIDER_ALREADY_EXIST, "WorkSpaceProvider already exists. Can't create more than one");
		}
		if (dto.getType().equals(WorkSpaceProviderType.LDAP_PROVIDER)) {
			return createLdapWorkSpaceProvider(authUser, (LDAPWorkSpaceProviderDto) dto, domain);
		} else {
			throw new BusinessException(BusinessErrorCode.WORKSPACE_PROVIDER_NOT_FOUND, "WorkSpaceProvider not found");
		}
	}

	private AbstractWorkSpaceProviderDto createLdapWorkSpaceProvider(User authUser, LDAPWorkSpaceProviderDto dto, AbstractDomain domain) {
		Validate.notNull(dto.getWorkSpaceFilter(), "WorkSpaceFilter payload is mandatory for WorkSpace provider creation");
		String workSpaceFilterUuid = dto.getWorkSpaceFilter().getUuid();
		Validate.notEmpty(workSpaceFilterUuid, "WorkSpaceFilter uuid is mandatory for WorkSpace provider creation");
		// ldap server
		Validate.notNull(dto.getLdapServer(), "LDAP Connection payload is mandatory for WorkSpace provider creation");
		String ldapConnectionUuid = dto.getLdapServer().getUuid();
		Validate.notEmpty(ldapConnectionUuid, "LDAP connection uuid is mandatory for WorkSpace provider creation");
		// baseDn
		String baseDn = dto.getBaseDn();
		Validate.notEmpty(baseDn, "baseDn is mandatory for WorkSpace provider creation");
		LdapWorkSpaceFilter workSpaceLdapPattern = workSpaceLdapFilterService.find(workSpaceFilterUuid);
		LdapConnection connection = ldapConnectionService.find(ldapConnectionUuid);
		LdapWorkSpaceProvider workSpaceProvider = (LdapWorkSpaceProvider) workSpaceProviderService
				.create(new LdapWorkSpaceProvider(
						domain,
						workSpaceLdapPattern,
						baseDn,
						connection,
						dto.getSearchInOtherDomains()));
		domain.setWorkSpaceProvider(workSpaceProvider);
		return new LDAPWorkSpaceProviderDto(workSpaceProvider);
	}

	@Override
	public AbstractWorkSpaceProviderDto update(String domainUuid, String uuid, AbstractWorkSpaceProviderDto dto) {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(dto, "WorkSpace provider must be set.");
		Validate.notNull(dto.getType(), "WorkSpace provider must be set.");
		AbstractDomain domain = domainService.find(authUser, domainUuid);
		if (Strings.isNullOrEmpty(uuid)) {
			Validate.notNull(dto, "WorkSpace provider must be set.");
			uuid = dto.getUuid();
			Validate.notEmpty(uuid, "Missing WorkSpace provider uuid in the payload.");
		}
		WorkSpaceProvider workSpaceProvider = workSpaceProviderService.find(uuid);
		if (isDomainBelonging(domain, workSpaceProvider)) {
			if (dto.getType().equals(WorkSpaceProviderType.LDAP_PROVIDER)) {
				return updateLdapWorkSpaceProvider((LDAPWorkSpaceProviderDto) dto, domain, workSpaceProvider);
			} else {
				throw new BusinessException(BusinessErrorCode.WORKSPACE_PROVIDER_NOT_FOUND, "WorkSpaceProvider not found");
			}
		} else {
			throw new BusinessException(BusinessErrorCode.WORKSPACE_PROVIDER_NOT_FOUND,
					"The requested WorkSpace provider does not belong to the entered domain, please check the entered information.");
		}
	}

	private LDAPWorkSpaceProviderDto updateLdapWorkSpaceProvider(LDAPWorkSpaceProviderDto workSpaceProviderDto, AbstractDomain domain,
			WorkSpaceProvider workSpaceProvider) {
		// WorkSpace filter
		Validate.notNull(workSpaceProviderDto.getWorkSpaceFilter(), "WorkSpaceFilter payload is mandatory for WorkSpace provider update");
		String workSpaceFilterUuid = workSpaceProviderDto.getWorkSpaceFilter().getUuid();
		Validate.notEmpty(workSpaceFilterUuid, "WorkSpaceFilter uuid is mandatory for WorkSpace provider update");
		// ldap connection
		Validate.notNull(workSpaceProviderDto.getLdapServer(), "LDAP Connection payload is mandatory for WorkSpace provider update");
		String ldapConnectionUuid = workSpaceProviderDto.getLdapServer().getUuid();
		Validate.notEmpty(ldapConnectionUuid, "LDAP connection uuid is mandatory for WorkSpace provider update");
		// baseDn
		String baseDn = workSpaceProviderDto.getBaseDn();
		Validate.notEmpty(baseDn, "baseDn is mandatory for WorkSpace provider update");
		LdapWorkSpaceProvider provider = (LdapWorkSpaceProvider) workSpaceProvider;
		provider.setBaseDn(baseDn);
		LdapWorkSpaceFilter workSpaceLdapFilter = workSpaceLdapFilterService.find(workSpaceFilterUuid);
		LdapConnection connection = ldapConnectionService.find(ldapConnectionUuid);
		provider.setLdapConnection(connection);
		provider.setWorkSpaceFilter(workSpaceLdapFilter);
		return new LDAPWorkSpaceProviderDto((LdapWorkSpaceProvider) workSpaceProviderService.update(provider));
	}

	@Override
	public AbstractWorkSpaceProviderDto delete(String domainUuid, String uuid, AbstractWorkSpaceProviderDto dto) {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		AbstractDomain domain = domainService.find(authUser, domainUuid);
		if (domain.isRootDomain() || domain.isGuestDomain()) {
			throw new BusinessException(BusinessErrorCode.WORKSPACE_PROVIDER_FORBIDDEN, "You can not manage WorkSpaceProvider for this kind of domain.");
		}
		if (Strings.isNullOrEmpty(uuid)) {
			Validate.notNull(dto, "WorkSpace provider must be set.");
			uuid = dto.getUuid();
			Validate.notEmpty(uuid, "Missing WorkSpace provider uuid in the payload.");
		}
		WorkSpaceProvider workSpaceProvider = workSpaceProviderService.find(uuid);
		if (isDomainBelonging(domain, workSpaceProvider)) {
			workSpaceProviderService.delete(workSpaceProvider);
			domain.setWorkSpaceProvider(null);
			return new LDAPWorkSpaceProviderDto((LdapWorkSpaceProvider) workSpaceProvider);
		} else {
			throw new BusinessException(BusinessErrorCode.WORKSPACE_PROVIDER_NOT_FOUND,
					"The requested WorkSpace provider does not belong to the entered domain, please check the entered information.");
		}
	}
}
