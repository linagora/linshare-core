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
package org.linagora.linshare.auth;

import java.util.List;

import org.linagora.linshare.core.domain.entities.Role;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsProvider {
	
	private UserService userService;
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public UserDetails getUserDetails(String userName) {
		org.linagora.linshare.core.domain.entities.User user;
		try {
			user = userService.searchAndCreateUserEntityFromUnkownDirectory(userName);
		} catch (TechnicalException e) {
			throw new UsernameNotFoundException("Cannot load user in domains", e.getCause());
		} catch (BusinessException e) {
			throw new UsernameNotFoundException("Cannot load user in domains", e.getCause());
		}

        if (user == null ||  Role.SYSTEM.equals(user.getRole())) {
            throw new UsernameNotFoundException("User not found");
        }

        List<GrantedAuthority> grantedAuthorities = RoleProvider.getRoles(user);
        
        return new User(user.getLogin(), "", true, true, true, true,
            grantedAuthorities.toArray(new GrantedAuthority[0]));
	}

}
