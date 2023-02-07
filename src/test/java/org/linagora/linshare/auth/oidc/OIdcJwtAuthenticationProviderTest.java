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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.auth.jwt.JwtAuthenticationToken;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({ MockitoExtension.class })
class OIdcJwtAuthenticationProviderTest {

	OidcAuthenticationTokenDetailsFactory oidcAuthenticationTokenDetailsFactoryMock = mock(OidcAuthenticationTokenDetailsFactory.class);
	static MockedStatic<OidcLinShareUserClaims> claimsMockedStatic = Mockito.mockStatic(OidcLinShareUserClaims.class);

	private OIdcJwtAuthenticationProvider subj =
			new OIdcJwtAuthenticationProvider(oidcAuthenticationTokenDetailsFactoryMock,
					"https://linshare.org", false);

	private String authToken = "idToken";
	private String idToken = "authToken";


	@BeforeEach
	void init() {
		subj.oidcAuthenticationTokenDataFactory = oidcAuthenticationTokenDetailsFactoryMock;
		subj.jwtDecoder = mock(JwtDecoder.class);
	}

	@Test
	public void testAuthenticateSmoke() {
		//given jwt token
		OidcJwtAuthenticationToken token = new OidcJwtAuthenticationToken(authToken, idToken);
		OidcLinShareUserClaims claims = newClaims();
		when(subj.jwtDecoder.decode(idToken)).thenReturn(mock(Jwt.class));
		when(subj.jwtDecoder.decode(authToken)).thenReturn(mock(Jwt.class));

		//when
		claimsMockedStatic.when(() -> OidcLinShareUserClaims.fromAttributes(any())).thenReturn(claims);
		when(oidcAuthenticationTokenDetailsFactoryMock.getAuthenticationToken(claims)).thenReturn(any());
		subj.authenticate(token);

		//then
		Assertions.assertEquals(claims, token.getClaims());
	}

	private OidcLinShareUserClaims newClaims() {
		OidcLinShareUserClaims claims = mock(OidcLinShareUserClaims.class);
		claimsMockedStatic.when(() -> OidcLinShareUserClaims.fromAttributes(any())).thenReturn(claims);
		return claims;
	}

	@Test
	public void testSupport() {
		assertTrue(subj.supports(OidcJwtAuthenticationToken.class),
				"OidcJwtAuthenticationToken should be supported!");
		assertFalse(subj.supports(OidcOpaqueAuthenticationToken.class),
				"OidcOpaqueAuthenticationToken shouldn't be supported!");
		assertFalse(subj.supports(JwtAuthenticationToken.class),
				"JwtAuthenticationToken shouldn't be supported!");
		assertFalse(subj.supports(org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken.class),
				"org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken shouldn't be supported!");
	}

}