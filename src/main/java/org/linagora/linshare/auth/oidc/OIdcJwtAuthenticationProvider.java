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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;

import java.text.ParseException;

public class OIdcJwtAuthenticationProvider implements AuthenticationProvider {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	OidcAuthenticationTokenDetailsFactory oidcAuthenticationTokenDataFactory;

	private String issuerUri;

	JwtDecoder jwtDecoder;

	public OIdcJwtAuthenticationProvider(OidcAuthenticationTokenDetailsFactory oidcAuthenticationTokenDataFactory,
										 String issuerUri,
										 Boolean useOIDC) {
		this.issuerUri = issuerUri;
		if (useOIDC) {
			this.oidcAuthenticationTokenDataFactory = oidcAuthenticationTokenDataFactory;
			this.jwtDecoder = JwtDecoders.fromOidcIssuerLocation(issuerUri);
		}
	}

	/**
	 *
	 * @param authentication
	 * @return
	 * @throws AuthenticationException
	 */
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		// Quick workaround: Not very nice code. :(
		logger.debug("Starting authentication process with OidcOpaqueAuthenticationProvider");
		if (issuerUri.endsWith("/")) {
			logger.warn("'issuerUri' ends with '/' character, might leads to connection issue !");
		}
		//check that auth token is valid, and as far we don't use eny custom scopes except the default one, do nothing
		OidcJwtAuthenticationToken jwtAuthenticationToken = (OidcJwtAuthenticationToken) authentication;

		jwtDecoder.decode(jwtAuthenticationToken.getAuthToken());
		Jwt idToken = jwtDecoder.decode(jwtAuthenticationToken.getIdToken());
		logger.debug("OIDC JWT access token seems to be good. Processing authentication...");

		OidcLinShareUserClaims claims = OidcLinShareUserClaims.fromAttributes(idToken.getClaims());
		logger.trace("claims: {}", claims);

		//not very clean why we are doing it, but we need to pass this attributes to SecurityContext
		// ot use them for example here org.linagora.linshare.core.service.impl.UserProviderServiceImpl
		//TODO[ASH] refactor this, and and just spring authorities and remove all isAssignableFrom logic
		jwtAuthenticationToken.setClaims(claims);

		UsernamePasswordAuthenticationToken authenticationToken = oidcAuthenticationTokenDataFactory.getAuthenticationToken(claims);
		return authenticationToken;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (OidcJwtAuthenticationToken.class).isAssignableFrom(authentication);
	}

}
