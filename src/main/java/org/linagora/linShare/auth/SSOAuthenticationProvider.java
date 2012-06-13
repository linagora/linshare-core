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

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class SSOAuthenticationProvider implements AuthenticationProvider {
	
	private UserDetailsProvider userDetailsProvider;
	
	public void setUserDetailsProvider(UserDetailsProvider userDetailsProvider) {
		this.userDetailsProvider = userDetailsProvider;
	}

	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		
		final String userName = (String)authentication.getPrincipal();
		
		UserDetails user = userDetailsProvider.getUserDetails(userName);
		
		return new UsernamePasswordAuthenticationToken(user, 
				authentication.getCredentials(), user.getAuthorities() ); 
	}

	@SuppressWarnings("rawtypes")
	public boolean supports(Class authentication) {
		return (PreAuthenticatedAuthenticationToken.class).isAssignableFrom(authentication);
	}
}
