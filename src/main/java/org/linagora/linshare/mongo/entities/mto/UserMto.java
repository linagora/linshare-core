/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2017. Contribute to
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
package org.linagora.linshare.mongo.entities.mto;

import java.util.Date;

import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;

public class UserMto extends AccountMto {

	protected String ldapUid;

	protected Boolean canUpload;

	protected Boolean inconsistent;

	protected Boolean canCreateGuest;

	protected boolean restricted;

	protected Date expirationDate;

	public UserMto() {
	}

	public UserMto(User user) {
		super(user);
		this.ldapUid = user.getLdapUid();
		this.canUpload = user.getCanUpload();
		this.inconsistent = user.isInconsistent();
		this.canCreateGuest = user.getCanCreateGuest();
		if (user instanceof Guest) {
			Guest guest = (Guest) user;
			this.restricted = guest.isRestricted();
			this.expirationDate = guest.getExpirationDate();
		}
	}

	public UserMto(Guest user) {
		this.uuid = user.getLsUuid();
		this.domain = new DomainMto(user.getDomain());
		this.mail = user.getMail();
		this.name = user.getFullName();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.role = user.getRole();
		this.accountType = user.getAccountType();
		this.ldapUid = user.getLdapUid();
		this.canUpload = user.getCanUpload();
		this.inconsistent = user.isInconsistent();
		this.canCreateGuest = user.getCanCreateGuest();
		this.restricted = user.isRestricted();
		this.expirationDate = user.getExpirationDate();
	}

	public String getLdapUid() {
		return ldapUid;
	}

	public void setLdapUid(String ldapUid) {
		this.ldapUid = ldapUid;
	}

	public Boolean getCanUpload() {
		return canUpload;
	}

	public void setCanUpload(Boolean canUpload) {
		this.canUpload = canUpload;
	}

	public Boolean getInconsistent() {
		return inconsistent;
	}

	public void setInconsistent(Boolean inconsistent) {
		this.inconsistent = inconsistent;
	}

	public Boolean getCanCreateGuest() {
		return canCreateGuest;
	}

	public void setCanCreateGuest(Boolean canCreateGuest) {
		this.canCreateGuest = canCreateGuest;
	}

	public boolean isRestricted() {
		return restricted;
	}

	public void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
}
