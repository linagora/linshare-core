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
package org.linagora.linshare.auth.details.service;

import java.util.List;

import org.linagora.linshare.auth.RoleProvider;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DatabaseUserDetailsServiceImpl implements UserDetailsService {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseUserDetailsServiceImpl.class);

	protected UserRepository<User> userRepository;

	public DatabaseUserDetailsServiceImpl(UserRepository<User> userRepository) {
		super();
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.debug("Looking for {}", username);
		User user = userRepository.findByLogin(username);
		if (user == null) {
			throw new UsernameNotFoundException("Account not found");
		}
		logger.debug("User found {}", user);
		if (user.isGuest() || user.isRoot() || user.isTechnicalAccount()) {
			return toUserDetail(user);
		}
		// TODO: Maybe we need to stop ProviderManager to look for a other
		// AuthenticationProvider ?
		// User is found but not handled bu the current DatabaseAuthenticationProvider
		throw new UsernameNotFoundException("Account not found");
	}
	
	private UserDetails toUserDetail(User user) {
		List<GrantedAuthority> grantedAuthorities = RoleProvider.getRoles(user);
		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked =  !user.isLocked();
		UserDetails loadedUser = new org.springframework.security.core.userdetails.User(
				user.getLsUuid(),
				user.getPassword(),
				enabled,
				accountNonExpired,
				credentialsNonExpired,
				accountNonLocked,
				grantedAuthorities);
		return loadedUser;
	}
}
