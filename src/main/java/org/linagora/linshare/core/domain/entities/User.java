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
package org.linagora.linshare.core.domain.entities;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDto;

public abstract class User extends Account {

	protected Set<ThreadMember> myThreads = new java.util.HashSet<ThreadMember>();

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
		this.externalMailLocale = userDto.getExternalMailLocale();
		this.locale = userDto.getLocale();
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

	public Boolean getCanUpload() {
		return canUpload;
	}

	public boolean isRestricted() {
		return false;
	}

	public Set<ThreadMember> getMyThreads() {
		return myThreads;
	}

	public void setMyThreads(Set<ThreadMember> myThreads) {
		this.myThreads = myThreads;
	}

	public Boolean getCanCreateGuest() {
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

	public boolean isInternal() {
		return this.getAccountType().equals(AccountType.INTERNAL);
	}

	public boolean isGuest() {
		return this.getAccountType().equals(AccountType.GUEST);
	}

	@Override
	public String toString() {
		return "User [firstName=" + firstName + ", lastName=" + lastName
				+ ", mail=" + getMail() + ", lsUuid=" + lsUuid + "]";
	}

}
