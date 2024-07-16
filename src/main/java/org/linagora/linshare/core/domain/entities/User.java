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
package org.linagora.linshare.core.domain.entities;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDto;

public abstract class User extends Account {

	protected Set<WorkgroupMember> myWorkgroupMembers = new java.util.HashSet<WorkgroupMember>();

	protected String firstName;

	protected String lastName;

	// NOT IMPLEMENTED YET
	protected Date notAfter;

	// NOT IMPLEMENTED YET
	protected Date notBefore;

	protected String ldapUid;

	protected Boolean canUpload;

	protected Boolean inconsistent;

	/** If the user is allowed to create guest */
	protected Boolean canCreateGuest;

	public User() {
		this.firstName = null;
		this.lastName = null;
		this.setMail(null);

		this.lsUuid = null;
		this.creationDate = new Date();
		this.modificationDate = new Date();
		this.role = Role.SIMPLE;
		this.enable = true;
		this.destroyed = 0;
		this.canUpload = true;
		this.canCreateGuest = true;
		this.mailLocale = Language.ENGLISH;
		this.externalMailLocale = Language.ENGLISH;
	}

	public User(String firstName, String lastName, String mail) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.setMail(mail);
		this.lsUuid = null;
		this.creationDate = new Date();
		this.modificationDate = new Date();
		this.role = Role.SIMPLE;
		this.enable = true;
		this.destroyed = 0;
		this.canUpload = true;
		this.canCreateGuest = true;
		this.mailLocale = Language.ENGLISH;
		this.externalMailLocale = Language.ENGLISH;
	}

	public User(final String firstName, final String lastName, final String mail,final String lsUuid) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.setMail(mail);
		this.lsUuid = lsUuid;
		this.creationDate = new Date();
		this.modificationDate = new Date();
		this.role = Role.SIMPLE;
		this.enable = true;
		this.destroyed = 0;
		this.canUpload = true;
		this.canCreateGuest = true;
		this.mailLocale = Language.ENGLISH;
		this.externalMailLocale = Language.ENGLISH;
	}

	public User(UserDto userDto) {
		this.lsUuid = userDto.getUuid();
		this.firstName = userDto.getFirstName();
		this.lastName = userDto.getLastName();
		this.setMail(userDto.getMail());
		this.role = Role.valueOf(userDto.getRole());
		this.canUpload = userDto.getCanUpload();
		this.canCreateGuest = userDto.getCanCreateGuest();
		this.mailLocale = userDto.getExternalMailLocale();
	}

	@Override
	public String getFullName() {
		StringBuffer b = new StringBuffer();
		boolean bf = (firstName != null && !firstName.equals(""));
		boolean bl = (lastName != null && !lastName.equals(""));
		if (bf || bl) {
			if (bf) {
				b.append(firstName);
				b.append(" ");
			}
			if (bl) {
				b.append(lastName);
			}
		} else {
			b.append(getMail());
		}
		return b.toString();
	}

	@Override
	public ContainerQuotaType getContainerQuotaType() {
		return ContainerQuotaType.USER;
	}

	public void setFirstName(String value) {
		this.firstName = StringUtils.capitalize(value);
	}

	public void setBusinessFirstName(String value) {
		if (value != null) {
			this.firstName = StringUtils.capitalize(value);
		}
	}

	public String getFirstName() {
		return firstName;
	}

	public void setLastName(String value) {
		this.lastName = StringUtils.capitalize(value);
	}

	public void setBusinessLastName(String value) {
		if (value != null) {
			this.lastName = StringUtils.capitalize(value);
		}
	}

	public String getLastName() {
		return lastName;
	}

	public String getLogin() {
		return getMail();
	}

	public void setNotAfter(Date value) {
		this.notAfter = value;
	}

	public Date getNotAfter() {
		return notAfter;
	}

	public void setNotBefore(Date value) {
		this.notBefore = value;
	}

	public Date getNotBefore() {
		return notBefore;
	}

	public String getLdapUid() {
		return ldapUid;
	}

	public void setLdapUid(String ldapUid) {
		this.ldapUid = ldapUid;
	}

	public void setCanUpload(Boolean value) {
		this.canUpload = value;
	}

	public Boolean isCanUpload() {
		return canUpload;
	}

	public boolean isRestricted() {
		return false;
	}

	public Set<WorkgroupMember> getMyWorkgroupMembers() {
		return myWorkgroupMembers;
	}

	public void setMyWorkgroupMembers(Set<WorkgroupMember> myWorkgroupMembers) {
		this.myWorkgroupMembers = myWorkgroupMembers;
	}

	public Boolean isCanCreateGuest() {
		return canCreateGuest;
	}

	public void setCanCreateGuest(Boolean canCreateGuest) {
		this.canCreateGuest = canCreateGuest;
	}

	public Boolean isInconsistent() {
		return inconsistent;
	}

	public void setInconsistent(Boolean inconsistent) {
		this.inconsistent = inconsistent;
	}

	/*
	 * Helpers
	 */

	public boolean isAdmin() {
		return this.role.equals(Role.ADMIN) || this.role.equals(Role.SUPERADMIN);
	}

	@Override
	public boolean isInternal() {
		return this.getAccountType().equals(AccountType.INTERNAL);
	}

	@Override
	public boolean isGuest() {
		return this.getAccountType().equals(AccountType.GUEST);
	}

	public boolean isTechnicalAccount() {
		return this.getAccountType().equals(AccountType.TECHNICAL_ACCOUNT);
	}

	@Override
	public String toString() {
		return "User [firstName=" + firstName + ", lastName=" + lastName
				+ ", mail=" + getMail()
				+ ", role=" + getRole()
				+ ", type=" + getAccountType()
				+ ", lsUuid=" + lsUuid + "]";
	}

	public boolean isLocked() {
		int modulo = authenticationFailureCount % 3;
		Instant instantNow = new Date().toInstant();
		if (authenticationFailureCount < 3) {
			return false;
		}
		if (modulo == 0) {
			Instant endLockout = authenticationFailureLastDate.toInstant();
			if (authenticationFailureCount < 6) {
				endLockout = endLockout.plus(10, ChronoUnit.MINUTES);
			} else if (authenticationFailureCount < 9) {
				endLockout = endLockout.plus(20, ChronoUnit.MINUTES);
			} else if (authenticationFailureCount < 12) {
				endLockout = endLockout.plus(60, ChronoUnit.MINUTES);
			} else if (authenticationFailureCount < 15) {
				endLockout = endLockout.plus(1440, ChronoUnit.MINUTES);
			} else {
				return true;
			}
			if (instantNow.isBefore(endLockout)) {
				return true;
			}
		}
		return false;
	}

}
