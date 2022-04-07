/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2021-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.LdapUserProvider;
import org.linagora.linshare.core.domain.entities.OIDCUserProvider;
import org.linagora.linshare.core.domain.entities.TwakeGuestUserProvider;
import org.linagora.linshare.core.domain.entities.TwakeUserProvider;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserProvider;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.impl.AdminGenericFacadeImpl;
import org.linagora.linshare.core.facade.webservice.adminv5.UserProviderFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.AbstractUserProviderDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.LDAPUserProviderDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.OIDCUserProviderDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.TwakeGuestUserProviderDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.TwakeUserProviderDto;
import org.linagora.linshare.core.repository.LdapUserProviderRepository;
import org.linagora.linshare.core.repository.OIDCUserProviderRepository;
import org.linagora.linshare.core.repository.TwakeGuestUserProviderRepository;
import org.linagora.linshare.core.repository.TwakeUserProviderRepository;
import org.linagora.linshare.core.repository.UserProviderRepository;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DomainService;
import org.linagora.linshare.core.service.UserProviderService;
import org.linagora.linshare.core.service.impl.LdapConnectionServiceImpl;
import org.linagora.linshare.core.service.impl.TwakeConnectionServiceImpl;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

public class UserProviderFacadeImpl extends AdminGenericFacadeImpl implements UserProviderFacade {

	private DomainService domainService;

	private UserProviderService userProviderService;

	private LdapConnectionServiceImpl ldapConnectionService;

	private TwakeConnectionServiceImpl twakeConnectionService;

	private UserProviderRepository userProviderRepository;

	private LdapUserProviderRepository ldapUserProviderRepository;

	private OIDCUserProviderRepository oidcUserProviderRepository;

	private TwakeUserProviderRepository twakeUserProviderRepository;

	private TwakeGuestUserProviderRepository twakeGuestUserProviderRepository;

	public UserProviderFacadeImpl(
			AccountService accountService,
			DomainService domainService,
			UserProviderService userProviderService,
			LdapConnectionServiceImpl ldapConnectionService,
			TwakeConnectionServiceImpl twakeConnectionService,
			UserProviderRepository userProviderRepository,
			LdapUserProviderRepository ldapUserProviderRepository,
			OIDCUserProviderRepository oidcUserProviderRepository,
			TwakeUserProviderRepository twakeUserProviderRepository,
			TwakeGuestUserProviderRepository twakeGuestUserProviderRepository
			) {
		super(accountService);
		this.domainService = domainService;
		this.userProviderService = userProviderService;
		this.ldapConnectionService = ldapConnectionService;
		this.twakeConnectionService = twakeConnectionService;
		this.userProviderRepository = userProviderRepository;
		this.ldapUserProviderRepository = ldapUserProviderRepository;
		this.oidcUserProviderRepository = oidcUserProviderRepository;
		this.twakeUserProviderRepository = twakeUserProviderRepository;
		this.twakeGuestUserProviderRepository = twakeGuestUserProviderRepository;
	}

	@Override
	public Set<AbstractUserProviderDto> findAll(String domainUuid) {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		AbstractDomain domain = domainService.find(authUser, domainUuid);
		UserProvider up = domain.getUserProvider();
		if (up != null) {
			return ImmutableSet.of(toDto(domain, userProviderRepository.findByUuid(up.getUuid())));
		}
		return ImmutableSet.of();
	}

	@Override
	public AbstractUserProviderDto find(String domainUuid, String uuid) {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		AbstractDomain domain = domainService.find(authUser, domainUuid);
		UserProvider up = userProviderRepository.findByUuid(uuid);
		if (up == null) {
			logger.debug("UserProvider {} was not found", uuid);
			throw new BusinessException(BusinessErrorCode.USER_PROVIDER_NOT_FOUND, "UserProvider not found");
		}
		return toDto(domain, up);
	}

	private AbstractUserProviderDto toDto(AbstractDomain domain, UserProvider up) {
		switch (up.getType()) {
			case LDAP_PROVIDER:
				return new LDAPUserProviderDto(domain, (LdapUserProvider) up);
			case OIDC_PROVIDER:
				return new OIDCUserProviderDto((OIDCUserProvider) up);
			case TWAKE_PROVIDER:
				return new TwakeUserProviderDto((TwakeUserProvider) up);
			case TWAKE_GUEST_PROVIDER:
				return new TwakeGuestUserProviderDto((TwakeGuestUserProvider) up);
		}
		throw new BusinessException(BusinessErrorCode.USER_PROVIDER_UNSUPPORTED_TYPE, "UserProvider not supported yet");
	}

	@Override
	public AbstractUserProviderDto create(String domainUuid, AbstractUserProviderDto dto) {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(dto, "user provider must be set.");
		Validate.notNull(dto.getType(), "user provider must be set.");
		AbstractDomain domain = domainService.find(authUser, domainUuid);
		// TODO: we need to put all the following lines of code in a new UserProviderService(s)
		if (domain.getUserProvider() != null) {
			throw new BusinessException(BusinessErrorCode.USER_PROVIDER_ALREADY_EXIST, "UserProvider already exists. Can't create more than one");
		}
		switch (dto.getType()) {
			case LDAP_PROVIDER:
				if (domain.isRootDomain() || domain.isGuestDomain()) {
					throw new BusinessException(BusinessErrorCode.USER_PROVIDER_FORBIDDEN, "You can not create an UserProvider for this kind of domain.");
				}
				return createLdapUserProvider((LDAPUserProviderDto) dto, domain);
			case OIDC_PROVIDER:
				if (domain.isRootDomain() || domain.isGuestDomain()) {
					throw new BusinessException(BusinessErrorCode.USER_PROVIDER_FORBIDDEN, "You can not create an UserProvider for this kind of domain.");
				}
				return createOidcUserProvider((OIDCUserProviderDto) dto, domain);
			case TWAKE_PROVIDER:
				if (domain.isRootDomain() || domain.isGuestDomain()) {
					throw new BusinessException(BusinessErrorCode.USER_PROVIDER_FORBIDDEN, "You can not create an UserProvider for this kind of domain.");
				}
				return createTwakeUserProvider((TwakeUserProviderDto) dto, domain);
			case TWAKE_GUEST_PROVIDER:
				if (!domain.isGuestDomain()) {
					throw new BusinessException(BusinessErrorCode.USER_PROVIDER_FORBIDDEN, "You can not create an UserProvider for this kind of domain.");
				}
				return createTwakeGuestUserProvider((TwakeGuestUserProviderDto) dto, domain);
		}
		throw new BusinessException(BusinessErrorCode.USER_PROVIDER_NOT_FOUND, "UserProvider not found");
	}

	private LDAPUserProviderDto createLdapUserProvider(LDAPUserProviderDto dto, AbstractDomain domain) {
		// user filter
		Validate.notNull(dto.getUserFilter(), "UserFilter is mandatory for user provider creation");
		String userFilterUuid = dto.getUserFilter().getUuid();
		Validate.notEmpty(userFilterUuid, "UserFilter uuid is mandatory for user provider creation");
		// ldap connection
		Validate.notNull(dto.getLdapServer(), "LDAP Connection is mandatory for user provider creation");
		String ldapConnectionUuid = dto.getLdapServer().getUuid();
		Validate.notEmpty(ldapConnectionUuid, "LDAP connection uuid is mandatory for user provider creation");
		// baseDn
		String baseDn = dto.getBaseDn();
		Validate.notEmpty(baseDn, "baseDn is mandatory for user provider creation");

		LdapUserProvider userProvider =  (LdapUserProvider) userProviderRepository.create(
				new LdapUserProvider(domain,
						baseDn,
						ldapConnectionService.find(ldapConnectionUuid),
						userProviderService.findDomainPattern(userFilterUuid)));
		// no update ? I think implicit opened transaction do the job
		domain.setUserProvider(userProvider);
		return new LDAPUserProviderDto(domain, userProvider);
	}

	private OIDCUserProviderDto createOidcUserProvider(OIDCUserProviderDto dto, AbstractDomain domain) {
		Validate.notEmpty(dto.getDomainDiscriminator(), "Domain discriminator is mandatory for user provider creation");
		OIDCUserProvider userProvider = new OIDCUserProvider(domain, dto.getDomainDiscriminator());
		if (dto.getCheckExternalUserID() != null) {
			userProvider.setCheckExternalUserID(dto.getCheckExternalUserID());
		}
		if (dto.getUseAccessClaim() != null) {
			userProvider.setUseAccessClaim(dto.getUseAccessClaim());
		}
		if (dto.getUseRoleClaim() != null) {
			userProvider.setUseRoleClaim(dto.getUseRoleClaim());
		}
		if (dto.getUseEmailLocaleClaim() != null) {
			userProvider.setUseEmailLocaleClaim(dto.getUseEmailLocaleClaim());
		}
		if (oidcUserProviderRepository.findByDomainDiscriminator(dto.getDomainDiscriminator()) != null) {
			throw new BusinessException(BusinessErrorCode.OIDC_USER_PROVIDER_DOMAIN_DISCRIMINATOR_ALREADY_EXISTS, "Domain discriminator should be unique.");
		}
		OIDCUserProvider created = (OIDCUserProvider) userProviderRepository.create(userProvider);
		// no update ? I think implicit opened transaction do the job
		domain.setUserProvider(created);
		return new OIDCUserProviderDto(created);
	}

	private TwakeUserProviderDto createTwakeUserProvider(TwakeUserProviderDto dto, AbstractDomain domain) {
		Validate.notNull(dto.getTwakeServer(), "Twake server is mandatory for user provider creation");
		String twakeConnectionUuid = dto.getTwakeServer().getUuid();
		Validate.notEmpty(twakeConnectionUuid, "Twake connection uuid is mandatory for user provider creation");
		Validate.notEmpty(dto.getTwakeCompanyId(), "Twake companyId is mandatory for user provider creation");

		TwakeUserProvider userProvider =  (TwakeUserProvider) userProviderRepository.create(
			new TwakeUserProvider(domain, twakeConnectionService.find(twakeConnectionUuid), dto.getTwakeCompanyId()));
		domain.setUserProvider(userProvider);
		return new TwakeUserProviderDto(userProvider);
	}

	private TwakeGuestUserProviderDto createTwakeGuestUserProvider(TwakeGuestUserProviderDto dto, AbstractDomain domain) {
		Validate.notNull(dto.getTwakeServer(), "Twake server is mandatory for user provider creation");
		String twakeConnectionUuid = dto.getTwakeServer().getUuid();
		Validate.notEmpty(twakeConnectionUuid, "Twake connection uuid is mandatory for user provider creation");
		Validate.notEmpty(dto.getTwakeCompanyId(), "Twake companyId is mandatory for user provider creation");

		TwakeGuestUserProvider userProvider =  (TwakeGuestUserProvider) userProviderRepository.create(
			new TwakeGuestUserProvider(domain, twakeConnectionService.find(twakeConnectionUuid), dto.getTwakeCompanyId()));
		domain.setUserProvider(userProvider);
		return new TwakeGuestUserProviderDto(userProvider);
	}

	@Override
	public AbstractUserProviderDto update(String domainUuid, String uuid, AbstractUserProviderDto dto) {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(dto, "user provider must be set.");
		Validate.notNull(dto.getType(), "user provider must be set.");
		AbstractDomain domain = domainService.find(authUser, domainUuid);
		// TODO: we need to put all the following lines of code in a new UserProviderService(s)
		if (Strings.isNullOrEmpty(uuid)) {
			Validate.notNull(dto, "user provider must be set.");
			uuid = dto.getUuid();
			Validate.notEmpty(uuid, "Missing user provider uuid in the payload.");
		}
		UserProvider userProvider = findByService(uuid);
		switch (dto.getType()) {
			case LDAP_PROVIDER:
				if (domain.isRootDomain() || domain.isGuestDomain()) {
					throw new BusinessException(BusinessErrorCode.USER_PROVIDER_FORBIDDEN, "You can not update an UserProvider for this kind of domain.");
				}
				return updateLdapUserProvider((LDAPUserProviderDto) dto, domain, userProvider);
			case OIDC_PROVIDER:
				if (domain.isRootDomain() || domain.isGuestDomain()) {
					throw new BusinessException(BusinessErrorCode.USER_PROVIDER_FORBIDDEN, "You can not update an UserProvider for this kind of domain.");
				}
				return updateOidcUserProvider((OIDCUserProviderDto) dto, userProvider);
			case TWAKE_PROVIDER:
				if (domain.isRootDomain() || domain.isGuestDomain()) {
					throw new BusinessException(BusinessErrorCode.USER_PROVIDER_FORBIDDEN, "You can not update an UserProvider for this kind of domain.");
				}
				return updateTwakeUserProvider((TwakeUserProviderDto) dto, userProvider);
			case TWAKE_GUEST_PROVIDER:
				if (!domain.isGuestDomain()) {
					throw new BusinessException(BusinessErrorCode.USER_PROVIDER_FORBIDDEN, "You can not update an UserProvider for this kind of domain.");
				}
				return updateTwakeGuestUserProvider((TwakeGuestUserProviderDto) dto, userProvider);
		}
		throw new BusinessException(BusinessErrorCode.USER_PROVIDER_NOT_FOUND, "UserProvider not found");
	}

	private OIDCUserProviderDto updateOidcUserProvider(OIDCUserProviderDto userProviderDto, UserProvider userProvider) {
		Validate.notEmpty(userProviderDto.getDomainDiscriminator(), "Domain discriminator is mandatory for user provider update");
		Validate.notNull(userProviderDto.getCheckExternalUserID(), "checkExternalUserID is mandatory for user provider update");
		Validate.notNull(userProviderDto.getUseAccessClaim(), "useAccessClaim is mandatory for user provider update");
		Validate.notNull(userProviderDto.getUseRoleClaim(), "useRoleClaim is mandatory for user provider update");
		Validate.notNull(userProviderDto.getUseEmailLocaleClaim(), "useEmailLocaleClaim is mandatory for user provider update");
		if (oidcUserProviderRepository.isDomainDiscriminatorAlreadyInUse(
				userProviderDto.getDomainDiscriminator(),
				userProvider.getDomain())) {
			throw new BusinessException(BusinessErrorCode.OIDC_USER_PROVIDER_DOMAIN_DISCRIMINATOR_ALREADY_EXISTS, "Domain discriminator should be unique.");
		}
		OIDCUserProvider provider = (OIDCUserProvider) userProvider;
		provider.setDomainDiscriminator(userProviderDto.getDomainDiscriminator());
		provider.setCheckExternalUserID(userProviderDto.getCheckExternalUserID());
		provider.setUseAccessClaim(userProviderDto.getUseAccessClaim());
		provider.setUseRoleClaim(userProviderDto.getUseRoleClaim());
		provider.setUseEmailLocaleClaim(userProviderDto.getUseEmailLocaleClaim());
		return new OIDCUserProviderDto((OIDCUserProvider) userProviderRepository.update(provider));
	}

	private LDAPUserProviderDto updateLdapUserProvider(LDAPUserProviderDto userProviderDto, AbstractDomain domain, UserProvider userProvider) {
		// user filter
		Validate.notNull(userProviderDto.getUserFilter(), "UserFilter is mandatory for user provider update");
		String userFilterUuid = userProviderDto.getUserFilter().getUuid();
		Validate.notEmpty(userFilterUuid, "UserFilter uuid is mandatory for user provider update");
		// ldap connection
		Validate.notNull(userProviderDto.getLdapServer(), "LDAP Connection is mandatory for user provider update");
		String ldapConnectionUuid = userProviderDto.getLdapServer().getUuid();
		Validate.notEmpty(ldapConnectionUuid, "LDAP connection uuid is mandatory for user provider update");
		// baseDn
		String baseDn = userProviderDto.getBaseDn();
		Validate.notEmpty(baseDn, "baseDn is mandatory for user provider update");

		LdapUserProvider provider = (LdapUserProvider) userProvider;
		provider.setBaseDn(baseDn);
		provider.setLdapConnection(ldapConnectionService.find(ldapConnectionUuid));
		provider.setPattern(userProviderService.findDomainPattern(userFilterUuid));
		return new LDAPUserProviderDto(domain, (LdapUserProvider) userProviderRepository.update(provider));
	}

	private TwakeUserProviderDto updateTwakeUserProvider(TwakeUserProviderDto userProviderDto, UserProvider userProvider) {
		Validate.notNull(userProviderDto.getTwakeServer(), "Twake server is mandatory for user provider update");
		Validate.notEmpty(userProviderDto.getTwakeServer().getUuid(), "Twake connection uuid is mandatory for user provider update");
		Validate.notEmpty(userProviderDto.getTwakeCompanyId(), "Twake companyId is mandatory for user provider update");
		TwakeUserProvider provider = (TwakeUserProvider) userProvider;
		provider.setTwakeConnection(provider.getTwakeConnection());
		return new TwakeUserProviderDto((TwakeUserProvider) userProviderRepository.update(provider));
	}

	private TwakeGuestUserProviderDto updateTwakeGuestUserProvider(TwakeGuestUserProviderDto userProviderDto, UserProvider userProvider) {
		Validate.notNull(userProviderDto.getTwakeServer(), "Twake server is mandatory for user provider update");
		Validate.notEmpty(userProviderDto.getTwakeServer().getUuid(), "Twake connection uuid is mandatory for user provider update");
		Validate.notEmpty(userProviderDto.getTwakeCompanyId(), "Twake companyId is mandatory for user provider update");
		TwakeGuestUserProvider provider = (TwakeGuestUserProvider) userProvider;
		provider.setTwakeConnection(provider.getTwakeConnection());
		return new TwakeGuestUserProviderDto((TwakeGuestUserProvider) userProviderRepository.update(provider));
	}

	@Override
	public AbstractUserProviderDto delete(String domainUuid, String uuid, AbstractUserProviderDto dto) {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		AbstractDomain domain = domainService.find(authUser, domainUuid);
		// TODO: we need to put all the following lines of code in a new UserProviderService(s)
		if (Strings.isNullOrEmpty(uuid)) {
			Validate.notNull(dto, "user provider must be set.");
			uuid = dto.getUuid();
			Validate.notEmpty(uuid, "Missing user provider uuid in the payload.");
		}
		UserProvider userProvider = findByService(uuid);
		switch (userProvider.getType()) {
			case LDAP_PROVIDER:
			case OIDC_PROVIDER:
			case TWAKE_PROVIDER:
				if (domain.isRootDomain() || domain.isGuestDomain()) {
					throw new BusinessException(BusinessErrorCode.USER_PROVIDER_FORBIDDEN, "You can not delete an UserProvider for this kind of domain.");
				}
				break;
			case TWAKE_GUEST_PROVIDER:
				if (!domain.isGuestDomain()) {
					throw new BusinessException(BusinessErrorCode.USER_PROVIDER_FORBIDDEN, "You can not delete an UserProvider for this kind of domain.");
				}
				break;
		}
		// no update ? I think implicit opened transaction do the job
		domain.setUserProvider(null);
		userProviderRepository.delete(userProvider);
		return toDto(domain, userProvider);
	}

	private UserProvider findByService(String uuid) {
		UserProvider userProvider = userProviderRepository.findByUuid(uuid);
		if (userProvider == null) {
			throw new BusinessException(BusinessErrorCode.USER_PROVIDER_NOT_FOUND, "UserProvider not found");
		}
		return userProvider;
	}
}
