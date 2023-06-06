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
import org.linagora.linshare.core.facade.auth.AuthentificationFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class LdapUserDetailsServiceImpl implements UserDetailsService {

	private static final Logger logger = LoggerFactory.getLogger(LdapUserDetailsServiceImpl.class);

	protected AuthentificationFacade authentificationFacade;

	public LdapUserDetailsServiceImpl(AuthentificationFacade authentificationFacade) {
		super();
		this.authentificationFacade = authentificationFacade;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.debug("Looking for {}", username);
		User user = authentificationFacade.findByLogin(username);
		if (user != null) {
			logger.debug("User found {}", user);
			if (user.isInternal()) {
				user = authentificationFacade.checkStillInLdap(user, username);
				if (user != null) {
					return toUserDetail(user);
				}
			}
			throw new UsernameNotFoundException("Account not found");
		}
		logger.debug("Can't find the user in DB. Searching user in all LDAP domains.");
		User transientUser = null;
		String transientDomainUuid = null;
		for (String domainUuid : authentificationFacade.getAllDomains()) {
			transientUser = authentificationFacade.userProviderSearchForAuth(domainUuid, username);
			if (transientUser != null) {
				transientDomainUuid = domainUuid;
				logger.debug("User found in domain " + transientDomainUuid);
				break;
			}
		}
		if (transientUser != null) {
			// need to persist
			user = authentificationFacade.findOrCreateUser(transientDomainUuid, transientUser.getMail());
		}
		if (user == null) {
			String message = "User not found ! Login : " + username;
			throw new UsernameNotFoundException(message);
		}
		logger.debug("User found in ldap : " + user.getAccountRepresentation() + " (domain:" + user.getDomainId() + ")");
		return toUserDetail(user);
	}

	private UserDetails toUserDetail(User user) {
		List<GrantedAuthority> grantedAuthorities = RoleProvider.getRoles(user);
		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = !user.isLocked();
		UserDetails loadedUser = new org.springframework.security.core.userdetails.User(
				user.getLsUuid(),
				"", // Passwords of LDAP users are not store in the database. using empty string, null is not accepted.
				enabled,
				accountNonExpired,
				credentialsNonExpired,
				accountNonLocked,
				grantedAuthorities);
		return loadedUser;
	}
}
