package org.linagora.linshare.auth.oidc;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;

import java.text.ParseException;

public class OIdcJwtAuthenticationProvider implements AuthenticationProvider {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	private OidcAuthenticationTokenDetailsFactory oidcAuthenticationTokenDataFactory;

	private String issuerUri;

	private JwtAuthenticationProvider jwtAuthenticationProvider;

	private NimbusJwtDecoder jwtDecoder;

	//TODO hack until we fix sertificate validation for Microsoft Azure. Not for Prod!!!
	public static class DummyJWTProcessor extends DefaultJWTProcessor {
		@Override
		public JWTClaimsSet process(SignedJWT signedJWT, SecurityContext context) throws BadJOSEException, JOSEException {
			try {
				return signedJWT.getJWTClaimsSet();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public OIdcJwtAuthenticationProvider(OidcAuthenticationTokenDetailsFactory oidcAuthenticationTokenDataFactory,
										 String issuerUri,
										 Boolean useOIDC) {
		this.issuerUri = issuerUri;
		if (useOIDC) {
			this.oidcAuthenticationTokenDataFactory = oidcAuthenticationTokenDataFactory;
			jwtDecoder = new NimbusJwtDecoder(new DummyJWTProcessor());
			jwtDecoder.setJwtValidator((OAuth2TokenValidator) token -> OAuth2TokenValidatorResult.success());
//			JwtDecoder jwtDecoder = JwtDecoders.fromOidcIssuerLocation(issuerUri);
			jwtAuthenticationProvider = new JwtAuthenticationProvider(jwtDecoder);
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
		final String token = ((OidcJwtAuthenticationToken) authentication).getAuthToken();
		BearerTokenAuthenticationToken authToken = new BearerTokenAuthenticationToken(token);

		Authentication authenticate = jwtAuthenticationProvider.authenticate(authToken);

		Jwt idToken = jwtDecoder.decode(((OidcJwtAuthenticationToken) authentication).getIdToken());

		logger.debug("OIDC opaque access token seems to be good. Processing authentication...");
		logger.trace("sub: {}", authenticate.getName());

		OidcLinShareUserClaims claims = OidcLinShareUserClaims.fromAttributes(idToken.getClaims());
		logger.trace("claims: {}", claims);

		((OidcJwtAuthenticationToken) authentication).setClaims(claims);

		UsernamePasswordAuthenticationToken authenticationToken = oidcAuthenticationTokenDataFactory.getAuthenticationToken(claims);
		return authenticationToken;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (OidcJwtAuthenticationToken.class).isAssignableFrom(authentication);
	}

}
