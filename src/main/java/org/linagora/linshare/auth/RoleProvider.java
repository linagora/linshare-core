/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
				if(guest.getCanUpload()) {
					grantedAuthorities.add(new SimpleGrantedAuthority(AuthRole.ROLE_UPLOAD));
				}
			} else if (account.isInternal()) {
				grantedAuthorities.add(new SimpleGrantedAuthority(AuthRole.ROLE_INTERNAL));
				grantedAuthorities.add(new SimpleGrantedAuthority(AuthRole.ROLE_UPLOAD));
			}
		} else if (account.hasDelegationRole()) {
			grantedAuthorities.add(new SimpleGrantedAuthority(AuthRole.ROLE_DELEGATION));
		} else if (account.hasUploadPropositionRole()) {
			grantedAuthorities.add(new SimpleGrantedAuthority(AuthRole.ROLE_UPLOAD_PROPOSTION));
		}
		return grantedAuthorities;
	}
}
