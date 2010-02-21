package org.linagora.linShare.auth;

import java.text.MessageFormat;

import javax.naming.directory.Attributes;

import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.ldap.LdapAuthoritiesPopulator;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.providers.ldap.LdapAuthenticationProvider;
import org.springframework.security.providers.ldap.LdapAuthenticator;
import org.springframework.security.userdetails.ldap.LdapUserDetails;


public class SSOAuthenticationProvider extends LdapAuthenticationProvider {
	
	private String userDN = null;
	
	public SSOAuthenticationProvider(LdapAuthenticator authenticator) {
		super(authenticator);
	}

	public SSOAuthenticationProvider(LdapAuthenticator authenticator,
			LdapAuthoritiesPopulator authoritiesPopulator) {
		super(authenticator, authoritiesPopulator);
	}

	public SSOAuthenticationProvider(LdapAuthenticator authenticator,
			LdapAuthoritiesPopulator authoritiesPopulator, String dn) {
		super(authenticator, authoritiesPopulator);
		userDN = dn;
	}

	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		if (authentication instanceof org.springframework.security.providers.UsernamePasswordAuthenticationToken) {
			// Default authentication
			return super.authenticate(authentication);
		} else {
			// Pre-authentification
			final String userName = (String)authentication.getPrincipal();
			final String myUserDN = MessageFormat.format(userDN, new Object[]{userName});
			LdapUserDetails user = new LdapUserDetails(){

				private static final long serialVersionUID = -2176920273295585699L;

				public Attributes getAttributes() {
					
					return null;
				}

				public String getDn() {
					// TODO Auto-generated method stub
					return myUserDN;
				}

				public GrantedAuthority[] getAuthorities() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getPassword() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getUsername() {
					// TODO Auto-generated method stub
					return userName;
				}

				public boolean isAccountNonExpired() {
					// TODO Auto-generated method stub
					return false;
				}

				public boolean isAccountNonLocked() {
					// TODO Auto-generated method stub
					return false;
				}

				public boolean isCredentialsNonExpired() {
					// TODO Auto-generated method stub
					return false;
				}

				public boolean isEnabled() {
					// TODO Auto-generated method stub
					return true;
				}};
			// Get role
			GrantedAuthorityImpl[] granted = new GrantedAuthorityImpl[] {
					new GrantedAuthorityImpl("ROLE_ANONYMOUS")
			};
			
			// Set fake user for pre-authentication
			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
					user, authentication.getCredentials(), granted ); 
			return auth;
		}
	}

	@SuppressWarnings("unchecked")
	public boolean supports(Class authentication) {
		return ((org.springframework.security.providers.UsernamePasswordAuthenticationToken.class).isAssignableFrom(authentication)
				|| (org.springframework.security.providers.preauth.PreAuthenticatedAuthenticationToken.class).isAssignableFrom(authentication));
	}
}
