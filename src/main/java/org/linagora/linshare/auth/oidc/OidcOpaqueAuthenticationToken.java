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

import java.util.Map;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import com.google.common.collect.Maps;

public class OidcOpaqueAuthenticationToken extends AbstractAuthenticationToken implements OidcTokenWithClaims {

	private static final long serialVersionUID = 9181838390481593863L;

	private final String token;

	private Map<String, Object> attributes = Maps.newConcurrentMap();

	private OidcLinShareUserClaims claims;

	public OidcOpaqueAuthenticationToken(String token) {
		super(null);
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return token;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
		this.claims = OidcLinShareUserClaims.fromAttributes(attributes);
	}

	public String get(String key) {
		return (String) attributes.get(key);
	}

	public void put(String key, Object value) {
		if (value != null) {
			attributes.put(key, value);
		}
		this.claims = OidcLinShareUserClaims.fromAttributes(attributes);
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
