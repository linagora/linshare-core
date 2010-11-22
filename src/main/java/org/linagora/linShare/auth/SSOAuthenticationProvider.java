/*
 *    This file is part of Linshare. Initial work has been done by
 *    C. Oudot on LinID Directory Manager project
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2010 Groupe Linagora - http://linagora.org
 *
 */
package org.linagora.linShare.auth;

import java.text.MessageFormat;

import javax.naming.directory.Attributes;

import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.GrantedAuthority;
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
			return super.authenticate(authentication);
		}
		
		final String userName = (String)authentication.getPrincipal();
		final String myUserDN = MessageFormat.format(userDN, new Object[]{userName});
		
		LdapUserDetails user = new LdapUserDetails(){

			private static final long serialVersionUID = -2176920273295585699L;

			public Attributes getAttributes() {
				return null;
			}

			public String getDn() {
				return myUserDN;
			}

			public GrantedAuthority[] getAuthorities() {
				return getAuthoritiesPopulator().getGrantedAuthorities(null, userName);
			}

			public String getPassword() {
				return null;
			}

			public String getUsername() {
				return userName;
			}

			public boolean isAccountNonExpired() {
				return false;
			}

			public boolean isAccountNonLocked() {
				return false;
			}

			public boolean isCredentialsNonExpired() {
				return false;
			}

			public boolean isEnabled() {
				return true;
			}
		};
		
		// Set fake user for pre-authentication
		return new UsernamePasswordAuthenticationToken(user, 
				authentication.getCredentials(), user.getAuthorities() ); 
	}

	@SuppressWarnings("unchecked")
	public boolean supports(Class authentication) {
		return ((org.springframework.security.providers.UsernamePasswordAuthenticationToken.class).isAssignableFrom(authentication)
				|| (org.springframework.security.providers.preauth.PreAuthenticatedAuthenticationToken.class).isAssignableFrom(authentication));
	}
}
