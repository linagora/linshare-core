/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.domain.entities.GuestDomain;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.LdapUserProvider;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MimePolicy;
import org.linagora.linshare.core.domain.entities.SubDomain;
import org.linagora.linshare.core.domain.entities.TopDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserLdapPattern;
import org.linagora.linshare.core.domain.entities.WelcomeMessages;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.DomainFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.LDAPUserProviderDto;
import org.linagora.linshare.core.facade.webservice.common.dto.DomainDto;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DomainPolicyService;
import org.linagora.linshare.core.service.LdapConnectionService;
import org.linagora.linshare.core.service.QuotaService;
import org.linagora.linshare.core.service.UserAndDomainMultiService;
import org.linagora.linshare.core.service.UserProviderService;
import org.linagora.linshare.core.service.WelcomeMessagesService;

import com.google.common.collect.Sets;

public class DomainFacadeImpl extends AdminGenericFacadeImpl implements
		DomainFacade {

	private final AbstractDomainService abstractDomainService;

	private final UserProviderService userProviderService;

	private final LdapConnectionService ldapConnectionService;

	private final DomainPolicyService domainPolicyService;

	private final UserAndDomainMultiService userAndDomainMultiService;

	private final QuotaService quotaService;

	private final WelcomeMessagesService welcomeMessagesService;

	public DomainFacadeImpl(final AccountService accountService,
			final AbstractDomainService abstractDomainService,
			final UserProviderService userProviderService,
			final DomainPolicyService domainPolicyService,
			final WelcomeMessagesService welcomeMessagesService,
			final LdapConnectionService ldapConnectionService,
			final QuotaService quotaService,
			final UserAndDomainMultiService userAndDomainMultiService) {
		super(accountService);
		this.abstractDomainService = abstractDomainService;
		this.userProviderService = userProviderService;
		this.domainPolicyService = domainPolicyService;
		this.userAndDomainMultiService = userAndDomainMultiService;
		this.welcomeMessagesService = welcomeMessagesService;
		this.quotaService = quotaService;
		this.ldapConnectionService = ldapConnectionService;
	}

	@Override
	public Set<DomainDto> findAll() throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		Set<DomainDto> domainDtoList = Sets.newHashSet();
		List<AbstractDomain> entities = abstractDomainService.findAll(actor);
		for (AbstractDomain abstractDomain : entities) {
			domainDtoList.add(DomainDto.getFull(abstractDomain));
		}
		return domainDtoList;
	}

	@Override
	public DomainDto find(String domain, boolean tree, boolean parent)
			throws BusinessException {
		 User actor = checkAuthentication(Role.ADMIN);
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
		 if (actor.hasSuperAdminRole()) {
			 simple = false;
		 } else if (entity.isManagedBy(actor)) {
			 simple = true;
		 } else {
			 throw new BusinessException(BusinessErrorCode.FORBIDDEN,
					 "the curent domain was not found : " + domain);
		 }
		 if (tree) {
			 if (simple) {
				 res = DomainDto.getSimpleTree(entity);
			 } else {
				 res = DomainDto.getFullTree(entity);
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
		User actor = checkAuthentication(Role.SUPERADMIN);
		AbstractDomain domain = getDomain(domainDto);
		switch (domain.getDomainType()) {
		case TOPDOMAIN:
			LdapUserProvider ldapUserProvider = createLdapUserProviderIfNeeded(domainDto);
			domain.setUserProvider(ldapUserProvider);
			return DomainDto.getFull(abstractDomainService.createTopDomain(actor, (TopDomain) domain));
		case SUBDOMAIN:
			LdapUserProvider ldapUserProvider2 = createLdapUserProviderIfNeeded(domainDto);
			domain.setUserProvider(ldapUserProvider2);
			return DomainDto.getFull(abstractDomainService.createSubDomain(actor, (SubDomain) domain));
		case GUESTDOMAIN:
			return DomainDto.getFull(abstractDomainService.createGuestDomain(actor, (GuestDomain) domain));
		default:
			throw new BusinessException(BusinessErrorCode.DOMAIN_INVALID_TYPE,
					"Try to create a root domain");
		}
	}

	private LdapUserProvider createLdapUserProviderIfNeeded(DomainDto domainDto) {
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
			ldapUserProvider = userProviderService.create(new LdapUserProvider(baseDn, ldapConnection, pattern));
		}
		return ldapUserProvider;
	}

	private LdapUserProvider updateLdapUserProvider(DomainDto domainDto) {
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
				ldapUserProvider = userProviderService.create(new LdapUserProvider(baseDn, ldapConnection, pattern));
			}
		}
		return ldapUserProvider;
	}

	@Override
	public DomainDto update(DomainDto domainDto) throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(domainDto.getIdentifier(),
				"domain identifier must be set.");
		AbstractDomain domain = getDomain(domainDto);
		LdapUserProvider ldapUserProvider = updateLdapUserProvider(domainDto);
		domain.setUserProvider(ldapUserProvider);
		return DomainDto.getFull(abstractDomainService.updateDomain(actor, domain));
	}

	@Override
	public DomainDto delete(DomainDto domainDto) throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(domainDto.getIdentifier(),
				"domain identifier must be set.");
		AbstractDomain domain = userAndDomainMultiService.deleteDomainAndUsers(actor, domainDto.getIdentifier());
		return DomainDto.getFull(domain);
	}

	@Override
	public DomainDto delete(String domainId) throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(domainId,
				"domain identifier must be set.");
		AbstractDomain domain = userAndDomainMultiService.deleteDomainAndUsers(actor, domainId);
		return DomainDto.getFull(domain);
	}

	private AbstractDomain getDomain(DomainDto domainDto)
			throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
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

		WelcomeMessages wlcm = welcomeMessagesService.find(actor, domainDto.getCurrentWelcomeMessage().getUuid());
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

}
