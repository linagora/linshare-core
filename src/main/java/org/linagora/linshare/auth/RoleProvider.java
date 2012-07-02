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

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Role;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

public class RoleProvider {

	public static final List<GrantedAuthority> getRoles(Account account) {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new GrantedAuthorityImpl(AuthRole.ROLE_AUTH));

		if (account.getRole() == Role.ADMIN) {
			grantedAuthorities.add(new GrantedAuthorityImpl(AuthRole.ROLE_ADMIN));
		} else if (account.getRole() == Role.SUPERADMIN) {
			grantedAuthorities.add(new GrantedAuthorityImpl(AuthRole.ROLE_ADMIN));
			grantedAuthorities.add(new GrantedAuthorityImpl(AuthRole.ROLE_SUPERADMIN));
		}
		
		if (!account.getAccountType().equals(AccountType.GUEST)) {
			grantedAuthorities.add(new GrantedAuthorityImpl(AuthRole.ROLE_INTERNAL));
			grantedAuthorities.add(new GrantedAuthorityImpl(AuthRole.ROLE_UPLOAD));
		} else if (account.getAccountType().equals(AccountType.GUEST)){
			Guest guest =(Guest)account;
			if(guest.getCanUpload()) {
			grantedAuthorities.add(new GrantedAuthorityImpl(AuthRole.ROLE_UPLOAD));
			}
		}
		
		return grantedAuthorities;
	}

	public static final List<GrantedAuthority> getRoles(UserVo user) {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new GrantedAuthorityImpl(AuthRole.ROLE_AUTH));

		if (user.isSuperAdmin()) {
			grantedAuthorities.add(new GrantedAuthorityImpl(AuthRole.ROLE_SUPERADMIN));
			grantedAuthorities.add(new GrantedAuthorityImpl(AuthRole.ROLE_ADMIN));
		} else if (user.isAdministrator()) {
			grantedAuthorities.add(new GrantedAuthorityImpl(AuthRole.ROLE_ADMIN));
		}
		
		if (!user.isGuest()) {
			grantedAuthorities.add(new GrantedAuthorityImpl(AuthRole.ROLE_INTERNAL));
			grantedAuthorities.add(new GrantedAuthorityImpl(AuthRole.ROLE_UPLOAD));
		} else if (user.isUpload()) {
			// TODO : Fix this when we will have AccountVo
			grantedAuthorities.add(new GrantedAuthorityImpl(AuthRole.ROLE_UPLOAD));
		}
		
		return grantedAuthorities;
	}

}
