/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.auth;

import java.util.List;

import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.entities.Role;
import org.linagora.linshare.core.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;



/** Helps Managing guests and root administrator authentication.
 *
 */
public class DaoAuthProvider implements UserDetailsService {

    private final AccountService accountService;
    private static Logger logger = LoggerFactory.getLogger(DaoAuthProvider.class);


    public DaoAuthProvider(AccountService accountService) {
		super();
		this.accountService = accountService;
	}


	/*
     * In this method, we try to load user details from the database.
     * We are four data types :
     *  - LdapUser : Just a profile : first name, last name, role, domain_id.
     *  - Guest : first name, last name, role, domain_id, user_owner, password, expiration date, ...
     *  - System : useless, we can't login with this account.
     *  - Root : super admin. His credential are stored in the database.
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {

        if (username == null || username.length() == 0) {
            throw new UsernameNotFoundException("username must not be null");
        }
        logger.debug("Trying to load '" + username +"' account detail ...");

        // TODO manage domain for guest 
        org.linagora.linshare.core.domain.entities.Account account = accountService.findByLsUid(username);
        String password = null ;
        if(account != null) {
        	logger.debug("Account in database found : " + account.getLsUuid());
        	password = account.getPassword();
        
        	// If the password field is not set (only Ldap user), we set it to an empty string.
        	// XXX : code etrange, a verifier
        	if (!AccountType.INTERNAL.equals(account) && password==null) password=""; 
        }
		
        if (account == null || password == null || Role.SYSTEM.equals(account.getRole())) {
        	logger.debug("throw UsernameNotFoundException: Account not found");
            throw new UsernameNotFoundException("Account not found");
        }

        List<GrantedAuthority> grantedAuthorities = RoleProvider.getRoles(account);

        return new User(account.getLsUuid(), password, true, true, true, true,
            grantedAuthorities.toArray(new GrantedAuthority[0]));
    }

}
