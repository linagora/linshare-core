/*
 *    This file is part of Linshare.
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
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.auth;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linagora.linShare.core.domain.entities.Role;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.service.LDAPQueryService;
import org.linagora.linShare.core.service.UserService;
import org.springframework.security.AuthenticationException;
import org.springframework.security.AuthenticationServiceException;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.providers.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.userdetails.UserDetails;

public class DomainAuthProviderDao extends AbstractUserDetailsAuthenticationProvider {
	
	private LDAPQueryService ldapQueryService;
	private UserService userService;
	
    private final static Log logger = LogFactory.getLog(DomainAuthProviderDao.class);
	
	public void setLdapQueryService(LDAPQueryService ldapQueryService) {
		this.ldapQueryService = ldapQueryService;
	}
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
	}

	protected UserDetails retrieveUser(String username,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		
		String login = username;
		String password = (String)authentication.getCredentials();
		String domain = (String)authentication.getDetails();
		User user = null;
		
		try {
			User foundUser = ldapQueryService.auth(login, password, domain);
			if(foundUser == null) {
			      throw new BadCredentialsException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials",
			          "Bad credentials"), domain);
			 } else {
				 user = userService.findAndCreateUser(foundUser.getMail(), domain);
			 }
		} catch (Exception e) {
			logger.error("Could not authenticate user: "+login, e);
			throw new AuthenticationServiceException("Could not authenticate user: "+login, e);
		}

        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new GrantedAuthorityImpl(AuthRole.ROLE_AUTH));
        
        if (user.getRole() == Role.ADMIN) {
            grantedAuthorities.add(new GrantedAuthorityImpl(AuthRole.ROLE_ADMIN));
        }
        
        return new org.springframework.security.userdetails.User(user.getLogin(), "", true, true, true, true,
		                grantedAuthorities.toArray(new GrantedAuthority[0]));
	}

}
