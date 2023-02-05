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
package org.linagora.linshare.auth.rememberme;

import java.util.List;

import org.linagora.linshare.auth.RoleProvider;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class RememberMeAccountProvider implements UserDetailsService {

	private final AccountService accountService;
	private static Logger logger = LoggerFactory
			.getLogger(RememberMeAccountProvider.class);

	public RememberMeAccountProvider(AccountService accountService) {
		super();
		this.accountService = accountService;
	}

	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException, DataAccessException {
		if (username == null || username.length() == 0)
			throw new UsernameNotFoundException("username must not be null");
		logger.debug("Trying to load '" + username + "' account detail ...");

		Account account = accountService.findByLsUuid(username);

		if (account != null) {
			logger.debug("Account in database found : "
					+ account.getAccountRepresentation());
		}
		if (account == null || Role.SYSTEM.equals(account.getRole())) {
			logger.debug("throw UsernameNotFoundException: Account not found");
			throw new UsernameNotFoundException("Account not found");
		}

		List<GrantedAuthority> grantedAuthorities = RoleProvider
				.getRoles(account);

		return new User(account.getLsUuid(), "", true, true, true, true,
				grantedAuthorities);
	}

}
