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
		this.canUpload = user.isCanUpload();
		this.inconsistent = user.isInconsistent();
		this.canCreateGuest = user.isCanCreateGuest();
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
		this.canUpload = user.isCanUpload();
		this.inconsistent = user.isInconsistent();
		this.canCreateGuest = user.isCanCreateGuest();
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
