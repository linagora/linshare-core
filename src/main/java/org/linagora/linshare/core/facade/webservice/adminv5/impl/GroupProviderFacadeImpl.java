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
import org.linagora.linshare.core.domain.constants.GroupProviderType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.GroupProvider;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.LdapGroupProvider;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.impl.AdminGenericFacadeImpl;
import org.linagora.linshare.core.facade.webservice.adminv5.GroupProviderFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.AbstractGroupProviderDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.LDAPGroupProviderDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DomainService;
import org.linagora.linshare.core.service.GroupLdapPatternService;
import org.linagora.linshare.core.service.GroupProviderService;
import org.linagora.linshare.core.service.RemoteServerService;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

public class GroupProviderFacadeImpl extends AdminGenericFacadeImpl implements GroupProviderFacade {

	private DomainService domainService;

	private GroupProviderService groupProviderService;

	private RemoteServerService ldapConnectionService;

	private GroupLdapPatternService groupLdapPatternService;

	public GroupProviderFacadeImpl(
			AccountService accountService,
			DomainService domainService,
			GroupProviderService groupProviderService,
			RemoteServerService ldapConnectionService,
			GroupLdapPatternService groupLdapPatternService) {
		super(accountService);
		this.domainService = domainService;
		this.groupProviderService = groupProviderService;
		this.ldapConnectionService = ldapConnectionService;
		this.groupLdapPatternService = groupLdapPatternService;
	}

	@Override
	public Set<AbstractGroupProviderDto> findAll(String domainUuid) {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		AbstractDomain domain = domainService.find(authUser, domainUuid);
		Set<AbstractGroupProviderDto> res = Sets.newHashSet();
		GroupProvider groupProvider = domain.getGroupProvider();
		if (groupProvider != null) {
			if (GroupProviderType.LDAP_PROVIDER.equals(groupProvider.getType())) {
				res.add(new LDAPGroupProviderDto((LdapGroupProvider) groupProvider));
			} else {
				throw new BusinessException(BusinessErrorCode.GROUP_PROVIDER_UNSUPPORTED_TYPE,
						"GroupProvider not supported yet");
			}
		}
		return res;
	}

	@Override
	public AbstractGroupProviderDto find(String domainUuid, String uuid) {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		AbstractDomain domain = domainService.find(authUser, domainUuid);
		GroupProvider groupProvider = groupProviderService.find(uuid);
		if (isDomainBelonging(domain, groupProvider)) {
			return new LDAPGroupProviderDto((LdapGroupProvider) groupProvider);
		} else {
			throw new BusinessException(BusinessErrorCode.GROUP_PROVIDER_NOT_FOUND,
					"The requested group provider does not belong to the entered domain, please check the entered information.");
		}
	}

	private Boolean isDomainBelonging(AbstractDomain domain, GroupProvider groupProvider) {
		return domain.getGroupProvider().getUuid().equals(groupProvider.getUuid()) ? true : false;
	}

	@Override
	public AbstractGroupProviderDto create(String domainUuid, AbstractGroupProviderDto dto) {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(dto, "group provider must be set.");
		Validate.notNull(dto.getType(), "group provider must be set.");
		AbstractDomain domain = domainService.find(authUser, domainUuid);
		if (domain.isRootDomain() || domain.isGuestDomain()) {
			throw new BusinessException(BusinessErrorCode.GROUP_PROVIDER_FORBIDDEN, "You can not manage a GroupProvider for this kind of domain.");
		}
		if (domain.getGroupProvider() != null) {
			throw new BusinessException(BusinessErrorCode.GROUP_PROVIDER_ALREADY_EXIST, "GroupProvider already exists. Can't create more than one");
		}
		if (dto.getType().equals(GroupProviderType.LDAP_PROVIDER)) {
			return createLdapGroupProvider(authUser, (LDAPGroupProviderDto) dto, domain);
		} else {
			throw new BusinessException(BusinessErrorCode.GROUP_PROVIDER_NOT_FOUND, "GroupProvider not found");
		}
	}

	private AbstractGroupProviderDto createLdapGroupProvider(User authUser, LDAPGroupProviderDto dto, AbstractDomain domain) {
		Validate.notNull(dto.getGroupFilter(), "GroupFilter payload is mandatory for group provider creation");
		String groupFilterUuid = dto.getGroupFilter().getUuid();
		Validate.notEmpty(groupFilterUuid, "GroupFilter uuid is mandatory for group provider creation");
		// ldap server
		Validate.notNull(dto.getLdapServer(), "LDAP Connection payload is mandatory for group provider creation");
		String ldapConnectionUuid = dto.getLdapServer().getUuid();
		Validate.notEmpty(ldapConnectionUuid, "LDAP connection uuid is mandatory for group provider creation");
		// baseDn
		String baseDn = dto.getBaseDn();
		Validate.notEmpty(baseDn, "baseDn is mandatory for group provider creation");
		GroupLdapPattern groupLdapPattern = groupLdapPatternService.find(groupFilterUuid);
		LdapConnection connection = ldapConnectionService.find(ldapConnectionUuid);
		LdapGroupProvider groupProvider = (LdapGroupProvider) groupProviderService
				.create(new LdapGroupProvider(
						domain,
						groupLdapPattern,
						baseDn,
						connection,
						dto.getSearchInOtherDomains()));
		domain.setGroupProvider(groupProvider);
		return new LDAPGroupProviderDto(groupProvider);
	}

	@Override
	public AbstractGroupProviderDto update(String domainUuid, String uuid, AbstractGroupProviderDto dto) {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(dto, "group provider must be set.");
		Validate.notNull(dto.getType(), "group provider must be set.");
		AbstractDomain domain = domainService.find(authUser, domainUuid);
		if (Strings.isNullOrEmpty(uuid)) {
			Validate.notNull(dto, "group provider must be set.");
			uuid = dto.getUuid();
			Validate.notEmpty(uuid, "Missing group provider uuid in the payload.");
		}
		GroupProvider groupProvider = groupProviderService.find(uuid);
		if (isDomainBelonging(domain, groupProvider)) {
			if (dto.getType().equals(GroupProviderType.LDAP_PROVIDER)) {
				return updateLdapGroupProvider((LDAPGroupProviderDto) dto, domain, groupProvider);
			} else {
				throw new BusinessException(BusinessErrorCode.GROUP_PROVIDER_NOT_FOUND, "GroupProvider not found");
			}
		} else {
			throw new BusinessException(BusinessErrorCode.GROUP_PROVIDER_NOT_FOUND,
					"The requested group provider does not belong to the entered domain, please check the entered information.");
		}
	}

	private LDAPGroupProviderDto updateLdapGroupProvider(LDAPGroupProviderDto groupProviderDto, AbstractDomain domain,
			GroupProvider groupProvider) {
		// group filter
		Validate.notNull(groupProviderDto.getGroupFilter(), "GroupFilter payload is mandatory for group provider update");
		String groupFilterUuid = groupProviderDto.getGroupFilter().getUuid();
		Validate.notEmpty(groupFilterUuid, "GroupFilter uuid is mandatory for group provider update");
		// ldap connection
		Validate.notNull(groupProviderDto.getLdapServer(), "LDAP Connection payload is mandatory for group provider update");
		String ldapConnectionUuid = groupProviderDto.getLdapServer().getUuid();
		Validate.notEmpty(ldapConnectionUuid, "LDAP connection uuid is mandatory for group provider update");
		// baseDn
		String baseDn = groupProviderDto.getBaseDn();
		Validate.notEmpty(baseDn, "baseDn is mandatory for group provider update");
		LdapGroupProvider provider = (LdapGroupProvider) groupProvider;
		provider.setBaseDn(baseDn);
		GroupLdapPattern groupLdapFilter = groupLdapPatternService.find(groupFilterUuid);
		LdapConnection connection = ldapConnectionService.find(ldapConnectionUuid);
		provider.setLdapConnection(connection);
		provider.setGroupPattern(groupLdapFilter);
		return new LDAPGroupProviderDto((LdapGroupProvider) groupProviderService.update(provider));
	}

	@Override
	public AbstractGroupProviderDto delete(String domainUuid, String uuid, AbstractGroupProviderDto dto) {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		AbstractDomain domain = domainService.find(authUser, domainUuid);
		if (domain.isRootDomain() || domain.isGuestDomain()) {
			throw new BusinessException(BusinessErrorCode.GROUP_PROVIDER_FORBIDDEN, "You can not manage GroupProvider for this kind of domain.");
		}
		if (Strings.isNullOrEmpty(uuid)) {
			Validate.notNull(dto, "group provider must be set.");
			uuid = dto.getUuid();
			Validate.notEmpty(uuid, "Missing group provider uuid in the payload.");
		}
		GroupProvider groupProvider = groupProviderService.find(uuid);
		if (isDomainBelonging(domain, groupProvider)) {
			groupProviderService.delete(groupProvider);
			domain.setGroupProvider(null);
			return new LDAPGroupProviderDto((LdapGroupProvider) groupProvider);
		} else {
			throw new BusinessException(BusinessErrorCode.GROUP_PROVIDER_NOT_FOUND,
					"The requested group provider does not belong to the entered domain, please check the entered information.");
		}
	}
}
