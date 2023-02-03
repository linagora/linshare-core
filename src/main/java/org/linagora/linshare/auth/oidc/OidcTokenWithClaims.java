package org.linagora.linshare.auth.oidc;

public interface OidcTokenWithClaims {

	OidcLinShareUserClaims getClaims();

	void setClaims(OidcLinShareUserClaims claims);
}
