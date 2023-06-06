/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.auth.oidc;

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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

	private OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;

	private String clientId;

	private String clientSecret;

	private String issuerUri;

	private OidcAuthenticationTokenDetailsFactory oidcAuthenticationTokenDetailsFactory;

	public OidcOpaqueAuthenticationProvider(OidcAuthenticationTokenDetailsFactory oidcAuthenticationTokenDataFabric,
											Boolean useOIDC, String issuerUri, String clientId, String clientSecret) {
		super();
		this.oidcAuthenticationTokenDetailsFactory = oidcAuthenticationTokenDataFabric;
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
		logger.debug("Starting authentication process with OidcOpaqueAuthenticationProvider");
		if (issuerUri.endsWith("/")) {
			logger.warn("'issuerUri' ends with '/' character, might leads to connection issue !");
		}
		final String token = ((OidcOpaqueAuthenticationToken) authentication).getToken();
		OidcLinShareUserClaims claims = getOidcLinShareUserClaims(token);
		return oidcAuthenticationTokenDetailsFactory.getAuthenticationToken(claims);
	}

	@NotNull
	private OidcLinShareUserClaims getOidcLinShareUserClaims(String token) {
		BearerTokenAuthenticationToken authToken = new BearerTokenAuthenticationToken(token);

		ClientRegistration clientRegistration = getClientRegistration();
		logger.debug("clientRegistration used " + clientRegistration);
		AuthenticationProvider opaqueTokenAuthenticationProvider = getOpaqueTokenAuthenticationProvider(clientRegistration, clientId, clientSecret);

		BearerTokenAuthentication authenticate = (BearerTokenAuthentication) opaqueTokenAuthenticationProvider
				.authenticate(authToken);
		logger.debug("OIDC opaque access token seems to be good. Processing authentication...");
		logger.trace("sub: {}", authenticate.getName());

		OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, token, null, null);
		OAuth2UserRequest req = new OAuth2UserRequest(clientRegistration, accessToken);
		OAuth2User loadUser = oAuth2UserService.loadUser(req);
		logger.trace("sub: {}", authenticate.getName());
		logger.trace("claims: {}", loadUser.getAttributes().toString());

		return OidcLinShareUserClaims.fromOAuth2User(loadUser);
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
