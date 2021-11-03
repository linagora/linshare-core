/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 *
 * Copyright (C) 2015-2021 LINAGORA
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
package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.constants.UserProviderType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.domain.entities.DriveProvider;
import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.GroupProvider;
import org.linagora.linshare.core.domain.entities.GuestDomain;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.LdapDriveFilter;
import org.linagora.linshare.core.domain.entities.LdapDriveProvider;
import org.linagora.linshare.core.domain.entities.LdapGroupProvider;
import org.linagora.linshare.core.domain.entities.LdapUserProvider;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MimePolicy;
import org.linagora.linshare.core.domain.entities.SubDomain;
import org.linagora.linshare.core.domain.entities.TopDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserLdapPattern;
import org.linagora.linshare.core.domain.entities.UserProvider;
import org.linagora.linshare.core.domain.entities.WelcomeMessages;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.DomainFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.LDAPDriveProviderDto;
import org.linagora.linshare.core.facade.webservice.admin.dto.LDAPGroupProviderDto;
import org.linagora.linshare.core.facade.webservice.admin.dto.LDAPUserProviderDto;
import org.linagora.linshare.core.facade.webservice.common.dto.DomainDto;
import org.linagora.linshare.core.facade.webservice.common.dto.LightCommonDto;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DomainPolicyService;
import org.linagora.linshare.core.service.DriveProviderService;
import org.linagora.linshare.core.service.GroupLdapPatternService;
import org.linagora.linshare.core.service.GroupProviderService;
import org.linagora.linshare.core.service.LdapDriveFilterService;
import org.linagora.linshare.core.service.QuotaService;
import org.linagora.linshare.core.service.UserProviderService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.core.service.WelcomeMessagesService;
import org.linagora.linshare.core.service.impl.LdapConnectionServiceImpl;

import com.google.common.collect.Sets;

@SuppressWarnings( "deprecation" )
public class DomainFacadeImpl extends AdminGenericFacadeImpl implements
		DomainFacade {

	private final AbstractDomainService abstractDomainService;

	private final UserProviderService userProviderService;

	private final LdapConnectionServiceImpl ldapConnectionService;

	private final DomainPolicyService domainPolicyService;

	private final QuotaService quotaService;

	private final WelcomeMessagesService welcomeMessagesService;

	private final GroupProviderService groupProviderService;

	private final DriveProviderService driveProviderService;

	private final GroupLdapPatternService groupLdapPatternService;

	private final LdapDriveFilterService driveLdapPatternService;

	private final UserService userService;

	public DomainFacadeImpl(final AccountService accountService,
			final AbstractDomainService abstractDomainService,
			final UserProviderService userProviderService,
			final DomainPolicyService domainPolicyService,
			final WelcomeMessagesService welcomeMessagesService,
			final LdapConnectionServiceImpl ldapConnectionService,
			final QuotaService quotaService,
			final GroupProviderService groupProviderService,
			final GroupLdapPatternService groupLdapPatternService,
			UserService userService,
			DriveProviderService driveProviderService,
			LdapDriveFilterService driveLdapPatternService) {
		super(accountService);
		this.abstractDomainService = abstractDomainService;
		this.userProviderService = userProviderService;
		this.domainPolicyService = domainPolicyService;
		this.welcomeMessagesService = welcomeMessagesService;
		this.quotaService = quotaService;
		this.ldapConnectionService = ldapConnectionService;
		this.groupProviderService = groupProviderService;
		this.groupLdapPatternService = groupLdapPatternService;
		this.userService = userService;
		this.driveProviderService = driveProviderService;
		this.driveLdapPatternService = driveLdapPatternService;
	}

	@Override
	public Set<DomainDto> findAll() throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Set<DomainDto> domainDtoList = Sets.newHashSet();
		List<AbstractDomain> entities = abstractDomainService.findAll(authUser);
		for (AbstractDomain abstractDomain : entities) {
			domainDtoList.add(DomainDto.getFull(abstractDomain));
		}
		return domainDtoList;
	}

	@Override
	public DomainDto find(String domain, boolean tree, boolean parent)
			throws BusinessException {
		 User authUser = checkAuthentication(Role.ADMIN);
		 Validate.notEmpty(domain, "domain identifier must be set.");
		 AbstractDomain entity =
				 abstractDomainService.retrieveDomain(domain);
		 DomainDto res = null;
		 if (entity == null) {
			 throw new
			 BusinessException(BusinessErrorCode.DOMAIN_DO_NOT_EXIST,
					 "the curent domain was not found : " + domain);
		 }
		 boolean simple = true;
		 if (authUser.hasSuperAdminRole()) {
			 simple = false;
		 } else if (entity.isManagedBy(authUser)) {
			 simple = true;
		 } else {
			 throw new BusinessException(BusinessErrorCode.FORBIDDEN,
					 "the curent domain was not found : " + domain);
		}
		if (tree) {
			if (simple) {
				res = getDomainTree(DomainDto.getSimple(entity), false);
			} else {
				res = getDomainTree(DomainDto.getFull(entity), false);
			}
		} else {
			 res = DomainDto.getSimple(entity);
		 }
		 DomainQuota quota = quotaService.find(entity);
		 // Workaround from 1.12 to 2.00. quota could be null.
		 if (quota != null) {
			 res.setQuota(quota.getUuid());
		 }
		 DomainDto rootdomain = res;
		 if (parent) {
			 if (entity.getParentDomain() != null) {
				 AbstractDomain firstParent = entity.getParentDomain();
				 DomainDto topdomain = DomainDto.getSimple(firstParent);
				 topdomain.addChild(res);
				 if (firstParent.getParentDomain() != null) {
					 rootdomain = DomainDto.getSimple(firstParent.getParentDomain());
					 rootdomain.addChild(topdomain);
				 } else {
					 rootdomain = topdomain;
				 }
			 }
		 }
		 return rootdomain;
	}

	@Override
	public DomainDto create(DomainDto domainDto) throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		AbstractDomain domain = null;
		LdapUserProvider ldapUserProvider = null;
		LdapGroupProvider ldapGroupProvider = null;
		LdapDriveProvider ldapDriveProvider = null;
		AbstractDomain domainObject = getDomain(domainDto);
		switch (domainObject.getDomainType()) {
		case TOPDOMAIN:
			domain = abstractDomainService.createTopDomain(authUser, (TopDomain) domainObject);
			ldapUserProvider = createLdapUserProviderIfNeeded(domainDto, domain);
			ldapGroupProvider = createLdapGroupProviderIfNeeded(domainDto);
			ldapDriveProvider = createLdapDriveProviderIfNeeded(domainDto);
			domain.setUserProvider(ldapUserProvider);
			domain.setGroupProvider(ldapGroupProvider);
			domain.setDriveProvider(ldapDriveProvider);
			return DomainDto.getFull(abstractDomainService.updateDomain(authUser, domain));
		case SUBDOMAIN:
			domain = abstractDomainService.createSubDomain(authUser, (SubDomain) domainObject);
			ldapUserProvider = createLdapUserProviderIfNeeded(domainDto, domain);
			ldapGroupProvider = createLdapGroupProviderIfNeeded(domainDto);
			ldapDriveProvider = createLdapDriveProviderIfNeeded(domainDto);
			domain.setUserProvider(ldapUserProvider);
			domain.setGroupProvider(ldapGroupProvider);
			domain.setDriveProvider(ldapDriveProvider);
			return DomainDto.getFull(abstractDomainService.updateDomain(authUser, domain));
		case GUESTDOMAIN:
			return DomainDto.getFull(abstractDomainService.createGuestDomain(authUser, (GuestDomain) domainObject));
		default:
			throw new BusinessException(BusinessErrorCode.DOMAIN_INVALID_TYPE,
					"Try to create a root domain");
		}
	}

	private LdapUserProvider createLdapUserProviderIfNeeded(DomainDto domainDto, AbstractDomain domain) {
		LdapUserProvider ldapUserProvider = null;
		List<LDAPUserProviderDto> providers = domainDto.getProviders();
		if (providers != null && !providers.isEmpty()) {
			// For now there is (must be) only one.
			LDAPUserProviderDto userProviderDto = providers.get(0);
			String domainPatternUuid = userProviderDto.getUserLdapPatternUuid();
			String ldapUuid = userProviderDto.getLdapConnectionUuid();
			String baseDn = userProviderDto.getBaseDn();
			Validate.notEmpty(domainPatternUuid, "domainPatternUuid is mandatory for user provider creation");
			Validate.notEmpty(ldapUuid, "ldapUuid is mandatory for user provider creation");
			Validate.notEmpty(baseDn, "baseDn is mandatory for user provider creation");
			LdapConnection ldapConnection = ldapConnectionService.find(ldapUuid);
			UserLdapPattern pattern = userProviderService.findDomainPattern(domainPatternUuid);
			ldapUserProvider = userProviderService.create(new LdapUserProvider(domain, baseDn, ldapConnection, pattern));
		}
		return ldapUserProvider;
	}

	private UserProvider updateLdapUserProvider(DomainDto domainDto) {
		AbstractDomain domain = abstractDomainService.findById(domainDto.getIdentifier());
		if (domain.getUserProvider() != null) {
			// If current provider is ldap, we handle it, otherwise we will need to use the new api
			UserProviderType type = domain.getUserProvider().getType();
			if (!UserProviderType.LDAP_PROVIDER.equals(type)) {
				String message = "Can update existing UserProvider (" + type + ") ! See new admin api. Update skipped.";
				logger.error(message);
				return domain.getUserProvider();
			}
		}
		LdapUserProvider ldapUserProvider = null;
		List<LDAPUserProviderDto> providers = domainDto.getProviders();
		if (providers != null && !providers.isEmpty()) {
			// For now there is (must be) only one.
			LDAPUserProviderDto userProviderDto = providers.get(0);
			String domainPatternUuid = userProviderDto.getUserLdapPatternUuid();
			String ldapUuid = userProviderDto.getLdapConnectionUuid();
			String baseDn = userProviderDto.getBaseDn();
			Validate.notEmpty(domainPatternUuid, "userLdapPatternUuid is mandatory for user provider creation");
			Validate.notEmpty(ldapUuid, "ldapUuid is mandatory for user provider creation");
			Validate.notEmpty(baseDn, "baseDn is mandatory for user provider creation");
			LdapConnection ldapConnection = ldapConnectionService.find(ldapUuid);
			UserLdapPattern pattern = userProviderService.findDomainPattern(domainPatternUuid);
			if (userProviderService.exists(userProviderDto.getUuid())) {
				LdapUserProvider userProvider = userProviderService.find(userProviderDto.getUuid());
				userProvider.setBaseDn(userProviderDto.getBaseDn());
				userProvider.setLdapConnection(ldapConnection);
				userProvider.setPattern(pattern);
				ldapUserProvider = userProviderService.update(userProvider);
			} else {
				ldapUserProvider = userProviderService.create(new LdapUserProvider(domain, baseDn, ldapConnection, pattern));
			}
		}
		return ldapUserProvider;
	}

	private LdapGroupProvider createLdapGroupProviderIfNeeded(DomainDto domainDto) {
		LdapGroupProvider ldapGroupProvider = null;
		List<LDAPGroupProviderDto> providers = domainDto.getGroupProviders();
		if (providers != null && !providers.isEmpty()) {
			LDAPGroupProviderDto groupProviderDto = providers.get(0);
			LightCommonDto groupPatternLight = groupProviderDto.getPattern();
			LightCommonDto ldapConnectionLight = groupProviderDto.getConnection();
			String baseDn = groupProviderDto.getBaseDn();
			Boolean searchInOtherDomains = groupProviderDto.getSearchInOtherDomains();
			Validate.notNull(groupPatternLight, "groupPattern is mandatory for group provider creation");
			Validate.notNull(ldapConnectionLight, "ldapConnection is mandatory for group provider creation");
			Validate.notEmpty(groupPatternLight.getUuid(), "group pattern uuid is mandatory for group provider creation");
			Validate.notEmpty(ldapConnectionLight.getUuid(), "ldap connection Uuid is mandatory for group provider creation");
			Validate.notEmpty(baseDn, "baseDn is mandatory for group provider creation");
			LdapConnection ldapConnection = ldapConnectionService.find(ldapConnectionLight.getUuid());
			GroupLdapPattern pattern = groupLdapPatternService.find(groupPatternLight.getUuid());
			ldapGroupProvider = groupProviderService.create(
					new LdapGroupProvider(pattern, baseDn, ldapConnection, searchInOtherDomains));
		}
		return ldapGroupProvider;
	}

	private LdapDriveProvider createLdapDriveProviderIfNeeded(DomainDto domainDto) {
		LdapDriveProvider ldapDriveProvider = null;
		List<LDAPDriveProviderDto> providers = domainDto.getDriveProviders();
		if (providers != null && !providers.isEmpty()) {
			LDAPDriveProviderDto driveProviderDto = providers.get(0);
			LightCommonDto drivePatternLight = driveProviderDto.getPattern();
			LightCommonDto ldapConnectionLight = driveProviderDto.getConnection();
			String baseDn = driveProviderDto.getBaseDn();
			Boolean searchInOtherDomains = driveProviderDto.getSearchInOtherDomains();
			Validate.notNull(drivePatternLight, "drivePatternLight is mandatory for drive provider creation");
			Validate.notNull(ldapConnectionLight, "ldapConnection is mandatory for drive provider creation");
			Validate.notEmpty(drivePatternLight.getUuid(),
					"group pattern uuid is mandatory for drive provider creation");
			Validate.notEmpty(ldapConnectionLight.getUuid(),
					"ldap connection Uuid is mandatory for drive provider creation");
			Validate.notEmpty(baseDn, "baseDn is mandatory for drive provider creation");
			LdapConnection ldapConnection = ldapConnectionService.find(ldapConnectionLight.getUuid());
			LdapDriveFilter pattern = driveLdapPatternService.find(drivePatternLight.getUuid());
			ldapDriveProvider = driveProviderService
					.create(new LdapDriveProvider(pattern, baseDn, ldapConnection, searchInOtherDomains));
		}
		return ldapDriveProvider;
	}

	@Override
	public DomainDto update(DomainDto domainDto) throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(domainDto.getIdentifier(),
				"domain identifier must be set.");
		AbstractDomain domain = getDomain(domainDto);
		UserProvider ldapUserProvider = updateLdapUserProvider(domainDto);
		domain.setUserProvider(ldapUserProvider);
		GroupProvider ldapGroupProvider = updateLdapGroupProvider(domainDto);
		domain.setGroupProvider(ldapGroupProvider);
		DriveProvider ldapDriveProvider = updateLdapDriveProvider(domainDto);
		domain.setDriveProvider(ldapDriveProvider);
		return DomainDto.getFull(abstractDomainService.updateDomain(authUser, domain));
	}

	private LdapGroupProvider updateLdapGroupProvider(DomainDto domainDto) {
		LdapGroupProvider ldapGroupProvider = null;
		List<LDAPGroupProviderDto> providers = domainDto.getGroupProviders();
		if (providers != null && !providers.isEmpty()) {
			LDAPGroupProviderDto groupProviderDto = providers.get(0);
			LightCommonDto groupPatternLight = groupProviderDto.getPattern();
			LightCommonDto ldapConnectionLight = groupProviderDto.getConnection();
			String baseDn = groupProviderDto.getBaseDn();
			Boolean searchInOtherDomains = groupProviderDto.getSearchInOtherDomains();
			Validate.notNull(groupPatternLight, "groupPattern is mandatory for group provider creation");
			Validate.notNull(ldapConnectionLight, "ldapConnection is mandatory for group provider creation");
			Validate.notEmpty(groupPatternLight.getUuid(), "group pattern uuid is mandatory for group provider creation");
			Validate.notEmpty(ldapConnectionLight.getUuid(), "ldap connection Uuid is mandatory for group provider creation");
			Validate.notEmpty(baseDn, "baseDn is mandatory for group provider creation");
			LdapConnection ldapConnection = ldapConnectionService.find(ldapConnectionLight.getUuid());
			GroupLdapPattern pattern = groupLdapPatternService.find(groupPatternLight.getUuid());
			if (groupProviderService.exists(groupProviderDto.getUuid())) {
				LdapGroupProvider groupProvider = groupProviderService.find(groupProviderDto.getUuid());
				groupProvider.setBaseDn(groupProviderDto.getBaseDn());
				groupProvider.setLdapConnection(ldapConnection);
				groupProvider.setGroupPattern(pattern);
				groupProvider.setSearchInOtherDomains(searchInOtherDomains);
				ldapGroupProvider = groupProviderService.update(groupProvider);
			} else {
				ldapGroupProvider = groupProviderService.create(
						new LdapGroupProvider(pattern, baseDn, ldapConnection, searchInOtherDomains));
			}
		}
		return ldapGroupProvider;
	}

	private LdapDriveProvider updateLdapDriveProvider(DomainDto domainDto) {
		LdapDriveProvider ldapDriveProvider = null;
		List<LDAPDriveProviderDto> providers = domainDto.getDriveProviders();
		if (providers != null && !providers.isEmpty()) {
			LDAPDriveProviderDto driveProviderDto = providers.get(0);
			LightCommonDto drivePatternLight = driveProviderDto.getPattern();
			LightCommonDto ldapConnectionLight = driveProviderDto.getConnection();
			String baseDn = driveProviderDto.getBaseDn();
			Boolean searchInOtherDomains = driveProviderDto.getSearchInOtherDomains();
			Validate.notNull(drivePatternLight, "drivePattern is mandatory for drive provider creation");
			Validate.notNull(ldapConnectionLight, "ldapConnection is mandatory for drive provider creation");
			Validate.notEmpty(drivePatternLight.getUuid(), "drive pattern uuid is mandatory for drive provider creation");
			Validate.notEmpty(ldapConnectionLight.getUuid(), "ldap connection Uuid is mandatory for drive provider creation");
			Validate.notEmpty(baseDn, "baseDn is mandatory for drive provider creation");
			LdapConnection ldapConnection = ldapConnectionService.find(ldapConnectionLight.getUuid());
			LdapDriveFilter pattern = driveLdapPatternService.find(drivePatternLight.getUuid());
			if (driveProviderService.exists(driveProviderDto.getUuid())) {
				LdapDriveProvider driveProvider = driveProviderService.find(driveProviderDto.getUuid());
				driveProvider.setBaseDn(driveProviderDto.getBaseDn());
				driveProvider.setLdapConnection(ldapConnection);
				driveProvider.setDriveFilter(pattern);
				driveProvider.setSearchInOtherDomains(searchInOtherDomains);
				ldapDriveProvider = driveProviderService.update(driveProvider);
			} else {
				ldapDriveProvider = driveProviderService.create(
						new LdapDriveProvider(pattern, baseDn, ldapConnection, searchInOtherDomains));
			}
		}
		return ldapDriveProvider;
	}

	@Override
	public DomainDto delete(String domainId) throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(domainId, "domain identifier must be set.");
		if (domainId.equals("LinShareRootDomain")) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "Can't remove domain LinShareRootDomain.");
		}
		userService.deleteAllUsersFromDomain(authUser, domainId);
		AbstractDomain domain = abstractDomainService.markToPurge(authUser, domainId);
		return DomainDto.getFull(domain);
	}

	private AbstractDomain getDomain(DomainDto domainDto)
			throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
//		Validate.notEmpty(domainDto.getUuid(),
//				"domain identifier must be set.");
		Validate.notNull(domainDto.getPolicy(), "domain policy must be set.");
		Validate.notEmpty(domainDto.getPolicy().getIdentifier(),
				"domain policy identifier must be set.");
		Validate.notEmpty(domainDto.getLabel(), "label must be set.");
		Validate.notNull(domainDto.getLanguage(), "language must be set.");
		Validate.notNull(domainDto.getExternalMailLocale(), "external mail locale must be set.");
		Validate.notNull(domainDto.getCurrentWelcomeMessage(), "Current messages must be set.");
		Validate.notEmpty(domainDto.getCurrentWelcomeMessage().getUuid(), "Current message uuid must be set.");
		Validate.notEmpty(domainDto.getCurrentWelcomeMessage().getUuid(), "Current message uuid must be set.");
		Validate.notEmpty(domainDto.getType(), "Domain type must be set.");

		DomainType domainType = DomainType.valueOf(domainDto.getType());
		AbstractDomain parent = abstractDomainService.retrieveDomain(domainDto
				.getParent());
		AbstractDomain domain = domainType.getDomain(domainDto, parent);
		DomainPolicy policy = domainPolicyService
				.find(domainDto.getPolicy().getIdentifier());
		domain.setPolicy(policy);

		WelcomeMessages wlcm = welcomeMessagesService.find(authUser, domainDto.getCurrentWelcomeMessage().getUuid());
		domain.setCurrentWelcomeMessages(wlcm);

		if (domainDto.getMailConfigUuid() != null) {
			MailConfig mailConfig = new MailConfig();
			mailConfig.setUuid(domainDto.getMailConfigUuid());
			domain.setCurrentMailConfiguration(mailConfig);
		}
		if (domainDto.getMimePolicyUuid() != null) {
			MimePolicy mimePolicy = new MimePolicy();
			mimePolicy.setUuid(domainDto.getMimePolicyUuid());
			domain.setMimePolicy(mimePolicy);
		}
		return domain;
	}
	
	private DomainDto getDomainTree(DomainDto domainDto, boolean light) {
		for (AbstractDomain child : abstractDomainService.getSubDomainsByDomain(domainDto.getIdentifier())) {
			DomainDto childDto = null;
			if (light)
				childDto = getDomainTree(DomainDto.getSimple(child), light);
			else
				childDto = getDomainTree(DomainDto.getFull(child), light);
			domainDto.getChildren().add(childDto);
			childDto.setParent(domainDto.getIdentifier());
		}
		return domainDto;
	}
}
