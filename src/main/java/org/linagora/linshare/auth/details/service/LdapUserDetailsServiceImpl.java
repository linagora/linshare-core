/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
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
			transientUser = authentificationFacade.ldapSearchForAuth(domainUuid, username);
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
