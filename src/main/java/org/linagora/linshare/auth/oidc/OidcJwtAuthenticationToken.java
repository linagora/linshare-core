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
