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
package org.linagora.linshare.auth;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class RoleProvider {

	public static final List<GrantedAuthority> getRoles(Account account) {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
		grantedAuthorities.add(new SimpleGrantedAuthority(AuthRole.ROLE_AUTH));

		if (account.hasAdminRole()) {
			// only internal users could be admin.
			if (account.isInternal()) {
				grantedAuthorities.add(new SimpleGrantedAuthority(AuthRole.ROLE_USER));
				grantedAuthorities.add(new SimpleGrantedAuthority(AuthRole.ROLE_ADMIN));
				grantedAuthorities.add(new SimpleGrantedAuthority(AuthRole.ROLE_UPLOAD));
				grantedAuthorities.add(new SimpleGrantedAuthority(AuthRole.ROLE_INTERNAL));
			}
		} else if (account.hasSuperAdminRole()) {
			grantedAuthorities.add(new SimpleGrantedAuthority(AuthRole.ROLE_USER));
			grantedAuthorities.add(new SimpleGrantedAuthority(AuthRole.ROLE_UPLOAD));
			grantedAuthorities.add(new SimpleGrantedAuthority(AuthRole.ROLE_ADMIN));
			grantedAuthorities.add(new SimpleGrantedAuthority(AuthRole.ROLE_SUPERADMIN));
		} else if (account.hasSimpleRole()) {
			grantedAuthorities.add(new SimpleGrantedAuthority(AuthRole.ROLE_USER));
			if (account.isGuest()) {
				Guest guest =(Guest)account;
				if(guest.isCanUpload()) {
					grantedAuthorities.add(new SimpleGrantedAuthority(AuthRole.ROLE_UPLOAD));
				}
			} else if (account.isInternal()) {
				grantedAuthorities.add(new SimpleGrantedAuthority(AuthRole.ROLE_INTERNAL));
				grantedAuthorities.add(new SimpleGrantedAuthority(AuthRole.ROLE_UPLOAD));
			}
		} else if (account.hasDelegationRole()) {
			grantedAuthorities.add(new SimpleGrantedAuthority(AuthRole.ROLE_DELEGATION));
		} else if (account.hasSafeRole()) {
			grantedAuthorities.add(new SimpleGrantedAuthority(AuthRole.ROLE_SAFE));
		}
		return grantedAuthorities;
	}
}
