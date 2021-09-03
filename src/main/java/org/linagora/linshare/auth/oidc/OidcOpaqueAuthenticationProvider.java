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
package org.linagora.linshare.auth.oidc;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.auth.AuthRole;
import org.linagora.linshare.auth.RoleProvider;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.auth.AuthentificationFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.OIDCUserProviderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistration.Builder;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

public class OidcOpaqueAuthenticationProvider implements AuthenticationProvider {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	private AuthentificationFacade authentificationFacade;

	private OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;

	private String clientId;

	private String clientSecret;

	private String issuerUri;

	private String linshareAccessClaimValue;

	public OidcOpaqueAuthenticationProvider(AuthentificationFacade authentificationFacade, Boolean useOIDC,
			String issuerUri, String clientId, String clientSecret, String linshareAccessClaimValue) {
		super();
		this.authentificationFacade = authentificationFacade;
		this.linshareAccessClaimValue = linshareAccessClaimValue;
		if (useOIDC) {
			Validate.notEmpty(clientId, "Missing OIDC client ID");
			Validate.notEmpty(clientSecret, "Missing OIDC client secret");
			Validate.notEmpty(issuerUri, "Missing OIDC issuer Uri");
			this.clientId = clientId;
			this.clientSecret = clientSecret;
			this.issuerUri = issuerUri;
			this.oAuth2UserService = new DefaultOAuth2UserService();
		}
	}

	private AuthenticationProvider getOpaqueTokenAuthenticationProvider(ClientRegistration clientRegistration, String clientId, String clientSecret) {
		Object introspectionUri = clientRegistration.getProviderDetails().getConfigurationMetadata().get("introspection_endpoint");
		Validate.notNull(introspectionUri);
		Validate.notEmpty((String)introspectionUri, "Can not get introspection_endpoint from OIDC provider.");
		OpaqueTokenIntrospector introspector = new NimbusOpaqueTokenIntrospector((String)introspectionUri, clientId, clientSecret);
		return new OpaqueTokenAuthenticationProvider(introspector);
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		// Quick workaround: Not very nice code. :(
		OidcOpaqueAuthenticationToken jwtAuthentication = (OidcOpaqueAuthenticationToken) authentication;
		final String token = jwtAuthentication.getToken();
		BearerTokenAuthenticationToken authToken = new BearerTokenAuthenticationToken(token);

		ClientRegistration clientRegistration = getClientRegistration();
		AuthenticationProvider opaqueTokenAuthenticationProvider = getOpaqueTokenAuthenticationProvider(clientRegistration, clientId, clientSecret);

		BearerTokenAuthentication authenticate = (BearerTokenAuthentication) opaqueTokenAuthenticationProvider
				.authenticate(authToken);
		logger.debug("OIDC opaque access token seems to be good. Processing authentication...");

		OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, token, null, null);
		OAuth2UserRequest req = new OAuth2UserRequest(clientRegistration, accessToken);
		OAuth2User loadUser = oAuth2UserService.loadUser(req);

		String email = authenticate.getName();
		Validate.notEmpty(email, "Missing email value in claims.");
		String domainDiscriminator = loadUser.getAttribute("domain_discriminator");
		jwtAuthentication.put("email", email);
		jwtAuthentication.put("domain_discriminator", domainDiscriminator);
		Optional<OIDCUserProviderDto> providerDto = Optional.empty();
		User foundUser = null;
		// looking in the db first.
		if (domainDiscriminator != null) {
			providerDto = Optional.of(authentificationFacade.findOidcProvider(domainDiscriminator));
			if (providerDto.get().getUseAccessClaim() ) {
				String allowed = loadUser.getAttribute("linshare_access");
				Validate.notEmpty(allowed, "Missing access value in claims.");
				if (allowed.toLowerCase().equals(linshareAccessClaimValue)) {
					logger.warn("Unauthorized access attempt : " + email);
					throw new UsernameNotFoundException("You are not allowed to use this service.");
				}
			}
			foundUser = authentificationFacade.findByLoginAndDomain(providerDto.get().getDomain().getUuid(), email);
			if (foundUser == null) {
				// looking through user provider.
				foundUser = authentificationFacade.ldapSearchForAuth(providerDto.get().getDomain().getUuid(), email);
			}
		} else {
			foundUser = authentificationFacade.findByLogin(email);
			if (foundUser == null) {
				// looking through user providers.
				List<String> domains = authentificationFacade.getAllDomains();
				for (String domain : domains) {
					foundUser = authentificationFacade.ldapSearchForAuth(domain, email);
					if (foundUser != null) {
						break;
					}
				}
			}
		}
		if (foundUser == null) {
			logger.error("User not found: " + token);
			throw new UsernameNotFoundException("Could not find user account : " + authenticate.getTokenAttributes().toString());
		}
		try {
			if(providerDto.isPresent()) {
				// It means we are using a OIDC user provider, so we need to provide some extra properties to allow on-the-fly creation.
				// It won't be use if the profile already exists.
				jwtAuthentication.put("first_name", loadUser.getAttribute("name"));
				jwtAuthentication.put("last_name", loadUser.getAttribute("family_name"));
				jwtAuthentication.put("external_uid", loadUser.getAttribute("external_uid"));
				jwtAuthentication.put("linshare_locale", loadUser.getAttribute("linshare_locale"));
				jwtAuthentication.put("linshare_role", loadUser.getAttribute("linshare_role"));
			}
			// loading/creating the real entity
			foundUser = authentificationFacade.findOrCreateUser(foundUser.getDomainId(), foundUser.getMail());
		} catch (BusinessException e) {
			logger.error(e.getMessage(), e);
			throw new AuthenticationServiceException(
					"Could not create user account : "
					+ foundUser.getDomainId() + " : "
					+ foundUser.getMail(), e);
		}

		logger.info(String.format("Successful authentication of  %1$s with OIDC opaque token : %2$s", foundUser.getLsUuid(), authenticate.getTokenAttributes().toString()));
		List<GrantedAuthority> grantedAuthorities = RoleProvider.getRoles(foundUser);
		grantedAuthorities.add(new SimpleGrantedAuthority(AuthRole.ROLE_AUTH_OIDC));
		UserDetails userDetail = new org.springframework.security.core.userdetails.User(foundUser.getLsUuid(), "", true,
				true, true, true, grantedAuthorities);

		return new UsernamePasswordAuthenticationToken(userDetail, jwtAuthentication.getCredentials(), grantedAuthorities);
	}

	private ClientRegistration getClientRegistration() {
		Builder clientRegistrationsBuilder = ClientRegistrations
				.fromOidcIssuerLocation(issuerUri)
				.clientId(clientId)
				.clientSecret(clientSecret);
		// Do not bother to add scopes here, they must be provided at authentication time, see the client(ex ui-user)
		return clientRegistrationsBuilder.build();
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (OidcOpaqueAuthenticationToken.class).isAssignableFrom(authentication);
	}

}
