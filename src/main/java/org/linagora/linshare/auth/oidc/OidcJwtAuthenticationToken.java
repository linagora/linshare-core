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

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class OidcJwtAuthenticationToken extends AbstractAuthenticationToken implements OidcTokenWithClaims {

	private String authToken;

	private String idToken;

	private OidcLinShareUserClaims claims;

	public OidcJwtAuthenticationToken(String authToken, String idToken) {
		super(null);
		this.authToken = authToken;
		this.idToken = idToken;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return null;
	}

	public String getIdToken() {
		return idToken;
	}

	public String getAuthToken() {
		return authToken;
	}

	@Override
	public OidcLinShareUserClaims getClaims() {
		return claims;
	}

	@Override
	public void setClaims(OidcLinShareUserClaims claims) {
		this.claims = claims;
	}
}
